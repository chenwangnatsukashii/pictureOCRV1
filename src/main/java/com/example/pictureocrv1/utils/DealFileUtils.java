package com.example.pictureocrv1.utils;

import com.documents4j.api.DocumentType;
import com.documents4j.api.IConverter;
import com.documents4j.job.LocalConverter;
import fr.opensagres.poi.xwpf.converter.pdf.PdfConverter;
import fr.opensagres.poi.xwpf.converter.pdf.PdfOptions;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.docx4j.Docx4J;
import org.docx4j.convert.out.FOSettings;
import org.docx4j.fonts.IdentityPlusMapper;
import org.docx4j.fonts.Mapper;
import org.docx4j.fonts.PhysicalFont;
import org.docx4j.fonts.PhysicalFonts;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class DealFileUtils {


    public final static String inPath = "/Users/lmc10213/Desktop/pictureOCR/projectManage.docx";
    public final static String outPath = "/Users/lmc10213/Desktop/pictureOCR/projectManage-2.pdf";

    public static void main(String[] args) throws Exception {
        wordToPdfPOI();
    }

    // no lcmm in java.library.path
    public static void wordToPdfPOI() {
        try (FileInputStream fileInputStream = new FileInputStream(inPath);
             FileOutputStream fileOutputStream = new FileOutputStream(outPath)) {

            XWPFDocument xwpfDocument = new XWPFDocument(fileInputStream);
            PdfOptions pdfOptions = PdfOptions.create();

            PdfConverter.getInstance().convert(xwpfDocument, fileOutputStream, pdfOptions);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 可以转PDF，但是格式有问题
    public static void wordToPdfDocx4JV1() throws Exception {
        WordprocessingMLPackage mlPackage = Docx4J.load(new File(inPath));
        mlPackage.setFontMapper(getFontMapper());
        Docx4J.toPDF(mlPackage, Files.newOutputStream(Paths.get(outPath)));
    }

    public static void wordToPdfDocx4JV2() throws Exception {
        WordprocessingMLPackage mlPackage = WordprocessingMLPackage.load(new File(inPath));
        mlPackage.setFontMapper(getFontMapper());
        Docx4J.toPDF(mlPackage, Files.newOutputStream(Paths.get(outPath)));
    }

    public static void wordToPdfDocx4JV3() throws Exception {
        WordprocessingMLPackage mlPackage = WordprocessingMLPackage.load(new File(inPath));

        FOSettings foSettings = Docx4J.createFOSettings();
        foSettings.setWmlPackage(mlPackage);

        FileOutputStream outputStream = new FileOutputStream(outPath);
        Docx4J.toFO(foSettings, outputStream, Docx4J.FLAG_NONE);

        outputStream.flush();
        outputStream.close();
    }

    // 需要调用本地的word
    public static void wordToPdfDocuments4j() {
        File inputWord = new File(inPath);
        File outputFile = new File(outPath);
        try (InputStream docxInputStream = Files.newInputStream(inputWord.toPath()); OutputStream outputStream = Files.newOutputStream(outputFile.toPath())) {

            IConverter converter = LocalConverter.builder().build();
            converter.convert(docxInputStream).as(DocumentType.DOCX).to(outputStream).as(DocumentType.PDF).execute();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void setFontMapper(WordprocessingMLPackage mlPackage) throws Exception {
        Mapper fontMapper = new IdentityPlusMapper();
        //加载字体文件（解决linux环境下无中文字体问题）
        if (PhysicalFonts.get("SimSun") == null) {
            System.out.println("加载本地SimSun字体库");
//          PhysicalFonts.addPhysicalFonts("SimSun", WordUtils.class.getResource("/fonts/SIMSUN.TTC"));
        }
        fontMapper.put("隶书", PhysicalFonts.get("LiSu"));
        fontMapper.put("宋体", PhysicalFonts.get("SimSun"));
        fontMapper.put("微软雅黑", PhysicalFonts.get("Microsoft Yahei"));
        fontMapper.put("黑体", PhysicalFonts.get("SimHei"));
        fontMapper.put("楷体", PhysicalFonts.get("KaiTi"));
        fontMapper.put("新宋体", PhysicalFonts.get("NSimSun"));
        fontMapper.put("华文行楷", PhysicalFonts.get("STXingkai"));
        fontMapper.put("华文仿宋", PhysicalFonts.get("STFangsong"));
        fontMapper.put("仿宋", PhysicalFonts.get("FangSong"));
        fontMapper.put("幼圆", PhysicalFonts.get("YouYuan"));
        fontMapper.put("华文宋体", PhysicalFonts.get("STSong"));
        fontMapper.put("华文中宋", PhysicalFonts.get("STZhongsong"));
        fontMapper.put("等线", PhysicalFonts.get("SimSun"));
        fontMapper.put("等线 Light", PhysicalFonts.get("SimSun"));
        fontMapper.put("华文琥珀", PhysicalFonts.get("STHupo"));
        fontMapper.put("华文隶书", PhysicalFonts.get("STLiti"));
        fontMapper.put("华文新魏", PhysicalFonts.get("STXinwei"));
        fontMapper.put("华文彩云", PhysicalFonts.get("STCaiyun"));
        fontMapper.put("方正姚体", PhysicalFonts.get("FZYaoti"));
        fontMapper.put("方正舒体", PhysicalFonts.get("FZShuTi"));
        fontMapper.put("华文细黑", PhysicalFonts.get("STXihei"));
        fontMapper.put("宋体扩展", PhysicalFonts.get("simsun-extB"));
        fontMapper.put("仿宋_GB2312", PhysicalFonts.get("FangSong_GB2312"));
        fontMapper.put("新細明體", PhysicalFonts.get("SimSun"));
        //解决宋体（正文）和宋体（标题）的乱码问题
        PhysicalFonts.put("PMingLiU", PhysicalFonts.get("SimSun"));
        PhysicalFonts.put("新細明體", PhysicalFonts.get("SimSun"));
        PhysicalFont simSunFont = PhysicalFonts.get("SimSun"); // 宋体&新宋体
        fontMapper.put("SimSun", simSunFont);
        mlPackage.setFontMapper(fontMapper); // 设置字体
    }

    private static Mapper getFontMapper() {
        Mapper fontMapper = new IdentityPlusMapper();
        fontMapper.put("隶书", PhysicalFonts.get("LiSu"));
        fontMapper.put("宋体", PhysicalFonts.get("SimSun"));
        fontMapper.put("微软雅黑", PhysicalFonts.get("Microsoft Yahei"));
        fontMapper.put("黑体", PhysicalFonts.get("SimHei"));
        fontMapper.put("楷体", PhysicalFonts.get("KaiTi"));
        fontMapper.put("新宋体", PhysicalFonts.get("NSimSun"));
        fontMapper.put("华文行楷", PhysicalFonts.get("STXingkai"));
        fontMapper.put("华文仿宋", PhysicalFonts.get("STFangsong"));
        fontMapper.put("仿宋", PhysicalFonts.get("FangSong"));
        fontMapper.put("幼圆", PhysicalFonts.get("YouYuan"));
        fontMapper.put("华文宋体", PhysicalFonts.get("STSong"));
        fontMapper.put("华文中宋", PhysicalFonts.get("STZhongsong"));
        fontMapper.put("等线", PhysicalFonts.get("SimSun"));
        fontMapper.put("等线 Light", PhysicalFonts.get("SimSun"));
        fontMapper.put("华文琥珀", PhysicalFonts.get("STHupo"));
        fontMapper.put("华文隶书", PhysicalFonts.get("STLiti"));
        fontMapper.put("华文新魏", PhysicalFonts.get("STXinwei"));
        fontMapper.put("华文彩云", PhysicalFonts.get("STCaiyun"));
        fontMapper.put("方正姚体", PhysicalFonts.get("FZYaoti"));
        fontMapper.put("方正舒体", PhysicalFonts.get("FZShuTi"));
        fontMapper.put("华文细黑", PhysicalFonts.get("STXihei"));
        fontMapper.put("宋体扩展", PhysicalFonts.get("simsun-extB"));
        fontMapper.put("仿宋_GB2312", PhysicalFonts.get("FangSong_GB2312"));
        fontMapper.put("新細明體", PhysicalFonts.get("SimSun"));
        return fontMapper;
    }


}
