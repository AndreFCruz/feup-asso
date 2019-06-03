package nodes.implementations.sources;

import nodes.Source;

public class IntegerGenerator extends Source<Integer> {
    private int MAX_NUMBER_GENERATED = (int) Math.exp(6);

    @Override
    protected Integer produceMessage() throws InterruptedException {
        Thread.sleep(500);
        return (int) Math.floor(Math.random() * MAX_NUMBER_GENERATED);
    }
}
