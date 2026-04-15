package com.hotel.catering.modules.home.controller;

import com.hotel.catering.common.api.ApiResponse;
import com.hotel.catering.modules.system.service.BusinessConfigService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotel.catering.modules.dish.entity.Dish;
import com.hotel.catering.modules.dish.mapper.DishMapper;
import com.hotel.catering.modules.dish.vo.DishVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class HomeController {

    private final BusinessConfigService businessConfigService;
    private final ObjectMapper objectMapper;
    private final DishMapper dishMapper;

    @GetMapping("/health")
    public ApiResponse<String> health() {
        return ApiResponse.success("ok");
    }

    @GetMapping("/home/index")
    public ApiResponse<Map<String, Object>> index() {
        Map<String, String> configs = businessConfigService.getPublicConfigs();
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("projectName", "酒店二楼餐饮服务小程序");
        result.put("businessScopes", List.of("点餐", "客房送餐", "包间预约", "宴席预约"));
        result.put("contactPhone", configs.getOrDefault("CONTACT_PHONE", ""));
        result.put("deliveryFee", configs.getOrDefault("DELIVERY_FEE", "0"));
        result.put("minOrderAmount", configs.getOrDefault("MIN_ORDER_AMOUNT", "0"));
        result.put("breakfastHours", configs.getOrDefault("BREAKFAST_HOURS", ""));
        result.put("lunchHours", configs.getOrDefault("LUNCH_HOURS", ""));
        result.put("dinnerHours", configs.getOrDefault("DINNER_HOURS", ""));
        result.put("homeNotice", configs.getOrDefault("HOME_NOTICE", ""));
        result.put("roomDeliveryNotice", configs.getOrDefault("ROOM_DELIVERY_NOTICE", ""));
        result.put("homeBanners", parseBanners(configs.get("HOME_BANNERS")));
        result.put("serviceEntries", parseServiceEntries(configs.get("HOME_SERVICE_ENTRIES")));
        result.put("topicCards", parseTopicCards(configs.get("HOME_TOPIC_CARDS")));
        result.put("featuredDishes", loadFeaturedDishes(configs.get("HOME_FEATURED_DISH_IDS")));
        result.put("sectionSettings", parseSectionSettings(configs.get("HOME_SECTION_SETTINGS")));
        return ApiResponse.success(result);
    }

    private List<Map<String, String>> parseBanners(String rawValue) {
        return parseJsonItems(rawValue, defaultBanners());
    }

    private List<Map<String, String>> parseServiceEntries(String rawValue) {
        return parseJsonItems(rawValue, defaultServiceEntries());
    }

    private List<Map<String, String>> parseTopicCards(String rawValue) {
        return parseJsonItems(rawValue, defaultTopicCards());
    }

    private List<Map<String, Object>> parseSectionSettings(String rawValue) {
        if (rawValue == null || rawValue.isBlank()) {
            return defaultSectionSettings();
        }
        try {
            return objectMapper.readValue(rawValue, new TypeReference<List<Map<String, Object>>>() {
            });
        } catch (Exception ignored) {
            return defaultSectionSettings();
        }
    }

    private List<Map<String, String>> parseJsonItems(String rawValue, List<Map<String, String>> fallback) {
        if (rawValue == null || rawValue.isBlank()) {
            return fallback;
        }
        try {
            return objectMapper.readValue(rawValue, new TypeReference<List<Map<String, String>>>() {
            });
        } catch (Exception ignored) {
            return fallback;
        }
    }

    private List<DishVO> loadFeaturedDishes(String rawValue) {
        List<Long> featuredIds = parseFeaturedDishIds(rawValue);
        if (!featuredIds.isEmpty()) {
            List<Dish> dishes = dishMapper.selectList(new LambdaQueryWrapper<Dish>()
                .in(Dish::getId, featuredIds)
                .eq(Dish::getStatus, 1));
            Map<Long, Dish> dishMap = new HashMap<>();
            dishes.forEach(item -> dishMap.put(item.getId(), item));
            List<DishVO> ordered = new ArrayList<>();
            for (Long id : featuredIds) {
                Dish dish = dishMap.get(id);
                if (dish != null) {
                    ordered.add(toDishVO(dish));
                }
            }
            if (!ordered.isEmpty()) {
                return ordered;
            }
        }
        return dishMapper.selectList(new LambdaQueryWrapper<Dish>()
                .eq(Dish::getStatus, 1)
                .eq(Dish::getIsRecommend, 1)
                .orderByAsc(Dish::getSort, Dish::getId)
                .last("limit 6"))
            .stream()
            .map(this::toDishVO)
            .toList();
    }

    private List<Long> parseFeaturedDishIds(String rawValue) {
        if (rawValue == null || rawValue.isBlank()) {
            return List.of();
        }
        try {
            return objectMapper.readValue(rawValue, new TypeReference<List<Long>>() {
            });
        } catch (Exception ignored) {
            return List.of();
        }
    }

    private DishVO toDishVO(Dish dish) {
        return new DishVO(
            dish.getId(),
            dish.getCategoryId(),
            dish.getName(),
            dish.getSubtitle(),
            dish.getDescription(),
            dish.getCoverImage(),
            dish.getBasePrice(),
            dish.getIsRecommend(),
            dish.getSupportsRoomDelivery()
        );
    }

    private List<Map<String, String>> defaultBanners() {
        return List.of(
            Map.of(
                "title", "客房扫码点餐",
                "subtitle", "扫码识别房间后即可享受二楼送餐服务",
                "linkType", "ROOM",
                "linkValue", "",
                "tone", "amber"
            ),
            Map.of(
                "title", "包间提前预约",
                "subtitle", "十个包间可选，早餐中餐晚餐均可预约",
                "linkType", "PRIVATE_ROOM",
                "linkValue", "",
                "tone", "tea"
            ),
            Map.of(
                "title", "婚宴寿宴预约",
                "subtitle", "线上留资，营业时间内优先电话跟进",
                "linkType", "BANQUET",
                "linkValue", "",
                "tone", "copper"
            )
        );
    }

    private List<Map<String, String>> defaultServiceEntries() {
        return List.of(
            Map.of(
                "title", "在线点餐",
                "subtitle", "浏览菜品，加入购物车，快速下单",
                "linkType", "MENU",
                "linkValue", "",
                "tone", "amber"
            ),
            Map.of(
                "title", "购物车",
                "subtitle", "查看已选菜品，准备提交订单",
                "linkType", "CART",
                "linkValue", "",
                "tone", "tea"
            ),
            Map.of(
                "title", "客房扫码点餐",
                "subtitle", "先识别房间，再进入送餐场景下单",
                "linkType", "ROOM",
                "linkValue", "",
                "tone", "copper"
            ),
            Map.of(
                "title", "包间预约",
                "subtitle", "选择日期、时段和包间，提前预约到店",
                "linkType", "PRIVATE_ROOM",
                "linkValue", "",
                "tone", "amber"
            ),
            Map.of(
                "title", "宴席预约",
                "subtitle", "婚宴、寿宴、商务宴先线上留资，后续人工跟进",
                "linkType", "BANQUET",
                "linkValue", "",
                "tone", "tea"
            ),
            Map.of(
                "title", "我的预约",
                "subtitle", "查看包间预约和宴席预约记录",
                "linkType", "RESERVATION",
                "linkValue", "",
                "tone", "copper"
            ),
            Map.of(
                "title", "我的服务",
                "subtitle", "查看订单、预约和客房点餐状态",
                "linkType", "MINE",
                "linkValue", "",
                "tone", "amber"
            )
        );
    }

    private List<Map<String, String>> defaultTopicCards() {
        return List.of(
            Map.of(
                "eyebrow", "ROOM DINING",
                "title", "客房送餐专场",
                "subtitle", "扫码识别房间后即可下单，配送费与起送金额按后台配置自动展示",
                "linkType", "ROOM",
                "linkValue", "",
                "tone", "amber"
            ),
            Map.of(
                "eyebrow", "PRIVATE DINING",
                "title", "包间预订推荐",
                "subtitle", "十个包间支持早餐、中餐、晚餐预约，可先预点菜再到店",
                "linkType", "PRIVATE_ROOM",
                "linkValue", "",
                "tone", "tea"
            ),
            Map.of(
                "eyebrow", "BANQUET",
                "title", "婚宴寿宴咨询",
                "subtitle", "线上留资，营业时间内优先人工电话跟进，适合婚礼和大型宴席",
                "linkType", "BANQUET",
                "linkValue", "",
                "tone", "copper"
            )
        );
    }

    private List<Map<String, Object>> defaultSectionSettings() {
        return List.of(
            Map.of("key", "businessScopes", "title", "服务范围", "subtitle", "当前小程序覆盖的餐饮与预约服务", "enabled", true),
            Map.of("key", "homeBanners", "title", "本周主推", "subtitle", "后台可维护的首页轮播运营位", "enabled", true),
            Map.of("key", "serviceEntries", "title", "推荐入口", "subtitle", "首页常用服务快捷入口", "enabled", true),
            Map.of("key", "topicCards", "title", "活动专题", "subtitle", "适合运营排活动和主推场景", "enabled", true),
            Map.of("key", "featuredDishes", "title", "今日推荐", "subtitle", "当前首页重点推荐菜品", "enabled", true)
        );
    }
}
