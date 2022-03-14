package moe.plushie.armourers_workshop.core.utils;

import moe.plushie.armourers_workshop.core.AWConstants;
import moe.plushie.armourers_workshop.core.base.AWBlocks;
import moe.plushie.armourers_workshop.core.skin.Skin;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.core.skin.SkinLoader;
import moe.plushie.armourers_workshop.core.tileentity.SkinnableTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector4f;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;

public class SkinItemUseContext extends BlockItemUseContext {

    private Quaternion rotations = Quaternion.ONE;
    private SkinDescriptor skin = SkinDescriptor.EMPTY;
    private ArrayList<Part> parts = new ArrayList<>();

    public SkinItemUseContext(ItemUseContext context) {
        super(context);
        this.loadElements(true);
    }

    public SkinItemUseContext(PlayerEntity player, Hand hand, ItemStack itemStack, BlockRayTraceResult traceResult, boolean dir) {
        super(player.level, player, hand, itemStack, traceResult);
        this.loadElements(dir);
    }


    public static SkinItemUseContext of(BlockPos pos) {
        if (pos instanceof AttachedBlockPos) {
            return ((AttachedBlockPos) pos).context;
        }
        return null;
    }

    protected boolean canPlace(Part part) {
        BlockPos pos = super.getClickedPos().offset(part.getOffset());
        return this.getLevel().getBlockState(pos).canBeReplaced(this);
    }

    @Override
    public boolean canPlace() {
        return parts != null && parts.stream().allMatch(this::canPlace) && super.canPlace();
    }

    @Override
    public BlockPos getClickedPos() {
        return new AttachedBlockPos(this, super.getClickedPos());
    }

    public ArrayList<Part> getParts() {
        return parts;
    }

    public ArrayList<BlockPos> getBlockPosList() {
        ArrayList<BlockPos> blockPosList = new ArrayList<>();
        for (Part part : parts) {
            blockPosList.add(part.getOffset());
        }
        return blockPosList;
    }


    protected void transform(Quaternion r) {
        for (Part part : parts) {
            part.transform(r);
        }
    }

    protected void loadElements(boolean dir) {
        if (!super.canPlace()) {
            return;
        }
        ItemStack itemStack = getItemInHand();
        SkinDescriptor descriptor = SkinDescriptor.of(itemStack);
        Skin skin = SkinLoader.getInstance().getSkin(descriptor);
        if (skin == null) {
            return;
        }
        ArrayList<Part> parts = new ArrayList<>();
        skin.getBlockBounds().forEach((pos, shape) -> {
            parts.add(new Part(pos, shape));
        });
        this.skin = descriptor;
        this.parts = parts;
        if (!dir) {
            return;
        }
        BlockState state = AWBlocks.SKINNABLE.getStateForPlacement(this);
        if (state != null) {
            this.rotations = SkinnableTileEntity.getRotations(state);
            this.transform(rotations);
        }
    }

    public SkinDescriptor getSkin() {
        return skin;
    }

    public Quaternion getRotations() {
        return rotations;
    }

    public static class Part {

        private BlockPos offset;
        private Rectangle3i shape;

        public Part() {
            this(BlockPos.ZERO, Rectangle3i.ZERO);
        }

        public Part(BlockPos offset, Rectangle3i shape) {
            this.offset = offset;
            this.shape = shape;
        }

        public Part(CompoundNBT nbt) {
            this.offset = AWDataSerializers.getBlockPos(nbt, AWConstants.NBT.TILE_ENTITY_REFER, BlockPos.ZERO);
            this.shape = AWDataSerializers.getRectangle3i(nbt, AWConstants.NBT.TILE_ENTITY_SHAPE, Rectangle3i.ZERO);
        }

        public void transform(Quaternion r) {
            Vector4f f = new Vector4f(offset.getX(), offset.getY(), offset.getZ(), 1.0f);
            f.transform(r);
            offset = new BlockPos(Math.round(f.x()), Math.round(f.y()), Math.round(f.z()));

            Rectangle3f of = new Rectangle3f(shape);
            of.mul(r);
            shape = new Rectangle3i(0, 0, 0, 0, 0, 0);
            shape.setX(Math.round(of.getX()));
            shape.setY(Math.round(of.getY()));
            shape.setZ(Math.round(of.getZ()));
            shape.setWidth(Math.round(of.getWidth()));
            shape.setHeight(Math.round(of.getHeight()));
            shape.setDepth(Math.round(of.getDepth()));
        }

//        public static void putBlockParts(CompoundNBT nbt, String key, ArrayList<Part> elements) {
//            if (elements.isEmpty()) {
//                return;
//            }
//            ListNBT list = new ListNBT();
//            for (Part element : elements) {
//                list.add(element.writeToNBT(new CompoundNBT()));
//            }
//            nbt.put(key, list);
//        }
//
//        public static ArrayList<Part> getBlockParts(CompoundNBT nbt, String key) {
//            ArrayList<Part> elements = new ArrayList<>();
//            ListNBT list = nbt.getList(key, Constants.NBT.TAG_COMPOUND);
//            for (int i = 0; i < list.size(); ++i) {
//                elements.add(new Part(list.getCompound(i)));
//            }
//            return elements;
//        }

        public CompoundNBT writeToNBT(CompoundNBT nbt) {
            AWDataSerializers.putBlockPos(nbt, AWConstants.NBT.TILE_ENTITY_REFER, offset, BlockPos.ZERO);
            AWDataSerializers.putRectangle3i(nbt, AWConstants.NBT.TILE_ENTITY_SHAPE, shape, Rectangle3i.ZERO);
            return nbt;
        }

        public BlockPos getOffset() {
            return offset;
        }

        public Rectangle3i getShape() {
            return shape;
        }
    }

    public static class AttachedBlockPos extends BlockPos {

        protected final SkinItemUseContext context;

        public AttachedBlockPos(SkinItemUseContext context, BlockPos pos) {
            super(pos);
            this.context = context;
        }
    }
}
