package rs.raf.ccc;

import ccc.parser.LanguageParser;
import lombok.Getter;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;

public class Parser {
    private final Language compiler;

    @Getter
    private LanguageParser languageParser;

    public Parser(Language compiler) {
        this.compiler = compiler;
    }

    public LanguageParser.StartContext getSyntaxTree(Lexer tokens) {
        CommonTokenStream tokenStream = new CommonTokenStream(tokens);
        languageParser = new LanguageParser(tokenStream);
        languageParser.removeErrorListeners();
        languageParser.addErrorListener(compiler.errorListener());

        return languageParser.start();
    }
}
