package moe.plushie.armourers_workshop.core.render.other;

import moe.plushie.armourers_workshop.core.api.ISkinPartType;
import moe.plushie.armourers_workshop.core.model.bake.PackedColorInfo;
import moe.plushie.armourers_workshop.core.skin.data.SkinDye;
import moe.plushie.armourers_workshop.core.skin.data.SkinPart;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;

@OnlyIn(Dist.CLIENT)
public class BakedSkinDye {

    private final SkinDye dye = new SkinDye();
    private final ResourceLocation texture;

    public BakedSkinDye(Entity entity, SkinDye... dyes) {
        this.texture = getTextureByEntity(entity);
        for (SkinDye dye : dyes) {
            if (dye != null) {
                this.dye.add(dye);
            }
        }
    }

    private static ResourceLocation getTextureByEntity(Entity entity) {
        if (entity instanceof ClientPlayerEntity) {
            return ((ClientPlayerEntity) entity).getSkinTextureLocation();
        }
        return null;
    }

    public int getColor(int x, int y, int z, Direction dir, ISkinPartType partType) {
        return 0;
    }

    public Object getRequirements(PackedColorInfo colorInfo) {
        ArrayList<Object> requirements = new ArrayList<>();
//        if (part.hasDyeChannel()) {
//
//        }
//        if (part.hasTextureChannel() && texture != null) {
//            requirements.add(texture);
//        }
        return requirements;
    }
}
