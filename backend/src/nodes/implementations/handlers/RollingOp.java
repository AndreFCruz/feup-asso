package nodes.implementations.handlers;

import nodes.Handler;

import java.util.LinkedList;
import java.util.List;

/**
 * Performs a rolling operation along a configurable-sized window.
 *
 * @param <T> The type on which this Handler operates.
 */
public abstract class RollingOp<T> extends Handler<T, T> {

    private static String SettingsKey = "values_to_keep";
    private LinkedList<T> window = new LinkedList<>();

    RollingOp() {
        this.registerSettings(new String[]{SettingsKey});
    }

    Integer getNumbersToKeep() {
        String settingVal = this.getSettingValue(SettingsKey);
        return Integer.parseInt(settingVal);
    }

    @Override
    public T handleMessage(T newValue) throws InterruptedException {
        Integer numsToKeep = getNumbersToKeep();
        if (numsToKeep == null) return null;

        window.add(newValue);
        if (window.size() < numsToKeep) {
            // Accumulate values up to numsToKeep
            return null;
        } else if (window.size() > numsToKeep) {
            // Delete oldest value if queue overflowed
            window.remove();
        }

        if (window.size() == numsToKeep) {
            // If queue is full, start returning sums
            return this.executeRollingOp(window);
        } else {
            System.err.println("Invalid queue size in RollingOp: " + window.size());
            return null;
        }
    }

    protected abstract T executeRollingOp(List<T> values);
}
