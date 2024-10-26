package rs.raf.calculator;

import calculator.parser.CalculatorLexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Token;

import java.util.ArrayList;
import java.util.List;

public class Scanner {

    public List<Token> getAllTokens(String expression) {
        CharStream chars = CharStreams.fromString(expression);
        Lexer lexer = new CalculatorLexer(chars);

        List<? extends Token> rawTokens = lexer.getAllTokens();
        List<Token> tokens = new ArrayList<>(rawTokens.size());

        tokens.addAll(rawTokens);

        return tokens;
    }
}
