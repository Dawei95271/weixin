CREATE DATABASE IF NOT EXISTS hotel_catering DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE hotel_catering;

SET NAMES utf8mb4;

CREATE TABLE IF NOT EXISTS user (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
  open_id VARCHAR(64) NOT NULL DEFAULT '' COMMENT '微信openId',
  union_id VARCHAR(64) NOT NULL DEFAULT '' COMMENT '微信unionId',
  nickname VARCHAR(100) NOT NULL DEFAULT '' COMMENT '昵称',
  avatar_url VARCHAR(255) NOT NULL DEFAULT '' COMMENT '头像',
  phone VARCHAR(20) NOT NULL DEFAULT '' COMMENT '手机号',
  status TINYINT NOT NULL DEFAULT 1 COMMENT '状态:1正常 0禁用',
  last_login_at DATETIME NULL COMMENT '最后登录时间',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_open_id (open_id),
  KEY idx_phone (phone)
) COMMENT='小程序用户表';

CREATE TABLE IF NOT EXISTS admin_user (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(50) NOT NULL COMMENT '登录账号',
  password VARCHAR(255) NOT NULL COMMENT '密码',
  real_name VARCHAR(50) NOT NULL DEFAULT '' COMMENT '姓名',
  phone VARCHAR(20) NOT NULL DEFAULT '' COMMENT '手机号',
  status TINYINT NOT NULL DEFAULT 1 COMMENT '状态:1正常 0停用',
  last_login_at DATETIME NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_username (username)
) COMMENT='后台用户表';

CREATE TABLE IF NOT EXISTS dish_category (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(50) NOT NULL COMMENT '分类名称',
  sort INT NOT NULL DEFAULT 0 COMMENT '排序值',
  status TINYINT NOT NULL DEFAULT 1 COMMENT '1启用 0停用',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) COMMENT='菜品分类表';

CREATE TABLE IF NOT EXISTS dish (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  category_id BIGINT NOT NULL COMMENT '分类ID',
  name VARCHAR(100) NOT NULL COMMENT '菜品名称',
  subtitle VARCHAR(200) NOT NULL DEFAULT '' COMMENT '副标题',
  cover_image VARCHAR(255) NOT NULL DEFAULT '' COMMENT '封面图',
  description VARCHAR(1000) NOT NULL DEFAULT '' COMMENT '描述',
  base_price DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '基础价格',
  status TINYINT NOT NULL DEFAULT 1 COMMENT '1上架 0下架',
  is_recommend TINYINT NOT NULL DEFAULT 0 COMMENT '是否推荐',
  supports_room_delivery TINYINT NOT NULL DEFAULT 1 COMMENT '是否支持客房配送',
  sort INT NOT NULL DEFAULT 0 COMMENT '排序值',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  KEY idx_category_id (category_id),
  KEY idx_status_sort (status, sort)
) COMMENT='菜品表';

CREATE TABLE IF NOT EXISTS dish_sku (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  dish_id BIGINT NOT NULL COMMENT '菜品ID',
  sku_name VARCHAR(100) NOT NULL COMMENT '规格名',
  price DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '销售价',
  stock INT NOT NULL DEFAULT 9999 COMMENT '库存',
  status TINYINT NOT NULL DEFAULT 1 COMMENT '1启用 0停用',
  sort INT NOT NULL DEFAULT 0,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  KEY idx_dish_id (dish_id)
) COMMENT='菜品规格表';

CREATE TABLE IF NOT EXISTS hotel_room (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  building_no VARCHAR(20) NOT NULL DEFAULT '' COMMENT '楼栋',
  floor_no INT NOT NULL COMMENT '楼层',
  room_no VARCHAR(20) NOT NULL COMMENT '房号',
  status TINYINT NOT NULL DEFAULT 1 COMMENT '1启用 0停用',
  remark VARCHAR(255) NOT NULL DEFAULT '' COMMENT '备注',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_room_no (room_no)
) COMMENT='酒店房间表';

CREATE TABLE IF NOT EXISTS room_qrcode (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  room_id BIGINT NOT NULL COMMENT '房间ID',
  qr_code_key VARCHAR(100) NOT NULL COMMENT '二维码唯一标识',
  status TINYINT NOT NULL DEFAULT 1 COMMENT '1有效 0失效',
  last_scan_at DATETIME NULL COMMENT '最后扫码时间',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_qr_code_key (qr_code_key),
  KEY idx_room_id (room_id)
) COMMENT='房间二维码表';

CREATE TABLE IF NOT EXISTS private_room (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(50) NOT NULL COMMENT '包间名称',
  cover_image VARCHAR(255) NOT NULL DEFAULT '' COMMENT '封面图',
  capacity_min INT NOT NULL DEFAULT 1 COMMENT '最少容纳人数',
  capacity_max INT NOT NULL DEFAULT 1 COMMENT '最多容纳人数',
  deposit_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '默认定金',
  status TINYINT NOT NULL DEFAULT 1 COMMENT '1启用 0停用',
  description VARCHAR(1000) NOT NULL DEFAULT '' COMMENT '描述',
  sort INT NOT NULL DEFAULT 0 COMMENT '排序值',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) COMMENT='包间表';

CREATE TABLE IF NOT EXISTS private_room_timeslot (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  timeslot_code VARCHAR(30) NOT NULL COMMENT 'BREAKFAST/LUNCH/DINNER',
  timeslot_name VARCHAR(30) NOT NULL COMMENT '早餐/中餐/晚餐',
  start_time TIME NOT NULL COMMENT '开始时间',
  end_time TIME NOT NULL COMMENT '结束时间',
  sort INT NOT NULL DEFAULT 0,
  status TINYINT NOT NULL DEFAULT 1,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_timeslot_code (timeslot_code)
) COMMENT='包间时段表';

CREATE TABLE IF NOT EXISTS private_room_reservation (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  reservation_no VARCHAR(50) NOT NULL COMMENT '预约单号',
  user_id BIGINT NOT NULL COMMENT '用户ID',
  private_room_id BIGINT NOT NULL COMMENT '包间ID',
  reserve_date DATE NOT NULL COMMENT '预约日期',
  timeslot_code VARCHAR(30) NOT NULL COMMENT '时段编码',
  guest_count INT NOT NULL DEFAULT 1 COMMENT '人数',
  contact_name VARCHAR(50) NOT NULL DEFAULT '' COMMENT '联系人',
  contact_phone VARCHAR(20) NOT NULL DEFAULT '' COMMENT '联系电话',
  remark VARCHAR(500) NOT NULL DEFAULT '' COMMENT '备注',
  deposit_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '定金金额',
  pay_status VARCHAR(20) NOT NULL DEFAULT 'UNPAID' COMMENT 'UNPAID/PAID/REFUNDED',
  reservation_status VARCHAR(20) NOT NULL DEFAULT 'WAIT_PAY' COMMENT 'WAIT_PAY/RESERVED/ARRIVED/COMPLETED/CANCELLED',
  pay_time DATETIME NULL,
  cancel_time DATETIME NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_reservation_no (reservation_no),
  UNIQUE KEY uk_room_date_slot (private_room_id, reserve_date, timeslot_code)
) COMMENT='包间预约表';

CREATE TABLE IF NOT EXISTS private_room_reservation_item (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  reservation_id BIGINT NOT NULL COMMENT '预约ID',
  dish_id BIGINT NOT NULL COMMENT '菜品ID',
  dish_sku_id BIGINT NOT NULL DEFAULT 0 COMMENT '规格ID',
  dish_name VARCHAR(100) NOT NULL COMMENT '菜名快照',
  sku_name VARCHAR(100) NOT NULL DEFAULT '' COMMENT '规格快照',
  unit_price DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '单价',
  quantity INT NOT NULL DEFAULT 1 COMMENT '数量',
  total_price DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '小计',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  KEY idx_reservation_id (reservation_id)
) COMMENT='包间预点菜明细表';

CREATE TABLE IF NOT EXISTS banquet_reservation (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  reservation_no VARCHAR(50) NOT NULL COMMENT '宴席预约单号',
  user_id BIGINT NOT NULL DEFAULT 0 COMMENT '用户ID',
  banquet_type VARCHAR(50) NOT NULL DEFAULT '' COMMENT '婚宴/寿宴/商务宴等',
  reserve_date DATE NOT NULL COMMENT '意向日期',
  guest_count INT NOT NULL DEFAULT 0 COMMENT '预计人数',
  budget_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '预算金额',
  contact_name VARCHAR(50) NOT NULL DEFAULT '' COMMENT '联系人',
  contact_phone VARCHAR(20) NOT NULL DEFAULT '' COMMENT '联系电话',
  requirement_desc VARCHAR(1000) NOT NULL DEFAULT '' COMMENT '需求描述',
  status VARCHAR(20) NOT NULL DEFAULT 'WAIT_FOLLOW' COMMENT 'WAIT_FOLLOW/CONTACTED/CONFIRMED/CANCELLED',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_reservation_no (reservation_no)
) COMMENT='宴席预约表';

CREATE TABLE IF NOT EXISTS banquet_follow_record (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  banquet_reservation_id BIGINT NOT NULL COMMENT '宴席预约ID',
  follow_content VARCHAR(1000) NOT NULL DEFAULT '' COMMENT '跟进内容',
  follow_user_id BIGINT NOT NULL DEFAULT 0 COMMENT '跟进人',
  next_follow_time DATETIME NULL COMMENT '下次跟进时间',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  KEY idx_banquet_reservation_id (banquet_reservation_id)
) COMMENT='宴席跟进记录表';

CREATE TABLE IF NOT EXISTS food_order (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  order_no VARCHAR(50) NOT NULL COMMENT '订单号',
  user_id BIGINT NOT NULL COMMENT '用户ID',
  order_scene VARCHAR(30) NOT NULL COMMENT 'NORMAL/ROOM_DELIVERY/PRIVATE_ROOM_PREORDER',
  room_id BIGINT NOT NULL DEFAULT 0 COMMENT '房间ID',
  room_no VARCHAR(20) NOT NULL DEFAULT '' COMMENT '房号',
  private_room_reservation_id BIGINT NOT NULL DEFAULT 0 COMMENT '包间预约ID',
  contact_name VARCHAR(50) NOT NULL DEFAULT '' COMMENT '联系人',
  contact_phone VARCHAR(20) NOT NULL DEFAULT '' COMMENT '联系电话',
  remark VARCHAR(500) NOT NULL DEFAULT '' COMMENT '备注',
  item_total_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '菜品总金额',
  delivery_fee DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '配送费',
  payable_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '应付金额',
  paid_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '实付金额',
  pay_status VARCHAR(20) NOT NULL DEFAULT 'UNPAID' COMMENT 'UNPAID/PAID/REFUNDED',
  order_status VARCHAR(30) NOT NULL DEFAULT 'WAIT_PAY' COMMENT 'WAIT_PAY/WAIT_ACCEPT/COOKING/DELIVERING/COMPLETED/CANCELLED',
  pay_time DATETIME NULL,
  cancel_time DATETIME NULL,
  finish_time DATETIME NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_order_no (order_no),
  KEY idx_user_id (user_id),
  KEY idx_order_status (order_status),
  KEY idx_room_id (room_id)
) COMMENT='餐饮订单表';

CREATE TABLE IF NOT EXISTS food_order_item (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  order_id BIGINT NOT NULL COMMENT '订单ID',
  dish_id BIGINT NOT NULL COMMENT '菜品ID',
  dish_sku_id BIGINT NOT NULL DEFAULT 0 COMMENT '规格ID',
  dish_name VARCHAR(100) NOT NULL COMMENT '下单时菜名快照',
  sku_name VARCHAR(100) NOT NULL DEFAULT '' COMMENT '规格名快照',
  unit_price DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '单价',
  quantity INT NOT NULL DEFAULT 1 COMMENT '数量',
  total_price DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '小计',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  KEY idx_order_id (order_id)
) COMMENT='订单明细表';

CREATE TABLE IF NOT EXISTS payment_record (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  business_type VARCHAR(30) NOT NULL COMMENT 'FOOD_ORDER/PRIVATE_ROOM_DEPOSIT',
  business_id BIGINT NOT NULL COMMENT '业务ID',
  order_no VARCHAR(50) NOT NULL COMMENT '业务单号',
  pay_no VARCHAR(50) NOT NULL DEFAULT '' COMMENT '支付单号',
  pay_channel VARCHAR(20) NOT NULL DEFAULT 'WECHAT' COMMENT '支付渠道',
  transaction_id VARCHAR(100) NOT NULL DEFAULT '' COMMENT '微信支付流水号',
  pay_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '支付金额',
  pay_status VARCHAR(20) NOT NULL DEFAULT 'WAIT_PAY' COMMENT 'WAIT_PAY/SUCCESS/FAIL/REFUND',
  notify_status TINYINT NOT NULL DEFAULT 0 COMMENT '0未回调 1已回调',
  pay_time DATETIME NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  KEY idx_business (business_type, business_id),
  KEY idx_order_no (order_no),
  KEY idx_transaction_id (transaction_id)
) COMMENT='支付记录表';

CREATE TABLE IF NOT EXISTS delivery_record (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  order_id BIGINT NOT NULL COMMENT '订单ID',
  delivery_type VARCHAR(20) NOT NULL DEFAULT 'ROOM' COMMENT 'ROOM/SELF_PICKUP',
  room_id BIGINT NOT NULL DEFAULT 0 COMMENT '房间ID',
  room_no VARCHAR(20) NOT NULL DEFAULT '' COMMENT '房号',
  delivery_status VARCHAR(20) NOT NULL DEFAULT 'WAIT_DELIVERY' COMMENT 'WAIT_DELIVERY/DELIVERING/DELIVERED',
  delivery_user_name VARCHAR(50) NOT NULL DEFAULT '' COMMENT '配送人',
  delivery_user_phone VARCHAR(20) NOT NULL DEFAULT '' COMMENT '配送人电话',
  delivered_at DATETIME NULL COMMENT '送达时间',
  remark VARCHAR(255) NOT NULL DEFAULT '',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  KEY idx_order_id (order_id)
) COMMENT='配送记录表';

CREATE TABLE IF NOT EXISTS business_config (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  config_key VARCHAR(100) NOT NULL COMMENT '配置键',
  config_value VARCHAR(2000) NOT NULL DEFAULT '' COMMENT '配置值',
  config_name VARCHAR(100) NOT NULL DEFAULT '' COMMENT '配置名称',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_config_key (config_key)
) COMMENT='营业配置表';

INSERT INTO private_room_timeslot (timeslot_code, timeslot_name, start_time, end_time, sort, status)
VALUES
  ('BREAKFAST', '早餐', '07:00:00', '09:30:00', 1, 1),
  ('LUNCH', '中餐', '11:00:00', '14:00:00', 2, 1),
  ('DINNER', '晚餐', '17:00:00', '21:00:00', 3, 1)
ON DUPLICATE KEY UPDATE
  timeslot_name = VALUES(timeslot_name),
  start_time = VALUES(start_time),
  end_time = VALUES(end_time),
  sort = VALUES(sort),
  status = VALUES(status);

INSERT INTO dish_category (id, name, sort, status)
VALUES
  (1, '热菜', 1, 1),
  (2, '凉菜', 2, 1),
  (3, '汤品', 3, 1)
ON DUPLICATE KEY UPDATE
  name = VALUES(name),
  sort = VALUES(sort),
  status = VALUES(status);

INSERT INTO dish (id, category_id, name, subtitle, cover_image, description, base_price, status, is_recommend, supports_room_delivery, sort)
VALUES
  (101, 1, '招牌红烧肉', '热销推荐', '', '经典家常菜', 58.00, 1, 1, 1, 1),
  (102, 1, '清蒸鲈鱼', '适合客房配送', '', '清淡鲜香', 88.00, 1, 1, 1, 2),
  (201, 2, '凉拌木耳', '爽口凉菜', '', '开胃爽口', 28.00, 1, 0, 1, 1)
ON DUPLICATE KEY UPDATE
  name = VALUES(name),
  subtitle = VALUES(subtitle),
  base_price = VALUES(base_price),
  status = VALUES(status),
  is_recommend = VALUES(is_recommend),
  supports_room_delivery = VALUES(supports_room_delivery),
  sort = VALUES(sort);

INSERT INTO private_room (id, name, cover_image, capacity_min, capacity_max, deposit_amount, status, description, sort)
VALUES
  (1, '牡丹厅', '', 6, 12, 500.00, 1, '适合家庭聚餐', 1),
  (2, '海棠厅', '', 8, 16, 800.00, 1, '适合商务宴请', 2),
  (3, '金桂厅', '', 10, 20, 1000.00, 1, '适合生日宴请', 3)
ON DUPLICATE KEY UPDATE
  name = VALUES(name),
  capacity_min = VALUES(capacity_min),
  capacity_max = VALUES(capacity_max),
  deposit_amount = VALUES(deposit_amount),
  status = VALUES(status),
  description = VALUES(description),
  sort = VALUES(sort);

INSERT INTO hotel_room (id, building_no, floor_no, room_no, status, remark)
VALUES
  (1, '', 8, '801', 1, ''),
  (2, '', 8, '802', 1, ''),
  (3, '', 12, '1201', 1, '')
ON DUPLICATE KEY UPDATE
  floor_no = VALUES(floor_no),
  room_no = VALUES(room_no),
  status = VALUES(status),
  remark = VALUES(remark);

INSERT INTO room_qrcode (id, room_id, qr_code_key, status)
VALUES
  (1, 1, 'ROOM-801', 1),
  (2, 2, 'ROOM-802', 1),
  (3, 3, 'ROOM-1201', 1)
ON DUPLICATE KEY UPDATE
  room_id = VALUES(room_id),
  qr_code_key = VALUES(qr_code_key),
  status = VALUES(status);

INSERT INTO admin_user (id, username, password, real_name, phone, status)
VALUES
  (1, 'admin', '$2y$10$kXTzBzB4CGvl71uXFNTyaOMCBDmtDsbYG8Uu/QRaPgwSSyc145iNW', '系统管理员', '13800000000', 1)
ON DUPLICATE KEY UPDATE
  username = VALUES(username),
  password = VALUES(password),
  real_name = VALUES(real_name),
  phone = VALUES(phone),
  status = VALUES(status);

INSERT INTO business_config (config_key, config_value, config_name)
VALUES
  ('CONTACT_PHONE', '13800000000', '联系电话'),
  ('DELIVERY_FEE', '6', '客房配送费'),
  ('MIN_ORDER_AMOUNT', '38', '起送金额'),
  ('BREAKFAST_HOURS', '07:00-09:30', '早餐营业时段'),
  ('LUNCH_HOURS', '11:00-14:00', '中餐营业时段'),
  ('DINNER_HOURS', '17:00-21:00', '晚餐营业时段'),
  ('HOME_NOTICE', '欢迎使用酒店二楼餐饮服务，客房扫码即可点餐。', '首页公告'),
  ('ROOM_DELIVERY_NOTICE', '客房送餐默认送至房门，请保持电话畅通。', '客房送餐提示')
ON DUPLICATE KEY UPDATE
  config_value = VALUES(config_value),
  config_name = VALUES(config_name);
