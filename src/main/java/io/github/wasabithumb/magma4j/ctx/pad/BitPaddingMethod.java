package io.github.wasabithumb.magma4j.ctx.pad;

import io.github.wasabithumb.magma4j.io.typed.ByteTypedArray;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
final class BitPaddingMethod implements PaddingMethod {

    @Override
    public @NotNull String name() {
        return "BIT";
    }

    @Override
    public @NotNull ByteTypedArray pad(@NotNull ByteTypedArray in, int blockSize) {
        int n = in.length();
        int m = Math.ceilDiv(n + 1, blockSize) * blockSize;
        ByteTypedArray ret = new ByteTypedArray(m, in.order());
        ret.set(in);

        ret.setByte(n, 1);
        for (int i=(n + 1); i < m; i++) {
            ret.setByte(i, 0);
        }

        return ret;
    }

    @Override
    public @NotNull ByteTypedArray unpad(@NotNull ByteTypedArray in, int blockSize) {
        int n = in.length();
        byte b;
        do {
            b = in.getByte(--n);
        } while (b == 0 && n > 1);

        if (b != 1)
            throw new IllegalArgumentException("Invalid padding");

        ByteTypedArray ret = new ByteTypedArray(n, in.order());
        if (n > 0) ret.set(in.slice(0, n));
        return ret;
    }

}
