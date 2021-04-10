package key_wallet.crypto;

public class LFSR {
    private int bits;
    private int mask;
    private int seed;
    private int bitMask;
    private int current;
    private int next;

    public LFSR(int bits, int mask, int seed) throws LFSRException {
        this.bits = bits;
        this.bitMask = (0x01 << bits) - 1;
        this.mask = mask & bitMask;
        this.seed = seed & bitMask;

        if (this.seed == 0) {
            throw new LFSRException("Seed is 0");
        }

        if (this.mask == 0) {
            throw new LFSRException("Mask is 0");
        }

        this.current = seed;
        this.next = next();
    }

    public byte nextByte() {
        for (int i = 0; i < 8; ++i) {
            next();
        }

        return (byte)current;
    }

    private int next() {
        return (current << 1) & bitMask | lsb();
    }

    private int lsb() {
        int bit = 0;

        for (int i = 0x01; i < bitMask; i <<= 1) {
            if ((mask & i) != 0 && (current & i) != 0) {
                bit ^= 0x01;
            }
        }

        return bit;
    }
}
