package com.example.pictureocrv1.paddleOCRV1;

import com.example.pictureocrv1.DealDocument;
import com.example.pictureocrv1.utils.IdCardOcrUtils;
import org.apache.poi.xwpf.usermodel.XWPFPicture;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
public class PaddleOcrController {
    @PostMapping("/paddleOCRV1")
    public Map<String, String> ocrTest(@RequestPart("testFile") MultipartFile file) {
        try {
            byte[] bytes = file.getBytes();
            List<XWPFPicture> allPicture = DealDocument.getAllPicture(bytes);

            allPicture.forEach(picture ->{
                byte[] pictureBytes = picture.getPictureData().getData();
                IdCardOcrUtils.getStringStringMap(pictureBytes);
            });

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
            return IdCardOcrUtils.getIDCardBack(bytes);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
