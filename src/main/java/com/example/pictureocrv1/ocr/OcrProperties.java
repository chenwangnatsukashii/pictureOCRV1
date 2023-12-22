package com.example.pictureocrv1.ocr;

import lombok.Data;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class OcrProperties {
    private String ocrExe = "Paddle_CPP/PaddleOCR_json.exe";
}
