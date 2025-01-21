package io.github.wasabithumb.magma4j.ctx.bp;

import io.github.wasabithumb.magma4j.io.typed.ByteTypedArray;
import io.github.wasabithumb.magma4j.io.typed.TypedArray;
import static io.github.wasabithumb.magma4j.util.CipherFunctions.*;

import io.github.wasabithumb.magma4j.ctx.sbox.SBox;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
final class Magma128BlockProcessor implements BlockProcessor {

    @Override
    public void process(
            @NotNull SBox s,
            @NotNull TypedArray<?> key,
            @NotNull TypedArray<?> block,
            int offset,
            boolean decrypt
    ) {
        ByteTypedArray key8 = key.asByte();
        ByteTypedArray dat = block.asByte().slice(offset, 16);
        if (decrypt) {
            for (int i=0; i < 9; i++) {
                funcReverseLSX(
                        dat,
                        key8.slice((9 - i) * 16, 16)
                );
            }
            funcX(dat, key8.slice(0, 16));
        } else {
            for (int i=0; i < 9; i++) {
                funcLSX(
                        dat,
                        key8.slice(i * 16, 16)
                );
            }
            funcX(dat, key8.slice(144, 16));
        }
    }

}
