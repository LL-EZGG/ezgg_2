import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// 로컬 테스트환경에서 사용
// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    proxy: {
      '/api': {
        target: 'http://localhost:8888',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, '')
      }
    }
  }
})
