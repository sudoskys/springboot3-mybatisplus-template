import type { AxiosError, AxiosRequestConfig, InternalAxiosRequestConfig } from 'axios'
import { useRuntimeConfig } from '#app'

import axios from 'axios'
import { z } from 'zod'
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
    id: z.number(),
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
    const response = await api[method](url, method === 'get' ? { params: data } : data, config)
    return schema ? schema.parse(response.data?.data) : response.data?.data
  }
  catch (error) {
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
    { email, password },
    authResponseSchema,
    undefined,
    'login'
  )
}

export function register(email: string, password: string) {
  return apiCall<AuthResponse>(
    'post',
    '/auth/register',
    { email, password },
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

// 定义用户相关的 Schema
const userSchema = z.object({
  id: z.number(),
  email: z.string(),
  role: z.string(),
})

type MessageResponse = {
  message: string
}

const userListSchema = z.array(userSchema)

type User = z.infer<typeof userSchema>
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
  const { data, error, isValidating, mutate } = useSWRV<User[]>(
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
export function createUser(email: string, password: string) {
  if (!email || !password) {
    throw new ApiError(400, '邮箱和密码不能为空', 'validation_error', 'createUser')
  }
  return apiCall<MessageResponse>(
    'post',
    '/users',
    { email, password },
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

export default defineNuxtPlugin(() => {
  updateApiBaseUrl()
})
