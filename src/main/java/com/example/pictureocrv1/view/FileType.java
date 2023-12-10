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

    public static FileType getFileType(String filePath) {
        String[] fileTypeArray = filePath.split("\\.");
        String fileType = fileTypeArray[fileTypeArray.length - 1];

        switch (fileType) {
            case "jpg":
                return JPG;
            case "docx":
                return DOCX;
            case "pdf":
                return PDF;

        }
        return null;
    }

}
