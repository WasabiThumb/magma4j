package io.github.wasabithumb.magma4j;

import io.github.wasabithumb.magma4j.ctx.mode.CipherMode;
import io.github.wasabithumb.magma4j.ctx.pad.PaddingMethod;
import io.github.wasabithumb.magma4j.io.stream.MagmaInputStream;
import io.github.wasabithumb.magma4j.io.stream.MagmaOutputStream;
import org.intellij.lang.annotations.Pattern;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Entry point for Magma4J
 * @see #generateKey()
 * @see #generateKeyFromPassword(String)
 * @see #encrypt(byte[], byte[])
 * @see #decrypt(byte[], byte[])
 * @see #newCipher()
 * @see #defaultCipher()
 */
public final class Magma {

    private static final MagmaCipher INSTANCE = MagmaCipher.builder()
            .mode(CipherMode.CBC)
            .padding(PaddingMethod.PKCS5)
            .build();

    //

    /**
     * Returns the default cipher ({@code GOST R 34.12-15-CBC-PKCS5PPADDING})
     */
    @Contract(pure = true)
    public static @NotNull MagmaCipher defaultCipher() {
        return INSTANCE;
    }

    /**
     * Returns a builder to create a new cipher instance with custom parameters.
     */
    @Contract("-> new")
    public static @NotNull MagmaCipher.Builder newCipher() {
        return MagmaCipher.builder();
    }

    /**
     * Creates a new cipher instance from the given cipher name, e.g. {@code GOST R 34.12-15-CBC-PKCS5PPADDING}
     */
    @Contract("_ -> new")
    public static @NotNull MagmaCipher newCipher(
            @NotNull @Pattern("^GOST R 34\\.12(?:-15)?(?:-(64|128))?-((?:CTR|CFB|OFB)-\\d+|(?:ECB|CBC|CTR|CFB|OFB))(?:-(NO|BIT|RANDOM|ZERO|PKCS5P)PADDING)?$") String name
    ) {
        return MagmaCipher.fromName(name);
    }

    /**
     * Creates a new Magma {@link CipherMode#ECB ECB} decrypting stream with block length 64
     * @param backing Source stream
     * @param key Decryption key
     * @return Stream from which decrypted data can be read
     * @see MagmaInputStream#MagmaInputStream(InputStream, byte[], CipherMode, int)
     */
    @Contract("_, _ -> new")
    public static @NotNull MagmaInputStream newInputStream(@NotNull InputStream backing, byte @NotNull [] key) {
        return new MagmaInputStream(backing, key, CipherMode.ECB, 64);
    }

    /**
     * Creates a new Magma {@link CipherMode#ECB ECB} encrypting stream with block length 64
     * @param backing Destination
     * @param key Encryption key
     * @return Stream to which unencrypted data can be written
     * @see MagmaOutputStream#MagmaOutputStream(OutputStream, byte[], CipherMode, int)
     */
    @Contract("_, _ -> new")
    public static @NotNull MagmaOutputStream newOutputStream(@NotNull OutputStream backing, byte @NotNull [] key) {
        return new MagmaOutputStream(backing, key, CipherMode.ECB, 64);
    }

    /**
     * Reports the key size for Magma (always {@code 32})
     */
    public static int keySize() {
        return INSTANCE.keySize();
    }

    /**
     * Generates a random encryption key
     */
    public static byte @NotNull [] generateKey() {
        return INSTANCE.generateKey();
    }

    /**
     * Generates an encryption key from the given password using PBKDF2
     */
    public static byte @NotNull [] generateKeyFromPassword(@NotNull String password) {
        return INSTANCE.generateKeyFromPassword(password);
    }

    /**
     * Encrypts a message with the default cipher ({@code GOST R 34.12-15-CBC-PKCS5PPADDING})
     * @param key The key to use
     * @param message The message to encrypt
     * @return An encrypted message
     * @see #defaultCipher()
     */
    public static byte @NotNull [] encrypt(
            byte @NotNull [] key,
            byte @NotNull [] message
    ) {
        return INSTANCE.encrypt(key, message);
    }

    /**
     * Decrypts a message with the default cipher ({@code GOST R 34.12-15-CBC-PKCS5PPADDING})
     * @param key The key to use
     * @param message The message to decrypt
     * @return The original message
     * @throws IllegalArgumentException Incorrect key or invalid data (e.g. bad padding)
     * @see #defaultCipher()
     */
    public static byte @NotNull [] decrypt(
            byte @NotNull [] key,
            byte @NotNull [] message
    ) throws IllegalArgumentException {
        return INSTANCE.decrypt(key, message);
    }

}
