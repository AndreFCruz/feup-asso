package nodes.implementations.sources;

import nodes.Source;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

public class FetchUrl extends Source<String> {
    private BufferedReader buffer;

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
    public boolean initializeSettings(Map<String, String> settings) {
        if (settings == null || settings.get("url") == null)
            return false;

        try {
            this.buffer = loadUrl(settings.get("url"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }
}
