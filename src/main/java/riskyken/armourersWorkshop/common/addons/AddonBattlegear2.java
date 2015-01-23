package riskyken.armourersWorkshop.common.addons;

import net.minecraftforge.client.IItemRenderer.ItemRenderType;

import org.lwjgl.opengl.GL11;

import riskyken.armourersWorkshop.utils.EventState;
import riskyken.armourersWorkshop.utils.ModLogger;

public class AddonBattlegear2 extends AbstractAddon {

    public AddonBattlegear2() {
        ModLogger.log("Loading Battlegear 2 Compatibility Addon");
    }

    @Override
    public void init() {

    }

    @Override
    public void initRenderers() {
    }

    @Override
    public String getModName() {
        return "battlegear2";
    }

    @Override
    public void onWeaponRender(ItemRenderType type, EventState state) {
        if (isBattlegearRender()) {
            if (state == EventState.PRE) {
                GL11.glScalef(-1F, 1F, 1F);
            }
            if (state == EventState.POST) {
                GL11.glScalef(-1F, 1F, 1F);
            }
        }
    }

    public static boolean isBattlegearRender() {
        String bgRenderHelper = "mods.battlegear2.client.utils.BattlegearRenderHelper";
        StackTraceElement[] stElements = Thread.currentThread().getStackTrace();
        String callerClassName = null;
        for (int i = 1; i < stElements.length; i++) {
            StackTraceElement ste = stElements[i];
            if (ste.getClassName().equals(bgRenderHelper)) {
                return true;
            }
        }
        return false;
    }
}
