package com.mercatura.backend.utils;

import org.springframework.web.multipart.MultipartFile;
import reactor.util.annotation.NonNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileMultiPart implements MultipartFile {

    private final File file;

    public FileMultiPart(File file) {
        this.file = file;
    }

    @Override
    @NonNull
    public String getName() {
        return file.getName();
    }

    @Override
    public String getOriginalFilename() {
        return file.getName();
    }

    @Override
    public String getContentType() {
        return "image/jpeg";
    }

    @Override
    public boolean isEmpty() {
        return file.length() == 0;
    }

    @Override
    public long getSize() {
        return file.length();
    }

    @Override
    @NonNull
    public byte[] getBytes() throws IOException {
        try (InputStream inputStream = new FileInputStream(file)) {
            return inputStream.readAllBytes();
        }
    }

    @Override
    @NonNull
    public InputStream getInputStream() throws IOException {
        return new FileInputStream(file);
    }

    @Override
    public void transferTo(@NonNull File dest) throws IllegalStateException {
        throw new UnsupportedOperationException("Not implemented");
    }
}
