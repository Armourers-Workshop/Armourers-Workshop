package moe.plushie.armourers_workshop.init.client;

import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import moe.plushie.armourers_workshop.core.client.bake.SkinBakery;
import moe.plushie.armourers_workshop.core.client.other.BlockEntityRenderData;
import moe.plushie.armourers_workshop.core.client.other.EntityRenderData;
import moe.plushie.armourers_workshop.core.client.other.SkinLightSource;
import moe.plushie.armourers_workshop.core.client.render.model.EmbeddedItemModel;
import moe.plushie.armourers_workshop.core.data.ticket.TicketManager;
import moe.plushie.armourers_workshop.init.ModConfig;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

@OnlyIn(Dist.CLIENT)
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

    public static SkinLightSource getLightSource(Entity entity) {
        // the user requires disable the skin dynamic light handler.
        if (!ModConfig.enableDynamicLightHandler()) {
            return null;
        }
        var renderData = EntityRenderData.of(entity);
        if (renderData != null) {
            var lightSource = renderData.lightSource();
            if (lightSource.isEnabled()) {
                return lightSource;
            }
        }
        return null;
    }

    public static SkinLightSource getLightSource(BlockEntity entity) {
        // the user requires disable the skin dynamic light handler.
        if (!ModConfig.enableDynamicLightHandler()) {
            return null;
        }
        var renderData = BlockEntityRenderData.of(entity);
        if (renderData != null) {
            var lightSource = renderData.lightSource();
            if (lightSource.isEnabled()) {
                return lightSource;
            }
        }
        return null;
    }

    public static SkinLightSource getLightSource(ItemStack itemStack, boolean submergedInWater) {
        // the user requires disable the skin dynamic light handler.
        if (!ModConfig.enableDynamicLightHandler()) {
            return null;
        }
        var itemModel = getItemModel(itemStack);
        if (itemModel != null) {
            var bakedSkin = SkinBakery.getInstance().loadSkin(TicketManager.INVENTORY.get(itemModel.skin()));
            if (bakedSkin != null) {
                return bakedSkin.renderInfo().lightSource();
            }
        }
        return null;
    }

    private static EmbeddedItemModel getItemModel(ItemStack itemStack) {
        if (TICKING_ENTITY instanceof LivingEntity entity) {
            return getItemModel(itemStack, entity.level(), entity);
        }
        return getItemModel(itemStack, null, null);
    }

    private static EmbeddedItemModel getItemModel(ItemStack itemStack, @Nullable Level level, @Nullable LivingEntity entity) {
        var model = itemStack.getItemModel(level, entity, 0);
        return itemStack.getEmbeddedItemModel(model);
    }
}
