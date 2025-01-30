package com.example.volare.global.common.config;

import com.example.volare.global.apiPayload.exception.handler.AsyncExceptionHandler;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {
    /*
    corePoolSize: 기본적으로 실행할 스레드 개수
    maxPoolSize: 최대 실행 가능 스레드 개수
    queueCapacity: 대기 중인 작업을 저장할 수 있는 큐 크기
    ThreadNamePrefix: 로그에서 식별하기 쉽게 스레드 이름을 지정
     */
    @Bean(name = "mailExecutor")
    public Executor mailExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);   // 기본 스레드 수
        executor.setMaxPoolSize(10);   // 최대 스레드 수
        executor.setQueueCapacity(100); // 작업 대기열 크기
        executor.setThreadNamePrefix("MailExecutor-");
        executor.initialize();
        return executor;
    }

}
