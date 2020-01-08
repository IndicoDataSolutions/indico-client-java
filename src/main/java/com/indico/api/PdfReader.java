package com.indico.api;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PdfReader {

    private final List<String> files;
    private final PdfExtractionOptions pdfExtractionOptions;

    private PdfReader(Builder builder) {
        this.files = builder.files;
        this.pdfExtractionOptions = builder.pdfExtractionOptions;
    }

    public List<String> getList() throws FileNotFoundException, IOException {
        ArrayList<String> pdfList = new ArrayList<>();
        for (String url : this.files) {
            pdfList.add(this.preProcess(url));
        }
        return pdfList;
    }

    public PdfExtractionOptions getPdfExtractionOptions() {
        return this.pdfExtractionOptions;
    }

    /**
     * Returns base64 encoded string of file or assumes the url passed is
     * already a base64 string
     *
     * @param url location of pdf file
     * @return url or base64 encoded content of file
     * @throws FileNotFoundException
     * @throws IOException
     */
    private String preProcess(String url) throws FileNotFoundException, IOException {
        File file = new File(url);
        if (file.exists()) {
            byte[] fileBytes = Files.readAllBytes(file.toPath());
            String encodedb64 = Base64.getEncoder().encodeToString(fileBytes);
            return encodedb64;
        } else {
            return url;
        }
    }

    public static class Builder {

        protected List<String> files = null;
        protected PdfExtractionOptions pdfExtractionOptions = null;

        public Builder setFiles(List<File> files) {
            ArrayList<String> filePaths = new ArrayList<>();
            System.out.println(files.get(0).getPath());
            files.forEach((file) -> {
                filePaths.add(file.getPath());
            });
            this.files = filePaths;
            return this;
        }

        public Builder setFilePaths(List<String> filePaths) {
            this.files = filePaths;
            return this;
        }

        public Builder setStream(Stream<String> stream) {
            this.files = stream.collect(Collectors.toList());
            return this;
        }

        public Builder setPdfExtractionOptions(PdfExtractionOptions pdfExtractionOptions) {
            this.pdfExtractionOptions = pdfExtractionOptions;
            return this;
        }

        public PdfReader build() {
            if (this.files == null) {
                throw new RuntimeException("Pdf File List Cannot Be Empty");
            }
            if (this.pdfExtractionOptions == null) {
                this.pdfExtractionOptions = new PdfExtractionOptions.Builder().build();
            }
            return new PdfReader(this);
        }
    }
}
