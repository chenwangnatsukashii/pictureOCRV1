package com.example.pictureocrv1.view;

import lombok.Getter;
import lombok.Setter;

@Getter
public enum FileType {
    JPG("jpg"), PNG("png"), DOCX("docx"), PDF("pdf");

    FileType(String name) {
        this.name = name;
    }

    private String name;

}
