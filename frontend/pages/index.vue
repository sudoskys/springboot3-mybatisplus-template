<script setup lang="ts">
import { ref } from "vue";
import { Settings, ShoppingCart } from "lucide-vue-next";

definePageMeta({
  requiresAuth: true,
})

const products = ref([
  {
    id: 1,
    name: "商品 1",
    description: "这是一个很棒的商品描述",
    price: 199,
    image: "https://picsum.photos/400/300"
  },
  {
    id: 2,
    name: "商品 2",
    description: "这是另一个很棒的商品描述",
    price: 299,
    image: "https://picsum.photos/400/300"
  },
  {
    id: 3,
    name: "商品 3",
    description: "这是第三个很棒的商品描述",
    price: 399,
    image: "https://picsum.photos/400/300"
  },
  {
    id: 4,
    name: "商品 4",
    description: "这是第四个很棒的商品描述",
    price: 499,
    image: "https://picsum.photos/400/300"
  }
]);

</script>

<template>
  <div class="container mx-auto p-4 bg-background">
    <!-- 页面标题 -->
    <div class="flex justify-between items-center">
      <h1 class="m-4 text-2xl font-bold mb-6">热门商品</h1>
      <div class="flex items-center gap-2">
        
        <Button variant="ghost">
          <ShoppingCart class="mr-2 w-4 h-4"/>
          下单
        </Button>
        <Button variant="ghost" @click="navigateTo('/dashboard')">
          <Settings class="mr-2 w-4 h-4"/>
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
          <CardContent>{{ product.description }}</CardContent>
          <CardFooter>
            <div class="flex w-full justify-between items-center flex-row">
              <span class="text-primary text-xl font-bold">¥{{ product.price }}</span>
              <Button class="animate-bounce" color="primary">加入购物车</Button>
            </div>
          </CardFooter>
        </Card>
      </div>
    </div>
  </div>
</template>


<style scoped>
</style>
