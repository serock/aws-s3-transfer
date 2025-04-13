// SPDX-License-Identifier: MIT
package com.github.serock.s3;

import javax.crypto.SecretKey;

public interface WrappingKeyGenerator {
    SecretKey generateKey(char[] password);
}
