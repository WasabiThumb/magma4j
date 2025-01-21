package io.github.wasabithumb.magma4j.io.typed;

import io.github.wasabithumb.magma4j.io.IntAccess;
import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public final class IntTypedArray extends AbstractTypedArray<Integer> implements IntAccess {

    public IntTypedArray(@NotNull ByteBuffer raw) {
        super(raw, Integer.BYTES);
    }

    public IntTypedArray(int size, @NotNull ByteOrder order) {
        this(ByteBuffer.allocate(size * Integer.BYTES).order(order));
    }

    //

    @Override
    public @NotNull Integer get(int index) throws IndexOutOfBoundsException {
        return this.getInt(index);
    }

    @Override
    public void set(int index, @NotNull Integer value) throws IndexOutOfBoundsException {
        this.setInt(index, value);
    }

    @Override
    public @NotNull IntTypedArray slice(int offset, int length) {
        return new IntTypedArray(this.raw.slice(offset, length * Integer.BYTES).order(this.raw.order()));
    }

    @Override
    public @NotNull IntTypedArray asInt() {
        return this;
    }

    @Override
    public int getInt(int index) throws IndexOutOfBoundsException {
        return this.raw.getInt(this.boundsCheck(index) << 2);
    }

    @Override
    public void setInt(int index, int value) throws IndexOutOfBoundsException {
        this.raw.putInt(this.boundsCheck(index) << 2, value);
    }

    public int[] toArray() {
        int[] ret = new int[this.length];
        for (int i=0; i < this.length; i++) ret[i] = this.getInt(i);
        return ret;
    }

}
