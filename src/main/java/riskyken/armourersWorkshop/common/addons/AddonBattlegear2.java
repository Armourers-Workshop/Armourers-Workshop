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
    public String getModName() {
        return "battlegear2";
    }

    @Override
    public void onWeaponRender(ItemRenderType type, EventState state) {
        if (isBattlegearRender()) {
            if (state == EventState.PRE) {
                /*
                FloatBuffer fb = ByteBuffer.allocateDirect(4 * 16).asFloatBuffer();
                GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, fb);
                double XScale = Math.sqrt(fb.get(0)*fb.get(0)+fb.get(6)*fb.get(6)+fb.get(3)*fb.get(3));
                double YScale = Math.sqrt(fb.get(1)*fb.get(1)+fb.get(4)*fb.get(4)+fb.get(7)*fb.get(7));
                //ModLogger.log(XScale);
                */
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
