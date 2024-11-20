<script setup lang="ts">
definePageMeta({
  layout: 'blank',
})
</script>

<template>
  <LayoutAuth>
    <div class="grid mx-auto max-w-sm gap-6">
      <div class="flex flex-col text-center space-y-2">
        <h1 class="text-2xl font-semibold tracking-tight">
          创建账号
        </h1>
        <p class="text-sm text-muted-foreground">
          输入您的邮箱以创建您的账号
        </p>
      </div>
      <AuthSignUp />
      <p class="text-center text-sm text-muted-foreground">
        已有账号？
        <NuxtLink
          to="/login"
          class="underline underline-offset-4 hover:text-primary"
        >
          登录
        </NuxtLink>
      </p>
      <p class="px-8 text-center text-sm text-muted-foreground">
        点击继续，您同意我们的
        <a
          href="/terms"
          class="underline underline-offset-4 hover:text-primary"
        >
          服务条款
        </a>
        和
        <a
          href="/privacy"
          class="underline underline-offset-4 hover:text-primary"
        >
          隐私政策
        </a>
        .
      </p>
    </div>
  </LayoutAuth>
</template>

<style scoped>

</style>
