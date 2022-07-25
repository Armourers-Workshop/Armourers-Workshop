package moe.plushie.armourers_workshop.core.client.model;

import moe.plushie.armourers_workshop.api.skin.ISkinDataProvider;
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

@Environment(value = EnvType.CLIENT)
public class BakedModelStroage {

    private static BakedModel SHARED_MODEL;

    final ItemStack itemStack;
    final Level world;
    final LivingEntity entity;
    final BakedModel bakedModel;

    public BakedModelStroage(ItemStack itemStack, LivingEntity entity, @Nullable Level world, BakedModel bakedModel) {
        this.itemStack = itemStack;
        this.world = world;
        this.entity = entity;
        this.bakedModel = bakedModel;
    }

    @Nullable
    public static BakedModelStroage unwrap(BakedModel bakedModel) {
        if (bakedModel instanceof ISkinDataProvider) {
            return ((ISkinDataProvider) bakedModel).getSkinData();
        }
        return null;
    }

    public static BakedModel wrap(BakedModel bakedModel, ItemStack itemStack, LivingEntity entity, @Nullable Level world) {
        // when the world is empty, this means the model is rendering on the GUI.
        if (world == null) {
            bakedModel = getSkinBakedModel();
        }
        // we use a java proxy, which will forward all methods back to the original baked model.
        Class<?>[] classes = new Class[]{BakedModel.class, ISkinDataProvider.class};
        BakedModelStroage stroage = new BakedModelStroage(itemStack, entity, world, bakedModel);
        return (BakedModel) Proxy.newProxyInstance(BakedModel.class.getClassLoader(), classes, (proxy, method, methodArgs) -> {
            if (method.getDeclaringClass() == ISkinDataProvider.class) {
                return stroage;
            }
            return method.invoke(stroage.bakedModel, methodArgs);
        });
    }

    public static BakedModel getSkinBakedModel() {
        if (SHARED_MODEL == null) {
            SHARED_MODEL = Minecraft.getInstance().getModelManager().getModel(new ModelResourceLocation("armourers_workshop:skin#inventory"));
        }
        return SHARED_MODEL;
    }

    public BakedModel getOriginModel() {
        return bakedModel;
    }

    public LivingEntity getEntity() {
        return entity;
    }

    public Level getWorld() {
        return world;
    }
}
