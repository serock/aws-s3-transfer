// SPDX-License-Identifier: MIT
package com.github.serock.s3;

import javax.crypto.SecretKey;

import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.encryption.s3.S3AsyncEncryptionClient;

public class S3ClientFactory {

    private S3ClientFactory() {
        super();
    }

    @SuppressWarnings("resource")
    public static S3AsyncClient newInstance(final AwsCredentialsProvider credentialsProvider, final SecretKey wrappingKey) {
        final S3AsyncClient s3AsyncClient = S3AsyncClient.crtBuilder()
                .credentialsProvider(credentialsProvider)
                .build();
        final S3AsyncEncryptionClient s3AsyncEncryptionClient = S3AsyncEncryptionClient.builder()
                .aesKey(wrappingKey)
                .wrappedClient(s3AsyncClient)
                .build();
        return s3AsyncEncryptionClient;
    }
}