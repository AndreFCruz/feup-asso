package stuff;

public class IntPublisher extends Publisher<Integer> {
    private Integer MAX_NUMBER_GENERATED = (int) Math.exp(6);

    @Override
    public Integer getMessage() throws InterruptedException {
        System.out.println("generating message for pub: " + this.getId());
        Thread.sleep(1000);
        System.out.println("after sleep: " + this.getId());
        return (int) Math.floor(Math.random() * MAX_NUMBER_GENERATED);
    }

}