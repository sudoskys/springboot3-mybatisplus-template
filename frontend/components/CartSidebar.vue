<script setup lang="ts">
import { computed, ref } from 'vue';
import { X, Trash2 } from 'lucide-vue-next';
import { Button } from '@/components/ui/button';
import { useCart } from '@/composables/useCart';
import { createOrder } from '@/lib/api';
import { useToast } from '@/components/ui/toast/use-toast';

const { cart, removeFromCart, updateQuantity, clearCart } = useCart();
const { toast } = useToast();

const totalPrice = computed(() => {
  return cart.value.reduce((total, item) => total + item.price * item.quantity, 0);
});

const { isOpen, onClose } = defineProps<{
  isOpen: boolean;
  onClose: () => void;
}>();

const address = ref('');
const phone = ref('');

async function handleCheckout() {
  try {
    if (!address.value || !phone.value) {
      toast({
        title: "请填写收货信息",
        description: "收货地址和联系电话不能为空",
        variant: "destructive"
      });
      return;
    }

    const orderData = {
      products: cart.value.map(item => ({
        productId: item.id,
        quantity: item.quantity
      })),
      address: address.value,
      phone: phone.value
    };
    
    await createOrder(orderData);
    clearCart();
    address.value = '';
    phone.value = '';
    toast({
      title: "订单创建成功",
      description: "您的订单已成功提交"
    });
    onClose();
  } catch (error: any) {
    toast({
      title: "订单创建失败",
      description: error.message,
      variant: "destructive"
    });
  }
}
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
                <Button variant="outline" size="icon" @click="updateQuantity(item.id.toString(), item.quantity - 1)">-</Button>
                <span class="mx-2">{{ item.quantity }}</span>
                <Button variant="outline" size="icon" @click="updateQuantity(item.id.toString(), item.quantity + 1)">+</Button>
              </div>
            </div>
            <Button variant="ghost" size="icon" @click="removeFromCart(item.id.toString())">
              <Trash2 class="h-5 w-5" />
            </Button>
          </div>
        </div>
      </div>
      <div class="p-4 border-t">
        <div class="space-y-4 mb-4">
          <div>
            <label class="block text-sm font-medium mb-1">收货地址</label>
            <input v-model="address" 
                   type="text" 
                   class="w-full p-2 border rounded"
                   placeholder="请输入收货地址" />
          </div>
          <div>
            <label class="block text-sm font-medium mb-1">联系电话</label>
            <input v-model="phone" 
                   type="tel" 
                   class="w-full p-2 border rounded"
                   placeholder="请输入联系电话" />
          </div>
        </div>
        
        <div class="flex justify-between items-center mb-4">
          <span class="font-semibold">总计：</span>
          <span class="font-bold text-xl">¥{{ totalPrice.toFixed(2) }}</span>
        </div>
        <Button 
          class="w-full" 
          size="lg" 
          :disabled="cart.length === 0" 
          @click="handleCheckout"
        >
          结算
        </Button>
      </div>
    </div>
  </div>
</template>