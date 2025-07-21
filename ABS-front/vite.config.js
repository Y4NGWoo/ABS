import { defineConfig } from 'vite';
import vue from '@vitejs/plugin-vue';
import path from 'path';
import vuetify from 'vite-plugin-vuetify';

export default defineConfig({
  plugins: [vue(),
            vuetify({ autoImport: true }),   // Vuetify 컴포넌트/디렉티브 자동 임포트
        ],
  
  // 개발 서버 설정
  server: {
    port: 3000,       // 기본 5173 → 3000번 포트로 변경
    open: true,       // 실행 시 자동으로 브라우저 열기
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, '')
      }
    }
  },

  // 모듈 경로 별칭 설정
  resolve: {
    alias: {
      '@': path.resolve(__dirname, 'src'),
      '~components': path.resolve(__dirname, 'src/components')
    }
  },

  // 빌드 최적화 옵션
  build: {
    outDir: 'dist',
    sourcemap: false,
    rollupOptions: {
      output: {
        manualChunks: {
          vue: ['vue', 'vue-router', 'pinia']
        }
      }
    }
  }
});
