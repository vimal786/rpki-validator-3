package net.ripe.rpki.validator3.util;

import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.SHA256Digest;

import java.io.*;
import java.util.Arrays;
import java.util.List;

public class Sha256 {
    public static byte[] hash(byte[] data) {
        try (ByteArrayInputStream in = new ByteArrayInputStream(data)) {
            return hash(in);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] hash(File targetFile) throws IOException {
        try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(targetFile))) {
            return hash(in);
        }
    }

    public static byte[] hash(InputStream in) throws IOException {
        Digest digest = new SHA256Digest();
        byte[] data = new byte[8192];

        int len;
        while ((len = in.read(data)) >= 0) {
            digest.update(data, 0, len);
        }

        byte[] result = new byte[digest.getDigestSize()];
        digest.doFinal(result, 0);

        return result;
    }

}
