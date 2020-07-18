package com.leyou.search.web;

import com.leyou.common.vo.PageResult;
import com.leyou.search.pojo.Goods;
import com.leyou.search.pojo.SearchRequest;
import com.leyou.search.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author chenxm
 * @date 2020/7/13 - 20:13
 */

@RestController
public class SearchController {
    @Autowired
    private SearchService searchService;

    @PostMapping("page")                             //请求的是json，所以@RequestBody
    public ResponseEntity<PageResult<Goods>> search(@RequestBody SearchRequest searchRequest) {
        return ResponseEntity.ok(searchService.search(searchRequest));
    }
}
