package moe.plushie.armourers_workshop.compatibility;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

@Environment(value = EnvType.CLIENT)
public class AbstractEntityRendererContext extends EntityRendererProvider.Context {

    public AbstractEntityRendererContext(Minecraft minecraft) {
        super(minecraft.getEntityRenderDispatcher(), minecraft.getItemRenderer(), minecraft.getResourceManager(), minecraft.getEntityModels(), minecraft.font);
    }
}
