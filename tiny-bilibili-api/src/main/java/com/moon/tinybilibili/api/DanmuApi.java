package com.moon.tinybilibili.api;

import com.moon.tinybilibili.api.support.UserSupport;
import com.moon.tinybilibili.domain.Danmu;
import com.moon.tinybilibili.domain.JsonResponse;
import com.moon.tinybilibili.service.DanmuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Chanmoey
 * @date 2022年07月18日
 */
@RestController
public class DanmuApi {

    @Autowired
    private DanmuService danmuService;

    @Autowired
    private UserSupport userSupport;

    @GetMapping("/danmus")
    public JsonResponse<List<Danmu>> getDanmus(@RequestParam Long videoId, String startTime, String endTime) throws Exception {
        List<Danmu> list;
        try {
            // 判断是否为登录模式
            userSupport.getCurrentUserId();
            list = danmuService.getDanmus(videoId, startTime, endTime);
        } catch (Exception ignored) {
            // 未登录不允许筛选时间
            list = danmuService.getDanmus(videoId, null, null);
        }

        return new JsonResponse<>(list);
    }
}
