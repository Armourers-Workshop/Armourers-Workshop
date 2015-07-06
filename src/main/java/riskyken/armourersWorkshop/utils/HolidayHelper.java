package riskyken.armourersWorkshop.utils;

import java.util.Calendar;
import java.util.Random;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.StatCollector;
import riskyken.armourersWorkshop.common.items.ModItems;
import riskyken.armourersWorkshop.common.skin.ExPropsPlayerEquipmentData;

public final class HolidayHelper {
    
    public enum EnumHoliday {
        NONE(0,0),
        HALLOWEEN(Calendar.OCTOBER, 31),
        CHRISTMAS(Calendar.DECEMBER, 25),
        VALENTINES(Calendar.FEBRUARY, 14),
        NEW_YEARS(Calendar.JANUARY, 1),
        PONYTAIL_DAY(Calendar.JULY, 7),
        BTM15_ANNIVERSARY(Calendar.JULY, 4);
        //TODO Lasts 2 days, update holiday system to deal with this.
        
        public final int month;
        public final int day;
        
        private EnumHoliday(int month, int day) {
            this.month = month;
            this.day = day;
        }
    }
    
    public static EnumHoliday getHoliday() {
        return getHoliday(3);
    }
    
    public static EnumHoliday getHoliday(int range) {
        Calendar current = Calendar.getInstance();
        
        for (int i = 1; i < EnumHoliday.values().length; i++) {
            EnumHoliday holiday = EnumHoliday.values()[i];
            Calendar holidayDate = Calendar.getInstance();

            holidayDate.set(Calendar.MONTH, holiday.month);
            holidayDate.set(Calendar.DAY_OF_MONTH, holiday.day);

            Calendar cLowerRange = (Calendar)holidayDate.clone();
            Calendar cUpperRange = (Calendar)holidayDate.clone();
            
            cLowerRange.add(Calendar.DAY_OF_MONTH, -range - 1);
            cUpperRange.add(Calendar.DAY_OF_MONTH, range + 1);
            
            if (current.after(cLowerRange) && current.before(cUpperRange)) {
                return holiday;
            }
        }
        
        return EnumHoliday.NONE;
    }
    
    public static EnumHoliday getBeforeHoliday(int range) {
        Calendar current = Calendar.getInstance();
        
        for (int i = 1; i < EnumHoliday.values().length; i++) {
            EnumHoliday holiday = EnumHoliday.values()[i];
            Calendar holidayDate = Calendar.getInstance();

            holidayDate.set(Calendar.MONTH, holiday.month);
            holidayDate.set(Calendar.DAY_OF_MONTH, holiday.day);

            Calendar cLowerRange = (Calendar)holidayDate.clone();
            Calendar cUpperRange = (Calendar)holidayDate.clone();
            
            cLowerRange.add(Calendar.DAY_OF_MONTH, -range - 1);
            cUpperRange.add(Calendar.DAY_OF_MONTH, + 1);
            
            if (current.after(cLowerRange) && current.before(cUpperRange)) {
                return holiday;
            }
        }
        
        return EnumHoliday.NONE;
    }
    
    public static int getYear() {
        return Calendar.getInstance().get(Calendar.YEAR);
    }
    
    public static void giftPlayer(EntityPlayerMP player) {
        if (getHoliday() == EnumHoliday.CHRISTMAS) {
            ExPropsPlayerEquipmentData playerData = ExPropsPlayerEquipmentData.get(player);
            if (playerData.lastXmasYear < getYear()) {
                Random rnd = new Random();
                ItemStack giftSack = new ItemStack(ModItems.equipmentSkinTemplate, 1, 1000);
                if (!player.inventory.addItemStackToInventory(giftSack)) {
                    player.addChatComponentMessage(new ChatComponentText(StatCollector.translateToLocal("chat.armourersworkshop:inventoryGiftFail")));
                } else {
                    playerData.lastXmasYear = getYear();
                }
            }
        }
    }
}
