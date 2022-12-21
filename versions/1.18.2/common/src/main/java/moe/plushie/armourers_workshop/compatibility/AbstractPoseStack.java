package moe.plushie.armourers_workshop.compatibility;

import moe.plushie.armourers_workshop.api.math.IPoseStack;
import moe.plushie.armourers_workshop.compatibility.ext.AbstractPoseStackExt_V1618;
import moe.plushie.armourers_workshop.utils.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value = EnvType.CLIENT)
public class AbstractPoseStack extends AbstractPoseStackExt_V1618 {

    public static IPoseStack modelViewStack() {
        return wrap(RenderSystem.getModelViewStack());
    }
}
