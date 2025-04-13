// SPDX-License-Identifier: MIT
package com.github.serock.s3;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultWrappingKeyGenerator implements WrappingKeyGenerator {

    private static final DefaultWrappingKeyGenerator INSTANCE = new DefaultWrappingKeyGenerator();
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultWrappingKeyGenerator.class);

    private DefaultWrappingKeyGenerator() {
        super();
    }

    public static DefaultWrappingKeyGenerator getInstance() {
        return INSTANCE;
    }

    @Override
    public SecretKey generateKey(final char[] password) {
        SecretKey wrappingKey = null;
        try {
            final SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            final byte[] salt = new byte[] {
                    23, 107, 47, 2, -9, 53, -40, -73, -95, 39, -32, 102, 111, -33, 36, 53
            };
            final KeySpec keySpec = new PBEKeySpec(password, salt, 1048576, 256);
            wrappingKey = new SecretKeySpec(keyFactory.generateSecret(keySpec).getEncoded(), "AES");
            Arrays.fill(password, ' ');
        } catch (final NoSuchAlgorithmException | InvalidKeySpecException e) {
            LOGGER.error("Failed to generate wrapping key", e);
            System.exit(1);
        }
        return wrappingKey;
    }
}
