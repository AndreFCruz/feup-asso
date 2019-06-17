package nodes.implementations.handlers;

import nodes.Handler;

import java.util.regex.Pattern;

public class RegexMatchBool extends Handler<String, Boolean> {
    static private String SettingsKey = "regex";

    public RegexMatchBool() {
        this.registerSettings(new String[]{SettingsKey});
    }

    @Override
    public Boolean handleMessage(String message) throws InterruptedException {
        String regexp = this.getSettingValue(SettingsKey);
        if (regexp == null) return null;

        return Pattern.matches(regexp, message);
    }
}
