<script setup lang="ts">
import { ref, onMounted, computed, watch, onUnmounted } from 'vue';
import { getAllOrders, updateOrder, deleteOrder } from '@/lib/api';
import type { Order, UIProduct } from '@/lib/api';
import { getProductById } from '@/lib/api';
import { Button } from '@/components/ui/button';
import { useToast } from '@/components/ui/toast/use-toast';
import { Input } from '@/components/ui/input';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import { Trash2 } from 'lucide-vue-next';

definePageMeta({
  layout: 'admin',
})

const orders = ref<Order[]>([]);
const productCache = new Map<string | number, UIProduct>();
const loading = ref(false);
const currentPage = ref(1);
const hasMore = ref(true);
const { toast } = useToast();

const searchQuery = ref('');
const selectedStatus = ref<string>('ALL');

const statusOptions = [
  { value: 'ALL', label: '全部状态' },
  { value: 'PENDING', label: '待处理' },
  { value: 'COMPLETED', label: '已完成' }
];

const filteredOrders = computed(() => {
  return orders.value.filter(order => {
    if (selectedStatus.value !== 'ALL' && order.status !== selectedStatus.value) {
      return false;
    }
    
    if (searchQuery.value) {
      const searchLower = searchQuery.value.toLowerCase();
      const hasMatchingProduct = order.items?.some(item => {
        const product = productCache.get(item.productId);
        return product?.name.toLowerCase().includes(searchLower) ||
               product?.description?.toLowerCase().includes(searchLower);
      });
      
      return hasMatchingProduct ||
             order.id.toString().includes(searchLower) ||
             order.userId.toString().includes(searchLower) ||
             order.address?.toLowerCase().includes(searchLower) ||
             order.phone?.includes(searchQuery.value);
    }
    
    return true;
  });
});

async function loadProductDetails(productId: string | number) {
  if (!productCache.has(productId)) {
    const product = await getProductById(Number(productId));
    productCache.set(productId, product);
  }
  return productCache.get(productId);
}

async function loadOrders() {
  loading.value = true;
  try {
    const newOrders = await getAllOrders(currentPage.value);
    if (newOrders.length === 0) {
      hasMore.value = false;
      return;
    }
    if (currentPage.value === 1) {
      orders.value = newOrders;
    } else {
      orders.value.push(...newOrders);
    }
    for (const order of newOrders) {
      if (order.items) {
        await Promise.all(order.items.map(item => loadProductDetails(item.productId)));
      }
    }
  } catch (error) {
    console.error('加载订单失败:', error);
  } finally {
    loading.value = false;
  }
}

async function completeOrder(orderId: string) {
  try {
    await updateOrder(orderId, { status: 'COMPLETED' });
    toast({
      title: "订单状态更新成功",
      description: "订单已标记为完成"
    });
    await loadOrders();
  } catch (error: any) {
    toast({
      title: "更新失败",
      description: error.message,
      variant: "destructive"
    });
  }
}

async function handleDeleteOrder(orderId: string) {
  try {
    await deleteOrder(orderId);
    toast({
      title: "删除成功",
      description: "订单已成功删除"
    });
    await loadOrders();
  } catch (error: any) {
    toast({
      title: "删除失败",
      description: error.message,
      variant: "destructive"
    });
  }
}

async function loadMore() {
  if (loading.value || !hasMore.value) return;
  currentPage.value++;
  await loadOrders();
}

function resetSearch() {
  currentPage.value = 1;
  hasMore.value = true;
  orders.value = [];
  loadOrders();
}

watch([searchQuery, selectedStatus], () => {
  resetSearch();
});

onMounted(() => {
  loadOrders();
  window.addEventListener('scroll', () => {
    const { scrollTop, scrollHeight, clientHeight } = document.documentElement;
    if (scrollTop + clientHeight >= scrollHeight - 100) {
      loadMore();
    }
  });
});

onUnmounted(() => {
  window.removeEventListener('scroll', loadMore);
});
</script>

<template>
  <div class="container mx-auto p-6">
    <header class="flex justify-between items-center mb-6">
      <div>
        <h1 class="text-3xl font-bold tracking-tight">订单管理</h1>
        <p class="text-muted-foreground mt-2">管理所有用户订单</p>
      </div>
    </header>
    
    <div class="mb-6 space-y-4 sm:space-y-0 sm:flex sm:items-center sm:gap-4">
      <Input
        v-model="searchQuery"
        placeholder="搜索订单号、用户ID、商品名称或地址..."
        class="sm:w-96"
      />
      
      <Select v-model="selectedStatus">
        <SelectTrigger class="w-32">
          <SelectValue placeholder="选择状态" />
        </SelectTrigger>
        <SelectContent>
          <SelectItem v-for="option in statusOptions" 
                     :key="option.value" 
                     :value="option.value">
            {{ option.label }}
          </SelectItem>
        </SelectContent>
      </Select>
    </div>
    
    <div v-if="loading" class="text-center py-8">
      加载中...
    </div>
    
    <div v-else class="space-y-4">
      <div v-for="order in filteredOrders" :key="order.id" 
           class="border rounded-lg p-4 shadow-sm">
        <div class="flex justify-between items-center mb-4">
          <div>
            <span class="text-sm text-gray-500">订单号：{{ order.id }}</span>
            <span class="ml-4 text-sm">用户ID: {{ order.userId }}</span>
          </div>
          <div class="flex items-center gap-2">
            <span class="text-sm font-medium">{{ order.status }}</span>
            <Button 
              v-if="order.status === 'PENDING'"
              size="sm"
              @click="completeOrder(String(order.id))"
            >
              完成订单
            </Button>
            <Button 
              variant="destructive"
              size="sm"
              @click="handleDeleteOrder(String(order.id))"
            >
              <Trash2 class="h-4 w-4" />
            </Button>
          </div>
        </div>
        
        <!-- 联系信息 -->
        <div class="mb-4 text-sm text-gray-600">
          <div>收货地址: {{ order.address }}</div>
          <div>联系电话: {{ order.phone }}</div>
        </div>
        
        <!-- 商品列表 -->
        <div class="space-y-2">
          <div v-for="item in order.items" :key="item.id!" 
               class="flex justify-between items-center">
            <div class="flex items-center gap-4">
              <img 
                :src="productCache.get(item.productId)?.imageUrl" 
                :alt="productCache.get(item.productId)?.name"
                class="w-16 h-16 object-cover rounded"
              />
              <div>
                <div class="font-medium">{{ productCache.get(item.productId)?.name }}</div>
                <div class="text-sm text-gray-500">
                  {{ productCache.get(item.productId)?.description }}
                </div>
              </div>
            </div>
            <div class="text-right">
              <div>数量: {{ item.quantity }}</div>
              <div>单价: ¥{{ item.price }}</div>
            </div>
          </div>
        </div>
        
        <div class="mt-4 pt-4 border-t flex justify-between items-center">
          <span class="text-gray-500">{{ order.createdAt }}</span>
          <span class="font-bold">总计: ¥{{ order.totalPrice }}</span>
        </div>
      </div>
      
      <div v-if="loading" class="text-center py-4">
        加载中...
      </div>
      <div v-else-if="!hasMore" class="text-center py-4 text-gray-500">
        没有更多订单了
      </div>
    </div>
  </div>
</template> 