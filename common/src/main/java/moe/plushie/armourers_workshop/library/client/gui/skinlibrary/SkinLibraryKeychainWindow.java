package moe.plushie.armourers_workshop.library.client.gui.skinlibrary;

import com.apple.library.foundation.NSString;
import com.apple.library.uikit.UIColor;
import com.apple.library.uikit.UIView;
import moe.plushie.armourers_workshop.api.common.IResultHandler;
import moe.plushie.armourers_workshop.core.client.gui.widget.InputDialog;
import moe.plushie.armourers_workshop.core.skin.exception.TranslatableException;
import moe.plushie.armourers_workshop.core.skin.serializer.SkinFileOptions;
import moe.plushie.armourers_workshop.library.data.SkinLibraryManager;
import moe.plushie.armourers_workshop.utils.ObjectUtils;

public class SkinLibraryKeychainWindow {

    private final String securityData;

    public SkinLibraryKeychainWindow(String securityData) {
        this.securityData = securityData;
    }

    public void showInView(UIView view, IResultHandler<SkinFileOptions> consumer) {
        // password algorithm
        if (securityData.startsWith(Algorithm.PASSWORD.method() + ";")) {
            var dialog = new InputDialog();
            dialog.setTitle(NSString.localizedString("skin-library.dialog.passwordProvider.title"));
            dialog.setMessageColor(new UIColor(0xff5555));
            dialog.setPlaceholder(NSString.localizedString("skin-library.dialog.passwordProvider.enterPassword"));
            dialog.setVerifier(value -> !value.isEmpty());
            dialog.showInView(view, () -> {
                if (dialog.isCancelled()) {
                    return;
                }
                var password = Algorithm.PASSWORD.key(dialog.value());
                var inputSecurityData = Algorithm.PASSWORD.signature(password);
                if (!securityData.equals(inputSecurityData)) {
                    consumer.throwing(new TranslatableException("inventory.armourers_workshop.skin-library.error.illegalPassword"));
                    return;
                }
                var options = new SkinFileOptions();
                options.setSecurityKey(password);
                options.setSecurityData(inputSecurityData);
                consumer.accept(options);
            });
            return;
        }
        // auth algorithm
        if (securityData.startsWith(Algorithm.AUTH.method() + ";")) {
            var setting = SkinLibraryManager.getClient().getSetting();
            var serverSecurityData = Algorithm.AUTH.signature(setting.getToken());
            if (!securityData.equals(serverSecurityData)) {
                consumer.throwing(new TranslatableException("inventory.armourers_workshop.skin-library.error.illegalServer"));
                return;
            }
            var options = new SkinFileOptions();
            options.setSecurityKey("");
            options.setSecurityData(securityData);
            consumer.accept(options);
            return;
        }
        // no support
        consumer.throwing(new TranslatableException("inventory.armourers_workshop.skin-library.error.illegalAlgorithm"));
    }


    public enum Algorithm {

        PASSWORD("password"),
        AUTH("auth");

        private final String method;

        Algorithm(String method) {
            this.method = method;
        }

        public String key(String text) {
            return ObjectUtils.md5(method + ";" + ObjectUtils.md5(String.format("%s(%s)", method, text)) + ";" + "aw");
        }

        public String signature(String key) {
            return method + ";" + ObjectUtils.md5(String.format("signature(%s)", key));
        }

        public String method() {
            return method;
        }
    }
}
