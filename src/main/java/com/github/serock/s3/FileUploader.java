// SPDX-License-Identifier: MIT
package com.github.serock.s3;

import java.nio.file.Path;

import javax.crypto.SecretKey;

import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.transfer.s3.S3TransferManager;
import software.amazon.awssdk.transfer.s3.model.CompletedFileUpload;
import software.amazon.awssdk.transfer.s3.model.FileUpload;
import software.amazon.awssdk.transfer.s3.model.UploadFileRequest;
import software.amazon.awssdk.transfer.s3.model.UploadFileRequest.Builder;
import software.amazon.awssdk.transfer.s3.progress.TransferListener;

public class FileUploader {
    private final String bucketName;
    private final Path filePath;
    private final String objectKey;

    private TransferListener transferListener;

    private FileUploader(final Path file, final String bucket, final String keyName) {
        this.filePath = file;
        this.bucketName = bucket;
        this.objectKey = keyName;
    }

    public static FileUploader newInstance(final Path file, final String bucket, final String keyName) {
        return new FileUploader(file, bucket, keyName);
    }

    public String uploadFile(final AwsCredentialsProvider credentialsProvider, final SecretKey wrappingKey) {
        String eTag = null;
        try (S3AsyncClient s3AsyncClient = S3ClientFactory.newInstance(credentialsProvider, wrappingKey)) {
            try (S3TransferManager transferManager = S3TransferManagerFactory.newInstance(s3AsyncClient)) {
                Builder builder = UploadFileRequest.builder()
                        .putObjectRequest(b -> b.bucket(bucketName()).key(objectKey()))
                        .source(filePath());
                if (transferListener() != null) {
                    builder.addTransferListener(transferListener());
                }
                final UploadFileRequest uploadFileRequest = builder.build();
                final FileUpload fileUpload = transferManager.uploadFile(uploadFileRequest);
                final CompletedFileUpload uploadResult = fileUpload.completionFuture().join();
                eTag = uploadResult.response().eTag();
            }
        }
        return eTag;
    }

    public String bucketName() {
        return this.bucketName;
    }

    public Path filePath() {
        return this.filePath;
    }

    public String objectKey() {
        return this.objectKey;
    }

    public void setTransferListener(final TransferListener listener) {
        this.transferListener = listener;
    }

    public TransferListener transferListener() {
        return this.transferListener;
    }
}