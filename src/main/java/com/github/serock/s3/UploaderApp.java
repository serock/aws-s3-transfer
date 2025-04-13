// SPDX-License-Identifier: MIT
package com.github.serock.s3;

import java.nio.file.Path;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
//import software.amazon.awssdk.transfer.s3.progress.LoggingTransferListener;

public class UploaderApp extends TransferApp {

    public static void main(final String[] args) {
        final UploaderApp app = new UploaderApp();
        app.setFilePath(Path.of(args[0]));
        app.setBucketName(args[1]);
        app.setObjectKey(args[2]);
        app.setKeyGenerator(DefaultWrappingKeyGenerator.getInstance());
        app.setPasswordRetriever(EnvironmentVariablePasswordRetriever.getInstance());
        app.transferFile();
    }

    @Override
    protected void doTransfer(final char[] password) {
        final FileUploader uploader = FileUploader.newInstance(filePath(), bucketName(), objectKey());
//        uploader.setTransferListener(LoggingTransferListener.create());
        try (ProfileCredentialsProvider credentialsProvider = ProfileCredentialsProvider.create()) {
            final String eTag = uploader.uploadFile(credentialsProvider, keyGenerator().generateKey(password));
            System.out.println(eTag + " *" + filePath().getFileName().toString());
        }
    }
}
