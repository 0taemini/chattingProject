// src/hooks/useChat.js
import { ref, onUnmounted } from 'vue'
import SockJS from 'sockjs-client'
import { Client } from '@stomp/stompjs'

export function useChat({ url = 'http://localhost:8080/ws-chat', topic = '/topic/chat', destination = '/app/chat.send' }) {
  const messages = ref([])        // 수신된 메시지 목록
  let stompClient = null

  // 1) 연결 및 구독  
  const connect = () => {
    // SockJS 전송 계층 위에 STOMP 프로토콜 구현
    const socket = new SockJS(url)
    stompClient = new Client({
      webSocketFactory: () => socket,
      onConnect: () => {
        console.log('✅ STOMP 연결 성공')
        // 토픽 구독
        stompClient.subscribe(topic, msg => {
          const body = JSON.parse(msg.body)
          messages.value.push(body)
        })
      },
      onStompError: frame => {
        console.error('❌ STOMP 에러:', frame)
      }
    })
    stompClient.activate()    
  }

  // 2) 메시지 발행  
  const sendMessage = (payload) => {
    if (stompClient && stompClient.connected) {
      stompClient.publish({
        destination,
        body: JSON.stringify(payload)
      })
    } else {
      console.warn('⚠️ STOMP가 연결되어 있지 않습니다.')
    }
  }

  // 3) 정리 (컴포넌트 언마운트 시)
  onUnmounted(() => {
    if (stompClient) stompClient.deactivate()
  })

  return { messages, connect, sendMessage }
}
