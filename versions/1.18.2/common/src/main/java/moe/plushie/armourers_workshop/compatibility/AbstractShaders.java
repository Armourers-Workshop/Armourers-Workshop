package moe.plushie.armourers_workshop.compatibility;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.IOException;

@Environment(value = EnvType.CLIENT)
public class AbstractShaders {

    public static AbstractShader SKIN_SOLID_SHADER;
    public static AbstractShader SKIN_TRANSLUCENT_SHADER;

    public static AbstractShader SKIN_LIGHTING_SOLID_SHADER;
    public static AbstractShader SKIN_LIGHTING_TRANSLUCENT_SHADER;

    public static void initShader() {
        RenderSystem.recordRenderCall(AbstractShaders::loadShader);
    }

    private static void loadShader() {
        ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
        try {
            SKIN_SOLID_SHADER = new AbstractShader(resourceManager, "rendertype_skin_solid", AbstractRenderType.SKIN_NORMAL_FORMAT);
            SKIN_TRANSLUCENT_SHADER = new AbstractShader(resourceManager, "rendertype_skin_translucent", AbstractRenderType.SKIN_NORMAL_FORMAT);
            SKIN_LIGHTING_SOLID_SHADER = new AbstractShader(resourceManager, "rendertype_skin_lighting_solid", AbstractRenderType.SKIN_NORMAL_FORMAT);
            SKIN_LIGHTING_TRANSLUCENT_SHADER = new AbstractShader(resourceManager, "rendertype_skin_lighting_translucent", AbstractRenderType.SKIN_NORMAL_FORMAT);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
