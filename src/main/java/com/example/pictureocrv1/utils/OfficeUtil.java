package com.example.pictureocrv1.utils;


import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontProvider;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import fr.opensagres.poi.xwpf.converter.core.BasicURIResolver;
import fr.opensagres.poi.xwpf.converter.core.FileImageExtractor;
import fr.opensagres.poi.xwpf.converter.core.ImageManager;
import fr.opensagres.poi.xwpf.converter.xhtml.XHTMLConverter;
import fr.opensagres.poi.xwpf.converter.xhtml.XHTMLOptions;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.converter.PicturesManager;
import org.apache.poi.hwpf.converter.WordToHtmlConverter;
import org.apache.poi.hwpf.usermodel.PictureType;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Entities;
import org.jsoup.select.Elements;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * 使用poi+itextpdf进行word转pdf
 * 先将word转成html，再将html转成pdf
 *
 * @author ：hewie
 * @date ：Created in 2020/2/27 22:41
 */
public class OfficeUtil {

    /**
     * 将doc格式文件转成html
     *
     * @param docPath  doc文件路径
     * @param imageDir doc文件中图片存储目录
     * @return html
     */
    public static String doc2Html(String docPath, String imageDir) {
        String content = null;
        ByteArrayOutputStream baos = null;
        try {
            HWPFDocument wordDocument = new HWPFDocument(Files.newInputStream(Paths.get(docPath)));
            WordToHtmlConverter wordToHtmlConverter = new WordToHtmlConverter(DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument());
            wordToHtmlConverter.setPicturesManager((content1, pictureType, suggestedName, widthInches, heightInches) -> {
                File file = new File(imageDir + suggestedName);
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(file);
                    fos.write(content1);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (fos != null) {
                            fos.close();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return imageDir + suggestedName;
            });
            wordToHtmlConverter.processDocument(wordDocument);
            Document htmlDocument = wordToHtmlConverter.getDocument();
            DOMSource domSource = new DOMSource(htmlDocument);
            baos = new ByteArrayOutputStream();
            StreamResult streamResult = new StreamResult(baos);

            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer serializer = tf.newTransformer();
            serializer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
            serializer.setOutputProperty(OutputKeys.INDENT, "yes");
            serializer.setOutputProperty(OutputKeys.METHOD, "html");
            serializer.transform(domSource, streamResult);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    content = baos.toString("utf-8");
                    baos.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return content;
    }

    /**
     * 将docx格式文件转成html
     *
     * @param docxPath docx文件路径
     * @param imageDir docx文件中图片存储目录
     * @return html
     */
    public static String docx2Html(String docxPath, String imageDir) {
        String content = null;

        try (FileInputStream in = new FileInputStream(docxPath);
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            XWPFDocument document = new XWPFDocument(in);

            XHTMLOptions options = XHTMLOptions.create();
            options.setExtractor(new FileImageExtractor(new File(imageDir))); // IImageExtractor
            options.URIResolver(new BasicURIResolver(imageDir)); // BasicURIResolver
            // extends FileImageExtractor implements IURIResolver
            options.setImageManager(new ImageManager(new File(imageDir),"/imageSubDir/"));
            options.setIgnoreStylesIfUnused(false);
            options.setFragment(true);

            XHTMLConverter.getInstance().convert(document, baos, options);
            content = baos.toString("utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return content;
    }

    /**
     * 使用jsoup规范化html
     *
     * @param html html内容
     * @return 规范化后的html
     */
    private static String formatHtml(String html) {
        org.jsoup.nodes.Document doc = Jsoup.parse(html);
        // 去除过大的宽度
        String style = doc.attr("style");
        if (StringUtils.isNotEmpty(style) && style.contains("width")) {
            doc.attr("style", "");
        }
        Elements divs = doc.select("div");
        for (Element div : divs) {
            String divStyle = div.attr("style");
            if (StringUtils.isNotEmpty(divStyle) && divStyle.contains("width")) {
                div.attr("style", "");
            }
        }
        // jsoup生成闭合标签
        doc.outputSettings().syntax(org.jsoup.nodes.Document.OutputSettings.Syntax.xml);
        doc.outputSettings().escapeMode(Entities.EscapeMode.xhtml);
        return doc.html();
    }


    /**
     * html转成pdf
     *
     * @param html          html
     * @param outputPdfPath 输出pdf路径
     */
    public static void htmlToPdf(String html, String outputPdfPath) {
        com.itextpdf.text.Document document = null;
        try (ByteArrayInputStream bais = new ByteArrayInputStream(html.getBytes())) {
            // 纸
            document = new com.itextpdf.text.Document(PageSize.A4);
            // 笔
            PdfWriter writer = PdfWriter.getInstance(document, Files.newOutputStream(Paths.get(outputPdfPath)));
            document.open();

            XMLWorkerHelper.getInstance().parseXHtml(writer, document, bais,
                    StandardCharsets.UTF_8, new FontProvider() {
                        @Override
                        public boolean isRegistered(String s) {
                            return false;
                        }

                        @Override
                        public Font getFont(String s, String s1, boolean embedded, float size, int style, BaseColor baseColor) {
                            // 配置字体
                            Font font = null;
                            try {
                                // 方案一：使用本地字体(本地需要有字体)
//                              BaseFont bf = BaseFont.createFont("c:/Windows/Fonts/simsun.ttc,0", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
//                                BaseFont bf = BaseFont.createFont("C:/Windows/Fonts/seguisym.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
                                // 方案二：使用jar包：iTextAsian，这样只需一个jar包就可以了
                                BaseFont bf = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.EMBEDDED);
                                font = new Font(bf, size, style, baseColor);
                                font.setColor(baseColor);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            return font;
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (document != null) {
                document.close();
            }

        }
    }

    public static void main(String[] args) {

        // 测试doc转pdf
//        String docHtml = doc2Html(docPath, imageDir);
//        docHtml = formatHtml(docHtml);
//        htmlToPdf(docHtml, pdfPath);

        // 测试docx转pdf
        String docxHtml = docx2Html(DealFileUtils.inPath, "/Users/lmc10213/Desktop/pictureOCR/");
        docxHtml = formatHtml(docxHtml);
        htmlToPdf(docxHtml, DealFileUtils.outPath);

    }
}


