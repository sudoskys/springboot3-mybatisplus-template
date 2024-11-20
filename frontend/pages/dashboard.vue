<script setup lang="ts">
definePageMeta({
  layout: 'admin',
})
import { useUserStore } from '~/stores/user'
import { storeToRefs } from 'pinia'
import { useRouter } from 'vue-router'
import { LogOut } from 'lucide-vue-next'
import { ref, computed } from 'vue'
import { useToast } from '@/components/ui/toast'
import { 
  createUser, 
  updateUser, 
  deleteUser 
} from '~/lib/api'

import { useUsers } from '~/lib/api'
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
  SelectGroup,
  SelectLabel,
} from '@/components/ui/select'

const userStore = useUserStore()
const { toast } = useToast()
const router = useRouter()
const { user } = storeToRefs(userStore)

// 定义不同角色可以看到的统计数据
const stats = ref([
  { name: '总用户数', value: '2,345', change: '+12%', changeType: 'increase' },
  { name: '本月订单', value: '456', change: '+4.75%', changeType: 'increase' },
  { name: '总收入', value: '¥123,456', change: '+10.18%', changeType: 'increase' },
  { name: '平均订单金额', value: '¥270', change: '-3.38%', changeType: 'decrease' },
])

// 最近用户列表
const recentUsers = ref([
  { id: 1, email: 'user1@example.com', createdAt: '2024-03-20', role: 'USER' },
  { id: 2, email: 'user2@example.com', createdAt: '2024-03-19', role: 'USER' },
  // ... 更多用户数据
])

// 判断是否为管理员
const isAdmin = computed(() => user.value?.role === 'ADMIN')

// 添加登出方法
const handleLogout = async () => {
  try {
    await userStore.logout()
    await router.push('/login')
  } catch (error) {
    console.error('登出失败:', error)
  }
}

// 添加客户端专用的欢迎信息
const welcomeMessage = computed(() => {
  return `欢迎回来${user.value?.email ? ', ' + user.value.email : ''}`
})

// 用户管理相关
const { users: usersData, isLoading, mutate: mutateUsers } = useUsers()
const editingId = ref<number | null>(null)
const editForm = ref({
  email: '',
  password: '',
  role: '',
})
// 开始编辑
const startEdit = (user: { id: number, email: string, role: string }) => {
  editingId.value = user.id
  editForm.value = {
    email: user.email,
    password: '',
    role: user.role,
  }
}

// 取消编辑
const cancelEdit = () => {
  editingId.value = null
  editForm.value = {
    email: '',
    password: '',
    role: '',
  }
}

// 保存编辑
const saveEdit = async (id: number) => {
  try {
    const updateData: any = {
      email: editForm.value.email,
      role: editForm.value.role,
    }
    
    // 只有当用户输入了新密码时，才包含密码字段
    if (editForm.value.password && editForm.value.password.trim() !== '') {
      updateData.password = editForm.value.password
    }
    await updateUser(id, updateData)
    await mutateUsers()
    editingId.value = null
    toast({
      title: '更新成功',
      description: '用户信息已成功更新'
    })
  } catch (error) {
    toast({
      title: '更新失败',
      description: '用户信息更新失败'
    })
    console.error(error)
  }
}

// 删除用户
const handleDelete = async (id: number) => {
  try {
    await deleteUser(id)
    await mutateUsers() // 刷新数据
    toast({
      title: '删除成功',
      description: '用户已成功删除'
    })
  } catch (error) {
    toast({
      title: '删除失败',
      description: '用户删除失败'
    })
    console.error(error)
  }
}

// 新建用户相关
const showCreateForm = ref(false)
const newUserForm = ref({
  email: '',
  password: '',
  role: 'USER'
})

const handleCreate = async () => {
  try {
    await createUser(newUserForm.value.email, newUserForm.value.password)
    await mutateUsers() // 刷新数据
    showCreateForm.value = false
    newUserForm.value = { email: '', password: '', role: 'USER' }
    toast({
      title: '创建成功',
      description: '用户已成功创建'
    })
  } catch (error) {
    toast({
      title: '创建失败',
      description: '用户创建失败'
    })
    console.error(error)
  }
}
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
        <LogOut class="h-4 w-4" />
        登出
      </Button>
    </header>

    <!-- 使用 client-only 包装所有依赖用户数据的部分 -->
    <client-only>
      <!-- 管理员统计卡片 -->
      <div v-if="isAdmin" class="grid gap-4 md:grid-cols-2 lg:grid-cols-4 mt-6">
        <Card v-for="stat in stats" :key="stat.name">
          <CardHeader class="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle class="text-sm font-medium">
              {{ stat.name }}
            </CardTitle>
          </CardHeader>
          <CardContent>
            <div class="text-2xl font-bold">{{ stat.value }}</div>
            <p class="text-xs text-muted-foreground">
              <span :class="stat.changeType === 'increase' ? 'text-green-600' : 'text-red-600'">
                {{ stat.change }}
              </span>
              相比上月
            </p>
          </CardContent>
        </Card>
      </div>

      <div v-if="!usersData">
        <div class="flex justify-center py-4">
          <Spinner class="h-6 w-6" />
        </div>
      </div>

      <!-- 管理员用户列表 -->
      <div v-if="isAdmin" class="mt-6">
        <Card>
          <CardHeader class="flex flex-row items-center justify-between">
            <div>
              <CardTitle>用户管理</CardTitle>
              <CardDescription>管理系统用户</CardDescription>
            </div>
            <Button @click="showCreateForm = true">新建用户</Button>
          </CardHeader>
          <CardContent>
            <!-- 加载状态 -->
            <div v-if="isLoading" class="flex justify-center py-4">
              <Spinner class="h-6 w-6" />
            </div>
            <!-- 用户列表 -->
            <Table v-else>
              <TableHeader>
                <TableRow>
                  <TableHead>ID</TableHead>
                  <TableHead>邮箱</TableHead>
                  <TableHead>角色</TableHead>
                  <TableHead>操作</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                <!-- 添加调试信息 -->
                <template v-if="usersData">
                  <pre>{{ JSON.stringify(usersData, null, 2) }}</pre>
                </template>
                
                <TableRow v-for="user in usersData" :key="user.id">
                  <TableCell>{{ user.id }}</TableCell>
                  <TableCell>
                    <div v-if="editingId === user.id">
                      <Input 
                        v-model="editForm.email" 
                        placeholder="邮箱" 
                        class="mb-2"
                      />
                      <Input 
                        v-model="editForm.password" 
                        type="password" 
                        placeholder="新密码（可选）" 
                        class="mb-2"
                      />
                    </div>
                    <span v-else>{{ user.email }}</span>
                  </TableCell>
                  <TableCell>
                    <div v-if="editingId === user.id">
                      <Select v-model="editForm.role">
                        <SelectTrigger class="w-[180px]">
                          <SelectValue placeholder="选择角色" />
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
                    <span v-else>{{ user.role }}</span>
                  </TableCell>
                  <TableCell>
                    <div class="flex space-x-2">
                      <template v-if="editingId === user.id">
                        <Button 
                          variant="default" 
                          size="sm"
                          @click="saveEdit(user.id)"
                        >
                          保存
                        </Button>
                        <Button 
                          variant="outline" 
                          size="sm"
                          @click="cancelEdit"
                        >
                          取消
                        </Button>
                      </template>
                      <template v-else>
                        <Button 
                          variant="outline" 
                          size="sm"
                          @click="startEdit(user)"
                        >
                          编辑
                        </Button>
                        <Button 
                          variant="destructive" 
                          size="sm"
                          @click="handleDelete(user.id)"
                        >
                          删除
                        </Button>
                      </template>
                    </div>
                  </TableCell>
                </TableRow>
              </TableBody>
            </Table>
          </CardContent>
        </Card>

        <!-- 新建用户对话框 -->
        <Dialog :open="showCreateForm" @update:open="showCreateForm = $event">
          <DialogContent>
            <DialogHeader>
              <DialogTitle>新建用户</DialogTitle>
              <DialogDescription>
                创建新的系统用户
              </DialogDescription>
            </DialogHeader>
            
            <div class="space-y-4">
              <div class="space-y-2">
                <Label>邮箱</Label>
                <Input v-model="newUserForm.email" placeholder="请输入邮箱" />
              </div>
              
              <div class="space-y-2">
                <Label>密码</Label>
                <Input 
                  v-model="newUserForm.password" 
                  type="password" 
                  placeholder="请输入密码" 
                />
              </div>
              
              <div class="space-y-2">
                <Label>角色</Label>
                <Select v-model="newUserForm.role">
                  <SelectTrigger class="w-[180px]">
                    <SelectValue placeholder="选择角色" />
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

            <DialogFooter>
              <Button variant="outline" @click="showCreateForm = false">
                取消
              </Button>
              <Button @click="handleCreate">
                创建
              </Button>
            </DialogFooter>
          </DialogContent>
        </Dialog>
      </div>

      <!-- 普通用户界面 -->
      <div v-else class="mt-6">
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
      </div>
    </client-only>
  </div>
</template>
