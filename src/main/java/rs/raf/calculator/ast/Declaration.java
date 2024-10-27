package rs.raf.calculator.ast;

import lombok.*;

/** A variable declaration.  */
@Getter
@Setter
@EqualsAndHashCode(callSuper=true)
public class Declaration extends Statement {
    private String name;
    private Expr value;

    public Declaration(Location location, String name, Expr value) {
	super(location);
	this.name = name;
	this.value = value;
    }

    @Override
    public void prettyPrint(ASTPrettyPrinter pp) {
	pp.node("declaration of %s".formatted(name),
		() -> {
		    value.prettyPrint(pp);
		});
    }
}
