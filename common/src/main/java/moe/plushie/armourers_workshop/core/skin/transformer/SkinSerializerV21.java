package moe.plushie.armourers_workshop.core.skin.transformer;

import moe.plushie.armourers_workshop.api.common.IResource;
import moe.plushie.armourers_workshop.api.skin.ISkinType;
import moe.plushie.armourers_workshop.core.skin.Skin;
import moe.plushie.armourers_workshop.core.skin.SkinTypes;
import moe.plushie.armourers_workshop.core.skin.transformer.bedrock.BedrockModelBone;
import moe.plushie.armourers_workshop.core.skin.transformer.bedrock.BedrockModelTexture;
import moe.plushie.armourers_workshop.core.skin.transformer.blockbench.BlockBenchReader;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperty;
import moe.plushie.armourers_workshop.core.skin.transformer.bedrock.BedrockModel;
import moe.plushie.armourers_workshop.core.skin.transformer.bedrock.BedrockModelExporter;
import moe.plushie.armourers_workshop.core.skin.transformer.bedrock.BedrockModelGeometry;
import moe.plushie.armourers_workshop.core.skin.transformer.bedrock.BedrockTransform;
import moe.plushie.armourers_workshop.core.data.transform.SkinBasicTransform;
import moe.plushie.armourers_workshop.utils.SkinFileUtils;
import moe.plushie.armourers_workshop.utils.math.Vector3f;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

public final class SkinSerializerV21 {

    public static Skin readSkinFromFile(File file) throws IOException {
        String name = file.getName();
        Collection<IResource> resources = getResourcesFromFile(file);
        return readSkinFromReader(BlockBenchReader.from(name, resources));
    }

    public static Skin readSkinFromReader(SkinPackReader reader) throws IOException {
        // access the bedrock addon pack, and the load the entity models.
        BedrockModelExporter exporter = new BedrockModelExporter();
        reader.loadEntityModel(modelReader -> {
            BedrockModel model = modelReader.readModel();
            for (BedrockModelGeometry geometry : model.getGeometries()) {
                BedrockModelTexture texture = modelReader.readTexture(geometry);
                for (BedrockModelBone bone : geometry.getBones()) {
                    exporter.add(bone, texture);
                }
            }
            SkinPack pack = modelReader.getPack();
            if (pack != null) {
                String name = pack.getName();
                if (name != null && !name.isEmpty()) {
                    exporter.add(SkinProperty.ALL_CUSTOM_NAME, name);
                }
                String description = pack.getDescription();
                if (description != null && !description.isEmpty()) {
                    exporter.add(SkinProperty.ALL_FLAVOUR_TEXT, description);
                }
                Collection<String> authors = pack.getAuthors();
                if (authors != null && !authors.isEmpty()) {
                    StringBuilder builder = null;
                    for (String a : authors) {
                        if (builder == null) {
                            builder = new StringBuilder(a);
                        } else {
                            builder.append(",");
                            builder.append(a);
                        }
                    }
                    exporter.add(SkinProperty.ALL_AUTHOR_NAME, builder.toString());
                }
            }
            Map<String, BedrockTransform> transforms = modelReader.getTransforms();
            if (transforms != null) {
                transforms.forEach((name, transform) -> {
                    Vector3f translation = transform.getTranslation();
                    Vector3f rotation = transform.getRotation();
                    Vector3f scale = transform.getScale();
                    SkinBasicTransform transform1 = SkinBasicTransform.create(translation, rotation, scale);
                    if (!transform1.isIdentity()) {
                        exporter.add(name, transform1);
                    }
                });
            }
        });

        ISkinType skinType = SkinTypes.ITEM_SWORD;

        if (skinType == SkinTypes.OUTFIT) {
            // because the entity origin is at (0, 24, 0).
            exporter.move(new Vector3f(0, -24, 0));

            exporter.add(SkinProperty.OVERRIDE_MODEL_HEAD, true);
            exporter.add(SkinProperty.OVERRIDE_MODEL_CHEST, true);
            exporter.add(SkinProperty.OVERRIDE_MODEL_LEFT_ARM, true);
            exporter.add(SkinProperty.OVERRIDE_MODEL_RIGHT_ARM, true);
            exporter.add(SkinProperty.OVERRIDE_MODEL_LEFT_LEG, true);
            exporter.add(SkinProperty.OVERRIDE_MODEL_RIGHT_LEG, true);

            exporter.add(SkinProperty.OVERRIDE_OVERLAY_HAT, true);
            exporter.add(SkinProperty.OVERRIDE_OVERLAY_JACKET, true);
            exporter.add(SkinProperty.OVERRIDE_OVERLAY_LEFT_SLEEVE, true);
            exporter.add(SkinProperty.OVERRIDE_OVERLAY_RIGHT_SLEEVE, true);
            exporter.add(SkinProperty.OVERRIDE_OVERLAY_LEFT_PANTS, true);
            exporter.add(SkinProperty.OVERRIDE_OVERLAY_RIGHT_PANTS, true);

            exporter.add(SkinProperty.OVERRIDE_EQUIPMENT_BOOTS, true);
            exporter.add(SkinProperty.OVERRIDE_EQUIPMENT_CHESTPLATE, true);
            exporter.add(SkinProperty.OVERRIDE_EQUIPMENT_HELMET, true);
            exporter.add(SkinProperty.OVERRIDE_EQUIPMENT_LEGGINGS, true);
        }

        return exporter.export(skinType);
    }


    private static Collection<IResource> getResourcesFromFile(File file) throws IOException {
        if (file.isDirectory()) {
            return getResourcesFromDirectory(file);
        }
        if (file.getName().toLowerCase().endsWith(".zip")) {
            return getResourcesFromZip(file);
        }
        return getResourcesFromSet(file);
    }


    private static Collection<IResource> getResourcesFromZip(File zipFile) throws IOException {
        ArrayList<IResource> resources = new ArrayList<>();
        ZipFile file = new ZipFile(zipFile);
        ZipInputStream zip = new ZipInputStream(new BufferedInputStream(new FileInputStream(zipFile)));
        ZipEntry entry;
        while ((entry = zip.getNextEntry()) != null) {
            if (entry.isDirectory()) {
                continue;
            }
            String fileName = entry.getName();
            ZipEntry fileEntry = entry;
            resources.add(new IResource() {
                @Override
                public String getName() {
                    return fileName;
                }

                @Override
                public InputStream getInputStream() throws IOException {
                    return file.getInputStream(fileEntry);
                }
            });
        }
        return resources;
    }

    private static Collection<IResource> getResourcesFromDirectory(File rootPath) throws IOException {
        ArrayList<IResource> resources = new ArrayList<>();
        for (File entry : SkinFileUtils.listAllFiles(rootPath)) {
            if (entry.isDirectory()) {
                continue;
            }
            String fileName = SkinFileUtils.getRelativePath(entry, rootPath, true).substring(1);
            resources.add(new IResource() {
                @Override
                public String getName() {
                    return fileName;
                }

                @Override
                public InputStream getInputStream() throws IOException {
                    return new FileInputStream(entry);
                }
            });
        }
        return resources;
    }

    private static Collection<IResource> getResourcesFromSet(File... entries) throws IOException {
        ArrayList<IResource> resources = new ArrayList<>();
        for (File entry : entries) {
            if (entry.isDirectory()) {
                continue;
            }
            String fileName = entry.getName();
            resources.add(new IResource() {
                @Override
                public String getName() {
                    return fileName;
                }

                @Override
                public InputStream getInputStream() throws IOException {
                    return new FileInputStream(entry);
                }
            });
        }
        return resources;
    }
}

