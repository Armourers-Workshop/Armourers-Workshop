package moe.plushie.armourers_workshop.common.holiday;

import moe.plushie.armourers_workshop.common.init.blocks.ModBlocks;
import moe.plushie.armourers_workshop.common.init.items.ItemGiftSack;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;

public class HolidayChristmasSeason extends Holiday {

    public HolidayChristmasSeason(String name, int dayOfMonth, int month, int lengthInDays, int lengthInHours) {
        super(name, dayOfMonth, month, lengthInDays, lengthInHours);
    }
    
    @Override
    public ItemStack getGiftSack() {
        return ItemGiftSack.createStack(0x990000, 0x267F00, this);
    }
    
    @Override
    public ItemStack getGift(EntityPlayer player) {
        ItemStack gift = new ItemStack(ModBlocks.doll);
        if (player != null) {
            gift.setTagCompound(new NBTTagCompound());
            NBTTagCompound owner = new NBTTagCompound();
            NBTUtil.writeGameProfile(owner, player.getGameProfile());
            gift.getTagCompound().setTag("owner", owner);
        }
        return gift;
    }
}
