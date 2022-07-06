package com.moon.tinybilibili.api;

import com.moon.tinybilibili.service.DemoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Chanmoey
 * @date 2022年07月06日
 */
@RestController
public class DemoApi {

    @Autowired
    private DemoService demoService;

    @GetMapping("/query")
    public Long query(Long id) {
        return this.demoService.query(id);
    }
}
