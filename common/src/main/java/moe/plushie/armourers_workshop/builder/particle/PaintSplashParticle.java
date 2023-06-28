//package moe.plushie.armourers_workshop.builder.particle;
//
//import moe.plushie.armourers_workshop.utils.color.PaintColor;
//import net.fabricmc.api.EnvType;
//import net.fabricmc.api.Environment;
//import net.minecraft.client.particle.*;
//import net.minecraft.client.world.ClientWorld;
//import net.minecraft.util.Direction;
//import net.minecraftforge.api.distmarker.Dist;
//import net.minecraftforge.api.distmarker.OnlyIn;
//
//@Environment(EnvType.CLIENT)
//public class PaintSplashParticle extends RainParticle {
//
//    private PaintSplashParticle(ClientLevel world, double x, double y, double z, double xd, double yd, double zd, Direction dir) {
//        super(world, x, y, z);
//
//        int sx = dir.getStepX();
//        int sy = dir.getStepY();
//        int sz = dir.getStepZ();
//
//        this.x += 0.5f * sx + (Math.random() - 0.5f) * sz + (Math.random() - 0.5f) * sy;
//        this.y += 0.5f * sy + (Math.random() - 0.5f) * sx + (Math.random() - 0.5f) * sz;
//        this.z += 0.5f * sz + (Math.random() - 0.5f) * sx + (Math.random() - 0.5f) * sy;
//
//        this.setPos(x, y - bbHeight, z);
//
//        this.xo = x;
//        this.yo = y;
//        this.zo = z;
//
//        this.xd = 0.06f * sx + (Math.random() - 0.5f) * 0.04f;
//        this.yd = 0.06f * sy + (Math.random() - 0.5f) * 0.04f;
//        this.zd = 0.06f * sz + (Math.random() - 0.5f) * 0.04f;
//
//        this.quadSize = 0.1f;
//
//        this.gravity = 0.05F;
//        this.lifetime = 50;
//        this.hasPhysics = true;
//    }
//
//    @Override
//    public void tick() {
//        this.xo = x;
//        this.yo = y;
//        this.zo = z;
//        if (this.age++ >= this.lifetime) {
//            this.remove();
//            return;
//        }
//        int fadeTime = 15;
//        int lifeLeft = lifetime - age;
//        if (lifeLeft <= fadeTime) {
//            this.alpha = ((float) lifeLeft / (float) fadeTime);
//        }
//        this.yd -= 0.04D * (double) gravity;
//        this.move(xd, yd, zd);
//        this.xd *= 0.98F;
//        this.yd *= 0.98F;
//        this.zd *= 0.98F;
//        if (this.onGround) {
//            this.xd *= 0.7F;
//            this.zd *= 0.7F;
//        }
//    }
//
//    @Override
//    public IParticleRenderType getRenderType() {
//        return IParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
//    }
//
//    @Environment(EnvType.CLIENT)
//    public static class Factory implements IParticleFactory<PaintSplashParticleData> {
//        private final IAnimatedSprite sprite;
//
//        public Factory(IAnimatedSprite p_i50679_1_) {
//            this.sprite = p_i50679_1_;
//        }
//
//        public Particle createParticle(PaintSplashParticleData data, ClientLevel world, double x, double y, double z, double xd, double yd, double zd) {
//            PaintColor paintColor = PaintColor.of(data.getPaintColor());
//            PaintSplashParticle particle = new PaintSplashParticle(world, x, y, z, xd, yd, zd, data.getDirection());
//            particle.pickSprite(this.sprite);
//            particle.setColor(paintColor.getRed() / 255f, paintColor.getGreen() / 255f, paintColor.getBlue() / 255f);
//            return particle;
//        }
//    }
//}
