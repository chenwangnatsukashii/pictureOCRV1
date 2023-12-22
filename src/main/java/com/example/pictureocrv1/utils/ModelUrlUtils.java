package com.example.pictureocrv1.utils;

import com.example.pictureocrv1.ocr.OcrCPP;
import com.example.pictureocrv1.ocr.OcrProperties;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Paths;

/**
 * @author sy
 * @date 2022/9/15 22:42
 */
@UtilityClass
@Slf4j
public class ModelUrlUtils {

    /**
     * 获取模型url，如果是http或file开头，直接返回
     *
     * @param name 模型名称
     * @return url
     */
    @SneakyThrows
    public String getRealUrl(String name) {
        URI uri = new ClassPathResource(name).getURI();
        System.out.println("model uri of " + name + " is " + uri);
        if (uri.toString().startsWith("jar:")) {

            // 获取当前Jar包所在目录
            String jarPath = OcrCPP.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
            String jarPathParent = new File(jarPath).getParent();

            // 构建exe文件的路径
            String exeFilePath = Paths.get(jarPathParent, new OcrProperties().getOcrExe()).toString();
            System.out.println(exeFilePath);

            return exeFilePath;
        }

        return uri.toString();
    }

}
