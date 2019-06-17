package nodes.implementations.handlers;

import nodes.Handler;

import java.util.regex.Pattern;

/**
 * Match input Strings against a given Regular Expression, and replace matching Strings.
 */
public class RegexReplace extends Handler<String, String> {
    static private String RegexSettingsKey = "regex";
    static private String ReplacementSettingsKey = "replacement";

    public RegexReplace() {
        this.registerSettings(new String[]{
                RegexSettingsKey, ReplacementSettingsKey
        });
    }

    @Override
    public String handleMessage(String message) throws InterruptedException {
        String regexp = this.getSettingValue(RegexSettingsKey);
        String replacement = this.getSettingValue(ReplacementSettingsKey);
        if (regexp == null || replacement == null) {
            System.err.println("Invalid settings: " + regexp + " / " + replacement);
            return null;
        }

        return Pattern.compile(regexp).matcher(message).replaceAll(replacement);
    }
}
