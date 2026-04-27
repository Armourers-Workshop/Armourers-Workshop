package moe.plushie.armourers_workshop.core.client.texture;

import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import moe.plushie.armourers_workshop.api.core.IResultHandler;
import moe.plushie.armourers_workshop.compat.client.texture.AbstractPlayerSkinResolver;
import moe.plushie.armourers_workshop.compat.client.utils.AbstractGameProfileResolver;
import moe.plushie.armourers_workshop.core.skin.texture.PlayerSkin;
import moe.plushie.armourers_workshop.core.skin.texture.PlayerSkinDescriptor;
import moe.plushie.armourers_workshop.core.skin.texture.PlayerSkinModel;
import moe.plushie.armourers_workshop.core.skin.texture.PlayerSkinPart;
import moe.plushie.armourers_workshop.core.utils.Executors;
import moe.plushie.armourers_workshop.core.utils.Objects;
import moe.plushie.armourers_workshop.core.utils.OpenGameProfile;
import moe.plushie.armourers_workshop.init.ModConstants;
import moe.plushie.armourers_workshop.init.platform.EnvironmentManager;
import moe.plushie.armourers_workshop.utils.RenderSystem;
import net.minecraft.client.Minecraft;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

@OnlyIn(Dist.CLIENT)
public class PlayerSkinDownloader {

    private static final ScheduledExecutorService TIMER = Executors.newSingleThreadScheduledExecutor();

    private final AbstractGameProfileResolver profileResolver = new AbstractGameProfileResolver();
    private final AbstractPlayerSkinResolver skinResolver = new AbstractPlayerSkinResolver();

    private final Executor workThread = Executors.newFixedThreadPool(1, "AW-SKIN-SD");

    public void downloadProfile(OpenGameProfile profile, IResultHandler<OpenGameProfile> handler) {
        workThread.execute(() -> {
            profileResolver.resolve(profile, (result, exception) -> {
                workThread.execute(() -> {
                    handler.apply(result, exception);
                });
            });
        });
    }

    public void downloadSkin(OpenGameProfile profile, IResultHandler<PlayerSkin> handler) {
        workThread.execute(() -> {
            downloadSkin(profile, 3, (result, exception) -> {
                workThread.execute(() -> {
                    handler.apply(result, exception);
                });
            });
        });
    }

    public void downloadSkin(String url, IResultHandler<PlayerSkin> handler) {
        var identifier = Objects.md5(url);
        var textureIdentifier = ModConstants.key("skins/" + identifier);
        var outputFile = new File(EnvironmentManager.getRootDirectory() + "/skin-textures/" + identifier.substring(0, 2) + "/" + identifier);
        downloadSkin(url, outputFile, (it, exception) -> {
            try {
                if (exception != null) {
                    throw exception;
                }
                // register a texture into manager.
                var decoder = PlayerSkinDecoder.getInstance();
                var image = decoder.decode(new FileInputStream(it));
                var semaphore = new Semaphore(0);
                RenderSystem.recordRenderCall(() -> {
                    var texture = new ImageTexture(textureIdentifier.path(), image);
                    Minecraft.getInstance().getTextureManager().register(textureIdentifier, texture);
                    semaphore.release();
                });
                semaphore.acquire();
                // build a skin object.
                var body = new PlayerSkinPart(textureIdentifier, url);
                var descriptor = PlayerSkinDescriptor.fromURL(url);
                var result = new PlayerSkin(descriptor, body, null, null, PlayerSkinModel.WIDE);
                handler.accept(result);
            } catch (Throwable e) {
                handler.abort(e);
            }
        });
    }

    private void downloadSkin(OpenGameProfile profile, int retryCount, IResultHandler<PlayerSkin> handler) {
        skinResolver.resolve(profile, (skin, exception) -> {
            // when this is an unknown user, it only calls back a placeholder result.
            if (!profile.isResolvable()) {
                handler.apply(skin, exception);
                return;
            }
            // in some cases will get a placeholder result (url is null),
            // this means the game profile still loading phase.
            if (skin != null && skin.body().url() == null && retryCount > 0) {
                TIMER.schedule(() -> downloadSkin(profile, retryCount - 1, handler), 500, TimeUnit.MILLISECONDS);
                return;
            }
            handler.apply(skin, exception);
        });
    }

    private void downloadSkin(String url, File outputFile, IResultHandler<File> handler) {
        workThread.execute(() -> {
            // the file is download?
            if (outputFile.exists()) {
                handler.accept(outputFile);
                return;
            }
            // download the skin from the network.
            try {
                var connection = (HttpURLConnection) (new URL(url)).openConnection();
                connection.setDoInput(true);
                connection.setDoOutput(false);
                connection.connect();
                FileUtils.copyInputStreamToFile(connection.getInputStream(), outputFile);
                handler.accept(outputFile);
            } catch (Exception exception) {
                handler.abort(exception);
            }
        });
    }
}
