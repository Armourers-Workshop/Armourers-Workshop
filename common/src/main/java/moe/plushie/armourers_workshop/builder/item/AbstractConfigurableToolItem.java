package moe.plushie.armourers_workshop.builder.item;

import moe.plushie.armourers_workshop.api.common.IItemParticleProvider;
import moe.plushie.armourers_workshop.api.common.IItemSoundProvider;
import moe.plushie.armourers_workshop.api.other.IRegistryObject;
import moe.plushie.armourers_workshop.api.painting.IPaintingTool;
import moe.plushie.armourers_workshop.api.painting.IPaintingToolProperty;
import moe.plushie.armourers_workshop.builder.client.gui.PaintingToolScreen;
import moe.plushie.armourers_workshop.core.item.FlavouredItem;
import moe.plushie.armourers_workshop.init.ModHolidays;
import moe.plushie.armourers_workshop.init.ModSounds;
import moe.plushie.armourers_workshop.utils.TranslateUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractConfigurableToolItem extends FlavouredItem implements IItemSoundProvider, IItemParticleProvider, IPaintingTool {

    public AbstractConfigurableToolItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (world.isClientSide() && player.isShiftKeyDown() && openContainer(world, player, hand, itemStack)) {
            return InteractionResultHolder.success(itemStack);
        }
        return super.use(world, player, hand);
    }

    @Override
    public void playSound(UseOnContext context) {
        IRegistryObject<SoundEvent> soundEvent = getItemSoundEvent(context);
        if (soundEvent == null) {
            return;
        }
        if (ModHolidays.APRIL_FOOLS.isHolidayActive()) {
            soundEvent = ModSounds.BOI;
        }
        float pitch = getItemSoundPitch(context);
        Level world = context.getLevel();
        BlockPos clickedPos = context.getClickedPos();
        if (world.isClientSide()) {
            world.playSound(context.getPlayer(), clickedPos, soundEvent.get(), SoundSource.BLOCKS, 1.0f, pitch);
        } else {
            world.playSound(null, clickedPos, soundEvent.get(), SoundSource.BLOCKS, 1.0f, pitch);
        }
    }

    @Override
    public void playParticle(UseOnContext context) {
    }

    public boolean openContainer(Level world, Player player, InteractionHand hand, ItemStack itemStack) {
        ArrayList<IPaintingToolProperty<?>> properties = new ArrayList<>();
        createToolProperties(properties::add);
        if (properties.isEmpty()) {
            return false;
        }
        openGUI(getName(itemStack), properties, hand, itemStack);
        return true;
    }

    public void appendColorHoverText(ItemStack itemStack, List<Component> tooltips) {
    }

    public void appendSettingHoverText(ItemStack itemStack, List<Component> tooltips) {
        tooltips.add(TranslateUtils.subtitle("item.armourers_workshop.rollover.openSettings"));
    }

    @Override
    @Environment(value = EnvType.CLIENT)
    public void appendHoverText(ItemStack itemStack, @Nullable Level world, List<Component> tooltips, TooltipFlag flags) {
        super.appendHoverText(itemStack, world, tooltips, flags);
        appendColorHoverText(itemStack, tooltips);
        appendSettingHoverText(itemStack, tooltips);
    }

    @Environment(value = EnvType.CLIENT)
    public void openGUI(Component title, ArrayList<IPaintingToolProperty<?>> properties, InteractionHand hand, ItemStack itemStack) {
        PaintingToolScreen screen = new PaintingToolScreen(title, properties, itemStack, hand);
        Minecraft.getInstance().setScreen(screen);
    }

    public float getItemSoundPitch(UseOnContext context) {
        return context.getLevel().getRandom().nextFloat() * 0.1F + 0.9F;
    }

    @Nullable
    public IRegistryObject<SoundEvent> getItemSoundEvent(UseOnContext context) {
        return null;
    }
}
