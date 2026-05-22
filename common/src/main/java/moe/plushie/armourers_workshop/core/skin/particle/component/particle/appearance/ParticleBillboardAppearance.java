package moe.plushie.armourers_workshop.core.skin.particle.component.particle.appearance;

import moe.plushie.armourers_workshop.core.math.OpenRectangle2f;
import moe.plushie.armourers_workshop.core.math.OpenSize2f;
import moe.plushie.armourers_workshop.core.math.OpenSize2i;
import moe.plushie.armourers_workshop.core.skin.particle.SkinParticleComponent;
import moe.plushie.armourers_workshop.core.skin.particle.SkinParticleCompiler;
import moe.plushie.armourers_workshop.core.skin.particle.math.ParticleCameraFacing;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IInputStream;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IOutputStream;
import moe.plushie.armourers_workshop.core.utils.OpenPrimitive;

import java.io.IOException;

/**
 * This component tells the particle system to render the particle as a billboard, a rectangle in the world facing a particular direction.
 */
public class ParticleBillboardAppearance implements SkinParticleComponent {

    /// specifies the x/y size of the billboard
    /// evaluated every frame
    private final OpenPrimitive width;
    private final OpenPrimitive height;

    /// used to orient the billboard.  Options are:
    /// "rotate_xyz" - aligned to the camera, perpendicular to the view axis
    /// "rotate_y" - aligned to camera, but rotating around world y axis
    /// "lookat_xyz" - aimed at the camera, biased towards world y up
    /// "lookat_y" - aimed at the camera, but rotating around world y axis
    /// "direction_x" - unrotated particle x axis is along the direction vector, unrotated y axis attempts to aim upwards
    /// "direction_y" - unrotated particle y axis is along the direction vector, unrotated x axis attempts to aim upwards
    /// "direction_z" - billboard face is along the direction vector, unrotated y axis attempts to aim upwards
    /// emitter_transform_xy, // orient the particles to match the emitter's transform (the billboard plane will match the transform's xy plane).
    /// emitter_transform_xz, // orient the particles to match the emitter's transform (the billboard plane will match the transform's xz plane).
    /// emitter_transform_yz, // orient the particles to match the emitter's transform (the billboard plane will match the transform's yz plane).
    private final ParticleCameraFacing cameraFacing;

//    // Specifies how to calculate the direction of a particle, this will be used by facing modes that require a direction as input (for instance: lookat_direction and direction)
//    // Options are:
//    // "derive_from_velocity" - The direction matches the direction of the velocity.
//    // "custom_direction" - The direction is specified in the json definition using a vector of floats or molang expressions.
//    // If the direction subsection is not defined, the default will be "derive_from_velocity" mode with a "min_speed_threshold" of 0.01.
//    "direction": {
//        "mode": "derive_from_velocity" or "custom_direction",
//        "min_speed_threshold": <float> // only used in "derive_from_velocity" mode. The direction is set if the speed of the particle is above the threshold. The default is 0.01
//        "custom_direction": [ <float/molang>, <float/molang>, <float/molang> ], // only used in "custom_direction" mode. Specifies the direction vector
//    }

    /// specifies the assumed texture width/height
    /// defaults to 1
    /// when set to 1, UV's work just like normalized UV's
    /// when set to the texture width/height, this works like texels
    private final OpenSize2i textureSize;

    /// Assuming the specified texture width and height, use these
    /// uv coordinates.
    /// evaluated every frame
    private final OpenPrimitive textureCoordsX;
    private final OpenPrimitive textureCoordsY;
    private final OpenPrimitive textureCoordsWidth;
    private final OpenPrimitive textureCoordsHeight;

    /// alternate way via specifying a flipbook animation
    /// a flipbook animation uses pieces of the texture to animate, by stepping over time from one
    /// "frame" to another
    private final boolean flipbook;

    /// how far to move the UV patch each frame
    private final OpenPrimitive stepX;
    private final OpenPrimitive stepY;

//    "base_UV": [ <float/molang>, <float/molang> ], // upper-left corner of starting UV patch
//    "size_UV": [ <float>, <float> ], // size of UV patch
//    "step_UV": [ <float>, <float> ], //

    /// default frames per second
    private final int fps;

    /// maximum frame number, with first frame being frame 1
    private final OpenPrimitive maxFrame;

    /// optional, adjust fps to match lifetime of particle. default=false
    private final boolean isStretchToLifetime;

    /// optional, makes the animation loop when it reaches the end? default=false
    private final boolean isLoop;

    public ParticleBillboardAppearance(OpenPrimitive width, OpenPrimitive height, ParticleCameraFacing cameraFacing, OpenSize2i textureSize, OpenPrimitive textureCoordsX, OpenPrimitive textureCoordsY, OpenPrimitive textureCoordsWidth, OpenPrimitive textureCoordsHeight, OpenPrimitive stepX, OpenPrimitive stepY, boolean flipbook, int fps, OpenPrimitive maxFrame, boolean isStretchToLifetime, boolean isLoop) {
        this.width = width;
        this.height = height;
        this.cameraFacing = cameraFacing;
        this.textureSize = textureSize;
        this.textureCoordsX = textureCoordsX;
        this.textureCoordsY = textureCoordsY;
        this.textureCoordsWidth = textureCoordsWidth;
        this.textureCoordsHeight = textureCoordsHeight;
        this.stepX = stepX;
        this.stepY = stepY;
        this.flipbook = flipbook;
        this.fps = fps;
        this.maxFrame = maxFrame;
        this.isStretchToLifetime = isStretchToLifetime;
        this.isLoop = isLoop;
    }

    public ParticleBillboardAppearance(IInputStream stream) throws IOException {
        this.width = stream.readPrimitiveObject();
        this.height = stream.readPrimitiveObject();
        this.cameraFacing = stream.readEnum(ParticleCameraFacing.class);
        int textureWidth = stream.readInt();
        int textureHeight = stream.readInt();
        this.textureSize = new OpenSize2i(textureWidth, textureHeight);
        this.textureCoordsX = stream.readPrimitiveObject();
        this.textureCoordsY = stream.readPrimitiveObject();
        this.textureCoordsWidth = stream.readPrimitiveObject();
        this.textureCoordsHeight = stream.readPrimitiveObject();
        this.stepX = stream.readPrimitiveObject();
        this.stepY = stream.readPrimitiveObject();
        this.flipbook = stream.readBoolean();
        this.fps = stream.readInt();
        this.maxFrame = stream.readPrimitiveObject();
        this.isStretchToLifetime = stream.readBoolean();
        this.isLoop = stream.readBoolean();
    }

    @Override
    public void writeToStream(IOutputStream stream) throws IOException {
        stream.writePrimitiveObject(width);
        stream.writePrimitiveObject(height);
        stream.writeEnum(cameraFacing);
        stream.writeInt(textureSize.width());
        stream.writeInt(textureSize.height());
        stream.writePrimitiveObject(textureCoordsX);
        stream.writePrimitiveObject(textureCoordsY);
        stream.writePrimitiveObject(textureCoordsWidth);
        stream.writePrimitiveObject(textureCoordsHeight);
        stream.writePrimitiveObject(stepX);
        stream.writePrimitiveObject(stepY);
        stream.writeBoolean(flipbook);
        stream.writeInt(fps);
        stream.writePrimitiveObject(maxFrame);
        stream.writeBoolean(isStretchToLifetime);
        stream.writeBoolean(isLoop);
    }

    @Override
    public void compile(SkinParticleCompiler compiler) {
        var width = compiler.compile(this.width, 0.0);
        var height = compiler.compile(this.height, 0.0);
        var textureCoordsX = compiler.compile(this.textureCoordsX, 0.0);
        var textureCoordsY = compiler.compile(this.textureCoordsY, 0.0);
        var textureCoordsWidth = compiler.compile(this.textureCoordsWidth, 0.0);
        var textureCoordsHeight = compiler.compile(this.textureCoordsHeight, 0.0);
        var maxFrame = compiler.compile(this.maxFrame, 0);
        var stepX = compiler.compile(this.stepX, 0);
        var stepY = compiler.compile(this.stepY, 0);
        compiler.instance().render((emitter, particle, context) -> {
            // calculate texture uvs.
            var w = width.compute(context);
            var h = height.compute(context);
            var u = textureCoordsX.compute(context);
            var v = textureCoordsY.compute(context);
            var s = textureCoordsWidth.compute(context);
            var t = textureCoordsHeight.compute(context);
            var n = Math.max(textureSize.width, 1);
            var m = Math.max(textureSize.height, 1);
            // is a animation texture.
            if (flipbook) {
                var time = particle.time();
                var index = Math.floor(time * fps);
                var frames = maxFrame.compute(context);
                // adjust fps to match lifetime of particle.
                if (isStretchToLifetime) {
                    var duration = particle.duration();
                    if (duration > 0) {
                        index = Math.floor((time / duration) * frames);
                    } else {
                        index = 0;
                    }
                }
                // makes the animation loop when it reaches the end
                if (this.isLoop && frames > 0) {
                    index = index % frames;
                }
                u += stepX.compute(context) * index;
                v += stepY.compute(context) * index;
            }
            // submit a element into render pipeline.
            var pos = particle.localPosition();
            var rotation = particle.localRotation();
            var size = new OpenSize2f(w * 2.25, h * 2.25);
            var textureBox = new OpenRectangle2f(u / n, v / m, s / n, t / m);
            particle.pipeline().submit(pos, rotation, size, textureBox, cameraFacing);
        });
    }
}
