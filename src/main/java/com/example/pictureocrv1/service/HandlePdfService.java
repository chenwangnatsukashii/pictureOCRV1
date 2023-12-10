package com.example.pictureocrv1.service;

import com.example.pictureocrv1.dto.OutputDTO;
import com.example.pictureocrv1.dto.PageOutputDTO;
import com.example.pictureocrv1.utils.IdCardOcrUtils;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.EventType;
import com.itextpdf.kernel.pdf.canvas.parser.PdfCanvasProcessor;
import com.itextpdf.kernel.pdf.canvas.parser.data.IEventData;
import com.itextpdf.kernel.pdf.canvas.parser.data.ImageRenderInfo;
import com.itextpdf.kernel.pdf.canvas.parser.listener.IEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class HandlePdfService extends HandleFile {

    private static PageOutputDTO pageOutputDTO;
    private final PdfDocument pdfDoc;

    public HandlePdfService(String inputFile) throws IOException {
        pdfDoc = new PdfDocument(new PdfReader(inputFile));
    }

    public void close() {
        this.pdfDoc.close();
    }

    public List<PageOutputDTO> getAllPicture() {

        List<PageOutputDTO> pdfOutputDTOList = new ArrayList<>(128);
        int pageNum = 1;
        int totalNum = pdfDoc.getNumberOfPages();
        System.out.println("总页数：" + totalNum);

        for (int i = 1; i <= totalNum; i++) {
            pageOutputDTO = new PageOutputDTO();
            pageOutputDTO.setImageDataList(new ArrayList<>());

            PdfPage page = pdfDoc.getPage(i);
            PdfCanvasProcessor processor = new PdfCanvasProcessor(new HandlePdfService.ImageEventListener());
            processor.processPageContent(page);

            System.out.println("Page " + pageNum + " processed.");
            pageOutputDTO.setPageNum(pageNum++);
            pdfOutputDTOList.add(pageOutputDTO);
        }

        return pdfOutputDTOList;
    }


    static class ImageEventListener implements IEventListener {
        @Override
        public void eventOccurred(IEventData data, EventType type) {
            if (type == EventType.RENDER_IMAGE) {
                ImageRenderInfo renderInfo = (ImageRenderInfo) data;
                byte[] imageData = renderInfo.getImage().getImageBytes();
                pageOutputDTO.getImageDataList().add(imageData);
            }
        }

        @Override
        public Set<EventType> getSupportedEvents() {
            return null; // 只处理图像事件，其他事件忽略
        }
    }
}
