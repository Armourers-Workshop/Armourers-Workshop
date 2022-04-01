package moe.plushie.armourers_workshop.core.handler;

import moe.plushie.armourers_workshop.core.capability.SkinWardrobe;
import moe.plushie.armourers_workshop.core.capability.SkinWardrobeProvider;
import moe.plushie.armourers_workshop.core.entity.EntityProfile;
import moe.plushie.armourers_workshop.core.entity.EntityProfiles;
import moe.plushie.armourers_workshop.core.network.NetworkHandler;
import moe.plushie.armourers_workshop.core.network.packet.UpdateContextPacket;
import moe.plushie.armourers_workshop.core.render.bake.SkinBakery;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.core.skin.SkinLoader;
import moe.plushie.armourers_workshop.core.utils.SkinSlotType;
import moe.plushie.armourers_workshop.init.common.AWCore;
import moe.plushie.armourers_workshop.library.data.SkinLibraryManager;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.world.GameRules;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import javax.annotation.Nullable;
import java.util.Objects;

public class PlayerNetworkHandler {

    private static boolean shouldKeepWardrobe(PlayerEntity entity) {
        if (entity.isSpectator()) {
            return true;
        }
        return entity.level.getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY);
    }

    private static ItemStack getSkinFromEquipment(@Nullable Entity entity, SkinSlotType skinSlotType, EquipmentSlotType equipmentSlotType) {
        ItemStack itemStack = ItemStack.EMPTY;
        if (entity instanceof LivingEntity) {
            itemStack = ((LivingEntity) entity).getItemBySlot(equipmentSlotType);
        }
        if (itemStack.isEmpty()) {
            return itemStack;
        }
        // embedded skin is the highest priority
        SkinDescriptor descriptor = SkinDescriptor.of(itemStack);
        if (descriptor.accept(itemStack)) {
            return itemStack;
        }
        SkinWardrobe wardrobe = SkinWardrobe.of(entity);
        if (wardrobe != null) {
            ItemStack itemStack1 = wardrobe.getItem(skinSlotType, 0);
            descriptor = SkinDescriptor.of(itemStack1);
            if (descriptor.accept(itemStack)) {
                return itemStack1;
            }
        }
        return ItemStack.EMPTY;
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void onPlayerLogin(ClientPlayerNetworkEvent.LoggedInEvent event) {
        if (Objects.equals(event.getPlayer(), Minecraft.getInstance().player)) {
            SkinBakery.start();
        }
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void onPlayerLogout(ClientPlayerNetworkEvent.LoggedOutEvent event) {
        if (Objects.equals(event.getPlayer(), Minecraft.getInstance().player)) {
            SkinBakery.stop();
            SkinLoader.getInstance().clear();
            SkinLibraryManager.getClient().getPublicSkinLibrary().reset();
            SkinLibraryManager.getClient().getPrivateSkinLibrary().reset();
        }
    }

    @SubscribeEvent
    public void onPlayerLogout2(PlayerEvent.PlayerLoggedOutEvent event) {
        SkinLibraryManager.getServer().remove(event.getPlayer());
    }

    @SubscribeEvent
    public void onPlayerClone(PlayerEvent.Clone event) {
        if (event.isCanceled()) {
            return;
        }
        SkinWardrobe oldWardrobe = SkinWardrobe.of(event.getOriginal());
        SkinWardrobe newWardrobe = SkinWardrobe.of(event.getPlayer());
        if (newWardrobe != null && oldWardrobe != null) {
            newWardrobe.deserializeNBT(oldWardrobe.serializeNBT());
            newWardrobe.sendToAll();
        }
    }

    @SubscribeEvent
    public void onPlayerDrops(LivingDropsEvent event) {
        if (event.isCanceled()) {
            return;
        }
        LivingEntity entity = event.getEntityLiving();
        if (entity instanceof PlayerEntity) {
            if (shouldKeepWardrobe((PlayerEntity) entity)) {
                return; // ignore
            }
            SkinWardrobe oldWardrobe = SkinWardrobe.of(entity);
            if (oldWardrobe != null) {
                oldWardrobe.dropAll(entity::spawnAtLocation);
            }
        }
    }

    @SubscribeEvent
    public void onAttachEntityCapabilities(AttachCapabilitiesEvent<Entity> event) {
        Entity entity = event.getObject();
        EntityProfile profile = EntityProfiles.getProfile(entity);
        if (profile != null) {
            event.addCapability(SkinWardrobeProvider.WARDROBE_ID, new SkinWardrobeProvider(entity, profile));
        }
    }

    @SubscribeEvent
    public void onStartTracking(PlayerEvent.StartTracking event) {
        if (event.isCanceled()) {
            return;
        }
        if (EntityProfiles.getProfile(event.getTarget()) == null) {
            return;
        }
        SkinWardrobe wardrobe = SkinWardrobe.of(event.getTarget());
        if (wardrobe != null) {
            wardrobe.broadcast((ServerPlayerEntity) event.getPlayer());
        }
    }

    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinWorldEvent event) {
        if (event.isCanceled()) {
            return;
        }
        Entity entity = event.getEntity();
        if (entity instanceof AbstractArrowEntity && !event.getWorld().isClientSide()) {
            Entity owner = ((AbstractArrowEntity) entity).getOwner();
            ItemStack itemStack = getSkinFromEquipment(owner, SkinSlotType.BOW, EquipmentSlotType.MAINHAND);
            if (!itemStack.isEmpty()) {
                SkinWardrobe wardrobe = SkinWardrobe.of(entity);
                if (wardrobe != null) {
                    wardrobe.setItem(SkinSlotType.BOW, 0, itemStack.copy());
                }
            }
        }
        if (entity instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity) entity;
            NetworkHandler.getInstance().sendTo(new UpdateContextPacket(), player);
            SkinWardrobe wardrobe = SkinWardrobe.of(player);
            if (wardrobe != null) {
                wardrobe.broadcast(player);
            }
        }
    }
}
