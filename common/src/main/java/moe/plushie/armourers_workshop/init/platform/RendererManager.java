package moe.plushie.armourers_workshop.init.platform;

import moe.plushie.armourers_workshop.core.client.skinrender.SkinRendererManager;
import moe.plushie.armourers_workshop.core.skin.molang.MolangVirtualMachine;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class RendererManager {

    public static void init() {
        SkinRendererManager.init();
//        try {
//            MolangVirtualMachine.get().eval("loop(10, {t.x = v.x + v.y; v.x = v.y; v.y = t.x; (v.y > 20) ? break;});");
//            MolangVirtualMachine.get().eval("v.x = 0; \nfor_each(t.pig, query.get_nearby_entities(4, 'minecraft:pig'), {\n v.x = v.x + 1;\n });");
//            MolangVirtualMachine.get().eval("25+math.sin(query.anim_time*360/4-90)*3");
//            MolangVirtualMachine.get().eval("v.should_reset_a ? { v.a = 0; }\n" +
//                    "v.larger_value = (v.a > v.b) ? v.a : v.b;");
//            MolangVirtualMachine.get().eval("v.x = 0;\n" +
//                    "loop(10, {\n" +
//                    " (v.x > 5) ? continue;\n" +
//                    " v.x = v.x + 1;\n" +
//                    "});");
//            MolangVirtualMachine.get().eval("variable.x = (variable.x ?? 1.2) + 0.3;");
//            MolangVirtualMachine.get().eval("query.is_item_equipped('main_hand'))");
//            MolangVirtualMachine.get().eval("1 >= 2");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }
}
