package riskyken.armourersWorkshop.common.wardrobe;

import java.util.BitSet;
import java.util.HashMap;

import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants.NBT;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinType;
import riskyken.armourersWorkshop.common.config.ConfigHandler;
import riskyken.armourersWorkshop.common.skin.type.SkinTypeRegistry;
import riskyken.armourersWorkshop.common.wardrobe.ExtraColours.ExtraColourType;

public class EquipmentWardrobeData {

    private static final String TAG_EXTRA_COLOUR = "extra-colour-";
    private static final String TAG_ARMOUR_OVERRIDE = "armourOverride";
    private static final String TAG_SLOTS_UNLOCKED = "slotsUnlocked";
    private static final String TAG_SLOT_KEY = "slotKey";
    private static final String TAG_SLOT_VALUE = "slotValue";

    private ExtraColours extraColours;
    /** Bit set of what armour is hidden on the player. */
    public BitSet armourOverride;
    /** Number of slots the player has unlocked in the wardrobe */
    public HashMap<String, Integer> slotsUnlocked;

    public EquipmentWardrobeData() {
        extraColours = new ExtraColours();
        armourOverride = new BitSet(4);
        slotsUnlocked = new HashMap<String, Integer>();
        ISkinType[] validSkins = ExPropsPlayerSkinData.validSkins;
        for (int i = 0; i < validSkins.length; i++) {
            ISkinType skinType = validSkins[i];
            slotsUnlocked.put(skinType.getRegistryName(), getUnlockedSlotsForSkinType(skinType));
        }
    }

    public int getUnlockedSlotsForSkinType(ISkinType skinType) {
        if (skinType == SkinTypeRegistry.skinBow) {
            return 1;
        }
        if (skinType == SkinTypeRegistry.skinSword) {
            return 5;
        }
        if (slotsUnlocked.containsKey(skinType.getRegistryName())) {
            return slotsUnlocked.get(skinType.getRegistryName());
        } else {
            return ConfigHandler.startingWardrobeSlots;
        }
    }

    public void setUnlockedSlotsForSkinType(ISkinType skinType, int value) {
        slotsUnlocked.put(skinType.getRegistryName(), value);
    }

    public EquipmentWardrobeData(EquipmentWardrobeData ewd) {
        extraColours = new ExtraColours(ewd.getExtraColours());
        armourOverride = (BitSet) ewd.armourOverride.clone();
        slotsUnlocked = ewd.slotsUnlocked;
    }

    public ExtraColours getExtraColours() {
        return extraColours;
    }

    public void saveNBTData(NBTTagCompound compound) {
        for (int i = 0; i < ExtraColourType.values().length; i++) {
            ExtraColourType type = ExtraColourType.values()[i];
            compound.setInteger(TAG_EXTRA_COLOUR + type.toString().toLowerCase(), extraColours.getColour(type));
        }
        for (int i = 0; i < 4; i++) {
            compound.setBoolean(TAG_ARMOUR_OVERRIDE + i, this.armourOverride.get(i));
        }

        NBTTagList slotsList = new NBTTagList();
        ISkinType[] validSkins = ExPropsPlayerSkinData.validSkins;
        for (int i = 0; i < validSkins.length; i++) {
            ISkinType skinType = validSkins[i];
            NBTTagCompound slotCount = new NBTTagCompound();
            slotCount.setString(TAG_SLOT_KEY, skinType.getRegistryName());
            slotCount.setInteger(TAG_SLOT_VALUE, getUnlockedSlotsForSkinType(skinType));
            slotsList.appendTag(slotCount);
        }
        compound.setTag(TAG_SLOTS_UNLOCKED, slotsList);
    }

    public void loadNBTData(NBTTagCompound compound) {
        for (int i = 0; i < ExtraColourType.values().length; i++) {
            ExtraColourType type = ExtraColourType.values()[i];
            if (compound.hasKey(TAG_EXTRA_COLOUR + type.toString().toLowerCase(), NBT.TAG_INT)) {
                extraColours.setColour(type, compound.getInteger(TAG_EXTRA_COLOUR + type.toString().toLowerCase()));
            }
        }
        for (int i = 0; i < 4; i++) {
            this.armourOverride.set(i, compound.getBoolean(TAG_ARMOUR_OVERRIDE + i));
        }
        if (compound.hasKey(TAG_SLOTS_UNLOCKED, NBT.TAG_LIST)) {
            NBTTagList slotsList = compound.getTagList(TAG_SLOTS_UNLOCKED, NBT.TAG_COMPOUND);
            this.slotsUnlocked.clear();
            for (int i = 0; i < slotsList.tagCount(); i++) {
                NBTTagCompound slotCount = slotsList.getCompoundTagAt(i);
                if (slotCount.hasKey(TAG_SLOT_KEY, NBT.TAG_STRING) & slotCount.hasKey(TAG_SLOT_VALUE, NBT.TAG_INT)) {
                    String key = slotCount.getString(TAG_SLOT_KEY);
                    int value = slotCount.getInteger(TAG_SLOT_VALUE);
                    slotsUnlocked.put(key, value);
                }
            }
        }
    }

    public void fromBytes(ByteBuf buf) {
        NBTTagCompound compound = ByteBufUtils.readTag(buf);
        loadNBTData(compound);
    }

    public void toBytes(ByteBuf buf) {
        NBTTagCompound compound = new NBTTagCompound();
        saveNBTData(compound);
        ByteBufUtils.writeTag(buf, compound);
    }
}
