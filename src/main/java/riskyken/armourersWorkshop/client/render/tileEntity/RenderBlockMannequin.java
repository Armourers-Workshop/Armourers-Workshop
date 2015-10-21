package riskyken.armourersWorkshop.client.render.tileEntity;

import org.lwjgl.opengl.GL11;

import com.mojang.authlib.GameProfile;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
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
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.common.MinecraftForge;
import riskyken.armourersWorkshop.client.model.ModelHelper;
import riskyken.armourersWorkshop.client.model.ModelMannequin;
import riskyken.armourersWorkshop.client.render.MannequinFakePlayer;
import riskyken.armourersWorkshop.client.render.ModRenderHelper;
import riskyken.armourersWorkshop.common.ApiRegistrar;
import riskyken.armourersWorkshop.common.SkinHelper;
import riskyken.armourersWorkshop.common.config.ConfigHandler;
import riskyken.armourersWorkshop.common.inventory.MannequinSlotType;
import riskyken.armourersWorkshop.common.lib.LibModInfo;
import riskyken.armourersWorkshop.common.tileentities.TileEntityMannequin;
import riskyken.armourersWorkshop.utils.HolidayHelper;
import riskyken.plushieWrapper.client.IRenderBuffer;
import riskyken.plushieWrapper.client.RenderBridge;

@SideOnly(Side.CLIENT)
public class RenderBlockMannequin extends TileEntitySpecialRenderer {
    
    private static final ResourceLocation circle = new ResourceLocation(LibModInfo.ID.toLowerCase(), "textures/other/nanohaCircle.png");
    
    private static RenderBlockMannequinItems renderItems = new RenderBlockMannequinItems();
    private static boolean isHalloweenSeason;
    private static boolean isHalloween;
    private final static float SCALE = 0.0625F;
    
    private ModelMannequin model;
    private MannequinFakePlayer mannequinFakePlayer;
    private RenderPlayer renderPlayer;
    private final Minecraft mc;
    
    
    public RenderBlockMannequin() {
        renderPlayer = (RenderPlayer) RenderManager.instance.entityRenderMap.get(EntityPlayer.class);
        mc = Minecraft.getMinecraft();
        model = new ModelMannequin();
        
        isHalloweenSeason = HolidayHelper.halloween_season.isHolidayActive();
        isHalloween = HolidayHelper.halloween.isHolidayActive();
    }
    
    public void renderTileEntityAt(TileEntityMannequin te, double x, double y, double z, float partialTickTime) {
        mc.mcProfiler.startSection("armourersMannequin");
        MannequinFakePlayer fakePlayer = te.getFakePlayer();
        
        model.compile(SCALE);
        
        GL11.glPushMatrix();
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glEnable(GL11.GL_NORMALIZE);
        
        int rotaion = te.getRotation();
        
        GL11.glTranslated(x + 0.5D, y + 1.5D, z + 0.5D);
        GL11.glScalef(SCALE * 15, SCALE * 15, SCALE * 15);
        GL11.glTranslated(0, SCALE * -1.6F, 0);
        
        GL11.glScalef(-1, -1, 1);
        GL11.glRotatef(rotaion * 22.5F, 0, 1, 0);
        
        if (te.getIsDoll()) {
            float dollScale = 0.5F;
            GL11.glScalef(dollScale, dollScale, dollScale);
            GL11.glTranslatef(0, SCALE * 24, 0);
        }
        
        mc.mcProfiler.startSection("fakePlayer");
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
        mc.mcProfiler.endSection();
        
        if (ConfigHandler.mannequinsCallPlayerRenders) {
            if (fakePlayer != null) {
                mc.mcProfiler.startSection("preEvents");
                model.isChild = te.getBipedRotations().isChild;
                fakePlayer.rotationPitch = (float) Math.toDegrees(te.getBipedRotations().head.rotationX);
                fakePlayer.prevRotationPitch = (float) Math.toDegrees(te.getBipedRotations().head.rotationX);
                fakePlayer.rotationYawHead = (float) Math.toDegrees(te.getBipedRotations().head.rotationY);
                fakePlayer.prevRotationYawHead = (float) Math.toDegrees(te.getBipedRotations().head.rotationY);
                fakePlayer.onUpdate();
                
                fakePlayer.ticksExisted = Minecraft.getMinecraft().thePlayer.ticksExisted;
                
                //Pre render events
                RenderPlayerEvent.Pre preEvent = new RenderPlayerEvent.Pre(fakePlayer, renderPlayer, partialTickTime);
                RenderPlayerEvent.Specials.Pre preEventSpecials = new RenderPlayerEvent.Specials.Pre(fakePlayer, renderPlayer, partialTickTime);

                if (model.isChild) {
                    ModelHelper.enableChildModelScale(false, SCALE);
                }
                GL11.glDisable(GL11.GL_CULL_FACE);
                
                MinecraftForge.EVENT_BUS.post(preEvent);
                MinecraftForge.EVENT_BUS.post(preEventSpecials);
                
                GL11.glEnable(GL11.GL_CULL_FACE);
                if (model.isChild) {
                    ModelHelper.disableChildModelScale();
                }
                mc.mcProfiler.endSection();
            }
        }
        
        
        ApiRegistrar.INSTANCE.onRenderMannequin(te, te.getGameProfile());
        
        model.bipedRightArm.setRotationPoint(-5.0F, 2.0F , 0.0F);
        model.bipedLeftArm.setRotationPoint(5.0F, 2.0F , 0.0F);
        model.bipedHead.setRotationPoint(0.0F, 0.0F, 0.0F);
        model.bipedHeadwear.setRotationPoint(0.0F, 0.0F, 0.0F);
        model.bipedRightLeg.setRotationPoint(-1.9F, 12.0F, 0.0F);
        model.bipedLeftLeg.setRotationPoint(1.9F, 12.0F, 0.0F);
        
        te.getBipedRotations().applyRotationsToBiped(model);
        
        //Render model
        mc.mcProfiler.startSection("textureBind");
        SkinHelper.bindPlayersNormalSkin(te.getGameProfile());
        mc.mcProfiler.endSection();
        mc.mcProfiler.startSection("modelRender");
        te.getBipedRotations().hasCustomHead = hasCustomHead(te);
        renderModel(te, model, fakePlayer);
        
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
        
        mc.mcProfiler.endSection();
        
        //Magic circle.
        if (te.getGameProfile() != null) {
            if (te.getGameProfile().getName().equals("RiskyKen")) {
                int offset = te.xCoord * te.yCoord * te.zCoord;
                renderMagicCircle(249F / 255, 223F / 255, 140F / 255, partialTickTime, offset, te.getBipedRotations().isChild);
            } else if (te.getGameProfile().getName().equals("Nanoha")) {
                int offset = te.xCoord * te.yCoord * te.zCoord;
                renderMagicCircle(1F, 173F / 255, 1F, partialTickTime, offset, te.getBipedRotations().isChild);
            }
        }

        
        //Render items.
        mc.mcProfiler.startSection("equippedItems");
        if (te.getDistanceFrom(field_147501_a.field_147560_j, field_147501_a.field_147561_k, field_147501_a.field_147558_l) < ConfigHandler.mannequinMaxEquipmentRenderDistance) {
            renderEquippedItems(te, fakePlayer, model);
        }
        mc.mcProfiler.endSection();
        
        //Post render events
        if (ConfigHandler.mannequinsCallPlayerRenders) {
            if (fakePlayer != null) {
                mc.mcProfiler.startSection("postEvents");
                RenderPlayerEvent.Post postEvent = new RenderPlayerEvent.Post(fakePlayer, renderPlayer, partialTickTime);
                RenderPlayerEvent.Specials.Post postEvenSpecialst = new RenderPlayerEvent.Specials.Post(fakePlayer, renderPlayer, partialTickTime);
                if (model.isChild) {
                    ModelHelper.enableChildModelScale(false, SCALE);
                }
                GL11.glDisable(GL11.GL_CULL_FACE);
                MinecraftForge.EVENT_BUS.post(postEvent);
                MinecraftForge.EVENT_BUS.post(postEvenSpecialst);
                GL11.glEnable(GL11.GL_CULL_FACE);
                if (model.isChild) {
                    ModelHelper.disableChildModelScale();
                }
                mc.mcProfiler.endSection();
            }
        }
        
        mc.mcProfiler.startSection("reset");
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
        mc.mcProfiler.endSection();
        
        GL11.glDisable(GL11.GL_NORMALIZE);
        GL11.glPopAttrib();
        GL11.glPopMatrix();
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
    
    private void renderEquippedItems(IInventory inventory, MannequinFakePlayer fakePlayer, ModelBiped targetBiped) {
        RenderItem ri = (RenderItem) RenderManager.instance.entityRenderMap.get(EntityItem.class);
        MannequinFakePlayer renderEntity = fakePlayer;
        if (renderEntity == null) {
            renderEntity = mannequinFakePlayer;
        }
        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            ItemStack stack = inventory.getStackInSlot(i);
            if (renderEntity != null) {
                if (i == 0 & isHalloweenSeason) {
                    renderEquippedItem(renderEntity, new ItemStack(Blocks.lit_pumpkin), targetBiped, i);
                } else {
                    if (stack != null) {
                        renderEquippedItem(renderEntity, stack, targetBiped, i);
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
    
    private void renderEquippedItem(MannequinFakePlayer fakePlayer, ItemStack stack, ModelBiped targetBiped, int slot) {
        Item targetItem = stack.getItem();
        RenderManager rm = RenderManager.instance;
        
        String[] slotName = {"head", "chest", "legs", "unused", "feet", "rightArm", "leftArm"};
        
        mc.mcProfiler.startSection(slotName[slot]);
        GL11.glPushMatrix();
        if (targetBiped.isChild) {
            ModelHelper.enableChildModelScale(slot == 0, SCALE);
        }
        switch (slot) {
        case 0:
            renderItems.renderHeadStack(fakePlayer, stack, targetBiped, rm);
            break;
        case 1:
            renderItems.renderChestStack(fakePlayer, stack, targetBiped, rm);
            break;
        case 2:
            renderItems.renderLegsStack(fakePlayer, stack, targetBiped, rm);
            break;
        case 4:
            renderItems.renderFeetStack(fakePlayer, stack, targetBiped, rm);
            break;
        case 5:
            renderItems.renderRightArmStack(fakePlayer, stack, targetBiped, rm);
            break;
        case 6:
            renderItems.renderLeftArmStack(fakePlayer, stack, targetBiped, rm);
            break;
        }
        if (targetBiped.isChild) {
            ModelHelper.disableChildModelScale();
        }
        GL11.glPopMatrix();
        mc.mcProfiler.endSection();
    }
    
    @Override
    public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float partialTickTime) {
        renderTileEntityAt((TileEntityMannequin)tileEntity, x, y, z, partialTickTime);
    }
}
