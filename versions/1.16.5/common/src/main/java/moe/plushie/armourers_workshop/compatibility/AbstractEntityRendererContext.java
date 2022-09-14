package moe.plushie.armourers_workshop.compatibility;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;

@Environment(value = EnvType.CLIENT)
public class AbstractEntityRendererContext {

    public AbstractEntityRendererContext(Minecraft minecraft) {
    }

    public EntityRenderDispatcher getEntityRenderDispatcher() {
        return Minecraft.getInstance().getEntityRenderDispatcher();
    }
}
