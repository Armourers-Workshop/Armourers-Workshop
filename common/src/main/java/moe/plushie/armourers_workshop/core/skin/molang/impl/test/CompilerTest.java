package moe.plushie.armourers_workshop.core.skin.molang.impl.test;

import moe.plushie.armourers_workshop.core.skin.molang.MolangVirtualMachine;

public class CompilerTest {

    public static void main() {
        try {
            test1();
            test2();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void test1() throws Exception {
        assertEquals("-99", -99);
        assertEquals("math.sin(90)", Math.sin(Math.PI / 2));
        assertEquals("math.abs(90+1)", 91);
        assertEquals("-Math.abs(-90)", -90);
        assertEquals("!math.abs(90)", 0); // !90 = 0
        assertEquals("!q.a[0]", 1); // !0 = 1
        assertEquals("v.a=1;loop(10,{v.a=v.a+1;(v.A>=3)?break;});return V.a;", 3.0);
    }

    private static void test2() throws Exception {
        assertEquals("math.sin(q.modified_distance_moved*90)*0.05-0.05", -0.05);
    }

    private static void assertEquals(String source, double expectedValue) throws Exception {
        var resultValue = MolangVirtualMachine.get().eval(source).getAsDouble();
        if (Math.abs(resultValue - expectedValue) > 1e-15) {
            throw new AssertionError("Source \"" + source + "\", expected " + expectedValue + " but got " + resultValue);
        }
    }
}
