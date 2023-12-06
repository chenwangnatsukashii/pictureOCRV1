package com.example.pictureocrv1.paddleOCRV1;

import com.example.pictureocrv1.DealDocument;
import com.example.pictureocrv1.dto.OutputDTO;
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
    public List<OutputDTO> ocrTest(@RequestPart("testFile") MultipartFile file) {
        try {
            byte[] bytes = file.getBytes();
            List<XWPFPicture> allPicture = DealDocument.getAllPicture(bytes);

            allPicture.forEach(picture -> {
                byte[] pictureBytes = picture.getPictureData().getData();
                IdCardOcrUtils.getStringStringMap(pictureBytes);
            });

            return IdCardOcrUtils.getStringStringMap(bytes);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
