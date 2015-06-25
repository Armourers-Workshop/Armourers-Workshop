package riskyken.armourersWorkshop.common.items;

import java.util.List;

import net.minecraft.util.StatCollector;
import riskyken.armourersWorkshop.ArmourersWorkshop;
import riskyken.armourersWorkshop.common.lib.LibModInfo;
import riskyken.minecraftWrapper.common.entity.EntityPlayerPointer;
import riskyken.minecraftWrapper.common.item.ItemStackPointer;
import riskyken.minecraftWrapper.common.item.ModItem;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class AbstractModItemNew extends ModItem {
    
    public AbstractModItemNew(String name) {
        this(name, true);
    }
    
    public AbstractModItemNew(String name, boolean addCreativeTab) {
        super(name, LibModInfo.ID);
        if (addCreativeTab) {
            setCreativeTab(ArmourersWorkshop.creativeTabArmorersWorkshop);
        }
        //setHasSubtypes(false);
        setMaxStackSize(1);
        //setNoRepair();
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStackPointer stack, EntityPlayerPointer player,
            List list, boolean advancedTooltips) {
        String unlocalized;
        String localized;

        unlocalized = stack.getUnlocalizedName() + ".flavour";
        localized = StatCollector.translateToLocal(unlocalized);
        if (!unlocalized.equals(localized)) {
            if (localized.contains("%n")) {
                String[] split = localized.split("%n");
                for (int i = 0; i < split.length; i++) {
                    list.add(split[i]);
                }
            } else {
                list.add(localized);
            }
        }
        super.addInformation(stack, player, list, advancedTooltips);
    }
}
