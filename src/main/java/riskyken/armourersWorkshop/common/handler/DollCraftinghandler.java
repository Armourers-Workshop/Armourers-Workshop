package riskyken.armourersWorkshop.common.handler;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.minecart.MinecartUpdateEvent;
import riskyken.armourersWorkshop.common.blocks.ModBlocks;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class DollCraftinghandler {

    public DollCraftinghandler() {
        MinecraftForge.EVENT_BUS.register(this);
    }
    
    @SubscribeEvent
    public void onMinecartUpdateEvent(MinecartUpdateEvent event) {
        World world = event.minecart.worldObj;

        List<Entity> entities;
        
        entities = world.getEntitiesWithinAABB(EntityItem.class, event.minecart.getCollisionBox(event.minecart));
        
        if (entities.size() == 0) {
            return;
        }
        
        for (int i = 0; i < entities.size(); i++) {
            Entity entity = entities.get(i);
            if (!(entity instanceof EntityItem)) {
                return; 
            }
            
            EntityItem item = (EntityItem) entity;
            ItemStack stack = item.getEntityItem();
            if (stack.getItem() == Item.getItemFromBlock(ModBlocks.mannequin)) {
                ItemStack dollStack = new ItemStack(ModBlocks.doll, stack.stackSize);
                dollStack.setTagCompound(stack.getTagCompound());
                EntityItem dollItem = new EntityItem(world, item.posX, item.posY, item.posZ, dollStack);
                world.spawnEntityInWorld(dollItem);
                item.setDead();
            }
        }
    }
}
