package com.kgc.kmall.service;

import com.kgc.kmall.bean.PmsBaseAttrInfo;
import com.kgc.kmall.bean.PmsBaseAttrValue;

import java.util.List;

/**
 * @author shkstart
 * @create 2020-12-16 16:29
 */
public interface AttrService {
    //根据三级分类id查询属性
    public List<PmsBaseAttrInfo> select(Long catalog3Id);
    //添加属性
    public Integer add(PmsBaseAttrInfo attrInfo);
    //根据属性id查询属性值
    public List<PmsBaseAttrValue> getAttrValueList(Long attrId);


}
