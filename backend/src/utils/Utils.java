package utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public class Utils {
    public static void shutdownAndAwaitTermination(ExecutorService pool, long time) {
        pool.shutdown(); // Disable new tasks from being submitted
        try {
            // Wait a while for existing tasks to terminate
            if (!pool.awaitTermination(time, TimeUnit.MILLISECONDS)) {
                pool.shutdownNow(); // Cancel currently executing tasks
                // Wait a while for tasks to respond to being cancelled
                if (!pool.awaitTermination(time, TimeUnit.MILLISECONDS))
                    System.err.println("Pool did not terminate");
            }
        } catch (InterruptedException ie) {
            // (Re-)Cancel if current thread also interrupted
            pool.shutdownNow();
            // Preserve interrupt status
            Thread.currentThread().interrupt();
        }
    }

    public static Map<String, Object> JSONObjectToMap(JSONObject jsonObj) throws JSONException {
        Map<String, Object> map = new HashMap<>();
        Iterator<String> keys = jsonObj.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            Object value = jsonObj.get(key);
            if (value instanceof JSONArray) {
                value = JSONArrayToList((JSONArray) value);
            } else if (value instanceof JSONObject) {
                value = JSONObjectToMap((JSONObject) value);
            }
            map.put(key, value);
        }
        return map;
    }

    private static List<Object> JSONArrayToList(JSONArray array) throws JSONException {
        List<Object> list = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            Object value = array.get(i);
            if (value instanceof JSONArray) {
                value = JSONArrayToList((JSONArray) value);
            } else if (value instanceof JSONObject) {
                value = JSONObjectToMap((JSONObject) value);
            }
            list.add(value);
        }
        return list;
    }
}
