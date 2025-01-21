package io.github.wasabithumb.magma4j.ctx.mode.instance;

import io.github.wasabithumb.magma4j.ctx.CipherContext;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
abstract class AbstractCipherModeInstance implements CipherModeInstance {

    private final int shiftBits;

    //

    protected AbstractCipherModeInstance(int shiftBits) {
        if (shiftBits < 0)
            throw new IllegalArgumentException("Invalid shift bits (" + shiftBits + ")");
        this.shiftBits = shiftBits;
    }

    public AbstractCipherModeInstance() {
        this.shiftBits = -1;
    }

    //

    protected final int shiftBits(int fallback) {
        if (this.shiftBits == -1) return fallback;
        return this.shiftBits;
    }

    protected final int shiftBits(@NotNull CipherContext ctx) {
        return this.shiftBits(ctx.blockLength());
    }

    //

    @Override
    public @NotNull String name() {
        if (this.shiftBits == -1) {
            return this.mode().name();
        } else {
            return this.mode().name() + "-" + this.shiftBits;
        }
    }

    //

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + this.mode().hashCode();
        hash = 31 * hash + this.shiftBits;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj instanceof AbstractCipherModeInstance other) {
            return this.mode() == other.mode() &&
                    this.shiftBits == other.shiftBits;
        }
        return super.equals(obj);
    }

}
