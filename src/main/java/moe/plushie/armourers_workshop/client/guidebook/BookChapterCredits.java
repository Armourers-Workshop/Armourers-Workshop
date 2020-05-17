package moe.plushie.armourers_workshop.client.guidebook;

import java.util.ArrayList;
import java.util.Arrays;

import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class BookChapterCredits extends BookChapterBase {

    public BookChapterCredits(IBook parentBook, String name) {
        super(parentBook, name);
    }

    @Override
    public void createPages() {
        addCategoryPage("programing", new String[] {"RiskyKen"});
        addCategoryPage("premade Skins", new String[] {
                "RiskyKen",
                "Choccie_Bunny",
                "VermillionX",
                "Dreamer",
                "Servantfly",
                "EXTZ",
                "Gray_Mooo",
                "Flummie2000"});
        addCategoryPage("textures", new String[] {
                "RiskyKen",
                "LordPhrozen",
                "TheEpicJames",
                "Thundercat_",
                "skylandersking",
                "MikaPikaaa",
                "LillyFae",
                "andrew0030"});
        addCategoryPage("sound", new String[] {"RiskyKen", "Borro55"});
        addCategoryPage("localisations", new String[] {
                "Ethan (zh_CN)",
                "ISJump (ko_KR)",
                "VicNightfall (de_DE)",
                "Shtopm (ru_RU)",
                "EzerArch (pt_PT)",
                "EzerArch (pt_BR)",
                "Flummie2000 (de_DE)",
                "V972 (ru_RU)",
                "BredFace (pt_BR)",
                "Equine0x (fr_FR)",
                "_Hoppang_ (ko_KR)",
                "M_H_Berre (ko_KR)",
                "BlackGear27 (es_ES)",
                "JasonJeong (ko_KR)", 
                "SQwatermark (zh_CN)"});
        addCategoryPage("wiki editors", new String[] {"DoomRater"});
    }

    private void addCategoryPage(String pageName, String[] people) {
        Arrays.sort(people);
        ArrayList<String> lines = new ArrayList<String>();
        lines.add(I18n.format(getUnlocalizedName() + "." + pageName));
        lines.add("");
        for (int i = 0; i < people.length; i++) {
            lines.add(people[i]);
        }
        addPage(new BookPage(parentBook, lines));
    }
}
