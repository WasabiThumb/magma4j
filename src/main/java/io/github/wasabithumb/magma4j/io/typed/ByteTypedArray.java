package io.github.wasabithumb.magma4j.io.typed;

import io.github.wasabithumb.magma4j.io.ByteAccess;
import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public record ByteTypedArray(
        @NotNull ByteBuffer raw
) implements TypedArray<Byte>, ByteAccess {

    public static @NotNull ByteTypedArray copyOf(@NotNull TypedArray<?> a) {
        ByteTypedArray ab = a.asByte();
        ByteTypedArray ret = new ByteTypedArray(ab.length(), ab.order());
        ret.set(ab);
        return ret;
    }

    //

    public ByteTypedArray(int size, @NotNull ByteOrder order) {
        this(ByteBuffer.allocate(size).order(order));
    }

    public ByteTypedArray(byte @NotNull [] array, @NotNull ByteOrder order) {
        this(ByteBuffer.wrap(array).order(order));
    }

    //

    @Override
    public @NotNull ByteBuffer raw() {
        return this.raw;
    }

    @Override
    public int elementSize() {
        return 1;
    }

    @Override
    public int length() {
        return this.raw.limit();
    }

    @Override
    public @NotNull Byte get(int index) throws IndexOutOfBoundsException {
        return this.getByte(index);
    }

    @Override
    public void set(int index, @NotNull Byte value) throws IndexOutOfBoundsException {
        this.setByte(index, value);
    }

    @Override
    public @NotNull ByteTypedArray asByte() {
        return this;
    }

    @Override
    public @NotNull ByteTypedArray slice(int offset, int length) {
        return new ByteTypedArray(this.raw.slice(offset, length).order(this.raw.order()));
    }

    //

    @Override
    public byte getByte(int index) throws IndexOutOfBoundsException {
        return this.raw.get(index);
    }

    @Override
    public void setByte(int index, byte value) throws IndexOutOfBoundsException {
        this.raw.put(index, value);
    }

    //

    public byte[] toArray() {
        if (this.raw.hasArray()) return this.raw.array();
        byte[] ret = new byte[this.length()];
        for (int i=0; i < ret.length; i++)
            ret[i] = this.getByte(i);
        return ret;
    }

}
