<template>
  <Card>
    <CardHeader>
      <CardTitle>您是普通用户</CardTitle>
    </CardHeader>
    <CardContent>
      <div class="space-y-4">
        <div class="grid grid-cols-2 gap-4">
          <div>
            <label class="text-sm font-medium">邮箱</label>
            <p class="text-muted-foreground">{{ user?.email }}</p>
          </div>
          <div>
            <label class="text-sm font-medium">角色</label>
            <p class="text-muted-foreground">{{ user?.role }}</p>
          </div>
        </div>
        <div class="pt-4">
          <Button>修改密码</Button>
        </div>
      </div>
    </CardContent>
  </Card>
</template>

<script setup lang="ts">
import {ref} from 'vue'
import {Card, CardContent, CardHeader, CardTitle} from '@/components/ui/card'
import {Button} from '@/components/ui/button'

const props = defineProps({
  user: {
    type: Object,
    required: true
  }
})
</script>