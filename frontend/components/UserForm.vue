<template>
  <div class="space-y-4">
    <div class="space-y-2">
      <Label>邮箱</Label>
      <Input v-model="form.email" placeholder="请输入邮箱"/>
    </div>

    <div class="space-y-2">
      <Label>密码</Label>
      <Input v-model="form.password" type="password" placeholder="请输入密码"/>
    </div>

    <div class="space-y-2">
      <Label>角色</Label>
      <Select v-model="form.role">
        <SelectTrigger class="w-[180px]">
          <SelectValue placeholder="选择角色"/>
        </SelectTrigger>
        <SelectContent>
          <SelectGroup>
            <SelectLabel>角色</SelectLabel>
            <SelectItem value="USER">用户</SelectItem>
            <SelectItem value="ADMIN">管理员</SelectItem>
          </SelectGroup>
        </SelectContent>
      </Select>
    </div>
  </div>
</template>

<script setup lang="ts">
import {ref} from 'vue'
import {Input} from '@/components/ui/input'
import {Label} from '@/components/ui/label'
import {Select, SelectContent, SelectGroup, SelectItem, SelectLabel, SelectTrigger, SelectValue} from '@/components/ui/select'

const props = defineProps({
  form: {
    type: Object,
    required: true
  }
})
</script>