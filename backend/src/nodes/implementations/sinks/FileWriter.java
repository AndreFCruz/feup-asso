package nodes.implementations.sinks;

import nodes.Sink;

public class FileWriter extends Sink.EndSink<Object> {

//    synchronized public SAVE_STATE saveFile(String fileName, String pathname, byte[] data) throws IOException {
//        if (memoryManager.getAvailableMemory() < data.length) {
//            Log.logWarning("Not enough space for saveFile!");
//            return SAVE_STATE.FAILURE;
//        }
//        String filePath = pathname + "/" + fileName;
//
//        if (Files.exists(Paths.get(filePath))) {
//            Log.logWarning("File already exists!");
//            return SAVE_STATE.EXISTS;
//        }
//
//        OutputStream out = Files.newOutputStream(Paths.get(filePath));
//        out.write(data);
//        out.close();
//
//        memoryManager.increaseUsedMemory(data.length);
//        return SAVE_STATE.SUCCESS;
//    }
//

    // May block when handling message
    @Override
    public Void handleMessage(Object message) {
        System.out.println("Sink id " + this.getId() + " | Received the message: " + message);
        return null;
    }
}
