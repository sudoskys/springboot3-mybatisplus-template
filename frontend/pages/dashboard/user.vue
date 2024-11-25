<script setup lang="ts">
import {definePageMeta} from '#imports'
import {useUserStore} from '~/stores/user'
import {storeToRefs} from 'pinia'
import {ref, computed} from 'vue'
import {useToast} from '@/components/ui/toast'
import {createUser, updateUser, deleteUser, useUsers} from '~/lib/api'
import {Button} from '@/components/ui/button'
import UserCard from '@/components/UserCard.vue'
import UserForm from '@/components/UserForm.vue'

definePageMeta({
  layout: 'admin',
})

const userStore = useUserStore()
const {toast} = useToast()
const {user} = storeToRefs(userStore)

const isAdmin = computed(() => user.value?.role === 'ADMIN')

const {users: usersData, isLoading, mutate: mutateUsers} = useUsers()
const editingId = ref<number | null>(null)

interface EditForm {
  email: string;
  password: string;
  role: string;
}

const editForm = ref<EditForm>({
  email: '',
  password: '',
  role: '',
})

const startEdit = (user: { id: number, email: string, role: string }) => {
  editingId.value = user.id
  editForm.value = {
    email: user.email,
    password: '',
    role: user.role,
  }
}

const cancelEdit = () => {
  editingId.value = null
  editForm.value = {
    email: '',
    password: '',
    role: '',
  }
}

const saveEdit = async (id: number) => {
  try {
    const updateData: any = {
      email: editForm.value.email,
      role: editForm.value.role,
    }

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

const handleDelete = async (id: number) => {
  try {
    await deleteUser(id)
    await mutateUsers()
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

const showCreateForm = ref(false)

interface NewUserForm {
  email: string;
  password: string;
  role: string;
}

const newUserForm = ref<NewUserForm>({
  email: '',
  password: '',
  role: 'USER'
})

const handleCreate = async () => {
  try {
    await createUser(newUserForm.value.email, newUserForm.value.password, newUserForm.value.role)
    await mutateUsers()
    showCreateForm.value = false
    newUserForm.value = {email: '', password: '', role: 'USER'}
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
        <h1 class="text-3xl font-bold tracking-tight">用户管理</h1>
      </div>
    </header>
    <client-only>
      <div v-if="!usersData">
        <div class="flex justify-center py-4">
          <span class="text-muted-foreground">加载中...</span>
        </div>
      </div>
      <div v-else-if="isAdmin" class="mt-6">
        <Card>
          <CardHeader class="flex flex-row items-center justify-between">
            <div>
              <CardTitle>用户管理</CardTitle>
              <CardDescription>管理系统用户</CardDescription>
            </div>
            <Button @click="showCreateForm = true">新建用户</Button>
          </CardHeader>
          <CardContent>
            <div v-if="isLoading" class="flex justify-center py-4">
              <span class="text-muted-foreground">加载中...</span>
            </div>
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
                <TableRow v-for="user in usersData" :key="user.id">
                  <TableCell>{{ user.id }}</TableCell>
                  <TableCell>
                    <div v-if="editingId === user.id">
                      <UserForm :form="editForm"/>
                    </div>
                    <span v-else>{{ user.email }}</span>
                  </TableCell>
                  <TableCell>
                    <div v-if="editingId === user.id">
                      <Select v-model="editForm.role">
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

        <Dialog :open="showCreateForm" @update:open="showCreateForm = $event">
          <DialogContent>
            <DialogHeader>
              <DialogTitle>新建用户</DialogTitle>
              <DialogDescription>
                创建用户
              </DialogDescription>
            </DialogHeader>
            <UserForm :form="newUserForm"/>
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

      <div v-else class="mt-6">
        <UserCard v-if="user" :user="user"/>
      </div>
    </client-only>
  </div>
</template>