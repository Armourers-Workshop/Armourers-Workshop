package moe.plushie.armourers_workshop.common.tileentities.property;

public interface IPropertyHolder {
    
    public void registerProperty(TileProperty<?> property);
    
    public void onPropertyChanged(TileProperty<?> property);
}
