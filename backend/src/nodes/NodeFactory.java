package nodes;

import nodes.implementations.handlers.MD5Converter;
import nodes.implementations.handlers.Uppercase;
import nodes.implementations.sinks.FileWriter;
import nodes.implementations.sinks.Printer;
import nodes.implementations.sources.IntegerGenerator;
import nodes.implementations.sources.StringGenerator;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public class NodeFactory {
    private static Map<String, SourceType> sourceNameToSourceType = new HashMap<>() {{
        put("INTEGER_GENERATOR", SourceType.INTEGER_GENERATOR);
        put("STRING_GENERATOR", SourceType.STRING_GENERATOR);
    }};
    private static Map<String, HandlerType> handlerNameToHandlerType = new HashMap<>() {{
        put("MD5_CONVERTER", HandlerType.MD5_CONVERTER);
        put("UPPER_CASE_CONVERTER", HandlerType.UPPER_CASE_CONVERTER);
    }};
    private static Map<String, SinkType> sinkNameToSinkType = new HashMap<>() {{
        put("FILE_WRITER", SinkType.FILE_WRITER);
        put("PRINTER", SinkType.PRINTER);
    }};
    private static Map<SourceType, Supplier<Source>> sourceTypeToSourceNode = new HashMap<>() {{
        put(SourceType.INTEGER_GENERATOR, IntegerGenerator::new);
        put(SourceType.STRING_GENERATOR, StringGenerator::new);
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
        return sourceNameToSourceType.get(sourceName);
    }

    public static HandlerType convertHandlerNameToHandlerType(String handlerName) {
        return handlerNameToHandlerType.get(handlerName);
    }

    public static SinkType convertSinkNameToSinkType(String sinkName) {
        return sinkNameToSinkType.get(sinkName);
    }

    public static Set<String> getSourceNames() {
        return sourceNameToSourceType.keySet();
    }

    public static Set<String> getHandlerNames() {
        return handlerNameToHandlerType.keySet();
    }

    public static Set<String> getSinkNames() {
        return sinkNameToSinkType.keySet();
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
