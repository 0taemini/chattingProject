<template>
  <div class="pa-4">
    <h2>채팅방 #{{ id }}</h2>
    <ChatRoom :roomId="id" />
  </div>
</template>

<script setup>
import { useRoute } from 'vue-router'
import ChatRoom from '@/components/chat/ChatRoom.vue'

const route = useRoute()
const id = route.params.id
</script>
