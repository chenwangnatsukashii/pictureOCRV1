package com.example.pictureocrv1.utils;

import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.util.Optional;


public class toFile {

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
            InputStream inputStream = file.getInputStream();
            String fileFullName = file.getOriginalFilename();
            String fileName = fileFullName.substring(0, fileFullName.lastIndexOf("."));
            String prefix = fileFullName.substring(fileFullName.lastIndexOf("."));
            toFile = new File(filePath + fileName + "_" + System.currentTimeMillis() + prefix);
            inputStreamToFile(inputStream, toFile);
            inputStream.close();
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
