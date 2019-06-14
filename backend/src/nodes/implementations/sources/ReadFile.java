package nodes.implementations.sources;

import nodes.Source;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Stream;

public class ReadFile extends Source<String> {
    private Iterator<String> iterator;

    private synchronized static Stream<String> loadFile(String pathname) throws IOException {
        return Files.lines(Paths.get(pathname));
    }

    @Override
    public String produceMessage() throws InterruptedException {
        Thread.sleep(500);
        if (this.iterator.hasNext())
            return this.iterator.next();

        return null;
    }

    @Override
    public void initializeSettings(Map<String, String> settings) {
        super.initializeSettings(settings);
        try {
            Stream<String> lines = loadFile(settings.get("path"));
            this.iterator = lines.iterator();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
