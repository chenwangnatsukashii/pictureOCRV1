package com.example.pictureocrv1.ocr;

import lombok.Data;

import java.util.Arrays;

@Data
public class OcrEntry {

    private String text;
    private int[][] box;
    private double score;

    @Override
    public String toString() {
        return "RecognizedText{" +
                "text='" + text + '\'' +
                ", box=" + Arrays.toString(box) +
                ", score=" + score +
                '}';
    }
}
