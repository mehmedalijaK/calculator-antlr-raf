package rs.raf.calculator.ast;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
public class Block extends Tree
{
    private Map<String, Declaration> environment = new HashMap<>();
    private List<Statement> statements = new ArrayList<>();

    public Block (Location location)
    {
        super (location);
    }

    @Override
    public void prettyPrint(ASTPrettyPrinter pp) {
        pp.node("block",
                () -> {
                    statements.forEach(x -> x.prettyPrint(pp));
                });
    }
}

