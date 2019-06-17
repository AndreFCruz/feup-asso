package nodes.implementations.sources;

import nodes.NodeFactory;
import nodes.Source;

public class StringGenerator extends Source<String> {
    static {
        NodeFactory.registerNode(NodeFactory.SourceType.STRING_GENERATOR, StringGenerator::new);
    }

    private static int MAX_STRING_LENGTH = 10;

    @Override
    public String produceMessage() throws InterruptedException {
        Thread.sleep(500);
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789"
                + "abcdefghijklmnopqrstuvxyz";

        StringBuilder sb = new StringBuilder(MAX_STRING_LENGTH);

        for (int i = 0; i < MAX_STRING_LENGTH; i++) {
            int index
                    = (int) (AlphaNumericString.length()
                    * Math.random());

            sb.append(AlphaNumericString
                    .charAt(index));
        }

        return sb.toString();
    }
}
