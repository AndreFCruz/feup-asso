package stuff;

public class IntPublisher extends Publisher<Integer> {
    private Integer MAX_NUMBER_GENERATED = (int) Math.exp(6);

    @Override
    public Integer getMessage() throws InterruptedException {
        Thread.sleep(1000);
        return (int) Math.floor(Math.random() * MAX_NUMBER_GENERATED);
    }

}