package moe.plushie.armourers_workshop.core.client.other;

import moe.plushie.armourers_workshop.api.client.IRenderType;
import moe.plushie.armourers_workshop.compatibility.client.AbstractRenderType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;

@SuppressWarnings("unused")
@Environment(EnvType.CLIENT)
public class SkinRenderSheets {

    private static final IRenderType CUTOUT_SHEET = normal(RenderType.cutout());
    private static final IRenderType TRANSLUCENT_SHEET = normal(RenderType.translucent());

    private static final IRenderType GLINT_SHEET = normal(RenderType.glint());
    private static final IRenderType GLINT_TRANSLUCENT_SHEET = normal(RenderType.glintTranslucent());

    private static final IRenderType SOLID_BLOCK_SHEET = normal(Sheets.solidBlockSheet());
    private static final IRenderType TRANSLUCENT_BLOCK_SHEET = normal(Sheets.translucentCullBlockSheet());

    private static final IRenderType OUTLINE_SOLID_BLOCK_SHEET = SOLID_BLOCK_SHEET.outline().orElse(null);
    private static final IRenderType OUTLINE_TRANSLUCENT_BLOCK_SHEET = TRANSLUCENT_BLOCK_SHEET.outline().orElse(null);

    public static IRenderType cutoutSheet() {
        return CUTOUT_SHEET;
    }

    public static IRenderType translucentSheet() {
        return TRANSLUCENT_SHEET;
    }

    public static IRenderType solidBlockSheet() {
        return SOLID_BLOCK_SHEET;
    }

    public static IRenderType translucentBlockSheet() {
        return TRANSLUCENT_BLOCK_SHEET;
    }

    public static IRenderType glintSheet() {
        return GLINT_SHEET;
    }

    public static IRenderType glintTranslucentSheet() {
        return GLINT_TRANSLUCENT_SHEET;
    }

    public static IRenderType outlineSolidBlockSheet() {
        return OUTLINE_SOLID_BLOCK_SHEET;
    }

    public static IRenderType outlineTranslucentBlockSheet() {
        return OUTLINE_TRANSLUCENT_BLOCK_SHEET;
    }

    private static IRenderType normal(RenderType renderType) {
        return AbstractRenderType.of(renderType);
    }
}
