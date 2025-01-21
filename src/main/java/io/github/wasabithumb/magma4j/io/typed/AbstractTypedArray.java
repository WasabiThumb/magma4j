package io.github.wasabithumb.magma4j.io.typed;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;

@ApiStatus.Internal
abstract class AbstractTypedArray<T> implements TypedArray<T> {

    protected final ByteBuffer raw;
    protected final int length;
    protected final int elementSize;

    public AbstractTypedArray(@NotNull ByteBuffer raw, int elementSize) {
        int len = raw.limit();
        if ((len % elementSize) != 0)
            throw new IllegalArgumentException("Buffer length " + len + " is not multiple of " + elementSize);

        this.raw = raw;
        this.length = len / elementSize;
        this.elementSize = elementSize;
    }

    //

    @Override
    public @NotNull ByteBuffer raw() {
        return this.raw;
    }

    @Override
    public int elementSize() {
        return this.elementSize;
    }

    @Override
    public int length() {
        return this.length;
    }

    @Contract("_ -> param1")
    protected final int boundsCheck(int index) throws IndexOutOfBoundsException {
        if (index < 0 || index >= this.length)
            throw new IndexOutOfBoundsException("Index out of bounds for length " + this.length);
        return index;
    }

}
