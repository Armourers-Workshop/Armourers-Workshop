package riskyken.armourersWorkshop.client.render.block;

import java.awt.Color;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.common.MinecraftForge;

import org.lwjgl.opengl.GL11;

import riskyken.armourersWorkshop.client.model.ModelMannequin;
import riskyken.armourersWorkshop.client.render.EquipmentModelRenderer;
import riskyken.armourersWorkshop.client.render.MannequinFakePlayer;
import riskyken.armourersWorkshop.common.ApiRegistrar;
import riskyken.armourersWorkshop.common.SkinHelper;
import riskyken.armourersWorkshop.common.inventory.MannequinSlotType;
import riskyken.armourersWorkshop.common.items.ModItems;
import riskyken.armourersWorkshop.common.tileentities.TileEntityMannequin;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderBlockMannequin extends TileEntitySpecialRenderer {
    
    private ModelMannequin model;
    private RenderPlayer renderPlayer;
    private final Minecraft mc;
    private float scale = 0.0625F;
    
    public RenderBlockMannequin() {
        renderPlayer = (RenderPlayer) RenderManager.instance.entityRenderMap.get(EntityPlayer.class);
        mc = Minecraft.getMinecraft();
        model = new ModelMannequin();
    }
    
    @Override
    public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float tickTime) {
        mc.mcProfiler.startSection("armourers mannequin");
        TileEntityMannequin te = (TileEntityMannequin) tileEntity;
        MannequinFakePlayer fakePlayer = te.getFakePlayer();
        
        double heightOffset = te.getHeightOffset();
        //heightOffset = 12;
        ModelBiped targetBiped = renderPlayer.modelBipedMain;
        if (Loader.isModLoaded("moreplayermodels")) {
            targetBiped = model;
        }
        
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_NORMALIZE);
        
        int rotaion = te.getRotation();
        
        GL11.glTranslated(x + 0.5D, y + 1.5D, z + 0.5D);
        GL11.glScalef(scale * 15, scale * 15, scale * 15);
        GL11.glTranslated(0, scale * -1.6F, 0);
        
        GL11.glScalef(-1, -1, 1);
        GL11.glRotatef(rotaion * 22.5F, 0, 1, 0);
        
        if (te.getIsDoll()) {
            float dollScale = 0.5F;
            GL11.glScalef(dollScale, dollScale, dollScale);
            GL11.glTranslatef(0, scale * 24, 0);
        }
        
        if (te.getGameProfile() != null) {
            if (te.getGameProfile() != null & te.getWorldObj() != null) {
                if (fakePlayer == null) {
                    fakePlayer = new MannequinFakePlayer(te.getWorldObj(),te.getGameProfile());
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
        
        float f6 = 2.0F;
        if (fakePlayer != null) {
            targetBiped.isChild = te.getBipedRotations().isChild;
            fakePlayer.rotationPitch = (float) Math.toDegrees(te.getBipedRotations().head.rotationX);
            fakePlayer.prevRotationPitch = (float) Math.toDegrees(te.getBipedRotations().head.rotationX);
            fakePlayer.rotationYawHead = (float) Math.toDegrees(te.getBipedRotations().head.rotationY);
            fakePlayer.prevRotationYawHead = (float) Math.toDegrees(te.getBipedRotations().head.rotationY);
            fakePlayer.onUpdate();
            
            fakePlayer.ticksExisted = Minecraft.getMinecraft().thePlayer.ticksExisted;
            
            if (te.getBipedRotations() != null) {
                te.getBipedRotations().applyRotationsToBiped(targetBiped);
                te.getBipedRotations().applyRotationsToBiped(renderPlayer.modelArmor);
                te.getBipedRotations().applyRotationsToBiped(renderPlayer.modelArmorChestplate);
            }
            
            //Pre render events
            RenderPlayerEvent.Pre preEvent = new RenderPlayerEvent.Pre(fakePlayer, renderPlayer, tickTime);
            RenderPlayerEvent.Specials.Pre preEventSpecials = new RenderPlayerEvent.Specials.Pre(fakePlayer, renderPlayer, tickTime);

            if (targetBiped.isChild) {
                GL11.glPushMatrix();
                GL11.glScalef(1.5F / f6, 1.5F / f6, 1.5F / f6);
                GL11.glTranslatef(0.0F, 16.0F * scale, 0.0F);
            }
            GL11.glDisable(GL11.GL_CULL_FACE);
            MinecraftForge.EVENT_BUS.post(preEvent);
            MinecraftForge.EVENT_BUS.post(preEventSpecials);
            GL11.glEnable(GL11.GL_CULL_FACE);
            if (targetBiped.isChild) {
                GL11.glPopMatrix();
            }
        }
        
        SkinHelper.bindPlayersNormalSkin(te.getGameProfile());
        
        ApiRegistrar.INSTANCE.onRenderMannequin(tileEntity, te.getGameProfile());
        
        targetBiped.bipedRightArm.setRotationPoint(-5.0F, 2.0F , 0.0F);
        targetBiped.bipedLeftArm.setRotationPoint(5.0F, 2.0F , 0.0F);
        targetBiped.bipedHead.setRotationPoint(0.0F, 0.0F, 0.0F);
        targetBiped.bipedHeadwear.setRotationPoint(0.0F, 0.0F, 0.0F);
        targetBiped.bipedRightLeg.setRotationPoint(-1.9F, 12.0F, 0.0F);
        targetBiped.bipedLeftLeg.setRotationPoint(1.9F, 12.0F, 0.0F);
        
        te.getBipedRotations().applyRotationsToBiped(targetBiped);
        
        if (te.getBipedRotations().isChild) {
            GL11.glTranslated(0, heightOffset * scale * 1.0F / f6, 0);
            if (!hasCustomHead(te, fakePlayer)) {
                GL11.glPushMatrix();
                GL11.glScalef(1.5F / f6, 1.5F / f6, 1.5F / f6);
                GL11.glTranslatef(0.0F, 16.0F * scale, 0.0F);
                targetBiped.bipedHead.render(scale);
                GL11.glDisable(GL11.GL_CULL_FACE);
                targetBiped.bipedHeadwear.render(scale);
                GL11.glEnable(GL11.GL_CULL_FACE);
                GL11.glPopMatrix();
            }
            GL11.glPushMatrix();
            GL11.glScalef(1.0F / f6, 1.0F / f6, 1.0F / f6);
            GL11.glTranslatef(0.0F, 24.0F * scale, 0.0F);
            targetBiped.bipedBody.render(scale);
            targetBiped.bipedRightArm.render(scale);
            targetBiped.bipedLeftArm.render(scale);
            targetBiped.bipedRightLeg.render(scale);
            targetBiped.bipedLeftLeg.render(scale);
            GL11.glPopMatrix();
        } else {
            GL11.glTranslated(0, heightOffset * scale, 0);
            if (!hasCustomHead(te, fakePlayer)) {
                targetBiped.bipedHead.render(scale);
                GL11.glDisable(GL11.GL_CULL_FACE);
                targetBiped.bipedHeadwear.render(scale);
                GL11.glEnable(GL11.GL_CULL_FACE);
            }
            targetBiped.bipedBody.render(scale);
            targetBiped.bipedRightArm.render(scale);
            targetBiped.bipedLeftArm.render(scale);
            targetBiped.bipedRightLeg.render(scale);
            targetBiped.bipedLeftLeg.render(scale);
        }
        
        //Post render events
        if (fakePlayer != null) {
            RenderPlayerEvent.Post postEvent = new RenderPlayerEvent.Post(fakePlayer, renderPlayer, tickTime);
            RenderPlayerEvent.Specials.Post postEvenSpecialst = new RenderPlayerEvent.Specials.Post(fakePlayer, renderPlayer, tickTime);
            if (targetBiped.isChild) {
                GL11.glPushMatrix();
                GL11.glScalef(1.5F / f6, 1.5F / f6, 1.5F / f6);
                GL11.glTranslatef(0.0F, 16.0F * scale, 0.0F);
            }
            GL11.glDisable(GL11.GL_CULL_FACE);
            MinecraftForge.EVENT_BUS.post(postEvent);
            MinecraftForge.EVENT_BUS.post(postEvenSpecialst);
            GL11.glEnable(GL11.GL_CULL_FACE);
            if (targetBiped.isChild) {
                GL11.glPopMatrix();
            }
        }
        
        //Render armourer's skins
        mc.mcProfiler.startSection("mannequin skin");
        EquipmentModelRenderer.INSTANCE.renderMannequinEquipment(((TileEntityMannequin)tileEntity), targetBiped);
        mc.mcProfiler.endSection();
        
        //Render items.
        renderEquippedItems(te, fakePlayer, targetBiped);
        
        targetBiped.bipedLeftLeg.rotateAngleZ = 0F;
        targetBiped.bipedRightLeg.rotateAngleZ = 0F;
        targetBiped.bipedHead.rotateAngleZ = 0F;
        targetBiped.bipedHeadwear.rotateAngleZ = 0F;
        
        GL11.glDisable(GL11.GL_NORMALIZE);
        GL11.glPopMatrix();
        mc.mcProfiler.endSection();
    }
    
    private void renderEquippedItems(IInventory inventory, MannequinFakePlayer fakePlayer, ModelBiped targetBiped) {
        RenderItem ri = (RenderItem) RenderManager.instance.entityRenderMap.get(EntityItem.class);
        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            ItemStack stack = inventory.getStackInSlot(i);
            if (stack != null & fakePlayer != null) {
                renderEquippedItem(fakePlayer, stack, targetBiped, i);
            }
        }
    }
    
    public ItemStack getStackInMannequinSlot(IInventory inventory, MannequinSlotType slot) {
        return inventory.getStackInSlot(slot.ordinal());
    }
    
    private boolean hasCustomHead(IInventory inventory, MannequinFakePlayer fakePlayer) {
        ItemStack stack = getStackInMannequinSlot(inventory, MannequinSlotType.HEAD);
        if (stack != null & fakePlayer != null) {
            if (stack.getItem() instanceof ItemBlock) {
                return true;
            }
        }
        return false;
    }
    
    private void renderEquippedItem(MannequinFakePlayer fakePlayer, ItemStack stack, ModelBiped targetBiped, int slot) {
        if (slot < 6) {
            if (stack.getItem() == ModItems.equipmentSkin) {
                return;
            }
        }
        
        Item targetItem = stack.getItem();
        RenderManager rm = RenderManager.instance;
        
        GL11.glPushMatrix();
        switch (slot) {
        case 0:
            renderHeadStack(fakePlayer, stack, targetBiped, rm);
            break;
        case 1:
            renderChestStack(fakePlayer, stack, targetBiped, rm);
            break;
        case 2:
            renderLegsStack(fakePlayer, stack, targetBiped, rm);
            break;
        case 4:
            renderFeetStack(fakePlayer, stack, targetBiped, rm);
            break;
        case 5:
            renderRightArmStack(fakePlayer, stack, targetBiped, rm);
            break;
        case 6:
            renderLeftArmStack(fakePlayer, stack, targetBiped, rm);
            break;
        }
        GL11.glPopMatrix();
    }
    
    private void renderHeadStack(MannequinFakePlayer fakePlayer, ItemStack stack, ModelBiped targetBiped, RenderManager rm) {
        Item targetItem = stack.getItem();
        if (targetItem instanceof ItemBlock) {
            float blockScale = 0.5F;
            GL11.glTranslatef(0, -4 * scale, 0);
            GL11.glScalef(-blockScale, -blockScale, blockScale);
            GL11.glRotatef(90F, 0F, 1F, 0F);
            rm.itemRenderer.renderItem(fakePlayer, stack, 0);
        } else {
            if (targetItem instanceof ItemArmor) {
                int passes = targetItem.getRenderPasses(stack.getItemDamage());
                for (int i = 0; i < passes; i++) {
                    ModelBiped armourBiped = ForgeHooksClient.getArmorModel(fakePlayer, stack, 1, renderPlayer.modelArmorChestplate);
                    if (i == 0) {
                        bindTexture(RenderBiped.getArmorResource(fakePlayer, stack, 0, null));
                    } else {
                        bindTexture(RenderBiped.getArmorResource(fakePlayer, stack, 0, "overlay"));
                    }
                    
                    Color c = new Color(targetItem.getColorFromItemStack(stack, i));
                    GL11.glColor3f((float)c.getRed() / 255, (float)c.getGreen() / 255, (float)c.getBlue() / 255);
                    if (armourBiped == renderPlayer.modelArmorChestplate) {
                        armourBiped.bipedHead.showModel = true;
                        armourBiped.bipedHead.render(scale);
                    } else {
                        armourBiped.render(fakePlayer, 0, 0, 0, 0, 0, scale);
                    }
                }
            }
        }
    }
    
    private void renderChestStack(MannequinFakePlayer fakePlayer, ItemStack stack, ModelBiped targetBiped, RenderManager rm) {
        Item targetItem = stack.getItem();
        if (targetItem instanceof ItemArmor) {
            int passes = targetItem.getRenderPasses(stack.getItemDamage());
            for (int i = 0; i < passes; i++) {
                ModelBiped armourBiped = ForgeHooksClient.getArmorModel(fakePlayer, stack, 1, renderPlayer.modelArmorChestplate);
                if (i == 0) {
                    bindTexture(RenderBiped.getArmorResource(fakePlayer, stack, 1, null));
                } else {
                    bindTexture(RenderBiped.getArmorResource(fakePlayer, stack, 1, "overlay"));
                }
                
                Color c = new Color(targetItem.getColorFromItemStack(stack, i));
                GL11.glColor3f((float)c.getRed() / 255, (float)c.getGreen() / 255, (float)c.getBlue() / 255);
                
                if (armourBiped == renderPlayer.modelArmorChestplate) {
                    armourBiped.bipedBody.showModel = true;
                    armourBiped.bipedLeftArm.showModel = true;
                    armourBiped.bipedRightArm.showModel = true;
                    
                    armourBiped.bipedBody.render(scale);
                    armourBiped.bipedLeftArm.render(scale);
                    armourBiped.bipedRightArm.render(scale);
                    
                    armourBiped = ForgeHooksClient.getArmorModel(fakePlayer, stack, 1, renderPlayer.modelArmor);
                    armourBiped.bipedBody.showModel = true;
                    armourBiped.bipedBody.render(scale);
                } else {
                    armourBiped.render(fakePlayer, 0, 0, 0, 0, 0, scale);
                }
            }
        }
    }
    private void renderLegsStack(MannequinFakePlayer fakePlayer, ItemStack stack, ModelBiped targetBiped, RenderManager rm) {
        Item targetItem = stack.getItem();
        if (targetItem instanceof ItemArmor) {
            int passes = targetItem.getRenderPasses(stack.getItemDamage());
            for (int i = 0; i < passes; i++) {
                ModelBiped armourBiped = ForgeHooksClient.getArmorModel(fakePlayer, stack, 2, renderPlayer.modelArmor);
                if (i == 0) {
                    bindTexture(RenderBiped.getArmorResource(fakePlayer, stack, 2, null));
                } else {
                    bindTexture(RenderBiped.getArmorResource(fakePlayer, stack, 2, "overlay"));
                }
                
                Color c = new Color(targetItem.getColorFromItemStack(stack, i));
                GL11.glColor3f((float)c.getRed() / 255, (float)c.getGreen() / 255, (float)c.getBlue() / 255);
                
                if (armourBiped == renderPlayer.modelArmor) {
                    armourBiped.bipedBody.showModel = true;
                    armourBiped.bipedLeftLeg.showModel = true;
                    armourBiped.bipedRightLeg.showModel = true;
                    armourBiped.bipedBody.render(scale);
                    armourBiped.bipedLeftLeg.render(scale);
                    armourBiped.bipedRightLeg.render(scale);
                } else {
                    armourBiped.render(fakePlayer, 0, 0, 0, 0, 0, scale);
                }
            }
        }
    }
    private void renderFeetStack(MannequinFakePlayer fakePlayer, ItemStack stack, ModelBiped targetBiped, RenderManager rm) {
        Item targetItem = stack.getItem();
        if (targetItem instanceof ItemArmor) {
            int passes = targetItem.getRenderPasses(stack.getItemDamage());
            for (int i = 0; i < passes; i++) {
                ModelBiped armourBiped = ForgeHooksClient.getArmorModel(fakePlayer, stack, 3, renderPlayer.modelArmorChestplate);
                if (i == 0) {
                    bindTexture(RenderBiped.getArmorResource(fakePlayer, stack, 3, null));
                } else {
                    bindTexture(RenderBiped.getArmorResource(fakePlayer, stack, 3, "overlay"));
                }
                
                Color c = new Color(targetItem.getColorFromItemStack(stack, i));
                GL11.glColor3f((float)c.getRed() / 255, (float)c.getGreen() / 255, (float)c.getBlue() / 255);
                
                if (armourBiped == renderPlayer.modelArmorChestplate) {
                    armourBiped.bipedLeftLeg.showModel = true;
                    armourBiped.bipedRightLeg.showModel = true;
                    armourBiped.bipedLeftLeg.render(scale);
                    armourBiped.bipedRightLeg.render(scale);
                } else {
                    armourBiped.render(fakePlayer, 0, 0, 0, 0, 0, scale);
                }
            }
        }
    }
    
    private void renderRightArmStack(MannequinFakePlayer fakePlayer, ItemStack stack, ModelBiped targetBiped, RenderManager rm) {
        Item targetItem = stack.getItem();
        float blockScale = 0.5F;
        float itemScale = 1 - (float)1 / 3;
        Tessellator tessellator = Tessellator.instance;
        
        if (targetItem instanceof ItemBlock) {
            GL11.glTranslatef(0, -4 * scale, 0);
            GL11.glScalef(-blockScale, -blockScale, blockScale);
            GL11.glRotatef(90F, 0F, 1F, 0F);
        } else {
            //Movement
            GL11.glTranslatef(-5F * scale, 0F, 0F);
            GL11.glTranslatef(0F, 2F * scale, 0F);
            
            GL11.glRotated(Math.toDegrees(targetBiped.bipedRightArm.rotateAngleZ), 0F, 0F, 1F);
            GL11.glRotated(Math.toDegrees(targetBiped.bipedRightArm.rotateAngleY), 0F, 1F, 0F);
            GL11.glRotated(Math.toDegrees(targetBiped.bipedRightArm.rotateAngleX), 1F, 0F, 0F);
            
            GL11.glTranslatef(-2F * scale, 0F, 0F);
            GL11.glTranslatef(0F, 10F * scale, 0F);
            
            GL11.glRotatef(-90, 0, 1, 0);
            GL11.glRotatef(45, 0, 0, 1);
            
            GL11.glScalef(itemScale, itemScale, itemScale);
            GL11.glRotatef(-335.0F, 0.0F, 0.0F, 1.0F);
            GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
        }
        
        rm.itemRenderer.renderItem(fakePlayer, stack, 0, ItemRenderType.EQUIPPED);
    }
    
    private void renderLeftArmStack(MannequinFakePlayer fakePlayer, ItemStack stack, ModelBiped targetBiped, RenderManager rm) {
        Item targetItem = stack.getItem();
        float blockScale = 0.5F;
        float itemScale = 1 - (float)1 / 3;
        
        if (targetItem instanceof ItemBlock) {
            GL11.glTranslatef(0, -4 * scale, 0);
            GL11.glScalef(-blockScale, -blockScale, blockScale);
            GL11.glRotatef(90F, 0F, 1F, 0F);
        } else {
            //Movement
            GL11.glTranslatef(5F * scale, 0F, 0F);
            GL11.glTranslatef(0F, 2F * scale, 0F);
            
            GL11.glRotated(Math.toDegrees(targetBiped.bipedLeftArm.rotateAngleZ), 0F, 0F, 1F);
            GL11.glRotated(Math.toDegrees(targetBiped.bipedLeftArm.rotateAngleY), 0F, 1F, 0F);
            GL11.glRotated(Math.toDegrees(targetBiped.bipedLeftArm.rotateAngleX), 1F, 0F, 0F);
            
            GL11.glTranslatef(1F * scale, 0F, 0F);
            GL11.glTranslatef(0F, 10F * scale, 0F);
            
            GL11.glRotatef(-90, 0, 1, 0);
            GL11.glRotatef(45, 0, 0, 1);
            
            GL11.glScalef(itemScale, itemScale, itemScale);
            GL11.glRotatef(-335.0F, 0.0F, 0.0F, 1.0F);
            GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
        }

        rm.itemRenderer.renderItem(fakePlayer, stack, 0, ItemRenderType.EQUIPPED);
    }
}
