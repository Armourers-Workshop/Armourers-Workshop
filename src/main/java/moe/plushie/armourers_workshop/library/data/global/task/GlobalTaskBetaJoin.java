package moe.plushie.armourers_workshop.library.data.global.task;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import moe.plushie.armourers_workshop.init.common.ModLog;
import moe.plushie.armourers_workshop.library.data.global.auth.MinecraftAuth;
import moe.plushie.armourers_workshop.library.data.global.auth.PlushieAuth;
import moe.plushie.armourers_workshop.library.data.global.auth.PlushieSession;
import moe.plushie.armourers_workshop.library.data.global.permission.PermissionSystem;
import net.minecraft.client.Minecraft;

import java.net.URLEncoder;

public class GlobalTaskBetaJoin extends GlobalTask<GlobalTaskBetaJoin.BetaJoinResult> {

    private static final String URL_JOIN = "join.php?username=%s&uuid=%s&serverId=%s";

    public GlobalTaskBetaJoin() {
        super(PermissionSystem.PlushieAction.BETA_JOIN, true);
    }

    @Override
    public BetaJoinResult call() throws Exception {
        permissionCheck();
        PlushieSession session = PlushieAuth.PLUSHIE_SESSION;
        if (session.hasServerId()) {
            return new BetaJoinResult(BetaJoinResult.JoinResult.ALREADY_JOINED);
        }
        GameProfile gameProfile = Minecraft.getInstance().player.getGameProfile();
        String username = URLEncoder.encode(gameProfile.getName(), "UTF-8");
        String uuid = URLEncoder.encode(gameProfile.getId().toString(), "UTF-8");
        String serverId = String.valueOf(getBaseUrl().hashCode());

        if (!MinecraftAuth.checkAndRefeshAuth(Minecraft.getInstance().getUser(), serverId)) {
            ModLog.debug("Failed MC Auth");
            return new BetaJoinResult(BetaJoinResult.JoinResult.MINECRAFT_AUTH_FAIL);
        } else {
            ModLog.debug("MC Auth Done");
        }

        String urlJoin = String.format(getBaseUrl() + URL_JOIN, username, uuid, serverId);

        JsonObject jsonJoinResult = new JsonParser().parse(downloadString(urlJoin)).getAsJsonObject();

        if (jsonJoinResult.has("action") & jsonJoinResult.has("valid")) {
            String action = jsonJoinResult.get("action").getAsString();
            boolean valid = jsonJoinResult.get("valid").getAsBoolean();
            if (action.equals("join")) {
                if (valid) {
                    PlushieAuth.doRemoteUserCheck();
                    PlushieAuth.PLUSHIE_SESSION.authenticate(jsonJoinResult);
                    return new BetaJoinResult(BetaJoinResult.JoinResult.JOINED);
                } else {
                    String reason = "";
                    if (jsonJoinResult.has("reason")) {
                        reason = jsonJoinResult.get("reason").getAsString();
                    }
                    return new BetaJoinResult(BetaJoinResult.JoinResult.JOIN_FAILED, reason);
                }
            }
        }

        return new BetaJoinResult(BetaJoinResult.JoinResult.JOIN_FAILED);
    }

    public static class BetaJoinResult {

        private final JoinResult joinResult;
        private final String message;

        public BetaJoinResult(JoinResult joinResult) {
            this.joinResult = joinResult;
            this.message = joinResult.toString().toLowerCase();
        }

        public BetaJoinResult(JoinResult joinResult, String message) {
            this.joinResult = joinResult;
            this.message = message;
        }

        public JoinResult getJoinResult() {
            return joinResult;
        }

        public String getMessage() {
            return message;
        }

        public enum JoinResult {
            ALREADY_JOINED, MINECRAFT_AUTH_FAIL, JOIN_FAILED, JOINED
        }
    }
}
