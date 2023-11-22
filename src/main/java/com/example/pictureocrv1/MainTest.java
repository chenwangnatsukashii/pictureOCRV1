package com.example.pictureocrv1;

import com.example.pictureocrv1.view.MainFrame;
import com.lijinjiang.beautyeye.BeautyEyeLNFHelper;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import net.sourceforge.tess4j.util.LoadLibs;

import java.io.File;

public class MainTest {

//    public static void main(String[] args) {
//        try {
//            /**
//             * 设置本属性将改变窗口边框样式定义
//             * 系统默认样式 : osLookAndFeelDecorated
//             * 强立体半透明 : translucencyAppleLike
//             * 弱立体半透明 : translucencySmallShadow
//             * 普通不透明 : generalNoTranslucencyShadow
//             */
//            BeautyEyeLNFHelper.frameBorderStyle = BeautyEyeLNFHelper.FrameBorderStyle.generalNoTranslucencyShadow;
//            BeautyEyeLNFHelper.launchBeautyEyeLNF();
//            // 初始化主窗口
//            new MainFrame();
//        } catch (Exception e) {
//            e.printStackTrace();
//            System.out.println(e.getMessage());
//        }
//    }

    public static void main(String[] args) {

        File tmpFolder = LoadLibs.extractTessResources("win32-x86-64");

        System.setProperty("java.library.path", tmpFolder.getPath());


        String projectPath = System.getProperty("user.dir");
        System.out.println(projectPath);

        String imagePath = "src/main/resources/image/WechatIMG147.jpg";
        File imageFile = new File(projectPath, imagePath);
        System.out.println(imageFile.exists());

        ITesseract instance = new Tesseract();
        instance.setDatapath("/Users/lmc10213/code/pictureOCRV1/src/main/resources/data/");
        instance.setLanguage("chi_sim");
        try {
            String result = instance.doOCR(imageFile);
            System.out.println(result);
        } catch (TesseractException e) {
            System.err.println(e.getMessage());
        }
    }

}
