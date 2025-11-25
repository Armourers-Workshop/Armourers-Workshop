package moe.plushie.armourers_workshop.core.client.other;

import moe.plushie.armourers_workshop.api.client.IRenderType;
import moe.plushie.armourers_workshop.compat.client.AbstractRenderTypeImpl;
import moe.plushie.armourers_workshop.core.skin.geometry.SkinGeometryType;
import moe.plushie.armourers_workshop.core.skin.geometry.SkinGeometryTypes;
import moe.plushie.armourers_workshop.core.utils.Collections;
import moe.plushie.armourers_workshop.core.utils.OpenResourceLocation;
import moe.plushie.armourers_workshop.init.ModTextures;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("unused")
public abstract class SkinRenderType implements IRenderType {

    public static final IRenderType BLIT_COLOR = _builder(SkinVertexFormat.BLIT_MASK).build("blit_color");
    public static final IRenderType BLIT_MASK = _builder(SkinVertexFormat.BLIT_MASK).colorWrite(false).depthWrite(false).build("blit_mask");
    public static final IRenderType BLIT_TEXTURED = _builder(SkinVertexFormat.BLIT_TEXTURED).build("blit_image");

    public static final IRenderType GUI_COLOR = _builder(SkinVertexFormat.GUI_COLOR).blend(BlendMode.TRANSLUCENT).build("gui_color");
    public static final IRenderType GUI_REVERSED_COLOR = _builder(SkinVertexFormat.GUI_COLOR).blend(BlendMode.ADDITIVE).depthTest(DepthTestMode.NO_DEPTH_TEST).build("gui_reversed_color");

    public static final IRenderType IMAGE_GUIDE = _blockCutout(ModTextures.GUIDES).polygonOffset(-1, -10).blend(BlendMode.TRANSLUCENT).target(Target.TRANSLUCENT).group(Group.CLOUDS).build("image_guide");
    public static final IRenderType IMAGE_MARKER = _blockCutout(ModTextures.MARKERS).polygonOffset(-1, -10).cull().build("image_marker");

    public static final IRenderType HIGHLIGHTED_LINES = _line(1).depthTest(DepthTestMode.NO_DEPTH_TEST).build("lines_ndt");
    public static final IRenderType HIGHLIGHTED_ENTITY_LINES = _entityHighlight(ModTextures.MANNEQUIN_HIGHLIGHT).build("entity_lines");

    public static final IRenderType PLAYER_CUTOUT = entityCutout(ModTextures.MANNEQUIN_DEFAULT);
    public static final IRenderType PLAYER_CUTOUT_NO_CULL = entityCutoutNoCull(ModTextures.MANNEQUIN_DEFAULT);
    public static final IRenderType PLAYER_TRANSLUCENT = entityTranslucentCull(ModTextures.MANNEQUIN_DEFAULT);

    public static final IRenderType BLOCK_EARTH = _builder(SkinVertexFormat.SKIN_BLOCK_FACE_LIGHTING_TRANSLUCENT).texture(ModTextures.EARTH).blend(BlendMode.TRANSLUCENT).target(Target.TRANSLUCENT).group(Group.TRANSLUCENT_BLOCKS).cull().build("block_earth");

    public static final IRenderType BLOCK_CUBE = _block(ModTextures.BLOCK_CUBE).group(Group.SOLID_BLOCKS).build("block_cube");
    public static final IRenderType BLOCK_CUBE_GLASS = _block(ModTextures.BLOCK_CUBE_GLASS).blend(BlendMode.TRANSLUCENT).target(Target.TRANSLUCENT).group(Group.TRANSLUCENT_BLOCKS).sortOnUpload().build("block_cube_glass");
    public static final IRenderType BLOCK_CUBE_GLASS_UNSORTED = _block(ModTextures.BLOCK_CUBE_GLASS).blend(BlendMode.TRANSLUCENT).target(Target.TRANSLUCENT).group(Group.TRANSLUCENT_BLOCKS).build("block_cube_glass_unsorted");

    public static final IRenderType BLOCK_FACE_SOLID = _blockFace(SkinVertexFormat.SKIN_BLOCK_FACE_SOLID).texture(ModTextures.CUBE).group(Group.SOLID_BLOCKS).ordinal(200).build("block_face_sold");
    public static final IRenderType BLOCK_FACE_LIGHTING = _blockFace(SkinVertexFormat.SKIN_BLOCK_FACE_LIGHTING).texture(ModTextures.LIGHTING_CUBE).group(Group.SOLID_BLOCKS).ordinal(200).build("block_face_lighting");
    public static final IRenderType BLOCK_FACE_TRANSLUCENT = _blockFace(SkinVertexFormat.SKIN_BLOCK_FACE_TRANSLUCENT).texture(ModTextures.CUBE).blend(BlendMode.TRANSLUCENT).target(Target.TRANSLUCENT).group(Group.TRANSLUCENT_BLOCKS).ordinal(400).build("block_face_translucent");
    public static final IRenderType BLOCK_FACE_LIGHTING_TRANSLUCENT = _blockFace(SkinVertexFormat.SKIN_BLOCK_FACE_LIGHTING_TRANSLUCENT).texture(ModTextures.LIGHTING_CUBE).blend(BlendMode.TRANSLUCENT).target(Target.TRANSLUCENT).group(Group.TRANSLUCENT_BLOCKS).ordinal(400).build("block_face_translucent_lighting");

    private static final IRenderType LINES = _line(1).build("lines");
    private static final IRenderType LINE_STRIP = _builder(SkinVertexFormat.LINE_STRIP).lineWidth(1).build("line_strip");

    private static final ConcurrentHashMap<String, IRenderType> CUSTOM_FACE_VARIANTS = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, IRenderType> CUSTOM_GUI_IMAGES = new ConcurrentHashMap<>();

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

    public static IRenderType customFace(String name, SkinVertexFormat format, OpenResourceLocation texture, boolean isTranslucent, boolean isEmissive, boolean isCull) {
        var key = String.format("%s/%s", name, texture.path());
        return CUSTOM_FACE_VARIANTS.computeIfAbsent(key, it -> {
            var builder = _customFace(format).texture(texture).group(Group.SOLID_BLOCKS);
            if (isTranslucent) {
                builder = builder.blend(BlendMode.TRANSLUCENT).target(Target.TRANSLUCENT).group(Group.TRANSLUCENT_BLOCKS);
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

    public static IRenderType geometryFace(SkinGeometryType type, OpenResourceLocation texture, boolean isTranslucent, boolean isEmissive) {
        var builder = GeometryFaceBuilder.search(type, isTranslucent, isEmissive);
        if (builder != null) {
            return builder.build(texture);
        }
        return by(type);
    }

    public static IRenderType line() {
        return LINES;
    }

    public static IRenderType lineStrip() {
        return LINE_STRIP;
    }

    public static IRenderType customImage(OpenResourceLocation texture) {
        var key = String.format("gui_image/%s", texture.path());
        return CUSTOM_GUI_IMAGES.computeIfAbsent(key, it -> _builder(SkinVertexFormat.GUI_TEXTURED).texture(texture).blend(BlendMode.TRANSLUCENT).build(it));
    }

    public static IRenderType entityCutout(OpenResourceLocation texture) {
        return _entity(SkinVertexFormat.ENTITY_CUTOUT, texture).group(Group.SOLID_ENTITIES).cull().build("player_solid");
    }

    public static IRenderType entityCutoutNoCull(OpenResourceLocation texture) {
        return _entity(SkinVertexFormat.ENTITY_CUTOUT_NO_CULL, texture).group(Group.CUTOUT_ENTITIES).build("player_cutout");
    }

    public static IRenderType entityTranslucentCull(OpenResourceLocation texture) {
        return _entity(SkinVertexFormat.ENTITY_TRANSLUCENT, texture).cull().blend(BlendMode.TRANSLUCENT).group(Group.TRANSLUCENT_ENTITIES).build("player_translucent");
    }

    private static Builder _entity(SkinVertexFormat format, OpenResourceLocation texture) {
        return _builder(format).texture(texture).polygonOffset(0, 30).overlay().lightmap().sortOnUpload().crumbling().outline();
    }

    private static Builder _entityHighlight(OpenResourceLocation texture) {
        return _builder(SkinVertexFormat.ENTITY_ALPHA).texture(texture).overlay().lightmap();
    }

    private static Builder _blockFace(SkinVertexFormat format) {
        return _builder(format).outline();
    }

    private static Builder _customFace(SkinVertexFormat format) {
        return _builder(format).outline();
    }

    private static Builder _blockCutout(OpenResourceLocation texture) {
        return _builder(SkinVertexFormat.BLOCK_CUTOUT).texture(texture).overlay().lightmap();
    }

    private static Builder _block(OpenResourceLocation texture) {
        return _builder(SkinVertexFormat.BLOCK).texture(texture).overlay().lightmap();
    }

    private static Builder _line(float lineWidth) {
        return _builder(SkinVertexFormat.LINE).lineWidth(lineWidth).polygonOffset(0, 10);
    }

    private static Builder _builder(SkinVertexFormat format) {
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

        public IRenderType build(OpenResourceLocation texture) {
            return customFace(name, format, texture, isTranslucent, isEmissive, isCull);
        }
    }
}
