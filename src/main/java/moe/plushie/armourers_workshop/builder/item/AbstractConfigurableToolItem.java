package moe.plushie.armourers_workshop.builder.item;

import moe.plushie.armourers_workshop.api.common.IItemParticleProvider;
import moe.plushie.armourers_workshop.api.common.IItemSoundProvider;
import moe.plushie.armourers_workshop.api.painting.IPaintingTool;
import moe.plushie.armourers_workshop.api.painting.IPaintingToolProperty;
import moe.plushie.armourers_workshop.builder.gui.PaintingToolScreen;
import moe.plushie.armourers_workshop.core.holiday.Holidays;
import moe.plushie.armourers_workshop.core.item.FlavouredItem;
import moe.plushie.armourers_workshop.init.common.ModSounds;
import moe.plushie.armourers_workshop.utils.TranslateUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("NullableProblems")
public abstract class AbstractConfigurableToolItem extends FlavouredItem implements IItemSoundProvider, IItemParticleProvider, IPaintingTool {

    public AbstractConfigurableToolItem(Properties properties) {
        super(properties);
    }

    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (world.isClientSide() && player.isShiftKeyDown() && openContainer(world, player, hand, itemStack)) {
            return ActionResult.success(itemStack);
        }
        return super.use(world, player, hand);
    }

    @Override
    public void playSound(ItemUseContext context) {
        SoundEvent soundEvent = getItemSoundEvent(context);
        if (soundEvent == null) {
            return;
        }
        if (Holidays.APRIL_FOOLS.isHolidayActive()) {
            soundEvent = ModSounds.BOI;
        }
        float pitch = getItemSoundPitch(context);
        IWorld world = context.getLevel();
        BlockPos clickedPos = context.getClickedPos();
        if (world.isClientSide()) {
            world.playSound(context.getPlayer(), clickedPos, soundEvent, SoundCategory.BLOCKS, 1.0f, pitch);
        } else {
            world.playSound(null, clickedPos, soundEvent, SoundCategory.BLOCKS, 1.0f, pitch);
        }
    }

    @Override
    public void playParticle(ItemUseContext context) {
    }

    public boolean openContainer(World world, PlayerEntity player, Hand hand, ItemStack itemStack) {
        ArrayList<IPaintingToolProperty<?>> properties = new ArrayList<>();
        createToolProperties(properties::add);
        if (properties.isEmpty()) {
            return false;
        }
        openGUI(getName(itemStack), properties, hand, itemStack);
        return true;
    }

    public void appendColorHoverText(ItemStack itemStack, List<ITextComponent> tooltips) {
    }

    public void appendSettingHoverText(ItemStack itemStack, List<ITextComponent> tooltips) {
        tooltips.add(TranslateUtils.subtitle("item.armourers_workshop.rollover.openSettings"));
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable World world, List<ITextComponent> tooltips, ITooltipFlag flags) {
        super.appendHoverText(itemStack, world, tooltips, flags);
        appendColorHoverText(itemStack, tooltips);
        appendSettingHoverText(itemStack, tooltips);
    }

    @OnlyIn(Dist.CLIENT)
    public void openGUI(ITextComponent title, ArrayList<IPaintingToolProperty<?>> properties, Hand hand, ItemStack itemStack) {
        PaintingToolScreen screen = new PaintingToolScreen(title, properties, itemStack, hand);
        Minecraft.getInstance().setScreen(screen);
    }

    public float getItemSoundPitch(ItemUseContext context) {
        return context.getLevel().getRandom().nextFloat() * 0.1F + 0.9F;
    }

    @Nullable
    public SoundEvent getItemSoundEvent(ItemUseContext context) {
        return null;
    }
}
