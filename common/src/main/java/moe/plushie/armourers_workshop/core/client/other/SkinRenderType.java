package moe.plushie.armourers_workshop.core.client.other;

import moe.plushie.armourers_workshop.api.client.IRenderType;
import moe.plushie.armourers_workshop.api.client.IRenderTypeBuilder;
import moe.plushie.armourers_workshop.api.core.IResourceLocation;
import moe.plushie.armourers_workshop.api.skin.geometry.ISkinGeometryType;
import moe.plushie.armourers_workshop.compatibility.client.AbstractRenderTypeImpl;
import moe.plushie.armourers_workshop.core.skin.geometry.SkinGeometryTypes;
import moe.plushie.armourers_workshop.init.ModTextures;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("unused")
@Environment(EnvType.CLIENT)
public abstract class SkinRenderType implements IRenderType {

    public static final IRenderType BLIT_COLOR = _builder(SkinVertexFormat.BLIT_MASK).build("aw_blit_color");
    public static final IRenderType BLIT_MASK = _builder(SkinVertexFormat.BLIT_MASK).writeMask(WriteMask.NONE).build("aw_blit_mask");
    public static final IRenderType BLIT_IMAGE = _builder(SkinVertexFormat.GUI_IMAGE).build("aw_blit_image");

    public static final IRenderType GUI_COLOR = _builder(SkinVertexFormat.GUI_COLOR).transparency(Transparency.DEFAULT).build("aw_gui_color");
    public static final IRenderType GUI_IMAGE = _builder(SkinVertexFormat.GUI_IMAGE).transparency(Transparency.TRANSLUCENT).build("aw_gui_image");
    public static final IRenderType GUI_HIGHLIGHTED_TEXT = _builder(SkinVertexFormat.GUI_HIGHLIGHTED_TEXT).transparency(Transparency.TRANSLUCENT).colorLogic(ColorLogic.OR_REVERSE).depthTest(DepthTest.NONE).build("aw_highlighted_text");

    public static final IRenderType IMAGE_MAGIC = _texture(ModTextures.CIRCLE).writeMask(WriteMask.COLOR_WRITE).sortOnUpload().build("aw_image_magic");

    public static final IRenderType IMAGE_GUIDE = _texture(ModTextures.GUIDES).polygonOffset(-1, -10).build("aw_image_guide");
    public static final IRenderType IMAGE_MARKER = _texture2(ModTextures.MARKERS).polygonOffset(-1, -10).cull().build("aw_image_marker");

    public static final IRenderType HIGHLIGHTED_LINES = _line(2).depthTest(DepthTest.NONE).build("aw_lines_ndt");
    public static final IRenderType HIGHLIGHTED_ENTITY_LINES = _entityHighlight(ModTextures.MANNEQUIN_HIGHLIGHT).build("aw_entity_lines");

    public static final IRenderType PLAYER_CUTOUT = entityCutout(ModTextures.MANNEQUIN_DEFAULT);
    public static final IRenderType PLAYER_CUTOUT_NO_CULL = entityCutoutNoCull(ModTextures.MANNEQUIN_DEFAULT);
    public static final IRenderType PLAYER_TRANSLUCENT = entityTranslucentCull(ModTextures.MANNEQUIN_DEFAULT);

    public static final IRenderType BLOCK_CUBE = _block(ModTextures.BLOCK_CUBE).build("aw_block_cube");
    public static final IRenderType BLOCK_CUBE_GLASS = _block(ModTextures.BLOCK_CUBE_GLASS).transparency(Transparency.TRANSLUCENT).sortOnUpload().build("aw_block_cube_glass");
    public static final IRenderType BLOCK_CUBE_GLASS_UNSORTED = _block(ModTextures.BLOCK_CUBE_GLASS).transparency(Transparency.TRANSLUCENT).build("aw_block_cube_glass_unsorted");
    public static final IRenderType BLOCK_EARTH = _builder(SkinVertexFormat.SKIN_BLOCK_FACE_LIGHTING_TRANSLUCENT).texture(ModTextures.EARTH).transparency(Transparency.TRANSLUCENT).target(Target.TRANSLUCENT).cull().build("aw_block_earth");

    public static final IRenderType BLOCK_FACE_SOLID = _blockFace(SkinVertexFormat.SKIN_BLOCK_FACE_SOLID).texture(ModTextures.CUBE).build("aw_face_sold");
    public static final IRenderType BLOCK_FACE_LIGHTING = _blockFace(SkinVertexFormat.SKIN_BLOCK_FACE_LIGHTING).texture(ModTextures.LIGHTING_CUBE).build("aw_lighting_quad_face");
    public static final IRenderType BLOCK_FACE_TRANSLUCENT = _blockFace(SkinVertexFormat.SKIN_BLOCK_FACE_TRANSLUCENT).texture(ModTextures.CUBE).transparency(Transparency.TRANSLUCENT).target(Target.TRANSLUCENT).build("aw_translucent_quad_face");
    public static final IRenderType BLOCK_FACE_LIGHTING_TRANSLUCENT = _blockFace(SkinVertexFormat.SKIN_BLOCK_FACE_LIGHTING_TRANSLUCENT).texture(ModTextures.LIGHTING_CUBE).transparency(Transparency.TRANSLUCENT).target(Target.TRANSLUCENT).build("aw_translucent_lighting_quad_face");

    private static final IRenderType LINES = _line(1).build("aw_lines");
    private static final IRenderType LINE_STRIP = _builder(SkinVertexFormat.LINE_STRIP).lineWidth(1).build("aw_line_strip");

    private static final ConcurrentHashMap<String, IRenderType> CUSTOM_FACE_SOLID_VARIANTS = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, IRenderType> CUSTOM_FACE_LIGHTING_VARIANTS = new ConcurrentHashMap<>();

    private static final IRenderType[] RENDER_ORDERING_FACES = {BLOCK_FACE_SOLID, BLOCK_FACE_LIGHTING, BLOCK_FACE_TRANSLUCENT, BLOCK_FACE_LIGHTING_TRANSLUCENT};

    public static IRenderType by(ISkinGeometryType geometryType) {
        if (geometryType == SkinGeometryTypes.BLOCK_GLASS) {
            return BLOCK_FACE_TRANSLUCENT;
        }
        if (geometryType == SkinGeometryTypes.BLOCK_GLASS_GLOWING) {
            return BLOCK_FACE_LIGHTING_TRANSLUCENT;
        }
        if (geometryType == SkinGeometryTypes.BLOCK_GLOWING) {
            return BLOCK_FACE_LIGHTING;
        }
        return BLOCK_FACE_SOLID;
    }

    public static IRenderType customFace(String name, SkinVertexFormat format, IResourceLocation texture, boolean isGrowing, boolean isCull) {
        // select a variant container.
        var variants = CUSTOM_FACE_SOLID_VARIANTS;
        if (isGrowing) {
            variants = CUSTOM_FACE_LIGHTING_VARIANTS;
        }
        var key = String.format("%s/%s", name, texture.getPath());
        return variants.computeIfAbsent(key, it -> {
            var builder = _customFace(format).texture(texture);
            if (isCull) {
                builder = builder.cull();
            }
            return builder.build(it);
        });
    }

    public static IRenderType geometryFace(ISkinGeometryType type, IResourceLocation texture, boolean isGrowing) {
        // ..
        if (isGrowing) {
            if (type == SkinGeometryTypes.CUBE) {
                return customFace("aw_cube_lighting", SkinVertexFormat.SKIN_CUBE_FACE_LIGHTING, texture, true, false);
            }
            if (type == SkinGeometryTypes.CUBE_CULL) {
                return customFace("aw_cube_lighting_cull", SkinVertexFormat.SKIN_CUBE_FACE_LIGHTING, texture, true, true);
            }
            if (type == SkinGeometryTypes.MESH) {
                return customFace("aw_mesh_lighting", SkinVertexFormat.SKIN_MESH_FACE_LIGHTING, texture, true, false);
            }
            if (type == SkinGeometryTypes.MESH_CULL) {
                return customFace("aw_mesh_lighting_cull", SkinVertexFormat.SKIN_MESH_FACE_LIGHTING, texture, true, true);
            }
        } else {
            if (type == SkinGeometryTypes.CUBE) {
                return customFace("aw_cube_solid", SkinVertexFormat.SKIN_CUBE_FACE, texture, false, false);
            }
            if (type == SkinGeometryTypes.CUBE_CULL) {
                return customFace("aw_cube_solid_cull", SkinVertexFormat.SKIN_CUBE_FACE, texture, false, true);
            }
            if (type == SkinGeometryTypes.MESH) {
                return customFace("aw_mesh_solid", SkinVertexFormat.SKIN_MESH_FACE, texture, false, false);
            }
            if (type == SkinGeometryTypes.MESH_CULL) {
                return customFace("aw_mesh_solid_cull", SkinVertexFormat.SKIN_MESH_FACE, texture, false, true);
            }
        }
        return by(type);
    }

    public static IRenderType lines() {
        return LINES;
    }

    public static IRenderType lineStrip() {
        return LINE_STRIP;
    }

    public static IRenderType entityCutout(IResourceLocation texture) {
        return _entity(SkinVertexFormat.ENTITY_CUTOUT, texture).cull().build("aw_player_solid");
    }

    public static IRenderType entityCutoutNoCull(IResourceLocation texture) {
        return _entity(SkinVertexFormat.ENTITY_CUTOUT_NO_CULL, texture).build("aw_player_cutout");
    }

    public static IRenderType entityTranslucentCull(IResourceLocation texture) {
        return _entity(SkinVertexFormat.ENTITY_TRANSLUCENT, texture).cull().transparency(Transparency.TRANSLUCENT).build("aw_player_translucent");
    }

    public static int getPriority(IRenderType renderType) {
        int index = 1;
        for (var target : SkinRenderType.RENDER_ORDERING_FACES) {
            if (target == renderType) {
                return index;
            }
            index += 1;
        }
        index += 1;
        if (CUSTOM_FACE_SOLID_VARIANTS.containsValue(renderType)) {
            return index;
        }
        index += 1;
        if (CUSTOM_FACE_LIGHTING_VARIANTS.containsValue(renderType)) {
            return index;
        }
        return 0;
    }

//    public static boolean isGrowing(IRenderType renderType) {
//        // do fast hitting.
//        if (renderType == BLOCK_FACE_LIGHTING || renderType == BLOCK_FACE_LIGHTING_TRANSLUCENT) {
//            return true;
//        }
//        // do fast missing.
//        if (renderType == BLOCK_FACE_SOLID || renderType == BLOCK_FACE_TRANSLUCENT) {
//            return false;
//        }
//        return CUSTOM_FACE_LIGHTING_VARIANTS.containsValue(renderType);
//    }
//
//    public static boolean isTranslucent(IRenderType renderType) {
//        // do fast hitting.
//        if (renderType == BLOCK_FACE_TRANSLUCENT || renderType == BLOCK_FACE_LIGHTING_TRANSLUCENT) {
//            return true;
//        }
//        // do fast missing.
//        if (renderType == BLOCK_FACE_SOLID || renderType == BLOCK_FACE_LIGHTING) {
//            return false;
//        }
//        if (DataContainer.get(renderType, USING_TRANSLUCENT)) {
//            return true;
//        }
//        return false;
//    }
//
//    public static boolean isUsingIndex(IRenderType renderType) {
//        return DataContainer.get(renderType, USING_INDEX);
//    }

    private static IRenderTypeBuilder _entity(SkinVertexFormat format, IResourceLocation texture) {
        return _builder(format).texture(texture).polygonOffset(0, 30).overlay().lightmap().sortOnUpload().crumbling().outline();
    }

    private static IRenderTypeBuilder _entityHighlight(IResourceLocation texture) {
        return _builder(SkinVertexFormat.ENTITY_ALPHA).texture(texture).overlay().lightmap();
    }

    private static IRenderTypeBuilder _blockFace(SkinVertexFormat format) {
        return _builder(format).outline();
    }

    private static IRenderTypeBuilder _customFace(SkinVertexFormat format) {
        return _builder(format).transparency(Transparency.TRANSLUCENT).target(Target.TRANSLUCENT).outline();
    }

    private static IRenderTypeBuilder _texture(IResourceLocation texture) {
        return _builder(SkinVertexFormat.IMAGE).texture(texture).transparency(Transparency.TRANSLUCENT).target(Target.TRANSLUCENT);
    }

    private static IRenderTypeBuilder _texture2(IResourceLocation texture) {
        return _builder(SkinVertexFormat.BLOCK_CUTOUT).texture(texture).overlay().lightmap();
    }

    private static IRenderTypeBuilder _block(IResourceLocation texture) {
        return _builder(SkinVertexFormat.BLOCK).texture(texture).overlay().lightmap();
    }

    private static IRenderTypeBuilder _line(float lineWidth) {
        return _builder(SkinVertexFormat.LINE).lineWidth(lineWidth).polygonOffset(0, 10);
    }

    private static IRenderTypeBuilder _builder(SkinVertexFormat format) {
        return AbstractRenderTypeImpl.builder(format);
    }
}
