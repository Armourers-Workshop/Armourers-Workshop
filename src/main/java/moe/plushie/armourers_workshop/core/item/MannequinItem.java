package moe.plushie.armourers_workshop.core.item;

import moe.plushie.armourers_workshop.core.AWConstants;
import moe.plushie.armourers_workshop.core.texture.PlayerTextureDescriptor;
import moe.plushie.armourers_workshop.core.utils.TranslateUtils;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.List;

@SuppressWarnings("NullableProblems")
public class MannequinItem extends Item {

    public MannequinItem(Item.Properties properties) {
        super(properties);
    }

    public static void setScale(ItemStack itemStack, float scale) {
        if (itemStack.isEmpty()) {
            return;
        }
        CompoundNBT nbt = itemStack.getOrCreateTag();
        nbt.putFloat(AWConstants.NBT.MANNEQUIN_SCALE, scale);
    }

    public static float getScale(ItemStack itemStack) {
        CompoundNBT nbt = itemStack.getTag();
        if (nbt == null || nbt.isEmpty() || !nbt.contains(AWConstants.NBT.MANNEQUIN_SCALE, Constants.NBT.TAG_FLOAT)) {
            return 1.0f;
        }
        return nbt.getFloat(AWConstants.NBT.MANNEQUIN_SCALE);
    }

    public static String getNameKey(ItemStack itemStack) {
        float scale = getScale(itemStack);
        if (scale <= 0.5f) {
            return "item.armourers_workshop.mannequin.small";
        }
        if (scale >= 2.0f) {
            return "item.armourers_workshop.mannequin.big";
        }
        return "item.armourers_workshop.mannequin";
    }

    @Override
    public ITextComponent getName(ItemStack itemStack) {
        return TranslateUtils.title(getNameKey(itemStack));
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable World world, List<ITextComponent> tooltips, ITooltipFlag flag) {
        PlayerTextureDescriptor descriptor = PlayerTextureDescriptor.of(itemStack);
        if (descriptor.getName() != null) {
            tooltips.add(TranslateUtils.subtitle("item.armourers_workshop.rollover.user", descriptor.getName()));
        }
        if (descriptor.getURL() != null) {
            tooltips.add(TranslateUtils.subtitle("item.armourers_workshop.rollover.url", descriptor.getURL()));
        }
        tooltips.add(TranslateUtils.subtitle(getNameKey(itemStack) + ".flavour"));
    }
}
