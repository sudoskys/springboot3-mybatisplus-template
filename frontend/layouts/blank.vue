<script setup lang="ts">
import { Toaster } from '@/components/ui/toast';
</script>

<template>
  <div>
    <slot />
    <Toaster />
  </div>
</template>

<style scoped>

</style>
