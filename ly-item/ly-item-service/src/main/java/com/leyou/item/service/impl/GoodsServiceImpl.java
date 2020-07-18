package com.leyou.item.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.vo.PageResult;
import com.leyou.item.mapper.*;
import com.leyou.item.pojo.*;
import com.leyou.item.service.BrandService;
import com.leyou.item.service.CategoryService;
import com.leyou.item.service.GoodsService;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.http.fileupload.util.LimitedInputStream;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author chenxm
 * @date 2020/7/8 - 9:06
 */

@Service
public class GoodsServiceImpl implements GoodsService {
    @Autowired
    private SpuMapper spuMapper;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private BrandService brandService;
    @Autowired
    private SpuDetailMapper spuDetailMapper;
    @Autowired
    private SkuMapper skuMapper;
    @Autowired
    private StockMapper stockMapper;

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Override
    public PageResult<Spu> querySpuByPage(Integer page, Integer rows, Boolean saleable, String key) {
        //分页
        PageHelper.startPage(page, rows);

        //过滤
        Example example = new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();
        if (StringUtils.isNotBlank(key)) {
            //搜索过滤条件
            criteria.andLike("title", "%" + key + "%");
        }
        if (saleable != null) {
            //上下架过滤条件：saleable是否为true
            criteria.andEqualTo("saleable", saleable);
        }
        //默认排序
        example.setOrderByClause("last_update_time DESC");
        //查询
        List<Spu> spuList = spuMapper.selectByExample(example);

        if (CollectionUtils.isEmpty(spuList)) {
            throw new LyException(ExceptionEnum.GOODS_NOT_FOUND);
        }

        //根据分类id查分类名字
        loadCategoryAndBrandName(spuList);

        //解析分页结果
        PageInfo<Spu> brandPageInfo = new PageInfo<>(spuList);      //PageInfo就是一个分页Bean，多了pageNum等等
        return new PageResult<Spu>(brandPageInfo.getTotal(), spuList);  //要多返回总数
    }


    @Transactional
    @Override
    public void saveGoods(Spu spu) {
        //新增Spu
        spu.setId(null);           //为什么原来是set为null
        spu.setCreate_time(new Date());
        spu.setLast_update_time(spu.getCreate_time());
        spu.setSaleable(true);
        spu.setValid(false);
        int count = spuMapper.insert(spu);
        if (count != 1) {
            throw new LyException(ExceptionEnum.GOODS_SAVE_ERROR);
        }


        //新增detail，其中的id就是spu的id
        SpuDetail spuDetail = spu.getSpuDetail();
        spuDetail.setSpuId(spu.getId());
        spuDetailMapper.insert(spuDetail);

        ArrayList<Stock> stockList = new ArrayList<>();

        //新增sku,可以写成批量新增模式，不过还是要循环，因为stock要id……
        List<Sku> skus = spu.getSkus();
        if (!CollectionUtils.isEmpty(skus)) {
            for (Sku sku : skus) {
                sku.setCreateTime(new Date());
                sku.setLastUpdateTime(sku.getCreateTime());
                sku.setSpuId(spu.getId());

                count = skuMapper.insert(sku);
                if (count != 1) {
                    throw new LyException(ExceptionEnum.GOODS_SAVE_ERROR);
                }

                //新增库存，stock的id跟sku的id一样，要在sku中取id。传过来有stock，先封装在sku了，转存为Stock
                Stock stock = new Stock();
                stock.setSkuId(sku.getId());
                stock.setStock(sku.getStock());
                stockList.add(stock);
            }
        }
        //批量新增库存
        if (!CollectionUtils.isEmpty(stockList)) {
            count = stockMapper.insertList(stockList);
            if (count != stockList.size()) {
                throw new LyException(ExceptionEnum.GOODS_SAVE_ERROR);
            }
        }

        //发送mq消息
        amqpTemplate.convertAndSend("item.insert", spu.getId());
    }

    @Override
    public SpuDetail queryDetailById(Long spuId) {
        SpuDetail spuDetail = spuDetailMapper.selectByPrimaryKey(spuId);
        if (spuDetail == null) {
            throw new LyException(ExceptionEnum.GOODS_DETAIL_NOT_FOUND);
        }
        return spuDetail;
    }


    @Override
    public List<Sku> querySkuById(Long spuId) {
        //查sku
        Sku sku = new Sku();
        sku.setSpuId(spuId);
        List<Sku> skuList = skuMapper.select(sku);
        if (CollectionUtils.isEmpty(skuList)) {
            throw new LyException(ExceptionEnum.GOODS_SKU_NOT_FOUND);
        }

        //查库存
 /*       for (Sku s : skuList) {            //逐个取出skuid也是stock的id
            Stock stock = stockMapper.selectByPrimaryKey(s.getId());
            if (stock==null){
                throw new LyException(ExceptionEnum.GOOS_SKU_NOT_FOUND);
            }
            s.setStock(stock.getStock());          //取出stock放入sku中，因为返回的stock存在sku中
        }*/

        //查库存，流的方式

        //Sku流转id类型流，并转list
        List<Long> ids = skuList.stream().map(Sku::getId).collect(Collectors.toList());
        //批量查出stock
        List<Stock> stockList = stockMapper.selectByIdList(ids);
        if (CollectionUtils.isEmpty(stockList)) {
            throw new LyException(ExceptionEnum.GOOS_STOCK_NOT_FOUND);
        }
        //一堆stock 的流，转为map，其中map的键是sku的id，值是对应sku库存量
        Map<Long, Integer> stockMap = stockList.stream().collect(Collectors.toMap(Stock::getSkuId, Stock::getStock));
        //每个sku，设置库存量为map的库存量（根据id查得）
        skuList.forEach(s -> s.setStock(stockMap.get(s.getId())));

        return skuList;
    }

    @Override
    public Spu querySpuById(Long id) {
        //查spu
        Spu spu = spuMapper.selectByPrimaryKey(id);
        if (spu == null) {
            throw new LyException(ExceptionEnum.GOODS_NOT_FOUND);
        }
        //查sku,Spu中有skus字段，顺便查了
        spu.setSkus(querySkuById(id));
        //查detail
        spu.setSpuDetail(queryDetailById(id));
        return spu;
    }


    private void loadCategoryAndBrandName(List<Spu> spuList) {
        for (Spu spu : spuList) {
            //处理分类名
            /**
             * map将流中的元素映射到另一个流中，将一种T类型转换成为R类型，参数是Function接口
             * 双冒号 :: 为引用运算符，而它所在的表达式被称为方法引用。如果Lambda要表达的函数方案已经存在于某个方法的实现中，那么则可以通过双冒号来引用该方法作为Lambda的替代者。
             对比下面两种写法，完全等效：
             * Lambda表达式写法： s -> System.out.println(s);
             * 方法引用写法： System.out::println
             * 第一种语义是指：拿到参数之后经Lambda之手，继而传递给System.out.println 方法去处理。
             * 第二种等效写法的语义是指：直接让 System.out 中的 println 方法来取Lambda。两种写法的执行效果完全一样，而第二种方法引用的写法复用了已有方案，更加简洁
             * collect有很多api比较常用的就是stream转成list/set/collection，toList()返回一个 Collector积累输入元素到一个新的 List，
             */
            List<Category> categories = categoryService.queryByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));
//            Stream<String> stringStream = categories.stream().map(Category::getName);   id转为name，还是stream的形式
            List<String> names = categories.stream().map(Category::getName).collect(Collectors.toList());

            spu.setCname(StringUtils.join(names, "/"));  // 手机/手机通讯/手机的形式


            //处理品牌名称
            spu.setBname(brandService.queryById(spu.getBrandId()).getName());
        }

    }

}
