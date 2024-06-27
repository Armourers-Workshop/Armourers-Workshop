package moe.plushie.armourers_workshop.core.skin.transformer.bedrock;

import moe.plushie.armourers_workshop.utils.math.Vector3f;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class BedrockModelBone {

    private final String id;
    private final String name;
    private final String parent;
    private final Vector3f pivot;
    private final Vector3f rotation;
    private final boolean mirror;
    private final Collection<BedrockModelCube> cubes;
    private final Map<String, Vector3f> locators;

    public BedrockModelBone(String id, String name, String parent, Vector3f pivot, Vector3f rotation, boolean mirror, Collection<BedrockModelCube> cubes, Map<String, Vector3f> locators) {
        this.id = id;
        this.name = name;
        this.parent = parent;
        this.pivot = pivot;
        this.rotation = rotation;
        this.mirror = mirror;
        this.cubes = cubes;
        this.locators = locators;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getParent() {
        return parent;
    }

    public Vector3f getPivot() {
        return pivot;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public boolean isMirror() {
        return mirror;
    }

    public Collection<BedrockModelCube> getCubes() {
        return cubes;
    }

    public Map<String, Vector3f> getLocators() {
        return locators;
    }

    public static class Builder {

        private String id;
        private String name;
        private String parent = "";
        private Vector3f pivot = Vector3f.ZERO;
        private Vector3f rotation = Vector3f.ZERO;
        private boolean mirror = false;
        private final ArrayList<BedrockModelCube> cubes = new ArrayList<>();
        private final HashMap<String, Vector3f> locators = new HashMap<>();

        public void id(String id) {
            this.id = id;
        }

        public void name(String name) {
            this.name = name;
        }

        public void parent(String parent) {
            this.parent = parent;
        }

        public void pivot(Vector3f pivot) {
            this.pivot = pivot;
        }

        public void rotation(Vector3f rotation) {
            this.rotation = rotation;
        }

        public void mirror(boolean mirror) {
            this.mirror = mirror;
        }

        public void addCube(BedrockModelCube cube) {
            this.cubes.add(cube);
        }

        public void addLocator(String name, Vector3f locator) {
            this.locators.put(name, locator);
        }

        public BedrockModelBone build() {
            return new BedrockModelBone(id, name, parent, pivot, rotation, mirror, cubes, locators);
        }
    }
}
