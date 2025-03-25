package pieker.dsl.parser;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.antlr.v4.runtime.ANTLRErrorListener;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.dfa.DFA;

import java.util.BitSet;

@Getter
@Setter
@NoArgsConstructor
public class ParserErrorListener implements ANTLRErrorListener {
    private String message;

    public boolean isFail() {
        return this.message != null;
    }

    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int position, String message, RecognitionException e) {
        // Stub Class for Parser
    }

    @Override
    public void reportAmbiguity(Parser parser, DFA dfa, int i, int i1, boolean b, BitSet bitSet, ATNConfigSet atnConfigSet) {
        //Stub Class for Parser
    }

    @Override
    public void reportAttemptingFullContext(Parser parser, DFA dfa, int i, int i1, BitSet bitSet, ATNConfigSet atnConfigSet) {
        //Stub Class for Parser
    }

    @Override
    public void reportContextSensitivity(Parser parser, DFA dfa, int i, int i1, int i2, ATNConfigSet atnConfigSet) {
        //Stub Class for Parser
    }
}
