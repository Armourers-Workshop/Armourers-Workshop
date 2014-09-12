package riskyken.armourersWorkshop.client.model;

import java.util.ArrayList;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

import riskyken.armourersWorkshop.common.custom.equipment.armour.ArmourType;
import riskyken.armourersWorkshop.common.custom.equipment.data.CustomArmourItemData;
import riskyken.armourersWorkshop.common.custom.equipment.data.CustomArmourPartData;
import riskyken.armourersWorkshop.common.custom.equipment.data.CustomEquipmentBlockData;
import riskyken.armourersWorkshop.common.lib.LibModInfo;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelCustomItemBuilt extends ModelBiped {
    
    private static final ResourceLocation texture = new ResourceLocation(LibModInfo.ID.toLowerCase(), "textures/armour/cube.png");
    private final ModelRenderer main;
    ArrayList<CustomEquipmentBlockData> blocks = new ArrayList<CustomEquipmentBlockData>();
            
    private static float scale = 0.0625F;
    
    public static void bindArmourTexture() {
        Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
    }
    
    public ModelCustomItemBuilt(CustomArmourItemData itemData, ArmourType armourType) {
        textureWidth = 4;
        textureHeight = 4;
        
        main = new ModelRenderer(this, 0, 0);
        main.addBox(0F, 0F, 0F, 1, 1, 1);
        main.setRotationPoint(0, 0, 0);
        
        ArrayList<CustomArmourPartData> parts = itemData.getParts();
        
        for (int i = 0; i < parts.size(); i++) {
            loadPart(parts.get(i));
        }
    }
    
    private void loadPart(CustomArmourPartData part) {
        ArrayList<CustomEquipmentBlockData> partBlocks = part.getArmourData();
        for (int i = 0; i < partBlocks.size(); i++) {
            CustomEquipmentBlockData blockData = partBlocks.get(i);
            if (blockCanBeSeen(partBlocks, blockData)) {
                    
                switch (part.getArmourPart()) {
                case LEFT_ARM:
                    blockData.x += 7;
                    blockData.y += 2;
                    break;
                case RIGHT_ARM:
                    blockData.x -= 7;
                    blockData.y += 2;
                    break;
                case LEFT_LEG:
                    blockData.x -= 4;
                    break;
                case RIGHT_LEG:
                    blockData.x += 4;
                    break;
                case LEFT_FOOT:
                    blockData.x -= 4;
                    break;
                case RIGHT_FOOT:
                    blockData.x += 4;
                    break;
                default:
                    break;
                }
                
                blocks.add(partBlocks.get(i));
            }
        }
    }
    
    private boolean blockCanBeSeen(ArrayList<CustomEquipmentBlockData> partBlocks, CustomEquipmentBlockData block) {
        int sidesCovered = 0;
        for (int i = 0; i < ForgeDirection.VALID_DIRECTIONS.length; i++) {
            ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
            for (int j = 0; j < partBlocks.size(); j++) {
                CustomEquipmentBlockData checkBlock = partBlocks.get(j);
                if (block.x + dir.offsetX == checkBlock.x &&
                        block.y + dir.offsetY == checkBlock.y &&
                        block.z + dir.offsetZ == checkBlock.z)
                {
                    sidesCovered++;
                    break;
                }
            }
        }
        return sidesCovered < 6;
    }

    public void render() {
        //ModLogger.log(blocks.size());
        bindArmourTexture();
        GL11.glPushMatrix();
        for (int i = 0; i < blocks.size(); i++) {
            CustomEquipmentBlockData blockData = blocks.get(i);
            if (!blockData.isGlowing()) {
                renderArmourBlock(blockData.x, blockData.y, blockData.z, blockData.colour, scale);
            }
        }
        
        float lastBrightnessX = OpenGlHelper.lastBrightnessX;
        float lastBrightnessY = OpenGlHelper.lastBrightnessY;
        GL11.glDisable(GL11.GL_LIGHTING);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240f, 240f);
        
        for (int i = 0; i < blocks.size(); i++) {
            CustomEquipmentBlockData blockData = blocks.get(i);
            if (blockData.isGlowing()) {
                renderArmourBlock(blockData.x, blockData.y, blockData.z, blockData.colour, scale);
            }
        }
        
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lastBrightnessX, lastBrightnessY);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glPopMatrix();
    }
    
    public void renderArmourBlock(int x, int y, int z, int colour, float scale) {
        float colourRed = (colour >> 16 & 0xff) / 255F;
        float colourGreen = (colour >> 8 & 0xff) / 255F;
        float colourBlue = (colour & 0xff) / 255F;

        GL11.glPushMatrix();
        GL11.glColor3f(colourRed, colourGreen, colourBlue);
        GL11.glTranslated(x * scale, y * scale, z * scale);
        main.render(scale);
        GL11.glPopMatrix();
    }
}
