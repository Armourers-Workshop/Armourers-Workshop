package moe.plushie.armourers_workshop.core.client.other;

import moe.plushie.armourers_workshop.api.data.IAssociatedContainerKey;
import moe.plushie.armourers_workshop.api.data.IAssociatedContainerProvider;
import moe.plushie.armourers_workshop.core.data.DataContainer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class SkinLuminanceManager<T> implements IAssociatedContainerProvider {

    private final DataContainer dataStorage = new DataContainer();

    private int luminance = 0;
    private boolean isEnabled = false;
    private boolean isWaterSensitive = false;

    public void update(int brightness) {
        this.luminance = brightness;
        this.isEnabled = brightness > 0;
    }

    public int getLuminance(T source) {
        return luminance;
    }

    public boolean isWaterSensitive(T source) {
        return isWaterSensitive;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    @Override
    public <V> V getAssociatedObject(IAssociatedContainerKey<V> key) {
        return dataStorage.getAssociatedObject(key);
    }

    @Override
    public <V> void setAssociatedObject(IAssociatedContainerKey<V> key, V value) {
        dataStorage.setAssociatedObject(key, value);
    }
}
