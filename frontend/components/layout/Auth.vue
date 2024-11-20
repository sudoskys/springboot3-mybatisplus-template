<script setup lang="ts">
defineProps<{
  reverse?: boolean
}>()

import { ShoppingCart } from "lucide-vue-next";
</script>

<template>
  <div
    class="relative flex items-center justify-center px-4 h-dvh lg:max-w-none lg:px-0"
    :class="{ 'flex-row-reverse': reverse }"
  >
    <div class="relative hidden h-full flex-1 flex-col bg-muted p-10 text-gray-500 lg:flex dark:border-r">
      <div class="absolute inset-0 bg-zinc-900 bg-cover bg-center" 
           style="background-image: url('/background.jpg')" />
      <div class="relative z-20 flex items-center text-lg font-medium">
        <ShoppingCart class="mr-2 w-6 h-6"/>
        购物商城系统展示用例
      </div>
      <div class="relative z-20 mt-auto">
        <blockquote class="space-y-2">
          <p class="text-lg text-white">
            &ldquo;我们为全世界的客户提供最优质的商品和服务&rdquo;
          </p>
          <footer class="text-sm text-white">
            购物商城系统
          </footer>
        </blockquote>
      </div>
    </div>
    <div class="mx-auto flex-1 lg:p-8">
      <slot />
    </div>
  </div>
</template>

<style scoped>

</style>
