package pieker.dsl.parser;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import pieker.dsl.Main;
import pieker.dsl.antlr.gen.PiekerLexer;
import pieker.dsl.antlr.gen.PiekerParser;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Getter
@Slf4j
public class ParsingReader {

    public final ParseTreeWalker walker = new ParseTreeWalker();
    public final ParserErrorListener errorListener = new ParserErrorListener();

    private final String fileName;
    private final PiekerParser parser;

    public ParsingReader(String fileName, boolean fromResources) throws IOException {
        this.fileName = fileName;
        PiekerLexer lexer = loadLexer(fileName, fromResources);
        this.parser = loadParser(lexer);
        this.parser.addErrorListener(this.errorListener);
        log.debug("Grammar Parser successfully initialized.");
    }

    /***
     * @param lexer: PiekerLexer object
     * @return a parser object for PIEKER DSL.
     */
    private PiekerParser loadParser(PiekerLexer lexer) {
        assert lexer != null;
        log.debug("Lexer Rules successfully initialized.");
        CommonTokenStream tokens = new CommonTokenStream(lexer); // Tokenize the input using the lexer
        return new PiekerParser(tokens);
    }

    /***
     * @param fileName path to file
     * @param fromResources flag to load from resources or different directory
     * @return PiekerLexer object, containing Lexer rules such as tokens etc.
     * @throws IOException: when filename couldn't be resolved or the file encoding is unknown.
     */
    private PiekerLexer loadLexer(String fileName, boolean fromResources) throws IOException {
        CharStream input;

        if (fromResources){
            InputStream resourceStream = Main.class.getClassLoader().getResourceAsStream("dsl/" + fileName);
            if (resourceStream == null) {
                throw new IOException("Resource not found: " + fileName);
            }
            input = CharStreams.fromStream(resourceStream, StandardCharsets.UTF_8);
        } else {
            input = CharStreams.fromFileName(fileName);
        }
        log.info("finished loading file '{}'.", fileName);

        return new PiekerLexer(input);
    }

    public ParseTree getParseTree(){
        return this.parser.feature();
    }
}
