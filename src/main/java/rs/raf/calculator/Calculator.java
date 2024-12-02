package rs.raf.calculator;

import org.antlr.v4.runtime.ANTLRErrorListener;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import lombok.*;
import rs.raf.calculator.ast.Location;
import rs.raf.calculator.ast.Position;

@Getter
@Setter
public class Calculator {
    @Getter(AccessLevel.NONE)
    private boolean hadError = false;
    @Getter(AccessLevel.NONE)
    private boolean hadRuntimeError = false;

    public boolean hadError() {
        return hadError;
    }

    public boolean hadRuntimeError() {
        return hadRuntimeError;
    }

    public ANTLRErrorListener errorListener() {
        /* ANTLR reports many kinds of errors, but we're only interested in
           syntax errors, so lets steal those from BaseErrorListener.  */
        return new BaseErrorListener() {
            @Override
            public void syntaxError(Recognizer<?, ?> recognizer,
                                    Object offendingSymbol, int line,
                                    int charPositionInLine, String msg,
                                    RecognitionException e) {
                var pos = new Position(line, charPositionInLine);
                error(new Location(pos, pos), "%s", msg);
            }
        };
    }

    public void error(Location location, String message, Object... args) {
        /* Could be improved to handle end also, later.  */
        var p = location.start();
        System.err.printf ("error: %d:%d: %s\n", p.line(), p.column(),
                           message.formatted(args));
        setHadError(true);
    }

    public static void reportError (Location location, String message, Object... args) {
        System.err.printf ("error: %s: %s\n", location.toString(), message);
    }
}
