package rs.raf;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import rs.raf.ccc.Language;
import rs.raf.ccc.Parser;
import rs.raf.ccc.Scanner;
import rs.raf.utils.PrettyPrint;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class Main {
    private static final Language language = new Language();
    public static void main(String[] args) throws IOException {
        if (args.length == 1) {
            runFile(args[0]);
        } else {
            runPrompt();
        }
    }

    private static void runFile(String path) throws IOException {
        run(CharStreams.fromFileName(path));
        if (language.hadError()) System.exit(65);
        if (language.hadRuntimeError()) System.exit(70);
    }

    private static void runPrompt() throws IOException {
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);

        while (true) {
            System.out.print("> ");
            String line = reader.readLine();

            if (line == null || line.equalsIgnoreCase("exit")) {
                /* Terminate the possibly EOF line.  */
                System.out.println();
                break;
            }

            language.setHadError(false);
            language.setHadRuntimeError(false);
            run(CharStreams.fromString(line));
        }
    }

    private static void run(CharStream source) {
        Scanner scanner = new Scanner(language);
        var tokens = scanner.getTokens(source);

        if (language.hadError()) return;

        Parser parser = new Parser(language);
        var tree = parser.getSyntaxTree(tokens);

        /* ANTLR error recovers, so lets print it in its error recovered
           form.  */
        System.out.println("Syntax Tree: " + PrettyPrint.prettyPrintTree(tree, parser.getLanguageParser().getRuleNames()));

        if (language.hadError())
            return;


        //System.out.println("AST:");
        //var pp = new ASTPrettyPrinter(System.out);
        //var program = (StatementList) tree.accept(new CSTtoASTConverter());
        //program.prettyPrint(pp);
    }
}