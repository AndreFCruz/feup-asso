package nodes;

import nodes.implementations.handlers.MD5Converter;
import nodes.implementations.handlers.Uppercase;
import nodes.implementations.sinks.FileWriter;
import nodes.implementations.sinks.Printer;
import nodes.implementations.sources.FileReader;
import nodes.implementations.sources.IntegerGenerator;
import nodes.implementations.sources.StringGenerator;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class NodeFactory {
    private static Map<SourceType, Supplier<Source>> sourceTypeToSourceNode = new HashMap<>() {{
        put(SourceType.INTEGER_GENERATOR, IntegerGenerator::new);
        put(SourceType.STRING_GENERATOR, StringGenerator::new);
        put(SourceType.FILE_READER, FileReader::new);
    }};
    private static Map<HandlerType, Supplier<Handler>> handlerTypeToHandlerNode = new HashMap<>() {{
        put(HandlerType.MD5_CONVERTER, MD5Converter::new);
        put(HandlerType.UPPER_CASE_CONVERTER, Uppercase::new);
    }};
    private static Map<SinkType, Supplier<Sink>> sinkTypeToSinkNode = new HashMap<>() {{
        put(SinkType.FILE_WRITER, FileWriter::new);
        put(SinkType.PRINTER, Printer::new);
    }};

    public static SourceType convertSourceNameToSourceType(String sourceName) {
        return SourceType.valueOf(sourceName);
    }

    public static HandlerType convertHandlerNameToHandlerType(String handlerName) {
        return HandlerType.valueOf(handlerName);
    }

    public static SinkType convertSinkNameToSinkType(String sinkName) {
        return SinkType.valueOf(sinkName);
    }

    public static SourceType[] getSourceNames() {
        return SourceType.values();
    }

    public static HandlerType[] getHandlerNames() {
        return HandlerType.values();
    }

    public static SinkType[] getSinkNames() {
        return SinkType.values();
    }

    public Source createSource(SourceType nodeType) {
        return sourceTypeToSourceNode.get(nodeType).get();
    }

    public Handler createHandler(HandlerType nodeType) {
        return handlerTypeToHandlerNode.get(nodeType).get();
    }

    public Sink createSink(SinkType nodeType) {
        return sinkTypeToSinkNode.get(nodeType).get();
    }


    public enum SourceType {
        INTEGER_GENERATOR,
        STRING_GENERATOR,
        FILE_READER
    }

    public enum SinkType {
        FILE_WRITER,
        PRINTER
    }

    public enum HandlerType {
        MD5_CONVERTER,
        UPPER_CASE_CONVERTER,
    }

}
