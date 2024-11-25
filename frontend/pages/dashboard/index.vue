<script setup lang="ts">
import {definePageMeta} from '#imports'
import {useUserStore} from '~/stores/user'
import {storeToRefs} from 'pinia'
import {useRouter} from 'vue-router'
import {LogOut} from 'lucide-vue-next'
import {computed} from 'vue'
import {Button} from '@/components/ui/button'

definePageMeta({
  layout: 'admin',
})

const userStore = useUserStore()
const router = useRouter()
const {user} = storeToRefs(userStore)

const isAdmin = computed(() => user.value?.role === 'ADMIN')

const handleLogout = async () => {
  try {
    await userStore.logout()
    await router.push('/login')
  } catch (error) {
    console.error('登出失败:', error)
  }
}

const welcomeMessage = computed(() => {
  return `欢迎回来${user.value?.email ? ', ' + user.value.email : ''}`
})
</script>

<template>
  <div class="p-6">
    <header class="flex justify-between items-center mb-6">
      <div class="space-y-4">
        <h1 class="text-3xl font-bold tracking-tight">仪表盘</h1>
        <client-only>
          <p class="text-muted-foreground">
            {{ welcomeMessage }}
          </p>
        </client-only>
      </div>

      <Button
          variant="outline"
          @click="handleLogout"
          class="flex items-center gap-2"
      >
        <LogOut class="h-4 w-4"/>
        登出
      </Button>
    </header>

    <client-only>
      <div v-if="isAdmin" class="mt-6">
        <Button @click="$router.push('/dashboard/user')">用户管理</Button>
      </div>
      <div v-else class="mt-6">
        <UserCard v-if="user" :user="user"/>
      </div>
    </client-only>
  </div>
</template>