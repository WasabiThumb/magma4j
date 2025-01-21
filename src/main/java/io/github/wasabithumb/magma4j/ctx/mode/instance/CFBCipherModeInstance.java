package io.github.wasabithumb.magma4j.ctx.mode.instance;

import io.github.wasabithumb.magma4j.ctx.CipherContext;
import io.github.wasabithumb.magma4j.ctx.mode.CipherMode;
import io.github.wasabithumb.magma4j.io.typed.ByteTypedArray;
import io.github.wasabithumb.magma4j.io.typed.TypedArray;
import org.jetbrains.annotations.NotNull;

public final class CFBCipherModeInstance extends AbstractCipherModeInstance {

    public CFBCipherModeInstance(int shiftBits) {
        super(shiftBits);
    }

    public CFBCipherModeInstance() {
        super();
    }

    //

    @Override
    public @NotNull CipherMode mode() {
        return CipherMode.CFB;
    }

    @Override
    public @NotNull TypedArray<?> encrypt(
            @NotNull CipherContext ctx,
            @NotNull TypedArray<?> key,
            @NotNull TypedArray<?> message
    ) {
        ByteTypedArray s = ByteTypedArray.copyOf(ctx.initialVector());
        ByteTypedArray c = ByteTypedArray.copyOf(message);
        int m = s.length();
        ByteTypedArray t = new ByteTypedArray(m, c.order());
        int b = this.shiftBits(ctx) >> 3;
        int cb = c.length();
        int r = cb % b;
        int q = (cb - r) / b;
        TypedArray<?> sk = ctx.keySchedule(key, false);

        for (int i=0; i < q; i++) {
            t.set(s);

            ctx.process(sk, s);

            for (int j=0; j < b; j++) {
                int d = i * b + j;
                c.setByte(d, c.getByte(d) ^ s.getByte(j));
            }

            for (int j=0; j < m - b; j++) {
                s.setByte(j, t.getByte(b + j));
            }

            for (int j=0; j < b; j++) {
                s.setByte(m - b + j, c.getByte(i * b + j));
            }

            // TODO: key meshing
        }

        if (r > 0) {
            ctx.process(sk, s);

            for (int i=0; i < r; i++) {
                int d = q * b + i;
                c.setByte(d, c.getByte(d) ^ s.getByte(i));
            }
        }

        return c;
    }

    @Override
    public @NotNull TypedArray<?> decrypt(
            @NotNull CipherContext ctx,
            @NotNull TypedArray<?> key,
            @NotNull TypedArray<?> message
    ) {
        ByteTypedArray s = ByteTypedArray.copyOf(ctx.initialVector());
        ByteTypedArray c = ByteTypedArray.copyOf(message);
        int m = s.length();
        ByteTypedArray t = new ByteTypedArray(m, c.order());
        int b = this.shiftBits(ctx) >> 3;
        int cb = c.length();
        int r = cb % b;
        int q = (cb - r) / b;
        TypedArray<?> sk = ctx.keySchedule(key, false); // TODO: suspicious

        for (int i=0; i < q; i++) {
            t.set(s);

            ctx.process(sk, s); // TODO: suspicious

            for (int j=0; j < b; j++) {
                int d = i * b + j;
                t.setByte(j, c.getByte(d));
                c.setByte(d, c.getByte(d) ^ s.getByte(j));
            }

            for (int j=0; j < m - b; j++) {
                s.setByte(j, t.getByte(b + j));
            }

            for (int j=0; j < b; j++) {
                s.setByte(m - b + j, t.getByte(j));
            }

            // TODO: key meshing
        }

        if (r > 0) {
            ctx.process(sk, s); // TODO: suspicious

            for (int i=0; i < r; i++) {
                int d = q * b + i;
                c.setByte(d, c.getByte(d) ^ s.getByte(i));
            }
        }

        return c;
    }

}
