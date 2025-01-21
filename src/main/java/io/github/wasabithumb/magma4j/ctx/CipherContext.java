package io.github.wasabithumb.magma4j.ctx;

import io.github.wasabithumb.magma4j.ctx.bp.BlockProcessor;
import io.github.wasabithumb.magma4j.ctx.ks.KeySchedule;
import io.github.wasabithumb.magma4j.ctx.pad.PaddingMethod;
import io.github.wasabithumb.magma4j.ctx.sbox.SBox;
import io.github.wasabithumb.magma4j.io.typed.ByteTypedArray;
import io.github.wasabithumb.magma4j.io.typed.TypedArray;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@ApiStatus.NonExtendable
public interface CipherContext {

    @NotNull SBox sBox();

    @NotNull KeySchedule keySchedule();

    @NotNull PaddingMethod padding();

    @NotNull BlockProcessor blockProcessor();

    int blockLength();

    default int blockSize() {
        return this.blockLength() >> 3;
    }

    @NotNull ByteTypedArray initialVector();

    //

    @ApiStatus.Internal
    default @NotNull TypedArray<?> keySchedule(@NotNull TypedArray<?> key, boolean decrypt) {
        return this.keySchedule().process(key, decrypt);
    }

    @ApiStatus.Internal
    @Contract("_ -> new")
    default @NotNull ByteTypedArray pad(@NotNull TypedArray<?> in) {
        return this.padding().pad(in.asByte(), this.blockSize());
    }

    @ApiStatus.Internal
    @Contract("_ -> new")
    default @NotNull ByteTypedArray unpad(@NotNull TypedArray<?> in) {
        return this.padding().unpad(in.asByte(), this.blockSize());
    }

    @ApiStatus.Internal
    default void process(@NotNull TypedArray<?> key, @NotNull TypedArray<?> block, int offset, boolean decrypt) {
        this.blockProcessor().process(this.sBox(), key, block, offset, decrypt);
    }

    @ApiStatus.Internal
    default void process(@NotNull TypedArray<?> key, @NotNull TypedArray<?> block, int offset) {
        this.process(key, block, offset, false);
    }

    @ApiStatus.Internal
    default void process(@NotNull TypedArray<?> key, @NotNull TypedArray<?> block) {
        this.process(key, block, 0);
    }

}
