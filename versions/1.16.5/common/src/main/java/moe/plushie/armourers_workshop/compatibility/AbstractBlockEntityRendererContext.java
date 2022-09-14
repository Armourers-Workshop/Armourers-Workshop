package moe.plushie.armourers_workshop.compatibility;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;

@Environment(value = EnvType.CLIENT)
public class AbstractBlockEntityRendererContext {

    public AbstractBlockEntityRendererContext(Minecraft minecraft) {
    }

    public BlockEntityRenderDispatcher getBlockEntityRenderDispatcher() {
        return BlockEntityRenderDispatcher.instance;
    }
}

