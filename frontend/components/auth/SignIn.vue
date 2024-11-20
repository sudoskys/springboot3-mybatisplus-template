<script setup lang="ts">
import {Loader2} from 'lucide-vue-next'
import {Label} from '~/components/ui/label/'
import { login } from '@/lib/api'
import { useUserStore } from '@/stores/user'
import { useToast } from '@/components/ui/toast'

const email = ref('')
const password = ref('')
const isLoading = ref(false)
const { toast } = useToast()
const userStore = useUserStore()

const emit = defineEmits(['login-success'])

async function onSubmit(event: Event) {
  event.preventDefault()
  if (!email.value || !password.value)
    return

  try {
    isLoading.value = true
    const response = await login(email.value, password.value)
    userStore.setAuthResponse(response)
    emit('login-success')
  }
  catch (error: any) {
    console.error(error)
    toast({
      title: '登录失败',
      description: error.message,
      variant: 'destructive',
    })
  }
  finally {
    isLoading.value = false
  }
}
</script>

<template>
  <form class="grid gap-4" @submit="onSubmit">
    <div class="grid gap-2">
      <Label for="email">
        邮箱
      </Label>
      <Input
          id="email"
          v-model="email"
          type="email"
          placeholder="name@example.com"
          :disabled="isLoading"
          auto-capitalize="none"
          auto-complete="email"
          auto-correct="off"
      />
    </div>
    <div class="grid gap-2">
      <div class="flex items-center">
        <Label for="password">
          密码
        </Label>
        <NuxtLink
            to="/forgot-password"
            class="ml-auto inline-block text-sm underline"
        >
          忘记密码？
        </NuxtLink>
      </div>
      <Input id="password" v-model="password" type="password"/>
    </div>
    <Button type="submit" class="w-full" :disabled="isLoading">
      <Loader2 v-if="isLoading" class="mr-2 h-4 w-4 animate-spin"/>
      登录
    </Button>
  </form>
  <div class="mt-4 text-center text-sm text-muted-foreground">
    没有账号？
    <NuxtLink to="/register" class="underline">
      注册
    </NuxtLink>
  </div>
</template>

<style scoped>

</style>
