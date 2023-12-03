package com.example.pictureocrv1;

import org.apache.poi.xwpf.usermodel.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


public class DealDocument {

    // 创建Word文档对象

    public static List<XWPFPicture> getAllPicture(byte[] documentByte) throws IOException {
        InputStream inputStream = new ByteArrayInputStream(documentByte);
        XWPFDocument document = new XWPFDocument(inputStream);
        // 获取文档中的所有图片
        List<XWPFPicture> pictures = new ArrayList<>();

        document.getParagraphs()
                .forEach(paragraph -> paragraph.getRuns()
                        .forEach(run -> pictures.addAll(run.getEmbeddedPictures())));

        for (XWPFTable table : document.getTables()) {
            for (XWPFTableRow row : table.getRows()) {
                for (XWPFTableCell cell : row.getTableCells()) {
                    for (XWPFParagraph paragraph : cell.getParagraphs()) {
                        for (XWPFRun run : paragraph.getRuns()) {
                            pictures.addAll(run.getEmbeddedPictures());
                        }
                    }
                }
            }
        }

        System.out.println("图片个数： " + pictures.size());
        return pictures;
    }
}
