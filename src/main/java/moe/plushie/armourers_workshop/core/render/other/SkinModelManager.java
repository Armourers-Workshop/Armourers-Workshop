package moe.plushie.armourers_workshop.core.render.other;

import moe.plushie.armourers_workshop.api.skin.ISkinPartType;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import moe.plushie.armourers_workshop.init.common.AWCore;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.HashMap;

@OnlyIn(Dist.CLIENT)
public class SkinModelManager {

    final static SkinModelManager INSTANCE = new SkinModelManager();

    final ModelManager modelManager;

    final HashMap<ISkinPartType, IBakedModel> cachedModels = new HashMap<>();

    SkinModelManager() {
        modelManager = Minecraft.getInstance().getModelManager();
    }

    public static SkinModelManager getInstance() {
        return INSTANCE;
    }

    public IBakedModel getModel(ISkinPartType partType, ItemStack itemStack, @Nullable World world, @Nullable Entity entity) {
        IBakedModel bakedModel = loadModel(partType);
        ClientWorld clientWorld = cast(world, ClientWorld.class);
        LivingEntity livingEntity = cast(entity, LivingEntity.class);
        return bakedModel.getOverrides().resolve(bakedModel, itemStack, clientWorld, livingEntity);
    }

    @Nullable
    private <T> T cast(Object value, Class<T> type) {
        if (type.isInstance(value)) {
            return type.cast(value);
        }
        return null;
    }

    private IBakedModel loadModel(ISkinPartType partType) {
        IBakedModel bakedModel = cachedModels.get(partType);
        if (bakedModel != null) {
            return bakedModel;
        }
        bakedModel = modelManager.getModel(AWCore.getCustomModel(partType.getRegistryName()));
        if (partType != SkinPartTypes.UNKNOWN && bakedModel == modelManager.getMissingModel()) {
            bakedModel = loadModel(SkinPartTypes.UNKNOWN);
        }
        return bakedModel;
    }
}
