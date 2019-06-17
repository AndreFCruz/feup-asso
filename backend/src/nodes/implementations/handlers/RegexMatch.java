package nodes.implementations.handlers;

import nodes.Handler;
import nodes.NodeFactory;

import java.util.regex.Pattern;

public class RegexMatch extends Handler<String, Boolean> {
    static private String SettingsKey = "regex";

    public RegexMatch() {
        this.registerSettings(new String[]{SettingsKey});
    }

    @Override
    public Boolean handleMessage(String message) throws InterruptedException {
        String regexp = this.getSettingValue(SettingsKey);
        if (regexp == null) return null;

        return Pattern.matches(regexp, message);
    }
}
