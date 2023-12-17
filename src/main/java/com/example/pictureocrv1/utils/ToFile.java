package com.example.pictureocrv1.utils;

import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.util.Objects;
import java.util.Optional;


public class ToFile {

    /**
     * MultipartFile转File
     *
     * @param file
     * @return
     * @throws IOException
     */
    public static File multipartFiletoFile(MultipartFile file) throws IOException {
        File toFile = null;
        if ((!file.equals("")) && (file.getSize() > 0)) {
            String filePath = "/tmp/img";
            if (!new File(filePath).exists()) {
                new File(filePath).mkdirs();
            }
            try (InputStream inputStream = file.getInputStream()) {
                toFile = new File(filePath + getTempName(Objects.requireNonNull(file.getOriginalFilename())));
                inputStreamToFile(inputStream, toFile);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return toFile;
    }

    private static String getTempName(String fileFullName) {
        String fileName = fileFullName.substring(0, fileFullName.lastIndexOf("."));
        String prefix = fileFullName.substring(fileFullName.lastIndexOf("."));
        return fileName + "_" + System.currentTimeMillis() + prefix;
    }

    public static File changeByteToFile(byte[] imageByte, String originalFileName) {
        String filePath = "/tmp/img";
        if (!new File(filePath).exists()) {
            new File(filePath).mkdirs();
        }
        File toFile = new File(filePath + getTempName(Objects.requireNonNull(originalFileName)));

        try (FileOutputStream fos = new FileOutputStream(toFile)) {
            fos.write(imageByte); // 将字节数组写入文件
        } catch (IOException e) {
            e.printStackTrace();
        }

        return toFile;
    }

    /**
     * 获取文件流
     *
     * @param inputStream
     * @param file
     */
    private static void inputStreamToFile(InputStream inputStream, File file) {
        try (OutputStream outputStream = Files.newOutputStream(file.toPath())) {
            int bytesRead;
            byte[] buffer = new byte[8192];
            while ((bytesRead = inputStream.read(buffer, 0, 8192)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除临时文件
     *
     * @param file
     */
    public static void deleteTempFile(File file) {
        if (Optional.ofNullable(file).isPresent()) {
            File del = new File(file.toURI());
            del.delete();
        }
    }

}
