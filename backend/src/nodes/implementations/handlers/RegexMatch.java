package nodes.implementations.handlers;

import nodes.Handler;

import java.util.regex.Pattern;

/**
 * Matches the inbound message against a RegularExpression, and passes it onwards if it's a match.
 */
public class RegexMatch extends Handler<String, String> {
    static private String SettingsKey = "regex";

    public RegexMatch() {
        this.registerSettings(new String[]{SettingsKey});
    }

    @Override
    public String handleMessage(String message) throws InterruptedException {
        String regexp = this.getSettingValue(SettingsKey);
        if (regexp == null) return null;

        return Pattern.matches(regexp, message) ? message : null;
    }
}
