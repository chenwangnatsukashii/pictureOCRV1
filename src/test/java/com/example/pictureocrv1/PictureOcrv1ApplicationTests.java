package com.example.pictureocrv1;

import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringBootVersion;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.SpringVersion;

@SpringBootTest
class PictureOcrv1ApplicationTests {

    @Test
    void contextLoads() {
        String versionSpring = SpringVersion.getVersion();
        String versionSpringBoot = SpringBootVersion.getVersion();
        System.out.println("Spring Version：" + versionSpring);
        System.out.println("SpringBoot Version：" + versionSpringBoot);
    }

}
