package com.leyou.item.service;

import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import org.springframework.beans.factory.annotation.Autowired;

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

