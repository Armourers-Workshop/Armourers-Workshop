package moe.plushie.armourers_workshop.common.skin.advanced.value;

import moe.plushie.armourers_workshop.common.ModRegistry;
import moe.plushie.armourers_workshop.common.lib.LibModInfo;
import moe.plushie.armourers_workshop.common.skin.advanced.value.SkinValueRegistry.SkinValue;
import moe.plushie.armourers_workshop.common.skin.data.Skin;
import moe.plushie.armourers_workshop.common.skin.data.SkinPart;
import moe.plushie.armourers_workshop.utils.ModLogger;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class SkinValueRegistry extends ModRegistry<SkinValue> {

    //    NONE,
    //    HEALTH,
    //    IN_WATER,
    //    SNEAKING,
    //    TIME,
    //    
    //    CLOSEST_MOB_RANGE,
    //    TARGET_RANGE,
    //    IN_COMBAT,
    //    LIGHT_LEVEL,
    //    FLYING,
    //    IN_RAIN,
    //    MOVEMENT_SPEED
    
    public static final SkinValueRegistry INSTANCE = new SkinValueRegistry();
    
    public static void init() {
        INSTANCE.register();
    }

    private SkinValueRegistry() {
        super("Skin Trigger Registry");
    }
    
    private void register() {
        ModLogger.log("Registering skin triggers.");
        register(new ValueHealth());
        register(new ValueInWater());
        register(new ValueSneaking());
        register(new ValueTime());
    }

    public static abstract class SkinValue implements ModRegistry.IRegistryItem, ISkinTrigger {

        private final String name;

        public SkinValue(String name) {
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
    
    public interface ISkinTrigger {
        
        public float getValue(World world, EntityLivingBase entityLivingBase, Skin skin, SkinPart skinPart);
    }
}
