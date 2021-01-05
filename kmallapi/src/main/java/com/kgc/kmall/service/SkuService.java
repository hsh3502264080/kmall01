package com.kgc.kmall.service;

import com.kgc.kmall.bean.PmsSkuInfo;

import java.util.List;

/**
 * @author shkstart
 * @create 2020-12-23 15:33
 */
public interface SkuService {
    public String saveSkuInfo(PmsSkuInfo skuInfo);

    PmsSkuInfo selectBySkuId(Long skuId);
    //根据属性值实现sku商品的切换 点击不同属性换商品
    List<PmsSkuInfo> selectBySpuId(Long spuId);
    List<PmsSkuInfo> getAllSku();
}
