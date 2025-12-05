package moe.plushie.armourers_workshop.gametest.common;

import moe.plushie.armourers_workshop.core.utils.Evaluator;
import moe.plushie.armourers_workshop.core.utils.Version;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestEvaluatorCase {

    @Test
    public void testExistsEval() {
        assertTrue(eval("mod-v1"));
        assertFalse(eval("!mod-v1"));

        assertTrue(eval("!mod-dummy"));
        assertFalse(eval("mod-dummy"));

        assertTrue(eval("mod-v1 || mod-dummy"));
        assertTrue(eval("mod-dummy || mod-v1"));
        assertFalse(eval("mod-dummy || mod-dummy"));
        assertTrue(eval("mod-v1 || mod-v2"));

        assertFalse(eval("mod-v1 && mod-dummy"));
        assertFalse(eval("mod-dummy && mod-v1"));
        assertTrue(eval("mod-v1 && mod-v2"));
        assertFalse(eval("mod-dummy && mod-dummy"));

        assertTrue(eval("!mod-dummy || !mod-dummy"));
        assertTrue(eval("!mod-dummy && !mod-dummy"));
    }

    @Test
    public void testCompareEval() {
        assertTrue(eval("mod-v3 >= 2.0.0")); // >
        assertTrue(eval("mod-v2 >= 2.0.0")); // =
        assertFalse(eval("mod-v1 >= 2.0.0")); // <

        assertTrue(eval("mod-v1 <= 2.0.0")); // <
        assertTrue(eval("mod-v2 <= 2.0.0")); // =
        assertFalse(eval("mod-v3 <= 2.0.0")); // >

        assertTrue(eval("mod-v2 == 2.0.0"));
        assertFalse(eval("mod-v3 == 2.0.0"));

        assertTrue(eval("mod-v3 != 2.0.0"));
        assertFalse(eval("mod-v2 != 2.0.0"));

        assertTrue(eval("mod-v3 > 2.0.0")); // >
        assertFalse(eval("mod-v1 > 2.0.0")); // <

        assertTrue(eval("mod-v1 < 2.0.0")); // <
        assertFalse(eval("mod-v3 < 2.0.0")); // >
    }

    @Test
    public void testComplexEval() {
        assertTrue(eval("mod-v1 >= 1.0.0 && mod-v2"));
        assertTrue(eval("mod-v2 >= 1.0.0 && (mod-dummy || mod-v2)"));
        assertTrue(eval("(!mod-dummy && mod-v2 >= 1.0.0) || (mod-v1 >= 1.0.0 && !mod-dummy)"));
    }

    private boolean eval(String value) {
        var evaluator = new Evaluator(it -> Optional.ofNullable(getVersion(it)).map(Version::parse));
        return evaluator.eval(value);
    }

    private String getVersion(String modId) {
        return switch (modId) {
            case "mod-v1" -> "1.0.0";
            case "mod-v2" -> "2.0.0";
            case "mod-v3" -> "3.0.0";
            default -> null;
        };
    }
}
