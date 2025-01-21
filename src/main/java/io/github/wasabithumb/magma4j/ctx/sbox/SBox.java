package io.github.wasabithumb.magma4j.ctx.sbox;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

/**
 * 128-element nibble array
 * @see #STANDARD
 */
public interface SBox {

    @Contract("_, _, _, _, _, _, _, _ -> new")
    static @NotNull SBox of(long l1, long l2, long l3, long l4, long l5, long l6, long l7, long l8) {
        return new CompactSBox(new long[] { l1, l2, l3, l4, l5, l6, l7, l8 });
    }

    /** See <a href="https://www.rfc-editor.org/rfc/rfc7836#appendix-C">RFC 7836 Appendix C</a> */
    SBox STANDARD = of(
            0x1F307D8E9B5A264CL,
            0xF0DB74E1C5A93286L,
            0x069C471EDAF2853BL,
            0xB9E35A076F4D128CL,
            0xC24BE390D618A5F7L,
            0x0E34187BAC296FD5L,
            0x73AD0B4FC19652E8L,
            0x2BC96AF43850DE71L
    );

    //

    /**
     * @param index Nibble index
     * @return Corresponding nibble
     */
    @Range(from=0, to=15) int get(@Range(from=0, to=127) int index);

}
