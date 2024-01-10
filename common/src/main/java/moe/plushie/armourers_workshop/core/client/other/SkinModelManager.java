package moe.plushie.armourers_workshop.core.client.other;

import moe.plushie.armourers_workshop.ArmourersWorkshop;
import moe.plushie.armourers_workshop.api.skin.ISkinPartType;
import moe.plushie.armourers_workshop.core.client.bake.BakedItemModel;
import moe.plushie.armourers_workshop.core.client.bake.BakedSkin;
import moe.plushie.armourers_workshop.core.client.bake.BakedSkinPart;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
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

@Environment(EnvType.CLIENT)
public class SkinModelManager {

    final static SkinModelManager INSTANCE = new SkinModelManager();

    final ModelManager modelManager;

    final HashMap<ISkinPartType, BakedModel> cachedModels = new HashMap<>();
    final HashMap<ISkinPartType, BakedModel> cachedItemModels = new HashMap<>();

    SkinModelManager() {
        modelManager = Minecraft.getInstance().getModelManager();
    }

    public static SkinModelManager getInstance() {
        return INSTANCE;
    }

    public BakedModel getModel(BakedSkinPart bakedPart, BakedSkin bakedSkin, ItemStack itemStack, Entity entity) {
        return getModel(bakedPart, bakedSkin, itemStack, entity.getLevel(), entity);
    }

    public BakedModel getModel(BakedSkinPart bakedPart, BakedSkin bakedSkin, ItemStack itemStack, @Nullable Level level, @Nullable Entity entity) {
        // yep, we prefer to use the overridden item model.
        BakedItemModel itemModel = bakedSkin.getItemModel();
        if (itemModel != null) {
            ClientLevel clientWorld = ObjectUtils.safeCast(level, ClientLevel.class);
            LivingEntity livingEntity = ObjectUtils.safeCast(entity, LivingEntity.class);
            return itemModel.resolve(itemModel, itemStack, clientWorld, livingEntity, 0);
        }
        BakedModel bakedModel = loadModel(bakedPart.getType());
        ClientLevel clientWorld = ObjectUtils.safeCast(level, ClientLevel.class);
        LivingEntity livingEntity = ObjectUtils.safeCast(entity, LivingEntity.class);
        return bakedModel.getOverrides().resolve(bakedModel, itemStack, clientWorld, livingEntity, 0);
    }

    public BakedModel getMissingModel() {
        return modelManager.getMissingModel();
    }

    private BakedModel loadModel(ISkinPartType partType) {
        BakedModel bakedModel = cachedModels.get(partType);
        if (bakedModel != null) {
            return bakedModel;
        }
        bakedModel = modelManager.getModel(ArmourersWorkshop.getCustomModel(partType.getRegistryName()));
        if (partType != SkinPartTypes.UNKNOWN && bakedModel == getMissingModel()) {
            bakedModel = loadModel(SkinPartTypes.UNKNOWN);
        }
        cachedModels.put(partType, bakedModel);
        return bakedModel;
    }
}
