package com.example.pictureocrv1;

import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTP;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageSz;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSectPr;

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

    public static List<XWPFPicture> getAllPicture(byte[] documentByte) {
        // 获取文档中的所有图片
        List<XWPFPicture> pictures = new ArrayList<>();
        try (InputStream inputStream = new ByteArrayInputStream(documentByte)) {
            XWPFDocument document = new XWPFDocument(inputStream);

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
        } catch (IOException e) {
            e.printStackTrace();
        }
        return pictures;
    }


    public static int getTotalPage(byte[] documentByte) throws IOException {
        InputStream inputStream = new ByteArrayInputStream(documentByte);
        XWPFDocument document = new XWPFDocument(inputStream);
        int currentPage = document.getProperties().getExtendedProperties().getUnderlyingProperties().getPages();
        System.out.println(currentPage);

        return currentPage;
    }


}
