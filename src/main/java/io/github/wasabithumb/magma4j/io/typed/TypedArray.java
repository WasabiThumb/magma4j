package io.github.wasabithumb.magma4j.io.typed;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

@ApiStatus.NonExtendable
public interface TypedArray<T> {

    @NotNull ByteBuffer raw();

    default @NotNull ByteOrder order() {
        return this.raw().order();
    }

    int length();

    int elementSize();

    @NotNull T get(int index) throws IndexOutOfBoundsException;

    void set(int index, @NotNull T value) throws IndexOutOfBoundsException;

    default void set(@NotNull TypedArray<T> other, int offset) {
        this.raw().put(
                offset * this.elementSize(),
                other.raw(),
                0,
                other.raw().limit()
        );
    }

    default void set(@NotNull TypedArray<T> other) {
        this.set(other, 0);
    }

    @NotNull TypedArray<T> slice(int offset, int length);

    //

    default @NotNull ByteTypedArray asByte() {
        return new ByteTypedArray(this.raw());
    }

    default @NotNull IntTypedArray asInt() {
        return new IntTypedArray(this.raw());
    }

}
