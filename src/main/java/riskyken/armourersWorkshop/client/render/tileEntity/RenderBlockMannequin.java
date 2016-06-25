package riskyken.armourersWorkshop.client.render.tileEntity;

import java.awt.Color;
import java.nio.FloatBuffer;
import java.util.HashSet;

import org.lwjgl.BufferUtils;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.mojang.authlib.GameProfile;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import riskyken.armourersWorkshop.api.common.skin.data.ISkinDye;
import riskyken.armourersWorkshop.api.common.skin.data.ISkinPointer;
import riskyken.armourersWorkshop.client.gui.GuiMannequin;
import riskyken.armourersWorkshop.client.gui.GuiMannequinTabSkinHair;
import riskyken.armourersWorkshop.client.model.ModelHelper;
import riskyken.armourersWorkshop.client.model.ModelMannequin;
import riskyken.armourersWorkshop.client.render.EntityTextureInfo;
import riskyken.armourersWorkshop.client.render.MannequinFakePlayer;
import riskyken.armourersWorkshop.client.render.ModRenderHelper;
import riskyken.armourersWorkshop.client.skin.ClientSkinCache;
import riskyken.armourersWorkshop.common.ApiRegistrar;
import riskyken.armourersWorkshop.common.config.ConfigHandler;
import riskyken.armourersWorkshop.common.data.BipedRotations;
import riskyken.armourersWorkshop.common.inventory.MannequinSlotType;
import riskyken.armourersWorkshop.common.lib.LibModInfo;
import riskyken.armourersWorkshop.common.skin.data.Skin;
import riskyken.armourersWorkshop.common.tileentities.TileEntityMannequin;
import riskyken.armourersWorkshop.utils.HolidayHelper;
import riskyken.armourersWorkshop.utils.SkinNBTHelper;
import riskyken.plushieWrapper.client.IRenderBuffer;
import riskyken.plushieWrapper.client.RenderBridge;

@SideOnly(Side.CLIENT)
public class RenderBlockMannequin extends TileEntitySpecialRenderer {
    
    private static final ResourceLocation circle = new ResourceLocation(LibModInfo.ID.toLowerCase(), "textures/other/nanohaCircle.png");
    
    private static RenderBlockMannequinItems renderItems = new RenderBlockMannequinItems();
    private static boolean isHalloweenSeason;
    private final static float SCALE = 0.0625F;
    private static long lastTextureBuild = 0;
    private static long lastSkinDownload = 0;
    private static final HashSet<String> downloadedSkins = new HashSet<String>();;
    
    private final ModelMannequin model;
    private MannequinFakePlayer mannequinFakePlayer;
    private final RenderPlayer renderPlayer;
    private final Minecraft mc;
    
    public RenderBlockMannequin() {
        renderPlayer = (RenderPlayer) RenderManager.instance.entityRenderMap.get(EntityPlayer.class);
        mc = Minecraft.getMinecraft();
        model = new ModelMannequin();
        isHalloweenSeason = HolidayHelper.halloween_season.isHolidayActive();
    }
    
    public void renderTileEntityAt(TileEntityMannequin te, double x, double y, double z, float partialTickTime) {
        mc.mcProfiler.startSection("armourersMannequin");
        MannequinFakePlayer fakePlayer = te.getFakePlayer();
        mc.mcProfiler.startSection("move");
        model.compile(SCALE);
        
        GL11.glPushMatrix();
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
        GL11.glPushAttrib(GL11.GL_LIGHTING_BIT);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        ModRenderHelper.disableAlphaBlend();
        
        int rotaion = te.getRotation();
        
        GL11.glTranslated(x + 0.5D + te.getOffsetX(), y + 1.0D + te.getOffsetY(), z + 0.5D + te.getOffsetZ());
        BipedRotations rots = te.getBipedRotations();
        GL11.glRotated(Math.toDegrees(rots.chest.rotationX), 1F, 0F, 0F);
        GL11.glRotated(Math.toDegrees(rots.chest.rotationY), 0F, 1F, 0F);
        GL11.glRotated(Math.toDegrees(rots.chest.rotationZ), 0F, 0F, 1F);
        GL11.glTranslated(0, 0.5D, 0);
        
        GL11.glScalef(SCALE * 15, SCALE * 15, SCALE * 15);
        GL11.glTranslated(0, SCALE * -1.6F, 0);
        
        GL11.glScalef(-1, -1, 1);
        GL11.glRotatef(rotaion * 22.5F, 0, 1, 0);

        if (te.getIsDoll()) {
            float dollScale = 0.5F;
            GL11.glScalef(dollScale, dollScale, dollScale);
            GL11.glTranslatef(0, SCALE * 24, 0);
        }
        
        mc.mcProfiler.endStartSection("fakePlayer");
        if (mannequinFakePlayer == null) {
            mannequinFakePlayer = new MannequinFakePlayer(te.getWorldObj(), new GameProfile(null, "[Mannequin]"));
            mannequinFakePlayer.posX = x;
            mannequinFakePlayer.posY = y;
            mannequinFakePlayer.posZ = z;
            mannequinFakePlayer.prevPosX = x;
            mannequinFakePlayer.prevPosY = y;
            mannequinFakePlayer.prevPosZ = z;
        }
        
        if (te.getGameProfile() != null) {
            if (te.getGameProfile() != null & te.getWorldObj() != null) {
                if (fakePlayer == null) {
                    fakePlayer = new MannequinFakePlayer(te.getWorldObj(), te.getGameProfile());
                    fakePlayer.posX = x;
                    fakePlayer.posY = y;
                    fakePlayer.posZ = z;
                    fakePlayer.prevPosX = x;
                    fakePlayer.prevPosY = y;
                    fakePlayer.prevPosZ = z;
                    
                    te.setFakePlayer(fakePlayer);
                }
            }
        }
        
        if (te.getBipedRotations() != null) {
            te.getBipedRotations().applyRotationsToBiped(model);
            te.getBipedRotations().applyRotationsToBiped(renderPlayer.modelArmor);
            te.getBipedRotations().applyRotationsToBiped(renderPlayer.modelArmorChestplate);
        }
        
        
        ApiRegistrar.INSTANCE.onRenderMannequin(te, te.getGameProfile());
        
        model.bipedRightArm.setRotationPoint(-5.0F, 2.0F , 0.0F);
        model.bipedLeftArm.setRotationPoint(5.0F, 2.0F , 0.0F);
        model.bipedHead.setRotationPoint(0.0F, 0.0F, 0.0F);
        model.bipedHeadwear.setRotationPoint(0.0F, 0.0F, 0.0F);
        model.bipedRightLeg.setRotationPoint(-1.9F, 12.0F, 0.0F);
        model.bipedLeftLeg.setRotationPoint(1.9F, 12.0F, 0.0F);
        
        rots.applyRotationsToBiped(model);
        model.bipedBody.rotateAngleX = 0;
        model.bipedBody.rotateAngleY = 0;
        model.bipedBody.rotateAngleZ = 0;
        
        mc.mcProfiler.endStartSection("getTexture");
        ResourceLocation rl = AbstractClientPlayer.locationStevePng;
        if (te.getGameProfile() != null) {
            String name = te.getGameProfile().getName();
            if (downloadedSkins.contains(name)) {
                rl = AbstractClientPlayer.getLocationSkin(name);
                AbstractClientPlayer.getDownloadImageSkin(rl, name);
            } else {
                if (lastSkinDownload + 100L < System.currentTimeMillis()) {
                    lastSkinDownload = System.currentTimeMillis();
                    rl = AbstractClientPlayer.getLocationSkin(name);
                    AbstractClientPlayer.getDownloadImageSkin(rl, name);
                    downloadedSkins.add(name);
                }
            }
        }
        
        
        mc.mcProfiler.endStartSection("textureBuild");
        
        if (te.haveSkinsUpdated()) {
            te.sp = getSkinPointers(te);
        }
        if (te.sp != null) {
            ISkinPointer[] sp = te.sp;
            Skin[] skins = new Skin[sp.length];
            ISkinDye[] dyes = new ISkinDye[sp.length];
            boolean hasPaintedSkin = false;
            
            for (int i = 0; i < sp.length; i++) {
                if (sp[i] != null) {
                    skins[i] = ClientSkinCache.INSTANCE.getSkin(sp[i]);
                    dyes[i] = sp[i].getSkinDye();
                    if (skins[i] != null && skins[i].hasPaintData()) {
                        hasPaintedSkin = true;
                    }
                }
            }
            
            if (hasPaintedSkin) {
                if (te.skinTexture == null) {
                    te.skinTexture = new EntityTextureInfo();
                }
                
                te.skinTexture.updateTexture(rl);
                te.skinTexture.updateSkinColour(te.getSkinColour());
                te.skinTexture.updateHairColour(te.getHairColour());
                te.skinTexture.updateSkins(skins);
                te.skinTexture.updateDyes(dyes);
                
                if (te.skinTexture.getNeedsUpdate()) {
                    if (lastTextureBuild + 100L < System.currentTimeMillis()) {
                        lastTextureBuild = System.currentTimeMillis();
                        rl = te.skinTexture.preRender();
                    }
                } else {
                    rl = te.skinTexture.preRender();
                }
            }
        }
        
        mc.mcProfiler.endStartSection("textureBind");
        bindTexture(rl);
        
        
        mc.mcProfiler.endStartSection("modelRender");
        te.getBipedRotations().hasCustomHead = hasCustomHead(te);
        
        boolean selectingColour = false;
        GuiMannequinTabSkinHair tabSkinHair = null;
        
        if (mc.currentScreen instanceof GuiMannequin) {
            GuiMannequin screen = (GuiMannequin) mc.currentScreen;
            if (screen.tileEntity == te) {
                tabSkinHair = screen.tabSkinAndHair;
                if (tabSkinHair.selectingSkinColour | tabSkinHair.selectingHairColour) {
                    selectingColour = true;
                }
            }

        }
        
        if (selectingColour) {
            GL11.glDisable(GL11.GL_LIGHTING);
            if (!(te.getGameProfile() != null && te.getGameProfile().getName().equalsIgnoreCase("null"))) {
                renderModel(te, model, fakePlayer);
            }
            tabSkinHair.hoverColour = getColourAtPos(Mouse.getX(), Mouse.getY());
            GL11.glEnable(GL11.GL_LIGHTING);
        }
        
        if (!(te.getGameProfile() != null && te.getGameProfile().getName().equalsIgnoreCase("null"))) {
            renderModel(te, model, fakePlayer);
        }
        
        if (te.getGameProfile() != null && te.getGameProfile().getName().equals("deadmau5")) {
            GL11.glPushMatrix();
            GL11.glRotated(Math.toDegrees(model.bipedHead.rotateAngleZ), 0, 0, 1);
            GL11.glRotated(Math.toDegrees(model.bipedHead.rotateAngleY), 0, 1, 0);
            GL11.glRotated(Math.toDegrees(model.bipedHead.rotateAngleX), 1, 0, 0);
            GL11.glTranslated(-5.5F * SCALE, 0, 0);
            GL11.glTranslated(0, -6.5F * SCALE, 0);
            model.bipedEars.render(SCALE);
            GL11.glTranslated(11F * SCALE, 0, 0);
            model.bipedEars.render(SCALE);
            GL11.glPopMatrix();
        }
        
        //Magic circle.
        if (te.isRenderExtras()) {
            if (te.hasSpecialRender()) {
                float[] colour = te.getSpecialRenderColour();
                int offset = te.xCoord * te.yCoord * te.zCoord;
                renderMagicCircle(colour[0], colour[1], colour[2], partialTickTime, offset, te.getBipedRotations().isChild);
            }
        }
        
        //Render items.
        mc.mcProfiler.endStartSection("equippedItems");
        if (te.getDistanceFrom(field_147501_a.field_147560_j, field_147501_a.field_147561_k, field_147501_a.field_147558_l) < ConfigHandler.mannequinMaxEquipmentRenderDistance) {
            renderEquippedItems(te, fakePlayer, model);
        }
        
        mc.mcProfiler.endStartSection("reset");
        model.bipedLeftLeg.rotateAngleZ = 0F;
        model.bipedRightLeg.rotateAngleZ = 0F;
        model.bipedHead.rotateAngleZ = 0F;
        model.bipedHeadwear.rotateAngleZ = 0F;
        
        renderPlayer.modelArmor.bipedLeftLeg.rotateAngleZ = 0F;
        renderPlayer.modelArmor.bipedRightLeg.rotateAngleZ = 0F;
        renderPlayer.modelArmor.bipedHead.rotateAngleZ = 0F;
        renderPlayer.modelArmor.bipedHeadwear.rotateAngleZ = 0F;
        
        renderPlayer.modelArmorChestplate.bipedLeftLeg.rotateAngleZ = 0F;
        renderPlayer.modelArmorChestplate.bipedRightLeg.rotateAngleZ = 0F;
        renderPlayer.modelArmorChestplate.bipedHead.rotateAngleZ = 0F;
        renderPlayer.modelArmorChestplate.bipedHeadwear.rotateAngleZ = 0F;
        mc.mcProfiler.endStartSection("pop");
        GL11.glPopAttrib();
        GL11.glPopAttrib();
        GL11.glPopMatrix();
        mc.mcProfiler.endSection();
        mc.mcProfiler.endSection();
    }
    
    private void renderMagicCircle(float r, float g, float b, float partialTickTime, int offset, boolean isChild) {
        mc.mcProfiler.startSection("magicCircle");
        GL11.glPushMatrix();
        if (isChild) {
            ModelHelper.enableChildModelScale(false, SCALE);
        }
        GL11.glColor4f(r, g, b, 1F);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glTranslatef(0F, 1.48F, 0F);
        float circleScale = 2.0F;
        GL11.glScalef(circleScale, circleScale, circleScale);
        float rotation = (float)((double)(mc.theWorld.getTotalWorldTime() + offset) / 0.8F % 360) + partialTickTime;
        GL11.glRotatef(rotation, 0, 1, 0);
        ModRenderHelper.disableLighting();
        ModRenderHelper.enableAlphaBlend();
        bindTexture(circle);
        IRenderBuffer renderBuffer = RenderBridge.INSTANCE;
        
        renderBuffer.startDrawingQuads();
        renderBuffer.addVertexWithUV(-1, 0, -1, 1, 0);
        renderBuffer.addVertexWithUV(1, 0, -1, 0, 0);
        renderBuffer.addVertexWithUV(1, 0, 1, 0, 1);
        renderBuffer.addVertexWithUV(-1, 0, 1, 1, 1);
        renderBuffer.draw();
        
        ModRenderHelper.disableAlphaBlend();
        ModRenderHelper.enableLighting();
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glColor4f(1F, 1F, 1F, 1F);
        if (isChild) {
            ModelHelper.disableChildModelScale();
        }
        GL11.glPopMatrix();
        mc.mcProfiler.endSection();
    }
    
    private void renderModel(TileEntityMannequin te, ModelBiped targetBiped, MannequinFakePlayer fakePlayer) {
        if (!hasCustomHead(te)) {
            if (te.getBipedRotations().isChild) {
                ModelHelper.enableChildModelScale(true, SCALE);
            }
            targetBiped.bipedHead.render(SCALE);
            GL11.glDisable(GL11.GL_CULL_FACE);
            targetBiped.bipedHeadwear.render(SCALE);
            GL11.glEnable(GL11.GL_CULL_FACE);
            if (te.getBipedRotations().isChild) {
                ModelHelper.disableChildModelScale();
            };
        }
        if (te.getBipedRotations().isChild) {
            ModelHelper.enableChildModelScale(false, SCALE);
        }

        targetBiped.bipedBody.render(SCALE);
        targetBiped.bipedRightArm.render(SCALE);
        targetBiped.bipedLeftArm.render(SCALE);
        targetBiped.bipedRightLeg.render(SCALE);
        targetBiped.bipedLeftLeg.render(SCALE);
        
        if (te.getBipedRotations().isChild) {
            ModelHelper.disableChildModelScale();
        }
    }
    
    private Color getColourAtPos(int x, int y) {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(3);
        GL11.glReadPixels(x, y, 1, 1, GL11.GL_RGB, GL11.GL_FLOAT, buffer);
        int r = Math.round(buffer.get() * 255);
        int g = Math.round(buffer.get() * 255);
        int b = Math.round(buffer.get() * 255);
        return new Color(r,g,b);
    }
    
    private void renderEquippedItems(TileEntityMannequin te, MannequinFakePlayer fakePlayer, ModelBiped targetBiped) {
        RenderItem ri = (RenderItem) RenderManager.instance.entityRenderMap.get(EntityItem.class);
        MannequinFakePlayer renderEntity = fakePlayer;
        if (renderEntity == null) {
            renderEntity = mannequinFakePlayer;
        }
        
        Color skinColour = new Color(te.getSkinColour());
        Color hairColour = new Color(te.getHairColour());
        
        byte[] extraColours = new byte[6];
        extraColours[0] = (byte) skinColour.getRed();
        extraColours[1] = (byte) skinColour.getGreen();
        extraColours[2] = (byte) skinColour.getBlue();
        extraColours[3] = (byte) hairColour.getRed();
        extraColours[4] = (byte) hairColour.getGreen();
        extraColours[5] = (byte) hairColour.getBlue();
        
        for (int i = 0; i < te.getSizeInventory(); i++) {
            ItemStack stack = te.getStackInSlot(i);
            if (renderEntity != null) {
                if (i == 0 & isHalloweenSeason) {
                    renderEquippedItem(renderEntity, new ItemStack(Blocks.lit_pumpkin), targetBiped, i, extraColours);
                } else {
                    if (stack != null) {
                        renderEquippedItem(renderEntity, stack, targetBiped, i, extraColours);
                    }
                }
            }
        }
    }
    
    public ItemStack getStackInMannequinSlot(IInventory inventory, MannequinSlotType slot) {
        return inventory.getStackInSlot(slot.ordinal());
    }
    
    private boolean hasCustomHead(IInventory inventory) {
        ItemStack stack = getStackInMannequinSlot(inventory, MannequinSlotType.HEAD);
        if (stack != null) {
            if (stack.getItem() instanceof ItemBlock) {
                return true;
            }
        }
        if (isHalloweenSeason) {
            return true;
        }
        return false;
    }
    
    private void renderEquippedItem(MannequinFakePlayer fakePlayer, ItemStack stack, ModelBiped targetBiped, int slot, byte[] extraColours) {
        Item targetItem = stack.getItem();
        RenderManager rm = RenderManager.instance;
        
        String[] slotName = {"head", "chest", "legs", "unused", "feet", "rightArm", "leftArm"};
        
        mc.mcProfiler.startSection(slotName[slot]);
        GL11.glPushMatrix();
        
        boolean isChild = targetBiped.isChild;
        
        if (isChild) {
            ModelHelper.enableChildModelScale(slot == 0, SCALE);
        }
        targetBiped.isChild = false;
        switch (slot) {
        case 0:
            renderItems.renderHeadStack(fakePlayer, stack, targetBiped, rm, extraColours);
            break;
        case 1:
            renderItems.renderChestStack(fakePlayer, stack, targetBiped, rm, extraColours);
            break;
        case 2:
            renderItems.renderLegsStack(fakePlayer, stack, targetBiped, rm, extraColours);
            break;
        case 3:
            renderItems.renderFeetStack(fakePlayer, stack, targetBiped, rm, extraColours);
            break;
        case 4:
            renderItems.renderRightArmStack(fakePlayer, stack, targetBiped, rm, extraColours);
            break;
        case 5:
            renderItems.renderLeftArmStack(fakePlayer, stack, targetBiped, rm, extraColours);
            break;
        case 6:
            //renderItems.renderWingsStack(fakePlayer, stack, targetBiped, rm, extraColours);
            break;
        }
        
        targetBiped.isChild = isChild;
        if (isChild) {
            ModelHelper.disableChildModelScale();
        }
        GL11.glPopMatrix();
        mc.mcProfiler.endSection();
    }
    
    private ISkinPointer[] getSkinPointers(TileEntityMannequin te) {
        ISkinPointer[] skinPointers = new ISkinPointer[4];
        skinPointers[0] = getSkinPointerForSlot(te, MannequinSlotType.HEAD);
        skinPointers[1] = getSkinPointerForSlot(te, MannequinSlotType.CHEST);
        skinPointers[2] = getSkinPointerForSlot(te, MannequinSlotType.LEGS);
        skinPointers[3] = getSkinPointerForSlot(te, MannequinSlotType.FEET);
        return skinPointers;
    }
    
    private ISkinPointer getSkinPointerForSlot(TileEntityMannequin te, MannequinSlotType slotType) {
        return SkinNBTHelper.getSkinPointerFromStack(getStackInMannequinSlot(te, slotType));
    }
    
    @Override
    public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float partialTickTime) {
        renderTileEntityAt((TileEntityMannequin)tileEntity, x, y, z, partialTickTime);
    }
}
