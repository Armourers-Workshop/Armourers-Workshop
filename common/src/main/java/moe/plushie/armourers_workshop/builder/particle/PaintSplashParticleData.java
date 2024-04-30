//package moe.plushie.armourers_workshop.builder.particle;
//
//import com.mojang.brigadier.StringReader;
//import com.mojang.brigadier.exceptions.CommandSyntaxException;
//import com.mojang.serialization.Codec;
//import com.mojang.serialization.codecs.RecordCodecBuilder;
//import moe.plushie.armourers_workshop.api.painting.IPaintColor;
//import moe.plushie.armourers_workshop.init.common.ModParticleTypes;
//import moe.plushie.armourers_workshop.utils.color.PaintColor;
//import moe.plushie.armourers_workshop.api.network.IFriendlyByteBuf;
//import net.minecraft.particles.IParticleData;
//import net.minecraft.particles.ParticleType;
//import net.minecraft.util.Direction;
//import net.minecraft.util.IStringSerializable;
//
//@SuppressWarnings("NullableProblems")
//public class PaintSplashParticleData implements IParticleData {
//
//    public static final Codec<Direction> DIRECTION_CODEC = IStringSerializable.fromEnum(Direction::values, Direction::byName);
//
//    public static final Codec<PaintSplashParticleData> CODEC = RecordCodecBuilder.create(builder -> builder
//            .group(PaintSplashParticleData.DIRECTION_CODEC.fieldOf("facing").forGetter(PaintSplashParticleData::getDirection), PaintColor.CODEC.fieldOf("color").forGetter(PaintSplashParticleData::getPaintColor))
//            .apply(builder, PaintSplashParticleData::new));
//
//    public static final IDeserializer<PaintSplashParticleData> DESERIALIZER = new IDeserializer<PaintSplashParticleData>() {
//
//        @Override
//        public PaintSplashParticleData fromCommand(ParticleType<PaintSplashParticleData> particleType, StringReader reader) throws CommandSyntaxException {
//            reader.skipWhitespace();
//            Direction dir = Direction.byName(reader.readString());
//            reader.skipWhitespace();
//            int color = reader.readInt() & 0xffffff;
//            return new PaintSplashParticleData(dir, PaintColor.of(color));
//        }
//
//        @Override
//        public PaintSplashParticleData fromNetwork(ParticleType<PaintSplashParticleData> particleType, IFriendlyByteBuf buffer) {
//            Direction dir = buffer.readEnum(Direction.class);
//            int color = buffer.readInt() & 0xffffff;
//            return new PaintSplashParticleData(dir, PaintColor.of(color));
//        }
//    };
//
//    protected final Direction direction;
//    protected final IPaintColor paintColor;
//
//    public PaintSplashParticleData(Direction direction, IPaintColor paintColor) {
//        this.paintColor = paintColor;
//        this.direction = direction;
//    }
//
//    @Override
//    public ParticleType<?> getType() {
//        return ModParticleTypes.PAINT_SPLASH;
//    }
//
//    @Override
//    public void writeToNetwork(IFriendlyByteBuf buffer) {
//        buffer.writeEnum(direction);
//        buffer.writeInt(paintColor.getRGB());
//    }
//
//    @Override
//    public String writeToString() {
//        return String.format("%s %d", direction.getName(), paintColor.getRGB());
//    }
//
//    public IPaintColor getPaintColor() {
//        return paintColor;
//    }
//
//    public Direction getDirection() {
//        return direction;
//    }
//
//    public static class Type extends ParticleType<PaintSplashParticleData> {
//
//        public Type(boolean p_i50792_1_) {
//            super(p_i50792_1_, PaintSplashParticleData.DESERIALIZER);
//        }
//
//        @Override
//        public Codec<PaintSplashParticleData> codec() {
//            return PaintSplashParticleData.CODEC;
//        }
//    }
//}
