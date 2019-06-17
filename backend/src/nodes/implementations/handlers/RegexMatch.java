package nodes.implementations.handlers;

import nodes.Handler;
import nodes.NodeFactory;

import java.util.regex.Pattern;

public class RegexMatch extends Handler<String, Boolean> {
    static {
        NodeFactory.registerNode(NodeFactory.HandlerType.REGEX_MATCH, RegexMatch::new);
    }

    static private String SettingsKey = "regex expression";

    RegexMatch() {
        this.registerSettings(new String[]{SettingsKey});
    }

    @Override
    public Boolean handleMessage(String message) throws InterruptedException {
        String regexp = this.getSettingValue(SettingsKey);
        if (regexp == null) return null;

        return Pattern.matches(regexp, message);
    }
}
