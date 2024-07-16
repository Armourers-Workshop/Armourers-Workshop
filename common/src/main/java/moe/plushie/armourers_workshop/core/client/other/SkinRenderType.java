package moe.plushie.armourers_workshop.core.client.other;

import moe.plushie.armourers_workshop.api.client.IRenderTypeBuilder;
import moe.plushie.armourers_workshop.api.core.IResourceLocation;
import moe.plushie.armourers_workshop.api.skin.ISkinCubeType;
import moe.plushie.armourers_workshop.compatibility.AbstractRenderType;
import moe.plushie.armourers_workshop.init.ModTextures;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.RenderType;

import java.util.concurrent.ConcurrentHashMap;

@Environment(EnvType.CLIENT)
public abstract class SkinRenderType implements IRenderTypeBuilder {

    public static final RenderType BLIT_COLOR = _builder(SkinRenderFormat.BLIT_MASK).build("aw_blit_color");
    public static final RenderType BLIT_MASK = _builder(SkinRenderFormat.BLIT_MASK).writeMask(WriteMask.NONE).build("aw_blit_mask");
    public static final RenderType BLIT_IMAGE = _builder(SkinRenderFormat.GUI_IMAGE).build("aw_blit_image");

    public static final RenderType GUI_COLOR = _builder(SkinRenderFormat.GUI_COLOR).transparency(Transparency.DEFAULT).build("aw_gui_color");
    public static final RenderType GUI_IMAGE = _builder(SkinRenderFormat.GUI_IMAGE).transparency(Transparency.TRANSLUCENT).build("aw_gui_image");
    public static final RenderType GUI_HIGHLIGHTED_TEXT = _builder(SkinRenderFormat.GUI_HIGHLIGHTED_TEXT).transparency(Transparency.TRANSLUCENT).colorLogic(ColorLogic.OR_REVERSE).depthTest(DepthTest.NONE).build("aw_highlighted_text");

    public static final RenderType IMAGE_MAGIC = _texture(ModTextures.CIRCLE).writeMask(WriteMask.COLOR_WRITE).sortOnUpload().build("aw_image_magic");

    public static final RenderType IMAGE_GUIDE = _texture(ModTextures.GUIDES).polygonOffset(-1, -10).build("aw_image_guide");
    public static final RenderType IMAGE_MARKER = _texture2(ModTextures.MARKERS).polygonOffset(-1, -10).cull().build("aw_image_marker");

    public static final RenderType HIGHLIGHTED_LINES = _line(2).depthTest(DepthTest.NONE).build("aw_lines_ndt");
    public static final RenderType HIGHLIGHTED_ENTITY_LINES = _entityHighlight(ModTextures.MANNEQUIN_HIGHLIGHT).build("aw_entity_lines");

    public static final RenderType PLAYER_CUTOUT = entityCutout(ModTextures.MANNEQUIN_DEFAULT);
    public static final RenderType PLAYER_CUTOUT_NO_CULL = entityCutoutNoCull(ModTextures.MANNEQUIN_DEFAULT);
    public static final RenderType PLAYER_TRANSLUCENT = entityTranslucentCull(ModTextures.MANNEQUIN_DEFAULT);

    public static final RenderType BLOCK_CUBE = _block(ModTextures.BLOCK_CUBE).build("aw_block_cube");
    public static final RenderType BLOCK_CUBE_GLASS = _block(ModTextures.BLOCK_CUBE_GLASS).transparency(Transparency.TRANSLUCENT).sortOnUpload().build("aw_block_cube_glass");
    public static final RenderType BLOCK_CUBE_GLASS_UNSORTED = _block(ModTextures.BLOCK_CUBE_GLASS).transparency(Transparency.TRANSLUCENT).build("aw_block_cube_glass_unsorted");
    public static final RenderType BLOCK_EARTH = _builder(SkinRenderFormat.SKIN_FACE_LIGHTING_TRANSLUCENT).texture(ModTextures.EARTH).transparency(Transparency.TRANSLUCENT).target(Target.TRANSLUCENT).cull().build("aw_block_earth");

    public static final RenderType FACE_SOLID = _cube(SkinRenderFormat.SKIN_FACE_SOLID).texture(ModTextures.CUBE).build("aw_face_sold");
    public static final RenderType FACE_LIGHTING = _cube(SkinRenderFormat.SKIN_FACE_LIGHTING).texture(ModTextures.LIGHTING_CUBE).build("aw_lighting_quad_face");
    public static final RenderType FACE_TRANSLUCENT = _cube(SkinRenderFormat.SKIN_FACE_TRANSLUCENT).texture(ModTextures.CUBE).transparency(Transparency.TRANSLUCENT).target(Target.TRANSLUCENT).build("aw_translucent_quad_face");
    public static final RenderType FACE_LIGHTING_TRANSLUCENT = _cube(SkinRenderFormat.SKIN_FACE_LIGHTING_TRANSLUCENT).texture(ModTextures.LIGHTING_CUBE).transparency(Transparency.TRANSLUCENT).target(Target.TRANSLUCENT).build("aw_translucent_lighting_quad_face");

    private static final RenderType LINES = _line(1).build("aw_lines");
    private static final RenderType LINE_STRIP = _builder(SkinRenderFormat.LINE_STRIP).lineWidth(1).build("aw_line_strip");

    private static final ConcurrentHashMap<String, RenderType> FACE_SOLID_VARIANTS = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, RenderType> FACE_LIGHTING_VARIANTS = new ConcurrentHashMap<>();

    private static final RenderType[] RENDER_ORDERING_FACES = {FACE_SOLID, FACE_LIGHTING, FACE_TRANSLUCENT, FACE_LIGHTING_TRANSLUCENT};

    public static RenderType by(ISkinCubeType cubeType) {
        if (cubeType.isGlass()) {
            if (cubeType.isGlowing()) {
                return FACE_LIGHTING_TRANSLUCENT;
            } else {
                return FACE_TRANSLUCENT;
            }
        }
        if (cubeType.isGlowing()) {
            return FACE_LIGHTING;
        } else {
            return FACE_SOLID;
        }
    }

    public static RenderType customSolidFace(IResourceLocation texture) {
        var key = String.format("aw_custom_solid/%s", texture.getPath());
        return FACE_SOLID_VARIANTS.computeIfAbsent(key, k -> _builder(SkinRenderFormat.SKIN_FACE_TEXTURE).texture(texture).transparency(Transparency.TRANSLUCENT).target(Target.TRANSLUCENT).outline().build(k));
    }

    public static RenderType customLightingFace(IResourceLocation texture) {
        var key = String.format("aw_custom_lighting/%s", texture.getPath());
        return FACE_LIGHTING_VARIANTS.computeIfAbsent(key, k -> _builder(SkinRenderFormat.SKIN_FACE_LIGHTING_TEXTURE).texture(texture).transparency(Transparency.TRANSLUCENT).target(Target.TRANSLUCENT).outline().build(k));
    }

    public static RenderType lines() {
        return LINES;
    }

    public static RenderType lineStrip() {
        return LINE_STRIP;
    }

    public static RenderType entityCutout(IResourceLocation texture) {
        return _entity(SkinRenderFormat.ENTITY_CUTOUT, texture).cull().build("aw_player_solid");
    }

    public static RenderType entityCutoutNoCull(IResourceLocation texture) {
        return _entity(SkinRenderFormat.ENTITY_CUTOUT_NO_CULL, texture).build("aw_player_cutout");
    }

    public static RenderType entityTranslucentCull(IResourceLocation texture) {
        return _entity(SkinRenderFormat.ENTITY_TRANSLUCENT, texture).cull().transparency(Transparency.TRANSLUCENT).build("aw_player_translucent");
    }

    public static int getOrdering(RenderType renderType) {
        int index = 1;
        for (var target : SkinRenderType.RENDER_ORDERING_FACES) {
            if (target == renderType) {
                return index;
            }
            index += 1;
        }
        index += 1;
        if (FACE_SOLID_VARIANTS.containsValue(renderType)) {
            return index;
        }
        index += 1;
        if (FACE_LIGHTING_VARIANTS.containsValue(renderType)) {
            return index;
        }
        return 0;
    }

    public static boolean isGrowing(RenderType renderType) {
        // do fast hitting.
        if (renderType == FACE_LIGHTING || renderType == FACE_LIGHTING_TRANSLUCENT) {
            return true;
        }
        // do fast missing.
        if (renderType == FACE_SOLID || renderType == FACE_TRANSLUCENT) {
            return false;
        }
        return FACE_LIGHTING_VARIANTS.containsValue(renderType);
    }

    public static boolean isTranslucent(RenderType renderType) {
        // do fast hitting.
        if (renderType == FACE_TRANSLUCENT || renderType == FACE_LIGHTING_TRANSLUCENT) {
            return true;
        }
        // do fast missing.
        if (renderType == FACE_SOLID || renderType == FACE_LIGHTING) {
            return false;
        }
        return false;
    }

    private static IRenderTypeBuilder _entity(SkinRenderFormat format, IResourceLocation texture) {
        return _builder(format).texture(texture).polygonOffset(0, 30).overlay().lightmap().sortOnUpload().crumbling().outline();
    }

    private static IRenderTypeBuilder _entityHighlight(IResourceLocation texture) {
        return _builder(SkinRenderFormat.ENTITY_ALPHA).texture(texture).overlay().lightmap();
    }

    private static IRenderTypeBuilder _cube(SkinRenderFormat format) {
        return _builder(format).outline();
    }

    private static IRenderTypeBuilder _texture(IResourceLocation texture) {
        return _builder(SkinRenderFormat.IMAGE).texture(texture).transparency(Transparency.TRANSLUCENT).target(Target.TRANSLUCENT);
    }

    private static IRenderTypeBuilder _texture2(IResourceLocation texture) {
        return _builder(SkinRenderFormat.BLOCK_CUTOUT).texture(texture).overlay().lightmap();
    }

    private static IRenderTypeBuilder _block(IResourceLocation texture) {
        return _builder(SkinRenderFormat.BLOCK).texture(texture).overlay().lightmap();
    }

    private static IRenderTypeBuilder _line(float lineWidth) {
        return _builder(SkinRenderFormat.LINE).lineWidth(lineWidth).polygonOffset(0, 10);
    }

    private static IRenderTypeBuilder _builder(SkinRenderFormat format) {
        return AbstractRenderType.builder(format);
    }
}
