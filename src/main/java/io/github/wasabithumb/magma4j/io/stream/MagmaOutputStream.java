package io.github.wasabithumb.magma4j.io.stream;

import io.github.wasabithumb.magma4j.MagmaCipher;
import io.github.wasabithumb.magma4j.ctx.mode.CipherMode;
import io.github.wasabithumb.magma4j.ctx.pad.PaddingMethod;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

public class MagmaOutputStream extends OutputStream {

    protected final OutputStream out;
    protected final byte[] key;
    protected final MagmaCipher cipher;
    protected final byte[] buf;
    protected int head;

    public MagmaOutputStream(@NotNull OutputStream out, byte @NotNull [] key, @NotNull CipherMode mode, int blockLength) {
        this.out = out;
        this.key = Arrays.copyOf(key, key.length);
        this.cipher = MagmaCipher.builder()
                .padding(PaddingMethod.NONE)
                .mode(mode)
                .blockLength(blockLength)
                .build();
        this.buf = new byte[this.cipher.blockSize()];
        this.head = 0;
    }

    //

    /**
     * Encrypts the content of the buffer, writes to destination stream and resets head
     */
    protected void put() throws IOException {
        if (this.head == 0) return;

        ByteBuffer key = ByteBuffer.wrap(this.key).order(ByteOrder.LITTLE_ENDIAN);
        ByteBuffer message = ByteBuffer.wrap(this.buf, 0, this.head).order(ByteOrder.LITTLE_ENDIAN);
        this.head = 0;

        byte[] encrypted = this.cipher.encrypt(key, message);
        this.out.write(encrypted);
    }

    //

    @Override
    public void write(int i) throws IOException {
        this.buf[this.head++] = (byte) i;
        if (this.head == this.buf.length) this.put();
    }

    @Override
    public void write(byte @NotNull [] b, int off, int len) throws IOException {
        while (len > 0) {
            int max = this.buf.length - this.head;
            int count;
            boolean partial;

            if (len >= max) {
                count = max;
                partial = false;
            } else {
                count = len;
                partial = true;
            }

            System.arraycopy(b, off, this.buf, this.head, count);
            this.head += count;

            if (partial) break;
            this.put();
            len -= count;
            off += count;
        }
    }

    @Override
    public void flush() throws IOException {
        this.out.flush();
    }

    @Override
    public void close() throws IOException {
        try {
            this.put();
        } finally {
            this.out.close();
        }
    }

}
