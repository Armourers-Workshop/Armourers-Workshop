package moe.plushie.armourers_workshop.library.client.gui.skinlibrary;

import com.apple.library.foundation.NSString;
import com.apple.library.uikit.UIColor;
import com.apple.library.uikit.UIView;
import moe.plushie.armourers_workshop.api.common.IResultHandler;
import moe.plushie.armourers_workshop.core.client.gui.widget.InputDialog;
import moe.plushie.armourers_workshop.core.data.DataEncryptMethod;
import moe.plushie.armourers_workshop.core.skin.exception.TranslatableException;
import moe.plushie.armourers_workshop.core.skin.serializer.SkinFileOptions;
import moe.plushie.armourers_workshop.library.data.SkinLibraryManager;

public class SkinLibraryKeychainWindow {

    private final String securityData;

    public SkinLibraryKeychainWindow(String securityData) {
        this.securityData = securityData;
    }

    public void showInView(UIView view, IResultHandler<SkinFileOptions> consumer) {
        // password algorithm
        if (securityData.startsWith(DataEncryptMethod.PASSWORD.method() + ";")) {
            var dialog = new InputDialog();
            dialog.setTitle(NSString.localizedString("skin-library.dialog.passwordProvider.title"));
            dialog.setMessageColor(new UIColor(0xff5555));
            dialog.setPlaceholder(NSString.localizedString("skin-library.dialog.passwordProvider.enterPassword"));
            dialog.setVerifier(value -> !value.isEmpty());
            dialog.showInView(view, () -> {
                if (dialog.isCancelled()) {
                    return;
                }
                var password = DataEncryptMethod.PASSWORD.key(dialog.value());
                var inputSecurityData = DataEncryptMethod.PASSWORD.signature(password);
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
        if (securityData.startsWith(DataEncryptMethod.AUTH.method() + ";")) {
            var setting = SkinLibraryManager.getClient().getSetting();
            if (!securityData.equals(setting.getPublicKey())) {
                consumer.throwing(new TranslatableException("inventory.armourers_workshop.skin-library.error.illegalServer"));
                return;
            }
            var options = new SkinFileOptions();
            options.setSecurityKey(""); // fill in server side.
            options.setSecurityData(securityData);
            consumer.accept(options);
            return;
        }
        // no support
        consumer.throwing(new TranslatableException("inventory.armourers_workshop.skin-library.error.illegalAlgorithm"));
    }
}
