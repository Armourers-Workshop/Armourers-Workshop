package moe.plushie.armourers_workshop.core.item;

import moe.plushie.armourers_workshop.core.entity.MannequinEntity;
import moe.plushie.armourers_workshop.core.texture.PlayerTextureDescriptor;
import moe.plushie.armourers_workshop.core.utils.MannequinRayTraceResult;
import moe.plushie.armourers_workshop.core.utils.TranslateUtils;
import moe.plushie.armourers_workshop.init.common.AWConstants;
import moe.plushie.armourers_workshop.init.common.ModEntities;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.List;

@SuppressWarnings("NullableProblems")
public class MannequinToolItem extends FlavouredItem {

    public MannequinToolItem(Properties properties) {
        super(properties);
    }


    @Override
    public ActionResultType interactLivingEntity(ItemStack itemStack, PlayerEntity player, LivingEntity entity, Hand hand) {
        if (entity instanceof MannequinEntity) {
            if (player.isShiftKeyDown()) {
                CompoundNBT config = new CompoundNBT();
                ((MannequinEntity) entity).addExtendedData(config);
                itemStack.addTagElement(AWConstants.NBT.ENTITY, config);
                player.setItemInHand(hand, itemStack);
                return ActionResultType.sidedSuccess(player.level.isClientSide);
            } else {
                CompoundNBT tag = itemStack.getTag();
                if (tag != null && tag.contains(AWConstants.NBT.ENTITY, Constants.NBT.TAG_COMPOUND)) {
                    CompoundNBT config = tag.getCompound(AWConstants.NBT.ENTITY);
                    ((MannequinEntity) entity).readExtendedData(config);
                    return ActionResultType.sidedSuccess(player.level.isClientSide);
                }
                return ActionResultType.FAIL;
            }
        }
        return ActionResultType.PASS;
    }

    @Override
    public boolean doesSneakBypassUse(ItemStack stack, IWorldReader world, BlockPos pos, PlayerEntity player) {
        return true;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable World world, List<ITextComponent> tooltips, ITooltipFlag flag) {
        super.appendHoverText(itemStack, world, tooltips, flag);
        CompoundNBT tag = itemStack.getTag();
        if (tag != null && tag.contains(AWConstants.NBT.ENTITY, Constants.NBT.TAG_COMPOUND)) {
            tooltips.add(TranslateUtils.subtitle("item.armourers_workshop.rollover.settingsSaved"));
        } else {
            tooltips.add(TranslateUtils.subtitle("item.armourers_workshop.rollover.noSettingsSaved"));
        }
    }
}
