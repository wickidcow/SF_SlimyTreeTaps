package io.github.thebusybiscuit.slimytreetaps;

/** Value-preserving conversion helper for vanilla resin forms. */
final class ResinConversion {

    private ResinConversion() {}

    static Recipe forClumpValue(int clumpValue, int clumpsPerRubber) {
        if (clumpValue < 1) {
            throw new IllegalArgumentException("clumpValue must be positive");
        }
        if (clumpsPerRubber < 1) {
            throw new IllegalArgumentException("clumpsPerRubber must be positive");
        }

        int gcd = greatestCommonDivisor(clumpValue, clumpsPerRubber);
        int inputAmount = clumpsPerRubber / gcd;
        int outputAmount = clumpValue / gcd;
        int processingSeconds = 4 * outputAmount;
        return new Recipe(inputAmount, outputAmount, processingSeconds);
    }

    private static int greatestCommonDivisor(int a, int b) {
        int x = a;
        int y = b;
        while (y != 0) {
            int remainder = x % y;
            x = y;
            y = remainder;
        }
        return x;
    }

    record Recipe(int inputAmount, int outputAmount, int processingSeconds) {}
}
