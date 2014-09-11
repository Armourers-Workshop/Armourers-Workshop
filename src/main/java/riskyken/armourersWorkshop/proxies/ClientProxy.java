package riskyken.armourersWorkshop.proxies;

import java.util.HashMap;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.common.MinecraftForge;

import org.lwjgl.opengl.GL11;

import riskyken.armourersWorkshop.client.ModClientFMLEventHandler;
import riskyken.armourersWorkshop.client.model.ModelCustomArmourChest;
import riskyken.armourersWorkshop.client.model.ModelCustomArmourFeet;
import riskyken.armourersWorkshop.client.model.ModelCustomArmourHead;
import riskyken.armourersWorkshop.client.model.ModelCustomArmourLegs;
import riskyken.armourersWorkshop.client.model.ModelCustomArmourSkirt;
import riskyken.armourersWorkshop.client.render.RenderBlockArmourer;
import riskyken.armourersWorkshop.client.render.RenderItemEquipmentSkin;
import riskyken.armourersWorkshop.client.settings.Keybindings;
import riskyken.armourersWorkshop.common.custom.equipment.armour.ArmourType;
import riskyken.armourersWorkshop.common.custom.equipment.data.CustomArmourItemData;
import riskyken.armourersWorkshop.common.items.ModItems;
import riskyken.armourersWorkshop.common.lib.LibCommonTags;
import riskyken.armourersWorkshop.common.tileentities.TileEntityArmourerBrain;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class ClientProxy extends CommonProxy {

    public static HashMap<String, CustomArmourItemData> customArmor = new HashMap<String, CustomArmourItemData>();
    //public static HashMap<Integer, ModelCustomItemBuilt> modelCache = new HashMap<Integer, ModelCustomItemBuilt>();
    
    public static ModelCustomArmourChest customChest = new ModelCustomArmourChest();
    public static ModelCustomArmourHead customHead = new ModelCustomArmourHead();
    public static ModelCustomArmourLegs customLegs = new ModelCustomArmourLegs();
    public static ModelCustomArmourSkirt customSkirt = new ModelCustomArmourSkirt();
    public static ModelCustomArmourFeet customFeet = new ModelCustomArmourFeet();
    
    public ClientProxy() {
        MinecraftForge.EVENT_BUS.register(this);
    }
    
    public static CustomArmourItemData getPlayerCustomArmour(Entity entity, ArmourType type) {
        if (!(entity instanceof AbstractClientPlayer)) { return null; }
        AbstractClientPlayer player = (AbstractClientPlayer) entity;
        String key = player.getDisplayName() + ":" + type.name();
        if (!customArmor.containsKey(key)) {
            return null;
        }

        CustomArmourItemData armorData = customArmor.get(key);
        if (armorData.getType() != type) { return null; }
        return armorData;
    }

    @Override
    public void init() {
    }

    @Override
    public void initRenderers() {
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityArmourerBrain.class, new RenderBlockArmourer());
        MinecraftForgeClient.registerItemRenderer(ModItems.equipmentSkin, new RenderItemEquipmentSkin());
    }

    @Override
    public void postInit() {
        FMLCommonHandler.instance().bus().register(new ModClientFMLEventHandler());
    }

    @Override
    public void registerKeyBindings() {
        ClientRegistry.registerKeyBinding(Keybindings.openCustomArmourGui);
    }
    
    @Override
    public void addCustomArmour(String playerName, CustomArmourItemData armourData) {
        String key = playerName + ":" + armourData.getType().name();
        if (customArmor.containsKey(key)) {
            customArmor.remove(key);
        }
        customArmor.put(key, armourData);
    }

    @Override
    public void removeCustomArmour(String playerName, ArmourType type) {
        String key = playerName + ":" + type.name();
        if (customArmor.containsKey(key)) {
            customArmor.remove(key);
        }
    }

    @Override
    public void removeAllCustomArmourData(String playerName) {
        removeCustomArmour(playerName, ArmourType.HEAD);
        removeCustomArmour(playerName, ArmourType.CHEST);
        removeCustomArmour(playerName, ArmourType.LEGS);
        removeCustomArmour(playerName, ArmourType.SKIRT);
        removeCustomArmour(playerName, ArmourType.FEET);
    }

    @Override
    public boolean playerHasSkirt(String playerName) {
        String key = playerName + ":" + ArmourType.SKIRT.name();
        return customArmor.containsKey(key);
    }
    
    @SubscribeEvent
    public void onRender(RenderPlayerEvent.Pre event) {
        EntityPlayer player = event.entityPlayer;
        if (playerHasSkirt(player.getDisplayName())) {
            if (player.limbSwingAmount > 0.25F) {
                player.limbSwingAmount = 0.25F;
            }
        }
    }
    
    @SubscribeEvent
    public void onRender(RenderPlayerEvent.Post event) {
    }
    
    @SubscribeEvent
    public void onRender(RenderPlayerEvent.SetArmorModel event) {
        EntityPlayer player = event.entityPlayer;
        RenderPlayer render = event.renderer;
        if (-event.slot + 3 == ArmourType.HEAD.getSlotId()) {
            CustomArmourItemData data = getPlayerCustomArmour(player, ArmourType.HEAD);
            if (data != null) {
                customHead.render(player, render, data);
                event.result = -2;
            }
        }
        if (-event.slot + 3 == ArmourType.CHEST.getSlotId()) {
            CustomArmourItemData data = getPlayerCustomArmour(player, ArmourType.CHEST);
            if (data != null) {
                customChest.render(player, render, data);
                event.result = -2;
            }
        }
        if (-event.slot + 3 == ArmourType.LEGS.getSlotId()) {
            CustomArmourItemData data = getPlayerCustomArmour(player, ArmourType.LEGS);
            if (data != null) {
                customLegs.render(player, render, data);
                event.result = -2;
            }
        }
        if (-event.slot + 3 == ArmourType.SKIRT.getSlotId()) {
            CustomArmourItemData data = getPlayerCustomArmour(player, ArmourType.SKIRT);
            if (data != null) {
                customSkirt.render(player, render, data);
                event.result = -2;
            }
        }
        if (-event.slot + 3 == ArmourType.FEET.getSlotId()) {
            CustomArmourItemData data = getPlayerCustomArmour(player, ArmourType.FEET);
            if (data != null) {
                customFeet.render(player, render, data);
                event.result = -2;
            }
        }
    }

    public static void renderItemAsArmourModel(ItemStack stack) {
        NBTTagCompound armourNBT = stack.getTagCompound().getCompoundTag(LibCommonTags.TAG_ARMOUR_DATA);
        CustomArmourItemData itemData = new CustomArmourItemData(armourNBT);
        switch (ArmourType.getOrdinal(stack.getItemDamage() + 1)) {
        case HEAD:
            GL11.glTranslatef(0F, 0.7F, 0F);
            customHead.render(null, null, itemData);
            break;
        case CHEST:
            GL11.glTranslatef(0F, -0.3F, 0F);
            customChest.render(null, null, itemData);
            break;
        case LEGS:
            GL11.glTranslatef(0F, -1.2F, 0F);
            customLegs.render(null, null, itemData);
            break;
        case SKIRT:
            GL11.glTranslatef(0F, -1.15F, 0F);
            customSkirt.render(null, null, itemData);
            break;
        case FEET:
            GL11.glTranslatef(0F, -1.2F, 0F);
            customFeet.render(null, null, itemData);
            break;
        default:
            break;
        }
        
    }
}