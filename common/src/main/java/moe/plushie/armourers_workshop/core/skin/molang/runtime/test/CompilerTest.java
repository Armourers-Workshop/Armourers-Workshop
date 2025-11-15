package moe.plushie.armourers_workshop.core.skin.molang.runtime.test;

import moe.plushie.armourers_workshop.core.client.animation.bind.ClientExecutionContextImpl;
import moe.plushie.armourers_workshop.core.skin.molang.MolangVirtualMachine;
import moe.plushie.armourers_workshop.core.skin.molang.core.ExecutionContext;
import moe.plushie.armourers_workshop.core.skin.molang.core.Name;
import moe.plushie.armourers_workshop.core.skin.molang.core.Result;
import moe.plushie.armourers_workshop.core.skin.molang.runtime.StaticVariableStorage;
import moe.plushie.armourers_workshop.core.skin.molang.runtime.bind.ContextBinding;
import moe.plushie.armourers_workshop.core.skin.molang.runtime.function.Function;
import moe.plushie.armourers_workshop.core.utils.Collections;
import moe.plushie.armourers_workshop.init.ModConfig;
import moe.plushie.armourers_workshop.init.ModLog;

import java.util.ArrayList;

public class CompilerTest {

    private ClientExecutionContextImpl context;
    private MolangVirtualMachine virtualMachine = new MolangVirtualMachine(Collections.immutableMap(it -> {
        it.put("test", new TestBinding());
    }));

    public static void main() {
        try {
            var test = new CompilerTest();
            test.context = new ClientExecutionContextImpl(new StaticVariableStorage());
            test.test1();
            test.test2();
            test.test3();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private void test1() throws Exception {
        assertEquals("-99", -99);
        assertEquals("!99", 0);

        assertEquals("1&&2", 1);
        assertEquals("0&&2", 0);
        assertEquals("2||2", 1);
        assertEquals("0||0", 0);

        assertEquals("1<2", 1);
        assertEquals("2<2", 0);
        assertEquals("2<=2", 1);
        assertEquals("3<=2", 0);
        assertEquals("2>1", 1);
        assertEquals("2>2", 0);
        assertEquals("2>=2", 1);
        assertEquals("2>=3", 0);

        assertEquals("2+2", 4);
        assertEquals("2-2", 0);
        assertEquals("2*2", 4);
        assertEquals("2/1", 2);
        assertEquals("2/0", 0);
        assertEquals("2%4", 2);
        assertEquals("2%0", 0);
        assertEquals("2^3", 8); // 2*2*2

        // ->
        assertEquals("v.number_value=2", 2);
        assertEquals("V.NUMBER_VALUE", 2);
        assertEquals("v.number_value??1", 2);
        assertEquals("v.null_value??1", 1);
        assertEquals("v.number_value?1", 1);
        assertEquals("v.null_value?1", Result.NULL);

        assertEquals("'a' == 'a'", 1);
        assertEquals("'a' == 'b'", 0);
        assertEquals("'a' != 'a'", 0);
        assertEquals("'a' != 'b'", 1);
        assertEquals("'a' != 'A'", 1); // case sensitive?
        assertEquals("1 == 1", 1);
        assertEquals("1 == 2", 0);
        assertEquals("1 != 1", 0);
        assertEquals("1 != 2", 1);

        assertEquals("t.number.a??=1", 1);
        assertEquals("t.number.a+=1", 2);
        assertEquals("t.number.a*=2", 4);
        assertEquals("t.number.a/=2", 2);
        assertEquals("t.number.a^=2", 4);
        assertEquals("t.number.a-=1", 3);
        assertEquals("t.number.a%=2", 1);

        assertEquals("test.array_value[-1]", "my_array_1");
        assertEquals("test.array_value[-2]", "my_array_1");
        assertEquals("test.array_value[0]", "my_array_1");
        assertEquals("test.array_value[1]", "my_array_2");
        assertEquals("test.array_value[2]", "my_array_3");
        assertEquals("test.array_value[3]", "my_array_1");
        assertEquals("test.array_value[4]", "my_array_2");
        assertEquals("test.array_value[0] == 'my_array_1'", 1);
        assertEquals("test.array_value[0] == 'my_array_2'", 0);

        assertEquals("v.number_value == 2 ? test.array_value[0]", "my_array_1");
        assertEquals("v.number_value > 2 ? test.array_value[0] : test.array_value[1]", "my_array_2");
        assertEquals("v.number_value <= 2 ? test.array_value[0] : test.array_value[1]", "my_array_1");

        assertEquals("1 < 2 ? 2 < 3 ? 'a' : 'b' : 'c'", "a");
        assertEquals("1 < 2 ? 2 > 3 ? 'a' : 'b' : 'c'", "b");
        assertEquals("1 > 2 ? 2 > 3 ? 'a' : 'b' : 'c'", "c");

        // struct
        assertEquals("v.a.b.c = 1", 1);
        assertEquals("v.a.b.c", 1); // set success?
        assertEquals("V.A.B.C", 1); // case insensitive?
        assertEquals("t.mm = v.a.b;return 1;", 1);
        assertEquals("t.mm.c", 1); // copy success?
        assertEquals("v.a.b.c = 2", 2);
        assertEquals("v.a.b.c", 2); // set success?
        assertEquals("t.mm.c", 1); // deep copy?

        assertEquals("math.sin(90)", 1);
        assertEquals("math.abs(90+1)", 91);
        assertEquals("-MATH.ABS(-90)", -90);
        assertEquals("!math.abs(90)", 0); // !90 = 0
        assertEquals("!v.a[0]", 1); // !0 = 1

        assertEquals("-(v.null_value*30)+(v.null_value?-50:-80)", -80);
        assertEquals("+(v.null_value*30)+(v.null_value?-50:-80)", -80);
        assertEquals("!(v.null_value*30)*(v.null_value?-50:-80)", -80);

        assertEquals("variable.x = (variable.x ?? 1.2) + 0.3;", 1.5);

        assertEquals("v.x = 0; loop(10, { v.x = v.x + 1; }); return v.x;", 10);
        assertEquals("v.x = 0; loop(10, { (v.x > 5) ? continue; v.x = v.x + 1; }); return v.x;", 6);
        //assertEquals("v.x = 0; loop(10, { (v.x > 5) ? return v.x; v.x = v.x + 1; });", 6);
        assertEquals("v.x = 0; loop(10, { loop(10, { v.x = v.x + 1; (v.x > 5) ? break; }); }); return v.x;", 15);

        assertEquals("v.x = 0; for_each(t.pig, test.array_value, { v.x = v.x + 1; }); return v.x;", 3);
        assertEquals("v.x = 0; for_each(t.pig, test.array_value, { (t.pig == 'my_array_2') ? break; v.x = v.x + 1; }); return v.x;", 1);
        assertEquals("v.x = 0; for_each(t.pig, test.array_value, { (t.pig == 'my_array_2') ? continue; v.x = v.x + 1; }); return v.x;", 2);
        //assertEquals("v.x = 0; for_each(t.pig, test.array_value, { (t.pig == 'my_array_2') ? return t.pig; v.x = v.x + 1; });", "my_array_2");

        assertEquals("v.x = 1; v.y = 1; loop(10, { t.x = v.x + v.y; v.x = v.y; v.y = t.x; }); return v.x;", 89);

        assertEquals("v.pigpig=test.entity_value;return 1;", 1);

        assertEquals("v.flag=1", 1);
        assertEquals("v.pigpig->v.flag=2", 2);
        assertEquals("v.pigpig->v.flag", 2);
        assertEquals("v.flag", 1);

        assertEquals("v.cowcow.friend = v.pigpig; v.pigpig->v.test.a.b.c = 1.23; return v.cowcow.friend->v.test.a.b.c;", 1.23);
        assertEquals("v.cowcow.friend = v.pigpig; v.pigpig->v.test.a.b.c = 1.23; v.moo = v.cowcow.friend->v.test; return v.moo.a.b.c;", 1.23);
        assertEquals("v.cowcow.friend = v.pigpig; v.pigpig->v.test.a.b.c = 1.23; v.moo = v.cowcow.friend->v.test.a; return v.moo.b.c;", 1.23);
        assertEquals("v.cowcow.friend = v.pigpig; v.pigpig->v.test.a.b.c = 1.23; v.moo = v.cowcow.friend->v.test.a.b; return v.moo.c;", 1.23);
        assertEquals("v.cowcow.friend = v.pigpig; v.pigpig->v.test.a.b.c = 1.23; v.moo = v.cowcow.friend->v.test.a.b.c; return v.moo;", 1.23);
    }

    private void test2() throws Exception {
        assertEquals("math.e", Math.E);
        assertEquals("math.pi", Math.PI);
        assertEquals("math.pi()", Math.PI);

        assertEquals("math.sin(q.modified_distance_moved*90)*0.05-0.05", -0.05);
    }

    private void test3() throws Exception {
        assertEquals("test.struct_array_value[0].a", "aValue");
        assertEquals("test.struct_array_value[0].b", "bValue");
        assertEquals("test.struct_func(0).a", "aValue");
        assertEquals("test.struct_func(0).b", "bValue");
    }


    private void assertEquals(String source, double expectedValue) throws Exception {
        assertEquals(source, Result.valueOf(expectedValue));
    }

    private void assertEquals(String source, String expectedValue) throws Exception {
        assertEquals(source, Result.valueOf(expectedValue));
    }

    private void assertEquals(String source, Result expectedValue) throws Exception {
        var oldValue = ModConfig.Client.enableMolangDebug;
        ModConfig.Client.enableMolangDebug = true;
        var expr = virtualMachine.compile(source);
        var resultValue = expr.evaluate(context);
        ModConfig.Client.enableMolangDebug = oldValue;
        if (resultValue.notEquals(expectedValue)) {
            var exception = new AssertionError("Source \"" + source + "\", expected " + expectedValue + " but got " + resultValue);
            var elements = Collections.filter(exception.getStackTrace(), it -> !it.getMethodName().equals("assertEquals"));
            ModLog.error("source {}", source);
            ModLog.error("expected {} but got {}", expectedValue, resultValue);
            ModLog.error("at {}", elements.get(0));
        } else {
            ModLog.debug("result: {}", resultValue);
        }
    }


    private static class TestBinding extends ContextBinding {

        TestBinding() {
            var listValue = new ArrayList<Result>();
            listValue.add(Result.valueOf("my_array_1"));
            listValue.add(Result.valueOf("my_array_2"));
            listValue.add(Result.valueOf("my_array_3"));
            constant("array_value", Result.valueOf(listValue));

            var listValue2 = new ArrayList<Result>();
            var rr2 = Result.newStruct();
            rr2.set(Name.of("a"), Result.valueOf("aValue"));
            rr2.set(Name.of("b"), Result.valueOf("bValue"));
            listValue2.add(rr2);
            constant("struct_array_value", Result.valueOf(listValue2));

            constant("entity_value", Result.wrap(new StaticVariableStorage()));

            function("struct_func", (r, d) -> new Function(r, 0, d) {
                @Override
                public double compute(final ExecutionContext context) {
                    return evaluate(context).getAsDouble();
                }

                @Override
                public Result evaluate(final ExecutionContext context) {
                    return rr2;
                }

                @Override
                public boolean isMutable() {
                    return true;
                }
            });
        }
    }
}
