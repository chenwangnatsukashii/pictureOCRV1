package com.example.pictureocrv1;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.EventType;
import com.itextpdf.kernel.pdf.canvas.parser.PdfCanvasProcessor;
import com.itextpdf.kernel.pdf.canvas.parser.data.IEventData;
import com.itextpdf.kernel.pdf.canvas.parser.data.ImageRenderInfo;
import com.itextpdf.kernel.pdf.canvas.parser.listener.IEventListener;

import java.util.Set;

public class ImageExtractor {
    public static void main(String[] args) {
        String inputFile = "/Users/lmc10213/Desktop/pictureOCR/projectManage.pdf";
        try (PdfDocument pdfDoc = new PdfDocument(new PdfReader(inputFile))) {
            int pageNum = 1;
            int totalNum = pdfDoc.getNumberOfPages();
            System.out.println("总页数：" + totalNum);

            for (int i = 1; i <= totalNum; i++) {
                PdfPage page = pdfDoc.getPage(i);
                PdfCanvasProcessor processor = new PdfCanvasProcessor(new ImageEventListener());
                processor.processPageContent(page);
                System.out.println("Page " + pageNum + " processed.");
                pageNum++;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static class ImageEventListener implements IEventListener {
        @Override
        public void eventOccurred(IEventData data, EventType type) {
            if (type == EventType.RENDER_IMAGE) {
                ImageRenderInfo renderInfo = (ImageRenderInfo) data;
                byte[] imageData = renderInfo.getImage().getImageBytes();
                System.out.println(imageData.length);
            }
        }

        @Override
        public Set<EventType> getSupportedEvents() {
            return null; // 只处理图像事件，其他事件忽略
        }
    }
}