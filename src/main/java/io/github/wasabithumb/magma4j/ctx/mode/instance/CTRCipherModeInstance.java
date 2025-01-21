package io.github.wasabithumb.magma4j.ctx.mode.instance;

import io.github.wasabithumb.magma4j.ctx.CipherContext;
import io.github.wasabithumb.magma4j.ctx.mode.CipherMode;
import io.github.wasabithumb.magma4j.io.typed.ByteTypedArray;
import io.github.wasabithumb.magma4j.io.typed.IntTypedArray;
import io.github.wasabithumb.magma4j.io.typed.TypedArray;
import org.jetbrains.annotations.NotNull;

public final class CTRCipherModeInstance extends AbstractCipherModeInstance {

    public CTRCipherModeInstance(int shiftBits) {
        super(shiftBits);
    }

    public CTRCipherModeInstance() {
        super();
    }

    //

    @Override
    public @NotNull CipherMode mode() {
        return CipherMode.CTR;
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

    private @NotNull TypedArray<?> process(
            @NotNull CipherContext ctx,
            @NotNull TypedArray<?> k,
            @NotNull TypedArray<?> message
    ) {
        ByteTypedArray c = ByteTypedArray.copyOf(message);
        int n = ctx.blockSize();
        int b = this.shiftBits(ctx) >> 3;
        int cb = c.length();
        int r = cb % b;
        int q = (cb - r) / b;
        ByteTypedArray s = new ByteTypedArray(n, c.order());
        IntTypedArray t = new IntTypedArray(n, c.order());
        TypedArray<?> key = ctx.keySchedule(k, false);

        s.set(ctx.initialVector());
        for (int i=0; i < q; i++) {
            for (int j=0; j < n; j++) {
                t.setInt(j, s.getByte(j) & 0xFF);
            }

            ctx.process(key, s);

            for (int j=0; j < b; j++) {
                int d = b * i + j;
                c.setByte(d, c.getByte(d) ^ s.getByte(j));
            }

            for (int j=0; j < n; j++) {
                s.setByte(j, t.getInt(j));
            }

            for (int j=(n - 1); i >= 0; i--) {
                int d = s.getByte(j) & 0xFF;
                if (d > 0xFE) {
                    s.setByte(j, d - 0xFE);
                } else {
                    s.setByte(j, d + 1);
                    break;
                }
            }
        }

        if (r > 0) {
            ctx.process(key, s);

            for (int j=0; j < r; j++) {
                int d = b * q + j;
                c.setByte(d, c.getByte(d) ^ s.getByte(j));
            }
        }

        return c;
    }

}
