package moe.plushie.armourers_workshop.compatibility;

import moe.plushie.armourers_workshop.api.math.IPoseStack;
import moe.plushie.armourers_workshop.compatibility.v19.PoseStack_V1920;
import moe.plushie.armourers_workshop.utils.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value = EnvType.CLIENT)
public class AbstractPoseStack extends PoseStack_V1920 {

    public static IPoseStack modelViewStack() {
        return wrap(RenderSystem.getModelViewStack());
    }
}
