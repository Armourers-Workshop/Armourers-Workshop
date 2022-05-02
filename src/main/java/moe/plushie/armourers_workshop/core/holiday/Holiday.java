package moe.plushie.armourers_workshop.core.holiday;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

import java.util.Calendar;
import java.util.Objects;

public class Holiday {
    
    protected final String name;
    protected final Calendar startDate;
    protected final Calendar endDate;

    protected IHandler handler;

    protected int tintColor1 = 0xff333333;
    protected int tintColor2 = 0xffffffff;

    protected boolean enabled = true;
    
    public Holiday(String name, Calendar startDate, Calendar endDate) {
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public String getName() {
        return name;
    }
    
    public Calendar getStartDate() {
        return startDate;
    }

    public Calendar getEndDate() {
        return endDate;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public IHandler getHandler() {
        return handler;
    }

    public void setHandler(IHandler handler) {
        this.handler = handler;
    }

    public boolean isEnabled() {
        return enabled;
    }
    
    public boolean isHolidayActive() {
        if (enabled) {
            Calendar current = Calendar.getInstance();
            return current.after(startDate) & current.before(endDate);
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Holiday holiday = (Holiday) o;
        return name.equals(holiday.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    public interface IHandler {

        ItemStack getGift(PlayerEntity player);

        default int getBackgroundColor() {
            return 0xffffff;
        }
        default int getForegroundColor() {
            return 0x333333;
        }
    }
}
