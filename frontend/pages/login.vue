<script setup lang="ts">
definePageMeta({
  layout: 'blank',
})

const router = useRouter()

const handleLoginSuccess = () => {
  router.push('/')
}
</script>

<template>
  <LayoutAuth reverse>
    <div class="grid mx-auto max-w-sm gap-6">
      <div class="grid gap-2 text-center">
        <h1 class="text-2xl font-semibold tracking-tight">
          登录
        </h1>
        <p class="text-balance text-sm text-muted-foreground">
          输入您的邮箱以登录您的账户
        </p>
      </div>
      <AuthSignIn @login-success="handleLoginSuccess" />
    </div>
  </LayoutAuth>
</template>

