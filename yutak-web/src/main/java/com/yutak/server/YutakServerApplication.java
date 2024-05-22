package com.yutak.server;

import com.yutak.vertx.core.SpringBeanFactory;
import com.yutak.vertx.core.VertxHttpServerConfig;
import com.yutak.vertx.start.ServerBoot;
import io.vertx.core.VertxOptions;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication(scanBasePackages = {"com.yutak.vertx","com.yutak.orm","com.yutak.server"})
@MapperScan("com.yutak.orm.mapper")
public class YutakServerApplication {
        private static final Logger log = LoggerFactory.getLogger(YutakServerApplication.class);

    public static void main(String[] args) {
        VertxHttpServerConfig vertxHttpServerConfig = new VertxHttpServerConfig();
        VertxOptions vertxOptions = new VertxOptions();
        vertxOptions.setEventLoopPoolSize(1);
        vertxOptions.setWorkerPoolSize(1);
//        SpringApplication application = new SpringApplication(YutakServerApplication.class);
//        ConfigurableApplicationContext run = application.run(args);
//        ConfigurableListableBeanFactory beanFactory = run.getBeanFactory();
//        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("applicationContext.xml");
//        // 也许这是spring ioc的启动代码
//        SpringBeanFactory springBeanFactory = applicationContext.getBean(SpringBeanFactory.class);


        SpringApplication app = new SpringApplication(YutakServerApplication.class);
        ConfigurableApplicationContext context = app.run(args);
        SpringBeanFactory springBeanFactory = context.getBean(SpringBeanFactory.class);
//        Demo bean = context.getBean(Demo.class);
//        log.info("bean: {}", bean);
        vertxHttpServerConfig.setBeanFactory(springBeanFactory);
        vertxHttpServerConfig.setHttpPort(10001);
        vertxHttpServerConfig.setBasePackages("com.yutak.server");
        ServerBoot.start(vertxHttpServerConfig, springMvcRouterHandler -> {
            log.info("Server started");
        }, springMvcRouterHandler -> {
            VertxHttpServerConfig serverConfig = springMvcRouterHandler.getHttpServerConfig();
//            ThymeleafTemplateEngine thymeleafTemplateEngine = ThymeleafTemplateEngine.create(serverConfig.getVertx());
//            FreeMarkerTemplateEngine freeMarkerTemplateEngine = FreeMarkerTemplateEngine.create(serverConfig.getVertx());
//            springMvcRouterHandler.registVertxTemplateEngine("myTemplate", "templates/", "html", thymeleafTemplateEngine);
//            springMvcRouterHandler.registVertxTemplateEngine("myTemplate2", "templates/", "ftl", freeMarkerTemplateEngine);
            log.info("Server start after");
        });
//        while (true) {
//            System.out.println("hello world");
//            try {
//                TimeUnit.SECONDS.sleep(2);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//        }


    }

}
