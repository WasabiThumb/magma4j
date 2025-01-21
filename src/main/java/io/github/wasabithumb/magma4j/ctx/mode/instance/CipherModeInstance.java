package io.github.wasabithumb.magma4j.ctx.mode.instance;

import io.github.wasabithumb.magma4j.io.typed.TypedArray;
import io.github.wasabithumb.magma4j.ctx.CipherContext;
import io.github.wasabithumb.magma4j.ctx.mode.CipherMode;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.NonExtendable
public interface CipherModeInstance {

    @NotNull CipherMode mode();

    @NotNull String name();

    default boolean supportsPadding() {
        return this.mode().supportsPadding();
    }

    @NotNull TypedArray<?> encrypt(
            @NotNull CipherContext ctx,
            @NotNull TypedArray<?> key,
            @NotNull TypedArray<?> message
    );

    @NotNull TypedArray<?> decrypt(
            @NotNull CipherContext ctx,
            @NotNull TypedArray<?> key,
            @NotNull TypedArray<?> message
    );

}
