// SPDX-License-Identifier: MIT
package com.github.serock.s3;

import java.nio.file.Path;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
//import software.amazon.awssdk.transfer.s3.progress.LoggingTransferListener;

public class DownloaderApp extends TransferApp {

    public static void main(final String[] args) {
        final DownloaderApp app = new DownloaderApp();
        app.setBucketName(args[0]);
        app.setObjectKey(args[1]);
        app.setFilePath(Path.of(args[2]));
        app.setKeyGenerator(DefaultWrappingKeyGenerator.getInstance());
        app.setPasswordRetriever(EnvironmentVariablePasswordRetriever.getInstance());
        app.transferFile();
    }

    @Override
    protected void doTransfer(final char[] password) {
        final FileDownloader downloader = FileDownloader.newInstance(bucketName(), objectKey(), filePath());
//        downloader.setTransferListener(LoggingTransferListener.create());
        try (ProfileCredentialsProvider credentialsProvider = ProfileCredentialsProvider.create()) {
            final String eTag = downloader.downloadFile(credentialsProvider, keyGenerator().generateKey(password));
            System.out.println(eTag + " *" + filePath().getFileName().toString());
        }
    }
}
