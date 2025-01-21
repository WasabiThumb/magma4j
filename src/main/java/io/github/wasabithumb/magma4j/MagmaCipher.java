package io.github.wasabithumb.magma4j;

import io.github.wasabithumb.magma4j.ctx.CipherContext;
import io.github.wasabithumb.magma4j.ctx.bp.BlockProcessor;
import io.github.wasabithumb.magma4j.ctx.ks.KeySchedule;
import io.github.wasabithumb.magma4j.ctx.mode.CipherMode;
import io.github.wasabithumb.magma4j.ctx.mode.instance.CipherModeInstance;
import io.github.wasabithumb.magma4j.ctx.pad.PaddingMethod;
import io.github.wasabithumb.magma4j.ctx.sbox.SBox;
import io.github.wasabithumb.magma4j.io.typed.ByteTypedArray;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A configured Magma cipher instance.
 * @see Magma#defaultCipher()
 */
public class MagmaCipher implements CipherContext {

    private static final Random KEY_RANDOM = new SecureRandom();
    private static final Pattern NAME_PATTERN = Pattern.compile("^GOST R 34\\.12(?:-15)?(?:-(64|128))?-((?:CTR|CFB|OFB)-\\d+|(?:ECB|CBC|CTR|CFB|OFB))(?:-(NO|BIT|RANDOM|ZERO|PKCS5P)PADDING)?$");

    @Contract("-> new")
    public static @NotNull Builder builder() {
        return new Builder();
    }

    @Contract("_ -> new")
    public static @NotNull MagmaCipher fromName(@NotNull CharSequence name) throws IllegalArgumentException {
        Matcher m = NAME_PATTERN.matcher(name);
        if (!m.matches()) throw new IllegalArgumentException("Unrecognized cipher name \"" + name + "\"");

        int blockSize = 64;
        String blockSizeS = m.group(1);
        if (blockSizeS != null && !blockSizeS.isEmpty() && blockSizeS.charAt(0) == '1') {
            blockSize = 128;
        }

        CipherModeInstance mode;
        String modeS = m.group(2);
        int modeSD = modeS.indexOf('-');
        if (modeSD == -1) {
            mode = CipherMode.valueOf(modeS).getInstance();
        } else {
            mode = CipherMode.valueOf(modeS.substring(0, modeSD))
                    .getInstance(Integer.parseInt(modeS.substring(modeSD + 1)));
        }

        PaddingMethod pad = switch (m.group(3)) {
            case "NO" -> PaddingMethod.NONE;
            case "BIT" -> PaddingMethod.BIT;
            case "RANDOM" -> PaddingMethod.RANDOM;
            case "ZERO" -> PaddingMethod.ZERO;
            case "PKCS5P" -> PaddingMethod.PKCS5;
            case null -> null;
            default -> null;
        };

        return MagmaCipher.builder()
                .blockLength(blockSize)
                .mode(mode)
                .padding(pad)
                .build();
    }

    //

    private final SBox sBox;
    private final int blockLength;
    private final BlockProcessor blockProcessor;
    private final KeySchedule keySchedule;
    private final CipherModeInstance mode;
    private final PaddingMethod padding;
    private final ByteOrder order;
    private final byte[] initialVector;
    private final byte[] kdfSalt;
    private final int kdfIterations;

    protected MagmaCipher(
            @NotNull SBox sBox,
            int blockLength,
            @NotNull CipherModeInstance mode,
            @Nullable PaddingMethod padding,
            @NotNull ByteOrder order,
            byte @Nullable [] initialVector,
            byte @NotNull [] kdfSalt,
            int kdfIterations
    ) {
        this.sBox = sBox;
        this.blockLength = blockLength;
        if (blockLength == 64) {
            this.blockProcessor = BlockProcessor.MAGMA_64;
            this.keySchedule = KeySchedule.MAGMA_64;
        } else if (blockLength == 128) {
            this.blockProcessor = BlockProcessor.MAGMA_128;
            this.keySchedule = KeySchedule.MAGMA_128;
        } else {
            throw new IllegalArgumentException("Invalid block length " + blockLength);
        }
        this.mode = mode;
        if (padding == null) {
            this.padding = mode.supportsPadding() ? PaddingMethod.ZERO : PaddingMethod.NONE;
        } else {
            if (!mode.supportsPadding())
                throw new IllegalArgumentException("Mode " + mode.name() + " does not support padding");
            this.padding = padding;
        }
        this.order = order;
        if (initialVector != null) {
            if (mode.mode() == CipherMode.CTR) {
                int size = blockLength >> 4;
                if (initialVector.length != size)
                    throw new IllegalArgumentException("Initial vector must be " + (blockLength >> 1) + " bits");
            } else {
                int size = blockLength >> 3;
                if (initialVector.length % size != 0)
                    throw new IllegalArgumentException("Initial vector must be multiple of " + blockLength + " bits");
            }
            this.initialVector = Arrays.copyOf(initialVector, initialVector.length);
        } else {
            this.initialVector = new byte[blockLength >> 3];
        }
        this.kdfSalt = Arrays.copyOf(kdfSalt, kdfSalt.length);
        this.kdfIterations = kdfIterations;
    }

    //

    public @NotNull String name() {
        StringBuilder sb = new StringBuilder("GOST R 34.12-15-");

        if (this.blockLength != 64) {
            sb.append(this.blockLength)
                    .append('-');
        }

        sb.append(this.mode.name());

        if (this.padding != (this.mode.supportsPadding() ? PaddingMethod.ZERO : PaddingMethod.NONE)) {
            sb.append('-')
                    .append(this.padding.name())
                    .append("PADDING");
        }

        return sb.toString();
    }

    //

    @Override
    public @NotNull SBox sBox() {
        return this.sBox;
    }

    @Override
    public @NotNull KeySchedule keySchedule() {
        return this.keySchedule;
    }

    @Override
    public @NotNull PaddingMethod padding() {
        return this.padding;
    }

    @Override
    public @NotNull BlockProcessor blockProcessor() {
        return this.blockProcessor;
    }

    @Override
    public int blockLength() {
        return this.blockLength;
    }

    @Override
    public @NotNull ByteTypedArray initialVector() {
        return new ByteTypedArray(this.initialVector, this.order);
    }

    public @NotNull ByteOrder byteOrder() {
        return this.order;
    }

    //

    public int keySize() {
        return 32;
    }

    public byte @NotNull [] generateKey() {
        byte[] ret = new byte[this.keySize()];
        KEY_RANDOM.nextBytes(ret);
        return ret;
    }

    public byte @NotNull [] generateKeyFromPassword(@NotNull String password) {
        try {
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            return skf.generateSecret(new PBEKeySpec(
                    password.toCharArray(),
                    this.kdfSalt,
                    this.kdfIterations,
                    256
            )).getEncoded();
        } catch (GeneralSecurityException e) {
            throw new AssertionError("Failed to generate key with PBKDF2", e);
        }
    }

    public byte @NotNull [] encrypt(byte @NotNull [] key, byte @NotNull [] message) {
        return this.encrypt(
                ByteBuffer.wrap(key).order(this.order),
                ByteBuffer.wrap(message).order(this.order)
        );
    }

    @ApiStatus.Internal
    public byte @NotNull [] encrypt(@NotNull ByteBuffer key, @NotNull ByteBuffer message) {
        return this.mode.encrypt(
                this,
                new ByteTypedArray(key),
                new ByteTypedArray(message)
        ).asByte().toArray();
    }

    public byte @NotNull [] decrypt(byte @NotNull [] key, byte @NotNull [] message) throws IllegalArgumentException {
        return this.decrypt(
                ByteBuffer.wrap(key).order(this.order),
                ByteBuffer.wrap(message).order(this.order)
        );
    }

    @ApiStatus.Internal
    public byte @NotNull [] decrypt(@NotNull ByteBuffer key, @NotNull ByteBuffer message) throws IllegalArgumentException {
        return this.mode.decrypt(
                this,
                new ByteTypedArray(key),
                new ByteTypedArray(message)
        ).asByte().toArray();
    }

    //

    public static final class Builder {

        private SBox sBox = SBox.STANDARD;
        private int blockLength = 64;
        private CipherModeInstance mode = CipherMode.ECB.getInstance();
        private PaddingMethod padding = null;
        private ByteOrder order = ByteOrder.LITTLE_ENDIAN;
        private byte[] initialVector = null;
        private byte[] kdfSalt = new byte[] { 22, -126, -8, 53, -36, 1, -68, -40, 120, 105, 5, -95, -6,-61, 45, -123, 49, 72, 120, 49, 100, -68, -19, 93 };
        private int kdfIterations = 1000;

        //

        @Contract("_ -> this")
        public @NotNull Builder sBox(@NotNull SBox sBox) {
            this.sBox = sBox;
            return this;
        }

        @Contract("_ -> this")
        public @NotNull Builder blockLength(int blockLength) {
            this.blockLength = blockLength;
            return this;
        }

        @Contract("_ -> this")
        public @NotNull Builder mode(@NotNull CipherModeInstance mode) {
            this.mode = mode;
            return this;
        }

        @Contract("_ -> this")
        public @NotNull Builder mode(@NotNull CipherMode mode) {
            this.mode = mode.getInstance();
            return this;
        }

        @Contract("_, _ -> this")
        public @NotNull Builder mode(@NotNull CipherMode mode, int shiftBits) throws IllegalArgumentException {
            try {
                this.mode = mode.getInstance(shiftBits);
            } catch (UnsupportedOperationException e) {
                throw new IllegalArgumentException("Method cannot accept mode: " + mode.name(), e);
            }
            return this;
        }

        @Contract("_ -> this")
        public @NotNull Builder padding(@Nullable PaddingMethod padding) {
            this.padding = padding;
            return this;
        }

        @Contract("_ -> this")
        public @NotNull Builder byteOrder(@NotNull ByteOrder order) {
            this.order = order;
            return this;
        }

        @Contract("_ -> this")
        public @NotNull Builder initialVector(byte @Nullable [] initialVector) {
            this.initialVector = initialVector;
            return this;
        }

        @Contract("_ -> this")
        public @NotNull Builder kdfSalt(byte @NotNull [] kdfSalt) {
            this.kdfSalt = kdfSalt;
            return this;
        }

        @Contract("_ -> this")
        public @NotNull Builder kdfIterations(int kdfIterations) {
            this.kdfIterations = kdfIterations;
            return this;
        }

        //

        @Contract("-> new")
        public @NotNull MagmaCipher build() {
            return new MagmaCipher(
                    this.sBox,
                    this.blockLength,
                    this.mode,
                    this.padding,
                    this.order,
                    this.initialVector,
                    this.kdfSalt,
                    this.kdfIterations
            );
        }

    }

}
