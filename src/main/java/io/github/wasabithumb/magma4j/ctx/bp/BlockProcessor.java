package io.github.wasabithumb.magma4j.ctx.bp;

import io.github.wasabithumb.magma4j.io.typed.TypedArray;
import io.github.wasabithumb.magma4j.ctx.sbox.SBox;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.NonExtendable
public interface BlockProcessor {

    BlockProcessor MAGMA_64 = new Magma64BlockProcessor();

    BlockProcessor MAGMA_128 = new Magma128BlockProcessor();

    //

    /**
     * @param s An S-Box
     * @param key Scheduled key
     * @param block Cipher block
     * @param offset Offset
     * @param decrypt True if decrypt
     */
    void process(@NotNull SBox s, @NotNull TypedArray<?> key, @NotNull TypedArray<?> block, int offset, boolean decrypt);

}
