package io.github.wasabithumb.magma4j.ctx.sbox;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

@ApiStatus.Internal
final class CompactSBox extends AbstractSBox {

    private final long[] data;
    CompactSBox(long @NotNull [] data) {
        if (data.length != 8)
            throw new IllegalArgumentException("Incorrect length of data (expected 8, got " + data.length + ")");
        this.data = data;
    }

    @Override
    public @Range(from = 0, to = 15) int get(@Range(from = 0, to = 127) int index) {
        this.boundsCheck(index);
        long d = this.data[index >> 4];
        return (int) ((d >>> ((index & 15) << 2)) & 15L);
    }

}
