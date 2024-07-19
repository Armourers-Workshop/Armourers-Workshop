package moe.plushie.armourers_workshop.core.holiday;

import moe.plushie.armourers_workshop.api.data.IDataSerializer;
import moe.plushie.armourers_workshop.compatibility.core.AbstractSavedData;
import moe.plushie.armourers_workshop.init.ModLog;
import moe.plushie.armourers_workshop.utils.Constants;
import moe.plushie.armourers_workshop.utils.DataSerializerKey;
import moe.plushie.armourers_workshop.utils.DataTypeCodecs;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class HolidayTracker extends AbstractSavedData {

    private static final DataSerializerKey<List<String>> LOG_KEY = DataSerializerKey.create("Logs", DataTypeCodecs.STRING.listOf(), Collections.emptyList());

    private final Calendar calendar = Calendar.getInstance();
    private final HashSet<String> logs = new HashSet<>();

    public HolidayTracker() {
    }

    public static HolidayTracker of(MinecraftServer server) {
        return server.overworld().getDataStorage().computeIfAbsent(HolidayTracker::new, 0, Constants.Key.HOLIDAY_TRACKER);
    }

    public void add(Player player, Holiday holiday) {
        ModLog.info("give a {} gift sack for the {}", holiday.getName(), player.getScoreboardName());
        logs.add(getKey(player, holiday));
        setDirty();
    }

    public void remove(Player player, Holiday holiday) {
        ModLog.info("take a {} gift sack for the {}", holiday.getName(), player.getScoreboardName());
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
        for (var log : serializer.read(LOG_KEY)) {
            // ignore more than 1 year ago the logs
            if (log.startsWith(prefix)) {
                logs.add(log);
            }
        }
    }

    @Override
    public void serialize(IDataSerializer serializer) {
        var prefix = calendar.get(Calendar.YEAR) + ":";
        var logs = new ArrayList<String>();
        for (var log : logs) {
            // ignore more than 1 year ago the logs
            if (log.startsWith(prefix)) {
                logs.add(log);
            }
        }
        serializer.write(LOG_KEY, logs);
    }

    private String getKey(Player player, Holiday holiday) {
        // 2020:new-years:889bebd9-9ebc-4dec-97ee-de9907cbbc85
        return calendar.get(Calendar.YEAR) + ":" + holiday.getName() + ":" + player.getStringUUID();
    }
}
