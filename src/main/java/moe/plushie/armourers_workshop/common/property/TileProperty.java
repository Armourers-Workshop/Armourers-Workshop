package moe.plushie.armourers_workshop.common.property;

public class TileProperty<TYPE> {
    
    private final IPropertyHolder owner;
    private final String key;
    private final TYPE defaultValue;
    private boolean sync;
    private TYPE value;
    
    public TileProperty(IPropertyHolder owner, String key, TYPE defaultValue) {
        this.owner = owner;
        this.key = key;
        this.defaultValue = defaultValue;
        sync = true;
        owner.registerProperty(this);
    }
    
    public TileProperty setSync(boolean sync) {
        this.sync = sync;
        return this;
    }
    
    public boolean isSync() {
        return sync;
    }
    
    public TYPE getDefault() {
        return defaultValue;
    }
    
    public String getKey() {
        return key;
    }
    
    public void set(TYPE value) {
        this.value = value;
        owner.onPropertyChanged(this);
    }
    
    public void loadType(Object value) {
        this.value = (TYPE) value;
    }
    
    public TYPE get() {
        if (value == null) {
            return defaultValue;
        }
        return value;
    }
}
