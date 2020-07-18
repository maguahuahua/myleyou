package com.leyou.item.api;

import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author chenxm
 * @date 2020/7/11 - 20:18
 */
public interface SpecificationApi {
    /**
     * 根据分类id查询规格组
     */
    @GetMapping("spec/groups/{cid}")
    List<SpecGroup> queryGroupByCid(@PathVariable("cid") Long cid);

    /**
     * 根据组id查参数
     */
    @GetMapping("spec/params")
    List<SpecParam> queryParamByGid(@RequestParam("gid") Long gid);

    /**
     * 直接根据分类id查询规格参数
     */
    @GetMapping("spec/{cid}")
    List<SpecParam> queryParamByCid(@PathVariable("cid") Long cid);

    /**
     * 重复的，查详情时可能用到，先写着
     * @param cid
     * @return
     */
    @GetMapping("spec/group")
    List<SpecGroup> queryGroupByCid2(@RequestParam("cid") Long cid);
}
