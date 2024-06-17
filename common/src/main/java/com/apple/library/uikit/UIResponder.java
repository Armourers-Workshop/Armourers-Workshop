package com.apple.library.uikit;

@SuppressWarnings("unused")
public abstract class UIResponder {

    public void mouseUp(UIEvent event) {
        var responder = nextResponder();
        if (responder != null) {
            responder.mouseUp(event);
        }
    }

    public void mouseDragged(UIEvent event) {
        var responder = nextResponder();
        if (responder != null) {
            responder.mouseDragged(event);
        }
    }

    public void mouseDown(UIEvent event) {
        var responder = nextResponder();
        if (responder != null) {
            responder.mouseDown(event);
        }
    }

    public void mouseWheel(UIEvent event) {
        var responder = nextResponder();
        if (responder != null) {
            responder.mouseWheel(event);
        }
    }

    public void mouseEntered(UIEvent event) {
        var responder = nextResponder();
        if (responder != null) {
            responder.mouseEntered(event);
        }
    }

    public void mouseMoved(UIEvent event) {
        var responder = nextResponder();
        if (responder != null) {
            responder.mouseMoved(event);
        }
    }

    public void mouseExited(UIEvent event) {
        var responder = nextResponder();
        if (responder != null) {
            responder.mouseExited(event);
        }
    }

    public void keyUp(UIEvent event) {
        var responder = nextResponder();
        if (responder != null) {
            responder.keyUp(event);
        }
    }

    public void keyDown(UIEvent event) {
        var responder = nextResponder();
        if (responder != null) {
            responder.keyDown(event);
        }
    }

    public void charTyped(UIEvent event) {
        var responder = nextResponder();
        if (responder != null) {
            responder.charTyped(event);
        }
    }

    public void becomeFirstResponder() {
    }

    public void resignFirstResponder() {
    }

    public abstract UIResponder nextResponder();
}
