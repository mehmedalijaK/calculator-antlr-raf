package rs.raf.calculator;

import calculator.parser.CalculatorLexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Lexer;

public class Scanner {
    private final Calculator compiler;

    public Scanner(Calculator compiler) {
        this.compiler = compiler;
    }

    public Lexer getTokens(CharStream chars) {
        var lex = new CalculatorLexer(chars);
        lex.removeErrorListeners();
        lex.addErrorListener(compiler.errorListener());
        return lex;
    }
}
