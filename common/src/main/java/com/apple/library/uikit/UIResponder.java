package com.apple.library.uikit;

@SuppressWarnings("unused")
public abstract class UIResponder {

    public void mouseUp(UIEvent event) {
        UIResponder responder = nextResponder();
        if (responder != null) {
            responder.mouseUp(event);
        }
    }

    public void mouseDragged(UIEvent event) {
        UIResponder responder = nextResponder();
        if (responder != null) {
            responder.mouseDragged(event);
        }
    }

    public void mouseDown(UIEvent event) {
        UIResponder responder = nextResponder();
        if (responder != null) {
            responder.mouseDown(event);
        }
    }

    public void mouseWheel(UIEvent event) {
        UIResponder responder = nextResponder();
        if (responder != null) {
            responder.mouseWheel(event);
        }
    }

    public void mouseEntered(UIEvent event) {
        UIResponder responder = nextResponder();
        if (responder != null) {
            responder.mouseEntered(event);
        }
    }

    public void mouseMoved(UIEvent event) {
        UIResponder responder = nextResponder();
        if (responder != null) {
            responder.mouseMoved(event);
        }
    }

    public void mouseExited(UIEvent event) {
        UIResponder responder = nextResponder();
        if (responder != null) {
            responder.mouseExited(event);
        }
    }

    public void keyUp(UIEvent event) {
        UIResponder responder = nextResponder();
        if (responder != null) {
            responder.keyUp(event);
        }
    }

    public void keyDown(UIEvent event) {
        UIResponder responder = nextResponder();
        if (responder != null) {
            responder.keyDown(event);
        }
    }

    public void charTyped(UIEvent event) {
        UIResponder responder = nextResponder();
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
