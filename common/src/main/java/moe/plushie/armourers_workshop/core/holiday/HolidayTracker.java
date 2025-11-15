package moe.plushie.armourers_workshop.core.holiday;

import moe.plushie.armourers_workshop.api.core.IDataCodec;
import moe.plushie.armourers_workshop.api.core.IDataSerializer;
import moe.plushie.armourers_workshop.api.core.IDataSerializerKey;
import moe.plushie.armourers_workshop.compat.core.data.AbstractSavedData;
import moe.plushie.armourers_workshop.compat.core.data.AbstractSavedDataType;
import moe.plushie.armourers_workshop.init.ModLog;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class HolidayTracker extends AbstractSavedData {

    public static final AbstractSavedDataType<HolidayTracker> TYPE = AbstractSavedDataType.create(HolidayTracker::new, "HolidayTracker");

    private final Calendar calendar = Calendar.getInstance();
    private final HashSet<String> logs = new HashSet<>();

    public HolidayTracker() {
    }

    public void add(Player player, Holiday holiday) {
        ModLog.info("give a {} gift sack for the {}", holiday.name(), player.getScoreboardName());
        logs.add(getKey(player, holiday));
        setDirty();
    }

    public void remove(Player player, Holiday holiday) {
        ModLog.info("take a {} gift sack for the {}", holiday.name(), player.getScoreboardName());
        logs.remove(getKey(player, holiday));
        setDirty();
    }

    public boolean has(Player player, Holiday holiday) {
        return logs.contains(getKey(player, holiday));
    }

    @Override
    public void deserialize(IDataSerializer serializer) {
        logs.clear();
        var prefix = calendar.get(Calendar.YEAR) + ":";
        for (var log : serializer.read(CodingKeys.LOG)) {
            // ignore more than 1 year ago the logs
            if (log.startsWith(prefix)) {
                logs.add(log);
            }
        }
    }

    @Override
    public void serialize(IDataSerializer serializer) {
        var prefix = calendar.get(Calendar.YEAR) + ":";
        var filteredLogs = new ArrayList<String>();
        for (var log : logs) {
            // ignore more than 1 year ago the logs
            if (log.startsWith(prefix)) {
                filteredLogs.add(log);
            }
        }
        serializer.write(CodingKeys.LOG, filteredLogs);
    }

    private String getKey(Player player, Holiday holiday) {
        // 2020:new-years:889bebd9-9ebc-4dec-97ee-de9907cbbc85
        return calendar.get(Calendar.YEAR) + ":" + holiday.name() + ":" + player.getStringUUID();
    }

    private static class CodingKeys {

        public static final IDataSerializerKey<List<String>> LOG = IDataSerializerKey.create("Logs", IDataCodec.STRING.listOf(), Collections.emptyList());
    }
}
