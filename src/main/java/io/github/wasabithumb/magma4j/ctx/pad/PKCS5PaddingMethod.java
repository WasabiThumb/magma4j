package io.github.wasabithumb.magma4j.ctx.pad;

import io.github.wasabithumb.magma4j.io.typed.ByteTypedArray;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
final class PKCS5PaddingMethod implements PaddingMethod {

    @Override
    public @NotNull String name() {
        return "PKCS5P";
    }

    @Override
    public @NotNull ByteTypedArray pad(@NotNull ByteTypedArray in, int blockSize) {
        int n = in.length();
        int q = blockSize - n % blockSize;
        int m = Math.ceilDiv(n + 1, blockSize) * blockSize;

        ByteTypedArray ret = new ByteTypedArray(m, in.order());
        ret.set(in);

        byte bq = (byte) q;
        for (int i=n; i < m; i++) {
            ret.setByte(i, bq);
        }
        return ret;
    }

    @Override
    public @NotNull ByteTypedArray unpad(@NotNull ByteTypedArray in, int blockSize) {
        int m = in.length();
        int q = in.getByte(m - 1) & 0xFF;
        int n = m - q;
        if (q > blockSize) throw new IllegalArgumentException("Invalid padding");

        ByteTypedArray ret = new ByteTypedArray(n, in.order());
        if (n > 0) {
            ret.set(in.slice(0, n));
        }
        return ret;
    }

}
