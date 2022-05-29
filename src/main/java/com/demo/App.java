package com.demo;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
@SpringBootApplication
public class App implements InitializingBean {

    @Autowired
    private ApplicationContext applicationContext;

    @PrometheusThreadPool
    @Bean("my-executor")
    public ThreadPoolExecutor executor() {
        return new ThreadPoolExecutor(10, 30, 0, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(50), new ThreadFactory() {

            private final AtomicInteger counter = new AtomicInteger();

            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                thread.setDaemon(true);
                thread.setName("thread-pool-" + counter.getAndIncrement());
                return thread;
            }
        }, new ThreadPoolExecutor.CallerRunsPolicy());
    }

    @Override
    public void afterPropertiesSet() {
        String[] beanNames = applicationContext.getBeanNamesForAnnotation(PrometheusThreadPool.class);
        for (String beanName : beanNames) {
            new CustomerThreadPoolExports((ThreadPoolExecutor) applicationContext.getBean(beanName), beanName).register();
        }
        new Thread(() -> {
            Server server = new Server(18011);
            ServletContextHandler handler = new ServletContextHandler(server, "/");
            handler.addServlet(new ServletHolder(new io.prometheus.client.exporter.MetricsServlet()), "/metrics");
            try {
                server.start();
                server.join();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

}
