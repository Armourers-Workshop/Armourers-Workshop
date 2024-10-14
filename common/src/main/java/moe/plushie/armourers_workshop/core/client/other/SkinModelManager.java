package moe.plushie.armourers_workshop.core.client.other;

import moe.plushie.armourers_workshop.ArmourersWorkshop;
import moe.plushie.armourers_workshop.api.skin.ISkinPartType;
import moe.plushie.armourers_workshop.core.client.bake.BakedItemModel;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

@Environment(EnvType.CLIENT)
public class SkinModelManager {

    private final static SkinModelManager INSTANCE = new SkinModelManager();

    private final ModelManager modelManager;

    private final HashMap<ISkinPartType, BakedModel> cachedModels = new HashMap<>();
    private final HashMap<ISkinPartType, BakedModel> cachedItemModels = new HashMap<>();

    private SkinModelManager() {
        modelManager = Minecraft.getInstance().getModelManager();
    }

    public static SkinModelManager getInstance() {
        return INSTANCE;
    }

    public BakedModel getModel(ISkinPartType partType, @Nullable BakedItemModel itemModel, ItemStack itemStack, Entity entity) {
        return getModel(partType, itemModel, itemStack, entity.getLevel(), entity);
    }

    public BakedModel getModel(ISkinPartType partType, @Nullable BakedItemModel itemModel, ItemStack itemStack, @Nullable Level level, @Nullable Entity entity) {
        var clientWorld = ObjectUtils.safeCast(level, ClientLevel.class);
        var livingEntity = ObjectUtils.safeCast(entity, LivingEntity.class);
        // we prefer to use the overridden item model.
        if (itemModel != null) {
            return itemModel.resolve(itemModel, itemStack, clientWorld, livingEntity, 0);
        }
        var bakedModel = loadModel(partType);
        return bakedModel.getOverrides().resolve(bakedModel, itemStack, clientWorld, livingEntity, 0);
    }

    public BakedModel getMissingModel() {
        return modelManager.getMissingModel();
    }

    private BakedModel loadModel(ISkinPartType partType) {
        var bakedModel = cachedModels.get(partType);
        if (bakedModel != null) {
            return bakedModel;
        }
        var modelId = ArmourersWorkshop.getCustomModel(partType.getRegistryName());
        bakedModel = modelManager.getModel(ResourceLocation.create(modelId, "inventory"));
        if (partType != SkinPartTypes.UNKNOWN && bakedModel == getMissingModel()) {
            bakedModel = loadModel(SkinPartTypes.UNKNOWN);
        }
        cachedModels.put(partType, bakedModel);
        return bakedModel;
    }
}
