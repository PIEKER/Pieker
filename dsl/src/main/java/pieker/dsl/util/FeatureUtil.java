package pieker.dsl.util;

import org.antlr.v4.runtime.tree.TerminalNode;

public class FeatureUtil {

    private FeatureUtil(){}

    public static int getShiftedLineIndex(TerminalNode node) {
        int count = 0;

        for(char c : node.getText().toCharArray()) {
            if (c == '\n') {
                ++count;
            } else if (!Character.isWhitespace(c)) {
                break;
            }
        }

        return node.getSymbol().getLine() + count;
    }
}
