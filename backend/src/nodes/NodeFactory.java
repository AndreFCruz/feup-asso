package nodes;

import nodes.implementations.handlers.MD5Converter;
import nodes.implementations.handlers.Uppercase;
import nodes.implementations.sinks.FileWriter;
import nodes.implementations.sinks.Printer;
import nodes.implementations.sources.IntegerGenerator;
import nodes.implementations.sources.StringGenerator;

public class NodeFactory { //TODO: Convert to MAP (String -> Type) and MAP (String -> Constructor)
    public static SourceType convertSourceNameToSourceType(String sourceName) {
        switch (sourceName) {
            case "integer_generator":
                return SourceType.INTEGERGENERATOR;
            case "string_generator":
                return SourceType.STRINGGENERATOR;
            default:
                throw new IllegalStateException("Unexpected value: " + sourceName);
        }
    }

    public static HandlerType convertHandlerNameToHandlerType(String handlerName) {
        switch (handlerName) {
            case "md5_converter":
                return HandlerType.MD5CONVERTER;
            case "upper_case":
                return HandlerType.UPPERCASE;
            default:
                throw new IllegalStateException("Unexpected value: " + handlerName);
        }
    }

    public static SinkType convertSinkNameToSinkType(String sinkName) {
        switch (sinkName) {
            case "file_writer":
                return SinkType.FILEWRITER;
            case "printer":
                return SinkType.PRINTER;
            default:
                throw new IllegalStateException("Unexpected value: " + sinkName);
        }
    }

    public Source createSource(SourceType nodeType) {
        switch (nodeType) {
            case STRINGGENERATOR:
                return new StringGenerator();
            case INTEGERGENERATOR:
                return new IntegerGenerator();
            default:
                throw new IllegalStateException("Unexpected value: " + nodeType);
        }
    }

    public Sink createSink(SinkType nodeType) {
        switch (nodeType) {
            case FILEWRITER:
                return new FileWriter();
            case PRINTER:
                return new Printer();
            default:
                throw new IllegalStateException("Unexpected value: " + nodeType);
        }
    }

    public Handler createHandler(HandlerType nodeType) {
        switch (nodeType) {
            case MD5CONVERTER:
                return new MD5Converter();
            case UPPERCASE:
                return new Uppercase();
            default:
                throw new IllegalStateException("Unexpected value: " + nodeType);
        }
    }

    public enum SourceType {
        INTEGERGENERATOR,
        STRINGGENERATOR,
    }

    public enum SinkType {
        FILEWRITER,
        PRINTER
    }

    public enum HandlerType {
        MD5CONVERTER,
        UPPERCASE,
    }

}
