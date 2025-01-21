package io.github.wasabithumb.magma4j.ctx.mode.instance;

import io.github.wasabithumb.magma4j.ctx.CipherContext;
import io.github.wasabithumb.magma4j.ctx.mode.CipherMode;
import io.github.wasabithumb.magma4j.io.typed.ByteTypedArray;
import io.github.wasabithumb.magma4j.io.typed.TypedArray;
import org.jetbrains.annotations.NotNull;

public final class CBCCipherModeInstance extends AbstractCipherModeInstance {

    public CBCCipherModeInstance() {
        super();
    }

    //

    @Override
    public @NotNull CipherMode mode() {
        return CipherMode.CBC;
    }

    @Override
    public @NotNull TypedArray<?> encrypt(
            @NotNull CipherContext ctx,
            @NotNull TypedArray<?> k,
            @NotNull TypedArray<?> message
    ) {
        ByteTypedArray s = ByteTypedArray.copyOf(ctx.initialVector());
        int n = ctx.blockSize();
        int m = s.length();
        ByteTypedArray c = ctx.pad(message);
        TypedArray<?> key = ctx.keySchedule(k, false);

        int b = c.length() / n;
        for (int i=0; i < b; i++) {
            for (int j=0; j < n; j++) {
                s.setByte(j, s.getByte(j) ^ c.getByte(i * n + j));
            }

            ctx.process(key, s);

            for (int j=0; j < n; j++) {
                c.setByte(i * n + j, s.getByte(j));
            }

            if (m != n) {
                for (int j=0; j < m - n; j++) {
                    s.setByte(j, s.getByte(n + j));
                }

                for (int j=0; j < n; j++) {
                    s.setByte(j + m - n, c.getByte(i * n + j));
                }
            }

            // TODO: key meshing
        }

        return c;
    }

    @Override
    public @NotNull TypedArray<?> decrypt(
            @NotNull CipherContext ctx,
            @NotNull TypedArray<?> k,
            @NotNull TypedArray<?> message
    ) {
        ByteTypedArray s = ByteTypedArray.copyOf(ctx.initialVector());
        int n = ctx.blockSize();
        int m = s.length();
        ByteTypedArray c = ByteTypedArray.copyOf(message);
        ByteTypedArray next = new ByteTypedArray(n, c.order());
        TypedArray<?> key = ctx.keySchedule(k, true);

        int b = c.length() / n;
        for (int i=0; i < b; i++) {
            for (int j=0; j < n; j++) {
                next.setByte(j, c.getByte(i * n + j));
            }

            ctx.process(key, c, i * n, true);

            for (int j=0; j < n; j++) {
                int d = i * n + j;
                c.setByte(d, c.getByte(d) ^ s.getByte(j));
            }

            if (m != n) {
                for (int j = 0; j < m - n; j++)
                    s.setByte(j, s.getByte(n + j));
            }

            for (int j=0; j < n; j++) {
                s.setByte(j + m - n, next.getByte(j));
            }

            // TODO: key meshing
        }

        return ctx.unpad(c);
    }

}
