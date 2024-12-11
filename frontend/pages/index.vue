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

const toggleCart = () => {
  isCartOpen.value = !isCartOpen.value;
};

onMounted(async () => {
  getAllProducts().then((response) => {
    products.value = response.records;
  });
});
</script>

<template>
  <div class="container mx-auto p-4 bg-background">
    <!-- 页面标题 -->
    <div class="flex justify-between items-center">
      <h1 class="m-4 text-2xl font-bold mb-6">热门商品</h1>
      <div class="flex items-center gap-2">
        <Button variant="ghost" @click="toggleCart">
          <ShoppingCart class="mr-2 w-4 h-4" />
          购物车 ({{ cart.length }})
        </Button>
        <Button variant="ghost" @click="navigateTo('/dashboard')">
          <Settings class="mr-2 w-4 h-4" />
          后台
        </Button>
      </div>
    </div>
    <!-- 商品网格 -->
    <div class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4">
      <div v-for="product in products" :key="product.id" class="card">
        <Card>
          <CardHeader>
            <h2 class="font-bold text-xl">{{ product.name }}</h2>
          </CardHeader>
          <CardContent>
            <img :src="product.imageUrl" alt="Product Image" class="w-full h-auto mb-4" />
            {{ product.description }}
          </CardContent>
          <CardFooter>
            <div class="flex w-full justify-between items-center flex-row">
              <span class="text-primary text-xl font-bold">¥{{ product.price }}</span>
              <Button @click="addToCart(product)">加入购物车</Button>
            </div>
          </CardFooter>
        </Card>
      </div>
    </div>

    <!-- 购物车侧边栏 -->
    <CartSidebar :isOpen="isCartOpen" :onClose="toggleCart" />
  </div>
</template>