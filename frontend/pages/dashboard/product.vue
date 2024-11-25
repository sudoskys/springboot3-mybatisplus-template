<script setup lang="ts">
import {definePageMeta} from '#imports'
import {useUserStore} from '~/stores/user'
import {storeToRefs} from 'pinia'
import {ref, computed} from 'vue'
import {useToast} from '@/components/ui/toast'
import {type Product, productUISchema, createProduct, updateProduct, deleteProduct, useProducts} from '~/lib/api'
import {Button} from '@/components/ui/button'
import UserCard from '@/components/UserCard.vue'
import ProductForm from '@/components/ProductForm.vue'

definePageMeta({
  layout: 'admin',
})

const userStore = useUserStore()
const {toast} = useToast()
const {user} = storeToRefs(userStore)

const isAdmin = computed(() => user.value?.role === 'ADMIN')

const {products: productsData, isLoading, mutate: mutateProducts} = useProducts()
const editingId = ref<string | number | null>(null)

const editForm = ref<UIProduct>({
  id: 0,
  imageUrl: '',
  name: '',
  description: '',
  uploaderId: user.value?.id || undefined,
  uploadTime: new Date().toISOString(),
  price: 0,
  eventId: undefined,
  tags: undefined,
})

const startEdit = (product: Product) => {
  editingId.value = product.id
  editForm.value = {...productUISchema.parse(product)}
}

const cancelEdit = () => {
  editingId.value = null
  editForm.value = {
    id: 0,
    imageUrl: '',
    name: '',
    description: '',
    uploaderId: user.value?.id || undefined,
    uploadTime: new Date().toISOString(),
    price: 0,
    eventId: undefined,
    tags: undefined,
  }
}

const saveEdit = async (id: string | number) => {
  try {
    console.log(editForm.value)
    await updateProduct(id, editForm.value)
    await mutateProducts()
    editingId.value = null
    toast({
      title: '更新成功',
      description: '产品信息已成功更新'
    })
  } catch (error) {
    toast({
      title: '更新失败',
      description: '产品信息更新失败'
    })
    console.error(error)
  }
}

const handleDelete = async (id: string | number) => {
  try {
    await deleteProduct(id)
    await mutateProducts()
    toast({
      title: '删除成功',
      description: '产品已成功删除'
    })
  } catch (error) {
    toast({
      title: '删除失败',
      description: '产品删除失败'
    })
    console.error(error)
  }
}

const showCreateForm = ref(false)

const newProductForm = ref<UIProduct>({
  id: 0,
  imageUrl: '',
  name: '',
  description: '',
  uploaderId: user.value?.id || undefined,
  uploadTime: new Date().toISOString(),
  price: 0,
  eventId: undefined,
  tags: undefined,
})

const handleCreate = async () => {
  try {
    await createProduct(newProductForm.value)
    await mutateProducts()
    showCreateForm.value = false
    newProductForm.value = {
      id: 0,
      imageUrl: '',
      name: '',
      description: '',
      uploaderId: user.value?.id || undefined,
      uploadTime: new Date().toISOString(),
      price: 0,
      eventId: undefined,
      tags: undefined,
    }
    toast({
      title: '创建成功',
      description: '产品已成功创建'
    })
  } catch (error) {
    toast({
      title: '创建失败',
      description: '产品创建失败'
    })
    console.error(error)
  }
}
</script>

<template>
  <div class="p-6">
    <header class="flex justify-between items-center mb-6">
      <div class="space-y-4">
        <h1 class="text-3xl font-bold tracking-tight">产品管理</h1>
      </div>
    </header>
    <client-only>
      <div v-if="!productsData">
        <div class="flex justify-center py-4">
          <span class="text-muted-foreground">加载中...</span>
        </div>
      </div>
      <div v-else-if="isAdmin" class="mt-6">
        <Card>
          <CardHeader class="flex flex-row items-center justify-between">
            <div>
              <CardTitle>产品管理</CardTitle>
              <CardDescription>管理系统产品</CardDescription>
            </div>
            <Button @click="showCreateForm = true">新建产品</Button>
          </CardHeader>
          <CardContent>
            <div v-if="isLoading" class="flex justify-center py-4">
              <span class="text-muted-foreground">加载中...</span>
            </div>
            <Table v-else>
              <TableHeader>
                <TableRow>
                  <TableHead>ID</TableHead>
                  <TableHead>名称</TableHead>
                  <TableHead>描述</TableHead>
                  <TableHead>价格</TableHead>
                  <TableHead>操作</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                <TableRow v-for="product in productsData.records" :key="product.id">
                  <TableCell>{{ product.id }}</TableCell>
                  <TableCell>
                    <div v-if="editingId === product.id">
                      <ProductForm :form="editForm"/>
                    </div>
                    <span v-else>{{ product.name }}</span>
                  </TableCell>
                  <TableCell>
                    <div v-if="editingId === product.id">
                      <Input v-model="editForm.description" :type="editForm.description ? 'text' : 'null'"/>
                    </div>
                    <span v-else>{{ product.description }}</span>
                  </TableCell>
                  <TableCell>
                    <div v-if="editingId === product.id">
                      <Input v-model="editForm.price" :type="editForm.price ? 'number' : 'null'"/>
                    </div>
                    <span v-else>{{ product.price }}</span>
                  </TableCell>
                  <TableCell>
                    <div class="flex space-x-2">
                      <template v-if="editingId === product.id">
                        <Button
                            variant="default"
                            size="sm"
                            @click="saveEdit(product.id)"
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
                            @click="startEdit(product)"
                        >
                          编辑
                        </Button>
                        <Button
                            variant="destructive"
                            size="sm"
                            @click="handleDelete(product.id)"
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
              <DialogTitle>新建产品</DialogTitle>
              <DialogDescription>
                创建产品
              </DialogDescription>
            </DialogHeader>
            <ProductForm :form="newProductForm"/>
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