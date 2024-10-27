package rs.raf.calculator;

import calculator.parser.CalculatorParser;
import calculator.parser.CalculatorParser.StartContext;
import lombok.Getter;
import org.antlr.v4.runtime.*;

import java.util.List;

@Getter
public class Parser {

    private CalculatorParser calculatorParser;

    public StartContext getSyntaxTree(List<Token> tokens) {
        CommonTokenStream tokenStream = new CommonTokenStream(new ListTokenSource(tokens));
        calculatorParser = new CalculatorParser(tokenStream);

        return calculatorParser.start();
    }

}
