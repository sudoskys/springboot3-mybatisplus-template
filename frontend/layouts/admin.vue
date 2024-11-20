<script setup lang="ts">
import { useAuthGuard } from '@/composables/useAuthGuard';
import { useRoute } from 'vue-router';
import { Toaster } from '@/components/ui/toast';
import { SidebarProvider, SidebarInset,SidebarTrigger } from '@/components/ui/sidebar'
import { Separator } from '@/components/ui/separator'
import { Breadcrumb, BreadcrumbList, BreadcrumbItem, BreadcrumbLink, BreadcrumbPage, BreadcrumbSeparator } from '@/components/ui/breadcrumb'

const route = useRoute();

// 使用带有自定义配置的认证守卫
useAuthGuard({
  routes: true,
  redirectTo: '/login',
  adminOnly: false
})
</script>

<template>
  <div>
    <SidebarProvider
      :style="{
        '--sidebar-width': '16rem',
        '--sidebar-width-mobile': '16rem'
      }"
    >
      <AppSidebar />
      <SidebarInset>
        <header class="flex h-16 shrink-0 items-center gap-2 transition-[width,height] ease-linear group-has-[[data-collapsible=icon]]/sidebar-wrapper:h-12">
          <div class="flex items-center gap-2 px-4">
            <SidebarTrigger class="-ml-1" />
            <Separator orientation="vertical" class="mr-2 h-4" />
            <Breadcrumb>
            <BreadcrumbList>
              <BreadcrumbItem class="hidden md:block">
                <BreadcrumbLink href="#">
                  仪表盘
                </BreadcrumbLink>
              </BreadcrumbItem>
              <BreadcrumbSeparator class="hidden md:block" />
              <BreadcrumbItem>
                <BreadcrumbPage>{{ route.name }}</BreadcrumbPage>
              </BreadcrumbItem>
              </BreadcrumbList>
            </Breadcrumb>
          </div>
        </header>
        <div class="flex flex-1 flex-col gap-4 p-4 pt-0">
          <slot />
        </div>
        <Toaster />
      </SidebarInset>
    </SidebarProvider>
  </div>
</template>

<style scoped>

</style>