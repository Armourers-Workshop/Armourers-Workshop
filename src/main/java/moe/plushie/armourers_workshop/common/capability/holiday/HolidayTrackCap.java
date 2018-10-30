package moe.plushie.armourers_workshop.common.capability.holiday;

import java.util.HashMap;
import java.util.concurrent.Callable;

import moe.plushie.armourers_workshop.common.holiday.Holiday;
import moe.plushie.armourers_workshop.common.holiday.ModHolidays;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.Constants.NBT;

public class HolidayTrackCap implements IHolidayTrackCap {

    @CapabilityInject(IHolidayTrackCap.class)
    public static final Capability<IHolidayTrackCap> HOLIDAY_TRACK_CAP = null;
    
    public final HashMap<Holiday, Integer> holidayYears;
    
    public HolidayTrackCap() {
        holidayYears = new HashMap<Holiday, Integer>();
    }
    
    @Override
    public int getLastHolidayYear(Holiday holiday) {
        if (holidayYears.containsKey(holiday)) {
            return holidayYears.get(holiday);
        }
        return 0;
    }

    @Override
    public void setLastHoloidayYear(Holiday holiday, int year) {
        holidayYears.put(holiday, year);
    }
    
    @Override
    public Holiday[] getHolidays() {
        return holidayYears.keySet().toArray(new Holiday[holidayYears.size()]);
    }
    
    @Override
    public void clearList() {
        holidayYears.clear();
    }
    
    public static class Provider implements ICapabilitySerializable<NBTTagCompound>  {

        private final IHolidayTrackCap holidayTrackCap;
        
        public Provider() {
            this.holidayTrackCap = HOLIDAY_TRACK_CAP.getDefaultInstance();
        }
        
        @Override
        public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
            return capability != null && capability == HOLIDAY_TRACK_CAP;
        }

        @Override
        public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
            if (hasCapability(capability, facing)) {
                return HOLIDAY_TRACK_CAP.cast(holidayTrackCap);
            }
            return null;
        }

        @Override
        public NBTTagCompound serializeNBT() {
            return (NBTTagCompound) HOLIDAY_TRACK_CAP.getStorage().writeNBT(HOLIDAY_TRACK_CAP, holidayTrackCap, null);
        }

        @Override
        public void deserializeNBT(NBTTagCompound nbt) {
            HOLIDAY_TRACK_CAP.getStorage().readNBT(HOLIDAY_TRACK_CAP, holidayTrackCap, null, nbt);
        }
    }

    public static class Storage implements IStorage<IHolidayTrackCap> {

        private static final String TAG_HOLIDAY_LIST = "holiday-list";
        
        @Override
        public NBTBase writeNBT(Capability<IHolidayTrackCap> capability, IHolidayTrackCap instance, EnumFacing side) {
            NBTTagCompound compound = new NBTTagCompound();
            NBTTagList list = new NBTTagList();
            for (Holiday holiday : instance.getHolidays()) {
                list.appendTag(new NBTTagString(holiday.getName() + ";" + instance.getLastHolidayYear(holiday))); 
            }
            compound.setTag(TAG_HOLIDAY_LIST, list);
            return compound;
        }

        @Override
        public void readNBT(Capability<IHolidayTrackCap> capability, IHolidayTrackCap instance, EnumFacing side, NBTBase nbt) {
            NBTTagCompound compound = (NBTTagCompound) nbt;
            if (compound.hasKey(TAG_HOLIDAY_LIST, NBT.TAG_LIST)) {
                NBTTagList list = compound.getTagList(TAG_HOLIDAY_LIST, NBT.TAG_STRING);
                for (int i = 0; i < list.tagCount(); i++) {
                    String key = list.getStringTagAt(i);
                    if (key.contains(";")) {
                        String[] split = key.split(";");
                        Holiday holiday = ModHolidays.getHoliday(split[0]);
                        int year = Integer.parseInt(split[1]);
                        instance.setLastHoloidayYear(holiday, year);
                    }
                }
            }
        }
    }
    
    public static class Factory implements Callable<IHolidayTrackCap> {

        @Override
        public IHolidayTrackCap call() throws Exception {
            return new HolidayTrackCap();
        }
    }

    public static IHolidayTrackCap get(EntityPlayer player) {
        return player.getCapability(HOLIDAY_TRACK_CAP, null);
    }
}
