package moe.plushie.armourers_workshop.core.client.other;

import moe.plushie.armourers_workshop.ArmourersWorkshop;
import moe.plushie.armourers_workshop.api.skin.ISkinPartType;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

@Environment(value = EnvType.CLIENT)
public class SkinModelManager {

    final static SkinModelManager INSTANCE = new SkinModelManager();

    final ModelManager modelManager;

    final HashMap<ISkinPartType, BakedModel> cachedModels = new HashMap<>();

    SkinModelManager() {
        modelManager = Minecraft.getInstance().getModelManager();
    }

    public static SkinModelManager getInstance() {
        return INSTANCE;
    }

    public BakedModel getModel(ISkinPartType partType, ItemStack itemStack, @Nullable Level level, @Nullable Entity entity) {
        BakedModel bakedModel = loadModel(partType);
        ClientLevel clientWorld = cast(level, ClientLevel.class);
        LivingEntity livingEntity = cast(entity, LivingEntity.class);
        //#if MC >= 11800
        return bakedModel.getOverrides().resolve(bakedModel, itemStack, clientWorld, livingEntity, 0);
        //#else
        //# return bakedModel.getOverrides().resolve(bakedModel, itemStack, clientWorld, livingEntity);
        //#endif
    }

    private BakedModel loadModel(ISkinPartType partType) {
        BakedModel bakedModel = cachedModels.get(partType);
        if (bakedModel != null) {
            return bakedModel;
        }
        bakedModel = modelManager.getModel(ArmourersWorkshop.getCustomModel(partType.getRegistryName()));
        if (partType != SkinPartTypes.UNKNOWN && bakedModel == modelManager.getMissingModel()) {
            bakedModel = loadModel(SkinPartTypes.UNKNOWN);
        }
        cachedModels.put(partType, bakedModel);
        return bakedModel;
    }

    @Nullable
    private <T> T cast(Object value, Class<T> type) {
        if (type.isInstance(value)) {
            return type.cast(value);
        }
        return null;
    }
}
