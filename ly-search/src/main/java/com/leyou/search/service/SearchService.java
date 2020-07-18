package com.leyou.search.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.JsonUtils;
import com.leyou.common.vo.PageResult;
import com.leyou.item.pojo.*;
import com.leyou.search.client.BrandClient;
import com.leyou.search.client.CategoryClient;
import com.leyou.search.client.GoodsClient;
import com.leyou.search.client.SpecificationClient;
import com.leyou.search.pojo.Goods;
import com.leyou.search.pojo.SearchRequest;
import com.leyou.search.pojo.SearchResult;
import com.leyou.search.repository.GoodsRepository;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.InternalMappedTerms;
import org.elasticsearch.search.aggregations.bucket.terms.LongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.EnableMBeanExport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SourceFilter;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author chenxm
 * @date 2020/7/11 - 21:05
 * 查数据库，得到spu，以spu为单位，存到索引库中，对应索引库中一个goods
 */
@Service
public class SearchService {
    @Autowired
    private CategoryClient categoryClient;
    @Autowired
    private BrandClient brandClient;
    @Autowired
    private GoodsClient goodsClient;
    @Autowired
    private SpecificationClient specificationClient;
    @Autowired
    private GoodsRepository goodsRepository;

    @Autowired
    private ElasticsearchTemplate template;

    public Goods buildGoods(Spu spu) {
        Long spuId = spu.getId();
        //-----------------------1.搜索字段all---------------------------------
        //查询分类
        List<Category> categories = categoryClient.queryCategoryByIds(
                Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));
        if (CollectionUtils.isEmpty(categories)) {
            throw new LyException(ExceptionEnum.CATEGORY_NOT_FOUND);
        }
        List<String> names = categories.stream().map(Category::getName).collect(Collectors.toList());
        //查询品牌
        Brand brand = brandClient.queryById(spu.getBrandId());
        if (brand == null) {
            throw new LyException(ExceptionEnum.BRAND_NOT_FOUND);
        }
        //搜索字段
        String all = spu.getTitle() + StringUtils.join(names, "-") + brand.getName();

        //-------------------2.sku集合skus，顺便priceList-----------------------
        //查询skus
        List<Sku> skuList = goodsClient.querySkuById(spuId);
        if (CollectionUtils.isEmpty(skuList)) {
            throw new LyException(ExceptionEnum.GOODS_SKU_NOT_FOUND);
        }
        //对sku处理，因为skuList中的sku有很多属性我们不需要的，skus才是需要的
        // Map序列化为json和对象是一样的，只是map的键还可以自己改
        ArrayList<Map<String, Object>> skus = new ArrayList<>();

        //价格集合
        HashSet<Long> priceList = new HashSet<>();
        for (Sku sku : skuList) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("id", sku.getId());
            map.put("title", sku.getTitle());
            map.put("price", sku.getPrice());
            map.put("image", StringUtils.substringBefore(sku.getImages(), ","));//sku.getImages()可能为空
            skus.add(map);
            //处理价格
            priceList.add(sku.getPrice());
        }

        //--------------------------3.specs可搜索的规格参数-------------------------
        //3.1查询规格参数，name作为specs的键
        List<SpecParam> specParams = specificationClient.queryParamByCid(spu.getCid3());
        //需要过滤掉searching是否为true
        specParams.removeIf(specParam -> !specParam.getSearching());
    /*
        不能用增强for，边循环边删除，要用迭代器，removeIf就是用的原始的迭代器
        for (SpecParam specParam : specParams) {
            if (!specParam.getSearching()) {
                specParams.remove(specParam);
            }else {
                continue;
            }
        }*/
        if (CollectionUtils.isEmpty(specParams)) {
            throw new LyException(ExceptionEnum.SPEC_PARAM_NOT_FOUND);
        }

        //3.2查商品详情中的通用、特有参数，作为specs的值
        SpuDetail spuDetail = goodsClient.queryDetailById(spuId);
        //获得通用规格参数
        Map<Long, String> genericSpec = JsonUtils.parseMap(spuDetail.getGenericSpec(), Long.class, String.class);
        //获得特有规格参数，比如RAM有4、6G是一个列表，所以用这个形式
        Map<Long, List<String>> specialSpec = JsonUtils.nativeRead(
                spuDetail.getSpecialSpec(), new TypeReference<Map<Long, List<String>>>() {
                });
        //规格参数，key是规格参数的名字，值是规格参数的值
        HashMap<String, Object> specs = new HashMap<>();
        for (SpecParam specParam : specParams) {
            //规格名称
            String key = specParam.getName();
            Object value = "";
            //是否通用参数
            if (specParam.getGeneric()) {
                value = genericSpec.get(specParam.getId());  //spu详情表跟spu的表主键是一样的
                //判断是否数组类型
                if (specParam.getNumeric()) {
                    //处理成段
                    value = chooseSegment(value.toString(), specParam);
                } else {
                    value = specialSpec.get(specParam.getId());
                }
                specs.put(key, value);
            }
        }

        //构建goods对象
        Goods goods = new Goods();
        goods.setBrandId(spu.getBrandId());
        goods.setCid1(spu.getCid1());
        goods.setCid2(spu.getCid2());
        goods.setCid3(spu.getCid3());
        goods.setCreateTime(spu.getCreate_time());
        goods.setId(spuId);
        goods.setAll(all);          //搜索字段：包含标题、分类、品牌等
        goods.setPrice(priceList);  //sku价格集合
        goods.setSkus(JsonUtils.serialize(skus));  // sku集合的json格式
        goods.setSpecs(specs); //所有可搜索的规格参数、
        goods.setSubTitle(spu.getSubTitle());
        return goods;
    }


    private String chooseSegment(String value, SpecParam p) {
        double val = NumberUtils.toDouble(value);
        String result = "其它";
        //保存数值段
        for (String segment : p.getSegments().split(",")) {
            String[] segs = segment.split("-");
            //获取数值范围
            double begin = NumberUtils.toDouble(segs[0]);
            double end = Double.MAX_VALUE;
            if (segs.length == 2) {
                end = NumberUtils.toDouble(segs[1]);
            }
            //判断是否在范围内
            if (val >= begin && val < end) {
                if (segs.length == 1) {
                    result = segs[0] + p.getUnit() + "以上";
                } else if (begin == 0) {
                    result = segs[1] + p.getUnit() + "以下";
                } else {
                    result = segment + p.getUnit();
                }
                break;
            }
        }
        return result;
    }

    public PageResult<Goods> search(SearchRequest searchRequest) {
        Integer page = searchRequest.getPage() - 1;
        Integer size = searchRequest.getSize();
        String key = searchRequest.getKey();

        //创建查询构建器
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        //0 结果过滤,有些字段不需要，返回为null
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{"id", "subTitle", "skus"}, null));
        //1 分页
        queryBuilder.withPageable(PageRequest.of(page, size));

//        QueryBuilder basicQuery = QueryBuilders.matchQuery("all", key);
        QueryBuilder basicQuery = buildBasicQuery(searchRequest);    //再加上过滤条件
        //2 查询条件,在all中搜key
        queryBuilder.withQuery(basicQuery);


        //聚合分类和品牌
        String categoryAggName = "catagory_agg";
        queryBuilder.addAggregation(AggregationBuilders.terms(categoryAggName).field("cid3"));
        String brandAggName = "brand_agg";
        queryBuilder.addAggregation(AggregationBuilders.terms(brandAggName).field("brandId"));

        //3 查询,有聚合的
        AggregatedPage<Goods> result = template.queryForPage(queryBuilder.build(), Goods.class);
        //3 查询
//        Page<Goods> result = goodsRepository.search(queryBuilder.build());


        //4 解析结果
        //4.1 解析分页结果
        long total = result.getTotalElements();
        int totalPages = result.getTotalPages();
        List<Goods> goodList = result.getContent();
        //4.2解析聚合结果
        Aggregations aggrs = result.getAggregations();
        List<Category> categories = parseCategoryAgg(aggrs.get(categoryAggName));
        List<Brand> brands = parseBrandAgg(aggrs.get(brandAggName));

        //5 完成规格参数聚合
        List<Map<String, Object>> specs = null;
        if (categories != null && categories.size() == 1) {
            //商品分类存在且数量为1，可以聚合规格参数
            specs = buildSpecificationAgg(categories.get(0).getId(), basicQuery);

        }

        return new SearchResult(total, totalPages, goodList, categories, brands, specs);

    }

    //TODO 这查询有问题好像，没生效。。待解决，可能是前端问题，数据格式问题
    private QueryBuilder buildBasicQuery(SearchRequest searchRequest) {
        //创建布尔查询
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //查询条件
        boolQueryBuilder.must(QueryBuilders.matchQuery("all", searchRequest.getKey()));
        //过滤条件
        Map<String, String> map = searchRequest.getFilter();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String key = entry.getKey();
            //处理key,
            if ("id3".equals(key) && "brandId".equals(key)) {
                key = "specs." + key + ".keyword";
            }
            String value = entry.getValue();
            boolQueryBuilder.filter(QueryBuilders.termQuery(key, value));
        }
        return boolQueryBuilder;
    }

    private List<Map<String, Object>> buildSpecificationAgg(Long cid, QueryBuilder basicQuery) {
        List<Map<String, Object>> specs = new ArrayList<>();
        //查询需要聚合的规格参数
        List<SpecParam> specParams = specificationClient.queryParamByCid(cid);
        specParams.removeIf(specParam -> !specParam.getSearching());
        //2聚合
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        //2.1带上查询条件
        queryBuilder.withQuery(basicQuery);
        //2.2聚合
        for (SpecParam specParam : specParams) {
            String name = specParam.getName();
            queryBuilder.addAggregation(
                    AggregationBuilders.terms(name).field("specs." + name + ".keyword"));  //参考Goods的specs字段,field是聚合字段
        }
        //获取结果
        AggregatedPage<Goods> result = template.queryForPage(queryBuilder.build(), Goods.class);
        //解析结果
        Aggregations aggs = result.getAggregations();
        for (SpecParam specParam : specParams) {
            //规格参数名
            String name = specParam.getName();
            Aggregation terms1 = aggs.get(name);     //聚合名也是以name命名
            if (terms1 instanceof StringTerms) {
                StringTerms terms = (StringTerms) terms1;

                //准备map
                HashMap<String, Object> map = new HashMap<>();
                map.put("k", name);
                map.put("options", terms.getBuckets().stream().map(bucket -> bucket.getKeyAsString()).collect(Collectors.toList()));
                specs.add(map);
            }
        }
        return specs;
    }

    private List<Brand> parseBrandAgg(LongTerms terms) {
        try {
            List<Long> ids = terms.getBuckets()
                    .stream().map(bucket -> bucket.getKeyAsNumber().longValue())
                    .collect(Collectors.toList());
            List<Brand> brands = brandClient.queryBrandByIds(ids);
            return brands;
        } catch (Exception e) {
            return null;
        }
    }

    private List<Category> parseCategoryAgg(LongTerms terms) {
        try {
            List<Long> ids = terms.getBuckets()
                    .stream().map(bucket -> bucket.getKeyAsNumber().longValue())
                    .collect(Collectors.toList());
            List<Category> categories = categoryClient.queryCategoryByIds(ids);
            return categories;
        } catch (Exception e) {
            return null;
        }
    }


    public void createOrUpdateIndex(Long spuId) {
        //查询spu
        Spu spu = goodsClient.querySpuById(spuId);
        //构建goods
        Goods goods = buildGoods(spu);
        //存入索引库
        goodsRepository.save(goods);
    }

    public void deleteIndex(Long spuId) {
        goodsRepository.deleteById(spuId);
    }
}
