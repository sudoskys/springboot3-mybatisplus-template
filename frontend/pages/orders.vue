<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { getCurrentUserOrders, getProductById } from '@/lib/api';
import type { Order, UIProduct } from '@/lib/api';

const orders = ref<Order[]>([]);
const productCache = new Map<string | number, UIProduct>();
const loading = ref(false);

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
    orders.value = await getCurrentUserOrders();
    // 预加载所有商品信息
    for (const order of orders.value) {
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

onMounted(() => {
  loadOrders();
});
</script>

<template>
  <div class="container mx-auto p-4">
    <h1 class="text-2xl font-bold mb-6">我的订单</h1>
    
    <div v-if="loading" class="text-center py-8">
      加载中...
    </div>
    
    <div v-else-if="orders.length === 0" class="text-center py-8 text-gray-500">
      暂无订单
    </div>
    
    <div v-else class="space-y-4">
      <div v-for="order in orders" :key="order.id" 
           class="border rounded-lg p-4 shadow-sm">
        <div class="flex justify-between items-center mb-4">
          <span class="text-sm text-gray-500">订单号：{{ order.id }}</span>
          <span class="text-sm font-medium">{{ order.status }}</span>
        </div>
        
        <div class="space-y-2">
          <div v-for="product in order.items" :key="product.productId" 
               class="flex justify-between items-center">
            <div class="flex items-center gap-4">
              <img 
                :src="productCache.get(product.productId)?.imageUrl" 
                :alt="productCache.get(product.productId)?.name"
                class="w-16 h-16 object-cover rounded"
              />
              <div>
                <div class="font-medium">{{ productCache.get(product.productId)?.name }}</div>
                <div class="text-sm text-gray-500">
                  {{ productCache.get(product.productId)?.description }}
                </div>
              </div>
            </div>
            <div class="text-right">
              <div>数量: {{ product.quantity }}</div>
              <div>单价: ¥{{ product.price }}</div>
            </div>
          </div>
        </div>
        
        <div class="mt-4 pt-4 border-t flex justify-between items-center">
          <span class="text-gray-500">{{ order.createdAt }}</span>
          <span class="font-bold">总计: ¥{{ order.totalPrice }}</span>
        </div>
      </div>
    </div>
  </div>
</template> 