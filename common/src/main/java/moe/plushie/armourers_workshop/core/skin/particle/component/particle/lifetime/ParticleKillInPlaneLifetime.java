package moe.plushie.armourers_workshop.core.skin.particle.component.particle.lifetime;

import moe.plushie.armourers_workshop.core.skin.particle.SkinParticleComponent;
import moe.plushie.armourers_workshop.core.skin.particle.SkinParticleGenerator;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IInputStream;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IOutputStream;

import java.io.IOException;

/**
 * Particles that cross this plane expire. The plane is relative to the emitter, but oriented in world space. The four parameters are the usual 4 elements of a plane equation.
 */
public class ParticleKillInPlaneLifetime implements SkinParticleComponent {

    /// A*x + B*y + C*z + D = 0
    /// with the parameters being [A, B, C, D ]
    private final float a;
    private final float b;
    private final float c;
    private final float d;

    public ParticleKillInPlaneLifetime(float a, float b, float c, float d) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
    }

    public ParticleKillInPlaneLifetime(IInputStream stream) throws IOException {
        this.a = stream.readFloat();
        this.b = stream.readFloat();
        this.c = stream.readFloat();
        this.d = stream.readFloat();
    }

    @Override
    public void writeToStream(IOutputStream stream) throws IOException {
        stream.writeFloat(a);
        stream.writeFloat(b);
        stream.writeFloat(c);
        stream.writeFloat(d);
    }

    @Override
    public void compile(SkinParticleGenerator generator) {
        // the particles that cross this plane expire.
        // the plane is relative to the emitter, but oriented in world space.
        generator.instance().tick((emitter, particle, context) -> {
            if (particle.isDead()) {
                return;
            }
            var p0 = particle.positionAt(0.0f);
            var p1 = particle.positionAt(1.0f);
            var prev = a * p0.x() + b * p0.y() + c * p0.z() + d;
            var now = a * p1.x() + b * p1.y() + c * p1.y() + d;
            if ((prev > 0 && now < 0) || (prev < 0 && now > 0)) {
                particle.kill();
            }
        });
    }

    @Override
    public int priority() {
        return 100;
    }
}
