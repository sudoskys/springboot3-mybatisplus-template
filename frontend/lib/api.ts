import type {AxiosError, AxiosRequestConfig, InternalAxiosRequestConfig} from 'axios'
import {useRuntimeConfig} from '#app'

import axios from 'axios'
import {z} from 'zod'
import useSWRV from 'swrv'

// Error handling
export class ApiError extends Error {
    constructor(
        public status: number,
        message: string,
        public errorType?: string | undefined,
        public caller?: string,
    ) {
        super(`${caller ? `[${caller}] ` : ''}${message || status}`)
        this.name = 'ApiError'
    }
}

// API instance
const api = axios.create({
    baseURL: 'http://127.0.0.1:8080/api',
    headers: {
        'Content-Type': 'application/json',
    },
})

const authResponseSchema = z.object({
    token: z.string(),
    user: z.object({
        id: z.string(),
        email: z.string(),
        role: z.string(),
    }),
})

export type AuthResponse = z.infer<typeof authResponseSchema>

// 创建一个函数来更新 baseURL
export function updateApiBaseUrl() {
    const config = useRuntimeConfig()
    if (config.public.apiBase) {
        api.defaults.baseURL = config.public.apiBase as string
    }
}

// Generic API call function
async function apiCall<T>(
    method: 'get' | 'post' | 'put' | 'delete',
    url: string,
    data?: any,
    schema?: z.ZodSchema<T>,
    config?: AxiosRequestConfig,
    caller?: string,
): Promise<T> {
    try {
        const response = await api[method](url, method === 'get' ? {params: data} : data, config)
        return schema ? schema.parse(response.data?.data) : response.data?.data
    } catch (error) {
        if (error instanceof z.ZodError) {
            throw new ApiError(400, error.errors.map(err => err.message).join(', '), 'validation_error', caller)
        }
        if (axios.isAxiosError(error)) {
            const axiosError = error as AxiosError<{ message: string, errorType: string }>
            throw new ApiError(
                axiosError.response?.status || 500,
                axiosError.response?.data?.message || '',
                axiosError.response?.data?.errorType,
                caller,
            )
        }
        throw new ApiError(500, '', undefined, caller)
    }
}

// 自动添加 token 到请求头
api.interceptors.request.use((config: InternalAxiosRequestConfig) => {
    if (import.meta.client) {
        const token = sessionStorage.getItem('access_token')
        if (token) {
            config.headers = config.headers ?? {}
            config.headers.Authorization = `Bearer ${token}`
        }
    }
    return config
})

// 添加响应拦截器处理 token 过期
api.interceptors.response.use(
    response => response,
    async error => {
        const originalRequest = error.config

        // 如果是 401 错误且不是刷新 token 的请求
        if (error.response?.status === 401 &&
            !originalRequest._retry &&
            originalRequest.url !== '/auth/ping') {
            originalRequest._retry = true

            const userStore = useUserStore()
            const refreshed = await userStore.refreshToken()

            if (refreshed) {
                originalRequest.headers.Authorization = `Bearer ${userStore.token}`
                return api(originalRequest)
            }
        }

        return Promise.reject(error)
    }
)

export function login(email: string, password: string) {
    return apiCall<AuthResponse>(
        'post',
        '/auth/login',
        {email, password},
        authResponseSchema,
        undefined,
        'login'
    )
}

export function register(email: string, password: string) {
    return apiCall<AuthResponse>(
        'post',
        '/auth/register',
        {email, password},
        authResponseSchema,
        undefined,
        'register'
    )
}

export function logout() {
    return apiCall('post', '/auth/logout', undefined, z.object({
        message: z.string(),
    }), undefined, 'logout')
}

export function getCurrentUser() {
    return apiCall('get', '/auth/user', undefined, z.object({
        id: z.string(),
        email: z.string(),
        role: z.string(),
    }), undefined, 'getCurrentUser')
}

/**
 * 刷新 token
 * @returns
 */
export function ping() {
    return apiCall<AuthResponse>(
        'post',
        '/auth/ping',
        undefined,
        authResponseSchema,
        undefined,
        'ping'
    )
}

type MessageResponse = {
    message: string
}

/**
 * 用户 Schema
 * 示例数据：
 * ```json
 * {
 *    "id": "1",
 *    "email": "e@exp.com",
 *    "role": "ADMIN"
 *}
 */
const userSchema = z.object({
    id: z.string(),
    email: z.string(),
    role: z.string(),
})


/*
* 产品 Schema
* 示例数据：
```json
{
        "id": 2,
        "imageUrl": "https://via.placeholder.com/500x300?text=Cake+Item",
        "name": "草莓水果蛋糕",
        "description": "草莓风味的水果蛋糕，好吃带回家～",
        "uploaderId": 1,
        "uploadTime": "2024-11-20T10:23:15.892909Z",
        "price": 12.0,
        "eventId": null,
        "tags": "tag1,tag2"
}
```
* */
const productSchema = z.object({
    id: z.union([z.string(), z.number()]),
    imageUrl: z
        .string()
        .nullable(),
    name: z.string(),
    description: z.string().nullable(),
    uploaderId: z.union([z.string(), z.number()]).nullable(),
    uploadTime: z.string().nullable(),
    price: z.number(),
    eventId: z.any(),
    tags: z.string().nullable(),
});

// 映射 null -> undefined
export const UIProductSchema = productSchema.transform((product) => ({
    ...product,
    imageUrl: product.imageUrl ?? undefined,
    description: product.description ?? undefined,
    uploaderId: product.uploaderId ?? undefined,
    uploadTime: product.uploadTime ?? undefined,
    eventId: product.eventId ?? undefined,
    tags: product.tags ?? undefined,
}));

export type User = z.infer<typeof userSchema>
export type Product = z.infer<typeof productSchema>
export type UIProduct = z.infer<typeof UIProductSchema>;

const userListSchema = z.array(userSchema)
const productListSchema = z.array(productSchema)
const UIProductListSchema = z.array(UIProductSchema)


// 获取所有用户
export function getAllUsers() {
    return apiCall<User[]>(
        'get',
        '/users',
        undefined,
        userListSchema,
        undefined,
        'getAllUsers'
    )
}

// 使用 SWRV 的用户列表 hook
export function useUsers() {
    const {data, error, isValidating, mutate} = useSWRV<User[]>(
        '/users',
        () => api.get('/users').then(res => res.data),
        {
            // SWRV 配置选项
            refreshInterval: 0, // 轮询间隔，0 表示禁用
            dedupingInterval: 2000, // 相同请求的去重时间间隔
            ttl: 0, // 缓存生存时间，0 表示永久
            shouldRetryOnError: true, // 错误时是否重试
            errorRetryInterval: 5000, // 错误重试间隔
            errorRetryCount: 2, // 最大错误重试次数
            revalidateOnFocus: false, // 窗口获得焦点时重新验证
            revalidateDebounce: 0, // 重新验证的防抖时间
        }
    )

    return {
        users: data,
        isLoading: isValidating,
        isError: error,
        mutate,
    }
}

// 获取单个用户
export function getUserById(id: number) {
    return apiCall<User>(
        'get',
        `/users/${id}`,
        undefined,
        userSchema,
        undefined,
        'getUserById'
    )
}

// 创建用户
export function createUser(email: string, password: string, role: string) {
    if (!email || !password || !role) {
        throw new ApiError(400, '须为非空', 'validation_error', 'createUser')
    }
    return apiCall<MessageResponse>(
        'post',
        '/users',
        {email, password, role},
        undefined,
        undefined,
        'createUser'
    )
}

// 更新用户
export function updateUser(id: number, userData: Partial<User>) {
    return apiCall<MessageResponse>(
        'put',
        `/users/${id}`,
        userData,
        undefined,
        undefined,
        'updateUser'
    )
}

// 删除用户
export function deleteUser(id: number) {
    return apiCall<MessageResponse>(
        'delete',
        `/users/${id}`,
        undefined,
        undefined,
        undefined,
        'deleteUser'
    )
}


// 获取所有产品
const productResponseSchema = z.object({
    total: z.number(),
    size: z.number(),
    current: z.number(),
    pages: z.number(),
    records: productListSchema,
})

const UIProductResponseSchema = productResponseSchema.transform((data) => ({
    ...data,
    records: data.records.map((product) => (
        UIProductSchema.parse(product)
    )),
}))

export async function getAllProducts() {
    return apiCall<z.infer<typeof productResponseSchema>>(
        'get',
        '/products',
        undefined,
        productResponseSchema,
        undefined,
        'getAllProducts'
    ).then((data) => {
        return UIProductResponseSchema.parse(data)
    })
}


export function useProducts(page: number = 1, size: number = 25) {
    const {data, error, isValidating, mutate} = useSWRV(
        ['/products', page, size],
        async () => {
            const response = await api.get('/products', {
                params: {page, size}
            })
            return response.data?.data
        },
        {
            refreshInterval: 0,
            dedupingInterval: 2000,
            ttl: 0,
            shouldRetryOnError: true,
            errorRetryInterval: 5000,
            errorRetryCount: 2,
            revalidateOnFocus: false,
            revalidateDebounce: 0,
        }
    )
    return {
        products: data,
        isLoading: isValidating,
        isError: error,
        mutate,
    }
}

export async function getProductById(id: number) {
    return apiCall<Product>(
        'get',
        `/products/${id}`,
        undefined,
        productSchema,
        undefined,
        'getProductById'
    ).then(
        (data) => UIProductSchema.parse(data)
    )
}

export function createProduct(productData: Partial<UIProduct>) {
    return apiCall<MessageResponse>(
        'post',
        '/products',
        productSchema.parse(productData),
        undefined,
        undefined,
        'createProduct'
    )
}

export function updateProduct(id: string | number, productData: Partial<UIProduct>) {
    return apiCall<MessageResponse>(
        'put',
        `/products/${id}`,
        productSchema.parse(productData),
        undefined,
        undefined,
        'updateProduct'
    )
}

export function deleteProduct(id: string | number) {
    return apiCall<MessageResponse>(
        'delete',
        `/products/${id}`,
        undefined,
        undefined,
        undefined,
        'deleteProduct'
    )
}

// 在已有的schema定义后添加订单相关的schema
const orderSchema = z.object({
    id: z.union([z.string(), z.number()]),
    userId: z.union([z.string(), z.number()]),
    items: z.array(z.object({
        id: z.union([z.string(), z.number()]).nullable(),
        orderId: z.union([z.string(), z.number()]).nullable(),
        productId: z.union([z.string(), z.number()]),
        quantity: z.number(),
        price: z.number()
    })).nullable(),
    totalPrice: z.number(),
    status: z.string(),
    createdAt: z.string().nullable(),
    updatedAt: z.string().nullable(),
    address: z.string().nullable(),
    phone: z.string().nullable()
});

export type Order = z.infer<typeof orderSchema>

// 创建订单请求
export interface CreateOrderRequest {
    products: Array<{
        productId: string | number;
        quantity: number;
    }>;
    address: string;
    phone: string;
}

// 添加订单相关的API函数
export function createOrder(orderData: CreateOrderRequest) {
    return apiCall<Order>(
        'post',
        '/orders',
        orderData,
        orderSchema,
        undefined,
        'createOrder'
    )
}

export function getOrder(id: number) {
    return apiCall<Order>(
        'get',
        `/orders/${id}`,
        undefined,
        orderSchema,
        undefined,
        'getOrder'
    )
}

export function getCurrentUserOrders() {
    return apiCall<Order[]>(
        'get',
        '/orders/user',
        undefined,
        z.array(orderSchema),
        undefined,
        'getCurrentUserOrders'
    )
}

export function updateOrder(id: string, data: Partial<Order>) {
    return apiCall<Order>(
        'put',
        `/orders/${id}`,
        data,
        undefined,
        undefined,
        'updateOrder'
    )
}

export function deleteOrder(id: string) {
    return apiCall<void>(
        'delete',
        `/orders/${id}`,
        undefined,
        undefined,
        undefined,
        'deleteOrder'
    )
}

export function getAllOrders(page: number = 1, size: number = 10) {
    return apiCall<Order[]>(
        'get',
        '/orders',
        { page, size },
        z.array(orderSchema),
        undefined,
        'getAllOrders'
    )
}

export default defineNuxtPlugin(() => {
    updateApiBaseUrl()
})

