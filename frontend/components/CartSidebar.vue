<script setup lang="ts">
import { computed } from 'vue';
import { X, Trash2 } from 'lucide-vue-next';
import { Button } from '@/components/ui/button';
import { useCart } from '@/composables/useCart';

const { cart, removeFromCart, updateQuantity, clearCart } = useCart();

const totalPrice = computed(() => {
  return cart.value.reduce((total, item) => total + item.price * item.quantity, 0);
});

defineProps<{
  isOpen: boolean;
  onClose: () => void;
}>();
</script>

<template>
  <div
      class="fixed inset-y-0 right-0 z-50 w-full sm:w-96 bg-background shadow-lg transform transition-transform duration-300 ease-in-out"
      :class="{ 'translate-x-0': isOpen, 'translate-x-full': !isOpen }"
  >
    <div class="flex flex-col h-full">
      <div class="flex justify-between items-center p-4 border-b">
        <h2 class="text-xl font-bold">购物车</h2>
        <Button variant="ghost" size="icon" @click="onClose">
          <X class="h-6 w-6" />
        </Button>
      </div>
      <div class="flex-grow overflow-y-auto p-4">
        <div v-if="cart.length === 0" class="text-center text-gray-500 mt-8">
          购物车是空的
        </div>
        <div v-else class="space-y-4">
          <div v-for="item in cart" :key="item.id" class="flex items-center space-x-4 border-b pb-4">
            <img :src="item.imageUrl" :alt="item.name" class="w-16 h-16 object-cover rounded" />
            <div class="flex-grow">
              <h3 class="font-semibold">{{ item.name }}</h3>
              <p class="text-sm text-gray-500">¥{{ item.price }}</p>
              <div class="flex items-center mt-2">
                <Button variant="outline" size="icon" @click="updateQuantity(item.id, item.quantity - 1)">-</Button>
                <span class="mx-2">{{ item.quantity }}</span>
                <Button variant="outline" size="icon" @click="updateQuantity(item.id, item.quantity + 1)">+</Button>
              </div>
            </div>
            <Button variant="ghost" size="icon" @click="removeFromCart(item.id)">
              <Trash2 class="h-5 w-5" />
            </Button>
          </div>
        </div>
      </div>
      <div class="p-4 border-t">
        <div class="flex justify-between items-center mb-4">
          <span class="font-semibold">总计：</span>
          <span class="font-bold text-xl">¥{{ totalPrice.toFixed(2) }}</span>
        </div>
        <Button class="w-full" size="lg" :disabled="cart.length === 0" @click="clearCart">
          结算
        </Button>
      </div>
    </div>
  </div>
</template>