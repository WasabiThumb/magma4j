package io.github.wasabithumb.magma4j.ctx.ks;

import io.github.wasabithumb.magma4j.io.typed.TypedArray;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.NonExtendable
public interface KeySchedule {

    KeySchedule MAGMA_64 = new Magma64KeySchedule();

    KeySchedule MAGMA_128 = new Magma128KeySchedule();

    //

    /**
     * Executes a key schedule algorithm.
     * @param key Key ctx
     * @param decrypt True if decrypt
     * @return Scheduled key
     */
    @NotNull TypedArray<?> process(@NotNull TypedArray<?> key, boolean decrypt);

}
