package com.kgc.kmall.service;

import com.kgc.kmall.bean.PmsSearchSkuInfo;
import com.kgc.kmall.bean.PmsSearchSkuParam;

import java.util.List;

/**
 * @author shkstart
 * @create 2021-01-05 15:17
 */
public interface SearchService {
    List<PmsSearchSkuInfo> list(PmsSearchSkuParam pmsSearchSkuParam);
}
