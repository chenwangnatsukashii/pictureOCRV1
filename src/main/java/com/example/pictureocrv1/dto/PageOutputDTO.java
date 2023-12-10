package com.example.pictureocrv1.dto;

import lombok.Data;

import java.util.List;

@Data
public class PageOutputDTO {
    private int pageNum;
    private List<byte[]> imageDataList;
}
