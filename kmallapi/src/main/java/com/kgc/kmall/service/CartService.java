package com.kgc.kmall.service;

import com.kgc.kmall.bean.OmsCartItem;

import java.util.List;

/**
 * @author shkstart
 * @create 2021-01-08 16:13
 */
public interface CartService {
    //根据skuid和member_id判断是否有商品
    OmsCartItem ifCartExistByUser(String memberId, long skuId);
   //添加方法
    void addCart(OmsCartItem omsCartItem);
    //修改数量
    void updateCart(OmsCartItem omsCartItemFromDb);
    //缓存
    void flushCartCache(String memberId);
    //
    List<OmsCartItem> cartList(String memberId);
    //修改状态选中状态是否登录skuid修改ischecked
    void checkCart(OmsCartItem omsCartItem);
}
