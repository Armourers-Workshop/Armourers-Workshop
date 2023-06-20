package moe.plushie.armourers_workshop.core.item;

import moe.plushie.armourers_workshop.api.common.IConfigurableToolProperty;
import moe.plushie.armourers_workshop.builder.item.option.PaintingToolOptions;
import moe.plushie.armourers_workshop.core.entity.MannequinEntity;
import moe.plushie.armourers_workshop.core.item.option.MannequinToolOptions;
import moe.plushie.armourers_workshop.core.texture.PlayerTextureDescriptor;
import moe.plushie.armourers_workshop.utils.Constants;
import moe.plushie.armourers_workshop.utils.DataSerializers;
import moe.plushie.armourers_workshop.utils.TranslateUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
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
    public InteractionResult interactLivingEntity(ItemStack itemStack, Player player, LivingEntity entity, InteractionHand hand) {
        if (entity instanceof MannequinEntity) {
            if (player.isShiftKeyDown()) {
                CompoundTag config = new CompoundTag();
                ItemStack newItemStack = itemStack.copy();
                ((MannequinEntity) entity).saveMannequinToolData(config);
                newItemStack.addTagElement(Constants.Key.ENTITY, config);
                player.setItemInHand(hand, newItemStack);
                return InteractionResult.sidedSuccess(player.getLevel().isClientSide());
            } else {
                CompoundTag entityTag = itemStack.getTagElement(Constants.Key.ENTITY);
                if (entityTag != null && !entityTag.isEmpty()) {
                    ((MannequinEntity) entity).readMannequinToolData(entityTag, itemStack);
                    return InteractionResult.sidedSuccess(player.getLevel().isClientSide());
                }
                return InteractionResult.FAIL;
            }
        }
        return InteractionResult.PASS;
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
    public void appendSettingHoverText(ItemStack itemStack, List<Component> tooltips) {
        CompoundTag tag = itemStack.getTag();
        if (tag != null && tag.contains(Constants.Key.ENTITY, Constants.TagFlags.COMPOUND)) {
            tooltips.add(TranslateUtils.subtitle("item.armourers_workshop.rollover.settingsSaved"));
        } else {
            tooltips.add(TranslateUtils.subtitle("item.armourers_workshop.rollover.noSettingsSaved"));
        }
        super.appendSettingHoverText(itemStack, tooltips);
    }
}
