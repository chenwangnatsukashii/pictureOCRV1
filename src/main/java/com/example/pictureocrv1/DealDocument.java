package com.example.pictureocrv1;

import org.apache.poi.xwpf.usermodel.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;


public class DealDocument {

    // 创建Word文档对象

    public static void main(String[] args) throws IOException {
//        XWPFDocument document = new XWPFDocument(Files.newInputStream(Paths.get("path/to/word.docx")));
//        // 获取文档中的所有图片
//        List<CTPicture> pictures = new ArrayList<>();
//
//
//        document.getParagraphs()
//                .forEach(paragraph -> paragraph.getRuns()
//                .forEach(run -> pictures.addAll(run.getEmbeddedPictures())));
//
//        for (XWPFTable table : document.getTables()) {
//            for (XWPFTableRow row : table.getRows()) {
//                for (XWPFTableCell cell : row.getTableCells()) {
//                    for (XWPFParagraph paragraph : cell.getParagraphs()) {
//                        for (XWPFRun run : paragraph.getRuns()) {
//                            pictures.addAll(run.getEmbeddedPictures());
//                        }
//                    }
//                }
//            }
//        }
    }
}
