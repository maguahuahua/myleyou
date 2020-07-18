package com.leyou.item.web;

import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import com.leyou.item.service.SpecGroupService;
import com.leyou.item.service.impl.SpecGroupServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author chenxm
 * @date 2020/7/7 - 15:27
 */

@RequestMapping("spec")
@RestController
public class SpecGroupController {

    @Autowired
    private SpecGroupService specGroupService;

    /**
     * 根据分类id查询规格组
     *
     * @param cid
     * @return http://api.leyou.com/api/item/spec/groups/76
     */
    @GetMapping("groups/{cid}")
    public ResponseEntity<List<SpecGroup>> queryGroupByCid(@PathVariable("cid") Long cid) {
        return ResponseEntity.ok(specGroupService.queryGroupByCid(cid));
    }

    /**
     * 根据组id查参数
     *
     * @param gid
     * @return http://api.leyou.com/api/item/spec/params?gid=1
     */
    @GetMapping("params")
    public ResponseEntity<List<SpecParam>> queryParamByGid(@RequestParam("gid") Long gid) {
        return ResponseEntity.ok(specGroupService.queryParamByGid(gid));
    }

    /**
     * 直接根据分类id查询规格参数
     * @param cid
     * @return http://api.leyou.com/api/item/spec/76
     */
    @GetMapping("{cid}")
    public ResponseEntity<List<SpecParam>> queryParamByCid(@PathVariable("cid") Long cid) {
        return ResponseEntity.ok(specGroupService.queryParamByCid(cid));
    }

    @GetMapping("group")
    public ResponseEntity<List<SpecGroup>> queryGroupByCid2(@RequestParam("cid") Long cid){
        return ResponseEntity.ok(specGroupService.queryGroupByCid2(cid));
    }
}
