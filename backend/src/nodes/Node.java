package nodes;

import java.util.HashMap;
import java.util.Map;

public abstract class Node<Id> {

    /**
     * This node's settings.
     * (e.g. name of file to extract from on a FileReaderSink)
     */
    private Map<String, String> settings = new HashMap<>();

    /**
     * This node's unique ID.
     */
    private Id id;

    Id initialize(Id id) {
        this.id = id;
        return id;
    }

    public Id getId() {
        return this.id;
    }

    public boolean initializeSettings(Map<String, String> settingValues) {
        if (this.settings.isEmpty())
            return true;

        for (String key : this.settings.keySet()) {
            if (settingValues.containsKey(key)) {
                this.settings.put(key, settingValues.get(key));
            } else {
                return false;
            }
        }

        return this.initSettingsHandler();
    }

    /**
     * Factory method for the node subclass to handle assets set-up after settings are set.
     * Override for handling this event.
     *
     * @return
     */
    protected boolean initSettingsHandler() {
        return true;
    }

    protected String getSettingValue(String key) {
        return this.settings.getOrDefault(key, null);
    }

    /**
     * Register the given settings as needed.
     */
    protected void registerSettings(String[] availableSettings) {
        for (String key : availableSettings)
            this.settings.put(key, null);
    }

    public String[] getSettingsKeys() {
        return new String[]{};
    }
}
