package moe.plushie.armourers_workshop.common;

import java.util.LinkedHashMap;

import org.apache.logging.log4j.Level;

import moe.plushie.armourers_workshop.common.lib.LibModInfo;
import moe.plushie.armourers_workshop.utils.ModLogger;
import net.minecraft.util.ResourceLocation;

public class ModRegistry<TYPE extends ModRegistry.IRegistryItem> {
    
    private LinkedHashMap<ResourceLocation, TYPE> registryMap = new LinkedHashMap<ResourceLocation, TYPE>();
    
    private final String name;
    
    public ModRegistry(String name) {
        this.name = name;
    }
    
    public boolean register(TYPE value) {
        if (value == null) {
            ModLogger.log(Level.WARN, "[" + name + "] A mod tried to register a null value.");
            return false;
        }
        if (value.getRegistryName() == null || value.getRegistryName().toString().trim().isEmpty()) {
            ModLogger.log(Level.WARN, "[" + name + "] A mod tried to register with an invalid registry key.");
            return false;
        }
        if (registryMap.containsKey(value.getRegistryName())) {
            ModLogger.log(Level.WARN, "[" + name + "] A mod tried to register with a key that is in use.");
            return false;
        }
        
        ModLogger.log(String.format("[" + name + "] Registering: %s as %s.", value.getName(), value.getRegistryName()));
        registryMap.put(value.getRegistryName(), value);
        return true;
    }
    
    public TYPE get(ResourceLocation key) {
        return registryMap.get(key);
    }
    
    public static class RegistryItem implements IRegistryItem {
        
        private final String name;

        public RegistryItem(String name) {
            this.name = name;
        }

        @Override
        public ResourceLocation getRegistryName() {
            return new ResourceLocation(LibModInfo.ID, name);
        }
        
        @Override
        public String getName() {
            return name;
        }
    }
    
    public static interface IRegistryItem {
        
        public ResourceLocation getRegistryName();
        
        public String getName();
    }
}
