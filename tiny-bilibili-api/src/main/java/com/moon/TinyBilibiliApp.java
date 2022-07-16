package com.moon;

import com.moon.tinybilibili.service.websocket.WebSocketService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

/**
 * @author Chanmoey
 * @date 2022年07月06日
 */

@SpringBootApplication
public class TinyBilibiliApp {

    public static void main(String[] args) {
        ApplicationContext app = SpringApplication.run(
                TinyBilibiliApp.class, args
        );
        WebSocketService.setApplicationContext(app);
    }
}
