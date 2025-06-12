package moe.plushie.armourers_workshop.core.skin.serializer.importer.blockbench;

import moe.plushie.armourers_workshop.core.math.OpenSize2f;
import moe.plushie.armourers_workshop.core.math.OpenSize3f;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class BlockBenchPack {

    private final String name;
    private final String description;
    private final String version;
    private final String format;

    private final List<String> authors;

    private final OpenSize2f resolution;
    private final OpenSize3f visibleBox;

    private final BlockBenchOutliner rootOutliner;

    private final List<BlockBenchElement> elements;
    private final List<BlockBenchTexture> textures;
    private final List<BlockBenchAnimation> animations;

    private final Map<String, BlockBenchDisplay> transforms;
    private final Map<String, BlockBenchObject> objects = new HashMap<>();

    public BlockBenchPack(String name, String description, String version, String format, List<String> authors, OpenSize2f resolution, OpenSize3f visibleBox, BlockBenchOutliner rootOutliner, List<BlockBenchElement> elements, List<BlockBenchTexture> textures, List<BlockBenchAnimation> animations, Map<String, BlockBenchDisplay> transforms) {
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
        elements.forEach(it -> objects.put(it.uuid(), it));
        rootOutliner.children().forEach(it -> {
            if (it instanceof BlockBenchObject object) {
                objects.put(object.uuid(), object);
            }
        });
    }

    public String name() {
        return name;
    }

    public String description() {
        return description;
    }

    public String version() {
        return version;
    }

    public String format() {
        return format;
    }

    public List<String> authors() {
        return authors;
    }

    public OpenSize2f resolution() {
        return resolution;
    }

    public OpenSize3f visibleBox() {
        return visibleBox;
    }

    public BlockBenchOutliner rootOutliner() {
        return rootOutliner;
    }

    public List<BlockBenchElement> elements() {
        return elements;
    }

    public List<BlockBenchTexture> textures() {
        return textures;
    }

    public List<BlockBenchAnimation> animations() {
        return animations;
    }

    @Nullable
    public Map<String, BlockBenchDisplay> itemTransforms() {
        return transforms;
    }

    @Nullable
    public BlockBenchObject getObject(String uuid) {
        return objects.get(uuid);
    }

    protected static class Builder {

        private String name;
        private String description;
        private String version = "4.10";
        private String format = "free";
        private List<String> authors;

        private OpenSize2f resolution = new OpenSize2f(64, 64);
        private OpenSize3f visibleBox = new OpenSize3f(8, 7, 1.5f);

        private boolean useItemTransforms = false;

        private final List<BlockBenchElement> elements = new ArrayList<>();
        private final List<BlockBenchTexture> textures = new ArrayList<>();
        private final List<BlockBenchAnimation> animations = new ArrayList<>();

        private final Map<String, BlockBenchDisplay> transforms = new LinkedHashMap<>();

        private final BlockBenchOutliner.Builder rootOutliner = new BlockBenchOutliner.Builder();

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

        public void resolution(OpenSize2f resolution) {
            this.resolution = resolution;
        }

        public void visibleBox(OpenSize3f visibleBox) {
            this.visibleBox = visibleBox;
        }

        public void setUseItemTransforms(boolean useItemTransforms) {
            this.useItemTransforms = useItemTransforms;
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

        public void addDisplay(String name, BlockBenchDisplay transform) {
            this.transforms.put(name, transform);
        }

        public void addAnimation(BlockBenchAnimation animation) {
            this.animations.add(animation);
        }

        public BlockBenchPack build() {
            var itemTransforms = transforms;
            if (!useItemTransforms) {
                itemTransforms = null;
            }
            return new BlockBenchPack(name, description, version, format, authors, resolution, visibleBox, rootOutliner.build(), elements, textures, animations, itemTransforms);
        }
    }
}

