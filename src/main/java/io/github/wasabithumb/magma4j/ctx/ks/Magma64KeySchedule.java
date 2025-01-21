package io.github.wasabithumb.magma4j.ctx.ks;

import io.github.wasabithumb.magma4j.io.typed.IntTypedArray;
import io.github.wasabithumb.magma4j.io.typed.TypedArray;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
final class Magma64KeySchedule implements KeySchedule {

    @Override
    public @NotNull IntTypedArray process(@NotNull TypedArray<?> key, boolean decrypt) {
        IntTypedArray ret = new IntTypedArray(32, key.order());
        IntTypedArray k = key.asInt();

        for (int i=0; i < 8; i++) {
            ret.setInt(i, Integer.reverseBytes(k.getInt(i)));
        }

        if (decrypt) {
            for (int i=0; i < 8; i++) {
                ret.setInt(i + 8, ret.getInt(7 - i));
                ret.setInt(i + 16, ret.getInt(7 - i));
            }
        } else {
            for (int i=0; i < 8; i++) {
                ret.setInt(i + 8, ret.getInt(i));
                ret.setInt(i + 16, ret.getInt(i));
            }
        }

        for (int i = 0; i < 8; i++)
            ret.setInt(i + 24, ret.getInt(7 - i));

        return ret;
    }

}
