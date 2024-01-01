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
        String exePath = System.getProperty("exe.path");
        System.out.println("exePath: " + exePath);
        if (exePath == null) {
            exePath = ModelUrlUtils.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
            exePath = new File(exePath).getParentFile().getParent() +
                    File.separator + "out" + File.separator + "artifacts" + File.separator + "pictureOCRV1_jar" + File.separator;
        }
        return exePath + name;
    }

}
