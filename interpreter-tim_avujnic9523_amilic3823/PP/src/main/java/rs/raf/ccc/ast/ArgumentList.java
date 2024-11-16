package rs.raf.ccc.ast;

import java.util.List;

public class ArgumentList extends Tree {
    private List<Expression> expressions;

    public ArgumentList(Location location ,List<Expression> expressions) {
        super(location);
        this.expressions = expressions;
    }

    public List<Expression> getExpressions() {
        return expressions;
    }

    @Override
    public void prettyPrint(ASTPrettyPrinter pp) {
        pp.node("arguments", () -> {
            // Ispisivanje prvog argumenta
            expressions.get(0).prettyPrint(pp);

            // Ispisivanje preostalih argumenata, sa zarezom između njih
            for (int i = 1; i < expressions.size(); i++) {
                pp.node("comma", () -> {});  // Ovaj čvor može biti korišćen da označi zarez
                expressions.get(i).prettyPrint(pp);
            }
        });
    }

}

