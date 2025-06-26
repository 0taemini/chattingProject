// src/hooks/useChat.js
import { ref, onMounted, onUnmounted } from 'vue'
import SockJS from 'sockjs-client'
import { Client } from '@stomp/stompjs'

/**
 * @param brokerUrl    SockJS 엔드포인트, ex) 'http://localhost:8080/ws-chat'
 * @param roomId       채팅방 ID
 * @param onMessage    메시지 수신 시 호출되는 콜백 (msg: 객체)
 * @param onConnect    연결 성공 시 호출되는 콜백
 * @param onDisconnect 연결 종료 시 호출되는 콜백
 */
export function useChat({ brokerUrl, roomId, onMessage, onConnect, onDisconnect }) {
  const connected = ref(false)
  let stompClient = null

  const connect = () => {
    const socket = new SockJS(brokerUrl)
    stompClient = new Client({
      webSocketFactory: () => socket,
      reconnectDelay: 5000,
      onConnect: () => {
        console.log('✅ STOMP 연결 성공!')
        connected.value = true
        onConnect?.()
        stompClient.subscribe(
          `/topic/${roomId}`,
          frame => {
            console.log('📩 구독 메시지 도착:', frame)
            onMessage(JSON.parse(frame.body))
          }
        )
      },
      onStompError: err => {
        console.error('❌ STOMP 에러:', err)
      },
      onWebSocketClose: () => {
        console.log('⚠️ WebSocket 연결 종료')
        connected.value = false
        onDisconnect?.()
      }
    })
    stompClient.activate()
  }

  const sendMessage = ({ type, sender, content }) => {
    if (!stompClient || !connected.value) {
      console.warn('⚠️ STOMP가 연결되어 있지 않습니다.')
      return
    }
    const payload = { roomId, type, sender, content, timestamp: Date.now() }
    stompClient.publish({
      destination: `/app/chat.send/${roomId}`,
      body: JSON.stringify(payload)
    })
  }

  // 컴포넌트 생명주기 훅
  onMounted(connect)
  onUnmounted(() => {
    if (stompClient) {
      stompClient.deactivate()
    }
  })

  return { connected, connect, sendMessage }
}
