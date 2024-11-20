<script setup lang="ts">
import { useAuthGuard } from '@/composables/useAuthGuard';
import { Toaster } from '@/components/ui/toast';
// 使用带有自定义配置的认证守卫
useAuthGuard({
  routes: [],
  redirectTo: '/login'
})
</script>

<template>
  <div>
    <slot />
    <Toaster />
  </div>
</template>

<style scoped>

</style>
