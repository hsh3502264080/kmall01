package com.kgc.kmall.searchservice.service;

import com.kgc.kmall.bean.PmsSearchSkuInfo;
import com.kgc.kmall.bean.PmsSearchSkuParam;
import com.kgc.kmall.bean.PmsSkuAttrValue;
import com.kgc.kmall.service.SearchService;
import io.searchbox.client.JestClient;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import org.apache.dubbo.config.annotation.Service;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author shkstart
 * @create 2021-01-05 15:17
 */
@Service
@Component
public class SearchServiceImpl implements SearchService {
    @Resource
    JestClient jestClient;
    @Override
    public List<PmsSearchSkuInfo> list(PmsSearchSkuParam pmsSearchSkuParam) {
        List<PmsSearchSkuInfo> pmsSearchSkuInfos=new ArrayList<>();
        String catalog3Id = pmsSearchSkuParam.getCatalog3Id();
        String keyword = pmsSearchSkuParam.getKeyword();
        String[] valueId = pmsSearchSkuParam.getValueId();
        SearchSourceBuilder searchSourceBuilder=new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder=new BoolQueryBuilder();
       if(catalog3Id!=null){
           TermQueryBuilder termQueryBuilder=new TermQueryBuilder("catalog3Id",catalog3Id);
       boolQueryBuilder.filter(termQueryBuilder);
       }
       if(valueId!=null){
           for (String pmsSkuAttrValue : valueId) {
               TermQueryBuilder termQueryBuilder=new TermQueryBuilder("skuAttrValueList.valueId",pmsSkuAttrValue);
               boolQueryBuilder.filter(termQueryBuilder);
           }
       }
       if(keyword!=null&&keyword.isEmpty()==false){
           MatchQueryBuilder matchQueryBuilder=new MatchQueryBuilder("skuName",keyword);
           boolQueryBuilder.filter(matchQueryBuilder);
       }
       searchSourceBuilder.query(boolQueryBuilder);
       searchSourceBuilder.sort("id", SortOrder.DESC);
        //高亮
        HighlightBuilder highlightBuilder=new HighlightBuilder();
        highlightBuilder.field("skuName" );
        highlightBuilder.preTags("<span style='color:red;'>");
        highlightBuilder.postTags("</span>");
        searchSourceBuilder.highlighter(highlightBuilder);
        String dsl = searchSourceBuilder.toString();
        Search search=new Search.Builder(dsl).addIndex("kmall").addType("PmsSkuInfo").build();
        try {
            SearchResult searchResult = jestClient.execute(search);
            List<SearchResult.Hit<PmsSearchSkuInfo, Void>> hits = searchResult.getHits(PmsSearchSkuInfo.class);
            for (SearchResult.Hit<PmsSearchSkuInfo, Void> hit : hits) {
                PmsSearchSkuInfo source = hit.source;
                //获取高亮数据
                Map<String, List<String>> highlight = hit.highlight;
                if (highlight!=null){
                    String skuName = highlight.get("skuName").get(0);
                    //使用高亮的skuName替换原来的skuName
                    source.setSkuName(skuName);
                }
                pmsSearchSkuInfos.add(source);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return pmsSearchSkuInfos;
    }
}
