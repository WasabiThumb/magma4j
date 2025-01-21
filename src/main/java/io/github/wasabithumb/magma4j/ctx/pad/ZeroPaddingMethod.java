package io.github.wasabithumb.magma4j.ctx.pad;

import io.github.wasabithumb.magma4j.io.typed.ByteTypedArray;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
final class ZeroPaddingMethod implements PaddingMethod {

    @Override
    public @NotNull String name() {
        return "ZERO";
    }

    @Override
    public @NotNull ByteTypedArray pad(@NotNull ByteTypedArray in, int blockSize) {
        int m = Math.ceilDiv(in.length(), blockSize) * blockSize;
        ByteTypedArray ret = new ByteTypedArray(m, in.order());
        ret.set(in);
        return ret;
    }

}
