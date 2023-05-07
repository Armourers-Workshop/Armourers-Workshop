package moe.plushie.armourers_workshop.core.client.other;

import moe.plushie.armourers_workshop.api.client.IRenderTypeBuilder;
import moe.plushie.armourers_workshop.api.skin.ISkinCubeType;
import moe.plushie.armourers_workshop.compatibility.AbstractRenderType;
import moe.plushie.armourers_workshop.init.ModTextures;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.resources.ResourceLocation;

@Environment(value = EnvType.CLIENT)
public abstract class SkinRenderType implements IRenderTypeBuilder {

    public static final RenderType GUI_IMAGE = _builder(SkinRenderFormat.GUI_IMAGE).transparency(Transparency.TRANSLUCENT).build("aw_image_gui");
    public static final RenderType GUI_HIGHLIGHTED_TEXT = _builder(SkinRenderFormat.GUI_HIGHLIGHTED_TEXT).transparency(Transparency.TRANSLUCENT).depthTest(DepthTest.NONE).build("aw_highlighted_text");

    public static final RenderType IMAGE_MAGIC = _texture(ModTextures.CIRCLE).writeMask(WriteMask.COLOR_WRITE).sortOnUpload().build("aw_image_magic");
    public static final RenderType IMAGE_EARTH = _texture(ModTextures.EARTH).build("aw_image_earth");

    public static final RenderType IMAGE_GUIDE = _texture(ModTextures.GUIDES).polygonOffset(-1, -10).build("aw_image_guide");
    public static final RenderType IMAGE_MARKER = _texture2(ModTextures.MARKERS).polygonOffset(-1, -10).cull().build("aw_image_marker");

    public static final RenderType HIGHLIGHTED_LINES = _line().depthTest(DepthTest.NONE).build("aw_lines");
    public static final RenderType HIGHLIGHTED_ENTITY_LINES = _line().build("aw_entity_lines");

    public static final RenderType PLAYER_CUTOUT = entityCutout(DefaultPlayerSkin.getDefaultSkin());
    public static final RenderType PLAYER_CUTOUT_NO_CULL = entityCutoutNoCull(DefaultPlayerSkin.getDefaultSkin());
    public static final RenderType PLAYER_TRANSLUCENT = entityTranslucentCull(DefaultPlayerSkin.getDefaultSkin());

    public static final RenderType BLOCK_CUBE = _block(ModTextures.BLOCK_CUBE).build("aw_block_cube");
    public static final RenderType BLOCK_CUBE_GLASS = _block(ModTextures.BLOCK_CUBE_GLASS).transparency(Transparency.TRANSLUCENT).sortOnUpload().build("aw_block_cube_glass");
    public static final RenderType BLOCK_CUBE_GLASS_UNSORTED = _block(ModTextures.BLOCK_CUBE_GLASS).transparency(Transparency.TRANSLUCENT).build("aw_block_cube_glass_unsorted");

    public static final RenderType FACE_SOLID = _cube(SkinRenderFormat.SKIN_FACE_SOLID).texture(ModTextures.CUBE).build("aw_face_sold");
    public static final RenderType FACE_LIGHTING = _cube(SkinRenderFormat.SKIN_FACE_LIGHTING).texture(ModTextures.LIGHTING_CUBE).build("aw_lighting_quad_face");
    public static final RenderType FACE_TRANSLUCENT = _cube(SkinRenderFormat.SKIN_FACE_TRANSLUCENT).texture(ModTextures.CUBE).transparency(Transparency.TRANSLUCENT).target(Target.TRANSLUCENT).build("aw_translucent_quad_face");
    public static final RenderType FACE_LIGHTING_TRANSLUCENT = _cube(SkinRenderFormat.SKIN_FACE_LIGHTING_TRANSLUCENT).texture(ModTextures.LIGHTING_CUBE).transparency(Transparency.TRANSLUCENT).target(Target.TRANSLUCENT).build("aw_translucent_lighting_quad_face");

    public static final RenderType[] RENDER_ORDERING_FACES = {FACE_SOLID, FACE_LIGHTING, FACE_TRANSLUCENT, FACE_LIGHTING_TRANSLUCENT};

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

    public static RenderType lines() {
        return RenderType.lines();
    }

    public static RenderType entityCutout(ResourceLocation texture) {
        return _entity(SkinRenderFormat.ENTITY_CUTOUT, texture).cull().build("aw_player_solid");
    }

    public static RenderType entityCutoutNoCull(ResourceLocation texture) {
        return _entity(SkinRenderFormat.ENTITY_CUTOUT_NO_CULL, texture).build("aw_player_cutout");
    }

    public static RenderType entityTranslucentCull(ResourceLocation texture) {
        return _entity(SkinRenderFormat.ENTITY_TRANSLUCENT, texture).cull().transparency(Transparency.TRANSLUCENT).build("aw_player_translucent");
    }

    private static IRenderTypeBuilder _entity(SkinRenderFormat format, ResourceLocation texture) {
        return _builder(format).texture(texture).polygonOffset(0, 30).overlay().lightmap().sortOnUpload().crumbling().outline();
    }

    private static IRenderTypeBuilder _cube(SkinRenderFormat format) {
        return _builder(format);
    }

    private static IRenderTypeBuilder _texture(ResourceLocation texture) {
        return _builder(SkinRenderFormat.IMAGE).texture(texture).transparency(Transparency.TRANSLUCENT).target(Target.TRANSLUCENT);
    }

    private static IRenderTypeBuilder _texture2(ResourceLocation texture) {
        return _builder(SkinRenderFormat.BLOCK_CUTOUT).texture(texture).overlay().lightmap();
    }

    private static IRenderTypeBuilder _block(ResourceLocation texture) {
        return _builder(SkinRenderFormat.BLOCK).texture(texture).overlay().lightmap();
    }

    private static IRenderTypeBuilder _line() {
        return _builder(SkinRenderFormat.LINE).lineWidth(2).polygonOffset(0, 10);
    }

    private static IRenderTypeBuilder _builder(SkinRenderFormat format) {
        return AbstractRenderType.builder(format);
    }
}
