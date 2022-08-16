package moe.plushie.armourers_workshop.core.client.other;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import moe.plushie.armourers_workshop.api.skin.ISkinCube;
import moe.plushie.armourers_workshop.utils.RenderUtils;
import moe.plushie.armourers_workshop.utils.TickUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.util.OptionalDouble;

@Environment(value = EnvType.CLIENT)
public class SkinRenderType extends RenderType {

    public static final RenderType MAGIC = createImageType(RenderUtils.TEX_CIRCLE, true, true);
    public static final RenderType EARTH = createImageType(RenderUtils.TEX_EARTH, false, false);
    public static final RenderType ENTITY_OUTLINE = createEntityOutline();
    public static final RenderType PLAYER_CUTOUT_NO_CULL = entityCutoutNoCull(DefaultPlayerSkin.getDefaultSkin());
    public static final RenderType LINES_WITHOUT_TEST = create("aw_box_lines", DefaultVertexFormat.POSITION_COLOR, 1, 256, RenderType.CompositeState.builder().setLineState(new RenderStateShard.LineStateShard(OptionalDouble.empty())).setLayeringState(VIEW_OFFSET_Z_LAYERING).setAlphaState(NO_ALPHA).setDepthTestState(NO_DEPTH_TEST).createCompositeState(false));
    public static final RenderType GUIDES = createMarkerFace2(RenderUtils.TEX_GUIDES);
    protected static final RenderStateShard.LayeringStateShard POLYGON_OFFSET_LAYERING3 = new RenderStateShard.LayeringStateShard("aw_polygon_offset_layering", () -> {
        RenderSystem.enablePolygonOffset();
        RenderSystem.polygonOffset(0.0F, -1000.0F);
    }, () -> {
        RenderSystem.polygonOffset(0.0F, 0.0F);
        RenderSystem.disablePolygonOffset();
    });
    public static final RenderType MARKER_FACE = createMarkerFace(RenderUtils.TEX_MARKERS);
    protected static final RenderStateShard.LayeringStateShard POLYGON_OFFSET_LAYERING2 = new RenderStateShard.LayeringStateShard("aw_polygon_offset_layering", () -> {
        RenderSystem.enablePolygonOffset();
        RenderSystem.polygonOffset(0.0F, 10.0F);
    }, () -> {
        RenderSystem.polygonOffset(0.0F, 0.0F);
        RenderSystem.disablePolygonOffset();
    });
    public static final RenderType PLAYER_CUTOUT = entityCutout(DefaultPlayerSkin.getDefaultSkin());
    public static final RenderType PLAYER_TRANSLUCENT = entityTranslucentCull(DefaultPlayerSkin.getDefaultSkin());
    private static final RenderStateShard.TextureStateShard COLORS = new TextureStateShard(RenderUtils.TEX_CUBE, false, false);
    private static final RenderStateShard.TexturingStateShard COLORS_OFFSET = new TexturingStateShard("aw_offset_texturing", () -> {
        RenderSystem.matrixMode(GL11.GL_TEXTURE);
        RenderSystem.pushMatrix();
        RenderSystem.loadIdentity();
        float f = TickUtils.getPaintTextureOffset() / 256.0f;
        RenderSystem.translatef(0, f, 0.0F);
        RenderSystem.matrixMode(GL11.GL_MODELVIEW);
    }, () -> {
        RenderSystem.matrixMode(GL11.GL_TEXTURE);
        RenderSystem.popMatrix();
        RenderSystem.matrixMode(GL11.GL_MODELVIEW);
    });
    public static final SkinRenderType SOLID_FACE = new SkinRenderType("aw_quad_face", createSolidFace(false), true, false);
    public static final SkinRenderType LIGHTING_FACE = new SkinRenderType("aw_lighting_quad_face", createLightingPart(false), false, false);
    public static final SkinRenderType TRANSLUCENT_SOLID_FACE = new SkinRenderType("aw_translucent_quad_face", createSolidFace(true), true, true);
    public static final SkinRenderType TRANSLUCENT_LIGHTING_FACE = new SkinRenderType("aw_translucent_lighting_quad_face", createLightingPart(true), false, true);
    public static final SkinRenderType[] RENDER_ORDERING_FACES = {
            SOLID_FACE,
            LIGHTING_FACE,
            TRANSLUCENT_SOLID_FACE,
            TRANSLUCENT_LIGHTING_FACE
    };
    private final boolean enableLight;
    private final boolean enableTranslucent;

    public SkinRenderType(String name, RenderType delegate, boolean enableLight, boolean enableTranslucent) {
        super(name, delegate.format(), delegate.mode(), delegate.bufferSize(), delegate.affectsCrumbling(), false, delegate::setupRenderState, delegate::clearRenderState);
        this.enableLight = enableLight;
        this.enableTranslucent = enableTranslucent;
    }

    public static SkinRenderType by(ISkinCube cube) {
        if (cube.isGlass()) {
            if (cube.isGlowing()) {
                return TRANSLUCENT_LIGHTING_FACE;
            } else {
                return TRANSLUCENT_SOLID_FACE;
            }
        }
        if (cube.isGlowing()) {
            return LIGHTING_FACE;
        } else {
            return SOLID_FACE;
        }
    }

    public static RenderType entityCutout(ResourceLocation texture) {
        RenderType.CompositeState state = RenderType.CompositeState.builder()
                .setTextureState(new RenderStateShard.TextureStateShard(texture, false, false))
                .setTransparencyState(NO_TRANSPARENCY)
                .setDiffuseLightingState(DIFFUSE_LIGHTING)
                .setAlphaState(DEFAULT_ALPHA)
                .setLightmapState(LIGHTMAP)
                .setLayeringState(POLYGON_OFFSET_LAYERING2)
                .setOverlayState(OVERLAY)
                .createCompositeState(true);
        return create("aw_entity_cutout_cull", DefaultVertexFormat.NEW_ENTITY, 7, 256, true, false, state);
    }

    public static RenderType entityTranslucentCull(ResourceLocation texture) {
        RenderType.CompositeState state = RenderType.CompositeState.builder()
                .setTextureState(new RenderStateShard.TextureStateShard(texture, false, false))
                .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                .setDiffuseLightingState(DIFFUSE_LIGHTING)
                .setAlphaState(DEFAULT_ALPHA)
                .setLightmapState(LIGHTMAP)
                .setLayeringState(POLYGON_OFFSET_LAYERING2)
                .setOverlayState(OVERLAY)
                .createCompositeState(true);
        return create("aw_entity_translucent_cull", DefaultVertexFormat.NEW_ENTITY, 7, 256, true, true, state);
    }

    public static RenderStateShard colorOffset() {
        return COLORS_OFFSET;
    }

    private static RenderType createEntityOutline() {

        RenderStateShard.LayeringStateShard layerState = new RenderStateShard.LayeringStateShard("custom_polygon_line_layering", () -> {
            GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
            GL11.glLineWidth(1.0f);
        }, () -> {
            GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
        });

        RenderType.CompositeState states = RenderType.CompositeState.builder()
                .setCullState(NO_CULL)
                .setLayeringState(layerState)
                .createCompositeState(false);
        return RenderType.create("aw_entity_outline", DefaultVertexFormat.POSITION_COLOR, GL11.GL_QUADS, 256, states);
    }


    private static RenderType createImageType(ResourceLocation texture, boolean mask, boolean sort) {
        RenderType.CompositeState states = RenderType.CompositeState.builder()
                .setCullState(NO_CULL)
                .setTextureState(new RenderStateShard.TextureStateShard(texture, false, false))
                .setAlphaState(DEFAULT_ALPHA)
                .setWriteMaskState(mask ? COLOR_WRITE : COLOR_DEPTH_WRITE)
                .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                .setOutputState(TRANSLUCENT_TARGET)
                .createCompositeState(false);
        return RenderType.create("aw_magic", DefaultVertexFormat.POSITION_COLOR_TEX, GL11.GL_QUADS, 256, false, sort, states);
    }

    private static RenderType createMarkerFace(ResourceLocation texture) {
        RenderType.CompositeState states = RenderType.CompositeState.builder()
                .setCullState(CULL)
                .setTextureState(new RenderStateShard.TextureStateShard(texture, false, false))
                .setAlphaState(DEFAULT_ALPHA)
                .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                .setOutputState(TRANSLUCENT_TARGET)
                .setLayeringState(POLYGON_OFFSET_LAYERING3)
                .createCompositeState(false);
        return RenderType.create("aw_marker_face", DefaultVertexFormat.POSITION_COLOR_TEX, GL11.GL_QUADS, 256, true, true, states);
    }

    private static RenderType createMarkerFace2(ResourceLocation texture) {
        RenderType.CompositeState states = RenderType.CompositeState.builder()
                .setCullState(NO_CULL)
                .setTextureState(new RenderStateShard.TextureStateShard(texture, false, false))
                .setAlphaState(DEFAULT_ALPHA)
                .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
//                .setOutputState(TRANSLUCENT_TARGET)
                .setLayeringState(POLYGON_OFFSET_LAYERING)
                .createCompositeState(false);
        return RenderType.create("aw_guide_face", DefaultVertexFormat.POSITION_COLOR_TEX, GL11.GL_QUADS, 256, false, false, states);
    }


    private static RenderType createSolidFace(boolean alpha) {
        ImmutableList<VertexFormatElement> elements = ImmutableList.<VertexFormatElement>builder()
                .add(DefaultVertexFormat.ELEMENT_POSITION)
                .add(DefaultVertexFormat.ELEMENT_COLOR)
                .add(DefaultVertexFormat.ELEMENT_UV0)
                .add(DefaultVertexFormat.ELEMENT_NORMAL)
                .add(DefaultVertexFormat.ELEMENT_PADDING)
                .build();
        RenderType.CompositeState states = RenderType.CompositeState.builder()
                .setCullState(NO_CULL)
                .setTextureState(COLORS)
                .setTexturingState(COLORS_OFFSET)
                .setDiffuseLightingState(DIFFUSE_LIGHTING)
                .setLightmapState(LIGHTMAP)
                .setTransparencyState(alpha ? TRANSLUCENT_TRANSPARENCY : NO_TRANSPARENCY)
                .setOutputState(alpha ? TRANSLUCENT_TARGET : MAIN_TARGET)
                .createCompositeState(false);
        return RenderType.create("aw_quad_face", new VertexFormat(elements), GL11.GL_QUADS, 256, states);
    }

    private static RenderType createLightingPart(boolean hasAlpha) {
        ImmutableList<VertexFormatElement> elements = ImmutableList.<VertexFormatElement>builder()
                .add(DefaultVertexFormat.ELEMENT_POSITION)
                .add(DefaultVertexFormat.ELEMENT_COLOR)
                .add(DefaultVertexFormat.ELEMENT_UV0)
                .build();
        RenderType.CompositeState states = RenderType.CompositeState.builder()
                .setCullState(NO_CULL)
                .setTextureState(COLORS)
                .setTexturingState(COLORS_OFFSET)
                .setTransparencyState(hasAlpha ? TRANSLUCENT_TRANSPARENCY : NO_TRANSPARENCY)
                .setOutputState(hasAlpha ? TRANSLUCENT_TARGET : MAIN_TARGET)
                .createCompositeState(false);
        return RenderType.create("aw_light_quad_face", new VertexFormat(elements), GL11.GL_QUADS, 256, states);
    }


    public static RenderType layeredItemSolid(ResourceLocation locationIn) {
        RenderType.CompositeState states = RenderType.CompositeState.builder()
                .setTextureState(new RenderStateShard.TextureStateShard(locationIn, false, false))
                .setTransparencyState(NO_TRANSPARENCY)
                .setDiffuseLightingState(DIFFUSE_LIGHTING)
                .setLightmapState(LIGHTMAP)
                .setOverlayState(OVERLAY)
                .createCompositeState(true);
        return create("forge_item_entity_solid", DefaultVertexFormat.NEW_ENTITY, 7, 256, true, false, states);
    }

    public static RenderType layeredItemTranslucent(ResourceLocation locationIn) {
        RenderType.CompositeState states = RenderType.CompositeState.builder()
                .setTextureState(new RenderStateShard.TextureStateShard(locationIn, false, false))
                .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                .setDiffuseLightingState(DIFFUSE_LIGHTING)
                .setAlphaState(DEFAULT_ALPHA)
                .setLightmapState(LIGHTMAP)
                .setOverlayState(OVERLAY)
                .createCompositeState(true);
        return create("forge_item_entity_translucent_cull", DefaultVertexFormat.NEW_ENTITY, 7, 256, true, true, states);
    }

    public static RenderType unsortedTranslucent(ResourceLocation textureLocation) {
        final boolean sortingEnabled = false;
        RenderType.CompositeState states = RenderType.CompositeState.builder()
                .setTextureState(new RenderStateShard.TextureStateShard(textureLocation, false, false))
                .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                .setDiffuseLightingState(DIFFUSE_LIGHTING)
                .setAlphaState(DEFAULT_ALPHA)
                .setCullState(NO_CULL)
                .setLightmapState(LIGHTMAP)
                .setOverlayState(OVERLAY)
                .createCompositeState(true);
        return create("forge_entity_unsorted_translucent", DefaultVertexFormat.NEW_ENTITY, GL11.GL_QUADS, 256, true, sortingEnabled, states);
    }

    public boolean usesLight() {
        return enableLight;
    }

    public boolean usesTranslucent() {
        return enableTranslucent;
    }
}
