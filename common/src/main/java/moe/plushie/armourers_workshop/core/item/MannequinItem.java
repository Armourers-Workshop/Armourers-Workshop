package moe.plushie.armourers_workshop.core.item;

import moe.plushie.armourers_workshop.api.common.ITooltipContext;
import moe.plushie.armourers_workshop.api.common.IUseOnContext;
import moe.plushie.armourers_workshop.core.data.MannequinHitResult;
import moe.plushie.armourers_workshop.core.entity.MannequinEntity;
import moe.plushie.armourers_workshop.core.math.OpenVector3d;
import moe.plushie.armourers_workshop.core.math.OpenVector3f;
import moe.plushie.armourers_workshop.core.skin.texture.PlayerSkinDescriptor;
import moe.plushie.armourers_workshop.core.utils.OpenEntitySpawnReason;
import moe.plushie.armourers_workshop.core.utils.OpenInteractionHand;
import moe.plushie.armourers_workshop.core.utils.OpenInteractionResult;
import moe.plushie.armourers_workshop.core.utils.Strings;
import moe.plushie.armourers_workshop.core.utils.TranslateUtils;
import moe.plushie.armourers_workshop.init.ModDataComponents;
import moe.plushie.armourers_workshop.init.ModEntityTypes;
import moe.plushie.armourers_workshop.init.environment.EnvironmentExecutor;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;

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
    protected OpenInteractionResult abi$useOn(IUseOnContext context) {
        if (context.hand() != OpenInteractionHand.MAIN_HAND) {
            return OpenInteractionResult.FAIL;
        }
        var player = context.player();
        if (player == null) {
            return OpenInteractionResult.FAIL;
        }
        var level = context.level();
        var origin = new OpenVector3d(player.getX(), player.getY(), player.getZ());
        var rayTraceResult = MannequinHitResult.test(player, origin, context.clickLocation(), context.clickedPos());
        var itemStack = context.itemInHand();
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
            var descriptor = PlayerSkinDescriptor.of(itemStack);
            if (Strings.isNotEmpty(descriptor.name())) {
                tooltips.add(TranslateUtils.subtitle("item.armourers_workshop.rollover.user", descriptor.name()));
            }
            if (Strings.isNotEmpty(descriptor.url())) {
                tooltips.add(TranslateUtils.subtitle("item.armourers_workshop.rollover.url", descriptor.url()));
            }
        });
    }

    @Override
    protected String abi$getDescriptionId(ItemStack itemStack) {
        var scale = getScale(itemStack);
        if (scale <= 0.5f) {
            return super.abi$getDescriptionId(itemStack) + ".small";
        }
        if (scale >= 2.0f) {
            return super.abi$getDescriptionId(itemStack) + ".big";
        }
        return super.abi$getDescriptionId(itemStack);
    }
}
