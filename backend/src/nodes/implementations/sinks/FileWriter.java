package nodes.implementations.sinks;

import nodes.Sink;
import utils.Log;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.Map;

public class FileWriter extends Sink.EndSink<Object> {
    private Path file;

    synchronized private Path createFile(String filePath) {
        Path file = Paths.get(filePath);
        if (Files.exists(file)) {
            Log.logWarning("File already exists!");
            return null;
        }

        try {
            Files.write(file, Collections.singletonList(""), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return file;
    }

    // May block when handling message
    @Override
    public Void handleMessage(Object message) {
        try {
            Files.writeString(file, message.toString() + "\n", StandardCharsets.UTF_8, StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Sink id " + this.getId() + " | Received the message: " + message);
        return null;
    }

    @Override
    public boolean initializeSettings(Map<String, String> settings) {
        if (settings == null || settings.get("path") == null)
            return false;

        this.file = createFile(settings.get("path"));

        return this.file != null;
    }
}
