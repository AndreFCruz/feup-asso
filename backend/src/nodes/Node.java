package nodes;

import java.util.Map;

public abstract class Node<Id> {

    /**
     * This node's unique ID.
     */
    private Id id;

    /**
     * This node's settings.
     * (e.g. name of file to extract from on a FileReaderSink)
     */
    private Map<String, String> settings;

    Id initialize(Id id) {
        this.id = id;
        return id;
    }

    public Id getId() {
        return this.id;
    }

}
