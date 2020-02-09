package moe.plushie.armourers_workshop.common.tileentities.property;

public class TileProperty<TYPE> {
    
    private final IPropertyHolder owner;
    private final String key;
    private final Class type;
    private boolean sync;
    private TYPE value;
    
    public TileProperty(IPropertyHolder owner, String key, Class type, TYPE defaultValue) {
        this.owner = owner;
        this.key = key;
        this.type = type;
        value = defaultValue;
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
    
    public Class getType() {
        return type;
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
        return value;
    }
}
