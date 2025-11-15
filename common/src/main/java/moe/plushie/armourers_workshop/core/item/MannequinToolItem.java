package moe.plushie.armourers_workshop.core.item;

import moe.plushie.armourers_workshop.api.common.IConfigurableToolProperty;
import moe.plushie.armourers_workshop.core.data.TypedEntityData;
import moe.plushie.armourers_workshop.core.entity.MannequinEntity;
import moe.plushie.armourers_workshop.core.item.option.MannequinToolOptions;
import moe.plushie.armourers_workshop.core.utils.OpenInteractionHand;
import moe.plushie.armourers_workshop.core.utils.OpenInteractionResult;
import moe.plushie.armourers_workshop.core.utils.TranslateUtils;
import moe.plushie.armourers_workshop.init.ModDataComponents;
import moe.plushie.armourers_workshop.init.ModEntityTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.function.Consumer;

public class MannequinToolItem extends ConfigurableToolItem {

    public MannequinToolItem(Properties properties) {
        super(properties);
    }

    @Override
    protected OpenInteractionResult abi$interactLivingEntity(ItemStack itemStack, Player player, LivingEntity entity, OpenInteractionHand hand) {
        if (entity instanceof MannequinEntity mannequinEntity) {
            if (player.isSecondaryUseActive()) {
                var config = new CompoundTag();
                var newItemStack = itemStack.copy();
                mannequinEntity.saveMannequinToolData(config);
                newItemStack.set(ModDataComponents.ENTITY_DATA.get(), TypedEntityData.of(ModEntityTypes.MANNEQUIN.get(), config));
                player.setItemInHand(hand, newItemStack);
                return OpenInteractionResult.sidedSuccess(player.level().isClientSide());
            } else {
                var entityData = itemStack.get(ModDataComponents.ENTITY_DATA.get());
                if (entityData != null && !entityData.isEmpty()) {
                    mannequinEntity.readMannequinToolData(entityData.tag(), itemStack);
                    return OpenInteractionResult.sidedSuccess(player.level().isClientSide());
                }
                return OpenInteractionResult.FAIL;
            }
        }
        return OpenInteractionResult.PASS;
    }

    @Override
    public void createToolProperties(Consumer<IConfigurableToolProperty<?>> builder) {
        builder.accept(MannequinToolOptions.MIRROR_MODE);
        builder.accept(MannequinToolOptions.CHANGE_SCALE);
        builder.accept(MannequinToolOptions.CHANGE_ROTATION);
        builder.accept(MannequinToolOptions.CHANGE_TEXTURE);
        builder.accept(MannequinToolOptions.CHANGE_OPTION);
    }

    @Override
    protected void appendSettingHoverText(ItemStack itemStack, List<Component> tooltips) {
        if (itemStack.has(ModDataComponents.ENTITY_DATA.get())) {
            tooltips.add(TranslateUtils.subtitle("item.armourers_workshop.rollover.settingsSaved"));
        } else {
            tooltips.add(TranslateUtils.subtitle("item.armourers_workshop.rollover.noSettingsSaved"));
        }
        super.appendSettingHoverText(itemStack, tooltips);
    }
}
