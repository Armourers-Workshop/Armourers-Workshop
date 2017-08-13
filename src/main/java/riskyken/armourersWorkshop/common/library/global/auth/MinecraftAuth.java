package riskyken.armourersWorkshop.common.library.global.auth;

import java.net.URL;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.HttpAuthenticationService;
import com.mojang.authlib.exceptions.AuthenticationException;

import net.minecraft.client.Minecraft;

public class MinecraftAuth {
    
    private static final String BASE_URL = "https://sessionserver.mojang.com/session/minecraft/";
    private static final URL JOIN_URL = HttpAuthenticationService.constantURL(BASE_URL + "join");
    
    public void joinServer(GameProfile profile, String authenticationToken, String serverId) throws AuthenticationException {
        
        Minecraft.getMinecraft().func_152347_ac().joinServer(profile, authenticationToken, serverId);
    }
}
