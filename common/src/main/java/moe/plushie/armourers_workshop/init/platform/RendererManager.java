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
//            MolangVirtualMachine.get().eval("math.abs(1)");
//            MolangVirtualMachine.get().eval("math.abs(q.sax)");
//            MolangVirtualMachine.get().eval("25+math.sin(query.anim_time*360/4-90)*3");
//            MolangVirtualMachine.get().eval("1 == 2");
//            MolangVirtualMachine.get().eval("1 >= 2");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }
}
