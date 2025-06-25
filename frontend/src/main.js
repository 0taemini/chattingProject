// src/main.js
import { createApp } from 'vue'
import App from './App.vue'
import { createRouter, createWebHistory } from 'vue-router'

// Vuetify 관련 import
import 'vuetify/styles'                // Vuetify 전체 스타일
import { createVuetify } from 'vuetify' // Vuetify 플러그인 생성기

// 페이지 컴포넌트
import Home from './pages/index.vue'
import ChatPage from './pages/chat/[id].vue'

// 라우터 설정
const routes = [
  { path: '/', component: Home },
  { path: '/chat/:id', component: ChatPage },
]
const router = createRouter({
  history: createWebHistory(),
  routes,
})

// Vuetify 인스턴스 생성 (필요한 옵션이 있다면 여기에 추가)
const vuetify = createVuetify({
  // theme, components, directives 등 옵션 가능
})

const app = createApp(App)
app.use(router)
app.use(vuetify)   // ← 여기에 Vuetify 플러그인 등록!
app.mount('#app')
