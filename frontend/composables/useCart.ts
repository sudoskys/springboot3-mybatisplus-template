import { ref, computed } from 'vue';
import type { UIProduct } from '@/lib/api';

interface CartItem extends UIProduct {
    quantity: number;
}

const cart = ref<CartItem[]>([]);

export function useCart() {
    const addToCart = (product: UIProduct) => {
        const existingItem = cart.value.find(item => item.id === product.id);
        if (existingItem) {
            existingItem.quantity++;
        } else {
            cart.value.push({ ...product, quantity: 1 });
        }
    };

    const removeFromCart = (productId: string) => {
        cart.value = cart.value.filter(item => item.id !== productId);
    };

    const updateQuantity = (productId: string, newQuantity: number) => {
        const item = cart.value.find(item => item.id === productId);
        if (item) {
            if (newQuantity > 0) {
                item.quantity = newQuantity;
            } else {
                removeFromCart(productId);
            }
        }
    };

    const clearCart = () => {
        cart.value = [];
    };

    const totalItems = computed(() => {
        return cart.value.reduce((total, item) => total + item.quantity, 0);
    });

    const totalPrice = computed(() => {
        return cart.value.reduce((total, item) => total + item.price * item.quantity, 0);
    });

    return {
        cart,
        addToCart,
        removeFromCart,
        updateQuantity,
        clearCart,
        totalItems,
        totalPrice,
    };
}