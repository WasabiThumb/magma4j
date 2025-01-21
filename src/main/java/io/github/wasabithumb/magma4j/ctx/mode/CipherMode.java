package io.github.wasabithumb.magma4j.ctx.mode;

import io.github.wasabithumb.magma4j.ctx.mode.instance.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.IntFunction;

public enum CipherMode {
    ECB(new ECBCipherModeInstance(),true),
    CTR(new CTRCipherModeInstance(), CTRCipherModeInstance::new),
    CBC(new CBCCipherModeInstance(), true),
    CFB(new CFBCipherModeInstance(), CFBCipherModeInstance::new),
    OFB(new OFBCipherModeInstance(), OFBCipherModeInstance::new);

    //

    private final CipherModeInstance defaultInstance;
    private final IntFunction<CipherModeInstance> withShiftBits;
    private final boolean supportsPadding;
    CipherMode(
            @NotNull CipherModeInstance defaultInstance,
            @Nullable IntFunction<CipherModeInstance> withShiftBits,
            boolean supportsPadding
    ) {
        this.defaultInstance = defaultInstance;
        this.withShiftBits = withShiftBits;
        this.supportsPadding = supportsPadding;
    }

    CipherMode(
            @NotNull CipherModeInstance defaultInstance,
            @Nullable IntFunction<CipherModeInstance> withShiftBits
    ) {
        this(defaultInstance, withShiftBits, false);
    }

    CipherMode(
            @NotNull CipherModeInstance defaultInstance,
            boolean supportsPadding
    ) {
        this(defaultInstance, null, supportsPadding);
    }

    //

    public @NotNull CipherModeInstance getInstance() {
        return this.defaultInstance;
    }

    public @NotNull CipherModeInstance getInstance(int shiftBits) throws UnsupportedOperationException {
        if (this.withShiftBits == null) {
            throw new UnsupportedOperationException("Mode " + this.name() + " does not support shift bits");
        }
        return this.withShiftBits.apply(shiftBits);
    }

    public boolean supportsPadding() {
        return this.supportsPadding;
    }

}
