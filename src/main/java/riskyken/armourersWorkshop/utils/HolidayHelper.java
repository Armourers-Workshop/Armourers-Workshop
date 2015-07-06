package riskyken.armourersWorkshop.utils;

import java.util.ArrayList;
import java.util.Calendar;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.StatCollector;
import riskyken.armourersWorkshop.common.items.ModItems;
import riskyken.armourersWorkshop.common.skin.ExPropsPlayerEquipmentData;

public final class HolidayHelper {
    
    private static ArrayList<Holiday> holidayList;
    
    public static final Holiday halloween = new Holiday("halloween", 31, Calendar.OCTOBER, 24);
    public static final Holiday christmas = new Holiday("christmas", 25, Calendar.DECEMBER, 24);
    public static final Holiday valentins = new Holiday("valentins", 14, Calendar.FEBRUARY, 24);
    public static final Holiday newYears = new Holiday("new_years", 1, Calendar.JANUARY, 24);
    public static final Holiday ponytailDay = new Holiday("ponytail_day", 7, Calendar.JULY, 24);
    public static final Holiday btm15Anniversary = new Holiday("better_than_minecon_anniversary", 4, Calendar.JULY, 48);
    //Should be 12 but making it 24 so more people can see it.
    public static final Holiday aprilFools = new Holiday("april_fools", 1, Calendar.APRIL, 24);
    public static final Holiday riskysbday = new Holiday("risky_kens_birthday", 6, Calendar.FEBRUARY, 24);
    
    static {
        holidayList = new ArrayList<HolidayHelper.Holiday>();
        holidayList.add(halloween);
        holidayList.add(christmas);
        holidayList.add(valentins);
        holidayList.add(newYears);
        holidayList.add(ponytailDay);
        holidayList.add(btm15Anniversary);
        holidayList.add(btm15Anniversary);
        holidayList.add(riskysbday);
    }
    
    public static ArrayList<Holiday> getActiveHolidays() {
        ArrayList<Holiday> activeList = new ArrayList<HolidayHelper.Holiday>();
        for (int i = 0; i < holidayList.size(); i++) {
            if (holidayList.get(i).isHolidayActive()) {
                activeList.add(holidayList.get(i));
            }
        }
        return activeList;
    }
    
    public static int getYear() {
        return Calendar.getInstance().get(Calendar.YEAR);
    }
    
    public static void giftPlayer(EntityPlayerMP player) {
        if (christmas.isHolidayActive()) {
            ExPropsPlayerEquipmentData playerData = ExPropsPlayerEquipmentData.get(player);
            if (playerData.lastXmasYear < getYear()) {
                ItemStack giftSack = new ItemStack(ModItems.equipmentSkinTemplate, 1, 1000);
                if (!player.inventory.addItemStackToInventory(giftSack)) {
                    player.addChatComponentMessage(new ChatComponentText(StatCollector.translateToLocal("chat.armourersworkshop:inventoryGiftFail")));
                } else {
                    playerData.lastXmasYear = getYear();
                }
            }
        }
    }
    
    public static class Holiday {
        
        private final String name;
        private final Calendar startDate;
        private final Calendar endDate;
        
        /**
         * Creates and new holiday that spans x number of hours.
         * @param name Name of the holiday.
         * @param dayOfMonth The day of the month this holiday takes place on.
         * @param month Month this holiday takes place on. Zero based 0 = Jan, 11 = Dec
         * @param lengthInHours Number of hours this holiday lasts.
         */
        public Holiday(String name, int dayOfMonth, int month, int lengthInHours) {
            this.name = name;
            startDate = Calendar.getInstance();
            startDate.set(Calendar.MINUTE, 0);
            startDate.set(Calendar.HOUR_OF_DAY, 0);
            startDate.set(Calendar.MONTH, month);
            startDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            endDate = (Calendar) startDate.clone();
            endDate.set(Calendar.HOUR_OF_DAY, lengthInHours);
        }
        
        public boolean isHolidayActive() {
            Calendar current = Calendar.getInstance();
            if (current.after(startDate) & current.before(endDate)) {
                return true;
            }
            return false;
        }

        @Override
        public String toString() {
            return "Holiday [name=" + name + ", startDate=" + startDate + ", endDate=" + endDate + "]";
        }
    }
}
