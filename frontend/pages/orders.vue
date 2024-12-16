<script setup lang="ts">
import { ref, onMounted, computed } from 'vue';
import { getCurrentUserOrders, getProductById } from '@/lib/api';
import type { Order, UIProduct } from '@/lib/api';
import { Input } from '@/components/ui/input';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import { RangeCalendar } from '@/components/ui/range-calendar';
import type { DateRange } from 'radix-vue';
import { getLocalTimeZone } from '@internationalized/date';
import { Button } from '@/components/ui/button';
import { Popover, PopoverContent, PopoverTrigger } from '@/components/ui/popover';
import { Calendar as CalendarIcon } from 'lucide-vue-next';
import { DateFormatter } from '@internationalized/date';
import { ArrowLeft } from 'lucide-vue-next';
import { navigateTo } from '#imports';

const orders = ref<Order[]>([]);
const productCache = new Map<string | number, UIProduct>();
const loading = ref(false);

// 搜索和过滤状态
const searchQuery = ref('');
const selectedStatus = ref<string>('ALL');
const dateRange = ref<DateRange>();

// 状态选项
const statusOptions = [
  { value: 'ALL', label: '全部状态' },
  { value: 'PENDING', label: '待处理' },
  { value: 'COMPLETED', label: '已完成' }
];

const df = new DateFormatter('zh-CN', { dateStyle: 'medium' });

// 过滤后的订单
const filteredOrders = computed(() => {
  return orders.value.filter(order => {
    // 状态过滤
    if (selectedStatus.value !== 'ALL' && order.status !== selectedStatus.value) {
      return false;
    }
    
    // 日期过滤
    if (dateRange.value?.start && dateRange.value?.end && order.createdAt) {
      const orderDate = new Date(order.createdAt);
      const startDate = dateRange.value.start.toDate(getLocalTimeZone());
      const endDate = dateRange.value.end.toDate(getLocalTimeZone());
      if (orderDate < startDate || orderDate > endDate) {
        return false;
      }
    }
    
    // 搜索过滤
    if (searchQuery.value) {
      const searchLower = searchQuery.value.toLowerCase();
      const hasMatchingProduct = order.items?.some(item => {
        const product = productCache.get(item.productId);
        return product?.name.toLowerCase().includes(searchLower) ||
               product?.description?.toLowerCase().includes(searchLower);
      });
      
      return hasMatchingProduct ||
             order.id.toString().includes(searchLower) ||
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
    <div class="flex justify-between items-center mb-6">
      <h1 class="text-2xl font-bold">我的订单</h1>
      <Button variant="outline" @click="navigateTo('/')" class="flex items-center gap-2">
        <ArrowLeft class="w-4 h-4" />
        返回商店
      </Button>
    </div>
    
    <!-- 搜索和过滤工具栏 -->
    <div class="mb-6 space-y-4 sm:space-y-0 sm:flex sm:items-center sm:gap-4">
      <Input
        v-model="searchQuery"
        placeholder="搜索订单号、商品名称或地址..."
        class="sm:w-64"
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
      
      <Popover>
        <PopoverTrigger as-child>
          <Button variant="outline" class="w-[280px] justify-start text-left font-normal">
            <CalendarIcon class="mr-2 h-4 w-4" />
            <template v-if="dateRange?.start">
              <template v-if="dateRange.end">
                {{ df.format(dateRange.start.toDate(getLocalTimeZone())) }} - 
                {{ df.format(dateRange.end.toDate(getLocalTimeZone())) }}
              </template>
              <template v-else>
                {{ df.format(dateRange.start.toDate(getLocalTimeZone())) }}
              </template>
            </template>
            <template v-else>
              选择日期范围
            </template>
          </Button>
        </PopoverTrigger>
        <PopoverContent class="w-auto p-0">
          <RangeCalendar
            v-model="dateRange"
            initial-focus
            :number-of-months="2"
          />
        </PopoverContent>
      </Popover>
    </div>
    
    <div v-if="loading" class="text-center py-8">
      加载中...
    </div>
    
    <div v-else-if="filteredOrders.length === 0" class="text-center py-8 text-gray-500">
      暂无符合条件的订单
    </div>
    
    <!-- 订单列表 -->
    <div v-else class="space-y-4">
      <div v-for="order in filteredOrders" :key="order.id" 
           class="border rounded-lg p-4 shadow-sm">
        <div class="flex justify-between items-center mb-4">
          <span class="text-sm text-gray-500">订单号：{{ order.id }}</span>
          <span class="text-sm font-medium">{{ order.status }}</span>
        </div>
        
        <!-- 联系信息 -->
        <div class="mb-4 text-sm text-gray-600">
          <div>收货地址: {{ order.address }}</div>
          <div>联系电话: {{ order.phone }}</div>
        </div>
        
        <!-- 商品列表 -->
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