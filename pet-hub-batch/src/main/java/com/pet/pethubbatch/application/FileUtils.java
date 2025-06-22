package com.pet.pethubbatch.application;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

public final class FileUtils {

    private FileUtils() {

    }

    public static String calculateHashCode(Path file) throws IOException, NoSuchAlgorithmException {
        final MessageDigest digest = MessageDigest.getInstance("SHA-256");

        try (var in = Files.newInputStream(file);
             var din = new DigestInputStream(in, digest)) {

            byte[] buffer = new byte[8192];
            while (din.read(buffer) != -1) {
                // digest is updated automatically
            }
        }

        return HexFormat.of().formatHex(digest.digest());
    }

}
