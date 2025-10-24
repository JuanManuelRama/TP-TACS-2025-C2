# Stage 1: Build React app
FROM node:20-alpine AS builder
WORKDIR /app

# Copy package files and install dependencies
COPY package.json pnpm-lock.yaml ./
RUN npm install -g pnpm
RUN pnpm install

# Copy the rest of the app and build
COPY . .
RUN pnpm run build

# ---------- Etapa 2: Servidor Nginx ----------
FROM nginx:alpine

# Remover configuración por defecto y agregar la tuya
RUN rm /etc/nginx/conf.d/default.conf
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Copiar archivos estáticos desde la etapa anterior
COPY --from=builder /app/dist /usr/share/nginx/html

# Exponer el puerto donde Nginx sirve el sitio
EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]