package io.github.thebusybiscuit.slimytreetaps;

/**
 * Calculates equivalent Rubber Factory recipes for vanilla resin forms.
 *
 * <p>The calculation preserves both resin value and processing time. For
 * example, with the default cost of four clumps per Rubber, one Resin Bricks
 * block (four clumps of value) becomes one Rubber in four seconds, while four
 * Resin Blocks (36 clumps of value) become nine Rubber in 36 seconds.</p>
 */
final class ResinConversion {

    private ResinConversion() {}

    static Recipe forClumpValue(int clumpValue, int clumpsPerRubber) {
        if (clumpValue < 1) {
            throw new IllegalArgumentException("clumpValue must be positive");
        }
        if (clumpsPerRubber < 1) {
            throw new IllegalArgumentException("clumpsPerRubber must be positive");
        }

        int divisor = greatestCommonDivisor(clumpValue, clumpsPerRubber);
        int inputAmount = clumpsPerRubber / divisor;
        int outputAmount = clumpValue / divisor;
        int processingSeconds = 4 * outputAmount;

        return new Recipe(inputAmount, outputAmount, processingSeconds);
    }

    private static int greatestCommonDivisor(int first, int second) {
        int a = first;
        int b = second;

        while (b != 0) {
            int remainder = a % b;
            a = b;
            b = remainder;
        }

        return a;
    }

    record Recipe(int inputAmount, int outputAmount, int processingSeconds) {
        Recipe {
            if (inputAmount < 1 || outputAmount < 1 || processingSeconds < 1) {
                throw new IllegalArgumentException("recipe values must be positive");
            }
        }
    }
}
