package moe.plushie.armourers_workshop.core.client.model;

import moe.plushie.armourers_workshop.api.data.IAssociatedObjectProvider;
import moe.plushie.armourers_workshop.utils.EmbeddedSkinStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Proxy;

@Environment(EnvType.CLIENT)
public class BakedModelStorage {

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
        if (bakedModel instanceof IAssociatedObjectProvider provider) {
            return provider.getAssociatedObject();
        }
        return null;
    }

    public static BakedModel wrap(BakedModel bakedModel, ItemStack itemStack, EmbeddedSkinStack embeddedStack, LivingEntity entity, @Nullable Level level) {
        // we use a java proxy, which will forward all methods back to the original baked model.
        var classes = new Class[]{BakedModel.class, IAssociatedObjectProvider.class};
        var storage = new BakedModelStorage(itemStack, embeddedStack, entity, level, bakedModel);
        return (BakedModel) Proxy.newProxyInstance(BakedModel.class.getClassLoader(), classes, (proxy, method, methodArgs) -> {
            if (method.getDeclaringClass() == IAssociatedObjectProvider.class) {
                return storage;
            }
            return method.invoke(storage.bakedModel, methodArgs);
        });
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
