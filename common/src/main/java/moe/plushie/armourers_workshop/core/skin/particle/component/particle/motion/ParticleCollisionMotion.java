package moe.plushie.armourers_workshop.core.skin.particle.component.particle.motion;

import moe.plushie.armourers_workshop.core.math.OpenAxisAlignedBoundingBox;
import moe.plushie.armourers_workshop.core.skin.particle.SkinParticleComponent;
import moe.plushie.armourers_workshop.core.skin.particle.SkinParticleGenerator;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IInputStream;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IOutputStream;
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
    public void compile(SkinParticleGenerator generator) {
        var radius = this.collisionRadius;
        var enabled = generator.compile(this.enabled, 1.0);
        var bounciness = this.coefficientOfRestitution;
        var collissionDrag = this.collisionDrag;
        var expireOnImpact = this.expireOnContact;
        generator.instance().tick((emitter, particle, context) -> {
            // the collision motion is enable?
            if (particle.isDead() || particle.isManualMode() || !enabled.test(context)) {
                return;
            }
            var previous = particle.positionAt(0.0f);
            var current = particle.positionAt(1.0f);

            var deltaX = current.x - previous.x;
            var deltaY = current.y - previous.y;
            var deltaZ = current.z - previous.z;
            if (Math.abs(deltaX) > 10 || Math.abs(deltaY) > 10 || Math.abs(deltaZ) > 10) {
                return; // too far
            }

            var level = particle.level();
//            if (!level.isLoaded(new BlockPos((int) current.x, (int) current.y, (int) current.z))) {
//                return;
//            }

            var targetBox = OpenAxisAlignedBoundingBox.sphere(current, radius);
            var selectedBlocks = level.getCollisionBlocks(targetBox.expand(-deltaX, -deltaY, -deltaZ));
            var clipBox = targetBox.copy();

            // y-axis
            for (var target : selectedBlocks) {
                if (clipBox.intersects(target)) {
                    clipBox.minY = Math.max(clipBox.minY, target.minY);
                    clipBox.maxY = Math.min(clipBox.maxY, target.maxY);
                }
            }
            // x-axis
            for (var target : selectedBlocks) {
                if (clipBox.intersects(target)) {
                    clipBox.minX = Math.max(clipBox.minX, target.minX);
                    clipBox.maxX = Math.min(clipBox.maxX, target.maxX);
                }
            }
            // z-axis
            for (var target : selectedBlocks) {
                if (clipBox.intersects(target)) {
                    clipBox.minZ = Math.max(clipBox.minZ, target.minZ);
                    clipBox.maxZ = Math.min(clipBox.maxZ, target.maxZ);
                }
            }

            var tx = clipBox.midX() - targetBox.midX();
            var ty = clipBox.midY() - targetBox.midY();
            var tz = clipBox.midZ() - targetBox.midZ();
            if (tx == 0 && ty == 0 && tz == 0) {
                return; // not any changes.
            }

            if (expireOnImpact) {
                particle.kill();
                return;
            }

            var now = particle.position();
            var accelerationFactor = particle.accelerationFactor().copy();

            accelerationFactor.x *= -bounciness;
            accelerationFactor.y *= -bounciness;
            accelerationFactor.z *= -bounciness;
//                now.x += cx2 - cx1; // origX < x ? r : -r;
//                now.y += cy2 - cy1; //d0 < y ? r : -r;
//                now.z += cz2 - cz1; // origZ < z ? r : -r;

            particle.setPosition(now.x - tx, now.y - ty, now.z - tz);
            particle.setAccelerationFactor(accelerationFactor);
            //particle.dragFactor += collissionDrag;


//            var resolvedBox = targetBox.copy();
//            for (var blockBox : LevelAccessor.getCollisionBlocks(level, targetBox.expand(deltaX, deltaY, deltaZ))) {
//                resolvedBox.subtract(blockBox);
//            }

//            // have diff?
//            if (targetBox.equals(clipBox)) {
//                return;
//            }
//
//            if (expireOnImpact) {
//                particle.kill();
//                return;
//            }
//
//            if (particle.relativePosition) {
//                particle.relativePosition = false;
//                particle.prevPosition.set(prev);
//            }
//

//            var now = new OpenVector3f(clipBox.minX() + radius, clipBox.minY() + radius, clipBox.minZ() + radius);
//            var accelerationFactor = particle.getAccelerationFactor().copy();
////            now.set(aabb.getXMin() + radius, aabb.getYMin() + radius, aabb.getZMin() + radius);
//
//            if (targetBox.minX() != clipBox.minX()) {
//                accelerationFactor.x *= -bounciness;
////                now.x += cx2 - cx1; // origX < x ? r : -r;
//            }
//
//            if (targetBox.minY() != clipBox.minY()) {
//                accelerationFactor.y *= -bounciness;
////                now.y += cy2 - cy1; //d0 < y ? r : -r;
//            }
//
//            if (targetBox.minZ() != clipBox.minZ()) {
//                accelerationFactor.z *= -bounciness;
////                now.z += cz2 - cz1; // origZ < z ? r : -r;
//            }
//
//            var t = emitter.globalToLocal(now);
//            particle.setPosition(t.x, t.y, t.z);
//            particle.setAccelerationFactor(accelerationFactor);
//            particle.dragFactor += collissionDrag;
        });
    }

    @Override
    public int priority() {
        return 50;
    }
}
