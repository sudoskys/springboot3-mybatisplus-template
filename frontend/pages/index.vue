<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { Settings, ShoppingCart } from 'lucide-vue-next';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardFooter, CardHeader } from '@/components/ui/card';
import { definePageMeta, navigateTo } from '#imports';
import { getAllProducts, type UIProduct } from '@/lib/api';
import { useCart } from '@/composables/useCart';
import CartSidebar from '@/components/CartSidebar.vue';

definePageMeta({
  requiresAuth: true,
});

const products = ref<UIProduct[]>();
const { addToCart, cart } = useCart();
const isCartOpen = ref(false);
const isLoading = ref(true);

const toggleCart = () => {
  isCartOpen.value = !isCartOpen.value;
};

onMounted(async () => {
  try {
    const response = await getAllProducts();
    products.value = response.records;
  } catch (error) {
    console.error('Failed to fetch products:', error);
  } finally {
    isLoading.value = false;
  }
});
</script>

<template>
  <div class="min-h-screen bg-gray-50">
    <!-- 导航栏 -->
    <nav class="bg-white shadow-sm sticky top-0 z-50">
      <div class="container mx-auto px-4 py-3">
        <div class="flex justify-between items-center">
          <h1 class="text-2xl font-bold text-primary">网上商城</h1>
          <div class="flex items-center gap-4">
            <Button variant="ghost" @click="toggleCart" class="hover:bg-gray-100">
              <ShoppingCart class="mr-2 w-5 h-5" />
              <span class="relative">
                购物车
                <span v-if="cart.length" 
                      class="absolute -top-2 -right-4 bg-primary text-white rounded-full text-xs px-2 py-1">
                  {{ cart.length }}
                </span>
              </span>
            </Button>
            <Button variant="ghost" @click="navigateTo('/dashboard')" class="hover:bg-gray-100">
              <Settings class="mr-2 w-5 h-5" />
              后台管理
            </Button>
          </div>
        </div>
      </div>
    </nav>

    <!-- 主要内容区 -->
    <div class="container mx-auto p-6">
      <!-- 页面标题 -->
      <div class="mb-8">
        <h2 class="text-3xl font-bold text-gray-800">热门商品</h2>
        <p class="text-gray-600 mt-2">发现最新最热门的商品</p>
      </div>

      <!-- 商品网格 -->
      <div v-if="isLoading" class="flex justify-center items-center min-h-[400px]">
        <div class="animate-spin rounded-full h-12 w-12 border-4 border-primary border-t-transparent"></div>
      </div>
      <div v-else-if="!products?.length" class="text-center text-gray-500 min-h-[400px] flex items-center justify-center">
        暂无商品
      </div>
      <div v-else class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-6">
        <div v-for="product in products" :key="product.id" 
             class="transform transition-transform duration-200 hover:scale-105">
          <Card class="h-full hover:shadow-lg">
            <CardHeader class="space-y-2">
              <img :src="product.imageUrl" 
                   :alt="product.name" 
                   class="w-full h-48 object-cover rounded-t-lg" />
              <h3 class="font-bold text-xl text-gray-800">{{ product.name }}</h3>
            </CardHeader>
            <CardContent>
              <p class="text-gray-600 line-clamp-2">{{ product.description }}</p>
            </CardContent>
            <CardFooter>
              <div class="flex w-full justify-between items-center flex-row">
                <span class="text-primary text-2xl font-bold">¥{{ product.price }}</span>
                <Button @click="addToCart(product)" 
                        class="bg-primary hover:bg-primary/90">
                  加入购物车
                </Button>
              </div>
            </CardFooter>
          </Card>
        </div>
      </div>
    </div>

    <!-- 购物车侧边栏 -->
    <CartSidebar :isOpen="isCartOpen" :onClose="toggleCart" />
  </div>
</template>