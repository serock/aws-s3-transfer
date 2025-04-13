package com.github.serock.s3;

import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class TransferApp {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransferApp.class);
    private static final int MIN_PASSWORD_LENGTH = 16;

    private String bucketName;
    private Path filePath;
    private String objectKey;
    private WrappingKeyGenerator keyGenerator;
    private PasswordRetriever passwordRetriever;

    protected abstract void doTransfer(char[] password);

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
        doTransfer(password);
    }

    protected WrappingKeyGenerator keyGenerator() {
        return this.keyGenerator;
    }

    protected PasswordRetriever passwordRetriever() {
        return this.passwordRetriever;
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
}
