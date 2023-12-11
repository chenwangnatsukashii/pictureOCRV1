package com.example.pictureocrv1.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OutputDTO implements Serializable {
    private String detailInfo;
    private boolean warningFlag;
    private boolean recognizeFlag;
}
