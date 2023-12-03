package com.example.pictureocrv1.view;

import com.example.pictureocrv1.DealDocument;
import com.example.pictureocrv1.paddleOCRV1.PaddleOcrController;
import com.example.pictureocrv1.utils.IdCardOcrUtils;
import com.lijinjiang.beautyeye.ch3_button.BEButtonUI;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import org.apache.poi.xwpf.usermodel.XWPFPicture;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;

import static com.example.pictureocrv1.view.Example.*;

/**
 * @ClassName MainFrame
 * @Description TODO
 * @Author Li
 * @Date 2022/10/20 23:19
 * @ModifyDate 2022/10/20 23:19
 * @Version 1.0
 */
public class MainFrame extends JFrame {
    private JComboBox<String> languageCombobox;
    private Map<String, String> languageMap;
    private JSplitPane splitPane;

    private JPanel picturePanel;
    protected JLabel previewLabel;//预览标签
    private JTextArea resultArea;
    protected BufferedImage ocrImage;
    private String filePath;
    private String language = "chi_sim";
    private final String tempImage = MainFrame.class.getResource("/").getPath() + "img/temp.jpg";

    @Resource
    private PaddleOcrController paddleOcrController;

    public MainFrame() {
        this.setTitle("文档图片检测");
        initLanguageMap();
        createOperatePanel();
        createSplitPane();
        this.setSize(new Dimension(1280, 880));
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
        splitPane.setDividerLocation(0.6); // 必须放在设置可见之后
    }

    // 初始化下拉语言包
    private void initLanguageMap() {
        languageMap = new HashMap<>();
        languageMap.put("中文", "chi_sim");
        languageMap.put("英文", "eng");
        languageMap.put("日文", "jpn");
    }

    // 初始化操作区
    private void createOperatePanel() {
        JPanel operatePanel = new JPanel();
        operatePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        operatePanel.setPreferredSize(new Dimension(1240, 56)); // 主要是设置高度
        operatePanel.setLayout(new GridLayout(1, 6, 10, 10));

        // 打开按钮
        JButton openBtn = new JButton("打开");
        openBtn.setFocusable(false);
        openBtn.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.green));
        openBtn.addActionListener(e -> showChooseFileDialog());

        // 截图按钮
        JButton screenshotBtn = new JButton("截图");
        screenshotBtn.setFocusable(false);
        screenshotBtn.addActionListener(e -> {
            this.setVisible(false);//设置不可见，进入截图状态
            new ScreenshotWindow(this);//创建截图窗口
        });

        // 语言选择下拉框
        languageCombobox = new JComboBox<>();
        Set<String> keys = languageMap.keySet();
        for (String key : keys) {
            languageCombobox.addItem(key);
        }
        languageCombobox.setSelectedItem("中文");
        languageCombobox.addActionListener(e -> {
            // 选择对应语言库赋值
            language = languageMap.get(Objects.requireNonNull(languageCombobox.getSelectedItem()).toString());
        });

        // 灰度化+二值化处理图片
        JButton processBtn = new JButton("处理图片");
        processBtn.setFocusable(false);
        processBtn.addActionListener(e -> processImage(ocrImage));

        // 执行识别按钮
        JButton executeBtn = new JButton("识别");
        executeBtn.setFocusable(false);
        executeBtn.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.green));
        executeBtn.addActionListener(e -> execute(ocrImage));

        operatePanel.add(openBtn);
        operatePanel.add(screenshotBtn);
        operatePanel.add(languageCombobox);
        operatePanel.add(processBtn);
        operatePanel.add(executeBtn);
        this.add(operatePanel, BorderLayout.NORTH);
    }

    // 初始化显示区
    private void createSplitPane() {
        splitPane = new JSplitPane();

        // 设置左边预览面板
        picturePanel = new JPanel();
        picturePanel.setLayout(new GridLayout(5, 1));
        JScrollPane leftPane = new JScrollPane(picturePanel);
        leftPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        leftPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        leftPane.setBorder(null);//设置边框为空
        splitPane.setLeftComponent(leftPane);

        // 设置右边结果显示面板
        resultArea = new JTextArea();
        resultArea.setFont(new Font("", Font.BOLD, 20));
        resultArea.setBorder(null);
        resultArea.setEditable(false);//设置结果区域不可编辑
//        resultArea.setLineWrap(true);
        JScrollPane rightPane = new JScrollPane(resultArea);
        rightPane.setBorder(null);//设置边框为空
        splitPane.setRightComponent(rightPane);
        this.add(splitPane, BorderLayout.CENTER);
    }

    //显示预览图片
    public void setPreviewImage(BufferedImage image) {
        if (image != null) {
            ImageIcon previewImage = new ImageIcon(image);
            previewLabel.setIcon(previewImage);
            ocrImage = image;
        }
    }

    // 选择需要识别的图片
    // 支持：TIFF、JPEG、GIF、PNG和BMP图像格式
    private void showChooseFileDialog() {
        JFileChooser fileChooser = getjFileChooser();
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            filePath = selectedFile.getAbsolutePath();

            String[] fileTypeArray = filePath.split("\\.");
            String fileType = fileTypeArray[fileTypeArray.length - 1];

            if (fileType.equalsIgnoreCase(FileType.DOCX.getName())) {
                File file = new File(filePath);
                try (InputStream inputStream = Files.newInputStream(file.toPath())) {
                    byte[] buffer = new byte[(int) file.length()];
                    while (inputStream.read(buffer, 0, buffer.length) != -1) {
                    }

                    // 这里的buffer就是包含了文件内容的字节数组
                    List<XWPFPicture> allPicture = DealDocument.getAllPicture(buffer);

                    List<String> textList = new ArrayList<>(100);
                    for (int i = 0; i < allPicture.size() - 1; i++) {
                        byte[] pictureBytes = allPicture.get(i).getPictureData().getData();

                        ImageIcon icon = new ImageIcon(pictureBytes);

                        int iconWidth = icon.getIconWidth();
                        int iconHeight = icon.getIconHeight();

                        if (iconWidth > 1000 || iconHeight > 1000) {
                            Image newImage = icon.getImage().getScaledInstance((int) (iconWidth * 0.5), (int) (iconHeight * 0.5), Image.SCALE_DEFAULT);
                            icon = new ImageIcon(newImage);
                        }

                        JLabel label = new JLabel(icon);
                        picturePanel.add(label);

                        if (i == 5) {
                            picturePanel.revalidate();
                            picturePanel.setLayout(new GridLayout(10, 1));
                            return;
                        }

//                        Map res = IdCardOcrUtils.getStringStringMap(pictureBytes);
//                        assert res != null;
//                        textList.add((String) res.get("appendText"));
//                        if (textList.size() == 5) {
//                            String res11 = String.join("\n", textList);
//                            resultArea.setText(res11);
//                            return;
//                        }
                    }

                    return;
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
//            ImageIcon imageIcon = new ImageIcon(filePath);
//            previewLabel.setIcon(imageIcon);
//            ImageIcon imageIcon1 = new ImageIcon(filePath);
//            previewLabel1.setIcon(imageIcon1);


            ImageIcon imageIcon = new ImageIcon(filePath);
            previewLabel.setIcon(imageIcon);
            try {
                ocrImage = ImageIO.read(Files.newInputStream(Paths.get(filePath)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private JFileChooser getjFileChooser() {
        JFileChooser fileChooser = new JFileChooser();
        if (filePath == null || filePath.isEmpty()) {
            FileSystemView fsv = fileChooser.getFileSystemView();
            fileChooser.setCurrentDirectory(fsv.getHomeDirectory()); // 设置桌面为当前文件路径
        } else {
            // 设置上一次选择路径为当前文件路径
            File file = new File(filePath);
            File parentFile = file.getParentFile();
            if (parentFile == null) {
                fileChooser.setCurrentDirectory(file);
            } else {
                fileChooser.setCurrentDirectory(parentFile);
            }
        }

        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY); // 可选文件夹和文件
        fileChooser.setMultiSelectionEnabled(false); // 设置可多选
        fileChooser.removeChoosableFileFilter(fileChooser.getAcceptAllFileFilter()); // 不显示所有文件的下拉选
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("文件", "TIFF", "JPG", "GIF", "PNG", "BMP", "DOCX"));

        return fileChooser;
    }

    // 灰度化+二值化处理图片
    private void processImage(BufferedImage targetImage) {
        // 灰度化
        int width = targetImage.getWidth();
        int height = targetImage.getHeight();
        BufferedImage grayImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int rgb = targetImage.getRGB(i, j);
                grayImage.setRGB(i, j, rgb);
            }
        }
        // 二值化
        width = grayImage.getWidth();
        height = grayImage.getHeight();
        BufferedImage binaryImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int rgb = grayImage.getRGB(i, j);
                binaryImage.setRGB(i, j, rgb);
            }
        }
        ocrImage = binaryImage;
        ImageIcon previewImage = new ImageIcon(ocrImage);
        previewLabel.setIcon(previewImage);
    }

    // 执行OCR识别
    private void execute(BufferedImage targetImage) {
        try {
            File tempFile = new File(tempImage);
            tempFile.mkdirs();
            ImageIO.write(targetImage, "jpg", tempFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        File file = new File(tempImage);

        ITesseract instance = new Tesseract();
        // 设置语言库位置
//        instance.setDatapath("src/main/resources/data");
        instance.setDatapath("D:\\code\\pictureOCRV1\\src\\main\\resources\\data\\");
        // 设置语言
        instance.setLanguage(language);
        Thread thread = new Thread(() -> {
            String result = null;
            try {
                result = instance.doOCR(file);
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println(result);
            resultArea.setText(result);
        });
        ProgressBar.show(this, thread, "图片正在识别中，请稍后...", "执行结束", "取消");
    }


}
