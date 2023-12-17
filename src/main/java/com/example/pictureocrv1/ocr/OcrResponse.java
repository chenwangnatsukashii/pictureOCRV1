package com.example.pictureocrv1.ocr;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Arrays;
@Getter
@NoArgsConstructor
public class OcrResponse {
    int code;
    OcrEntry[] data;
    String msg;
    String hotUpdate;

    @Override
    public String toString() {
        return "OcrResponse{" +
                "code=" + code +
                ", data=" + Arrays.toString(data) +
                ", msg='" + msg + '\'' +
                ", hotUpdate='" + hotUpdate + '\'' +
                '}';
    }

    public OcrResponse(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
