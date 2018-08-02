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

    int lastGeneratedNumber;

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

    private int next() {
        long oldseed, nextseed;
        AtomicLong seed = this.seed;
        do {
            oldseed = seed.get();
            nextseed = (oldseed * multiplier + addend) & mask;
            lastSeed = nextseed;
        } while (!seed.compareAndSet(oldseed, nextseed));

        lastGeneratedNumber = (int)nextseed >>> (17);
        return lastGeneratedNumber;
    }

    public int nextInt(int bound) {
        if (bound <= 0)
            throw new IllegalArgumentException("bound must be positive");

        int r = next();
        int m = bound - 1;
        if ((bound & m) == 0)  // i.e., bound is a power of 2
            r = (int)((bound * (long)r) >> 31);
        else {
            for (int u = r; u - (r = u % bound) + m < 0; u = next()) ;
        }
        return r;
    }

    public Optional<Long> getLastSeed() {
        return Optional.of(lastSeed);
    }

    public Optional<Integer> getLastGeneratedNumber() {
        return Optional.of(lastGeneratedNumber);
    }

}
