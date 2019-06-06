package nodes;

import nodes.implementations.handlers.MD5Converter;
import nodes.implementations.handlers.Uppercase;
import nodes.implementations.sinks.FileWriter;
import nodes.implementations.sinks.Printer;
import nodes.implementations.sources.IntegerGenerator;
import nodes.implementations.sources.StringGenerator;

public class NodeFactory {
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

    public Source createSource(SourceType nodeType) {
        switch (nodeType) {
            case INTEGERGENERATOR:
                return new StringGenerator();
            case STRINGGENERATOR:
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

}
