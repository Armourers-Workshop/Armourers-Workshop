package moe.plushie.armourers_workshop.core.handler;

import moe.plushie.armourers_workshop.core.capability.SkinWardrobe;
import moe.plushie.armourers_workshop.core.capability.SkinWardrobeProvider;
import moe.plushie.armourers_workshop.core.entity.EntityProfile;
import moe.plushie.armourers_workshop.core.entity.EntityProfiles;
import moe.plushie.armourers_workshop.core.render.bake.SkinBakery;
import moe.plushie.armourers_workshop.core.skin.SkinLoader;
import moe.plushie.armourers_workshop.core.utils.SkinSlotType;
import moe.plushie.armourers_workshop.init.common.AWCore;
import moe.plushie.armourers_workshop.init.common.ModLog;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Collection;
import java.util.Objects;

@OnlyIn(Dist.CLIENT)
public class PlayerNetworkHandler {

    @SubscribeEvent
    public void onPlayerLogin(ClientPlayerNetworkEvent.LoggedInEvent event) {
        if (Objects.equals(event.getPlayer(), Minecraft.getInstance().player)) {
            SkinBakery.start();
        }
    }

    @SubscribeEvent
    public void onPlayerLogout(ClientPlayerNetworkEvent.LoggedOutEvent event) {
        if (Objects.equals(event.getPlayer(), Minecraft.getInstance().player)) {
            SkinBakery.stop();
            SkinLoader.getInstance().clear();
        }
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

    private static boolean shouldKeepWardrobe(PlayerEntity entity) {
        if (entity.isSpectator()) {
            return true;
        }
        return entity.level.getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY);
    }
}
