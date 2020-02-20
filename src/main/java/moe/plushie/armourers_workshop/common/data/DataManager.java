package moe.plushie.armourers_workshop.common.data;

import java.util.HashMap;

public final class DataManager {

    private static DataManager dataManager;

    public static DataManager getInstance() {
        if (dataManager == null) {
            dataManager = new DataManager();
        }
        return dataManager;
    }

    private HashMap<Class, IDeserializer<?, ?>> deserializersMap = new HashMap<Class, IDeserializer<?, ?>>();
    private HashMap<Class, ISerializer<?, ?>> serializersMap = new HashMap<Class, ISerializer<?, ?>>();

    public DataManager() {

    }

    public static interface IDeserializer<DATA, STORAGE> {

        public DATA deserialize(STORAGE data);
    }

    public static interface ISerializer<DATA, STORAGE> {

        public STORAGE serialize(DATA instance);
    }
}
