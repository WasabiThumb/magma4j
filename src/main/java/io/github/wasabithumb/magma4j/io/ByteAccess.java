package io.github.wasabithumb.magma4j.io;

import io.github.wasabithumb.magma4j.io.typed.ByteTypedArray;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.nio.ByteOrder;

@ApiStatus.NonExtendable
public interface ByteAccess {

    static @NotNull ByteAccess copyOf(@NotNull ByteAccess other) {
        if (other instanceof ByteTypedArray bta) {
            return ByteTypedArray.copyOf(bta);
        } else {
            ByteTypedArray ret = new ByteTypedArray(other.length(), ByteOrder.LITTLE_ENDIAN);
            for (int i=0; i < other.length(); i++) {
                ret.setByte(i, other.getByte(i));
            }
            return ret;
        }
    }

    //

    int length();

    byte getByte(int index) throws IndexOutOfBoundsException;

    void setByte(int index, byte b) throws IndexOutOfBoundsException;

    default void setByte(int index, int b) throws IndexOutOfBoundsException {
        this.setByte(index, (byte) b);
    }

}
