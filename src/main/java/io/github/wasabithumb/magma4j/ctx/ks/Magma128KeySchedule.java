package io.github.wasabithumb.magma4j.ctx.ks;

import io.github.wasabithumb.magma4j.io.typed.ByteTypedArray;
import io.github.wasabithumb.magma4j.io.typed.TypedArray;
import static io.github.wasabithumb.magma4j.util.CipherFunctions.*;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
final class Magma128KeySchedule implements KeySchedule {

    @Override
    public @NotNull ByteTypedArray process(@NotNull TypedArray<?> key, boolean decrypt) {
        ByteTypedArray k = new ByteTypedArray(160, key.order());
        ByteTypedArray c = new ByteTypedArray(16, key.order());
        k.set(key.asByte());

        for (int u=0; u < 4; u++) {
            int u0 = u << 5;
            int u1 = u0 + 32;
            k.set(k.slice(u0, 32), u1);

            for (int v=1; v < 9; v++) {
                funcC(u * 8 + v, c);
                funcF(
                        k.slice(u1, 16),
                        k.slice(u1 + 16, 16),
                        c
                );
            }
        }

        return k;
    }

}
