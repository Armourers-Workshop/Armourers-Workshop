package moe.plushie.armourers_workshop.gametest.common;

import moe.plushie.armourers_workshop.core.skin.molang.MolangVirtualMachine;
import moe.plushie.armourers_workshop.core.skin.molang.core.ExecutionContext;
import moe.plushie.armourers_workshop.core.skin.molang.core.Name;
import moe.plushie.armourers_workshop.core.skin.molang.core.Result;
import moe.plushie.armourers_workshop.core.skin.molang.runtime.StaticVariableStorage;
import moe.plushie.armourers_workshop.core.skin.molang.runtime.bind.ContextBinding;
import moe.plushie.armourers_workshop.core.skin.molang.runtime.function.Function;
import moe.plushie.armourers_workshop.core.skin.molang.thirdparty.bind.ExecutionContextImpl;
import moe.plushie.armourers_workshop.core.utils.Collections;
import moe.plushie.armourers_workshop.init.ModConfig;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestMolangCase {

    private ExecutionContextImpl context = new ExecutionContextImpl(new StaticVariableStorage());
    
    private MolangVirtualMachine vm = new MolangVirtualMachine(Collections.immutableMap(it -> {
        it.put("test", new TestBinding());
    }));

    @Test
    public void testBasic() throws Exception {
        evaluate("-99", -99);
        evaluate("!99", 0);

        evaluate("1&&2", 1);
        evaluate("0&&2", 0);
        evaluate("2||2", 1);
        evaluate("0||0", 0);

        evaluate("1<2", 1);
        evaluate("2<2", 0);
        evaluate("2<=2", 1);
        evaluate("3<=2", 0);
        evaluate("2>1", 1);
        evaluate("2>2", 0);
        evaluate("2>=2", 1);
        evaluate("2>=3", 0);

        evaluate("2+2", 4);
        evaluate("2-2", 0);
        evaluate("2*2", 4);
        evaluate("2/1", 2);
        evaluate("2/0", 0);
        evaluate("2%4", 2);
        evaluate("2%0", 0);
        evaluate("2^3", 8); // 2*2*2

        // ->
        evaluate("v.number_value=2", 2);
        evaluate("V.NUMBER_VALUE", 2);
        evaluate("v.number_value??1", 2);
        evaluate("v.null_value??1", 1);
        evaluate("v.number_value?1", 1);
        evaluate("v.null_value?1", Result.NULL);

        evaluate("'a' == 'a'", 1);
        evaluate("'a' == 'b'", 0);
        evaluate("'a' != 'a'", 0);
        evaluate("'a' != 'b'", 1);
        evaluate("'a' != 'A'", 1); // case sensitive?
        evaluate("1 == 1", 1);
        evaluate("1 == 2", 0);
        evaluate("1 != 1", 0);
        evaluate("1 != 2", 1);

        evaluate("t.number.a??=1", 1);
        evaluate("t.number.a+=1", 2);
        evaluate("t.number.a*=2", 4);
        evaluate("t.number.a/=2", 2);
        evaluate("t.number.a^=2", 4);
        evaluate("t.number.a-=1", 3);
        evaluate("t.number.a%=2", 1);

        evaluate("test.array_value[-1]", "my_array_1");
        evaluate("test.array_value[-2]", "my_array_1");
        evaluate("test.array_value[0]", "my_array_1");
        evaluate("test.array_value[1]", "my_array_2");
        evaluate("test.array_value[2]", "my_array_3");
        evaluate("test.array_value[3]", "my_array_1");
        evaluate("test.array_value[4]", "my_array_2");
        evaluate("test.array_value[0] == 'my_array_1'", 1);
        evaluate("test.array_value[0] == 'my_array_2'", 0);

        evaluate("v.number_value == 2 ? test.array_value[0]", "my_array_1");
        evaluate("v.number_value > 2 ? test.array_value[0] : test.array_value[1]", "my_array_2");
        evaluate("v.number_value <= 2 ? test.array_value[0] : test.array_value[1]", "my_array_1");

        evaluate("1 < 2 ? 2 < 3 ? 'a' : 'b' : 'c'", "a");
        evaluate("1 < 2 ? 2 > 3 ? 'a' : 'b' : 'c'", "b");
        evaluate("1 > 2 ? 2 > 3 ? 'a' : 'b' : 'c'", "c");

        // struct
        evaluate("v.a.b.c = 1", 1);
        evaluate("v.a.b.c", 1); // set success?
        evaluate("V.A.B.C", 1); // case insensitive?
        evaluate("t.mm = v.a.b;return 1;", 1);
        evaluate("t.mm.c", 1); // copy success?
        evaluate("v.a.b.c = 2", 2);
        evaluate("v.a.b.c", 2); // set success?
        evaluate("t.mm.c", 1); // deep copy?

        evaluate("math.sin(90)", 1);
        evaluate("math.abs(90+1)", 91);
        evaluate("-MATH.ABS(-90)", -90);
        evaluate("!math.abs(90)", 0); // !90 = 0
        evaluate("!v.a[0]", 1); // !0 = 1

        evaluate("-(v.null_value*30)+(v.null_value?-50:-80)", -80);
        evaluate("+(v.null_value*30)+(v.null_value?-50:-80)", -80);
        evaluate("!(v.null_value*30)*(v.null_value?-50:-80)", -80);

        evaluate("variable.x = (variable.x ?? 1.2) + 0.3;", 1.5);

        evaluate("v.x = 0; loop(10, { v.x = v.x + 1; }); return v.x;", 10);
        evaluate("v.x = 0; loop(10, { (v.x > 5) ? continue; v.x = v.x + 1; }); return v.x;", 6);
        //assertEvaluate("v.x = 0; loop(10, { (v.x > 5) ? return v.x; v.x = v.x + 1; });", 6);
        evaluate("v.x = 0; loop(10, { loop(10, { v.x = v.x + 1; (v.x > 5) ? break; }); }); return v.x;", 15);

        evaluate("v.x = 0; for_each(t.pig, test.array_value, { v.x = v.x + 1; }); return v.x;", 3);
        evaluate("v.x = 0; for_each(t.pig, test.array_value, { (t.pig == 'my_array_2') ? break; v.x = v.x + 1; }); return v.x;", 1);
        evaluate("v.x = 0; for_each(t.pig, test.array_value, { (t.pig == 'my_array_2') ? continue; v.x = v.x + 1; }); return v.x;", 2);
        //assertEvaluate("v.x = 0; for_each(t.pig, test.array_value, { (t.pig == 'my_array_2') ? return t.pig; v.x = v.x + 1; });", "my_array_2");

        evaluate("v.x = 1; v.y = 1; loop(10, { t.x = v.x + v.y; v.x = v.y; v.y = t.x; }); return v.x;", 89);

        evaluate("v.pigpig=test.entity_value;return 1;", 1);

        evaluate("v.flag=1", 1);
        evaluate("v.pigpig->v.flag=2", 2);
        evaluate("v.pigpig->v.flag", 2);
        evaluate("v.flag", 1);

        evaluate("v.cowcow.friend = v.pigpig; v.pigpig->v.test.a.b.c = 1.23; return v.cowcow.friend->v.test.a.b.c;", 1.23);
        evaluate("v.cowcow.friend = v.pigpig; v.pigpig->v.test.a.b.c = 1.23; v.moo = v.cowcow.friend->v.test; return v.moo.a.b.c;", 1.23);
        evaluate("v.cowcow.friend = v.pigpig; v.pigpig->v.test.a.b.c = 1.23; v.moo = v.cowcow.friend->v.test.a; return v.moo.b.c;", 1.23);
        evaluate("v.cowcow.friend = v.pigpig; v.pigpig->v.test.a.b.c = 1.23; v.moo = v.cowcow.friend->v.test.a.b; return v.moo.c;", 1.23);
        evaluate("v.cowcow.friend = v.pigpig; v.pigpig->v.test.a.b.c = 1.23; v.moo = v.cowcow.friend->v.test.a.b.c; return v.moo;", 1.23);
    }

    @Test
    public void testFunction() throws Exception {
        evaluate("math.e", Math.E);
        evaluate("math.pi", Math.PI);
        evaluate("math.pi()", Math.PI);

        evaluate("math.sin(q.modified_distance_moved*90)*0.05-0.05", -0.05);
    }

    @Test
    public void testString() throws Exception {
        evaluate("test.struct_array_value[0].a", "aValue");
        evaluate("test.struct_array_value[0].b", "bValue");
        evaluate("test.struct_func(0).a", "aValue");
        evaluate("test.struct_func(0).b", "bValue");
    }


    private void evaluate(String source, double expectedValue) throws Exception {
        evaluate(source, Result.valueOf(expectedValue));
    }

    private void evaluate(String source, String expectedValue) throws Exception {
        evaluate(source, Result.valueOf(expectedValue));
    }

    private void evaluate(String source, Result expectedValue) throws Exception {
        var oldValue = ModConfig.Client.enableMolangDebug;
        ModConfig.Client.enableMolangDebug = true;
        var expr = vm.compile(source);
        var resultValue = expr.evaluate(context);
        ModConfig.Client.enableMolangDebug = oldValue;
        assertEquals(resultValue, expectedValue, "Source \"" + source + "\", expected " + expectedValue + " but got " + resultValue);
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
