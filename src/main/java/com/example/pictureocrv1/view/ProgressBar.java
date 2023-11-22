package com.example.pictureocrv1.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
 * @ClassName ProgressBar
 * @Description TODO
 * @Author Li
 * @Date 2022/9/27 14:23
 * @ModifyDate 2022/9/27 14:23
 * @Version 1.0
 */
public class ProgressBar implements ActionListener {
    private static final String DEFAULT_STATUS = "Default Info";
    private JDialog dialog;
    private JProgressBar progressBar;
    private JLabel statusLabel;
    private JButton btnCancel;
    private final Window parent;
    private final Thread thread; // 处理业务的线程
    private final String statusInfo;
    private String resultInfo;
    private final String cancelInfo;

    /**
     * 显示进度条测试对话框
     */

    public static void show(Window parent, Thread thread) {
        new ProgressBar(parent, thread, DEFAULT_STATUS, null, null);
    }

    /**
     * 显示进度条测试对话框
     */

    public static void show(Window parent, Thread thread, String statusInfo) {
        new ProgressBar(parent, thread, statusInfo, null, null);
    }

    /**
     * 显示对话框
     */

    public static void show(Window parent, Thread thread, String statusInfo, String resultInfo, String cancelInfo) {
        new ProgressBar(parent, thread, statusInfo, resultInfo, cancelInfo);
    }

    /**
     * 对话框构造函数
     */

    private ProgressBar(Window parent, Thread thread, String statusInfo, String resultInfo, String cancelInfo) {
        this.parent = parent;
        this.thread = thread;
        this.statusInfo = statusInfo;
        this.resultInfo = resultInfo;
        this.cancelInfo = cancelInfo;
        initUI();
        startThread();
        dialog.setVisible(true);
    }

    /**
     * 构建显示进度条的对话框
     */
    private void initUI() {
        if (parent instanceof Dialog) {
            dialog = new JDialog((Dialog) parent, true);
        } else if (parent instanceof Frame) {
            dialog = new JDialog((Frame) parent, true);
        } else {
            dialog = new JDialog((Frame) null, true);
        }
        final JPanel mainPane = new JPanel(null);
        progressBar = new JProgressBar();
        statusLabel = new JLabel(statusInfo);
        btnCancel = new JButton("Cancel");
        progressBar.setIndeterminate(true);
        btnCancel.addActionListener(this);
        mainPane.add(progressBar);
        mainPane.add(statusLabel);

        dialog.getContentPane().add(mainPane);
        dialog.setTitle("执行中");
        dialog.setResizable(true);
        dialog.setSize(360, 120);
        dialog.setLocationRelativeTo(parent); // 设置此窗口相对于指定组件的位置
        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE); // 不允许关闭
        mainPane.addComponentListener(new ComponentAdapter() {

            public void componentResized(ComponentEvent e) {
                layout(mainPane.getWidth(), mainPane.getHeight());
            }

        });

    }

    /**
     * 启动线程，测试进度条
     */
    private void startThread() {
        new Thread() {

            public void run() {
                try {
                    thread.start(); // 处理耗时任务
                    // 等待事务处理线程结束
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    // 关闭进度提示框
                    dialog.dispose();
                    if (resultInfo != null && !resultInfo.trim().isEmpty()) {
                        String title = "消息";
                        JOptionPane.showMessageDialog(parent, resultInfo, title, JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            }

        }.start();

    }

    /**
     * 设置控件的位置和大小
     */

    private void layout(int width, int height) {
        progressBar.setBounds(20, 20, 320, 15);
        statusLabel.setBounds(20, 50, 320, 25);
        btnCancel.setBounds(width - 85, height - 31, 75, 21);
    }

    @SuppressWarnings("deprecation")
    public void actionPerformed(ActionEvent e) {
        resultInfo = cancelInfo;
        thread.stop();
    }
}
