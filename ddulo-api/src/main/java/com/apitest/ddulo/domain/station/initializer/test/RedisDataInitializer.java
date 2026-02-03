//package com.apitest.ddulo.domain.station.initializer.test;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.context.annotation.Profile;
//import org.springframework.core.io.Resource;
//import org.springframework.core.io.ResourceLoader;
//import org.springframework.core.io.support.ResourcePatternUtils;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.stereotype.Component;
//
//import java.nio.charset.StandardCharsets;
//import java.nio.file.Files;
//import java.nio.file.Paths;
//
//@Component
//@Profile("dev")
//@RequiredArgsConstructor
//public class RedisDataInitializer implements CommandLineRunner {
//
//    private final RedisTemplate<String, Object> redisTemplate;
//    private final ResourceLoader resourceLoader;
//
//    @Override
//    public void run(String... args) throws Exception {
//        Resource[] resources = ResourcePatternUtils
//                .getResourcePatternResolver(resourceLoader)
//                .getResources("classpath:dummy/*.json");
//
//        for (Resource res : resources) {
//            String filename = res.getFilename();
//
//            if(filename != null && filename.contains(".json")){
//                String redisKey = filename
//                        .substring(0, filename.lastIndexOf(".json"))
//                        .replace("_", ":");
//                String content = new String(Files.readAllBytes(Paths.get(res.getURI())), StandardCharsets.UTF_8);
//                redisTemplate.opsForValue().set(redisKey, content);
//
//                System.out.println("@@@@@@@Redis 적재 완료 : " + redisKey);
//            }
//            else {
//                System.out.println("@@@@@@@Redis 적재 실패 : " + filename);
//            }
//        }
//    }
//}
