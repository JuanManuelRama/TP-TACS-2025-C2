# Stage 1: Build React App
FROM node:20-alpine AS builder
WORKDIR /app

# Copy package files and install dependencies
COPY package.json pnpm-lock.yaml ./
RUN npm install -g pnpm
RUN pnpm install

# Copy the rest of the app and build
COPY . .
RUN pnpm run build

# Stage 2: Serve with "serve" from node_modules
FROM node:20-alpine
WORKDIR /app

# Copy only the build output and node_modules
COPY --from=builder /app/dist ./dist
COPY --from=builder /app/node_modules ./node_modules
COPY package.json ./package.json

# Expose port
EXPOSE 80

# Run "serve" from local node_modules
CMD ["npx", "serve", "-s", "dist", "-l", "80"]