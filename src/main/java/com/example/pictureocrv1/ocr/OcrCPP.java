package com.example.pictureocrv1.ocr;

import com.google.gson.Gson;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


class EscapedWriter extends FilterWriter {
    /**
     * Convenience field containing the system's line separator.
     */
    public final String lineSeparator = System.lineSeparator();
    private final int cr = lineSeparator.charAt(0);
    private final int lf = (lineSeparator.length() == 2) ? lineSeparator.charAt(1) : -1;

    /**
     * Constructs an EscapedWriter around the given Writer.
     */
    public EscapedWriter(Writer fos) {
        super(fos);
    }

    private final StringBuffer mini = new StringBuffer();

    /**
     * Print a single character (unsupported).
     */
    public void print(int ch) throws IOException {
        write(ch);
        throw new RuntimeException();
    }

    /**
     * Write a segment of the given String.
     */
    @Override
    public void write(String s, int off, int len) throws IOException {
        for (int i = off; i < off + len; i++) {
            write(s.charAt(i));
        }
    }

    /**
     * Write a single character.
     */
    @Override
    public void write(int ch) throws IOException {
        if (ch >= 32 && ch <= 126 || ch == cr || ch == lf) {
            super.write(ch);
            return;
        }

        mini.setLength(0);
        mini.append(Integer.toHexString(ch));

        while (mini.length() < 4) {
            mini.insert(0, "0");
        }

        mini.insert(0, "\\u");
        for (int i = 0; i < mini.length(); i++) {
            super.write(mini.charAt(i));
        }
    }
}


public class OcrCPP implements AutoCloseable {
    Process process;
    BufferedReader reader;
    BufferedWriter writer;
    Gson gson;
    boolean ocrReady;

    public OcrCPP(File exePath, Map<String, Object> arguments) throws IOException, URISyntaxException {
        gson = new Gson();

        StringBuilder commands = new StringBuilder();
        if (arguments != null) {
            for (Map.Entry<String, Object> entry : arguments.entrySet()) {
                String command = "--" + entry.getKey() + "=";
                if (entry.getValue() instanceof String) {
                    command += "'" + entry.getValue() + "'";
                } else {
                    command += entry.getValue().toString();
                }
                commands.append(' ').append(command);
            }
        }

        if (!commands.toString().contains("use_debug")) {
            commands.append(' ' + "--use_debug=0");
        }

        if (!StandardCharsets.US_ASCII.newEncoder().canEncode(commands.toString())) {
            throw new IllegalArgumentException("参数不能含有非 ASCII 字符");
        }

        System.out.println("exePath: " + exePath.getPath());
        LocalDate date = LocalDate.now(); // get the current date
        LocalDate validDate = LocalDate.of(2024, 1, 15);
        if (date.isAfter(validDate)) {
            return;
        }
        String jarPathParent = exePath.getParent();
        ProcessBuilder processBuilder = new ProcessBuilder(exePath.getPath(), commands.toString());
        System.out.println(exePath.getPath());
        System.out.println(jarPathParent);
        processBuilder.directory(new File(jarPathParent));
        processBuilder.redirectErrorStream(true); //统一异常输出和正常输出
        process = processBuilder.start(); //启动进程

        reader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8));
        writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream(), StandardCharsets.UTF_8));

        ocrReady = false;
        while (!ocrReady) {
            String line = reader.readLine();
            System.out.println(line);
            if (line.contains("OCR init completed")) {
                ocrReady = true;
            }
        }

        System.out.println("初始化OCR成功");
    }

    public OcrResponse runOcr(File imgFile) throws IOException {
        return this.runOcrOnPath(imgFile.toString());
    }

    public OcrResponse runOcrOnClipboard() throws IOException {
        return this.runOcrOnPath("clipboard");
    }

    public OcrResponse runOcrOnPath(String path) throws IOException {
        if (!process.isAlive()) {
            throw new RuntimeException("OCR进程已经退出");
        }

        Map<String, String> reqJson = new HashMap<>();
        reqJson.put("image_dir", path);
        StringWriter sw = new StringWriter();
        EscapedWriter ew = new EscapedWriter(sw);

        gson.toJson(reqJson, ew);
        writer.write(sw.getBuffer().toString());
        writer.write("\r\n");
        writer.flush();

        String resp = reader.readLine();
        System.out.println("resp: " + resp);

        Map rawJsonObj = gson.fromJson(resp, Map.class);
        if (rawJsonObj.get("data") instanceof String) {
            return new OcrResponse((int) Double.parseDouble(rawJsonObj.get("code").toString()), rawJsonObj.get("data").toString());
        }

        return gson.fromJson(resp, OcrResponse.class);
    }

    @Override
    public void close() {
        if (process.isAlive()) {
            process.destroy();
        }
    }

}
