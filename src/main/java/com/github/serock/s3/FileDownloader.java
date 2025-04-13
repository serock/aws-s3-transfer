// SPDX-License-Identifier: MIT
package com.github.serock.s3;

import java.nio.file.Path;

import javax.crypto.SecretKey;

import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.transfer.s3.S3TransferManager;
import software.amazon.awssdk.transfer.s3.model.CompletedFileDownload;
import software.amazon.awssdk.transfer.s3.model.DownloadFileRequest;
import software.amazon.awssdk.transfer.s3.model.DownloadFileRequest.Builder;
import software.amazon.awssdk.transfer.s3.model.FileDownload;
import software.amazon.awssdk.transfer.s3.progress.TransferListener;

public class FileDownloader {
    private final String bucketName;
    private final Path filePath;
    private final String objectKey;

    private TransferListener transferListener;

    private FileDownloader(final String bucket, final String keyName, final Path file) {
        this.filePath = file;
        this.bucketName = bucket;
        this.objectKey = keyName;
    }

    public static FileDownloader newInstance(final String bucket, final String keyName, final Path file) {
        return new FileDownloader(bucket, keyName, file);
    }

//    public static void main(String[] args) {
//        FileDownloader download = new FileDownloader();
//        download.downloadFile(S3ClientFactory.transferManager, download.bucketName, download.key,
//                download.downloadedFileWithPath);
//        download.cleanUp();
//    }

    public String downloadFile(final AwsCredentialsProvider credentialsProvider, final SecretKey wrappingKey) {
        String eTag = null;
        try (S3AsyncClient s3AsyncClient = S3ClientFactory.newInstance(credentialsProvider, wrappingKey)) {
            try (S3TransferManager transferManager = S3TransferManagerFactory.newInstance(s3AsyncClient)) {
                Builder builder = DownloadFileRequest.builder()
                        .getObjectRequest(b -> b.bucket(bucketName()).key(objectKey()))
                        .destination(filePath());
                if (transferListener() != null) {
                    builder.addTransferListener(transferListener());
                }
                final DownloadFileRequest downloadFileRequest = builder.build();
                final FileDownload fileDownload = transferManager.downloadFile(downloadFileRequest);
                final CompletedFileDownload downloadResult = fileDownload.completionFuture().join();
                eTag = downloadResult.response().eTag();
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