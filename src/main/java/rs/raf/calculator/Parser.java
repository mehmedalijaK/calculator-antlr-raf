package rs.raf.calculator;

import calculator.parser.CalculatorParser;
import calculator.parser.CalculatorParser.StartContext;
import lombok.Getter;

import org.antlr.v4.runtime.*;

public class Parser {
    private final Calculator compiler;

    @Getter
    private CalculatorParser calculatorParser;

    public Parser(Calculator compiler) {
        this.compiler = compiler;
    }

    public StartContext getSyntaxTree(Lexer tokens) {
        CommonTokenStream tokenStream = new CommonTokenStream(tokens);
        calculatorParser = new CalculatorParser(tokenStream);
        calculatorParser.removeErrorListeners();
        calculatorParser.addErrorListener(compiler.errorListener());

        return calculatorParser.start();
    }

}
