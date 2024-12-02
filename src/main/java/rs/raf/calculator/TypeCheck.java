package rs.raf.calculator;

import rs.raf.calculator.ast.Block;
import rs.raf.calculator.ast.Declaration;
import rs.raf.calculator.ast.Expr;
import rs.raf.calculator.ast.Statement;

import java.util.List;
import java.util.Objects;

public class TypeCheck {

    public static void typeCheck(List<Block> blocks) {
        if (blocks.isEmpty()) {
            throw new IllegalArgumentException("No blocks provided for type checking.");
        }

        // Type check all declarations in the top-level block
        for (var tldecl : blocks.getLast().getEnvironment().values()) {
//            checkTopLevel(tldecl);
        }
    }

    private static void typeCheck(Block block) {
        // Type check all statements in the block
        for (var stmt : block.getStatements()) {
//            typeCheck(stmt);
        }

        // Ensure all declarations in the block have been typed
        assert block.getEnvironment().values().stream()
                .map(Declaration::getName)
                .noneMatch(Objects::isNull) : "Some declarations went untyped!";
    }


}
