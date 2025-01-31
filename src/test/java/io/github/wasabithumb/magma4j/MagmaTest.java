package io.github.wasabithumb.magma4j;

import io.github.wasabithumb.magma4j.ctx.pad.PaddingMethod;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.*;

class MagmaTest {

    @Test
    void simple() {
        byte[] key = Magma.generateKeyFromPassword("foobar123");

        byte[] data = "super secret".getBytes(StandardCharsets.UTF_8);
        byte[] encrypted = Magma.encrypt(key, data);
        byte[] decrypted = Magma.decrypt(key, encrypted);

        assertEquals("super secret", new String(decrypted, StandardCharsets.UTF_8));
    }

    @Test
    void size64() {
        byte[] src = new byte[64];
        ThreadLocalRandom.current().nextBytes(src);
        byte[] encrypted = Magma.newCipher()
                .blockLength(64)
                .padding(PaddingMethod.NONE)
                .build()
                .encrypt(Magma.generateKey(), src);
        assertEquals(encrypted.length, 64);
    }

    @Test
    void size128() {
        byte[] src = new byte[128];
        ThreadLocalRandom.current().nextBytes(src);
        byte[] encrypted = Magma.newCipher()
                .blockLength(128)
                .padding(PaddingMethod.NONE)
                .build()
                .encrypt(Magma.generateKey(), src);
        assertEquals(encrypted.length, 128);
    }

    @Test
    void stream() {
        byte[] key = Magma.generateKey();
        byte[] payload = new byte[4209];
        ThreadLocalRandom.current().nextBytes(payload);

        byte[] encrypted = assertDoesNotThrow(() -> {
            try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
                try (OutputStream mos = Magma.newOutputStream(bos, key)) {
                    mos.write(payload);
                }
                return bos.toByteArray();
            }
        });

        byte[] decrypted = assertDoesNotThrow(() -> {
            try (ByteArrayInputStream bis = new ByteArrayInputStream(encrypted);
                 InputStream mis = Magma.newInputStream(bis, key)
            ) {
                return mis.readAllBytes();
            }
        });

        assertArrayEquals(payload, decrypted);
    }

    @Test
    void ecb() {
        encryptDecrypt("GOST R 34.12-15-ECB");
        encryptDecrypt("GOST R 34.12-15-ECB-BITPADDING");
        encryptDecrypt("GOST R 34.12-15-ECB-PKCS5PPADDING");
    }

    @Test
    void cbc() {
        encryptDecrypt("GOST R 34.12-15-CBC");
        encryptDecrypt("GOST R 34.12-15-CBC-BITPADDING");
        encryptDecrypt("GOST R 34.12-15-CBC-PKCS5PPADDING");
    }

    //

    private void encryptDecrypt(String name) {
        MagmaCipher cipher = Magma.newCipher(name);
        byte[] key = cipher.generateKey();

        byte[] data = new byte[65536];
        ThreadLocalRandom.current().nextBytes(data);

        byte[] encrypted = cipher.encrypt(key, data);
        byte[] decrypted = cipher.decrypt(key, encrypted);

        assertArrayEquals(data, decrypted);
    }

}