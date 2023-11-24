package moe.plushie.armourers_workshop.core.skin.exception;

public class NewerFileVersionException extends Exception {

    public NewerFileVersionException(int fileVersion) {
        super("target version: " + fileVersion);
    }
}
