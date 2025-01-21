package io.github.wasabithumb.magma4j.ctx.pad;

import io.github.wasabithumb.magma4j.io.typed.ByteTypedArray;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@ApiStatus.NonExtendable
public interface PaddingMethod {

    /** PKCS#5 padding */
    PaddingMethod PKCS5 = new PKCS5PaddingMethod();

    /** Pad with random bytes */
    PaddingMethod RANDOM = new RandomPaddingMethod();

    /** Pad with 1 followed by zeros */
    PaddingMethod BIT = new BitPaddingMethod();

    /** Pad with zeros */
    PaddingMethod ZERO = new ZeroPaddingMethod();

    /** No padding */
    PaddingMethod NONE = new NonePaddingMethod();

    //

    @NotNull String name();

    @Contract("_, _ -> new")
    @NotNull ByteTypedArray pad(@NotNull ByteTypedArray in, int blockSize);

    @Contract("_, _ -> new")
    default @NotNull ByteTypedArray unpad(@NotNull ByteTypedArray in, int blockSize) {
        ByteTypedArray cpy = new ByteTypedArray(in.length(), in.order());
        cpy.set(in);
        return cpy;
    }

}
