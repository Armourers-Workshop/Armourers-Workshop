package moe.plushie.armourers_workshop.common.capability.holiday;

import moe.plushie.armourers_workshop.common.holiday.Holiday;

public interface IHolidayTrackCap {

    public int getLastHolidayYear(Holiday holiday);
    
    public void setLastHoloidayYear(Holiday holiday, int year);
    
    public Holiday[] getHolidays();
    
    public void clearList();
}
