package moe.plushie.armourers_workshop.compatibility.client;

import moe.plushie.armourers_workshop.api.annotation.Available;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;

@Available("[1.16, )")
@Environment(EnvType.CLIENT)
public class AbstractRenderSheet {

    private static final RenderType SOLID = Sheets.solidBlockSheet();
    private static final RenderType TRANSLUCENT = Sheets.translucentCullBlockSheet();
    private static final RenderType OUTLINE = Sheets.solidBlockSheet().outline().orElse(null);

    public static RenderType solidBlockSheet() {
        return SOLID;
    }

    public static RenderType translucentBlockSheet() {
        return TRANSLUCENT;
    }

    public static RenderType outlineBlockSheet() {
        return OUTLINE;
    }
}
