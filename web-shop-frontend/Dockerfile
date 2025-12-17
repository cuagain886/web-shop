# ===== Build stage =====
FROM node:22-alpine AS build

WORKDIR /app

# 复制依赖清单（尽量利用缓存）
COPY package*.json ./

# 本地构建：不限制 Node build 内存
# 更推荐 npm ci（有 lock 文件更稳定），没有 lock 则退回 npm install
RUN if [ -f package-lock.json ]; then npm ci --no-audit --no-fund; else npm install --no-audit --no-fund; fi

# 复制源代码
COPY . .

# 构建
RUN npm run build


# ===== Runtime stage =====
FROM nginx:alpine

COPY --from=build /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]