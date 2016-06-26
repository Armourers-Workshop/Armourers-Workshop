package riskyken.armourersWorkshop.common.items;

import java.util.List;

import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import riskyken.armourersWorkshop.ArmourersWorkshop;
import riskyken.armourersWorkshop.common.lib.LibModInfo;
import riskyken.plushieWrapper.common.entity.PlushieEntityPlayer;
import riskyken.plushieWrapper.common.item.PlushieItem;
import riskyken.plushieWrapper.common.item.PlushieItemStack;

public class AbstractModItemNew extends PlushieItem {
    
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
    public void addInformation(PlushieItemStack stack, PlushieEntityPlayer player,
            List list, boolean advancedTooltips) {
        String unlocalized;
        String localized;

        unlocalized = stack.getUnlocalizedName() + ".flavour";
        localized = I18n.format(unlocalized);
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
