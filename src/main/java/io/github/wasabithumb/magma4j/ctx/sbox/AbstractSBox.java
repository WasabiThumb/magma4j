package io.github.wasabithumb.magma4j.ctx.sbox;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
abstract class AbstractSBox implements SBox {

    private static final String TRIM = "+-----------------------------+";

    //

    protected final void boundsCheck(int index) throws IndexOutOfBoundsException {
        if (index < 0 || index > 127)
            throw new IndexOutOfBoundsException("Index " + index + " out of bounds for length 128");
    }

    //

    @Override
    public @NotNull String toString() {
        StringBuilder ret = new StringBuilder(319);
        ret.append(TRIM).append('\n');
        for (int i=0; i < 128; i++) {
            if ((i & 15) != 0) ret.append(' ');
            ret.append(Character.forDigit(this.get(i), 16));
            if ((i & 15) == 15) ret.append('\n');
        }
        ret.append(TRIM);
        return ret.toString();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        for (int i=0; i < 128; i++) {
            hash = 31 * hash + this.get(i);
        }
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj instanceof SBox other) {
            int i = 0;
            while (this.get(i) == other.get(i)) {
                if (++i == 128) return true;
            }
        }
        return super.equals(obj);
    }

}
