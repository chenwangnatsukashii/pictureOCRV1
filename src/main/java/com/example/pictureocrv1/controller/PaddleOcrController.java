package com.example.pictureocrv1.controller;

import com.example.pictureocrv1.DealDocument;
import com.example.pictureocrv1.dto.PictureDTO;
import com.example.pictureocrv1.utils.IdCardOcrUtils;
import org.apache.poi.xwpf.usermodel.XWPFPicture;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
public class PaddleOcrController {
    @PostMapping("/paddleOCRV1")
    public List<PictureDTO> ocrTest(@RequestPart("testFile") MultipartFile file) {
        List<PictureDTO> pictureDTOList = new ArrayList<>();
        try {
            byte[] bytes = file.getBytes();
            List<XWPFPicture> allPicture = DealDocument.getAllPicture(bytes);
            allPicture.forEach(picture -> {
                byte[] pictureBytes = picture.getPictureData().getData();
                PictureDTO pictureDTO = new PictureDTO();
                pictureDTO.setImageData(pictureBytes);
                pictureDTOList.add(pictureDTO);
                IdCardOcrUtils.getStringStringMap(pictureDTO, null);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        return pictureDTOList;
    }
}
