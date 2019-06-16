package nodes.implementations.sources;

import nodes.Source;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.stream.Stream;

public class FileReader extends Source<String> {
    private static String[] settingsKeys = new String[]{"path"};
    private Iterator<String> iterator;

    public FileReader() {
        this.registerSettings(settingsKeys);
    }

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
    protected void initSettingsHandler() {
        String path = this.getSettingValue("path");
        if (path == null) return;

        try {
            Stream<String> lines = loadFile(path);
            this.iterator = lines.iterator();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String[] getSettingsKeys() {
        return settingsKeys;
    }
}
