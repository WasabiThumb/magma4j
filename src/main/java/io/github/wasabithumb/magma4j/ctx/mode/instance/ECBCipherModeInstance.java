package io.github.wasabithumb.magma4j.ctx.mode.instance;

import io.github.wasabithumb.magma4j.ctx.CipherContext;
import io.github.wasabithumb.magma4j.ctx.mode.CipherMode;
import io.github.wasabithumb.magma4j.io.typed.ByteTypedArray;
import io.github.wasabithumb.magma4j.io.typed.TypedArray;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public final class ECBCipherModeInstance extends AbstractCipherModeInstance {

    public ECBCipherModeInstance() {
        super();
    }

    //

    @Override
    public @NotNull CipherMode mode() {
        return CipherMode.ECB;
    }

    @Override
    public @NotNull TypedArray<?> encrypt(
            @NotNull CipherContext ctx,
            @NotNull TypedArray<?> key,
            @NotNull TypedArray<?> message
    ) {
        TypedArray<?> p = ctx.pad(message);
        int n = ctx.blockSize();
        int b = p.length() / n;
        TypedArray<?> k = ctx.keySchedule(key, false);

        for (int i=0; i < b; i++) {
            ctx.process(k, p, n * i, false);
        }

        return p;
    }

    @Override
    public @NotNull TypedArray<?> decrypt(
            @NotNull CipherContext ctx,
            @NotNull TypedArray<?> key,
            @NotNull TypedArray<?> message
    ) {
        ByteTypedArray p = ByteTypedArray.copyOf(message);

        int n = ctx.blockSize();
        int b = p.length() / n;
        TypedArray<?> sk = ctx.keySchedule(key, true);

        for (int i=0; i < b; i++) {
            ctx.process(sk, p, n * i, true);
        }

        return ctx.unpad(p);
    }

}
