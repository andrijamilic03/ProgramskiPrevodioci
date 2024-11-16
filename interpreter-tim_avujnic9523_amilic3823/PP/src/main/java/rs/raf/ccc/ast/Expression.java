package rs.raf.ccc.ast;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/** Bazna klasa za sve tipove izraza (Expression). */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public abstract class Expression extends Tree {

    public Expression(Location location) {
        super(location);
    }

    /** Binarni izraz (PLUS, MINUS, AND, OR, itd.) */
    @Getter
    @Setter
    @EqualsAndHashCode(callSuper = true)
    public static class BinaryExpression extends Expression {
        private Expression left;
        private Expression right;
        private String operator;

        public BinaryExpression(Location location, Expression left, String operator, Expression right) {
            super(location);
            this.left = left;
            this.operator = operator;
            this.right = right;
        }

        @Override
        public void prettyPrint(ASTPrettyPrinter pp) {
            pp.node("binary expression %s".formatted(operator), () -> {
                left.prettyPrint(pp);
                right.prettyPrint(pp);
            });
        }
    }

    /** Unarni izraz (NOT, INCREMENT, DECREMENT) */
    @Getter
    @Setter
    @EqualsAndHashCode(callSuper = true)
    public static class UnaryExpression extends Expression {
        private Expression expr;
        private String operator;

        public UnaryExpression(Location location, String operator, Expression expr) {
            super(location);
            this.operator = operator;
            this.expr = expr;
        }

        @Override
        public void prettyPrint(ASTPrettyPrinter pp) {
            pp.node("unary expression %s".formatted(operator), () -> expr.prettyPrint(pp));
        }
    }

    /** Literalni izraz (INT_LIT, BOOL_LIT) */
    @Getter
    @Setter
    @EqualsAndHashCode(callSuper = true)
    public static class LiteralExpression extends Expression {
        private Object value;

        public LiteralExpression(Location location, Object value) {
            super(location);
            this.value = value;
        }

        @Override
        public void prettyPrint(ASTPrettyPrinter pp) {
            // Dodajemo prazan Runnable jer nema podizraza za prikazivanje
            pp.node("literal %s".formatted(value.toString()), () -> {});
        }
    }

    /** Pristup elementima niza (ID[expression]) */
    @Getter
    @Setter
    @EqualsAndHashCode(callSuper = true)
    public static class ArrayAccessExpression extends Expression {
        private String arrayName;
        private Expression index;

        public ArrayAccessExpression(Location location, String arrayName, Expression index) {
            super(location);
            this.arrayName = arrayName;
            this.index = index;
        }

        @Override
        public void prettyPrint(ASTPrettyPrinter pp) {
            pp.node("array access %s".formatted(arrayName), () -> index.prettyPrint(pp));
        }
    }

    /** Referenca na promenljivu (ID) */
    @Getter
    @Setter
    @EqualsAndHashCode(callSuper = true)
    public static class VariableReferenceExpression extends Expression {
        private String name;

        public VariableReferenceExpression(Location location, String name) {
            super(location);
            this.name = name;
        }

        @Override
        public void prettyPrint(ASTPrettyPrinter pp) {
            // Dodajemo prazan Runnable jer nema podizraza za prikazivanje
            pp.node("variable reference %s".formatted(name), () -> {});
        }
    }

    /** Grupisani izraz (izrazi u zagradama, kao (expression)) */
    @Getter
    @Setter
    @EqualsAndHashCode(callSuper = true)
    public static class GroupingExpression extends Expression {
        private Expression expr;

        public GroupingExpression(Location location, Expression expr) {
            super(location);
            this.expr = expr;
        }

        @Override
        public void prettyPrint(ASTPrettyPrinter pp) {
            pp.node("grouped expression", () -> expr.prettyPrint(pp));
        }
    }
}
