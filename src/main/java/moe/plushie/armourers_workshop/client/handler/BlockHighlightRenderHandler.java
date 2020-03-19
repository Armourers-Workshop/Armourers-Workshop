package moe.plushie.armourers_workshop.client.handler;

import moe.plushie.armourers_workshop.api.common.skin.data.ISkinDescriptor;
import moe.plushie.armourers_workshop.client.skin.cache.ClientSkinCache;
import moe.plushie.armourers_workshop.common.init.items.ModItems;
import moe.plushie.armourers_workshop.common.skin.data.Skin;
import moe.plushie.armourers_workshop.common.skin.type.SkinTypeRegistry;
import moe.plushie.armourers_workshop.common.tileentities.TileEntitySkinnable;
import moe.plushie.armourers_workshop.utils.SkinNBTHelper;
import moe.plushie.armourers_workshop.utils.PlayerUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class BlockHighlightRenderHandler {

    public BlockHighlightRenderHandler() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onDrawBlockHighlightEvent(DrawBlockHighlightEvent event) {
        EntityPlayer player = event.getPlayer();
        World world = event.getPlayer().getEntityWorld();
        RayTraceResult target = event.getTarget();

        if (target != null && target.typeOfHit != RayTraceResult.Type.BLOCK) {
            return;
        }

        BlockPos pos = target.getBlockPos();
        EnumFacing facing = target.sideHit;
        IBlockState state = world.getBlockState(pos);
        ItemStack stack = player.getHeldItemMainhand();

        if (stack.getItem()  == ModItems.SKIN) {
            ISkinDescriptor descriptor = SkinNBTHelper.getSkinDescriptorFromStack(stack);

            if (descriptor != null && descriptor.getIdentifier().getSkinType() == SkinTypeRegistry.skinBlock) {
                drawSkinnableBlockHelper(world, pos.offset(facing), facing, player, event.getPartialTicks(), descriptor);
            }
        }
    }
    
    private void drawSkinnableBlockHelper(World world, BlockPos pos, EnumFacing facing, EntityPlayer player, float partialTicks, ISkinDescriptor descriptor) {
        //int meta = world.getBlockMetadata(x, y, z);
        
        //Rectangle3D[][][] blockGrid;
        Skin skin = ClientSkinCache.INSTANCE.getSkin(descriptor, false);
        if (skin == null) {
            return;
        }
        if (skin.getSkinType() != SkinTypeRegistry.skinBlock) {
            return;
        }
         
        //blockGrid = skin.getParts().get(0).getBlockGrid();
        
        double xOff = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks;
        double yOff = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks;
        double zOff = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks;
        
        float f1 = 0.002F;
        float scale = 0.0625F;
        
        EnumFacing dir = PlayerUtils.getDirectionSide(player).getOpposite();
        
        for (int ix = 0; ix < 3; ix++) {
            for (int iy = 0; iy < 3; iy++) {
                for (int iz = 0; iz < 3; iz++) {
                    float[] bounds = TileEntitySkinnable.getBlockBounds(skin, -ix + 2, iy, iz, dir);
                    if (bounds != null) {
                        double minX = bounds[0];
                        double minY = bounds[1];
                        double minZ = bounds[2];
                        double maxX = bounds[3];
                        double maxY = bounds[4];
                        double maxZ = bounds[5];
                        
                        AxisAlignedBB aabb = new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ);
                        aabb = aabb.offset(-xOff - 1, -yOff, -zOff - 1);
                        aabb = aabb.offset(dir.getXOffset() * -1, 0, dir.getZOffset() * -1);
                        aabb = aabb.offset(pos);
                        aabb = aabb.offset(ix, iy, iz);

                        BlockPos target = pos.add(ix - 1 - dir.getXOffset(), 0, iz - 1 - dir.getZOffset());
                        boolean blocked = false;
                        if (!world.isAirBlock(target)) {
                            blocked = true;
                        }
                        GlStateManager.enableBlend();
                        GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
                        GlStateManager.glLineWidth(1F);
                        GlStateManager.disableDepth();
                        GlStateManager.disableTexture2D();
                        GlStateManager.disableAlpha();
                        if (!blocked) {
                            RenderGlobal.drawSelectionBoundingBox(aabb.contract(f1, f1, f1), 1F, 1F, 1F, 0.75F);
                        } else {
                            RenderGlobal.drawSelectionBoundingBox(aabb.contract(f1, f1, f1), 1F, 0F, 0F, 0.75F);
                        }
                        GlStateManager.enableAlpha();
                        GlStateManager.enableTexture2D();
                        GlStateManager.enableDepth();
                        GlStateManager.disableBlend();;
                    }
                }
            }
        }
    }

    /*@SubscribeEvent
    public void onRenderGameOverlayEvent(RenderGameOverlayEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer player = mc.thePlayer;
        World world = player.worldObj;
        if (player.getCurrentEquippedItem() == null || player.getCurrentEquippedItem().getItem() != ModItems.debugTool) {
            return;
        }

        if (event.type != ElementType.TEXT) {
            return;
        }

        RayTraceResult target = Minecraft.getMinecraft().objectMouseOver;

        if (target != null && target.typeOfHit != RayTraceResult.Type.BLOCK) {
            return;
        }
        int x = target.blockX;
        int y = target.blockY;
        int z = target.blockZ;

        Block block = world.getBlock(x, y, z);

        FontRenderer fontRenderer = mc.fontRenderer;

        ArrayList<String> textLines = new ArrayList<String>();
        textLines.add("name: " + block.getLocalizedName());
        textLines.add("meta: " + world.getBlockMetadata(x, y, z));

        if (block instanceof IDebug) {
            IDebug debug = (IDebug) block;
            debug.getDebugHoverText(world, x, y, z, textLines);
        }
        int centerX = event.resolution.getScaledWidth() / 2;
        int centerY = event.resolution.getScaledHeight() / 2;

        int longestLine = 0;

        for (int i = 0; i < textLines.size(); i++) {
            int sWidth = fontRenderer.getStringWidth(textLines.get(i));
            longestLine = Math.max(longestLine, sWidth);
        }

        for (int i = 0; i < textLines.size(); i++) {
            fontRenderer.drawStringWithShadow(textLines.get(i), centerX - longestLine / 2, 5 + fontRenderer.FONT_HEIGHT * i, 0xFFFFFFFF);
        }
    }*/
}
