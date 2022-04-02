package moe.plushie.armourers_workshop.core.utils;

import moe.plushie.armourers_workshop.init.common.AWConstants;
import moe.plushie.armourers_workshop.init.common.ModBlocks;
import moe.plushie.armourers_workshop.core.skin.Skin;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.core.skin.SkinLoader;
import moe.plushie.armourers_workshop.core.skin.data.SkinMarker;
import moe.plushie.armourers_workshop.core.skin.data.property.SkinProperties;
import moe.plushie.armourers_workshop.core.skin.data.property.SkinProperty;
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
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.math.vector.Vector4f;

import java.util.ArrayList;
import java.util.Collection;

public class SkinItemUseContext extends BlockItemUseContext {

    private Vector3f rotations = new Vector3f();
    private SkinDescriptor skin = SkinDescriptor.EMPTY;
    private ArrayList<Part> parts = new ArrayList<>();

    private SkinProperties properties;

    public SkinItemUseContext(ItemUseContext context) {
        super(context);
        this.loadElements();
    }

    public SkinItemUseContext(PlayerEntity player, Hand hand, ItemStack itemStack, BlockRayTraceResult traceResult) {
        super(player.level, player, hand, itemStack, traceResult);
        this.loadElements();
    }

    public static SkinItemUseContext of(BlockPos pos) {
        if (pos instanceof AttachedBlockPos) {
            return ((AttachedBlockPos) pos).context;
        }
        return null;
    }

    protected void transform(Vector3f r) {
        for (Part part : parts) {
            part.transform(r);
        }
    }

    protected void loadElements() {
        ItemStack itemStack = getItemInHand();
        SkinDescriptor descriptor = SkinDescriptor.of(itemStack);
        Skin skin = SkinLoader.getInstance().getSkin(descriptor.getIdentifier());
        if (skin == null) {
            return;
        }
        ArrayList<Part> parts = new ArrayList<>();
        ArrayList<BlockPos> blockPosList = new ArrayList<>();
        skin.getBlockBounds().forEach((pos, shape) -> {
            if (pos.equals(BlockPos.ZERO)) {
                parts.add(new ParentPart(pos, shape, blockPosList, descriptor, skin));
            } else {
                parts.add(new Part(pos, shape));
            }
        });
        this.skin = descriptor;
        this.parts = parts;
        this.properties = skin.getProperties();
        BlockState state = ModBlocks.SKINNABLE.getStateForPlacement(this);
        if (state != null) {
            this.rotations = SkinnableTileEntity.getRotations(state);
            this.transform(rotations);
        }
        // copy all transformed block pose into list.
        for (Part part : parts) {
            blockPosList.add(part.getOffset());
        }
    }


    public <V> V getProperty(SkinProperty<V> property) {
        if (properties != null && !properties.isEmpty()) {
            return properties.get(property);
        }
        return property.getDefaultValue();
    }

    public boolean canPlace(Part part) {
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

    public SkinDescriptor getSkin() {
        return skin;
    }

    public ArrayList<Part> getParts() {
        return parts;
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

//        public Part(CompoundNBT nbt) {
//            this.offset = AWDataSerializers.getBlockPos(nbt, AWConstants.NBT.TILE_ENTITY_REFER, BlockPos.ZERO);
//            this.shape = AWDataSerializers.getRectangle3i(nbt, AWConstants.NBT.TILE_ENTITY_SHAPE, Rectangle3i.ZERO);
//        }
//
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
            AWDataSerializers.putBlockPos(nbt, AWConstants.NBT.TILE_ENTITY_REFER, offset, null);
            AWDataSerializers.putRectangle3i(nbt, AWConstants.NBT.TILE_ENTITY_SHAPE, shape, null);
            return nbt;
        }

        public void transform(Vector3f r) {
            Quaternion q = TrigUtils.rotate(r.x(), r.y(), r.z(), true);

            Vector4f f = new Vector4f(offset.getX(), offset.getY(), offset.getZ(), 1.0f);
            f.transform(q);
            offset = new BlockPos(Math.round(f.x()), Math.round(f.y()), Math.round(f.z()));

            Rectangle3f of = new Rectangle3f(shape);
            of.mul(q);
            shape = new Rectangle3i(0, 0, 0, 0, 0, 0);
            shape.setX(Math.round(of.getX()));
            shape.setY(Math.round(of.getY()));
            shape.setZ(Math.round(of.getZ()));
            shape.setWidth(Math.round(of.getWidth()));
            shape.setHeight(Math.round(of.getHeight()));
            shape.setDepth(Math.round(of.getDepth()));
        }

        public BlockPos getOffset() {
            return offset;
        }

        public Rectangle3i getShape() {
            return shape;
        }

        public CompoundNBT getEntityTag() {
            return writeToNBT(new CompoundNBT());
        }
    }

    public static class ParentPart extends Part {

        private SkinDescriptor descriptor;
        private SkinProperties properties;
        private Collection<BlockPos> blockPosList;
        private Collection<SkinMarker> markerList;

        public ParentPart(BlockPos offset, Rectangle3i shape, Collection<BlockPos> blockPosList, SkinDescriptor descriptor, Skin skin) {
            super(offset, shape);
            this.descriptor = descriptor;
            this.blockPosList = blockPosList;
            this.properties = skin.getProperties();
            this.markerList = skin.getMarkers();
        }

        @Override
        public CompoundNBT writeToNBT(CompoundNBT nbt) {
            nbt = super.writeToNBT(nbt);
            AWDataSerializers.putBlockPosList(nbt, AWConstants.NBT.TILE_ENTITY_REFERS, blockPosList);
            AWDataSerializers.putMarkerList(nbt, AWConstants.NBT.TILE_ENTITY_MARKERS, markerList);
            AWDataSerializers.putSkinDescriptor(nbt, AWConstants.NBT.TILE_ENTITY_SKIN, descriptor, SkinDescriptor.EMPTY);
            AWDataSerializers.putSkinProperties(nbt, AWConstants.NBT.TILE_ENTITY_SKIN_PROPERTIES, properties);
            return nbt;
        }

        @Override
        public void transform(Vector3f r) {
            super.transform(r);

            Quaternion q = TrigUtils.rotate(r.x(), r.y(), r.z(), true);
            ArrayList<SkinMarker> newMarkerList = new ArrayList<>();
            for (SkinMarker marker : markerList) {
                Vector4f f = new Vector4f(marker.x, marker.y, marker.z, 1.0f);
                f.transform(Matrix4f.createScaleMatrix(-1, -1, 1));
                f.transform(q);
                int x = Math.round(f.x());
                int y = Math.round(f.y());
                int z = Math.round(f.z());
                marker = new SkinMarker((byte) x, (byte) y, (byte) z, marker.meta);
                newMarkerList.add(marker);
            }
            this.markerList = newMarkerList;
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
