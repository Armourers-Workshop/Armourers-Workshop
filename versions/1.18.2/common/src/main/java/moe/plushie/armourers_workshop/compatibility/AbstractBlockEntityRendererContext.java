package moe.plushie.armourers_workshop.compatibility;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

@Environment(value = EnvType.CLIENT)
public class AbstractBlockEntityRendererContext extends BlockEntityRendererProvider.Context {

    public AbstractBlockEntityRendererContext(Minecraft minecraft) {
        super(minecraft.getBlockEntityRenderDispatcher(), minecraft.getBlockRenderer(), minecraft.getEntityModels(), minecraft.font);
    }
}


