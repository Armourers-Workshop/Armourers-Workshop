package moe.plushie.armourers_workshop.core.skin.transformer.blockbench;

import moe.plushie.armourers_workshop.core.skin.transformer.SkinPack;
import moe.plushie.armourers_workshop.core.skin.transformer.bedrock.BedrockTransform;
import moe.plushie.armourers_workshop.utils.math.Size2f;
import moe.plushie.armourers_workshop.utils.math.Size3f;
import moe.plushie.armourers_workshop.utils.math.Vector3f;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BlockBenchPack implements SkinPack {

    private final String name;
    private final String description;
    private final String version;
    private final String format;

    private final List<String> authors;

    private final Size2f resolution;
    private final Size3f visibleBox;

    private final BlockBenchOutliner rootOutliner;

    private final List<BlockBenchElement> elements;
    private final List<BlockBenchTexture> textures;
    private final List<BlockBenchAnimation> animations;

    private final Map<String, BedrockTransform> transforms;
    private final HashMap<String, BlockBenchObject> objects = new HashMap<>();

    public BlockBenchPack(String name, String description, String version, String format, List<String> authors, Size2f resolution, Size3f visibleBox, BlockBenchOutliner rootOutliner, List<BlockBenchElement> elements, List<BlockBenchTexture> textures, List<BlockBenchAnimation> animations, Map<String, BedrockTransform> transforms) {
        this.name = name;
        this.description = description;
        this.version = version;
        this.format = format;
        this.authors = authors;
        this.resolution = resolution;
        this.visibleBox = visibleBox;
        this.elements = elements;
        this.rootOutliner = rootOutliner;
        this.textures = textures;
        this.animations = animations;
        this.transforms = transforms;
        // rebuild object map.
        elements.forEach(it -> objects.put(it.getUUID(), it));
        rootOutliner.getChildren().forEach(it -> {
            if (it instanceof BlockBenchObject object) {
                objects.put(object.getUUID(), object);
            }
        });
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    public String getVersion() {
        return version;
    }

    public String getFormat() {
        return format;
    }

    @Override
    public List<String> getAuthors() {
        return authors;
    }

    @Nullable
    public Vector3f getOrigin() {
        // java block/item has a special origin.
        if (format.equals("java_block")) {
            return new Vector3f(8, 8, 8);
        }
        return null;
    }

    public Size2f getResolution() {
        return resolution;
    }

    public Size3f getVisibleBox() {
        return visibleBox;
    }

    public BlockBenchOutliner getRootOutliner() {
        return rootOutliner;
    }

    public List<BlockBenchElement> getElements() {
        return elements;
    }

    public List<BlockBenchTexture> getTextures() {
        return textures;
    }

    public List<BlockBenchAnimation> getAnimations() {
        return animations;
    }

    public Map<String, BedrockTransform> getTransforms() {
        return transforms;
    }

    @Nullable
    public BlockBenchObject getObject(String uuid) {
        return objects.get(uuid);
    }

    public static class Builder {

        private String name;
        private String description;
        private String version = "4.5";
        private String format = "bedrock";
        private List<String> authors;

        private Size2f resolution = new Size2f(64, 64);
        private Size3f visibleBox = new Size3f(8, 7, 1.5f);

        private final ArrayList<BlockBenchElement> elements = new ArrayList<>();
        private final ArrayList<BlockBenchTexture> textures = new ArrayList<>();
        private final ArrayList<BlockBenchAnimation> animations = new ArrayList<>();

        private final BlockBenchOutliner.Builder rootOutliner = new BlockBenchOutliner.Builder();

        private final HashMap<String, BedrockTransform> transforms = new HashMap<>();

        public void name(String name) {
            this.name = name;
        }

        public void description(String description) {
            this.description = description;
        }

        public void version(String version) {
            this.version = version;
        }

        public void format(String format) {
            this.format = format;
        }

        public void author(List<String> author) {
            this.authors = author;
        }

        public void resolution(Size2f resolution) {
            this.resolution = resolution;
        }

        public void visibleBox(Size3f visibleBox) {
            this.visibleBox = visibleBox;
        }

        public void addElement(BlockBenchElement element) {
            this.elements.add(element);
        }

        public void addOutliner(Object obj) {
            this.rootOutliner.addChild(obj);
        }

        public void addTexture(BlockBenchTexture texture) {
            this.textures.add(texture);
        }

        public void addDisplay(String name, BedrockTransform transform) {
            this.transforms.put(name, transform);
        }

        public void addAnimation(BlockBenchAnimation animation) {
            this.animations.add(animation);
        }

        public BlockBenchPack build() {
            return new BlockBenchPack(name, description, version, format, authors, resolution, visibleBox, rootOutliner.build(), elements, textures, animations, transforms);
        }
    }
}

