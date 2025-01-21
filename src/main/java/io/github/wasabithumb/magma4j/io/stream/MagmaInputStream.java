package io.github.wasabithumb.magma4j.io.stream;

import io.github.wasabithumb.magma4j.MagmaCipher;
import io.github.wasabithumb.magma4j.ctx.mode.CipherMode;
import io.github.wasabithumb.magma4j.ctx.pad.PaddingMethod;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

public class MagmaInputStream extends InputStream {

    protected final InputStream in;
    protected final byte[] key;
    protected final MagmaCipher cipher;
    protected final byte[] buf;
    protected int bufLen;
    protected int bufHead;
    protected boolean bufMode;

    public MagmaInputStream(@NotNull InputStream in, byte @NotNull [] key, @NotNull CipherMode mode, int blockLength) {
        this.in = in;
        this.key = Arrays.copyOf(key, key.length);
        this.cipher = MagmaCipher.builder()
                .padding(PaddingMethod.NONE)
                .mode(mode)
                .blockLength(blockLength)
                .build();
        this.buf = new byte[this.cipher.blockSize()];
        this.bufLen = this.buf.length;
        this.bufHead = 0;
        this.bufMode = false;
    }

    //

    /**
     * Fills the internal buffer with decrypted data.
     * @return True if EOF
     */
    protected boolean fill() throws IOException {
        if (this.bufMode) {
            if (this.bufHead < this.bufLen) return false;
            this.bufLen = this.buf.length;
            this.bufHead = 0;
        }

        int count = 0;
        while (count < this.bufLen) {
            int read = this.in.read(this.buf, count, this.bufLen - count);
            if (read == -1) break;
            count += read;
        }

        if (count == 0) {
            this.bufLen = 0;
            this.bufMode = true;
            return true;
        }

        ByteBuffer encrypted = ByteBuffer.wrap(this.buf, 0, count).order(ByteOrder.LITTLE_ENDIAN);
        byte[] decrypted;
        try {
            decrypted = this.cipher.decrypt(
                    ByteBuffer.wrap(this.key).order(ByteOrder.LITTLE_ENDIAN),
                    encrypted
            );
        } catch (IllegalArgumentException e) {
            throw new IOException("Failed to decrypt data from backing stream", e);
        }
        System.arraycopy(decrypted, 0, this.buf, 0, decrypted.length);
        this.bufLen = decrypted.length;
        this.bufMode = true;
        return false;
    }

    //

    @Override
    public int read() throws IOException {
        if (this.fill()) return -1;
        return this.buf[this.bufHead++] & 0xFF;
    }

    @Override
    public int read(byte @NotNull [] b, int off, int len) throws IOException {
        if (this.fill()) return -1;
        int count = Math.min(this.bufLen - this.bufHead, len);
        for (int i=0; i < count; i++) {
            b[off++] = this.buf[this.bufHead++];
        }
        return count;
    }

    //

    @Override
    public void close() throws IOException {
        this.in.close();
    }

}
