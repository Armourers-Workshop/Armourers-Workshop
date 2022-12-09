package moe.plushie.armourers_workshop.compatibility;

import com.mojang.blaze3d.systems.RenderSystem;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.math.IPoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Available("[1.18, )")
@Environment(value = EnvType.CLIENT)
public class AbstractRenderPoseStack {

    public static IPoseStack create() {
        return IPoseStack.of(RenderSystem.getModelViewStack());
    }
}
