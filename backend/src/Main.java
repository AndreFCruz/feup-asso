import manager.Broker;
import nodes.Handler;
import nodes.Sink;
import nodes.Source;
import nodes.implementations.handlers.MD5Converter;
import nodes.implementations.handlers.Uppercase;
import nodes.implementations.sinks.FileWriter;
import nodes.implementations.sinks.Printer;
import nodes.implementations.sources.IntegerGenerator;
import nodes.implementations.sources.StringGenerator;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) {
        final ExecutorService executor = Executors.newCachedThreadPool();

        // Create Broker
        Broker<Object> manager = new Broker<>();

        // Create Publishers and populate registry
        Source<String> stringSource = new StringGenerator();
        String stringSourceKey = manager.register(stringSource);

        Source<Integer> integerSource = new IntegerGenerator();
        String integerSourceKey = manager.register(integerSource);

        // Create Handlers
        Handler<Object, String> md5Converter = new MD5Converter();
        String[] md5ConverterKeys = manager.register(md5Converter).split("-");

        Handler<String, String> uppercase = new Uppercase();
        String[] uppercaseKeys = manager.register(uppercase).split("-");

        // Create Sinks
        Sink<Object, Void> printerSink = new Printer();
        String printerSinkKey = manager.register(printerSink);

        Sink<Object, Void> fileWriterSink = new FileWriter();
        String fileWriterSinkKey = manager.register(fileWriterSink);


        // // Manage subscriptions
        manager.addSubscriber(printerSinkKey, stringSourceKey);
        manager.addSubscriber(printerSinkKey, integerSourceKey);
        manager.addSubscriber(uppercaseKeys[0], stringSourceKey);
        manager.addSubscriber(fileWriterSinkKey, uppercaseKeys[1]);

        executor.submit(stringSource);
        executor.submit(integerSource);

        executor.submit(uppercase);

        executor.submit(printerSink);
        executor.submit(fileWriterSink);

        ExecutorService brokerExec = Executors.newSingleThreadExecutor();
        brokerExec.execute(manager);

        new Thread(() -> {
            try {
                long brokerRunTime = 5000;
                System.out.println("Trying to block Broker's execution in " + brokerRunTime + " millisecs");
                brokerExec.awaitTermination(brokerRunTime, TimeUnit.MILLISECONDS);
                executor.shutdownNow();
                System.out.println("#...#");
            } catch (InterruptedException e) {
                System.out.println("Interrupted kill-switch Thread, lol");
            }
        }).start();
    }
}
