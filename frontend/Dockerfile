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

# Stage 2: Serve using Nginx
FROM nginx:alpine
WORKDIR /usr/share/nginx/html

# Remove default nginx website
RUN rm -rf ./*

# Copy build output from previous stage
COPY --from=builder /app/dist ./

# Copy custom Nginx config
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Expose port
EXPOSE 80

# Start Nginx
CMD ["nginx", "-g", "daemon off;"]