package com.kgc.kmall.cartweb.controller;

import com.alibaba.fastjson.JSON;
import com.kgc.kmall.annotations.LoginRequired;
import com.kgc.kmall.bean.OmsCartItem;
import com.kgc.kmall.bean.PmsSkuInfo;
import com.kgc.kmall.service.CartService;
import com.kgc.kmall.service.SkuService;
import com.kgc.kmall.util.CookieUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.*;

/**
 * @author shkstart
 * @create 2021-01-08 14:19
 */
@Controller
public class CartController {
    @Reference
    SkuService skuService;
    @Reference
    CartService cartService;

    private BigDecimal getTotalAmount(List<OmsCartItem> omsCartItems) {
        if (omsCartItems == null || omsCartItems.size() == 0) {
            return new BigDecimal(0);
        }
        BigDecimal total = new BigDecimal(0);
        for (OmsCartItem omsCartItem : omsCartItems) {
            //计算小计
            BigDecimal multiply = omsCartItem.getPrice().multiply(new BigDecimal(omsCartItem.getQuantity()));
            omsCartItem.setTotalPrice(multiply);

            //计算总价
            if (omsCartItem.getIsChecked() != null && omsCartItem.getIsChecked() == 1) {
                total = total.add(omsCartItem.getTotalPrice());
            }
        }
        return total;
    }

    @LoginRequired(value = false)
    @RequestMapping("/addToCart")
    public String addToCart(Long skuId, Integer num, HttpServletResponse response, HttpServletRequest request) {
        //先封装一个购物车的集合
        List<OmsCartItem> omsCartItems = new ArrayList<>();
        //根据skuid查询商品基本信息
        PmsSkuInfo skuInfo = skuService.selectBySkuId(skuId);
        //将商品信息封装到购物车集合中
        OmsCartItem omsCartItem = new OmsCartItem();
        omsCartItem.setCreateDate(new Date());
        omsCartItem.setDeleteStatus(0);
        omsCartItem.setModifyDate(new Date());
        omsCartItem.setPrice(new BigDecimal(skuInfo.getPrice()));
        omsCartItem.setProductAttr("");
        omsCartItem.setProductBrand("");
        omsCartItem.setProductCategoryId(skuInfo.getCatalog3Id());
        omsCartItem.setProductId(skuInfo.getSpuId());
        omsCartItem.setProductName(skuInfo.getSkuName());
        omsCartItem.setProductPic(skuInfo.getSkuDefaultImg());
        omsCartItem.setProductSkuCode("11111111111");
        omsCartItem.setProductSkuId(skuId);
        omsCartItem.setQuantity(num);
        //判断用户是否登录
        String memberId = "";
        if (request.getAttribute("memberId") != null) {
            String mid = request.getAttribute("memberId").toString();
            memberId=mid;
        }
        System.out.println("用户编号"+memberId);
        //未登录状态
        if (StringUtils.isBlank(memberId)) {
            System.out.println("cookie" + "11111111111111111111111");
            //获取cookie里的数据
            String cartListCookie = CookieUtil.getCookieValue(request, "cartListCookie", true);
            //cookie没有数据
            if (StringUtils.isBlank(cartListCookie)) {
                omsCartItems.add(omsCartItem);
            } else {
                //有数据
                omsCartItems = JSON.parseArray(cartListCookie, OmsCartItem.class);
                //判断添加的购物车数据在cookie是否存在
                boolean exist = if_cart_exist(omsCartItems, omsCartItem);
                if (exist) {
                    //数量加一
                    for (OmsCartItem cartItem : omsCartItems) {
                        if (cartItem.getProductSkuId().equals(omsCartItem.getProductSkuId())) {
                            cartItem.setQuantity(cartItem.getQuantity() + omsCartItem.getQuantity());
                            break;
                        }
                    }
                }
                //不存在
                else {
                    // 之前没有添加，新增当前的购物车
                    omsCartItems.add(omsCartItem);
                }
            }
            // 更新cookie
            CookieUtil.setCookie(request, response, "cartListCookie", JSON.toJSONString(omsCartItems), 60 * 60 * 72, true);
        } else {
            System.out.println("DB");
            //思路二：根据用户id和skuid查询，如果不存在则添加，如果存在则修改
            // 用户已经登录 登录状态
            // 从db中查出购物车数
            OmsCartItem omsCartItemFromDb = cartService.ifCartExistByUser(memberId, skuId);
            if (omsCartItemFromDb == null) {
                // 该用户没有添加过当前商品
                omsCartItem.setMemberId(Long.parseLong(memberId));
                omsCartItem.setMemberNickname("test 小明");
                cartService.addCart(omsCartItem);
            } else {
                // 该用户添加过当前商品
                Integer quantity = omsCartItemFromDb.getQuantity();
                quantity = quantity + num;
                omsCartItemFromDb.setQuantity(quantity);
                cartService.updateCart(omsCartItemFromDb);
            }
            // 同步缓存
            cartService.flushCartCache(memberId);
        }
        return "redirect:/success.html";
    }

    private boolean if_cart_exist(List<OmsCartItem> omsCartItems, OmsCartItem omsCartItem) {
        boolean b = false;
        for (OmsCartItem cartItem : omsCartItems) {
            Long productSkuId = cartItem.getProductSkuId();
            if (productSkuId.equals(omsCartItem.getProductSkuId())) {
                b = true;
                break;
            }
        }
        return b;
    }

    @LoginRequired(false)
    @RequestMapping("/cartList")
    public String cartList(ModelMap modelMap, HttpServletRequest request) {
        List<OmsCartItem> omsCartItems = new ArrayList<>();
        String memberId = "";

        if (request.getAttribute("memberId") != null) {
            String mid = request.getAttribute("memberId").toString();
            memberId=mid;
        }
        System.out.println("用户编号"+memberId);
        if (StringUtils.isNotBlank(memberId)) {
            // 已经登录查询db
            omsCartItems = cartService.cartList(memberId);
        } else {
            // 没有登录查询cookie
            String cartListCookie = CookieUtil.getCookieValue(request, "cartListCookie", true);
            if (StringUtils.isNotBlank(cartListCookie)) {
                omsCartItems = JSON.parseArray(cartListCookie, OmsCartItem.class);
            }
        }


        for (OmsCartItem omsCartItem : omsCartItems) {
            omsCartItem.setTotalPrice(omsCartItem.getPrice().multiply(new BigDecimal(omsCartItem.getQuantity())));
        }
        modelMap.put("cartList", omsCartItems);

        //总价
        BigDecimal totalAmount = getTotalAmount(omsCartItems);
        System.out.println("总价格" + totalAmount);
        modelMap.put("totalAmount", totalAmount);
        return "cartList";
    }

    @LoginRequired(true)
    @RequestMapping("toTrade")
    public String toTrade() {
        return "toTrade";
    }

    @LoginRequired(false)
    @RequestMapping("/checkCart")
    @ResponseBody
    public Map<String, Object> checkCart(Integer isChecked, Long skuId, HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> map = new HashMap<>();
        String memberId = "";
        if (request.getAttribute("memberId") != null) {
            String mid = request.getAttribute("memberId").toString();
            memberId=mid;
        }
        System.out.println("用户编号"+memberId);
        if (StringUtils.isNotBlank(memberId)) {
            OmsCartItem omsCartItem = new OmsCartItem();
            omsCartItem.setMemberId(Long.parseLong(memberId));
            omsCartItem.setProductSkuId(skuId);
            omsCartItem.setIsChecked(isChecked);
            cartService.checkCart(omsCartItem);
            //计算总价
            List<OmsCartItem> omsCartItems = cartService.cartList(memberId);
            BigDecimal totalAmount = getTotalAmount(omsCartItems);
            map.put("totalAmount", totalAmount);
        } else {
            // 没有登录 查询cookie
            String cartListCookie = CookieUtil.getCookieValue(request, "cartListCookie", true);
            if (StringUtils.isNotBlank(cartListCookie)) {
                List<OmsCartItem> omsCartItems = JSON.parseArray(cartListCookie, OmsCartItem.class);
                for (OmsCartItem omsCartItem : omsCartItems) {
                    if (omsCartItem.getProductSkuId() == skuId) {
                        omsCartItem.setIsChecked(isChecked);
                        break;
                    }
                }
                //保存cookie
                CookieUtil.setCookie(request, response, "cartListCookie", JSON.toJSONString(omsCartItems), 60 * 60 * 72, true);
                BigDecimal totalAmount = getTotalAmount(omsCartItems);
                map.put("totalAmount", totalAmount);
            }
        }
        return map;
    }

}
