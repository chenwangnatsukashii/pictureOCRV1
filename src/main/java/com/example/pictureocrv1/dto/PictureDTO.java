package com.example.pictureocrv1.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class PictureDTO implements Serializable {
    private byte[] imageData;
    private String pictureInfo;
    private List<OutputDTO> outputDTOList;
}
