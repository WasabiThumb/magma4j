package io.github.wasabithumb.magma4j.ctx.pad;

import io.github.wasabithumb.magma4j.io.typed.ByteTypedArray;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.security.SecureRandom;
import java.util.Random;

@ApiStatus.Internal
final class RandomPaddingMethod implements PaddingMethod {

    private static final Random RANDOM = new SecureRandom();

    @Override
    public @NotNull String name() {
        return "RANDOM";
    }

    @Override
    public @NotNull ByteTypedArray pad(@NotNull ByteTypedArray in, int blockSize) {
        int n = in.length();
        int q = blockSize - n % blockSize;
        int m = Math.ceilDiv(n, blockSize) * blockSize;

        ByteTypedArray ret = new ByteTypedArray(m, in.order());
        ret.set(in);

        byte[] rand = new byte[q];
        RANDOM.nextBytes(rand);
        ret.raw().put(rand, n, q);

        return ret;
    }

}
