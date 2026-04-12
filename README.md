# 酒店二楼餐饮服务小程序

这是一个面向酒店二楼餐饮场景的生产级项目骨架，覆盖以下核心业务：

- 餐饮点单
- 客房扫码送餐
- 包间预约与预点菜
- 宴席预约登记

## 项目结构

- `docs/` 产品与技术文档
- `backend/` Spring Boot 后端
- `miniprogram/` 微信小程序占位目录
- `admin/` 商家后台占位目录

## 当前阶段

当前仓库已完成：

- Git 仓库初始化
- PRD 文档
- 技术设计文档
- 部署文档
- Spring Boot 后端基础骨架
- Docker 部署骨架
- Vue 3 后台基础工程
- 微信小程序基础工程

## 建议启动顺序

1. 先阅读 `docs/PRD.md`
2. 再阅读 `docs/ARCHITECTURE.md`
3. 再阅读 `docs/DEVELOPMENT.md`
4. 如果准备上云，阅读 `docs/DEPLOYMENT.md`
5. 以 `backend/` 为起点继续后端业务开发
6. 再补小程序与后台前端工程

## 快速启动

### 本地开发

要求：

- JDK 17
- MySQL 8
- Redis 6+

### Docker 启动

```bash
cp .env.example .env
docker compose up -d --build
```

### 启动后台前端

```bash
cd admin
npm install
npm run dev
```

当前后台已具备：

- 管理员登录
- 订单管理
- 分类管理
- 菜品管理
- 包间预约管理
- 宴席预约管理

### 打开微信小程序

使用微信开发者工具导入：

```bash
/Users/wei/code/weixin/miniprogram
```

当前小程序已具备：

- 首页展示
- 菜单与加入购物车
- 购物车数量调整
- 普通下单
- 客房送餐场景识别
- 我的订单与订单详情
- 我的预约
- 包间预约提交
- 宴席预约提交
