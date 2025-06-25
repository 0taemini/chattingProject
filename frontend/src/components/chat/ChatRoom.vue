<template>
  <v-container>
    <v-list>
      <v-list-item v-for="msg in messages" :key="msg.id">
        <v-list-item-content>
          <strong>{{ msg.sender }}:</strong> {{ msg.content }}
        </v-list-item-content>
      </v-list-item>
    </v-list>

    <v-text-field
      v-model="input"
      label="메시지 입력"
      @keyup.enter="send()"
    />
  </v-container>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import {useChat} from '@/hooks/useChat'  // 여러분이 만든 훅

const input = ref('')
// const { messages, connect, sendMessage } = useChat()
const { messages, connect, sendMessage } = useChat({
  url: 'http://localhost:8080/ws-chat',
  topic: '/topic/chat',
  destination: '/app/chat.send'
})

onMounted(() => {
  connect()   // 서버에 구독(subscribe) 신청
})

function send() {
  if (!input.value) return
  sendMessage({ sender: 'me', content: input.value })
  input.value = ''
}
</script>
