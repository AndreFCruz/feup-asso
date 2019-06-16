package nodes.implementations.sources;

import nodes.Source;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class FetchUrl extends Source<String> {
    private static String[] settingsKeys = new String[]{"url"};
    private BufferedReader buffer;

    public FetchUrl() {
        this.registerSettings(settingsKeys);
    }

    private synchronized static BufferedReader loadUrl(String url) throws IOException {
        URLConnection connection = new URL(url).openConnection();
        return new BufferedReader(
                new InputStreamReader(
                        connection.getInputStream()));
    }

    @Override
    public String produceMessage() throws InterruptedException {
        Thread.sleep(500);
        String inputLine;
        try {
            if ((inputLine = this.buffer.readLine()) != null)
                return inputLine;
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            this.buffer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void initSettingsHandler() {
        String url = this.getSettingValue("url");
        if (url == null) return;

        try {
            this.buffer = loadUrl(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String[] getSettingsKeys() {
        return settingsKeys;
    }

}
