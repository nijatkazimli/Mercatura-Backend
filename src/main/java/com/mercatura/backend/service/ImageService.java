package com.mercatura.backend.service;

import com.azure.storage.blob.BlobClientBuilder;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.models.PublicAccessType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
public class ImageService {

    @Value("${azure.storage.connection-string}")
    private String connectionString;

    @Value("${azure.storage.container-name}")
    private String containerName;

    public String saveImage(MultipartFile file) throws IOException {
        BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
                .connectionString(connectionString)
                .buildClient();

        BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(containerName);

        if (!containerClient.exists()) {
            containerClient.create();
            containerClient.setAccessPolicy(PublicAccessType.BLOB, null);
        }

        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();

        new BlobClientBuilder()
                .connectionString(connectionString)
                .containerName(containerName)
                .blobName(filename)
                .buildClient()
                .upload(file.getInputStream(), file.getSize(), true);

        String url = containerClient.getBlobClient(filename).getBlobUrl();
        // we are using docker-service -> forward to localhost
        return url.replace("azuriteDocker", "localhost");
    }
}
