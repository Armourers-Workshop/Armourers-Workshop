package moe.plushie.armourers_workshop.core.item;

import moe.plushie.armourers_workshop.api.common.ITooltipContext;
import moe.plushie.armourers_workshop.core.data.MannequinHitResult;
import moe.plushie.armourers_workshop.core.entity.MannequinEntity;
import moe.plushie.armourers_workshop.core.math.OpenVector3f;
import moe.plushie.armourers_workshop.core.skin.texture.EntityTextureDescriptor;
import moe.plushie.armourers_workshop.core.utils.OpenEntitySpawnReason;
import moe.plushie.armourers_workshop.core.utils.OpenInteractionResult;
import moe.plushie.armourers_workshop.core.utils.TranslateUtils;
import moe.plushie.armourers_workshop.init.ModDataComponents;
import moe.plushie.armourers_workshop.init.ModEntityTypes;
import moe.plushie.armourers_workshop.init.environment.EnvironmentExecutor;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class MannequinItem extends FlavouredItem {

    public MannequinItem(Properties properties) {
        super(properties);
    }

    public static boolean isSmall(ItemStack itemStack) {
        var entityData = itemStack.get(ModDataComponents.ENTITY_DATA.get());
        if (entityData != null) {
            var data = new MannequinEntity.EntityData(entityData.tag());
            return data.isSmall();
        }
        return false;
    }

    public static float getScale(ItemStack itemStack) {
        var entityData = itemStack.get(ModDataComponents.ENTITY_DATA.get());
        if (entityData != null) {
            var data = new MannequinEntity.EntityData(entityData.tag());
            return data.scale();
        }
        return 1.0f;
    }

    @Override
    protected OpenInteractionResult abi$useOn(UseOnContext context) {
        if (context.getHand() != InteractionHand.MAIN_HAND) {
            return OpenInteractionResult.FAIL;
        }
        var player = context.getPlayer();
        if (player == null) {
            return OpenInteractionResult.FAIL;
        }
        var level = context.getLevel();
        var origin = new Vec3(player.getX(), player.getY(), player.getZ());
        var rayTraceResult = MannequinHitResult.test(player, origin, context.getClickLocation(), context.getClickedPos());
        var itemStack = context.getItemInHand();
        if (level instanceof ServerLevel serverLevel) {
            var entity = ModEntityTypes.MANNEQUIN.get().create(serverLevel, rayTraceResult.getBlockPos(), itemStack, OpenEntitySpawnReason.SPAWN_ITEM_USE);
            if (entity == null) {
                return OpenInteractionResult.FAIL;
            }
            var clickedLocation = rayTraceResult.getLocation();
            entity.setPositionAndRot(new OpenVector3f(clickedLocation.x(), clickedLocation.y(), clickedLocation.z()), 0.0f, 0.0f);
            entity.setYBodyRot(rayTraceResult.rotation());

            serverLevel.addFreshEntity(entity);
            serverLevel.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ARMOR_STAND_PLACE, SoundSource.BLOCKS, 0.75F, 0.8F);

            itemStack.shrink(1);
            return OpenInteractionResult.sidedSuccess(serverLevel.isClientSide());
        }
        return OpenInteractionResult.FAIL;
    }

    @Override
    protected void abi$appendHoverText(ItemStack itemStack, List<Component> tooltips, ITooltipContext context) {
        super.abi$appendHoverText(itemStack, tooltips, context);
        // only call on the client.
        EnvironmentExecutor.runOnClient(() -> () -> {
            var descriptor = EntityTextureDescriptor.of(itemStack);
            if (descriptor.name() != null) {
                tooltips.add(TranslateUtils.subtitle("item.armourers_workshop.rollover.user", descriptor.name()));
            }
            if (descriptor.url() != null) {
                tooltips.add(TranslateUtils.subtitle("item.armourers_workshop.rollover.url", descriptor.url()));
            }
        });
    }

    @Override
    protected Component abi$getName(ItemStack itemStack) {
        var scale = getScale(itemStack);
        if (scale <= 0.5f) {
            return TranslateUtils.title(getDescriptionId() + ".small");
        }
        if (scale >= 2.0f) {
            return TranslateUtils.title(getDescriptionId() + ".big");
        }
        return super.abi$getName(itemStack);
    }
}
