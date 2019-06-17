package nodes.implementations.handlers;

import nodes.Handler;
import nodes.NodeFactory;

import java.util.regex.Pattern;

public class RegexReplace extends Handler<String, String> {
    static {
        NodeFactory.registerNode(NodeFactory.HandlerType.REGEX_REPLACE, RegexReplace::new);
    }

    static private String RegexSettingsKey = "regex";
    static private String ReplacementSettingsKey = "regex";

    RegexReplace() {
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
