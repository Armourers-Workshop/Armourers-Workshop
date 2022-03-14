package moe.plushie.armourers_workshop.core.tileentity;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import moe.plushie.armourers_workshop.core.AWConstants;
import moe.plushie.armourers_workshop.core.base.AWTileEntities;
import moe.plushie.armourers_workshop.core.block.SkinnableBlock;
import moe.plushie.armourers_workshop.core.render.bake.BakedSkin;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.core.utils.AWDataSerializers;
import moe.plushie.armourers_workshop.core.utils.Rectangle3f;
import moe.plushie.armourers_workshop.core.utils.Rectangle3i;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.properties.AttachFace;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.ArrayList;

public class SkinnableTileEntity extends RotableTileEntity {

    private static final BlockPos INVALID = BlockPos.of(-1);

    private static final ImmutableMap<?, Vector3f> FACING_TO_ROT = new ImmutableMap.Builder<Object, Vector3f>()
            .put(Pair.of(AttachFace.CEILING, Direction.EAST), new Vector3f(180, 90, 0))
            .put(Pair.of(AttachFace.CEILING, Direction.NORTH), new Vector3f(180, 0, 0))
            .put(Pair.of(AttachFace.CEILING, Direction.WEST), new Vector3f(180, 270, 0))
            .put(Pair.of(AttachFace.CEILING, Direction.SOUTH), new Vector3f(180, 180, 0))
            .put(Pair.of(AttachFace.WALL, Direction.EAST), new Vector3f(0, 270, 0))
            .put(Pair.of(AttachFace.WALL, Direction.SOUTH), new Vector3f(0, 180, 0))
            .put(Pair.of(AttachFace.WALL, Direction.WEST), new Vector3f(0, 90, 0))
            .put(Pair.of(AttachFace.WALL, Direction.NORTH), new Vector3f(0, 0, 0))
            .put(Pair.of(AttachFace.FLOOR, Direction.EAST), new Vector3f(0, 90, 0))
            .put(Pair.of(AttachFace.FLOOR, Direction.SOUTH), new Vector3f(0, 0, 0))
            .put(Pair.of(AttachFace.FLOOR, Direction.WEST), new Vector3f(0, 270, 0))
            .put(Pair.of(AttachFace.FLOOR, Direction.NORTH), new Vector3f(0, 180, 0))
            .build();

//            "face=wall,facing=east": {
//        "model": "armourers_workshop:block/hologram-projector",
//                "x": 90,
//                "y": 90
//    },
//            "face=wall,facing=north": {
//        "model": "armourers_workshop:block/hologram-projector",
//                "x": 90
//    },
//            "face=wall,facing=south": {
//        "model": "armourers_workshop:block/hologram-projector",
//                "x": 90,
//                "y": 180
//    },
//            "face=wall,facing=west": {
//        "model": "armourers_workshop:block/hologram-projector",
//                "x": 90,
//                "y": 270
//    }

    private BlockPos refer = INVALID;
    private ArrayList<BlockPos> refers = new ArrayList<>();

    private Rectangle3i shape = Rectangle3i.ZERO;
    private SkinDescriptor descriptor = SkinDescriptor.EMPTY;

    private Quaternion renderRotations;
    private AxisAlignedBB renderBoundingBox;

    public SkinnableTileEntity() {
        super(AWTileEntities.SKINNABLE);
    }

    public static Quaternion getRotations(BlockState state) {
        AttachFace face = state.getValue(SkinnableBlock.FACE);
        Direction facing = state.getValue(SkinnableBlock.FACING);
        Vector3f rot = FACING_TO_ROT.getOrDefault(Pair.of(face, facing), new Vector3f());
        return new Quaternion(rot.x(), rot.y(), rot.z(), true);
    }

    @Override
    public void readFromNBT(CompoundNBT nbt) {
        refer = AWDataSerializers.getBlockPos(nbt, AWConstants.NBT.TILE_ENTITY_REFER, INVALID);
        refers = AWDataSerializers.getBlockPosList(nbt, AWConstants.NBT.TILE_ENTITY_REFERS);
        descriptor = AWDataSerializers.getSkinDescriptor(nbt, AWConstants.NBT.TILE_ENTITY_SKIN, SkinDescriptor.EMPTY);
        shape = AWDataSerializers.getRectangle3i(nbt, AWConstants.NBT.TILE_ENTITY_SHAPE, Rectangle3i.ZERO);
    }

    @Override
    public void writeToNBT(CompoundNBT nbt) {
        AWDataSerializers.putBlockPos(nbt, AWConstants.NBT.TILE_ENTITY_REFER, refer, INVALID);
        AWDataSerializers.putBlockPosList(nbt, AWConstants.NBT.TILE_ENTITY_REFERS, refers);
        AWDataSerializers.putSkinDescriptor(nbt, AWConstants.NBT.TILE_ENTITY_SKIN, descriptor, SkinDescriptor.EMPTY);
        AWDataSerializers.putRectangle3i(nbt, AWConstants.NBT.TILE_ENTITY_SHAPE, shape, Rectangle3i.ZERO);
    }

    public void updateBlockStates() {
        setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 2);
        }
    }

    public void setRefer(BlockPos refer) {
        this.refer = refer;
    }

    public SkinDescriptor getDescriptor() {
        if (BlockPos.ZERO.equals(refer)) {
            return descriptor;
        }
        return SkinDescriptor.EMPTY;
    }

    public void setDescriptor(SkinDescriptor descriptor) {
        this.descriptor = descriptor;
    }

    public VoxelShape getShape() {
        if (!shape.equals(Rectangle3i.ZERO)) {
            float minX = shape.getMinX() / 16f + 0.5f;
            float minY = shape.getMinY() / 16f + 0.5f;
            float minZ = shape.getMinZ() / 16f + 0.5f;
            float maxX = shape.getMaxX() / 16f + 0.5f;
            float maxY = shape.getMaxY() / 16f + 0.5f;
            float maxZ = shape.getMaxZ() / 16f + 0.5f;
            return VoxelShapes.box(minX, minY, minZ, maxX, maxY, maxZ);
        }
        return VoxelShapes.block();
    }

    public void setShape(Rectangle3i shape) {
        this.shape = shape;
    }

    @Override
    public Quaternion getRenderRotations() {
        if (renderRotations != null) {
            return renderRotations;
        }
        renderRotations = getRotations(getBlockState());
        return renderRotations;
    }

    @Nullable
    public TileEntity getParent() {
        if (BlockPos.ZERO.equals(refer)) {
            return this;
        }
        if (getLevel() != null && refer != INVALID) {
            return getLevel().getBlockEntity(getBlockPos().subtract(refer));
        }
        return null;
    }

    public ArrayList<BlockPos> getRefers() {
        return refers;
    }

    public void setRefers(ArrayList<BlockPos> refers) {
        this.refers = refers;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public Rectangle3f getRenderBoundingBox(BlockState state) {
        BakedSkin bakedSkin = BakedSkin.of(getDescriptor());
        if (bakedSkin == null) {
            return null;
        }
        float f = 1 / 16f;
        Rectangle3f box = bakedSkin.getRenderBounds(null, null, null).copy();
        box.mul(Matrix4f.createScaleMatrix(f, -f, f));
        return box;
    }
}
