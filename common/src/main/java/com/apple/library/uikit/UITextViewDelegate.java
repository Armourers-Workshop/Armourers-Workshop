package com.apple.library.uikit;

import com.apple.library.foundation.NSRange;

@SuppressWarnings("unused")
public interface UITextViewDelegate extends UIScrollViewDelegate {

    default boolean textViewShouldBeginEditing(UITextView textView) {
        return true;
    }

    default boolean textViewShouldEndEditing(UITextView textView) {
        return true;
    }

    default void textViewDidBeginEditing(UITextView textView) {
    }

    default void textViewDidEndEditing(UITextView textView) {
    }


    default boolean textViewShouldChangeText(UITextView textView, NSRange range, String replacementText) {
        return true;
    }

    default void textViewDidChange(UITextView textView) {

    }

    default void textViewDidChangeSelection(UITextView textView) {
    }
}
