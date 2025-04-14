package pieker.dsl.parser;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.ParseCancellationException;

@Getter
@Setter
@NoArgsConstructor
public class ParserErrorListener extends BaseErrorListener {
    private String message;

    public boolean isFail() {
        return this.message != null;
    }

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
        throw new ParseCancellationException("line " + line + ":" + charPositionInLine + " " + msg);
    }
}
