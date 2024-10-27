package rs.raf.calculator.ast;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.*;

import calculator.parser.CalculatorParser.*;
import calculator.parser.CalculatorLexer;
import calculator.parser.CalculatorVisitor;

public class CSTtoASTConverter extends AbstractParseTreeVisitor<Tree> implements CalculatorVisitor<Tree> {

    @Override
    public Tree visitStart(StartContext ctx) {
        var stmts = ctx.statement()
            /* Take all the parsed statements, ... */
            .stream()
            /* ... visit them using this visitor, ... */
            .map(this::visit)
            /* ... then cast them to statements (because 'start: statement*',
               so they can't be anything else), ...  */
            .map(x -> (Statement) x)
            /* ... and put them into a list.  */
            .toList();
        return new StatementList(getLocation(ctx), stmts);
    }

    @Override
    public Tree visitStatement(StatementContext ctx) {
        /* A statement just contains a child.  Visit it instead.  It has to be
           a statement, so we check for that by casting.

           Note that we assume here that statement is defined as an OR of many
           different rules, with its first child being whatever the statement
           actually is, and the rest, if any, are unimportant.  */
        var substatement = visit(ctx.getChild(0));
        if (substatement instanceof Expr e) {
            /* It's an expression statement.  */
            substatement = new ExprStmt(e.getLocation(), e);
        }
        return (Statement) substatement;
    }

    @Override
    public Tree visitDeclaration(DeclarationContext ctx) {
        /* Declarations consist of a name and an expression, which is the value
           of the variable we're declaring.  */
        var name = ctx.IDENTIFIER().getText();
        var value = (Expr) visit(ctx.expr());
        return new Declaration(getLocation(ctx), name, value);
    }

    @Override
    public Tree visitPrintStatement(PrintStatementContext ctx) {
        var args = ctx.expr()
            /* Take all the parsed arguments, ... */
            .stream()
            /* ... visit them using this visitor, ... */
            .map(this::visit)
            /* ... then cast them to expressions, ...  */
            .map(x -> (Expr) x)
            /* ... and put them into a list.  */
            .toList();
        return new PrintStmt(getLocation(ctx), args);
    }

    @Override
    public Tree visitExpr(ExprContext ctx) {
        /* expr: additionExpr; so we just return that.  */
        return (Expr) visit(ctx.additionExpr());
    }

    @Override
    public Tree visitAdditionExpr(AdditionExprContext ctx) {
        /* Now this one is annoying.  We have a rule structure of:
             e: f (op=(OP1 | OP2 | ...) f)*;

           ... so, we have many 'f's, out of which first is initial, and the
           rest are combined in accordance to op.  To make this a little
           easier, lets label the first 'f' 'initial', and the others 'rest':

             e: initial=f (op=(OP1 | OP2 | ...) f)*;

           Following that, we can iterate 'op' and 'f' using the same indices,
           and combine them.

           See
           https://github.com/antlr/antlr4/blob/dev/doc/parser-rules.md#rule-element-labels
           ... for information about labels.  */

        var value = (Expr) visit(ctx.initial);

        assert ctx.op.size() == ctx.rest.size();
        for (int i = 0; i < ctx.op.size(); i++) {
            var op = ctx.op.get(i);
            var rhs = (Expr) visit(ctx.rest.get(i));

            var exprOp = switch (op.getType()) {
            case CalculatorLexer.PLUS -> Expr.Operation.ADD;
            case CalculatorLexer.MINUS -> Expr.Operation.SUB;
            default -> throw new IllegalArgumentException("unhandled expr op " + op);
            };

            /* For an expression A+B+C, the location spanning A+B is the
               location from the start of A to the end of B, which will
               conveniently be created by
               {@code A.getLocation().span(b.getLocation())}.  */
            var loc = value.getLocation().span(rhs.getLocation());
            value = new Expr(loc, exprOp, value, rhs);
        }
        return value;
    }

    @Override
    public Tree visitMultiplicationExpr(MultiplicationExprContext ctx) {
        /* This one is even more annoying, because it's the exact same.  It is
           possible to abstract and not specify twice, but I won't do that
           here.  It's long and ugly.  */
        var value = (Expr) visit(ctx.initial);

        assert ctx.op.size() == ctx.rest.size();
        for (int i = 0; i < ctx.op.size(); i++) {
            var op = ctx.op.get(i);
            var rhs = (Expr) visit(ctx.rest.get(i));

            /* This part changed, I guess.  */
            var exprOp = switch (op.getType()) {
            case CalculatorLexer.STAR -> Expr.Operation.MUL;
            case CalculatorLexer.SLASH -> Expr.Operation.DIV;
            default -> throw new IllegalArgumentException("unhandled expr op " + op);
            };

            var loc = value.getLocation().span(rhs.getLocation());
            value = new Expr(loc, exprOp, value, rhs);
        }
        return value;
    }

    @Override
    public Tree visitExponentExpr(ExponentExprContext ctx) {
        /* This is a right-associative operation, specified as:
             exp: term (CARET exp)?;

           ... so, it's even easier.  */
        var lhs = (Expr) visit(ctx.lhs);
        if (ctx.rhs == null)
            return lhs;

        var rhs = (Expr) visit(ctx.rhs);
        return new Expr(lhs.getLocation().span(rhs.getLocation()),
                        Expr.Operation.POW, lhs, rhs);
    }

    @Override
    public Tree visitNumberConstant(NumberConstantContext ctx) {
        /* Each labeled alternative gets its own visitor, making this quite
           convenient.  */
        return new NumberLit(getLocation(ctx), Double.parseDouble(ctx.getText()));
    }

    @Override
    public Tree visitVariableReference(VariableReferenceContext ctx) {
        return new VarRef(getLocation(ctx), ctx.IDENTIFIER().getText());
    }

    @Override
    public Tree visitGroupingOperator(GroupingOperatorContext ctx) {
        /* This one is easy.  */
        return (Expr) visit(ctx.expr());
    }

    @Override
    public Tree visitVectorConstructor(VectorConstructorContext ctx) {
        /* This one is easy, too - the rule just invokes vectorLiteral.  */
        return (Expr) visit(ctx.vectorLiteral());
    }

    @Override
    public Tree visitVectorLiteral(VectorLiteralContext ctx) {
        /* It's kinda like a function.  */
        var args = ctx.expr()
            /* Take all the parsed arguments, ... */
            .stream()
            /* ... visit them using this visitor, ... */
            .map(this::visit)
            /* ... then cast them to expressions, ...  */
            .map(x -> (Expr) x)
            /* ... and put them into a list.  */
            .toList();
        return new VectorExpr(getLocation(ctx), args);
    }

    /* Helpers.  */
    /** Returns the range that this subtree is in.  */
    private static Location getLocation(ParserRuleContext context) {
        return getLocation(context.getStart())
            .span(getLocation(context.getStop ()));
    }

    /** Returns the location this terminal is in.  */
    private static Location getLocation(TerminalNode term) {
        return getLocation(term.getSymbol());
    }

    /** Returns the location this token is in.  */
    private static Location getLocation(Token token) {
        /* The token starts at the position ANTLR provides us.  */
        var start = new Position(token.getLine(), token.getCharPositionInLine());

        /* But it does not provide a convenient way to get where it ends, so we
           have to calculate it based on length.  */
        assert !token.getText ().contains ("\n")
            : "CSTtoASTConverter assumes single-line tokens";
        var length = token.getText ().length ();
        assert length > 0;

        /* And then put it together.  */
        var end = new Position (start.line (), start.column () + length - 1);
        return new Location (start, end);
    }
}
