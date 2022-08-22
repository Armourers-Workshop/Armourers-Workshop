package com.apple.library.uikit;

import com.apple.library.foundation.NSRange;

public interface UITextFieldDelegate {

    default boolean textFieldShouldBeginEditing(UITextField textField) {
        return true;
    }

    default void textFieldDidBeginEditing(UITextField textField) {
    }

    default boolean textFieldShouldEndEditing(UITextField textField) {
        return true;
    }

    default void textFieldDidEndEditing(UITextField textField) {
    }

    default boolean textFieldShouldChangeCharacters(UITextField textField, NSRange range, String string) {
        return true;
    }

    default void textFieldDidChangeSelection(UITextField textField) {
    }

    default boolean textFieldShouldReturn(UITextField textField) {
        return true;
    }
}
