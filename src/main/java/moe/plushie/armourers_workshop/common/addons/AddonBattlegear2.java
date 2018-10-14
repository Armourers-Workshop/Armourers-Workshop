package moe.plushie.armourers_workshop.common.addons;

import moe.plushie.armourers_workshop.common.addons.ModAddonManager.ItemOverrideType;

public class AddonBattlegear2 extends ModAddon {
    
    public AddonBattlegear2() {
        super("battlegear2", "Battlegear 2");
    }

    @Override
    public void preInit() {
        addItemOverride(ItemOverrideType.SWORD, "waraxe.wood");
        addItemOverride(ItemOverrideType.SWORD, "waraxe.stone");
        addItemOverride(ItemOverrideType.SWORD, "waraxe.iron");
        addItemOverride(ItemOverrideType.SWORD, "waraxe.diamond");
        addItemOverride(ItemOverrideType.SWORD, "waraxe.gold");
        
        addItemOverride(ItemOverrideType.SWORD, "mace.wood");
        addItemOverride(ItemOverrideType.SWORD, "mace.stone");
        addItemOverride(ItemOverrideType.SWORD, "mace.iron");
        addItemOverride(ItemOverrideType.SWORD, "mace.diamond");
        addItemOverride(ItemOverrideType.SWORD, "mace.gold");
        
        addItemOverride(ItemOverrideType.SWORD, "spear.wood");
        addItemOverride(ItemOverrideType.SWORD, "spear.stone");
        addItemOverride(ItemOverrideType.SWORD, "spear.iron");
        addItemOverride(ItemOverrideType.SWORD, "spear.diamond");
        addItemOverride(ItemOverrideType.SWORD, "spear.gold");
    }

    /* @Override
    public void onWeaponRender(ItemRenderType type, EventState state) {
        if (isBattlegearRender()) {
            if (state == EventState.PRE) {
                /*
                FloatBuffer fb = ByteBuffer.allocateDirect(4 * 16).asFloatBuffer();
                GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, fb);
                double XScale = Math.sqrt(fb.get(0)*fb.get(0)+fb.get(6)*fb.get(6)+fb.get(3)*fb.get(3));
                double YScale = Math.sqrt(fb.get(1)*fb.get(1)+fb.get(4)*fb.get(4)+fb.get(7)*fb.get(7));
                //ModLogger.log(XScale);
                *
                GL11.glScalef(-1F, 1F, 1F);
            }
            if (state == EventState.POST) {
                GL11.glScalef(-1F, 1F, 1F);
            }
        }
    } */

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
