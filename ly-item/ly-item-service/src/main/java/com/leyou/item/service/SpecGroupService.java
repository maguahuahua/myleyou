package com.leyou.item.service;

import com.leyou.pojo.SpecGroup;
import com.leyou.pojo.SpecParam;

import java.util.List;

/**
 * @author chenxm
 * @date 2020/7/7 - 15:25
 */
public interface SpecGroupService {

    List<SpecGroup> queryGroupByCid(Long cid);

    List<SpecParam> queryParamByGid(Long gid);

    List<SpecParam> queryParamByCid(Long cid);

    List<SpecGroup> queryGroupByCid2(Long cid);
}

