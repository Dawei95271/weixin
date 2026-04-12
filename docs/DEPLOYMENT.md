# 部署文档

## 1. 部署目标

当前仓库已经具备基础部署骨架，支持使用 Docker Compose 在 Linux 云服务器启动：

- MySQL 8
- Redis 7
- Spring Boot 后端

后续补齐后台前端打包产物后，可以再接入 Nginx 提供静态资源访问。

## 2. 推荐服务器配置

- 系统：Ubuntu 22.04 LTS
- CPU：2 核以上
- 内存：4 GB 以上
- 磁盘：40 GB 以上

## 3. 服务器软件要求

- Docker
- Docker Compose Plugin
- Git
- Nginx

## 4. 初始化步骤

### 4.1 拉取代码

```bash
git clone <your-repo-url> hotel-catering
cd hotel-catering
```

### 4.2 配置环境变量

```bash
cp .env.example .env
```

编辑 `.env`，至少修改：

- `MYSQL_ROOT_PASSWORD`
- `MYSQL_PASSWORD`
- `JWT_SECRET`

注意：

- `JWT_SECRET` 长度必须不少于 32 个字符
- 否则后端启动时会因 JWT 密钥长度不足而报错

### 4.3 启动基础服务

```bash
docker compose up -d --build
```

启动后服务端口：

- MySQL: `3306`
- Redis: `6379`
- Backend: `8080`

## 5. 验证方式

### 5.1 健康检查

```bash
curl http://127.0.0.1:8080/api/health
```

### 5.2 首页接口

```bash
curl http://127.0.0.1:8080/api/home/index
```

### 5.3 Swagger 文档

浏览器访问：

- `http://服务器IP:8080/swagger-ui.html`

## 6. Nginx 反向代理

仓库已提供示例配置：

- `deploy/nginx.conf`

建议部署方式：

- 后端 API 走 `/api`
- 后台静态页面走 `/admin`

## 7. 目录说明

- `docker-compose.yml`：容器编排文件
- `.env.example`：环境变量示例
- `backend/Dockerfile`：后端镜像构建文件
- `deploy/nginx.conf`：Nginx 示例配置
- `deploy/server-init.sh`：服务器初始化目录脚本

## 8. 当前限制

当前仓库还没有完全补齐：

- 小程序正式工程
- Vue 后台正式工程
- 微信支付证书与回调配置
- 生产环境日志、监控、告警

## 9. 下一步建议

建议继续补下面几项：

1. 后台登录与权限
2. 订单后台管理接口
3. 微信支付接入
4. Vue 3 后台初始化
5. 小程序页面正式开发
