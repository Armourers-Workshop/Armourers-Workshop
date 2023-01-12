package moe.plushie.armourers_workshop.core.item;

import moe.plushie.armourers_workshop.core.entity.MannequinEntity;
import moe.plushie.armourers_workshop.utils.Constants;
import moe.plushie.armourers_workshop.utils.TranslateUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MannequinToolItem extends FlavouredItem {

    public MannequinToolItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack itemStack, Player player, LivingEntity entity, InteractionHand hand) {
        if (entity instanceof MannequinEntity) {
            if (player.isShiftKeyDown()) {
                CompoundTag config = new CompoundTag();
                ItemStack newItemStack = itemStack.copy();
                ((MannequinEntity) entity).addExtendedData(config);
                newItemStack.addTagElement(Constants.Key.ENTITY, config);
                player.setItemInHand(hand, newItemStack);
                return InteractionResult.sidedSuccess(player.level.isClientSide());
            } else {
                CompoundTag tag = itemStack.getTag();
                if (tag != null && tag.contains(Constants.Key.ENTITY, Constants.TagFlags.COMPOUND)) {
                    CompoundTag config = tag.getCompound(Constants.Key.ENTITY);
                    ((MannequinEntity) entity).readExtendedData(config);
                    return InteractionResult.sidedSuccess(player.level.isClientSide());
                }
                return InteractionResult.FAIL;
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    public InteractionResult useOn(UseOnContext useOnContext) {
        // to air clear.
        return super.useOn(useOnContext);
    }

    @Override
    @Environment(value = EnvType.CLIENT)
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> tooltips, TooltipFlag flag) {
        super.appendHoverText(itemStack, level, tooltips, flag);
        CompoundTag tag = itemStack.getTag();
        if (tag != null && tag.contains(Constants.Key.ENTITY, Constants.TagFlags.COMPOUND)) {
            tooltips.add(TranslateUtils.subtitle("item.armourers_workshop.rollover.settingsSaved"));
        } else {
            tooltips.add(TranslateUtils.subtitle("item.armourers_workshop.rollover.noSettingsSaved"));
        }
    }
}
