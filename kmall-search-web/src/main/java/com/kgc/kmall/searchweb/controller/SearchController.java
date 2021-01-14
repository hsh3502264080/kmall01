package com.kgc.kmall.searchweb.controller;

import com.kgc.kmall.annotations.LoginRequired;
import com.kgc.kmall.bean.*;
import com.kgc.kmall.service.AttrService;
import com.kgc.kmall.service.SearchService;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author shkstart
 * @create 2021-01-04 16:35
 */
@Controller
public class SearchController {

    @Reference
    SearchService searchService;
    @Reference
    AttrService attrService;

    public String getURLParam(PmsSearchSkuParam pmsSearchSkuParam) {
        StringBuffer buffer = new StringBuffer();
        String catalog3Id = pmsSearchSkuParam.getCatalog3Id();
        String keyword = pmsSearchSkuParam.getKeyword();
        String[] valueId = pmsSearchSkuParam.getValueId();
        if (StringUtils.isNoneBlank(catalog3Id)) {
            buffer.append("&catalog3Id=" + catalog3Id);
        }
        if (StringUtils.isNoneBlank(keyword)) {
            buffer.append("&keyword=" + keyword);
        }
        if (valueId != null) {
            for (String vid : valueId) {
                buffer.append("&valueId=" + vid);
            }
        }
        return buffer.substring(1);
    }

    //面包屑地址获取
    public String getURLParam(PmsSearchSkuParam pmsSearchSkuParam, String vvid) {
        StringBuffer buffer = new StringBuffer();
        String catalog3Id = pmsSearchSkuParam.getCatalog3Id();
        String keyword = pmsSearchSkuParam.getKeyword();
        String[] valueId = pmsSearchSkuParam.getValueId();
        if (StringUtils.isNoneBlank(catalog3Id)) {
            buffer.append("&catalog3Id=" + catalog3Id);
        }
        if (StringUtils.isNoneBlank(keyword)) {
            buffer.append("&keyword=" + keyword);
        }
        if (valueId != null) {
            for (String vid : valueId) {
                if (vid.equals(vvid) == false) {
                    buffer.append("&valueId=" + vid);
                }
            }
        }
        return buffer.substring(1);
    }

    //面包屑名字获取
    public String getValueName(List<PmsBaseAttrInfo> pmsBaseAttrInfos, String vid) {
        String valueName = "";
        for (PmsBaseAttrInfo pmsBaseAttrInfo : pmsBaseAttrInfos) {
            for (PmsBaseAttrValue pmsBaseAttrValue : pmsBaseAttrInfo.getAttrValueList()) {
                if (vid.equals(pmsBaseAttrValue.getId().toString())) {
                    valueName = pmsBaseAttrInfo.getAttrName() + ":" + pmsBaseAttrValue.getValueName();
                    return valueName;
                }
            }
        }
        return valueName;
    }
    @LoginRequired(false)
    @RequestMapping("/index.html")
    public String index() {
        return "index";
    }

    @RequestMapping("/list.html")
    public String list(PmsSearchSkuParam pmsSearchSkuParam, Model model) {
        List<PmsSearchSkuInfo> pmsSearchSkuInfos = searchService.list(pmsSearchSkuParam);
        //获取平台属性valueId
        Set<Long> valueIdSet = new HashSet<>();//封装一个set集合 set不可重复 用来去重获取vlaueId
        for (PmsSearchSkuInfo pmsSearchSkuInfo : pmsSearchSkuInfos) {
            for (PmsSkuAttrValue pmsSkuAttrValue : pmsSearchSkuInfo.getSkuAttrValueList()) {
                valueIdSet.add(pmsSkuAttrValue.getValueId());
            }
        }
        System.out.println(Arrays.toString(valueIdSet.toArray()));
        List<PmsBaseAttrInfo> pmsBaseAttrInfos = attrService.selectAttrInfoValueListByValueId(valueIdSet);
        String urlParam = getURLParam(pmsSearchSkuParam);
        model.addAttribute("urlParam", urlParam);
        //已选中的valueId
        String[] valueId = pmsSearchSkuParam.getValueId();
        //封装面包屑数据
        if (valueId != null) {
            List<PmsSearchCrumb> pmsSearchCrumbList = new ArrayList<>();
            for (String s : valueId) {
                PmsSearchCrumb pmsSearchCrumb = new PmsSearchCrumb();
                pmsSearchCrumb.setUrlParam(getURLParam(pmsSearchSkuParam, s));
                pmsSearchCrumb.setValueId(s);
                String valueName = getValueName(pmsBaseAttrInfos, s);
                pmsSearchCrumb.setValueName(valueName);
                pmsSearchCrumbList.add(pmsSearchCrumb);
            }
            model.addAttribute("attrValueSelectedList", pmsSearchCrumbList);
        }
        //判断条件为不为空
        if (valueId != null) {
            //利用迭代器排除已选的平台属性,删除集合元素不能使用for循环，因为会出现数组越界
            Iterator<PmsBaseAttrInfo> iterator = pmsBaseAttrInfos.iterator();
            while (iterator.hasNext()) {
                PmsBaseAttrInfo pmsBaseAttrInfo = iterator.next();
                for (PmsBaseAttrValue pmsBaseAttrValue : pmsBaseAttrInfo.getAttrValueList()) {
                    for (String s : valueId) {
                        if (s.equals(pmsBaseAttrValue.getId().toString())) {
                            iterator.remove();
                        }
                    }
                }
            }
        }

        //显示关键字
        String keyword = pmsSearchSkuParam.getKeyword();
        model.addAttribute("keyword", keyword);
        model.addAttribute("attrList", pmsBaseAttrInfos);
        model.addAttribute("skuLsInfoList", pmsSearchSkuInfos);
        return "list";
    }

}
