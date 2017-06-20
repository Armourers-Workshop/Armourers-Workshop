package riskyken.armourersWorkshop.client.guidebook;

import java.util.ArrayList;
import java.util.Arrays;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.util.StatCollector;

@SideOnly(Side.CLIENT)
public class BookChapterCredits extends BookChapterBase {

    public BookChapterCredits(IBook parentBook, String name) {
        super(parentBook, name);
    }
    
    @Override
    public void createPages() {
        addCategoryPage("programing", new String[] {"RiskyKen"});
        addCategoryPage("premade Skins", new String[] {"RiskyKen", "Choccie_Bunny", "VermillionX", "Dreamer", "Servantfly", "EXTZ", "Gray_Mooo", "Flummie2000"});
        addCategoryPage("textures", new String[] {"RiskyKen", "LordPhrozen", "TheEpicJames"});
        addCategoryPage("sound", new String[] {"RiskyKen", "Borro55"});
        addCategoryPage("localisations", new String[] {
                "Ethan (zh_CN)",
                "ISJump (ko_KR)",
                "VicNightfall (de_DE)",
                "Shtopm (ru_RU)",
                "EzerArch (pt_PT)",
                "EzerArch (pt_BR)",
                "Flummie2000 (de_DE)",
                "V972 (ru_RU)"});
    }
    
    private void addCategoryPage(String pageName, String[] people) {
        Arrays.sort(people);
        ArrayList<String> lines = new ArrayList<String>();
        lines.add(StatCollector.translateToLocal(getUnlocalizedName() + "." + pageName));
        lines.add("");
        for (int i = 0; i < people.length; i++) {
            lines.add(people[i]);
        }
        addPage(new BookPage(parentBook, lines));
    }
}
