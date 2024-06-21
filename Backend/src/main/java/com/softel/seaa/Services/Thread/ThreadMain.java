package com.softel.seaa.Services.Thread;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class ThreadMain {

    @Bean(name = "saveLog")
    public Executor saveLog(){
        return executor("saveLog");
    }

    @Bean(name = "notification")
    public Executor notification(){
        return executor("notification");
    }


    private Executor executor(String text){
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix(text);
        executor.initialize();

        return executor;
    }
}
