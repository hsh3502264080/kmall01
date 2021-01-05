package com.kgc.kmall.bean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.List;
@ApiModel("销售属性和商品的关联实体类")
public class PmsProductSaleAttr implements Serializable {
    @ApiModelProperty("编号")
    private Long id;
    @ApiModelProperty("商品编号")
    private Long productId;
    @ApiModelProperty("销售属性编号")
    private Long saleAttrId;
    @ApiModelProperty("销售属性name")
    private String saleAttrName;
    @ApiModelProperty("销售属性值list")



    private List<pmsProductSaleAttrValue> spuSaleAttrValueList;
    public List<pmsProductSaleAttrValue> getSpuSaleAttrValueList() {
        return spuSaleAttrValueList;
    }

    public void setSpuSaleAttrValueList(List<pmsProductSaleAttrValue> spuSaleAttrValueList) {
        this.spuSaleAttrValueList = spuSaleAttrValueList;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Long getSaleAttrId() {
        return saleAttrId;
    }

    public void setSaleAttrId(Long saleAttrId) {
        this.saleAttrId = saleAttrId;
    }

    public String getSaleAttrName() {
        return saleAttrName;
    }

    public void setSaleAttrName(String saleAttrName) {
        this.saleAttrName = saleAttrName == null ? null : saleAttrName.trim();
    }
}