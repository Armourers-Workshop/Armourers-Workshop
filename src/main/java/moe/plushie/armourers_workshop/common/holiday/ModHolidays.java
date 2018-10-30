package moe.plushie.armourers_workshop.common.holiday;

import java.util.ArrayList;
import java.util.Calendar;

import moe.plushie.armourers_workshop.common.capability.holiday.HolidayTrackCap;
import moe.plushie.armourers_workshop.common.capability.holiday.IHolidayTrackCap;
import moe.plushie.armourers_workshop.common.lib.LibModInfo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;

@Mod.EventBusSubscriber(modid = LibModInfo.ID)
public final class ModHolidays {
    
    private static final ArrayList<Holiday> HOLIDAY_LIST = new ArrayList<Holiday>();;
    
    // Spooky scary skeletons.
    public static final Holiday HALLOWEEN = new Holiday("halloween", 31, Calendar.OCTOBER, 1, 0);
    public static final Holiday HALLOWEEN_SEASON = new HolidayHalloweenSeason("halloween-season", 24, Calendar.OCTOBER, 8, 0);
    
    // Some guy was born or something.
    public static final Holiday CHRISTMAS = new Holiday("christmas", 25, Calendar.DECEMBER, 0, 24);
    public static final Holiday CHRISTMAS_SEASON = new HolidayChristmasSeason("christmas-season", 1, Calendar.DECEMBER, 31, 0);
    
    // Forever alone.
    public static final Holiday VALENTINES = new HolidayValentines("valentines", 14, Calendar.FEBRUARY, 1, 0);
    
    // year++
    public static final Holiday NEW_YEARS = new Holiday("new-years", 1, Calendar.JANUARY, 1, 0);
    
    // The best holiday!
    public static final Holiday PONYTAIL_DAY = new Holiday("ponytail-day", 7, Calendar.JULY, 1, 0);
    
    // Should be 12 but making it 24 so more people can see it.
    public static final Holiday APRIL_FOOLS = new Holiday("april-fools", 1, Calendar.APRIL, 1, 0);
    
    public static ArrayList<Holiday> getHolidays() {
        return HOLIDAY_LIST;
    }
    
    public static Holiday getHoliday(String name) {
        for (Holiday holiday : HOLIDAY_LIST) {
            if (holiday.getName().equals(name)) {
                return holiday;
            }
        }
        return null;
    }
    
    public static ArrayList<Holiday> getActiveHolidays() {
        ArrayList<Holiday> activeList = new ArrayList<Holiday>();
        for (int i = 0; i < HOLIDAY_LIST.size(); i++) {
            if (HOLIDAY_LIST.get(i).isHolidayActive()) {
                activeList.add(HOLIDAY_LIST.get(i));
            }
        }
        return activeList;
    }
    
    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerLoggedInEvent event) {
        EntityPlayer player = event.player;
        IHolidayTrackCap holidayTrackCap = HolidayTrackCap.get(player);
        if (holidayTrackCap == null) {
            return;
        }
        for (Holiday holiday : getActiveHolidays()) {
            if (holiday.hasGiftSack()) {
                if (holidayTrackCap.getLastHolidayYear(holiday) < getYear()) {
                    ItemStack gift = holiday.getGiftSack();
                    if (!gift.isEmpty()) {
                        if (!player.inventory.addItemStackToInventory(gift)) {
                            player.sendMessage(new TextComponentTranslation("chat.armourersworkshop:inventoryGiftFail"));
                        } else {
                            holidayTrackCap.setLastHoloidayYear(holiday, getYear());
                        }
                    }
                }
            }
        }
    }
    
    public static int getYear() {
        return Calendar.getInstance().get(Calendar.YEAR);
    }
}
