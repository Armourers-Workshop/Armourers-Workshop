package moe.plushie.armourers_workshop.gametest.common;

import moe.plushie.armourers_workshop.core.utils.Version;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestVersionCase {

    @Test
    public void testBasicNumericComparison() {
        assertTrue(compile("1.0").compareTo(compile("1.1")) < 0);
        assertTrue(compile("2").compareTo(compile("10")) < 0);
        assertTrue(compile("1.10").compareTo(compile("1.2")) > 0);
    }

    @Test
    public void testMultipleNumericSegments() {
        assertTrue(compile("1.0.0.0").compareTo(compile("1.0.0")) > 0);
        assertTrue(compile("1.0.0.1").compareTo(compile("1.0.0.0")) > 0);
    }

    @Test
    public void testStringSegments() {
        assertTrue(compile("1.0.0-alpha").compareTo(compile("1.0.0-beta")) < 0);
    }

    @Test
    public void testSnapshotIsLessThanRelease() {
        assertTrue(compile("1.0.0-SNAPSHOT").compareTo(compile("1.0.0")) < 0);
        assertTrue(compile("1.0.0-snapshot").compareTo(compile("1.0.0")) < 0);
    }

    @Test
    public void testMixedNumericAndString() {
        assertTrue(compile("1.0-1").compareTo(compile("1.0-alpha")) < 0);
        assertTrue(compile("1.0-alpha").compareTo(compile("1.0-1")) > 0);
        assertTrue(compile("2.0-rc").compareTo(compile("2.0-2")) > 0);
    }

    @Test
    public void testPrefix() {
        assertEquals(0, compile("v1.2.3").compareTo(compile("1.2.3")));
        assertEquals(0, compile("release-1.0").compareTo(compile("1.0")));
        assertEquals(0, compile("ver1.0").compareTo(compile("1.0")));
    }

    @Test
    public void testDateLikeVersion() {
        assertTrue(compile("2024.12.31").compareTo(compile("2023.12.31")) > 0);
    }

    @Test
    public void testLongSegmentList() {
        assertTrue(compile("1.2.3.4.5").compareTo(compile("1.2.3.4")) > 0);
    }

    @Test
    public void testEqualityByRawString() {
        assertEquals(compile("1.0.0"), compile("1.0.0"));
        assertNotEquals(compile("1.0.0"), compile("1.0.0.0"));
    }

    @Test
    public void testLexicalCompare() {
        assertTrue(compile("1.a").compareTo(compile("1.b")) < 0);
    }

    @Test
    public void testPreReleaseOrderingWeights() {
        assertTrue(compile("1.0.0-alpha").compareTo(compile("1.0.0-beta")) < 0);
        assertTrue(compile("1.0.0-beta").compareTo(compile("1..0.0-rc")) < 0);
        assertTrue(compile("1.0.0-rc").compareTo(compile("1.0.0-snapshot")) < 0);
        assertTrue(compile("1.0.0-snapshot").compareTo(compile("1.0.0")) < 0);
    }

    @Test
    public void testSemverCompare() {
        assertEquals(0, compile("1.8.12-snapshot+mc1.21.1-local").compareTo(compile("1.8.12-snapshot"))); // ignore build meta
    }

    private Version compile(String value) {
        return Version.parse(value);
    }
}
