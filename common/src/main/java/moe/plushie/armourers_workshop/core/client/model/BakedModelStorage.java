package moe.plushie.armourers_workshop.core.client.model;

import moe.plushie.armourers_workshop.api.skin.ISkinDataProvider;
import moe.plushie.armourers_workshop.init.ModConstants;
import moe.plushie.armourers_workshop.utils.EmbeddedSkinStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Proxy;

@Environment(EnvType.CLIENT)
public class BakedModelStorage {

    private static BakedModel SHARED_MODEL;

    final ItemStack itemStack;
    final EmbeddedSkinStack embeddedStack;
    final Level level;
    final LivingEntity entity;
    final BakedModel bakedModel;

    public BakedModelStorage(ItemStack itemStack, EmbeddedSkinStack embeddedStack, LivingEntity entity, @Nullable Level level, BakedModel bakedModel) {
        this.itemStack = itemStack;
        this.embeddedStack = embeddedStack;
        this.level = level;
        this.entity = entity;
        this.bakedModel = bakedModel;
    }

    @Nullable
    public static BakedModelStorage unwrap(BakedModel bakedModel) {
        if (bakedModel instanceof ISkinDataProvider) {
            return ((ISkinDataProvider) bakedModel).getSkinData();
        }
        return null;
    }

    public static BakedModel wrap(BakedModel bakedModel, ItemStack itemStack, EmbeddedSkinStack embeddedStack, LivingEntity entity, @Nullable Level level) {
        // when the world is empty, this means the model is rendering on the GUI.
        if (level == null) {
            bakedModel = getSkinBakedModel();
        }
        // we use a java proxy, which will forward all methods back to the original baked model.
        Class<?>[] classes = new Class[]{BakedModel.class, ISkinDataProvider.class};
        BakedModelStorage storage = new BakedModelStorage(itemStack, embeddedStack, entity, level, bakedModel);
        return (BakedModel) Proxy.newProxyInstance(BakedModel.class.getClassLoader(), classes, (proxy, method, methodArgs) -> {
            if (method.getDeclaringClass() == ISkinDataProvider.class) {
                return storage;
            }
            return method.invoke(storage.bakedModel, methodArgs);
        });
    }

    public static BakedModel getSkinBakedModel() {
        if (SHARED_MODEL == null) {
            SHARED_MODEL = Minecraft.getInstance().getModelManager().getModel(new ModelResourceLocation(ModConstants.key("skin"), "inventory"));
        }
        return SHARED_MODEL;
    }

    public BakedModel getOriginModel() {
        return bakedModel;
    }

    public LivingEntity getEntity() {
        return entity;
    }

    public Level getLevel() {
        return level;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public EmbeddedSkinStack getEmbeddedStack() {
        return embeddedStack;
    }
}
