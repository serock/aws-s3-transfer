// SPDX-License-Identifier: MIT
package com.github.serock.s3;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EnvironmentVariablePasswordRetriever implements PasswordRetriever {

    private static final EnvironmentVariablePasswordRetriever INSTANCE = new EnvironmentVariablePasswordRetriever();
    private static final String S3_ENCRYPTION_CLIENT_WRAPPING_KEY_PASSWORD = "S3_WRAPPING_KEY_PASSWORD";
    private static final Logger LOGGER = LoggerFactory.getLogger(EnvironmentVariablePasswordRetriever.class);

    private EnvironmentVariablePasswordRetriever() {
        super();
    }

    public static EnvironmentVariablePasswordRetriever getInstance() {
        return INSTANCE;
    }

    @Override
    public char[] retrievePassword() {
        try {
            return System.getenv(S3_ENCRYPTION_CLIENT_WRAPPING_KEY_PASSWORD).toCharArray();
        } catch (final NullPointerException e) {
            LOGGER.error("Environment variable is not set", e);
            System.exit(1);
        }
        return null;
    }
}
