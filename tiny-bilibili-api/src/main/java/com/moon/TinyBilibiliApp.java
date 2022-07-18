package com.moon;

import com.moon.tinybilibili.service.websocket.WebSocketService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author Chanmoey
 * @date 2022年07月06日
 */

@SpringBootApplication
@EnableTransactionManagement
@EnableAsync
@EnableScheduling
public class TinyBilibiliApp {

    public static void main(String[] args) {
        ApplicationContext app = SpringApplication.run(
                TinyBilibiliApp.class, args
        );
        WebSocketService.setApplicationContext(app);
    }
}
