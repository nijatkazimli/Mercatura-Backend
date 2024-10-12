package com.mercatura.backend.utils;

import com.mercatura.backend.service.ImageService;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

public class SampleImageUploader {

    public static String uploadSingleImage(ImageService imageService, File file) throws IOException {
        MultipartFile multipartFile = new FileMultiPart(file);
        return imageService.saveImage(multipartFile);
    }
}
