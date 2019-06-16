package nodes.implementations.sources;

import nodes.Source;
import utils.Log;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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
        Path file = Paths.get(pathname);
        if (!Files.exists(file)) {
            Log.logWarning("File didn't exist!");
            return null;
        }

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
    protected boolean initSettingsHandler() {
        String path = this.getSettingValue("path");
        if (path == null) return false;

        Stream<String> lines = null;
        try {
            lines = loadFile(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (lines == null)
            return false;

        this.iterator = lines.iterator();
        return true;
    }

    @Override
    public String[] getSettingsKeys() {
        return settingsKeys;
    }
}
