package moe.plushie.armourers_workshop.core.data;

import moe.plushie.armourers_workshop.core.holiday.Holiday;
import moe.plushie.armourers_workshop.init.common.AWConstants;
import moe.plushie.armourers_workshop.init.common.ModLog;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.storage.DimensionSavedDataManager;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;

import java.util.Calendar;
import java.util.HashSet;

@SuppressWarnings("NullableProblems")
public class HolidayTracker extends WorldSavedData {

    private final Calendar calendar = Calendar.getInstance();
    private final HashSet<String> logs = new HashSet<>();

    public HolidayTracker() {
        super(AWConstants.NBT.HOLIDAY_TRACKER);
    }

    public static HolidayTracker of(MinecraftServer server) {
        DimensionSavedDataManager dataStorage = server.overworld().getDataStorage();
        return dataStorage.computeIfAbsent(HolidayTracker::new, AWConstants.NBT.HOLIDAY_TRACKER);
    }

    public void add(PlayerEntity player, Holiday holiday) {
        ModLog.info("give a {} gift sack for the {}", holiday.getName(), player.getName().getContents());
        logs.add(getKey(player, holiday));
        setDirty();
    }

    public void remove(PlayerEntity player, Holiday holiday) {
        ModLog.info("take a {} gift sack for the {}", holiday.getName(), player.getName().getContents());
        logs.remove(getKey(player, holiday));
        setDirty();
    }

    public boolean has(PlayerEntity player, Holiday holiday) {
        return logs.contains(getKey(player, holiday));
    }

    @Override
    public void load(CompoundNBT nbt) {
        logs.clear();
        String prefix = calendar.get(Calendar.YEAR) + ":";
        ListNBT listNBT = nbt.getList(AWConstants.NBT.HOLIDAY_LOGS, Constants.NBT.TAG_STRING);
        int size = listNBT.size();
        for (int i = 0; i < size; ++i) {
            String log = listNBT.getString(i);
            // ignore more than 1 year ago the logs
            if (log.startsWith(prefix)) {
                logs.add(log);
            }
        }
    }

    @Override
    public CompoundNBT save(CompoundNBT nbt) {
        String prefix = calendar.get(Calendar.YEAR) + ":";
        ListNBT listNBT = new ListNBT();
        for (String log : logs) {
            // ignore more than 1 year ago the logs
            if (log.startsWith(prefix)) {
                listNBT.add(StringNBT.valueOf(log));
            }
        }
        nbt.put(AWConstants.NBT.HOLIDAY_LOGS, listNBT);
        return nbt;
    }

    private String getKey(PlayerEntity player, Holiday holiday) {
        // 2020:new-years:889bebd9-9ebc-4dec-97ee-de9907cbbc85
        return calendar.get(Calendar.YEAR) + ":" + holiday.getName() + ":" + player.getStringUUID();
    }
}
