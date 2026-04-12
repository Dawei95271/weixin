# 技术设计文档

## 1. 技术选型

### 1.1 后端

- Java 17
- Spring Boot 3
- Spring Web
- Spring Validation
- Spring Security
- MyBatis-Plus
- MySQL 8
- Redis
- Lombok
- SpringDoc OpenAPI

### 1.2 小程序

- 微信小程序原生
- TypeScript

### 1.3 商家后台

- Vue 3
- Vite
- TypeScript
- Element Plus
- Pinia

## 2. 系统拆分

### 2.1 微信小程序

负责：

- 菜品浏览
- 下单支付
- 客房扫码点餐
- 包间预约
- 宴席预约

### 2.2 Spring Boot 后端

负责：

- 用户登录
- 菜品与分类
- 订单与支付
- 客房二维码解析
- 包间预约
- 宴席预约
- 后台权限与日志

### 2.3 商家后台

负责：

- 菜品管理
- 订单处理
- 配送处理
- 包间管理
- 宴席预约跟进

## 3. 后端模块划分

建议以后端单体应用起步，按业务模块拆包：

- `common`：公共响应体、异常、常量
- `config`：配置类
- `security`：鉴权与登录
- `modules.user`
- `modules.dish`
- `modules.order`
- `modules.room`
- `modules.private_room`
- `modules.banquet`
- `modules.payment`
- `modules.admin`
- `modules.system`

## 4. 核心数据模型

### 4.1 商品域

- 菜品分类 `dish_category`
- 菜品 `dish`
- 菜品规格 `dish_sku`

### 4.2 订单域

- 餐饮订单 `food_order`
- 订单明细 `food_order_item`
- 支付记录 `payment_record`
- 退款记录 `refund_record`
- 配送记录 `delivery_record`

### 4.3 房间域

- 酒店房间 `hotel_room`
- 房间二维码 `room_qrcode`

### 4.4 包间域

- 包间 `private_room`
- 包间时段 `private_room_timeslot`
- 包间预约 `private_room_reservation`
- 包间预点菜 `private_room_reservation_item`

### 4.5 宴席域

- 宴席预约 `banquet_reservation`
- 宴席跟进记录 `banquet_follow_record`

## 5. 关键技术规则

### 5.1 并发控制

- 包间预约使用数据库唯一索引控制重复预约
- 支付回调用幂等校验避免重复处理

### 5.2 数据快照

- 订单明细保存菜名、规格、价格快照
- 包间预点菜也保存菜品快照

### 5.3 安全与权限

- 小程序端采用微信登录
- 后台采用用户名密码登录
- 后台鉴权采用 JWT
- 后台角色与权限分离

### 5.4 定时任务

- 超时未支付订单自动关闭
- 超时未支付包间预约自动取消

## 6. API 设计原则

- 用户端统一前缀 `/api`
- 后台统一前缀 `/admin`
- 响应格式统一
- 所有写操作记录操作日志

统一响应建议：

```json
{
  "code": 0,
  "message": "success",
  "data": {}
}
```

## 7. 部署建议

- 应用部署方式：Docker
- Web 服务：Nginx
- 数据库：MySQL
- 缓存：Redis
- 文件存储：腾讯云 COS
- 运行环境：Linux 云服务器

## 8. 开发顺序

### 第一阶段

- 后端框架搭建
- 基础文档
- 权限与登录
- 商品与分类模块

### 第二阶段

- 购物车与订单
- 微信支付
- 订单管理

### 第三阶段

- 客房扫码点餐
- 配送状态流转

### 第四阶段

- 包间预约
- 定金支付
- 宴席预约
