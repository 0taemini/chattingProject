<template>
  <v-container>
    <h3>채팅방 #{{ roomId }}</h3>

    <v-list height="400px" class="overflow-auto">
      <v-list-item
        v-for="msg in messages"
        :key="msg.timestamp + msg.sender"
      >
        <v-list-item-title>
          <strong>{{ msg.sender }}:</strong> {{ msg.content }}
        </v-list-item-title>
        <v-list-item-subtitle>
          {{ formatTime(msg.timestamp) }}
        </v-list-item-subtitle>
      </v-list-item>
    </v-list>

    <v-text-field
      v-model="input"
      label="메시지 입력"
      @keyup.enter="send"
      :disabled="!connected"
    />

    <v-btn
      color="primary"
      @click="send"
      :disabled="!connected || !input"
    >
      전송
    </v-btn>
  </v-container>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'
import axios from 'axios'               // ← 반드시 추가
import { useChat } from '@/hooks/useChat'

const props = defineProps({
  roomId: {
    type: String,
    required: true
  }
})

const input = ref('')
const messages = ref([])
const connected = ref(false)

const { connect, sendMessage, disconnect } = useChat({
  brokerUrl: 'http://localhost:8080/ws-chat',
  roomId: props.roomId,
  onMessage: msg => messages.value.push(msg),
  onConnect: () => { connected.value = true },
  onDisconnect: () => { connected.value = false }
})

onMounted(async () => {
  // 1) 과거 메시지 로딩
  try {
    const res = await axios.get(
      `http://localhost:8080/api/chat/history/${props.roomId}`
    )
    messages.value = res.data.map(m => ({
      sender: m.sender,
      content: m.content,
      timestamp: m.timestamp
    }))
  } catch (e) {
    console.error('히스토리 로드 실패', e)
  }
  // 2) 실시간 구독 시작
  connect()
})

onBeforeUnmount(disconnect)

function send() {
  if (!input.value || !connected.value) return
  sendMessage({
    type: 'TALK',      // 서버 enum과 일치
    sender: 'me',
    content: input.value
  })
  input.value = ''
}

function formatTime(ts) {
  const d = new Date(ts)
  return `${d.getHours()}:${String(d.getMinutes()).padStart(2,'0')}`
}
</script>

<style scoped>
.v-list {
  background: #f9f9f9;
  border-radius: 4px;
}
</style>
