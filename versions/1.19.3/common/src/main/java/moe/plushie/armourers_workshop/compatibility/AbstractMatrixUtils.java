package moe.plushie.armourers_workshop.compatibility;

import moe.plushie.armourers_workshop.api.math.IPoseStack;
import moe.plushie.armourers_workshop.compatibility.ext.AbstractMatrixUtilsExt_V19;
import moe.plushie.armourers_workshop.utils.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value = EnvType.CLIENT)
public class AbstractMatrixUtils extends AbstractMatrixUtilsExt_V19 {

    public static IPoseStack modelViewStack() {
        return of(RenderSystem.getModelViewStack());
    }
}
