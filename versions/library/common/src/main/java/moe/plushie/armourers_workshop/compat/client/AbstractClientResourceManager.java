package moe.plushie.armourers_workshop.compat.client;

import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import moe.plushie.armourers_workshop.compat.core.AbstractResourceManager;
import net.minecraft.client.Minecraft;

@OnlyIn(Dist.CLIENT)
public class AbstractClientResourceManager {

    private static final AbstractResourceManager INSTANCE = AbstractResourceManager.wrap(Minecraft.getInstance().getResourceManager());

    public static AbstractResourceManager getInstance() {
        return INSTANCE;
    }
}
