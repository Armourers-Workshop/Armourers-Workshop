package moe.plushie.armourers_workshop.core.skin.data;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import moe.plushie.armourers_workshop.core.api.ISkinPartType;
import moe.plushie.armourers_workshop.core.api.common.skin.ISkinPart;
import moe.plushie.armourers_workshop.core.model.bake.ColouredFace;
import moe.plushie.armourers_workshop.core.model.bake.PackedCubeFace;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import moe.plushie.armourers_workshop.core.skin.data.property.SkinProperties;
import moe.plushie.armourers_workshop.core.utils.Rectangle3D;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;

public class SkinPart implements ISkinPart {

    private Rectangle3D partBounds;
    private Rectangle3D[][][] blockGrid;
    private SkinCubeData cubeData;
    private ArrayList<SkinMarker> markerBlocks;
    private ISkinPartType skinPart;

    private PackedCubeFace packedFaces;

    private SkinProperties properties;

    private Rectangle3D bounds;

    public SkinPart(SkinCubeData cubeData, ISkinPartType skinPart, ArrayList<SkinMarker> markerBlocks) {
        this.cubeData = cubeData;
        this.skinPart = skinPart;
        this.properties = properties;
        this.markerBlocks = markerBlocks;
        this.partBounds = cubeData.getBounds();
    }

    public SkinProperties getProperties() {
        return properties;
    }

    public void setProperties(SkinProperties properties) {
        this.properties = properties;
    }


    public int getModelCount() {
        return 0;
    }

    @OnlyIn(Dist.CLIENT)
    public VoxelShape getRenderShape() {
        if (getType() == SkinPartTypes.ITEM_ARROW) {
            return VoxelShapes.empty();
        }
        return VoxelShapes.box(
                partBounds.getMinX(), partBounds.getMinY(), partBounds.getMinZ(),
                partBounds.getMaxX(), partBounds.getMaxY(), partBounds.getMaxZ());
    }

    @OnlyIn(Dist.CLIENT)
    public PackedCubeFace getPackedFaces() {
        return packedFaces;
    }

    @OnlyIn(Dist.CLIENT)
    public void setPackedFaces(PackedCubeFace packedFaces) {
        this.packedFaces = packedFaces;
    }


    @OnlyIn(Dist.CLIENT)
    public void render(SkinDye dye, int light, MatrixStack matrixStack, IRenderTypeBuffer buffer) {
        packedFaces.forEach((renderType, faces) -> {
            IVertexBuilder builder = buffer.getBuffer(renderType);
            for (ColouredFace face : faces) {
                face.renderVertex(this, dye, matrixStack, builder);
            }
        });
    }


    private void setupPartBounds() {
        if (skinPart == SkinPartTypes.BLOCK || skinPart == SkinPartTypes.BLOCK_MULTI) {
            setupBlockBounds();
        }
    }

    public void setSkinPart(ISkinPartType skinPart) {
        this.skinPart = skinPart;
        setupPartBounds();
    }

    public Rectangle3D getBlockBounds(int x, int y, int z) {
        if (blockGrid != null) {
            x = MathHelper.clamp(x, 0, blockGrid.length);
            y = MathHelper.clamp(y, 0, blockGrid[1].length);
            z = MathHelper.clamp(z, 0, blockGrid[0][1].length);
            return blockGrid[x][y][z];
        }
        return null;
    }

    public Rectangle3D[][][] getBlockGrid() {
        return blockGrid;
    }

    private void setupBlockBounds() {
        blockGrid = new Rectangle3D[3][3][3];
        for (int i = 0; i < cubeData.getCubeCount(); i++) {
            byte[] loc = cubeData.getCubeLocation(i);
            int x = MathHelper.floor((loc[0] + 8) / 16F);
            int y = MathHelper.floor((loc[1] + 8) / 16F);
            int z = MathHelper.floor((loc[2] + 8) / 16F);
            setupBlockBounds(x, y, z, loc[0] - x * 16, loc[1] - y * 16, loc[2] - z * 16);
        }
        for (int ix = 0; ix < 3; ix++) {
            for (int iy = 0; iy < 3; iy++) {
                for (int iz = 0; iz < 3; iz++) {
                    Rectangle3D rec = blockGrid[ix][iy][iz];
                    if (rec != null) {
                        rec.setWidth(rec.getWidth() - rec.getX() + 1);
                        rec.setHeight(rec.getHeight() - rec.getY() + 1);
                        rec.setDepth(rec.getDepth() - rec.getZ() + 1);
                    }
                }
            }
        }
    }

    private void setupBlockBounds(int blockX, int blockY, int blockZ, int x, int y, int z) {
        BlockPos loc = new BlockPos(blockX + 1, -blockY, blockZ);
        if (blockGrid[loc.getX()][loc.getY()][loc.getZ()] == null) {
            blockGrid[loc.getX()][loc.getY()][loc.getZ()] = new Rectangle3D(127, 127, 127, -127, -127, -127);
        }
        Rectangle3D rec = blockGrid[loc.getX()][loc.getY()][loc.getZ()];
        rec.setX(Math.min(rec.getX(), x));
        rec.setY(Math.min(rec.getY(), y));
        rec.setZ(Math.min(rec.getZ(), z));
        rec.setWidth(Math.max(rec.getWidth(), x));
        rec.setHeight(Math.max(rec.getHeight(), y));
        rec.setDepth(Math.max(rec.getDepth(), z));
        // blockGrid[loc.x][loc.y][loc.z] = rec;
    }

    public SkinCubeData getCubeData() {
        return cubeData;
    }

    public void clearCubeData() {
        cubeData = null;
    }

    public Rectangle3D getPartBounds() {
        return partBounds;
    }

    @Override
    public ISkinPartType getType() {
        return this.skinPart;
    }

    @Override
    public List<SkinMarker> getMarkers() {
        return markerBlocks;
    }


    @OnlyIn(Dist.CLIENT)
    public void applyTransform(MatrixStack matrixStack, BipedModel<?> model, Skin skin, int index) {
//        if (skinPart instanceof ISkinRenderAdjustable) {
//            ModelRenderer renderer = ((ISkinRenderAdjustable)skinPart).getModelRenderer(model);
//            matrixStack.translate(renderer.x, renderer.y, renderer.z);
//            matrixStack.mulPose(Vector3f.ZP.rotation(renderer.zRot));
//            matrixStack.mulPose(Vector3f.YP.rotation(renderer.yRot));
//            matrixStack.mulPose(Vector3f.XP.rotation(renderer.xRot));
//        }
//        if (skinPart == SkinPartTypes.BIPED_LEFT_WING || skinPart == SkinPartTypes.BIPED_RIGHT_WING) {
//            SkinMarker marker = getMarkers().get(0);
//            marker.getPosition();
//            marker.getDirection();
//        }
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SkinPart other = (SkinPart) obj;
        if (!toString().equals(other.toString()))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "SkinPart [cubeData=" + cubeData + ", markerBlocks=" + markerBlocks + ", skinPart=" + skinPart.getRegistryName() + "]";
    }
}
