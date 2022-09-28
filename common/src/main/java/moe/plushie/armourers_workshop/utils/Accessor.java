package moe.plushie.armourers_workshop.utils;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;

public class Accessor {

    public static Abilities getAbilities(Player player) {
        //#if MC >= 11800
        return player.getAbilities();
        //#else
        //# return player.abilities;
        //#endif
    }
    
    public static Inventory getInventory(Player player) {
        //#if MC >= 11800
        return player.getInventory();
        //#else
        //# return player.inventory;
        //#endif
    }

    public static float getYRot(Entity entity) {
        //#if MC >= 11800
        return entity.getYRot();
        //#else
        //# return entity.yRot;
        //#endif
    }

    public static float getXRot(Entity entity) {
        //#if MC >= 11800
        return entity.getXRot();
        //#else
        //# return entity.xRot;
        //#endif
    }

}
