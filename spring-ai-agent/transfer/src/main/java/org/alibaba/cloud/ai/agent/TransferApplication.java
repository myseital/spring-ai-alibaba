package org.alibaba.cloud.ai.agent;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;

import java.util.Properties;

/**
 *
 * @author myseital
 * @date 2026/4/13
 */
@MapperScan({"org.alibaba.cloud.ai.agent.mapper", "org.alibaba.cloud.ai.agent.dao"})
@SpringBootApplication
public class TransferApplication {
    public static void main(String[] args) {

        ConfigurableApplicationContext context = SpringApplication.run(TransferApplication.class, args);
//        // 从项目根目录加载.env
//        try {
//            Resource resource = new DefaultResourceLoader().getResource("classpath:.env");
//            if (resource.exists()) {
//                Properties ENV_PROPS = new Properties();
//                ENV_PROPS.load(resource.getInputStream());
//                System.out.println("✅ .env 配置加载成功！");
//            } else {
//                System.out.println("⚠️ 项目根目录未找到 .env 文件，使用默认配置");
//            }
//        } catch (Exception e) {
//            throw new RuntimeException("❌ .env 配置加载失败", e);
//        }
//
//        Environment environment = context.getEnvironment();
//
//        System.out.println(environment.getProperty("zz"));
    }
}
