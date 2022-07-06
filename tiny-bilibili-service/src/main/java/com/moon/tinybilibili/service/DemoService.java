package com.moon.tinybilibili.service;

import com.moon.tinybilibili.dao.DemoDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Chanmoey
 * @date 2022年07月06日
 */
@Service
public class DemoService {

    @Autowired
    private DemoDao demoDao;

    public Long query(Long id) {
        return this.demoDao.query(id);
    }
}
