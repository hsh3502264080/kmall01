package com.kgc.kmall.service;

import com.kgc.kmall.bean.PmsBaseAttrInfo;
import com.kgc.kmall.bean.PmsBaseAttrValue;

import java.util.List;
import java.util.Set;

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

    //显示平台属性 set去重的结果 根据 valueId 查询视图
    List<PmsBaseAttrInfo> selectAttrInfoValueListByValueId(Set<Long> valueIds);

}
