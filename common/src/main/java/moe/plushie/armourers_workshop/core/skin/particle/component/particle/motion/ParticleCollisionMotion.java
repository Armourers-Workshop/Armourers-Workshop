package moe.plushie.armourers_workshop.core.skin.particle.component.particle.motion;

import moe.plushie.armourers_workshop.core.math.OpenAxisAlignedBoundingBox;
import moe.plushie.armourers_workshop.core.math.OpenVoxelShape;
import moe.plushie.armourers_workshop.core.skin.particle.SkinParticleCompiler;
import moe.plushie.armourers_workshop.core.skin.particle.SkinParticleComponent;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IInputStream;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IOutputStream;
import moe.plushie.armourers_workshop.core.utils.Collections;
import moe.plushie.armourers_workshop.core.utils.OpenPrimitive;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * This component enables collisions between the terrain and the particle.
 * Collision detection in Minecraft consists of detecting an intersection, moving to a nearby non-intersecting point for the particle (if possible), and setting its direction to not be aimed towards the collision (usually perpendicular to the collision surface).
 */
public class ParticleCollisionMotion implements SkinParticleComponent {

    /// enables collision when true/non-zero.
    /// evaluated every frame
    private final OpenPrimitive enabled;

    /// alters the speed of the particle when it has collided
    /// useful for emulating friction/drag when colliding, e.g a particle
    /// that hits the ground would slow to a stop.
    /// This drag slows down the particle by this amount in blocks/sec
    /// when in contact
    private final float collisionDrag;

    /// used to minimize interpenetration of particles with the environment
    /// note that this must be less than or equal to 1/2 block
    private final float collisionRadius;

    /// used for bouncing/not-bouncing
    /// Set to 0.0 to not bounce, 1.0 to bounce back up to original hight
    /// and in-between to lose speed after bouncing.  Set to >1.0 to gain energy on each bounce
    private final float coefficientOfRestitution;

    /// triggers expiration on contact if true
    private final boolean expireOnContact;

    /// triggers the specified event if the conditions are met
    /// "min_speed": <float> // default/minimum is 2 blocks/sec
    private final Map<Float, String> events;

    public ParticleCollisionMotion(OpenPrimitive enabled, float collisionDrag, float collisionRadius, float coefficientOfRestitution, boolean expireOnContact, Map<Float, String> events) {
        this.enabled = enabled;
        this.collisionDrag = collisionDrag;
        this.collisionRadius = collisionRadius;
        this.coefficientOfRestitution = coefficientOfRestitution;
        this.expireOnContact = expireOnContact;
        this.events = events;
    }

    public ParticleCollisionMotion(IInputStream stream) throws IOException {
        this.enabled = stream.readPrimitiveObject();
        this.collisionDrag = stream.readFloat();
        this.collisionRadius = stream.readFloat();
        this.coefficientOfRestitution = stream.readFloat();
        this.expireOnContact = stream.readBoolean();
        this.events = new LinkedHashMap<>();
        int eventSize = stream.readVarInt();
        for (int i = 0; i < eventSize; ++i) {
            float key = stream.readFloat();
            var value = stream.readString();
            this.events.put(key, value);
        }
    }

    @Override
    public void writeToStream(IOutputStream stream) throws IOException {
        stream.writePrimitiveObject(enabled);
        stream.writeFloat(collisionDrag);
        stream.writeFloat(collisionRadius);
        stream.writeFloat(coefficientOfRestitution);
        stream.writeBoolean(expireOnContact);
        stream.writeVarInt(events.size());
        for (var entry : events.entrySet()) {
            stream.writeFloat(entry.getKey());
            stream.writeString(entry.getValue());
        }
    }

    @Override
    public void compile(SkinParticleCompiler compiler) {
        var radius = this.collisionRadius;
        var enabled = compiler.compile(this.enabled, 1.0);
        var bounciness = this.coefficientOfRestitution;
        var collissionDrag = this.collisionDrag;
        var expireOnImpact = this.expireOnContact;
        compiler.instance().tick((emitter, particle, context) -> {
            // the collision motion is enabled?
            if (particle.isDead() || particle.isManualMode() || !enabled.test(context)) {
                return;
            }

            var begin = particle.globalPosition(0.0f);
            var end = particle.globalPosition(1.0f);

            var deltaX = end.x - begin.x;
            var deltaY = end.y - begin.y;
            var deltaZ = end.z - begin.z;
            if (Math.abs(deltaX) > 10 || Math.abs(deltaY) > 10 || Math.abs(deltaZ) > 10) {
                return; // too far.
            }

            var level = particle.level();
            if (!level.isLoaded(begin)) {
                return; // chunk not loaded.
            }

            var clipBox = OpenAxisAlignedBoundingBox.sphere(begin, radius);
            var selectedBlocks = Collections.compactMap(level.getCollisionBlocks(clipBox.expand(deltaX, deltaY, deltaZ)), OpenVoxelShape::aabb);

            var tx = deltaX;
            var ty = deltaY;
            var tz = deltaZ;

            // Resolve each axis from the last valid particle box.
            for (var target : selectedBlocks) {
                ty = clipY(clipBox, target, ty);
            }
            clipBox = clipBox.offset(0.0f, ty, 0.0f);

            for (var target : selectedBlocks) {
                tx = clipX(clipBox, target, tx);
            }
            clipBox = clipBox.offset(tx, 0.0f, 0.0f);

            for (var target : selectedBlocks) {
                tz = clipZ(clipBox, target, tz);
            }
            clipBox = clipBox.offset(0.0f, 0.0f, tz);

            var collidedX = tx != deltaX;
            var collidedY = ty != deltaY;
            var collidedZ = tz != deltaZ;
            if (!collidedX && !collidedY && !collidedZ) {
                return;
            }

            if (expireOnImpact) {
                particle.kill();
                return;
            }

            var accelerationFactor = particle.accelerationFactor().copy();

            if (collidedX) {
                accelerationFactor.x *= -bounciness;
            }
            if (collidedY) {
                accelerationFactor.y *= -bounciness;
            }
            if (collidedZ) {
                accelerationFactor.z *= -bounciness;
            }

            // Collision boxes are in world space. Detach before storing the clipped world position.
            particle.freeze();

            particle.setPosition(clipBox.midX(), clipBox.midY(), clipBox.midZ());
            particle.setMotionDragFactor(particle.motionDragFactor() + collissionDrag);
            particle.setAccelerationFactor(accelerationFactor);
        });
    }

    private static float clipX(OpenAxisAlignedBoundingBox box, OpenAxisAlignedBoundingBox target, float offset) {
        if (box.maxY <= target.minY || box.minY >= target.maxY || box.maxZ <= target.minZ || box.minZ >= target.maxZ) {
            return offset;
        }
        if (offset > 0.0f && box.maxX <= target.minX) {
            return Math.min(offset, target.minX - box.maxX);
        }
        if (offset < 0.0f && box.minX >= target.maxX) {
            return Math.max(offset, target.maxX - box.minX);
        }
        return offset;
    }

    private static float clipY(OpenAxisAlignedBoundingBox box, OpenAxisAlignedBoundingBox target, float offset) {
        if (box.maxX <= target.minX || box.minX >= target.maxX || box.maxZ <= target.minZ || box.minZ >= target.maxZ) {
            return offset;
        }
        if (offset > 0.0f && box.maxY <= target.minY) {
            return Math.min(offset, target.minY - box.maxY);
        }
        if (offset < 0.0f && box.minY >= target.maxY) {
            return Math.max(offset, target.maxY - box.minY);
        }
        return offset;
    }

    private static float clipZ(OpenAxisAlignedBoundingBox box, OpenAxisAlignedBoundingBox target, float offset) {
        if (box.maxX <= target.minX || box.minX >= target.maxX || box.maxY <= target.minY || box.minY >= target.maxY) {
            return offset;
        }
        if (offset > 0.0f && box.maxZ <= target.minZ) {
            return Math.min(offset, target.minZ - box.maxZ);
        }
        if (offset < 0.0f && box.minZ >= target.maxZ) {
            return Math.max(offset, target.maxZ - box.minZ);
        }
        return offset;
    }

    @Override
    public int priority() {
        return 50;
    }
}