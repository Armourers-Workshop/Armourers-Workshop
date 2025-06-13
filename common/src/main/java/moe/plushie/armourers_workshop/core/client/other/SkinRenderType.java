package moe.plushie.armourers_workshop.core.client.other;

import moe.plushie.armourers_workshop.api.client.IRenderType;
import moe.plushie.armourers_workshop.api.client.IRenderTypeBuilder;
import moe.plushie.armourers_workshop.api.core.IResourceLocation;
import moe.plushie.armourers_workshop.compatibility.client.AbstractRenderTypeImpl;
import moe.plushie.armourers_workshop.core.skin.geometry.SkinGeometryType;
import moe.plushie.armourers_workshop.core.skin.geometry.SkinGeometryTypes;
import moe.plushie.armourers_workshop.core.utils.Collections;
import moe.plushie.armourers_workshop.init.ModTextures;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.List;
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

    public static final IRenderType BLOCK_FACE_SOLID = _blockFace(SkinVertexFormat.SKIN_BLOCK_FACE_SOLID).texture(ModTextures.CUBE).ordinal(200).build("aw_block_face_sold");
    public static final IRenderType BLOCK_FACE_LIGHTING = _blockFace(SkinVertexFormat.SKIN_BLOCK_FACE_LIGHTING).texture(ModTextures.LIGHTING_CUBE).ordinal(200).build("aw_block_face_lighting");
    public static final IRenderType BLOCK_FACE_TRANSLUCENT = _blockFace(SkinVertexFormat.SKIN_BLOCK_FACE_TRANSLUCENT).texture(ModTextures.CUBE).transparency(Transparency.TRANSLUCENT).target(Target.TRANSLUCENT).ordinal(400).build("aw_block_face_translucent");
    public static final IRenderType BLOCK_FACE_LIGHTING_TRANSLUCENT = _blockFace(SkinVertexFormat.SKIN_BLOCK_FACE_LIGHTING_TRANSLUCENT).texture(ModTextures.LIGHTING_CUBE).transparency(Transparency.TRANSLUCENT).target(Target.TRANSLUCENT).ordinal(400).build("aw_block_face_translucent_lighting");

    private static final IRenderType LINES = _line(1).build("aw_lines");
    private static final IRenderType LINE_STRIP = _builder(SkinVertexFormat.LINE_STRIP).lineWidth(1).build("aw_line_strip");

    private static final ConcurrentHashMap<String, IRenderType> CUSTOM_FACE_VARIANTS = new ConcurrentHashMap<>();

    public static IRenderType by(SkinGeometryType geometryType) {
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

    public static IRenderType customFace(String name, SkinVertexFormat format, IResourceLocation texture, boolean isTranslucent, boolean isEmissive, boolean isCull) {
        var key = String.format("%s/%s", name, texture.path());
        return CUSTOM_FACE_VARIANTS.computeIfAbsent(key, it -> {
            var builder = _customFace(format).texture(texture);
            if (isTranslucent) {
                builder = builder.transparency(Transparency.TRANSLUCENT).target(Target.TRANSLUCENT);
            }
            if (isCull) {
                builder = builder.cull();
            }
            if (isTranslucent) {
                builder = builder.ordinal(400);
            } else {
                builder = builder.ordinal(200);
            }
            return builder.build(it);
        });
    }

    public static IRenderType geometryFace(SkinGeometryType type, IResourceLocation texture, boolean isTranslucent, boolean isEmissive) {
        var builder = GeometryFaceBuilder.search(type, isTranslucent, isEmissive);
        if (builder != null) {
            return builder.build(texture);
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
        return _builder(format).outline();
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

    private static class GeometryFaceBuilder {

        private static final List<GeometryFaceBuilder> BUILDERS = Collections.immutableList(it -> {

            it.add(new GeometryFaceBuilder("aw_cube_face_solid", SkinGeometryTypes.CUBE, SkinVertexFormat.SKIN_CUBE_FACE_SOLID, false, false, false));
            it.add(new GeometryFaceBuilder("aw_cube_face_lighting", SkinGeometryTypes.CUBE, SkinVertexFormat.SKIN_CUBE_FACE_LIGHTING, false, true, false));
            it.add(new GeometryFaceBuilder("aw_cube_face_translucent", SkinGeometryTypes.CUBE, SkinVertexFormat.SKIN_CUBE_FACE_TRANSLUCENT, true, false, false));
            it.add(new GeometryFaceBuilder("aw_cube_face_translucent_lighting", SkinGeometryTypes.CUBE, SkinVertexFormat.SKIN_CUBE_FACE_LIGHTING_TRANSLUCENT, true, true, false));

            it.add(new GeometryFaceBuilder("aw_cube_face_solid_cull", SkinGeometryTypes.CUBE_CULL, SkinVertexFormat.SKIN_CUBE_FACE_SOLID, false, false, true));
            it.add(new GeometryFaceBuilder("aw_cube_face_lighting_cull", SkinGeometryTypes.CUBE_CULL, SkinVertexFormat.SKIN_CUBE_FACE_LIGHTING, false, true, true));
            it.add(new GeometryFaceBuilder("aw_cube_face_translucent_cull", SkinGeometryTypes.CUBE_CULL, SkinVertexFormat.SKIN_CUBE_FACE_TRANSLUCENT, true, false, true));
            it.add(new GeometryFaceBuilder("aw_cube_face_translucent_lighting_cull", SkinGeometryTypes.CUBE_CULL, SkinVertexFormat.SKIN_CUBE_FACE_LIGHTING_TRANSLUCENT, true, true, true));

            it.add(new GeometryFaceBuilder("aw_mesh_face_solid", SkinGeometryTypes.MESH, SkinVertexFormat.SKIN_MESH_FACE_SOLID, false, false, false));
            it.add(new GeometryFaceBuilder("aw_mesh_face_lighting", SkinGeometryTypes.MESH, SkinVertexFormat.SKIN_MESH_FACE_LIGHTING, false, true, false));
            it.add(new GeometryFaceBuilder("aw_mesh_face_translucent", SkinGeometryTypes.MESH, SkinVertexFormat.SKIN_MESH_FACE_TRANSLUCENT, true, false, false));
            it.add(new GeometryFaceBuilder("aw_mesh_face_translucent_lighting", SkinGeometryTypes.MESH, SkinVertexFormat.SKIN_MESH_FACE_LIGHTING_TRANSLUCENT, true, true, false));

            it.add(new GeometryFaceBuilder("aw_mesh_face_solid_cull", SkinGeometryTypes.MESH_CULL, SkinVertexFormat.SKIN_MESH_FACE_SOLID, false, false, true));
            it.add(new GeometryFaceBuilder("aw_mesh_face_lighting_cull", SkinGeometryTypes.MESH_CULL, SkinVertexFormat.SKIN_MESH_FACE_LIGHTING, false, true, true));
            it.add(new GeometryFaceBuilder("aw_mesh_face_translucent_cull", SkinGeometryTypes.MESH_CULL, SkinVertexFormat.SKIN_MESH_FACE_TRANSLUCENT, true, false, true));
            it.add(new GeometryFaceBuilder("aw_mesh_face_translucent_lighting_cull", SkinGeometryTypes.MESH_CULL, SkinVertexFormat.SKIN_MESH_FACE_LIGHTING_TRANSLUCENT, true, true, true));
        });

        private final String name;
        private final SkinGeometryType type;
        private final SkinVertexFormat format;
        private final boolean isTranslucent;
        private final boolean isEmissive;
        private final boolean isCull;

        public GeometryFaceBuilder(String name, SkinGeometryType type, SkinVertexFormat format, boolean isTranslucent, boolean isEmissive, boolean isCull) {
            this.name = name;
            this.type = type;
            this.format = format;
            this.isTranslucent = isTranslucent;
            this.isEmissive = isEmissive;
            this.isCull = isCull;
        }

        public static GeometryFaceBuilder search(SkinGeometryType type, boolean isTranslucent, boolean isEmissive) {
            for (var it : BUILDERS) {
                if (type.equals(it.type) && isTranslucent == it.isTranslucent && isEmissive == it.isEmissive) {
                    return it;
                }
            }
            return null;
        }

        public IRenderType build(IResourceLocation texture) {
            return customFace(name, format, texture, isTranslucent, isEmissive, isCull);
        }
    }
}
