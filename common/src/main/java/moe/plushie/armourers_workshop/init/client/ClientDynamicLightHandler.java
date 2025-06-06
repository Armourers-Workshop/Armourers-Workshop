package moe.plushie.armourers_workshop.init.client;

import moe.plushie.armourers_workshop.core.client.bake.SkinBakery;
import moe.plushie.armourers_workshop.core.client.model.EmbeddedItemModel;
import moe.plushie.armourers_workshop.core.client.model.EmbeddedItemModels;
import moe.plushie.armourers_workshop.core.client.other.BlockEntityRenderData;
import moe.plushie.armourers_workshop.core.client.other.EntityRenderData;
import moe.plushie.armourers_workshop.core.client.other.SkinLuminanceManager;
import moe.plushie.armourers_workshop.core.data.DataContainer;
import moe.plushie.armourers_workshop.core.data.ticket.Tickets;
import moe.plushie.armourers_workshop.init.ModConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Function;

@Environment(EnvType.CLIENT)
public class ClientDynamicLightHandler {

    private static Entity TICKING_ENTITY;

    public static void init() {
        ModConfig.Client.enableDynamicLightHandler = true;
    }

    public static void startTick(Entity entity) {
        TICKING_ENTITY = entity;
    }

    public static void endTick(Entity entity) {
        TICKING_ENTITY = null;
    }

    public static void apply(ItemStack itemStack, boolean submergedInWater, CallbackInfoReturnable<Integer> cir) {
        // the user requires disable the skin dynamic light handler.
        if (!ModConfig.enableDynamicLightHandler()) {
            return;
        }
        var itemModel = getItemModel(itemStack);
        if (itemModel == null) {
            return;
        }
        var descriptor = itemModel.getSourceSkin();
        var bakedSkin = SkinBakery.getInstance().loadSkin(descriptor, Tickets.INVENTORY);
        if (bakedSkin != null) {
            cir.setReturnValue(bakedSkin.getRenderInfo().getLuminance());
        }
    }

    public static <T extends Entity, A> A apply(A handler, T entity, Function<SkinLuminanceManager<? super T>, A> builder) {
        // the user requires disable the skin dynamic light handler.
        if (!ModConfig.enableDynamicLightHandler()) {
            return handler;
        }
        var renderData = EntityRenderData.of(entity);
        if (renderData != null) {
            var manger = renderData.getLuminanceManager();
            if (manger.isEnabled()) {
                return DataContainer.of(manger, builder);
            }
        }
        return handler;
    }

    public static <T extends BlockEntity, A> A apply(A handler, T entity, Function<SkinLuminanceManager<? super T>, A> builder) {
        // the user requires disable the skin dynamic light handler.
        if (!ModConfig.enableDynamicLightHandler()) {
            return handler;
        }
        var renderData = BlockEntityRenderData.of(entity);
        if (renderData != null) {
            var manager = renderData.getLuminanceManager();
            if (manager.isEnabled()) {
                return DataContainer.of(manager, builder);
            }
        }
        return handler;
    }

    private static EmbeddedItemModel getItemModel(ItemStack itemStack) {
        if (TICKING_ENTITY instanceof LivingEntity entity) {
            return getItemModel(itemStack, entity.getLevel(), entity);
        }
        return getItemModel(itemStack, null, null);
    }

    private static EmbeddedItemModel getItemModel(ItemStack itemStack, @Nullable Level level, @Nullable LivingEntity entity) {
        var model = Minecraft.getInstance().getItemModel(itemStack, level, entity, 0);
        var itemModels = EmbeddedItemModels.of(itemStack);
        return itemModels.get(model);
    }
}
