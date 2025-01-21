package io.github.wasabithumb.magma4j.io;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.NonExtendable
public interface IntAccess {

    int length();

    int getInt(int index) throws IndexOutOfBoundsException;

    void setInt(int index, int i) throws IndexOutOfBoundsException;

}
