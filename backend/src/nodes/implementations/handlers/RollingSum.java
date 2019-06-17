package nodes.implementations.handlers;

import nodes.Handler;
import nodes.NodeFactory;

import java.util.LinkedList;
import java.util.List;

public class RollingSum extends Handler<Integer, Integer> {
    static {
        NodeFactory.registerNode(NodeFactory.HandlerType.ROLLING_SUM, RollingSum::new);
    }

    private static String SettingsKey = "numbers_to_keep";
    private List<Integer> previousValues = new LinkedList<>();

    RollingSum() {
        this.registerSettings(new String[] {SettingsKey});
    }

    Integer getNumbersToKeep() {
        String settingVal = this.getSettingValue(SettingsKey);
        return Integer.parseInt(settingVal);
    }

    @Override
    public Integer handleMessage(Integer message) throws InterruptedException {
        Integer numsToKeep = getNumbersToKeep();
        if (numsToKeep == null) return null;

        if (previousValues.size() < numsToKeep) {
            // Accumulate values up to numsToKeep
            previousValues.add(message);
            return null;
        } else {
            // If queue is full, start returning sums
            return this.previousValues.stream()
                    .reduce((Integer i1, Integer i2) -> i1 + i2)
                    .orElse(null);
        }
    }
}
