# ---- Base image ----
FROM node:22-alpine

# ---- Create app directory ----
WORKDIR /app

# ---- Copy only package files first (for caching) ----
COPY package*.json ./

# ---- Install dependencies ----
RUN npm install --production

# ---- Copy the rest of your app ----
COPY . .

# ---- Default command ----
CMD ["npm", "start"]