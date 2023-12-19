package com.example.pictureocrv1;

import com.example.pictureocrv1.view.MainFrame;
import com.lijinjiang.beautyeye.BeautyEyeLNFHelper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.core.SpringVersion;
import org.springframework.boot.SpringBootVersion;

@SpringBootApplication
public class PictureOcrv1Application {

    public static void main(String[] args) throws Exception {

        SpringApplicationBuilder builder = new SpringApplicationBuilder(MainFrame.class);
        builder.headless(false).web(WebApplicationType.NONE).run(args);

//        SpringApplication.run(PictureOcrv1Application.class, args);
//        BeautyEyeLNFHelper.frameBorderStyle = BeautyEyeLNFHelper.FrameBorderStyle.generalNoTranslucencyShadow;
//        BeautyEyeLNFHelper.launchBeautyEyeLNF();
//        // 初始化主窗口
//        new MainFrame();
//        String versionSpring = SpringVersion.getVersion();
//        String versionSpringBoot = SpringBootVersion.getVersion();
//        System.out.println("Spring Version：" + versionSpring);
//        System.out.println("SpringBoot Version：" + versionSpringBoot);

    }

}
