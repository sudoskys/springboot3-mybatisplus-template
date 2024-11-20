<script setup lang="ts">
import { Loader2 } from 'lucide-vue-next'
import { cn } from '@/lib/utils'
import { register } from '@/lib/api'
import { useToast } from '@/components/ui/toast'
import { GitHubIcon } from 'vue3-simple-icons'
const { toast } = useToast()
const userStore = useUserStore()
const isLoading = ref(false)
const form = ref({
  email: '',
  password: '',
})

async function onSubmit(event: Event) {
  event.preventDefault()
  isLoading.value = true
  
  try {
    const response = await register(form.value.email, form.value.password)
    toast({
      title: '注册成功',
      variant: 'default',
    })
    userStore.setAuthResponse(response)
    // 触发注册成功事件
    emit('register-success')
  }
  catch (error: any) {
    console.error(error)
    toast({
      title: '注册失败',
      description: error.message,
      variant: 'destructive',
    })
  }
  finally {
    isLoading.value = false
  }
}

const emit = defineEmits(['register-success'])
</script>

<template>
  <div :class="cn('grid gap-6', $attrs.class ?? '')">
    <form @submit="onSubmit">
      <div class="grid gap-4">
        <div class="grid gap-2">
          <Label for="email">Email</Label>
          <Input
            id="email"
            v-model="form.email"
            placeholder="name@example.com"
            type="email"
            auto-capitalize="none"
            auto-complete="email"
            auto-correct="off"
            :disabled="isLoading"
          />
        </div>
        <div class="grid gap-2">
          <Label for="password">密码</Label>
          <Input
            id="password"
            v-model="form.password"
            type="password"
            auto-complete="current-password"
            :disabled="isLoading"
          />
        </div>
        <Button :disabled="isLoading">
          <Loader2 v-if="isLoading" class="mr-2 h-4 w-4 animate-spin" />
          注册
        </Button>
      </div>
    </form>
    <div class="relative">
      <div class="absolute inset-0 flex items-center">
        <span class="w-full border-t" />
      </div>
      <div class="relative flex justify-center text-xs uppercase">
        <span class="bg-background px-2 text-muted-foreground">
          或者使用
        </span>
      </div>
    </div>
    <Button variant="outline" type="button" :disabled="isLoading">
      <Loader2 v-if="isLoading" class="mr-2 h-4 w-4 animate-spin" />
      <GitHubIcon v-else class="mr-2 w-4 h-4"/>
      使用GitHub账号登录
    </Button>
  </div>
</template>
