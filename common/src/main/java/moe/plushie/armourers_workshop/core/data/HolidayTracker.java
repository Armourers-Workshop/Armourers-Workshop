package moe.plushie.armourers_workshop.core.data;

import moe.plushie.armourers_workshop.core.holiday.Holiday;
import moe.plushie.armourers_workshop.init.ModLog;
import moe.plushie.armourers_workshop.utils.Constants;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.Calendar;
import java.util.HashSet;

public class HolidayTracker extends SavedData {

    private final Calendar calendar = Calendar.getInstance();
    private final HashSet<String> logs = new HashSet<>();

    public HolidayTracker() {
        super(Constants.Key.HOLIDAY_TRACKER);
    }

    public static HolidayTracker of(MinecraftServer server) {
        return server.overworld().getDataStorage().computeIfAbsent(HolidayTracker::new, Constants.Key.HOLIDAY_TRACKER);
    }

    public void add(Player player, Holiday holiday) {
        ModLog.info("give a {} gift sack for the {}", holiday.getName(), player.getName().getContents());
        logs.add(getKey(player, holiday));
        setDirty();
    }

    public void remove(Player player, Holiday holiday) {
        ModLog.info("take a {} gift sack for the {}", holiday.getName(), player.getName().getContents());
        logs.remove(getKey(player, holiday));
        setDirty();
    }

    public boolean has(Player player, Holiday holiday) {
        return logs.contains(getKey(player, holiday));
    }

    @Override
    public void load(CompoundTag nbt) {
        logs.clear();
        String prefix = calendar.get(Calendar.YEAR) + ":";
        ListTag listNBT = nbt.getList(Constants.Key.HOLIDAY_LOGS, Constants.TagFlags.STRING);
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
    public CompoundTag save(CompoundTag nbt) {
        String prefix = calendar.get(Calendar.YEAR) + ":";
        ListTag listNBT = new ListTag();
        for (String log : logs) {
            // ignore more than 1 year ago the logs
            if (log.startsWith(prefix)) {
                listNBT.add(StringTag.valueOf(log));
            }
        }
        nbt.put(Constants.Key.HOLIDAY_LOGS, listNBT);
        return nbt;
    }

    private String getKey(Player player, Holiday holiday) {
        // 2020:new-years:889bebd9-9ebc-4dec-97ee-de9907cbbc85
        return calendar.get(Calendar.YEAR) + ":" + holiday.getName() + ":" + player.getStringUUID();
    }
}
