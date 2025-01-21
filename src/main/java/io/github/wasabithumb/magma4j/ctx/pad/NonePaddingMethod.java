package io.github.wasabithumb.magma4j.ctx.pad;

import io.github.wasabithumb.magma4j.io.typed.ByteTypedArray;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
final class NonePaddingMethod implements PaddingMethod {

    @Override
    public @NotNull String name() {
        return "NO";
    }

    @Override
    public @NotNull ByteTypedArray pad(@NotNull ByteTypedArray in, int blockSize) {
        return this.unpad(in, blockSize);
    }

}
