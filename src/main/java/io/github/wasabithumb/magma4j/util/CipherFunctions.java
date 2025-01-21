package io.github.wasabithumb.magma4j.util;

import io.github.wasabithumb.magma4j.io.ByteAccess;
import static io.github.wasabithumb.magma4j.util.CipherConstants.*;

import io.github.wasabithumb.magma4j.io.IntAccess;
import io.github.wasabithumb.magma4j.ctx.sbox.SBox;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
public final class CipherFunctions {

    public static void funcR(@NotNull ByteAccess a) {
        int sum = 0;
        for (int i=0; i < 16; i++) {
            sum ^= (R_TABLE[KB[i]][a.getByte(i) & 0xFF] & 0xFF);
        }

        // TODO: try to remove this
        if (a.length() > 16)
            a.setByte(16, a.getByte(15));

        for (int i=15; i > 0; i--)
            a.setByte(i, a.getByte(i - 1));

        a.setByte(0, sum);
    }

    public static void funcReverseR(@NotNull ByteAccess a) {
        byte tmp = a.getByte(0);
        for (int i=0; i < 15; i++) {
            a.setByte(i, a.getByte(i + 1));
        }
        a.setByte(15, tmp);

        int sum = 0;
        for (int i=0; i < 16; i++) {
            sum ^= (R_TABLE[KB[i]][a.getByte(i) & 0xFF] & 0xFF);
        }
        a.setByte(15, sum);
    }

    public static void funcC(int n, @NotNull ByteAccess a) {
        for (int i=0; i < 15; i++) a.setByte(i, (byte) 0);
        a.setByte(15, (byte) n);
        funcL(a);
    }

    public static void funcL(@NotNull ByteAccess a) {
        for (int i=0; i < 16; i++)
            funcR(a);
    }

    public static void funcReverseL(@NotNull ByteAccess a) {
        for (int i=0; i < 16; i++)
            funcReverseR(a);
    }

    public static void funcF(@NotNull ByteAccess k1, @NotNull ByteAccess k2, @NotNull ByteAccess c) {
        ByteAccess tmp = ByteAccess.copyOf(k1);

        funcLSX(k1, c);
        funcX(k1, k2);

        for (int i=0; i < tmp.length(); i++)
            k2.setByte(i, tmp.getByte(i));
    }

    public static void funcLSX(@NotNull ByteAccess a, @NotNull ByteAccess b) {
        funcX(a, b);
        funcS(a);
        funcL(a);
    }

    public static void funcS(@NotNull ByteAccess a) {
        for (int i=0; i < 16; i++) {
            a.setByte(i, K_PI[a.getByte(i) & 0xFF]);
        }
    }

    public static void funcReverseS(@NotNull ByteAccess a) {
        for (int i=0; i < 16; i++) {
            a.setByte(i, REVERSE_K_PI[a.getByte(i) & 0xFF]);
        }
    }

    public static void funcX(@NotNull ByteAccess a, @NotNull ByteAccess b) {
        for (int i=0; i < 16; i++) {
            a.setByte(
                    i,
                    a.getByte(i) ^ b.getByte(i)
            );
        }
    }

    public static void funcReverseLSX(@NotNull ByteAccess a, @NotNull ByteAccess b) {
        funcX(a, b);
        funcReverseL(a);
        funcReverseS(a);
    }

    public static void round(@NotNull SBox s, @NotNull IntAccess block, int k) {
        int cm = block.getInt(0) + k;

        int om = s.get(cm & 0xF);
        om |= s.get( 16 + ((cm >>  4) & 0xF)) << 4;
        om |= s.get( 32 + ((cm >>  8) & 0xF)) << 8;
        om |= s.get( 48 + ((cm >> 12) & 0xF)) << 12;
        om |= s.get( 64 + ((cm >> 16) & 0xF)) << 16;
        om |= s.get( 80 + ((cm >> 20) & 0xF)) << 20;
        om |= s.get( 96 + ((cm >> 24) & 0xF)) << 24;
        om |= s.get(112 + ((cm >> 28) & 0xF)) << 28;
        cm = (om << 11) | (om >>> 21);

        cm ^= block.getInt(1);
        block.setInt(1, block.getInt(0));
        block.setInt(0, cm);
    }

}
