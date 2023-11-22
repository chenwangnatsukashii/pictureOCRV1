package com.example.pictureocrv1.view;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Description 截图窗口
 * @Author Lijinjiang
 * @ModifyDate 2023/8/25 13:28
 * @Version 2.0
 */
public class ScreenshotWindow extends JWindow {

    private int startX, startY, endX, endY;
    private final BufferedImage captureImage;
    private BufferedImage tempImage;
    private BufferedImage selectedImage; // 选择的图片
    private ToolsWindow toolsWindow; //工具条窗口
    protected MainFrame mainFrame; //传递过来的MainFrame

    private boolean screenshotting = false;//是否正在截图中

    public ScreenshotWindow(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        //获取屏幕尺寸
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        this.setBounds(0, 0, dimension.width, dimension.height);

        //截取屏幕
        Robot robot = null;
        try {
            robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }

        assert robot != null;
        captureImage = robot.createScreenCapture(new Rectangle(0, 0, dimension.width, dimension.height));

        this.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3) {
                    exit();//如果点击的右键，取消截图
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    //鼠标左键按下时记录结束点坐标，并隐藏操作窗口
                    startX = e.getX();
                    startY = e.getY();
                } else if (e.getButton() == MouseEvent.BUTTON3) {
                    //鼠标右键按下，退出截图
                    exit();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    if (screenshotting) {
                        // 鼠标松开时，显示操作窗口
                        if (toolsWindow == null) {
                            toolsWindow = new ToolsWindow(ScreenshotWindow.this);

                        }
                        setToolsLocation(e.getX(), e.getY());//设置工具条位置
                        toolsWindow.setVisible(true);
                        toolsWindow.toFront();
                        screenshotting = false;
                    }
                }
            }
        });

        this.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                screenshotting = true;
                if (toolsWindow != null) {
                    toolsWindow.setVisible(false);
                }
                // 鼠标拖动时，记录坐标并重绘窗口
                endX = e.getX();
                endY = e.getY();

                // 临时图像，用于缓冲屏幕区域放置屏幕闪烁
                Image bufferImage = createImage(ScreenshotWindow.this.getWidth(), ScreenshotWindow.this.getHeight());
                Graphics g = bufferImage.getGraphics();
                g.drawImage(tempImage, 0, 0, null);
                int x = Math.min(startX, endX);
                int y = Math.min(startY, endY);
                int width = Math.abs(endX - startX) + 1;
                int height = Math.abs(endY - startY) + 1;
                // 加上1防止width或height0
                g.setColor(new Color(0, 147, 250));
                g.drawRect(x - 1, y - 1, width + 1, height + 1);
                // 减1加1都了防止图片矩形框覆盖掉
                selectedImage = captureImage.getSubimage(x, y, width, height);
                g.drawImage(selectedImage, x, y, null);
                ScreenshotWindow.this.getGraphics().drawImage(bufferImage, 0, 0, ScreenshotWindow.this);
            }
        });
        this.setVisible(true);
    }

    @Override
    public void paint(Graphics g) {
        RescaleOp ro = new RescaleOp(0.8f, 0, null);
        tempImage = ro.filter(captureImage, null);
        g.drawImage(tempImage, 0, 0, this);
    }

    //退出截图
    public void exit() {
        this.dispose();//截图框关闭
        toolsWindow.dispose();//工具条关闭
        mainFrame.setVisible(true);//显示主窗口
    }

    // 保存图像到文件
    public void saveImage() throws IOException {
        // 先隐藏窗口后台执行，显得程序执行很快
        this.setVisible(false);
        toolsWindow.setVisible(false);

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("保存");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        fileChooser.removeChoosableFileFilter(fileChooser.getAcceptAllFileFilter()); // 不显示所有文件的下拉选
        // 文件过滤器，用户过滤可选择文件
        FileNameExtensionFilter filter = new FileNameExtensionFilter("JPG", "jpg");
        fileChooser.setFileFilter(filter);

        // 初始化一个默认文件（此文件会生成到桌面上）
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HHmmss");
        String fileName = sdf.format(new Date());
        File filePath = FileSystemView.getFileSystemView().getHomeDirectory();
        File defaultFile = new File(filePath + File.separator + fileName + ".jpg");
        fileChooser.setSelectedFile(defaultFile);

        int flag = fileChooser.showSaveDialog(this);
        if (flag == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            String path = file.getPath();
            // 检查文件后缀，放置用户忘记输入后缀或者输入不正确的后缀
            if (!(path.endsWith(".jpg") || path.endsWith(".JPG"))) {
                path += ".jpg";
            }
            ImageIO.write(selectedImage, "jpg", new File(path));
        }
    }

    public void setToolsLocation(int x, int y) {
        if (x < startX && y < startY) {
            toolsWindow.setLocation(x, y - 35);
        } else if (x > startX && y < startY) {
            toolsWindow.setLocation(x - 118, y - 35);
        } else if (x < startX && y > startY) {
            toolsWindow.setLocation(x, y + 5);
        } else if (x > startX && y > startY) {
            toolsWindow.setLocation(x - 118, y + 5);
        }
    }

    /*
     * 工具窗口类
     */
    class ToolsWindow extends JWindow implements ActionListener {
        private final ScreenshotWindow parentWindow;

        JButton saveBtn, cancelBtn, selectBtn;

        public ToolsWindow(ScreenshotWindow parentWindow) {
            this.parentWindow = parentWindow;
            this.setLayout(null);
            JToolBar toolBar = new JToolBar();
            toolBar.setBorder(BorderFactory.createLineBorder(Color.BLACK)); // 设置边框为空
            toolBar.setFloatable(false); // 设置不可移动

            saveBtn = new JButton("保存");// 保存按钮
            cancelBtn = new JButton(" ✘ ");// 取消按钮
            selectBtn = new JButton(" ✔ ");// 选定按钮

            saveBtn.addActionListener(this);
            cancelBtn.addActionListener(this);
            selectBtn.addActionListener(this);

            toolBar.add(saveBtn);
            toolBar.addSeparator(new Dimension(10, 30));//添加分隔符
            toolBar.add(cancelBtn);
            toolBar.add(selectBtn);

            toolBar.setBounds(0, 0, 120, 30);

            this.add(toolBar);

            this.setSize(120, 30);
            this.setVisible(true);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == saveBtn) {
                try {
                    parentWindow.saveImage();
                    exit();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
            if (e.getSource() == cancelBtn) {
                exit();
            }
            if (e.getSource() == selectBtn) {
                mainFrame.setPreviewImage(selectedImage);//设置选中的图片
                exit();
            }
        }
    }
}
