package rs.raf.ccc;

import ccc.parser.LanguageLexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Lexer;


public class Scanner {

    private final Language compiler;

    public Scanner(Language compiler) {
        this.compiler = compiler;
    }

    public Lexer getTokens(CharStream chars) {
        var lex = new LanguageLexer(chars);
        lex.removeErrorListeners();
        lex.addErrorListener(compiler.errorListener());
        return lex;
    }
}
