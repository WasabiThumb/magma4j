package io.github.wasabithumb.magma4j.ctx.bp;

import io.github.wasabithumb.magma4j.io.typed.IntTypedArray;
import io.github.wasabithumb.magma4j.io.typed.TypedArray;
import io.github.wasabithumb.magma4j.ctx.sbox.SBox;
import io.github.wasabithumb.magma4j.util.CipherFunctions;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
final class Magma64BlockProcessor implements BlockProcessor {

    @Override
    public void process(
            @NotNull SBox s,
            @NotNull TypedArray<?> key,
            @NotNull TypedArray<?> block,
            int offset,
            boolean decrypt
    ) {
        IntTypedArray dat = block.asInt().slice(offset, 2);

        int tmp = Integer.reverseBytes(dat.getInt(0));
        dat.setInt(0, Integer.reverseBytes(dat.getInt(1)));
        dat.setInt(1, tmp);

        IntTypedArray key32 = key.asInt();
        for (int i=0; i < 32; i++) {
            CipherFunctions.round(s, dat, key32.getInt(i));
        }

        for (int i=0; i < 2; i++) {
            dat.setInt(i, Integer.reverseBytes(dat.getInt(i)));
        }
    }

}
