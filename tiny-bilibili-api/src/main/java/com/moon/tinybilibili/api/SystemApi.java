package com.moon.tinybilibili.api;

import com.moon.tinybilibili.domain.JsonResponse;
import com.moon.tinybilibili.service.ElasticsearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author Chanmoey
 * @date 2022年07月22日
 */
@RestController
public class SystemApi {

    @Autowired
    private ElasticsearchService elasticsearchService;

    @GetMapping("/contents")
    public JsonResponse<List<Map<String, Object>>> getContents(@RequestParam String keyword,
                                                               @RequestParam Integer pageNo,
                                                               @RequestParam Integer pageSize)
            throws IOException {
        return new JsonResponse<>(elasticsearchService.getContents(keyword, pageNo, pageSize));
    }
}
