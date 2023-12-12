package moe.plushie.armourers_workshop.init;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.utils.math.OpenQuaternionf;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class ModDebugger {

    public static float rx = 0;
    public static float ry = 0;
    public static float rz = 0;
    public static float tx = 0;
    public static float ty = 0;
    public static float tz = 0;
    public static float sx = 1;
    public static float sy = 1;
    public static float sz = 1;

    public static int flag1 = 0;


    public static boolean skinnable = false;
    public static boolean hologramProjector = false;
    public static boolean advancedBuilder = false;

    public static boolean skinBounds = false;
    public static boolean skinOrigin = false;

    public static boolean skinPartBounds = false;
    public static boolean skinPartOrigin = false;

    public static boolean targetBounds = false;

    public static boolean boundingBox = false;

    public static boolean mannequinCulling = false;

    public static boolean itemOverride = false;
    public static boolean handOverride = false;
    public static boolean modelOverride = false;

    public static boolean textureBounds = false;
    public static boolean spin = false;

    public static boolean tooltip = false;
    public static boolean properties = false;

    public static boolean wireframeRender = false;

    public static boolean viewHierarchy = false;

    public static boolean armature = false;

    // Debug tool
    public static boolean armourerDebugRender;
    public static boolean lodLevels;
    public static boolean skinBlockBounds;
    public static boolean skinRenderBounds;
    public static boolean sortOrderToolTip;

    @Environment(EnvType.CLIENT)
    public static void rotate(PoseStack poseStack) {
        poseStack.mulPose(new OpenQuaternionf(rx, ry, rz, true));
    }

    @Environment(EnvType.CLIENT)
    public static void scale(PoseStack poseStack) {
        poseStack.scale(sx, sy, sz);
    }

    @Environment(EnvType.CLIENT)
    public static void translate(PoseStack poseStack) {
        poseStack.translate(tx, ty, tz);
    }

    public static void init() {
//        File file = new File("/Users/sagesse/Projects/Minecraft/Armourers-Workshop-2F/web/doc/bipped.bbmodel");
//        File file = new File("/Users/sagesse/Documents/Models/青龙.bbmodel");
//        File file = new File("/Users/sagesse/Documents/Models/ahri.bbmodel");
//        File file = new File("/Users/sagesse/Documents/Models/棉被王.bbmodel");
//        File file = new File("/Users/sagesse/Documents/Models/薪炎.bbmodel");
//        File file = new File("/Users/sagesse/Documents/Models/人物.bbmodel");
//        File file = new File("/Users/sagesse/Documents/Models/等离子影秀.bbmodel");
//        File file = new File("/Users/sagesse/Documents/Models/test.bbmodel");
//        File file = new File("/Users/sagesse/Downloads/Ahri Spirit Blossom.zip");
//        File tt = new File("/Users/sagesse/Downloads/Default/test.armour");
//
//        try {
//            Skin skin = SkinSerializerV21.readSkinFromFile(file);
//            ModLog.debug("{}", skin);
//            File tt = new File("/Users/sagesse/Library/Application Support/minecraft/armourers_workshop/skin-library/test-bb/" + skin.getCustomName() + ".armour");
//            SkinFileStreamUtils.saveSkinToFile(tt, skin);
//            ModLog.debug("{}", skin);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

//        File dir = new File("/Users/sagesse/Downloads/1.16东方人物模型包(Discord)");
//        for (File file : dir.listFiles()) {
//            try {
//                Skin skin = SkinSerializerV21.readSkinFromFile(file);
//                ModLog.debug("{}", skin);
//                SkinFileStreamUtils.saveSkinToFile(new File("/Users/sagesse/Library/Application Support/minecraft/armourers_workshop/skin-library/test-bb/" + file.getName().replaceAll("\\..*?$", ".armour")), skin);
//                ModLog.debug("{}", skin);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }


////        if (true) {
////            return;
////        }
//
////                    File file = new File("/Users/sagesse/Projects/Minecraft/CustomPlayerModel/steve");
////            File file = new File("/Users/sagesse/Downloads/Ahri Spirit Blossom");
//        File file = new File("/Users/sagesse/Downloads/Default");
//////            File file = new File("/Users/sagesse/Downloads/Hakurei Reimu");
//////            File file = new File("/Users/sagesse/Downloads/Syameimaru Aya.zip");
//////            File file = new File("/Users/sagesse/Downloads/legacy_guide.mcaddon");
//        try {
////            BedrockModel model = BedrockModelLoader.readFromStream(new FileInputStream(file));
//            Skin skin = SkinSerializerV21.readSkinFromFile(file);
//            ModLog.debug("{}", skin);
//            SkinFileStreamUtils.saveSkinToFile(tt, skin);
////            skin = SkinFileStreamUtils.loadSkinFromFile(tt);
////            ModLog.debug("{}", skin);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }
}
