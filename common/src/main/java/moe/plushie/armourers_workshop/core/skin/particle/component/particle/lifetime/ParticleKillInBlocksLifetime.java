package moe.plushie.armourers_workshop.core.skin.particle.component.particle.lifetime;

import moe.plushie.armourers_workshop.core.skin.particle.SkinParticleComponent;
import moe.plushie.armourers_workshop.core.skin.particle.SkinParticleCompiler;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IInputStream;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IOutputStream;
import moe.plushie.armourers_workshop.core.utils.Collections;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Particles expire when in a block of the type in the list. Note: this component can exist alongside particle_lifetime_expression.
 */
public class ParticleKillInBlocksLifetime implements SkinParticleComponent {

    /// minecraft block names, e.g. 'minecraft:water', 'minecraft:air'
    /// these are typically the same name as in the /setblock command
    /// except for the minecraft: prefix
    private final List<String> blocks;

    public ParticleKillInBlocksLifetime(List<String> blocks) {
        this.blocks = blocks;
    }

    public ParticleKillInBlocksLifetime(IInputStream stream) throws IOException {
        var size = stream.readVarInt();
        var blocks = new ArrayList<String>();
        for (int i = 0; i < size; ++i) {
            blocks.add(stream.readString());
        }
        this.blocks = blocks;
    }

    @Override
    public void writeToStream(IOutputStream stream) throws IOException {
        stream.writeVarInt(blocks.size());
        for (var block : blocks) {
            stream.writeString(block);
        }
    }

    @Override
    public void compile(SkinParticleCompiler compiler) {
        // the blocks that let the particle expire on contact.
        var blocks = Collections.compactMap(this.blocks, compiler.registry()::getBlock);
        compiler.instance().tick((emitter, particle, context) -> {
            if (particle.isAlive() && blocks.contains(particle.level().getBlock(particle.globalPosition()))) {
                particle.kill();
            }
        });
    }
}
