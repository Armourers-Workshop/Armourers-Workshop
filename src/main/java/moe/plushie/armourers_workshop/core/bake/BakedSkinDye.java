package moe.plushie.armourers_workshop.core.bake;

import javafx.util.Pair;
import moe.plushie.armourers_workshop.core.api.ISkinPaintType;
import moe.plushie.armourers_workshop.core.api.ISkinPartType;
import moe.plushie.armourers_workshop.core.skin.data.SkinDye;
import moe.plushie.armourers_workshop.core.skin.painting.SkinPaintTypes;
import moe.plushie.armourers_workshop.core.utils.SkinCore;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.function.BiConsumer;

@OnlyIn(Dist.CLIENT)
public class BakedSkinDye {

    private final SkinDye dye = new SkinDye();

    private BakedSkinTexture texture;

    private ResourceLocation cachedResource;
    private SkinDye[] cachedDyes;

    public BakedSkinDye() {
    }

    public BakedSkinDye setTexture(Entity entity) {
        ResourceLocation newLocation = getTextureByEntity(entity);
        if (!Objects.equals(newLocation, this.cachedResource)) {
            this.texture = null;
            this.cachedResource = newLocation;
        }
        return this;
    }
    public BakedSkinDye setDye(SkinDye... dyes) {
        if (Arrays.equals(this.cachedDyes, dyes)) {
            return this;
        }
        this.dye.clear();
        for (SkinDye dye : dyes) {
            if (dye != null && !dye.isEmpty()) {
                this.dye.add(dye);
            }
        }
        this.cachedDyes = dyes;
        return this;
    }

    private static ResourceLocation getTextureByEntity(Entity entity) {
        if (entity instanceof ClientPlayerEntity) {
            return ((ClientPlayerEntity) entity).getSkinTextureLocation();
        }
        return new ResourceLocation("textures/entity/steve.png");
    }


    /**
     * Create a new colour for a dyed vertex.
     *
     * @param source  ARGB.
     * @param dye     ARGB.
     * @param average ARGB.
     */
    private static int getMixedColor(int source, Integer dye, Integer average) {
        if (dye == null || average == null) {
            return source;
        }
        int alpha = source & dye & average & 0xff000000;
        if (alpha == 0) {
            return 0;
        }
        int src = ((source >> 16 & 0xff) + (source >> 8 & 0xff) + (source & 0xff)) / 3;
        int avg = ((average >> 16 & 0xff) + (average >> 8 & 0xff) + (average & 0xff)) / 3;
        int r = MathHelper.clamp((dye >> 16 & 0xff) + src - avg, 0, 255);
        int g = MathHelper.clamp((dye >> 8 & 0xff) + src - avg, 0, 255);
        int b = MathHelper.clamp((dye & 0xff) + src - avg, 0, 255);
        return alpha | r << 16 | g << 8 | b;
    }


    public void resolve(int color, ColouredFace face, PackedColorInfo colorInfo, ISkinPaintType paintType, ISkinPartType partType, BiConsumer<ISkinPaintType, Integer> consumer) {
        SkinDye.PaintColor resolvedColor = dye.getResolvedColor(paintType);
        if (resolvedColor != null) {
            if (resolvedColor.getPaintType() == SkinPaintTypes.NORMAL) {
                color = getMixedColor(color, resolvedColor.getRGB(), colorInfo.getAverageDyeColor(paintType));
            }
            paintType = resolvedColor.getPaintType();
        }
        if (paintType == SkinPaintTypes.NONE) {
            return;
        }
        if (paintType == SkinPaintTypes.RAINBOW) {
            color = getMixedColor(color, 0xff7f7f7f, colorInfo.getAverageDyeColor(paintType));
        }
        if (paintType == SkinPaintTypes.TEXTURE && getTexture() != null) {
            Integer value = getTexture().getColor(face.x, face.y, face.z, face.direction, partType);
            if (value != null) {
                color = value;
            }
        }
        consumer.accept(paintType, color);
    }

    public Object getRequirements(PackedColorInfo colorInfo) {
        if (colorInfo.getPaintTypes().isEmpty()) {
            return null;
        }
        ArrayList<Object> requirements = new ArrayList<>();
        for (ISkinPaintType paintType : colorInfo.getPaintTypes()) {
            if (paintType == SkinPaintTypes.TEXTURE && getTexture() != null) {
                requirements.add(paintType.getId());
                requirements.add(cachedResource);
            } else if (paintType.getDyeType() != null) {
                requirements.add(paintType.getId());
                requirements.add(dye.getColor(paintType));
            }
        }
        return requirements;
    }

    private BakedSkinTexture getTexture() {
        if (cachedResource == null) {
            return null;
        }
        if (texture != null) {
            return texture;
        }
        texture = SkinCore.loadBakedSkinTexture(cachedResource);
        return texture;
    }
}
