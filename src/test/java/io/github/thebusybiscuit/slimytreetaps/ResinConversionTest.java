package io.github.thebusybiscuit.slimytreetaps;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class ResinConversionTest {

    @Test
    void defaultClumpRecipeProducesOneRubber() {
        ResinConversion.Recipe recipe = ResinConversion.forClumpValue(1, 4);

        assertEquals(4, recipe.inputAmount());
        assertEquals(1, recipe.outputAmount());
        assertEquals(4, recipe.processingSeconds());
    }

    @Test
    void resinBlockPreservesNineClumpsOfValue() {
        ResinConversion.Recipe recipe = ResinConversion.forClumpValue(9, 4);

        assertEquals(4, recipe.inputAmount());
        assertEquals(9, recipe.outputAmount());
        assertEquals(36, recipe.processingSeconds());
        assertEquivalentValue(recipe, 9, 4);
    }

    @Test
    void resinBricksBlockUsesOneToOneAtDefaultBalance() {
        ResinConversion.Recipe recipe = ResinConversion.forClumpValue(4, 4);

        assertEquals(1, recipe.inputAmount());
        assertEquals(1, recipe.outputAmount());
        assertEquals(4, recipe.processingSeconds());
        assertEquivalentValue(recipe, 4, 4);
    }

    @Test
    void unusualConfiguredRatiosRemainEquivalent() {
        ResinConversion.Recipe recipe = ResinConversion.forClumpValue(9, 6);

        assertEquals(2, recipe.inputAmount());
        assertEquals(3, recipe.outputAmount());
        assertEquals(12, recipe.processingSeconds());
        assertEquivalentValue(recipe, 9, 6);
    }

    @Test
    void rejectsInvalidValues() {
        assertThrows(IllegalArgumentException.class, () -> ResinConversion.forClumpValue(0, 4));
        assertThrows(IllegalArgumentException.class, () -> ResinConversion.forClumpValue(1, 0));
    }

    private void assertEquivalentValue(
            ResinConversion.Recipe recipe,
            int clumpValue,
            int clumpsPerRubber) {
        int inputClumps = recipe.inputAmount() * clumpValue;
        int outputClumps = recipe.outputAmount() * clumpsPerRubber;
        assertEquals(inputClumps, outputClumps);
        assertEquals(4 * recipe.outputAmount(), recipe.processingSeconds());
    }
}
