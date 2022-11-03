package moe.plushie.armourers_workshop.api.config;

import java.util.Map;

public interface IConfigSpec {

    Map<String, Object> snapshot();

    void apply(Map<String, Object> snapshot);

    void reload();

    void save();

    void notify(Runnable action);
}
