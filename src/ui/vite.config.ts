import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import tailwindcss from '@tailwindcss/vite'
import path from "path";
// https://vite.dev/config/
export default defineConfig({
  plugins: [
      react(),
      tailwindcss()
  ],
    resolve: {
      alias: {
          "@assets": path.resolve(__dirname, "src/assets/"),
          "@components": path.resolve(__dirname, "src/components/"),
          "@contexts": path.resolve(__dirname, "src/contexts/"),
          "@layouts": path.resolve(__dirname, "src/layouts/"),
          "@pages": path.resolve(__dirname, "src/pages/"),
          "@routes": path.resolve(__dirname, "src/routes/"),
          "@services": path.resolve(__dirname, "src/services/"),
          "@utils": path.resolve(__dirname, "src/utils/"),
          "@models": path.resolve(__dirname, "src/models")
      }
    }
})
