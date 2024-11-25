<template>
  <form class="space-y-4">
    <!-- 产品名称 -->
    <div class="space-y-2">
      <Label for="name">产品名称</Label>
      <Input id="name" v-model="form.name" placeholder="请输入产品名称"/>
    </div>

    <!-- 产品描述 -->
    <div class="space-y-2">
      <Label for="description">产品描述</Label>
      <Input
          id="description"
          v-model="form.description"
          placeholder="请输入产品描述"
      />
    </div>

    <!-- 图片链接 -->
    <div class="space-y-2">
      <Label for="imageUrl">图片链接</Label>
      <Input id="imageUrl" v-model="form.imageUrl" placeholder="请输入图片链接"/>
    </div>

    <!-- 产品价格 -->
    <div class="space-y-2">
      <Label for="price">价格</Label>
      <Input
          id="price"
          v-model="form.price"
          type="number"
          placeholder="请输入价格"
      />
    </div>

    <!-- 事件 ID
    <div class="space-y-2">
      <Label for="eventId">事件 ID (可选)</Label>
      <Input id="eventId" v-model="form.eventId" type="number" placeholder="事件 ID"/>
    </div>
    -->

    <!-- 标签 -->
    <div class="space-y-2">
      <Label for="tags">标签 (以逗号分隔，可选)</Label>
      <Input
          id="tags"
          v-model="tagsInput"
          placeholder="例如 '服装, 时尚, 限量'"
          @blur="updateTags"
      />
    </div>

    <!-- 上传时间 -->
    <div class="space-y-2">
      <Label for="uploadTime">上传时间 (只读)</Label>
      <Input id="uploadTime" :value="uploadTime" readonly/>
    </div>
  </form>
</template>

<script setup lang="ts">
import {ref, computed, watch} from "vue";
import {Input} from "@/components/ui/input";
import {Label} from "@/components/ui/label";
import {type UIProduct} from "~/lib/api";

// 接收 `form` 属性：外部传入的产品表单对象
const props = defineProps<{
  form: UIProduct;
}>();

// 双向绑定 `props.form` 的中间层，避免直接修改父级状态
const form = ref({...props.form});

// 处理标签输入框的字符串
const tagsInput = ref(form.value.tags);

// 更新表单对象中的 `tags` 数组
const updateTags = () => {
  form.value.tags = tagsInput.value || ""
};

// 上传时间 (只读)
const uploadTime = computed(() => {
  return new Date(form.value.uploadTime as string).toLocaleString();
});

// 监听 props.form 的变化，同步到本地 state
watch(
    () => props.form,
    (newVal) => {
      form.value = {...newVal};
      tagsInput.value = form.value.tags || ""
    },
    {deep: true}
);
</script>