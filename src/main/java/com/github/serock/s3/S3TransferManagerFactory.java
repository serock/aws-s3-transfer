// SPDX-License-Identifier: MIT
package com.github.serock.s3;

import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.transfer.s3.S3TransferManager;

public class S3TransferManagerFactory {

    private S3TransferManagerFactory() {
        super();
    }

    public static S3TransferManager newInstance(final S3AsyncClient s3AsyncClient) {
        final S3TransferManager transferManager = S3TransferManager.builder()
                .s3Client(s3AsyncClient)
                .build();
        return transferManager;
    }
}
