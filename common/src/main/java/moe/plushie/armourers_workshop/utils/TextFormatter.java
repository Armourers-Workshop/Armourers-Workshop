package moe.plushie.armourers_workshop.utils;

public class TextFormatter {

    public String getEmbeddedStyle(String value) {
        int i = value.length();
        StringBuilder results = new StringBuilder();
        for (int j = 0; j < i; ++j) {
            char c0 = value.charAt(j);
            if (c0 == 167) {
                if (j + 1 >= i) {
                    break;
                }
                char c1 = value.charAt(j + 1);
                results.append(c0);
                results.append(c1);
                ++j;
            }
        }
        return results.toString();
    }

    public String getFormattedString(String value) {
        // The following color codes can be added to the start of text to colur it.
        // &0 Black
        // &1 Dark Blue
        // &2 Dark Green
        // &3 Dark Aqua
        // &4 Dark Red
        // &5 Dark Purple
        // &6 Gold
        // &7 Gray
        // &8 Dark Gray
        // &9 Blue
        // &a Green
        // &b Aqua
        // &c Red
        // &d Light Purple
        // &e Yellow
        // &f White
        //
        // A new line can be inserted with %n. Please add/remove new lines to fit the localisations you are writing.
        //
        // The text %s will be replace with text. Example: "Author: %s" could become "Author: RiskyKen".
        // The text %d will be replace with a number. Example: "Radius: %d*%d*%d" could become "Radius: 3*3*3"
        value = value.replace("\n", System.lineSeparator());
        value = value.replace("%n", System.lineSeparator());
        return value;
    }
}
