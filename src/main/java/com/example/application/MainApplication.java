package com.example.application;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.PDFTextStripperByArea;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;


public class MainApplication {

    static String currentRelativePath = Paths.get("").toAbsolutePath().toString();
    public static File folder = new File(currentRelativePath);
    static String outputCsvName = currentRelativePath + "/dataCsv.csv";
    static String errorFiles = currentRelativePath + "/errorFiles.txt";
    static String keywordsFilePath = currentRelativePath + "/keywords.txt";
    static String fileListPath = currentRelativePath + "/FileList.csv";

    public static void main(String[] args) {
        List<String> keywords;
        try {
            keywords = Files.readAllLines(Paths.get(keywordsFilePath));
        } catch (IOException e) {
            keywords = new ArrayList<>();
        }
        List<String> pdfPathList = new ArrayList<>();
        listFilesForFolder(folder, pdfPathList);
        List<Map<String, String>> detailsList = new ArrayList<>();
        List<String> finalKeywords = keywords;
        deleteFile(fileListPath);
        deleteFile(errorFiles);
        Map<String, String> headers = getHeaders(finalKeywords);
        dataMapToCsv(headers);
        pdfPathList.forEach(pdfPath -> {
            System.out.println(pdfPath);
            PDDocument document = null;
            try {
                document = PDDocument.load(new File(pdfPath));
                Map<String, String> detailsMap = new HashMap<>();
                detailsMap.put("File Path", pdfPath);
                detailsMap.put("File Name", pdfPath.substring(pdfPath.lastIndexOf('/') + 1));
                detailsMap.put("No. of Pages", String.valueOf(document.getNumberOfPages()));
                finalKeywords.forEach(keyword -> detailsMap.put(keyword, ""));
                document.getClass();
                if (!document.isEncrypted()) {
                    if (document.getNumberOfPages() == 1 && finalKeywords.size() > 0) {
                        PDFTextStripperByArea stripper = new PDFTextStripperByArea();
                        stripper.setSortByPosition(true);
                        PDFTextStripper tStripper = new PDFTextStripper();
                        String pdfFileInText = tStripper.getText(document);
                        finalKeywords.forEach(keyword -> detailsMap.put(keyword, String.valueOf(keywordExists(pdfFileInText, keyword))));
                    }
                }
                dataMapToCsv(detailsMap);
                detailsList.add(detailsMap);
                document.close();
            } catch (IOException e) {
                writeTxtFile(pdfPath);
            }
        });
        outputCsv(detailsList);
    }

    private static Map<String, String> getHeaders(List<String> finalKeywords) {
        Map<String, String> headers = new HashMap<>();
        headers.put("File Path", "File Path");
        headers.put("File Name", "File Name");
        headers.put("No. of Pages", "No. of Pages");
        finalKeywords.forEach(keyword -> headers.put(keyword, keyword));
        return headers;
    }

    private static void dataMapToCsv(Map<String, String> dataMap) {
        try (FileWriter writer = new FileWriter(String.valueOf(fileListPath), true)) {
            for (Map.Entry<String, String> string2 : dataMap.entrySet()) {
                writer.write(string2.getValue());
                writer.write(",");
            }
            writer.write("\r\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static boolean keywordExists(String pdfFileInText, String keyword) {
        return pdfFileInText.contains(keyword);
    }

    private static void listFilesForFolder(final File folder, List<String> pdfPathList) {
        for (final File fileEntry : Objects.requireNonNull(folder.listFiles())) {
            if (fileEntry.isDirectory()) {
                listFilesForFolder(fileEntry, pdfPathList);
            } else {
                if (fileEntry.isFile()) {
                    String temp = fileEntry.getName();
                    if ((temp.substring(temp.lastIndexOf('.') + 1).toLowerCase()).equals("pdf"))
                        pdfPathList.add(folder.getAbsolutePath() + "/" + fileEntry.getName());
                }
            }
        }
    }

    private static void outputCsv(List<Map<String, String>> detailsList) {
        List<String> headers = detailsList.stream().flatMap(map -> map.keySet().stream()).distinct().collect(Collectors.toList());
        String path = outputCsvName;
        deleteFile(outputCsvName);
        try (FileWriter writer = new FileWriter(path, false)) {
            for (String string : headers) {
                writer.write(string);
                writer.write(",");
            }
            writer.write("\r\n");

            for (Map<String, String> lmap : detailsList) {
                for (Map.Entry<String, String> string2 : lmap.entrySet()) {
                    writer.write(string2.getValue());
                    writer.write(",");
                }
                writer.write("\r\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void deleteFile(String path) {
        File csvFile = new File(path);
        if (csvFile.exists()) {
            csvFile.delete();
        }
    }

    private static void writeTxtFile(String desc) {
        try (FileWriter writer = new FileWriter(errorFiles, true)) {
            writer.write(desc);
            writer.write("\r\n");
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }
}
