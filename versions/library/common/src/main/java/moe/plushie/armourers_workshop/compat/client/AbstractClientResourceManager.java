package moe.plushie.armourers_workshop.compat.client;

import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import moe.plushie.armourers_workshop.compat.core.AbstractResourceManager;
import net.minecraft.client.Minecraft;

@OnlyIn(Dist.CLIENT)
public class AbstractClientResourceManager extends AbstractResourceManager {

    private static final AbstractClientResourceManager INSTANCE = new AbstractClientResourceManager();

    public AbstractClientResourceManager() {
        super(Minecraft.getInstance().getResourceManager());
    }

    public static AbstractClientResourceManager getInstance() {
        return INSTANCE;
    }
}
