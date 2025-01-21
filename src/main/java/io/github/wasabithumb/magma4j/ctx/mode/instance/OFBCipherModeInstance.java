package io.github.wasabithumb.magma4j.ctx.mode.instance;

import io.github.wasabithumb.magma4j.ctx.CipherContext;
import io.github.wasabithumb.magma4j.ctx.mode.CipherMode;
import io.github.wasabithumb.magma4j.io.typed.ByteTypedArray;
import io.github.wasabithumb.magma4j.io.typed.TypedArray;
import org.jetbrains.annotations.NotNull;

public final class OFBCipherModeInstance extends AbstractCipherModeInstance {

    public OFBCipherModeInstance(int shiftBits) {
        super(shiftBits);
    }

    public OFBCipherModeInstance() {
        super();
    }

    //

    @Override
    public @NotNull CipherMode mode() {
        return CipherMode.OFB;
    }

    @Override
    public @NotNull TypedArray<?> encrypt(
            @NotNull CipherContext ctx,
            @NotNull TypedArray<?> key,
            @NotNull TypedArray<?> message
    ) {
        return this.process(ctx, key, message);
    }

    @Override
    public @NotNull TypedArray<?> decrypt(
            @NotNull CipherContext ctx,
            @NotNull TypedArray<?> key,
            @NotNull TypedArray<?> message
    ) {
        return this.process(ctx, key, message);
    }

    public @NotNull TypedArray<?> process(
            @NotNull CipherContext ctx,
            @NotNull TypedArray<?> k,
            @NotNull TypedArray<?> message
    ) {
        ByteTypedArray s = ByteTypedArray.copyOf(ctx.initialVector());
        ByteTypedArray c = ByteTypedArray.copyOf(message);
        int m = s.length();
        ByteTypedArray t = new ByteTypedArray(m, c.order());
        int b = this.shiftBits(ctx) >> 3;
        ByteTypedArray p = new ByteTypedArray(b, c.order());
        int cb = c.length();
        int r = cb % b;
        int q = (cb - r) / b;
        TypedArray<?> key = ctx.keySchedule(k, false);

        for (int i=0; i < q; i++) {
            t.set(s);

            ctx.process(key, s);

            for (int j=0; j < b; j++)
                p.setByte(j, s.getByte(j));

            for (int j=0; j < b; j++) {
                int d = i * b + j;
                c.setByte(d, c.getByte(d) ^ s.getByte(j));
            }

            for (int j=0; j < m - b; j++)
                s.setByte(j, t.getByte(b + j));

            for (int j=0; j < b; j++)
                s.setByte(m - b + j, p.getByte(j));

            // TODO: Key meshing
        }

        if (r > 0) {
            ctx.process(key, s);

            for (int i=0; i < r; i++) {
                int d = q * b + i;
                c.setByte(d, c.getByte(d) ^ s.getByte(i));
            }
        }

        return c;
    }

}
