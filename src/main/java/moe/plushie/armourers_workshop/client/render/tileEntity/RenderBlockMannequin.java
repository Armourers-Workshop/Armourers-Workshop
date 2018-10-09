package moe.plushie.armourers_workshop.client.render.tileEntity;

import java.awt.Color;
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import moe.plushie.armourers_workshop.api.common.skin.data.ISkinDescriptor;
import moe.plushie.armourers_workshop.client.model.ModelHelper;
import moe.plushie.armourers_workshop.client.model.ModelMannequin;
import moe.plushie.armourers_workshop.client.render.IRenderBuffer;
import moe.plushie.armourers_workshop.client.render.MannequinFakePlayer;
import moe.plushie.armourers_workshop.client.render.ModRenderHelper;
import moe.plushie.armourers_workshop.client.render.RenderBridge;
import moe.plushie.armourers_workshop.client.skin.cache.ClientSkinCache;
import moe.plushie.armourers_workshop.common.blocks.ModBlocks;
import moe.plushie.armourers_workshop.common.data.BipedRotations;
import moe.plushie.armourers_workshop.common.inventory.MannequinSlotType;
import moe.plushie.armourers_workshop.common.lib.LibModInfo;
import moe.plushie.armourers_workshop.common.skin.data.Skin;
import moe.plushie.armourers_workshop.common.skin.data.SkinDescriptor;
import moe.plushie.armourers_workshop.common.skin.data.SkinProperties;
import moe.plushie.armourers_workshop.common.tileentities.TileEntityMannequin;
import moe.plushie.armourers_workshop.utils.HolidayHelper;
import moe.plushie.armourers_workshop.utils.SkinNBTHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderBlockMannequin extends TileEntitySpecialRenderer<TileEntityMannequin> {
    
    private static final ResourceLocation circle = new ResourceLocation(LibModInfo.ID.toLowerCase(), "textures/other/nanohaCircle.png");
    
    private static RenderBlockMannequinItems renderItems = new RenderBlockMannequinItems();
    private static boolean isHalloweenSeason;
    private static boolean isHalloween;
    private final static float SCALE = 0.0625F;
    private static long lastTextureBuild = 0;
    private final ModelMannequin modelSteve;
    private final ModelMannequin modelAlex;
    //private MannequinFakePlayer mannequinFakePlayer;
    //private final RenderPlayer renderPlayer;
    private final Minecraft mc;
    
    public RenderBlockMannequin() {
        //renderPlayer = (RenderPlayer) RenderManager.instance.entityRenderMap.get(EntityPlayer.class);
        mc = Minecraft.getMinecraft();
        modelSteve = new ModelMannequin(false);
        modelAlex = new ModelMannequin(true);
    }
    
    @Override
    public void render(TileEntityMannequin te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        mc.profiler.startSection("armourersMannequin");
        mc.profiler.startSection("holidayCheck");
        isHalloweenSeason = HolidayHelper.halloween_season.isHolidayActive();
        isHalloween = HolidayHelper.halloween.isHolidayActive();
        //MannequinFakePlayer fakePlayer = te.getFakePlayer();
        
        mc.profiler.endStartSection("move");
        
        
        GlStateManager.pushMatrix();
        
        
        
        GlStateManager.translate(x, y, z);
        GlStateManager.scale(-1, -1, 1);
        GlStateManager.translate(-8 * SCALE, -24F * SCALE, 8 * SCALE);
        GlStateManager.enableRescaleNormal();
        
        
        
        GlStateManager.pushAttrib();
        if (te.getBlockType() == ModBlocks.doll) {
            float dollScale = 0.5F;
            GL11.glScalef(dollScale, dollScale, dollScale);
            GL11.glTranslatef(0, SCALE * 24, 0);
        }
        
        bindTexture(DefaultPlayerSkin.getDefaultSkinLegacy());
        renderModel(te, modelSteve);
        
        GlStateManager.popAttrib();
        GlStateManager.popMatrix();
        
        /*
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
        
        mc.profiler.endStartSection("getTexture");
        ResourceLocation rl;
        boolean slimModel = false;
        boolean download;
        PlayerTexture playerTexture = MannequinTextureHelper.getMannequinTexture(te);
        rl = playerTexture.getResourceLocation();
        slimModel = playerTexture.isSlimModel();
        download = playerTexture.isDownloaded();
        
        ModelMannequin model = modelSteve;
        if (slimModel) {
            model = modelAlex;
        }
        
        mc.profiler.endStartSection("fakePlayer");
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
            if (te.getWorldObj() != null & fakePlayer != null) {
                fakePlayer.setEntityId(te.xCoord * 31 * -te.zCoord);
                fakePlayer.isAirBorne = te.isFlying();
                fakePlayer.capabilities.isFlying = te.isFlying();
            }
        } else {
            mannequinFakePlayer.setEntityId(te.xCoord * 31 * -te.zCoord);
            mannequinFakePlayer.isAirBorne = te.isFlying();
            mannequinFakePlayer.capabilities.isFlying = te.isFlying();
        }
        if (fakePlayer != null) {
            fakePlayer.setEntityId(te.xCoord * 31 * -te.zCoord);
            fakePlayer.isAirBorne = te.isFlying();
            fakePlayer.capabilities.isFlying = te.isFlying();
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
        
        if (isHalloween) {
            double dX = -x - 0.5F;
            double dY = -y - 1.72F;
            double dZ = -z - 0.5F;
            
            double yaw = Math.atan2(dZ, dX);
            double pitch = Math.atan2(Math.sqrt(dZ * dZ + dX * dX), dY) + Math.PI;
            
            yaw -= Math.toRadians(rotaion * 22.5F - 90F);
            pitch += Math.PI / 2D;
            
            model.bipedHead.rotateAngleX = (float) (pitch);
            model.bipedHead.rotateAngleY = (float) (yaw);
            model.bipedHeadwear.rotateAngleX = model.bipedHead.rotateAngleX;
            model.bipedHeadwear.rotateAngleY = model.bipedHead.rotateAngleY;
        }
        
        mc.profiler.endStartSection("textureBuild");
        
        
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
                    if (skins[i] != null) {
                        if (skins[i].hasPaintData() | SkinProperties.PROP_ARMOUR_OVERRIDE.getValue(skins[i].getProperties())) {
                            hasPaintedSkin = true;
                        }
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
                    if (lastTextureBuild + 200L < System.currentTimeMillis()) {
                        lastTextureBuild = System.currentTimeMillis();
                        rl = te.skinTexture.preRender();
                    }
                } else {
                    rl = te.skinTexture.preRender();
                }
            }
        }
        
        
        mc.profiler.endStartSection("textureBind");
        bindTexture(rl);
        
        
        mc.profiler.endStartSection("selectModelRender");
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
            if (te.isVisible() & !(te.getGameProfile() != null && te.getGameProfile().getName().equalsIgnoreCase("null"))) {
                renderModel(te, model, fakePlayer);
            }
            tabSkinHair.hoverColour = getColourAtPos(Mouse.getX(), Mouse.getY());
            GL11.glEnable(GL11.GL_LIGHTING);
        }
        */
        /*
        mc.profiler.endStartSection("modelRender");
        if (te.isVisible() & !(te.getGameProfile() != null && te.getGameProfile().getName().equalsIgnoreCase("null"))) {
            long time = System.currentTimeMillis();
            int fadeTime = 1000;
            int fade = (int) (time - playerTexture.getDownloadTime());
            if (playerTexture.isDownloaded() & fade < fadeTime) {
                bindTexture(AbstractClientPlayer.locationStevePng);
                renderModel(te, model, fakePlayer);
                bindTexture(rl);
                ModRenderHelper.enableAlphaBlend();
                GL11.glColor4f(1, 1, 1, fade / 1000F);
                GL11.glEnable(GL11.GL_POLYGON_OFFSET_FILL);
                GL11.glPolygonOffset(-3F, -3F);
                renderModel(te, model, fakePlayer);
                GL11.glPolygonOffset(0F, 0F);
                GL11.glDisable(GL11.GL_POLYGON_OFFSET_FILL);
                ModRenderHelper.disableAlphaBlend();
                GL11.glColor4f(1, 1, 1, 1);
            } else {
                renderModel(te, model, fakePlayer);
            }
        }
        
        mc.profiler.endStartSection("magicCircle");
        //Magic circle.
        if (te.isRenderExtras() & te.isVisible()) {
            Contributor contributor = Contributors.INSTANCE.getContributor(te.getGameProfile());
            if (contributor != null) {
                int offset = te.xCoord * te.yCoord * te.zCoord;
                renderMagicCircle(contributor.r, contributor.g, contributor.b, partialTickTime, offset, te.getBipedRotations().isChild);
            }
        }
        
        //Render items.
        mc.profiler.endStartSection("equippedItems");
        double distance = Minecraft.getMinecraft().thePlayer.getDistance(
                te.xCoord + 0.5F,
                te.yCoord + 0.5F,
                te.zCoord + 0.5F);
        if (distance <= ConfigHandlerClient.mannequinMaxEquipmentRenderDistance) {
            renderEquippedItems(te, fakePlayer, model, distance);
        }
        
        mc.profiler.endStartSection("reset");
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
        mc.profiler.endStartSection("pop");
        GL11.glPopAttrib();
        GL11.glPopAttrib();
        GL11.glPopMatrix();
        */
        mc.profiler.endSection();
        mc.profiler.endSection();
    }
    
    private void renderMagicCircle(byte r, byte g, byte b, float partialTickTime, int offset, boolean isChild) {
        GL11.glPushMatrix();
        if (isChild) {
            ModelHelper.enableChildModelScale(false, SCALE);
        }
        GL11.glColor4ub(r, g, b, (byte)255);
        //GL11.glColor4f(r, g, b, 1F);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glTranslatef(0F, 1.48F, 0F);
        float circleScale = 2.0F;
        GL11.glScalef(circleScale, circleScale, circleScale);
        float rotation = (float)((double)(mc.world.getTotalWorldTime() + offset) / 0.8F % 360) + partialTickTime;
        GL11.glRotatef(rotation, 0, 1, 0);
        ModRenderHelper.disableLighting();
        ModRenderHelper.enableAlphaBlend();
        bindTexture(circle);
        IRenderBuffer renderBuffer = RenderBridge.INSTANCE;
        
        renderBuffer.startDrawingQuads(DefaultVertexFormats.POSITION_TEX);
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
    }
    
    private void renderModel(TileEntityMannequin te, ModelBiped targetBiped) {
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
    
    private void renderEquippedItems(TileEntityMannequin te, MannequinFakePlayer fakePlayer, ModelMannequin targetBiped, double distance) {
        /*
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
                    renderEquippedItem(renderEntity, new ItemStack(Blocks.lit_pumpkin), targetBiped, i, extraColours, distance, te.getBipedRotations());
                } else {
                    if (stack != null) {
                        renderEquippedItem(renderEntity, stack, targetBiped, i, extraColours, distance, te.getBipedRotations());
                    }
                }
            }
        }*/
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
        SkinDescriptor skinPointer = SkinNBTHelper.getSkinDescriptorFromStack(stack);
        if (skinPointer != null) {
            Skin skin = ClientSkinCache.INSTANCE.getSkin(skinPointer, false);
            if (skin != null) {
                return SkinProperties.PROP_ARMOUR_OVERRIDE.getValue(skin.getProperties());
            }
        }
        return false;
    }
    
    private void renderEquippedItem(MannequinFakePlayer fakePlayer, ItemStack stack, ModelMannequin targetBiped, int slot, byte[] extraColours, double distance, BipedRotations rots) {
        /*Item targetItem = stack.getItem();
        RenderManager rm = RenderManager.instance;
        slot = slot % 7;
        String[] slotName = {"head", "chest", "legs", "unused", "feet", "rightArm", "leftArm"};
        
        mc.profiler.startSection(slotName[slot]);
        GL11.glPushMatrix();
        
        boolean isChild = targetBiped.isChild;
        
        if (isChild) {
            ModelHelper.enableChildModelScale(slot == 0, SCALE);
        }
        targetBiped.isChild = false;
        switch (slot) {
        case 0:
            renderItems.renderHeadStack(fakePlayer, stack, targetBiped, rm, extraColours, distance);
            if (rots != null) {
                rots.applyRotationsToBiped(targetBiped);
            }
            break;
        case 1:
            renderItems.renderChestStack(fakePlayer, stack, targetBiped, rm, extraColours, distance);
            if (rots != null) {
                rots.applyRotationsToBiped(targetBiped);
            }
            break;
        case 2:
            renderItems.renderLegsStack(fakePlayer, stack, targetBiped, rm, extraColours, distance);
            if (rots != null) {
                rots.applyRotationsToBiped(targetBiped);
            }
            break;
        case 3:
            renderItems.renderFeetStack(fakePlayer, stack, targetBiped, rm, extraColours, distance);
            if (rots != null) {
                rots.applyRotationsToBiped(targetBiped);
            }
            break;
        case 4:
            renderItems.renderRightArmStack(fakePlayer, stack, targetBiped, rm, extraColours, distance);
            if (rots != null) {
                rots.applyRotationsToBiped(targetBiped);
            }
            break;
        case 5:
            renderItems.renderLeftArmStack(fakePlayer, stack, targetBiped, rm, extraColours, distance);
            if (rots != null) {
                rots.applyRotationsToBiped(targetBiped);
            }
            break;
        case 6:
            renderItems.renderWingsStack(fakePlayer, stack, targetBiped, rm, extraColours, distance);
            break;
        }
        
        targetBiped.isChild = isChild;
        if (isChild) {
            ModelHelper.disableChildModelScale();
        }
        GL11.glPopMatrix();
        mc.profiler.endSection();*/
    }
    
    private ISkinDescriptor[] getSkinPointers(TileEntityMannequin te) {
        ISkinDescriptor[] skinPointers = new ISkinDescriptor[4 * TileEntityMannequin.INVENTORY_ROWS_COUNT];
        
        for (int i = 0; i < TileEntityMannequin.INVENTORY_ROWS_COUNT; i++) {
            skinPointers[0 + i * 4] = getSkinPointerForSlot(te, 0 + i * 7);
            skinPointers[1 + i * 4] = getSkinPointerForSlot(te, 1 + i * 7);
            skinPointers[2 + i * 4] = getSkinPointerForSlot(te, 2 + i * 7);
            skinPointers[3 + i * 4] = getSkinPointerForSlot(te, 3 + i * 7);
        }

        return skinPointers;
    }
    
    private ISkinDescriptor getSkinPointerForSlot(TileEntityMannequin te, MannequinSlotType slotType) {
        return SkinNBTHelper.getSkinDescriptorFromStack(getStackInMannequinSlot(te, slotType));
    }
    
    private ISkinDescriptor getSkinPointerForSlot(TileEntityMannequin te, int slotIndex) {
        return SkinNBTHelper.getSkinDescriptorFromStack(te.getStackInSlot(slotIndex));
    }
}
