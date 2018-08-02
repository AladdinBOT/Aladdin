package net.heyzeer0.aladdin.utils;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by HeyZeer0 on 02/08/2018.
 * Copyright © HeyZeer0 - 2016
 */
public class RandomSeed {

    private static final long multiplier = 0x5DEECE66DL;
    private static final long addend = 0xBL;
    private static final long mask = (1L << 48) - 1;
    private static final AtomicLong seedUniquifier = new AtomicLong(8682522807148012L);

    AtomicLong seed;
    long lastSeed;

    public RandomSeed() { seed = new AtomicLong(seedUniquifier() ^ System.nanoTime()); }

    private static long seedUniquifier() {
        // L'Ecuyer, "Tables of Linear Congruential Generators of
        // Different Sizes and Good Lattice Structure", 1999
        for (;;) {
            long current = seedUniquifier.get();
            long next = current * 181783497276652981L;
            if (seedUniquifier.compareAndSet(current, next))
                return next;
        }
    }

    private int next(int bits) {
        long oldseed, nextseed;
        AtomicLong seed = this.seed;
        do {
            oldseed = seed.get();
            nextseed = (oldseed * multiplier + addend) & mask;
            lastSeed = nextseed;
        } while (!seed.compareAndSet(oldseed, nextseed));
        return (int)(nextseed >>> (48 - bits));
    }

    public int nextInt(int bound) {
        if (bound <= 0)
            throw new IllegalArgumentException("bound must be positive");

        int r = next(31);
        int m = bound - 1;
        if ((bound & m) == 0)  // i.e., bound is a power of 2
            r = (int)((bound * (long)r) >> 31);
        else {
            for (int u = r;
                 u - (r = u % bound) + m < 0;
                 u = next(31))
                ;
        }
        return r;
    }

    public Optional<Long> getLastSeed() {
        return Optional.of(lastSeed);
    }


}
