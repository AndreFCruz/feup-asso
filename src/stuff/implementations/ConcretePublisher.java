package stuff.implementations;


import stuff.AbstractEntity;
import stuff.Publisher;

public class ConcretePublisher extends AbstractEntity<Integer> implements Publisher<Integer> {
    
    private int MAX_NUMBER_GENERATED = (int) Math.exp(6);

    @Override
    public Integer getMessage() throws InterruptedException {
        Thread.sleep(500);
        return (int) Math.floor(Math.random() * MAX_NUMBER_GENERATED);
    }

}
