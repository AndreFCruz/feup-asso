package nodes;

import nodes.implementations.handlers.*;
import nodes.implementations.sinks.*;
import nodes.implementations.sources.*;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class NodeFactory {
    private static Map<SourceType, Supplier<Source>> sourceTypeToSourceNode = new HashMap<>();
    private static Map<HandlerType, Supplier<Handler>> handlerTypeToHandlerNode = new HashMap<>();
    private static Map<SinkType, Supplier<Sink>> sinkTypeToSinkNode = new HashMap<>();

    static {
        NodeFactory.registerSources();
        NodeFactory.registerHandlers();
        NodeFactory.registerSinks();
    }

    private static void registerSources() {
        registerNode(SinkType.FILE_WRITER, FileWriter::new);
        registerNode(SinkType.PRINTER, Printer::new);
    }

    private static void registerHandlers() {
        registerNode(HandlerType.AND, AND::new);
        registerNode(HandlerType.IF, If::new);
        registerNode(HandlerType.MD5_HASH, MD5Converter::new);
        registerNode(HandlerType.OR, OR::new);
        registerNode(HandlerType.PARSE_FLOAT, ParseFloat::new);
        registerNode(HandlerType.PARSE_INT, ParseInt::new);
        registerNode(HandlerType.PAIRWISE_PRODUCT, Product::new);
        registerNode(HandlerType.REGEX_MATCH, RegexMatch::new);
        registerNode(HandlerType.REGEX_REPLACE, RegexReplace::new);
        registerNode(HandlerType.ROLLING_AVERAGE, RollingAverage::new);
        registerNode(HandlerType.ROLLING_SUM, RollingSum::new);
        registerNode(HandlerType.TO_STRING, ToString::new);
        registerNode(HandlerType.TO_UPPERCASE, Uppercase::new);
        registerNode(HandlerType.XOR, XOR::new);
    }

    private static void registerSinks() {
        registerNode(SourceType.FETCH_URL, FetchUrl::new);
        registerNode(SourceType.FILE_READER, FileReader::new);
        registerNode(SourceType.INTEGER_GENERATOR, IntegerGenerator::new);
        registerNode(SourceType.STRING_GENERATOR, StringGenerator::new);
    }

    /**
     * Functions for registering new nodes on this factory
     * (in order to extend this NodeFactory's nodes without changing core source code)
     */
    public static Supplier<Source> registerNode(SourceType sourceType, Supplier<Source> constructor) {
        return NodeFactory.sourceTypeToSourceNode.put(sourceType, constructor);
    }

    public static Supplier<Handler> registerNode(HandlerType handlerType, Supplier<Handler> constructor) {
        return NodeFactory.handlerTypeToHandlerNode.put(handlerType, constructor);
    }

    public static Supplier<Sink> registerNode(SinkType sinkType, Supplier<Sink> constructor) {
        return NodeFactory.sinkTypeToSinkNode.put(sinkType, constructor);
    }

    /**
     * Functions for getting a Node Supplier (constructor) from their String type names.
     */
    public static SourceType convertSourceNameToSourceType(String sourceName) {
        return SourceType.valueOf(sourceName);
    }

    public static HandlerType convertHandlerNameToHandlerType(String handlerName) {
        return HandlerType.valueOf(handlerName);
    }

    public static SinkType convertSinkNameToSinkType(String sinkName) {
        return SinkType.valueOf(sinkName);
    }

    /**
     * Fetch all Node names of the given type (Source/Handler/Sink).
     */
    public static SourceType[] getSourceNames() {
        return SourceType.values();
    }

    public static HandlerType[] getHandlerNames() {
        return HandlerType.values();
    }

    public static SinkType[] getSinkNames() {
        return SinkType.values();
    }

    /**
     * Create Node of the given type (Source/Handler/Sink)
     */
    public static Source createSource(SourceType nodeType) {
        return sourceTypeToSourceNode.get(nodeType).get();
    }

    public static Handler createHandler(HandlerType nodeType) {
        return handlerTypeToHandlerNode.get(nodeType).get();
    }

    public static Sink createSink(SinkType nodeType) {
        return sinkTypeToSinkNode.get(nodeType).get();
    }


    /**
     * Hierarchy for Node Types.
     */
    interface NodeType {
        String name();
    }

    public enum SourceType implements NodeType {
        INTEGER_GENERATOR,
        STRING_GENERATOR,
        FILE_READER,
        FETCH_URL
    }

    public enum SinkType implements NodeType {
        FILE_WRITER,
        PRINTER
    }

    public enum HandlerType implements NodeType {
        MD5_HASH,
        TO_UPPERCASE,
        ROLLING_SUM,
        PAIRWISE_PRODUCT,
        ROLLING_AVERAGE,
        IF,                 // If message is false, return null (no message)
        REGEX_MATCH,        // Return whether message matches regex expression
        REGEX_REPLACE,
        AND,
        OR,
        XOR,
        PARSE_INT,
        PARSE_FLOAT,
        TO_STRING,
    }

}
