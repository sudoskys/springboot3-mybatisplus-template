<script setup lang="ts">
import {Home, User2, ChevronUp} from 'lucide-vue-next'
import {DropdownMenu, DropdownMenuTrigger, DropdownMenuContent, DropdownMenuItem} from '@/components/ui/dropdown-menu'
import {useUserStore} from '~/stores/user'
import {storeToRefs} from 'pinia'
import {useRouter} from 'vue-router'
import {
  Sidebar, SidebarContent, SidebarGroup, SidebarGroupLabel, SidebarGroupContent,
  SidebarMenu, SidebarMenuItem, SidebarMenuButton, SidebarFooter
} from '@/components/ui/sidebar'

const userStore = useUserStore()
const router = useRouter()
const {user} = storeToRefs(userStore)

// 处理登出
const handleLogout = async () => {
  try {
    await userStore.logout()
    await router.push('/login')
  } catch (error) {
    console.error('登出失败:', error)
  }
}

// 菜单项定义
const items = [
  {
    title: "仪表盘",
    url: "/dashboard",
    icon: Home,
  },
  {
    title: "用户管理",
    url: "/dashboard/user",
    icon: User2,
  },
  {
    title: "产品管理",
    url: "/dashboard/product",
    icon: User2,
  },
  {
    title: "订单管理",
    url: "/dashboard/orders",
    icon: User2,
  }
]
</script>

<template>
  <Sidebar
      collapsible="icon"
  >
    <SidebarContent>
      <SidebarGroup>
        <SidebarGroupLabel>应用面板</SidebarGroupLabel>
        <SidebarMenu>
          <SidebarMenuItem v-for="item in items" :key="item.title">
            <SidebarMenuButton as-child>
              <Button variant="ghost">
                <NuxtLink :to="item.url" class="flex items-center gap-2 w-full h-full">
                  <component :is="item.icon" class="h-4 w-4"/>
                  <span>{{ item.title }}</span>
                </NuxtLink>
              </Button>
            </SidebarMenuButton>
          </SidebarMenuItem>
        </SidebarMenu>
      </SidebarGroup>
    </SidebarContent>

    <SidebarFooter>
      <SidebarMenu>
        <SidebarMenuItem>
          <DropdownMenu>
            <DropdownMenuTrigger asChild>
              <SidebarMenuButton>
                <div class="flex items-center gap-2">
                  <User2 class="h-4 w-4"/>
                  <span>{{ user?.email }}</span>
                  <ChevronUp class="ml-auto h-4 w-4"/>
                </div>
              </SidebarMenuButton>
            </DropdownMenuTrigger>
            <DropdownMenuContent side="top" class="w-[--radix-popper-anchor-width]">
              <DropdownMenuItem>
                <NuxtLink to="/settings/account" class="flex items-center gap-2">
                  <span>账户设置</span>
                </NuxtLink>
              </DropdownMenuItem>
              <DropdownMenuItem @click="handleLogout">
                <span>退出登录</span>
              </DropdownMenuItem>
            </DropdownMenuContent>
          </DropdownMenu>
        </SidebarMenuItem>
      </SidebarMenu>
    </SidebarFooter>
  </Sidebar>
</template>

<style scoped>
/* 如果需要添加样式 */
</style>
