interface AuthGuardOptions {
  routes?: string[] | boolean  // 支持字符串数组或布尔值
  redirectTo?: string
  publicOnly?: boolean  // 仅允许未认证用户访问
  adminOnly?: boolean   // 仅允许管理员用户访问
}

export function useAuthGuard(options: AuthGuardOptions = {}) {
  const userStore = useUserStore()
  const { isLoggedIn, isAuthInitialized,isAdmin } = storeToRefs(userStore)
  const route = useRoute()
  const router = useRouter()

  // 如果不指定重定向路径，则默认重定向到登录页面
  const redirectPath = options.redirectTo ?? '/login'

  watchEffect(async () => {
    // 等待认证状态初始化
    if (!isAuthInitialized.value) {
      await userStore.initializeAuth()
    }

    // 根据 routes 配置判断是否需要认证
    let requiresAuth = false
    
    if (typeof options.routes === 'boolean') {
      // 如果是布尔值，直接使用该值
      requiresAuth = options.routes
    } else if (Array.isArray(options.routes)) {
      // 如果是路由数组，检查当前路径是否匹配
      requiresAuth = options.routes.some(path => route.path.startsWith(path))
    }

    // 处理需要认证的情况
    if (requiresAuth && !isLoggedIn.value) {
      console.log('requiresAuth')
      router.push(redirectPath)
      return
    }

    // 处理仅允许管理员用户访问的情况
    if (options.adminOnly && !isAdmin.value) {
      console.log('adminOnly')
      router.push('/')
      return
    }

    // 处理仅允许未认证用户访问的情况
    if (options.publicOnly && isLoggedIn.value) {
      console.log('publicOnly')
      router.push('/')
      return
    }
  })

  // 监听状态进行登出操作
  watch(() => isLoggedIn.value, (newValue) => {
    if (!newValue && isAuthInitialized.value) {
      const requiresAuth = typeof options.routes === 'boolean' 
        ? options.routes 
        : Array.isArray(options.routes) && options.routes.some(path => route.path.startsWith(path))
      
      if (requiresAuth) {
        router.push(redirectPath)
      }
    }
  })

  return {
    isLoggedIn,
    isAuthInitialized
  }
}