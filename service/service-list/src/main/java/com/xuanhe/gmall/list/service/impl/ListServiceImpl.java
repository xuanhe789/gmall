package com.xuanhe.gmall.list.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.xuanhe.gmall.list.repository.GoodsRespository;
import com.xuanhe.gmall.list.service.ListService;
import com.xuanhe.gmall.model.list.*;
import com.xuanhe.gmall.product.feign.ProductFeignClient;
import org.apache.commons.lang.ArrayUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ListServiceImpl implements ListService {
    @Autowired
    ProductFeignClient productFeignClient;
    @Autowired
    GoodsRespository goodsRespository;
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    RestHighLevelClient restHighLevelClient;

    @Override
    public void onSale(Long skuId) {
        Goods goods=productFeignClient.getGoodsBySkuId(skuId);
        goodsRespository.save(goods);
    }

    @Override
    public void cancelSale(Long skuId) {
//        Goods goods=productFeignClient.getGoodsBySkuId(skuId);
        goodsRespository.deleteById(skuId);
    }

    @Override
    public void incrHotScore(Long skuId) {
        //查询缓存中的热度值
        Long hotScore= (Long) redisTemplate.opsForValue().increment("skuId_hotScore:"+skuId);
        if (hotScore%20==0){
            Optional<Goods> goodOpt = goodsRespository.findById(skuId);
            Goods goods = goodOpt.get();
            goods.setHotScore(hotScore);
            goodsRespository.save(goods);
        }
    }

    @Override
    public SearchResponseVo search(SearchParam searchParam) throws IOException {
        //构建dsl语句
        SearchRequest searchRequest=buildDSL(searchParam);
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        SearchResponseVo responseVO = parseSearchResult(searchResponse);
        responseVO.setPageNo(searchParam.getPageNo());
        responseVO.setPageSize(searchParam.getPageSize());
        Long record=responseVO.getTotal()/responseVO.getPageSize();
        Long totalPages=responseVO.getTotal()%responseVO.getPageSize()==0?record:record+1;
        responseVO.setTotalPages(totalPages);
        return responseVO;
    }

    public SearchResponseVo parseSearchResult(SearchResponse searchResponse) {
        SearchResponseVo searchResponseVo = new SearchResponseVo();
        SearchHits hits = searchResponse.getHits();
        //这个hits存储着goods数据
        SearchHit[] hits1 = hits.getHits();
        //存储goods集合
        List<Goods> goodsList=new ArrayList<>();
        for (SearchHit searchHit : hits1) {
            String sourceAsString = searchHit.getSourceAsString();
            Goods goods = JSONObject.parseObject(sourceAsString, Goods.class);
            Map<String, HighlightField> highlightFields = searchHit.getHighlightFields();
            if (null!=highlightFields&&highlightFields.size()>0) {
                HighlightField titleHighlightField = highlightFields.get("title");
                Text[] fragments = titleHighlightField.getFragments();
                String title = fragments[0].toString();
                goods.setTitle(title);
            }
            goodsList.add(goods);
        }
        searchResponseVo.setGoodsList(goodsList);
        //解析品牌聚合聚合
        Map<String, Aggregation> stringAggregationMap = searchResponse.getAggregations().asMap();
        ParsedLongTerms tmIdAgg = (ParsedLongTerms) stringAggregationMap.get("tmIdAgg");
        List<SearchResponseTmVo> searchResponseTmVoList = tmIdAgg.getBuckets().stream().map(bucket -> {
            SearchResponseTmVo searchResponseTmVo = new SearchResponseTmVo();
            Long tmId = Long.parseLong(bucket.getKeyAsString());
            ParsedStringTerms tmNameAgg = bucket.getAggregations().get("tmNameAgg");
            ParsedStringTerms tmLogoUrlAgg = bucket.getAggregations().get("tmLogoUrlAgg");
            searchResponseTmVo.setTmId(tmId);
            searchResponseTmVo.setTmName(tmNameAgg.getBuckets().get(0).getKeyAsString());
            searchResponseTmVo.setTmLogoUrl(tmLogoUrlAgg.getBuckets().get(0).getKeyAsString());
            return searchResponseTmVo;
        }).collect(Collectors.toList());
        searchResponseVo.setTrademarkList(searchResponseTmVoList);

        //解析平台属性聚合
        ParsedNested attrAgg = searchResponse.getAggregations().get("attrAgg");
        ParsedLongTerms attrIdAgg = attrAgg.getAggregations().get("attrIdAgg");
        List<SearchResponseAttrVo> searchResponseAttrVoList = attrIdAgg.getBuckets().stream().map(bucket -> {
            SearchResponseAttrVo searchResponseAttrVo = new SearchResponseAttrVo();
            Long attrId = Long.parseLong(((Terms.Bucket) bucket).getKeyAsString());
            //获取平台属性值集合，和attrId为1对多的关系
            ParsedStringTerms parsedStringTerms = bucket.getAggregations().get("attrValueAgg");
            List<String> attrValueList = parsedStringTerms.getBuckets().stream().map(bucket1 -> ((Terms.Bucket) bucket1).getKeyAsString()).collect(Collectors.toList());
            searchResponseAttrVo.setAttrId(attrId);
            searchResponseAttrVo.setAttrValueList(attrValueList);
            //获取平台属性名，和attrId为1对1的关系
            ParsedStringTerms attrNameAgg = ((Terms.Bucket) bucket).getAggregations().get("attrNameAgg");
            String attrName = attrNameAgg.getBuckets().get(0).getKeyAsString();
            searchResponseAttrVo.setAttrName(attrName);
            return searchResponseAttrVo;
        }).collect(Collectors.toList());

        //解析高亮

        searchResponseVo.setAttrsList(searchResponseAttrVoList);
        searchResponseVo.setTotal(hits.totalHits);
        return searchResponseVo;
    }

    //制作dsl语句
    private SearchRequest buildDSL(SearchParam searchParam) {
        SearchSourceBuilder searchSourceBuilder=new SearchSourceBuilder();
        // 构建boolQueryBuilder
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        //判断条件是否为空，关键字
        if (!StringUtils.isEmpty(searchParam.getKeyword())){
            //最后那个operator的值是AND或OR，意思是说分词之后，如果是OR，只要搜索的字段包含其中一个分词就全部返回
            //AND的话，就是搜索字段必须包含全部的分词才会返回
            MatchQueryBuilder keywordQuery = QueryBuilders.matchQuery("title", searchParam.getKeyword()).operator(Operator.OR);
            boolQueryBuilder.must(keywordQuery);
        }

        //品牌查询
        String trademark = searchParam.getTrademark();
        if (!StringUtils.isEmpty(trademark)){
            // trademark=2:华为
            String[] split = trademark.split(":");
            Long tmId=Long.parseLong(split[0]);
            // 根据品牌Id过滤
            TermQueryBuilder termQueryBuilder=QueryBuilders.termQuery("tmId",tmId);
            boolQueryBuilder.filter(termQueryBuilder);
        }

        //三级分类查询
        if (!StringUtils.isEmpty(searchParam.getCategory3Id())){
            TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("category3Id", searchParam.getCategory3Id());
            boolQueryBuilder.filter(termQueryBuilder);
        }

        //平台属性值查询
        // 23:4G:运行内存
        String[] props = searchParam.getProps();
        if (!ArrayUtils.isEmpty(props)){
            for (String prop:props){
                String[] split = prop.split(":");
                // 嵌套查询子查询
                BoolQueryBuilder subBoolQuery = QueryBuilders.boolQuery();
                // 构建子查==询中的过滤条件
                subBoolQuery.must(QueryBuilders.termQuery("attrs.attrId",split[0]));
                subBoolQuery.must(QueryBuilders.termQuery("attrs.attrValue",split[1]));
                // ScoreMode.None ？
                NestedQueryBuilder nestedQueryBuilder = QueryBuilders.nestedQuery("attrs", subBoolQuery, ScoreMode.None);
                // 添加到整个过滤对象中
                boolQueryBuilder.filter(nestedQueryBuilder);
            }
        }
        //执行查询方法
        searchSourceBuilder.query(boolQueryBuilder);

        //构建分页
        int from=(searchParam.getPageNo()-1)*searchParam.getPageSize();
        searchSourceBuilder.from(from);
        searchSourceBuilder.size(searchParam.getPageSize());

        //排序
        String order = searchParam.getOrder();
        if (!StringUtils.isEmpty(order)){
            String[] split = order.split(":");
            String orderFiled=null;
            if (split[0]=="1"){
                orderFiled="hotScore";
            }else {
                orderFiled="price";
            }
            searchSourceBuilder.sort(orderFiled,"asc".equals(split[1])? SortOrder.ASC: SortOrder.DESC);
        }else {
            // 没有传值的时候给默认值
            searchSourceBuilder.sort("hotScore",SortOrder.DESC);
        }

        //品牌聚合，抽取品牌平台属性，便于搜索
        TermsAggregationBuilder termsAggregationBuilder = AggregationBuilders.terms("tmIdAgg").field("tmId")
                .subAggregation(AggregationBuilders.terms("tmNameAgg").field("tmName"))
                .subAggregation(AggregationBuilders.terms("tmLogoUrlAgg").field("tmLogoUrl"));
        searchSourceBuilder.aggregation(termsAggregationBuilder);
        //其余平台属性聚合,nested类型
        NestedAggregationBuilder nestedAggregationBuilder = AggregationBuilders.nested("attrAgg", "attrs")
                .subAggregation(AggregationBuilders.terms("attrIdAgg").field("attrs.attrId")
                        .subAggregation(AggregationBuilders.terms("attrNameAgg").field("attrs.attrName"))
                        .subAggregation(AggregationBuilders.terms("attrValueAgg").field("attrs.attrValue")));
        searchSourceBuilder.aggregation(nestedAggregationBuilder);

        //设置高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("title");
        //设置高亮属性的前置内容
        highlightBuilder.preTags("<span style=color:red>");
        //设置高亮属性的后置内容
        highlightBuilder.postTags("</span>");
        searchSourceBuilder.highlighter(highlightBuilder);

        System.out.println(searchSourceBuilder.toString());
        SearchRequest searchRequest = new SearchRequest("goods");
        searchRequest.types("info");
        searchRequest.source(searchSourceBuilder);
        return searchRequest;

    }
}
