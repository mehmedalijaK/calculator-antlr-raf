package rs.raf.calculator.ast;

import lombok.*;

@Getter
@Setter
@EqualsAndHashCode(callSuper=true)
public class VarRef extends Expr {
    private String varName;

    protected VarRef(Location location, String varName) {
        super(location);
        this.varName = varName;
    }

    @Override
    public void prettyPrint(ASTPrettyPrinter pp) {
	pp.node("var", () -> pp.terminal(varName));
    }
}
