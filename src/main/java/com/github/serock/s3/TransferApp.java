// SPDX-License-Identifier: MIT
package com.github.serock.s3;

import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;

public class TransferApp {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransferApp.class);
    private static final int MIN_PASSWORD_LENGTH = 16;

    private Mode transferMode;
    private String bucketName;
    private Path filePath;
    private String objectKey;
    private WrappingKeyGenerator keyGenerator;
    private PasswordRetriever passwordRetriever;
    private TransferImplementation transferImplementation;

    public static void main(final String[] args) {
        final TransferApp app = new TransferApp();
        app.setTransferMode(Mode.valueOf(args[0]));
        if (app.transferMode().equals(Mode.upload)) {
            app.setFilePath(Path.of(args[1]));
            app.setBucketName(args[2]);
            app.setObjectKey(args[3]);
            app.setTransferImplementation(app.new Uploader());
        }
        if (app.transferMode().equals(Mode.download)) {
            app.setBucketName(args[1]);
            app.setObjectKey(args[2]);
            app.setFilePath(Path.of(args[3]));
            app.setTransferImplementation(app.new Downloader());
        }
        app.setKeyGenerator(DefaultWrappingKeyGenerator.getInstance());
        app.setPasswordRetriever(EnvironmentVariablePasswordRetriever.getInstance());
        app.transferFile();
    }

    protected char[] retrievePassword() {
        final char[] password = passwordRetriever().retrievePassword();
        if (password == null || password.length < MIN_PASSWORD_LENGTH) {
            LOGGER.error("Password does not have {} or more characters", Integer.valueOf(MIN_PASSWORD_LENGTH));
            System.exit(1);
        }
        return password;
    }

    protected void transferFile() {
        final char[] password = retrievePassword();
        transferImplementation().doTransfer(password);
    }

    protected WrappingKeyGenerator keyGenerator() {
        return this.keyGenerator;
    }

    protected PasswordRetriever passwordRetriever() {
        return this.passwordRetriever;
    }

    protected Mode transferMode() {
        return this.transferMode;
    }

    protected String bucketName() {
        return this.bucketName;
    }

    protected Path filePath() {
        return this.filePath;
    }

    protected String objectKey() {
        return this.objectKey;
    }

    protected TransferImplementation transferImplementation() {
        return this.transferImplementation;
    }

    protected void setTransferMode(final Mode mode) {
        this.transferMode = mode;
    }

    protected void setBucketName(final String name) {
        this.bucketName = name;
    }

    protected void setFilePath(final Path path) {
        this.filePath = path;
    }

    protected void setObjectKey(final String key) {
        this.objectKey = key;
    }

    protected void setKeyGenerator(final WrappingKeyGenerator generator) {
        this.keyGenerator = generator;
    }

    protected void setPasswordRetriever(final PasswordRetriever retriever) {
        this.passwordRetriever = retriever;
    }

    protected void setTransferImplementation(final TransferImplementation implementation) {
        this.transferImplementation = implementation;
    }

    protected enum Mode {download, upload}

    protected class Uploader implements TransferImplementation {

        @Override
        public void doTransfer(final char[] password) {
            final FileUploader uploader = FileUploader.newInstance(filePath(), bucketName(), objectKey());
//            uploader.setTransferListener(LoggingTransferListener.create());
            try (ProfileCredentialsProvider credentialsProvider = ProfileCredentialsProvider.create()) {
                final String eTag = uploader.uploadFile(credentialsProvider, keyGenerator().generateKey(password));
                System.out.println(eTag + " *" + filePath().getFileName().toString());
            }
        }
    }

    protected class Downloader implements TransferImplementation {

        @Override
        public void doTransfer(final char[] password) {
            final FileDownloader downloader = FileDownloader.newInstance(bucketName(), objectKey(), filePath());
//            downloader.setTransferListener(LoggingTransferListener.create());
            try (ProfileCredentialsProvider credentialsProvider = ProfileCredentialsProvider.create()) {
                final String eTag = downloader.downloadFile(credentialsProvider, keyGenerator().generateKey(password));
                System.out.println(eTag + " *" + filePath().getFileName().toString());
            }
        }
    }
}
