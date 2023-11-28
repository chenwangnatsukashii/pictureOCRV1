package com.example.pictureocrv1.paddleOCRV1;

import com.example.pictureocrv1.utils.IdCardOcrUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
public class PaddleOcrTest {
    @PostMapping("/paddleOCRV1")
    public Map<String, String> ocrTest(MultipartFile file) {
        try {
            byte[] bytes = file.getBytes();
            return IdCardOcrUtils.getStringStringMap(bytes);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 身份证反面识别功能
     *
     * @param file 传入的文件
     * @return 身份证反面信息，Map集合，包括身份证反面的：签发机关、有效期限
     */
    @PostMapping("/ocrBack")
    public Map<String, String> ocrBack(@RequestPart("testFile") MultipartFile file) {
        try {
            byte[] bytes = file.getBytes();
            // 这里可以考虑将前端页面上传的文件保存到文件夹中，返回图片的访问地址给前端。
            // 也可考虑转成base64位，把base64位返回给前端。
            // 或者在前端那里直接将图片转base64位，然后将base64位的图片赋值到对应的字段中，提交后保存到数据库中
            // 然后用户在前端点击提交用户信息时，将对应的信息保存到数据库中
            //  fanmianInfo.put("imgUrl", "图片的访问地址或者图片的base64位");
            return IdCardOcrUtils.getIDCardBack(bytes);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @PostMapping("/test")
    public void test() {
        System.out.println(1111111111);
    }


}
