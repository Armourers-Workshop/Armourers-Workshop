package moe.plushie.armourers_workshop.init.platform;

import com.apple.library.coregraphics.CGGraphicsRenderer;
import moe.plushie.armourers_workshop.compatibility.AbstractBlockEntityRendererContext;
import moe.plushie.armourers_workshop.compatibility.AbstractEntityRendererContext;
import moe.plushie.armourers_workshop.compatibility.AbstractModelPartRegistries;
import moe.plushie.armourers_workshop.core.armature.Armatures;
import moe.plushie.armourers_workshop.core.armature.Joints;
import moe.plushie.armourers_workshop.core.client.skinrender.SkinRendererManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;

@Environment(value = EnvType.CLIENT)
public class RendererManager {

    private static AbstractBlockEntityRendererContext BLOCK;
    private static AbstractEntityRendererContext ENTITY;

    public static void init() {
        CGGraphicsRenderer.init();
        AbstractModelPartRegistries.init();
        Armatures.init();
        Joints.init();
        SkinManager.init();
        SkinRendererManager.getInstance().init();
    }

    public static AbstractBlockEntityRendererContext getBlockContext() {
        if (BLOCK == null) {
            BLOCK = new AbstractBlockEntityRendererContext(Minecraft.getInstance());
        }
        return BLOCK;
    }

    public static AbstractEntityRendererContext getEntityContext() {
        if (ENTITY == null) {
            ENTITY = new AbstractEntityRendererContext(Minecraft.getInstance());
        }
        return ENTITY;
    }

}
