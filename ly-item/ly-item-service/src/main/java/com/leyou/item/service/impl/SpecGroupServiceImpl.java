package com.leyou.item.service.impl;

import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.item.mapper.SpecGroupMapper;
import com.leyou.item.mapper.SpecParamMapper;
import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import com.leyou.item.service.SpecGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author chenxm
 * @date 2020/7/7 - 15:27
 */

@Service
public class SpecGroupServiceImpl implements SpecGroupService {
    @Autowired
    private SpecGroupMapper specGroupMapper;
    @Autowired
    private SpecParamMapper specParamMapper;

    @Override
    public List<SpecGroup> queryGroupByCid(Long cid) {
        //查询条件
        SpecGroup specGroup = new SpecGroup();
        specGroup.setCid(cid);

        List<SpecGroup> list = specGroupMapper.select(specGroup);
        if (CollectionUtils.isEmpty(list)) {
            throw new LyException(ExceptionEnum.SPEC_GROUP_NOT_FOUND);
        }
        return list;
    }

    @Override
    public List<SpecParam> queryParamByGid(Long gid) {
        //查询条件
        SpecParam specParam = new SpecParam();
        specParam.setGroupId(gid);

        List<SpecParam> list = specParamMapper.select(specParam);
        if (CollectionUtils.isEmpty(list)) {
            throw new LyException(ExceptionEnum.SPEC_PARAM_NOT_FOUND);
        }
        return list;
    }

    @Override
    public List<SpecParam> queryParamByCid(Long cid) {
        //查询条件
        SpecParam specParam = new SpecParam();
        specParam.setCid(cid);

        List<SpecParam> list = specParamMapper.select(specParam);
        if (CollectionUtils.isEmpty(list)) {
            throw new LyException(ExceptionEnum.SPEC_PARAM_NOT_FOUND);
        }
        return list;
    }


    //值得一看,比queryGroupByCid多封装了一些信息
    @Override
    public List<SpecGroup> queryGroupByCid2(Long cid) {
        //查询规格组
        List<SpecGroup> specGroups = queryGroupByCid(cid);
        //查询当前分类下的参数
        List<SpecParam> specParams = queryParamByCid(cid);

        //先把规格参数变成map，键是规格组的id，值是组下所有参数
        Map<Long, List<SpecParam>> map = new HashMap<>();
        for (SpecParam specParam : specParams) {
            //这个组id在map中不存在，新增一个List
            if (!map.containsKey(specParam.getGroupId())) {
                map.put(specParam.getGroupId(), new ArrayList<>());
            }
            map.get(specParam.getGroupId()).add(specParam);   //在对应组id下添加参数
        }
        //填充params到group,  params在map中作为值
        for (SpecGroup specGroup : specGroups) {
            specGroup.setParams(map.get(specGroup.getId()));
        }

        return specGroups;
    }
}
