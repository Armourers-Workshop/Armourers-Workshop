package moe.plushie.armourers_workshop.core.skin.transformer.bedrock;

import java.util.ArrayList;
import java.util.Collection;

public class BedrockModel {

    private final String formatVersion;
    private final Collection<BedrockModelGeometry> geometries;

    public BedrockModel(String formatVersion, Collection<BedrockModelGeometry> geometries) {
        this.formatVersion = formatVersion;
        this.geometries = geometries;
    }

    public Collection<BedrockModelGeometry> getGeometries() {
        return geometries;
    }

    public String getFormatVersion() {
        return formatVersion;
    }

    public static class Builder {

        private String formatVersion;
        private final ArrayList<BedrockModelGeometry> geometries = new ArrayList<>();

        public void formatVersion(String formatVersion) {
            this.formatVersion = formatVersion;
        }

        public void addGeometry(BedrockModelGeometry geometry) {
            this.geometries.add(geometry);
        }

        public BedrockModel build() {
            return new BedrockModel(formatVersion, geometries);
        }
    }
}
