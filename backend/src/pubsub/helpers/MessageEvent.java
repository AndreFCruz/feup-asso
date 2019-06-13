package pubsub.helpers;

public class MessageEvent {
    public String publisherId;
    public int messageHash;
    private Long timeToLive;    // In milliseconds
    private long arrivalTime;   // In milliseconds

    public MessageEvent(String publisherId, int messageHash) {
        this(publisherId, messageHash, null);
    }

    private MessageEvent(String publisherId, int messageHash, Long timeToLive) {
        this.publisherId = publisherId;
        this.messageHash = messageHash;
        this.timeToLive = timeToLive;
        this.arrivalTime = System.currentTimeMillis();
    }

    public boolean isAlive() {
        return this.timeToLive == null ||
                this.arrivalTime + this.timeToLive > System.currentTimeMillis();

    }
}