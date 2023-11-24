package com.apple.library.impl;

//import com.apple.library.foundation.NSLayoutRelation;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.LinkedHashMap;
//import java.util.LinkedList;
//import java.util.List;
//import java.util.Map;
//import java.util.function.Supplier;

public class LayoutEngineImpl {


//    public static class Expression {
//
//        private List<Term> terms;
//
//        private double constant;
//
//        public Expression(double constant) {
//            this.constant = constant;
//            this.terms = new ArrayList<>();
//        }
//
//        public Expression(Term term, double constant) {
//            this.terms = new ArrayList<>();
//            terms.add(term);
//            this.constant = constant;
//        }
//
//        public Expression(Term term) {
//            this(term, 0.0);
//        }
//
//        public Expression(List<Term> terms, double constant) {
//            this.terms = terms;
//            this.constant = constant;
//        }
//
//        public Expression(List<Term> terms) {
//            this(terms, 0);
//        }
//
//        public double getConstant() {
//            return constant;
//        }
//
//        public void setConstant(double constant) {
//            this.constant = constant;
//        }
//
//        public List<Term> getTerms() {
//            return terms;
//        }
//
//        public void setTerms(List<Term> terms) {
//            this.terms = terms;
//        }
//
//        public double getValue() {
//            return terms.parallelStream().mapToDouble(Term::getValue).sum() + constant;
//        }
//
//        public final boolean isConstant() {
//            return terms.size() == 0;
//        }
//
//        @Override
//        public String toString() {
//            return "constant: " + constant + terms.stream().map(Term::toString).map(s -> "(" + s + ")").reduce("terms: ", (s1, s2) -> s1 + s2);
//        }
//
//    }
//
//
//    public static class Constraint {
//
//        private Expression expression;
//        private double strength;
//        private NSLayoutRelation op;
//
//        public Constraint() {
//        }
//
//        public Constraint(Expression expr, NSLayoutRelation op) {
//            this(expr, op, Strength.REQUIRED);
//        }
//
//        public Constraint(Expression expr, NSLayoutRelation op, double strength) {
//            this.expression = reduce(expr);
//            this.op = op;
//            this.strength = Strength.clip(strength);
//        }
//
//        public Constraint(Constraint other, double strength) {
//            this(other.expression, other.op, strength);
//        }
//
//        private static Expression reduce(Expression expr) {
//
//            return new Expression(expr
//                    .getTerms()
//                    .stream()
//                    .collect(LinkedHashMap<Variable, Double>::new, (m, t) -> m.put(t.getVariable(), m.getOrDefault(t.getVariable(), 0.0) + t.getCoefficient()), LinkedHashMap::putAll).entrySet().stream().map(entry -> new Term(entry.getKey(), entry.getValue())).toList(), expr.getConstant());
//        }
//
//        public Expression getExpression() {
//            return expression;
//        }
//
//        public void setExpression(Expression expression) {
//            this.expression = expression;
//        }
//
//        public double getStrength() {
//            return strength;
//        }
//
//        public Constraint setStrength(double strength) {
//            this.strength = strength;
//            return this;
//        }
//
//        public NSLayoutRelation getOp() {
//            return op;
//        }
//
//        public void setOp(NSLayoutRelation op) {
//            this.op = op;
//        }
//
//        @Override
//        public String toString() {
//            return "expression: (" + expression + ") strength: " + strength + " operator: " + op;
//        }
//
//    }
//
//
//    public static class Row {
//
//        private double constant;
//
//        private Map<Symbol, Double> cells = new LinkedHashMap<>();
//
//        public Row() {
//            this(0);
//        }
//
//        public Row(double constant) {
//            this.constant = constant;
//        }
//
//        public Row(Row other) {
//            this.cells = new LinkedHashMap<>(other.cells);
//            this.constant = other.constant;
//        }
//
//        public double getConstant() {
//            return constant;
//        }
//
//        public void setConstant(double constant) {
//            this.constant = constant;
//        }
//
//        public Map<Symbol, Double> getCells() {
//            return cells;
//        }
//
//        public void setCells(Map<Symbol, Double> cells) {
//            this.cells = cells;
//        }
//
//        double add(double value) {
//            return this.constant += value;
//        }
//
//        void insert(Symbol symbol, double coefficient) {
//            double addedCoefficient = coefficient + cells.getOrDefault(symbol, 0.0);
//            if (Utils.nearZero(addedCoefficient)) {
//                cells.remove(symbol);
//            } else {
//                cells.put(symbol, addedCoefficient);
//            }
//        }
//
//        void insert(Symbol symbol) {
//            insert(symbol, 1.0);
//        }
//
//        void insert(Row other, double coefficient) {
//
//            this.constant += other.constant * coefficient;
//            for (Symbol s : other.cells.keySet()) {
//                double coeff = other.cells.get(s) * coefficient;
//                this.cells.putIfAbsent(s, 0.0);
//                double temp = this.cells.get(s) + coeff;
//                this.cells.put(s, temp);
//                if (Utils.nearZero(temp)) {
//                    this.cells.remove(s);
//                }
//            }
//        }
//
//        void insert(Row other) {
//            insert(other, 1.0);
//        }
//
//        void remove(Symbol symbol) {
//            cells.remove(symbol);
//        }
//
//        void invertSign() {
//            constant = -constant;
//            cells.replaceAll((k, v) -> -v);
//        }
//
//        void solve(Symbol symbol) {
//            double coefficient = -1.0 / cells.get(symbol);
//            cells.remove(symbol);
//            this.constant *= coefficient;
//            cells.replaceAll((k, v) -> v * coefficient);
//        }
//
//        void solve(Symbol lhs, Symbol rhs) {
//            insert(lhs, -1.0);
//            solve(rhs);
//        }
//
//        double getCoefficient(Symbol symbol) {
//            return cells.getOrDefault(symbol, 0.0);
//        }
//
//        void substitute(Symbol symbol, Row row) {
//            if (cells.containsKey(symbol)) {
//                insert(row, cells.remove(symbol));
//            }
//        }
//
//    }
//
//    public static class Variable {
//
//        private String name;
//
//        private double value;
//
//        public Variable(String name) {
//            this.name = name;
//        }
//
//        public Variable(double value) {
//        }
//
//        public double getValue() {
//            return value;
//        }
//
//        public void setValue(double value) {
//            this.value = value;
//        }
//
//        public String getName() {
//            return name;
//        }
//
//        @Override
//        public String toString() {
//            return "name: " + name + " value: " + value;
//        }
//    }
//
//    public static class Symbol {
//
//        enum Type {
//            INVALID,
//            EXTERNAL,
//            SLACK,
//            ERROR,
//            DUMMY
//        }
//
//        private Type type;
//
//        public Symbol() {
//            this(Type.INVALID);
//        }
//
//        public Symbol(Type type) {
//            this.type = type;
//        }
//
//        public Type getType() {
//            return type;
//        }
//
//    }
//
//    public static class Strength {
//
//        public static final double REQUIRED = create(1000.0, 1000.0, 1000.0);
//
//        public static final double STRONG = create(1.0, 0.0, 0.0);
//
//        public static final double MEDIUM = create(0.0, 1.0, 0.0);
//
//        public static final double WEAK = create(0.0, 0.0, 1.0);
//
//
//        public static double create(double a, double b, double c, double w) {
//            double result = 0.0;
//            result += Math.max(0.0, Math.min(1000.0, a * w)) * 1000000.0;
//            result += Math.max(0.0, Math.min(1000.0, b * w)) * 1000.0;
//            result += Math.max(0.0, Math.min(1000.0, c * w));
//            return result;
//        }
//
//        public static double create(double a, double b, double c) {
//            return create(a, b, c, 1.0);
//        }
//
//        public static double clip(double value) {
//            return Math.max(0.0, Math.min(REQUIRED, value));
//        }
//    }
//
//    public static class Tag {
//        Symbol marker;
//        Symbol other;
//
//        public Tag() {
//            marker = new Symbol();
//            other = new Symbol();
//        }
//    }
//
//    public static class EditInfo {
//        Tag tag;
//        Constraint constraint;
//        double constant;
//
//        public EditInfo(Constraint constraint, Tag tag, double constant) {
//            this.constraint = constraint;
//            this.tag = tag;
//            this.constant = constant;
//        }
//    }
//
//    public static class Term {
//
//        private Variable variable;
//        double coefficient;
//
//        public Term(Variable variable, double coefficient) {
//            this.variable = variable;
//            this.coefficient = coefficient;
//        }
//
//        public Term(Variable variable) {
//            this(variable, 1.0);
//        }
//
//        public Variable getVariable() {
//            return variable;
//        }
//
//        public void setVariable(Variable variable) {
//            this.variable = variable;
//        }
//
//        public double getCoefficient() {
//            return coefficient;
//        }
//
//        public void setCoefficient(double coefficient) {
//            this.coefficient = coefficient;
//        }
//
//        public double getValue() {
//            return coefficient * variable.getValue();
//        }
//
//        @Override
//        public String toString() {
//            return "variable: (" + variable + ") coefficient: " + coefficient;
//        }
//    }
//
//    public interface VariableResolver {
//        Variable resolveVariable(String variableName);
//
//        Expression resolveConstant(String name);
//    }
//
//    public class Solver {
//        public static final String LEFT = "left";
//        public static final String RIGHT = "right";
//        public static final String TOP = "top";
//        public static final String BOTTOM = "bottom";
//        public static final String HEIGHT = "height";
//        public static final String WIDTH = "width";
//        public static final String CENTERX = "centerX";
//        public static final String CENTERY = "centerY";
//
//
//        private final Map<Constraint, Tag> cns = new LinkedHashMap<>();
//        private final Map<Symbol, Row> rows = new LinkedHashMap<>();
//        private final Map<Variable, Symbol> vars = new LinkedHashMap<>();
//        private final Map<Variable, EditInfo> edits = new LinkedHashMap<>();
//        private final List<Symbol> infeasibleRows = new ArrayList<>();
//        private final Row objective = new Row();
//        private Row artificial;
//
//
//        public void addConstraint(Constraint constraint) throws ConstraintException {
//
//            if (cns.containsKey(constraint)) {
//                throw new ConstraintException(constraint);
//            }
//
//            Tag tag = new Tag();
//            Row row = createRow(constraint, tag);
//            Symbol subject = chooseSubject(row, tag);
//
//            if (subject.getType() == Symbol.Type.INVALID && allDummies(row)) {
//                if (!Utils.nearZero(row.getConstant())) {
//                    throw new ConstraintException(constraint);
//                } else {
//                    subject = tag.marker;
//                }
//            }
//
//            if (subject.getType() == Symbol.Type.INVALID) {
//                if (!addWithArtificialVariable(row)) {
//                    throw new ConstraintException(constraint);
//                }
//            } else {
//                row.solve(subject);
//                substitute(subject, row);
//                this.rows.put(subject, row);
//            }
//
//            this.cns.put(constraint, tag);
//
//            optimize(objective);
//        }
//
//        public void removeConstraint(Constraint constraint) throws ConstraintException, Error {
//            Tag tag = cns.get(constraint);
//            if (tag == null) {
//                throw new ConstraintException(constraint);
//            }
//
//            cns.remove(constraint);
//            removeConstraintEffects(constraint, tag);
//
//            Row row = rows.get(tag.marker);
//            if (row != null) {
//                rows.remove(tag.marker);
//            } else {
//                row = getMarkerLeavingRow(tag.marker);
//                if (row == null) {
//                    throw new Error("internal solver error");
//                }
//
//                Symbol leaving = null;
//                for (Symbol s : rows.keySet()) {
//                    if (rows.get(s) == row) {
//                        leaving = s;
//                    }
//                }
//                if (leaving == null) {
//                    throw new Error("internal solver error");
//                }
//
//                rows.remove(leaving);
//                row.solve(leaving, tag.marker);
//                substitute(tag.marker, row);
//            }
//            optimize(objective);
//        }
//
//        void removeConstraintEffects(Constraint constraint, Tag tag) {
//            if (tag.marker.getType() == Symbol.Type.ERROR) {
//                removeMarkerEffects(tag.marker, constraint.getStrength());
//            } else if (tag.other.getType() == Symbol.Type.ERROR) {
//                removeMarkerEffects(tag.other, constraint.getStrength());
//            }
//        }
//
//        void removeMarkerEffects(Symbol marker, double strength) {
//            Row row = rows.get(marker);
//            if (row != null) {
//                objective.insert(row, -strength);
//            } else {
//                objective.insert(marker, -strength);
//            }
//        }
//
//        Row getMarkerLeavingRow(Symbol marker) {
//            double dmax = Double.MAX_VALUE;
//            double r1 = dmax;
//            double r2 = dmax;
//
//            Row first = null;
//            Row second = null;
//            Row third = null;
//
//            for (Symbol s : rows.keySet()) {
//                Row candidateRow = rows.get(s);
//                double c = candidateRow.getCoefficient(marker);
//                if (c == 0.0) {
//                    continue;
//                }
//                if (s.getType() == Symbol.Type.EXTERNAL) {
//                    third = candidateRow;
//                } else if (c < 0.0) {
//                    double r = -candidateRow.getConstant() / c;
//                    if (r < r1) {
//                        r1 = r;
//                        first = candidateRow;
//                    }
//                } else {
//                    double r = candidateRow.getConstant() / c;
//                    if (r < r2) {
//                        r2 = r;
//                        second = candidateRow;
//                    }
//                }
//            }
//
//            if (first != null) {
//                return first;
//            }
//            if (second != null) {
//                return second;
//            }
//            return third;
//        }
//
//        public boolean hasConstraint(Constraint constraint) {
//            return cns.containsKey(constraint);
//        }
//
//        public void addEditVariable(Variable variable, double strength) throws Exception {
//            if (edits.containsKey(variable)) {
//                throw new Exception("Duplicate edit variable");
//            }
//
//            strength = Strength.clip(strength);
//
//            if (strength == Strength.REQUIRED) {
//                throw new Exception("An edit variable cannot be required");
//            }
//
//            List<Term> terms = new ArrayList<>();
//            terms.add(new Term(variable));
//            Constraint constraint = new Constraint(new Expression(terms), NSLayoutRelation.EQUAL, strength);
//
//            try {
//                addConstraint(constraint);
//            } catch (ConstraintException e) {
//                e.printStackTrace();
//            }
//
//
//            EditInfo info = new EditInfo(constraint, cns.get(constraint), 0.0);
//            edits.put(variable, info);
//        }
//
//        public void removeEditVariable(Variable variable) throws Exception {
//            EditInfo edit = edits.get(variable);
//            if (edit == null) {
//                throw new Exception("Unknown edit variable");
//            }
//
//            try {
//                removeConstraint(edit.constraint);
//            } catch (ConstraintException e) {
//                e.printStackTrace();
//            }
//
//            edits.remove(variable);
//        }
//
//        public boolean hasEditVariable(Variable variable) {
//            return edits.containsKey(variable);
//        }
//
//        public void suggestValue(Variable variable, double value) throws Exception {
//            EditInfo info = edits.get(variable);
//            if (info == null) {
//                throw new Exception("Unknown edit variable");
//            }
//
//            double delta = value - info.constant;
//            info.constant = value;
//
//            Row row = rows.get(info.tag.marker);
//            if (row != null) {
//                if (row.add(-delta) < 0.0) {
//                    infeasibleRows.add(info.tag.marker);
//                }
//                dualOptimize();
//                return;
//            }
//
//            row = rows.get(info.tag.other);
//            if (row != null) {
//                if (row.add(delta) < 0.0) {
//                    infeasibleRows.add(info.tag.other);
//                }
//                dualOptimize();
//                return;
//            }
//
//            rows.forEach((k, v) -> {
//                double coeff = v.getCoefficient(info.tag.marker);
//                if (coeff != 0.0 && v.add(delta * coeff) < 0.0 && k.getType() != Symbol.Type.EXTERNAL)
//                    infeasibleRows.add(k);
//            });
//
//            dualOptimize();
//        }
//
//        public void updateVariables() {
//            vars.forEach((k, v) -> {
//                Row row = this.rows.get(v);
//                k.setValue(row != null ? row.getConstant() : 0);
//            });
//        }
//
//        Row createRow(Constraint constraint, Tag tag) {
//            Expression expression = constraint.getExpression();
//            Row row = new Row(expression.getConstant());
//
//            expression.getTerms().forEach(term -> {
//                if (Utils.nearZero(term.getCoefficient())) {
//                    return;
//                }
//                Symbol sym = getVarSymbol(term.getVariable());
//
//                Row symRow = rows.get(sym);
//                if (symRow == null) {
//                    row.insert(sym, term.getCoefficient());
//                } else {
//                    row.insert(symRow, term.getCoefficient());
//                }
//            });
//
//            switch (constraint.getOp()) {
//                case LESS_THAN_OR_EQUAL:
//                case GREATER_THAN_OR_EQUAL: {
//                    double coeff = constraint.getOp() == NSLayoutRelation.LESS_THAN_OR_EQUAL ? 1.0 : -1.0;
//                    Symbol slack = new Symbol(Symbol.Type.SLACK);
//                    tag.marker = slack;
//                    row.insert(slack, coeff);
//                    if (constraint.getStrength() < Strength.REQUIRED) {
//                        Symbol error = new Symbol(Symbol.Type.ERROR);
//                        tag.other = error;
//                        row.insert(error, -coeff);
//                        this.objective.insert(error, constraint.getStrength());
//                    }
//                    break;
//                }
//                case EQUAL: {
//                    if (constraint.getStrength() < Strength.REQUIRED) {
//                        Symbol errplus = new Symbol(Symbol.Type.ERROR);
//                        Symbol errminus = new Symbol(Symbol.Type.ERROR);
//                        tag.marker = errplus;
//                        tag.other = errminus;
//                        row.insert(errplus, -1.0); // v = eplus - eminus
//                        row.insert(errminus, 1.0); // v - eplus + eminus = 0
//                        this.objective.insert(errplus, constraint.getStrength());
//                        this.objective.insert(errminus, constraint.getStrength());
//                    } else {
//                        Symbol dummy = new Symbol(Symbol.Type.DUMMY);
//                        tag.marker = dummy;
//                        row.insert(dummy);
//                    }
//                    break;
//                }
//            }
//            if (row.getConstant() < 0.0) {
//                row.invertSign();
//            }
//            return row;
//        }
//
//        private static Symbol chooseSubject(Row row, Tag tag) {
//            return row.getCells().keySet().stream().filter(k -> k.getType() == Symbol.Type.EXTERNAL).findFirst().orElse(((Supplier<Symbol>) () -> {
//                if (tag.marker.getType() == Symbol.Type.SLACK || tag.marker.getType() == Symbol.Type.ERROR) {
//                    if (row.getCoefficient(tag.marker) < 0.0) return tag.marker;
//                }
//                if (tag.other != null && (tag.other.getType() == Symbol.Type.SLACK || tag.other.getType() == Symbol.Type.ERROR)) {
//                    if (row.getCoefficient(tag.other) < 0.0) return tag.other;
//                }
//                return new Symbol();
//            }).get());
//        }
//
//        private boolean addWithArtificialVariable(Row row) {
//
//            Symbol art = new Symbol(Symbol.Type.SLACK);
//            rows.put(art, new Row(row));
//            this.artificial = new Row(row);
//            optimize(this.artificial);
//
//            boolean success = Utils.nearZero(artificial.getConstant());
//            artificial = null;
//
//            Row rowptr = this.rows.get(art);
//            if (rowptr != null) {
//                LinkedList<Symbol> toRemove = rows.entrySet().stream().filter(e -> e.getValue() == rowptr).map(Map.Entry::getKey).collect(LinkedList::new, LinkedList::add, LinkedList::addAll);
//                toRemove.forEach(rows::remove);
//                toRemove.clear();
//
//                if (rowptr.getCells().isEmpty()) {
//                    return success;
//                }
//
//                Symbol entering = anyPivotableSymbol(rowptr);
//                if (entering.getType() == Symbol.Type.INVALID) {
//                    return false;
//                }
//                rowptr.solve(art, entering);
//                substitute(entering, rowptr);
//                this.rows.put(entering, rowptr);
//            }
//
//            // Remove the artificial variable from the tableau.
//            for (Map.Entry<Symbol, Row> rowEntry : rows.entrySet()) {
//                rowEntry.getValue().remove(art);
//            }
//
//            objective.remove(art);
//
//            return success;
//        }
//
//        void substitute(Symbol symbol, Row row) {
//            rows.forEach((k, v) -> {
//                v.substitute(symbol, row);
//                if (k.getType() != Symbol.Type.EXTERNAL && v.getConstant() < 0.0) infeasibleRows.add(k);
//            });
//
//            objective.substitute(symbol, row);
//            if (artificial != null) {
//                artificial.substitute(symbol, row);
//            }
//        }
//
//        void optimize(Row objective) {
//            while (true) {
//                Symbol entering = getEnteringSymbol(objective);
//                if (entering.getType() == Symbol.Type.INVALID) {
//                    return;
//                }
//
//                Row entry = getLeavingRow(entering);
//                if (entry == null) {
//                    throw new Error("The objective is unbounded.");
//                }
//                Symbol leaving = rows.entrySet().stream().filter(e -> e.getValue() == entry).map(Map.Entry::getKey).findFirst().orElse(null);
//
//                rows.remove(leaving);
//                entry.solve(leaving, entering);
//                substitute(entering, entry);
//                rows.put(entering, entry);
//            }
//        }
//
//        void dualOptimize() throws Error {
//            while (!infeasibleRows.isEmpty()) {
//                Symbol leaving = infeasibleRows.remove(infeasibleRows.size() - 1);
//                Row row = rows.get(leaving);
//                if (row != null && row.getConstant() < 0.0) {
//                    Symbol entering = getDualEnteringSymbol(row);
//                    if (entering.getType() == Symbol.Type.INVALID) {
//                        throw new Error("internal solver error");
//                    }
//                    rows.remove(leaving);
//                    row.solve(leaving, entering);
//                    substitute(entering, row);
//                    rows.put(entering, row);
//                }
//            }
//        }
//
//        private static Symbol getEnteringSymbol(Row objective) {
//            return objective.getCells().entrySet().stream().filter(e -> e.getKey().getType() != Symbol.Type.DUMMY && e.getValue() < 0.0).findFirst().orElse(Map.entry(new Symbol(), 0.0)).getKey();
//        }
//
//        private Symbol getDualEnteringSymbol(Row row) {
//            Symbol entering = new Symbol();
//            double ratio = Double.MAX_VALUE;
//            for (Symbol s : row.getCells().keySet()) {
//                if (s.getType() != Symbol.Type.DUMMY) {
//                    double currentCell = row.getCells().get(s);
//                    if (currentCell > 0.0) {
//                        double coefficient = objective.getCoefficient(s);
//                        double r = coefficient / currentCell;
//                        if (r < ratio) {
//                            ratio = r;
//                            entering = s;
//                        }
//                    }
//                }
//            }
//            return entering;
//        }
//
//        private Symbol anyPivotableSymbol(Row row) {
//            return row.getCells().keySet().stream().filter(s -> s.getType() == Symbol.Type.SLACK || s.getType() == Symbol.Type.ERROR).findFirst().orElse(new Symbol());
//        }
//
//        private Row getLeavingRow(Symbol entering) {
//            double minRatio = Double.MAX_VALUE;
//            Row minRow = null;
//            for (Map.Entry<Symbol, Row> entry : rows.entrySet()) {
//                if (entry.getKey().getType() == Symbol.Type.EXTERNAL) {
//                    continue;
//                }
//                double coeff = entry.getValue().getCoefficient(entering);
//                if (coeff >= 0.0) continue;
//                double ratio = (-entry.getValue().getConstant() / coeff);
//                if (ratio < minRatio) {
//                    minRatio = ratio;
//                    minRow = entry.getValue();
//                }
//            }
//            return minRow;
//        }
//
//        private Symbol getVarSymbol(Variable variable) {
//            return vars.computeIfAbsent(variable, it -> new Symbol(Symbol.Type.EXTERNAL));
//        }
//
//        private static boolean allDummies(Row row) {
//            return row.getCells().keySet().parallelStream().allMatch(s -> s.getType() == Symbol.Type.DUMMY);
//        }
//
//        public void addStayConstraint(Variable variable) throws ConstraintException {
//            addStayConstraint(variable, Strength.WEAK, 1.0);
//        }
//
//        public void addStayConstraint(Variable variable, double strength, double weight) throws ConstraintException {
//            addConstraint(new Constraint(new Expression(new Term(variable), weight), NSLayoutRelation.EQUAL, strength));
//        }
//
//        public HashMap<String, HashMap<String, Variable>> solve(List<String> constraints, float height, float width) throws Exception {
//            HashMap<String, HashMap<String, Variable>> result = new HashMap<>();
//            var variableSolver = new VariableResolver() {
//                private Variable getVariableFromNode(HashMap<String, Variable> node, String propertyName) {
//
//                    try {
//                        if (node.containsKey(propertyName)) {
//                            return node.get(propertyName);
//                        } else {
//                            Variable variable = new Variable(propertyName);
//                            node.put(propertyName, variable);
//                            if (RIGHT.equals(propertyName)) {
//                                addConstraint(Operations.equals(variable, Operations.add(getVariableFromNode(node, LEFT), getVariableFromNode(node, WIDTH))));
//                            } else if (BOTTOM.equals(propertyName)) {
//                                addConstraint(Operations.equals(variable, Operations.add(getVariableFromNode(node, TOP), getVariableFromNode(node, HEIGHT))));
//                            }
//                            return variable;
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//
//                    return null;
//
//                }
//
//                private HashMap<String, Variable> getNode(String nodeName) {
//                    HashMap<String, Variable> node;
//                    if (result.containsKey(nodeName)) {
//                        node = result.get(nodeName);
//                    } else {
//                        node = new HashMap<>();
//                        result.put(nodeName, node);
//                    }
//                    return node;
//                }
//
//                @Override
//                public Variable resolveVariable(String variableName) {
//
//                    String[] stringArray = variableName.split("\\.");
//                    if (stringArray.length == 2) {
//                        String nodeName = stringArray[0];
//                        String propertyName = stringArray[1];
//
//                        HashMap<String, Variable> node = getNode(nodeName);
//
//                        return getVariableFromNode(node, propertyName);
//
//                    } else {
//                        throw new RuntimeException("can't resolve variable");
//                    }
//                }
//
//                @Override
//                public Expression resolveConstant(String name) {
//                    try {
//                        return new Expression(Double.parseDouble(name));
//                    } catch (NumberFormatException e) {
//                        return null;
//                    }
//                }
//            };

//            var defaultConstraints = Arrays.asList(
//                    "container.height == " + height,
//                    "container.width == " + width,
//                    "container.top == 0",
//                    "container.bottom == " + height,
//                    "container.left == 0",
//                    "container.right == " + width
//            );
//
//            for (String constraint : defaultConstraints) {
//                var con = ConstraintParser.parseConstraint(constraint, variableSolver);
//                addConstraint(con);
//            }
//
//            for (String constraint : constraints) {
//                var con = ConstraintParser.parseConstraint(constraint, variableSolver);
//                addConstraint(con);
//            }
//
//            updateVariables();
//            return result;
//        }
//    }
//
//    public static class ConstraintException extends Exception {
//        public ConstraintException(Constraint constraint) {
//            super(constraint.toString());
//        }
//    }
//
//    public static class Utils {
//
//        public static boolean nearZero(double value) {
//            double EPS = 1.0e-8;
//            return value < 0.0 ? -value < EPS : value < EPS;
//        }
//    }
//
//    public static class Operations {
//
//        private Operations() {
//        }
//
//        public static <T, U, V> T multiply(U u, V v) throws Exception {
//
//            if (u instanceof Double u1) {
//                if (v instanceof Double v1)
//                    return (T) Double.valueOf(u1 * v1);
//
//                return multiply(v, u1);
//            }
//
//            if (v instanceof Expression e2) {
//                Expression e1 = (Expression) u;
//                if (e1.isConstant())
//                    return multiply(e1.getConstant(), e2);
//                if (e2.isConstant())
//                    return multiply(e1, e2.getConstant());
//                throw new Exception("Nonlinear expression");
//            }
//
//            Double coefficient = (Double) v;
//
//            if (u instanceof Variable variable)
//                return (T) new Term(variable, coefficient);
//
//            if (u instanceof Term term)
//                return (T) new Term(term.getVariable(), term.getCoefficient() * coefficient);
//
//            Expression expression = (Expression) u;
//            List<Term> terms = expression
//                    .getTerms()
//                    .stream()
//                    .map(t -> new Term(t.getVariable(), t.getCoefficient() * coefficient))
//                    .toList();
//
//            return (T) new Expression(terms, expression.getConstant() * coefficient);
//        }
//
//        public static <T, U, V> T divide(U u, V v) throws Exception {
//            if (v instanceof Expression e2) {
//                if (e2.isConstant())
//                    return divide(u, e2.getConstant());
//                throw new Exception("Nonlinear expression");
//            }
//            return multiply(u, 1 / (Double) v);
//        }
//
//        public static <T, U> T negate(U u) throws Exception {
//            return multiply(u, -1.0);
//        }
//
//        public static <U, V> Expression add(U u, V v) throws Exception {
//            if (u instanceof Double)
//                return add(v, u);
//
//            if (u instanceof Expression e1) {
//                if (v instanceof Double constant)
//                    return new Expression(e1.getTerms(), e1.getConstant() + constant);
//                if (v instanceof Variable variable)
//                    return add(e1, new Term(variable));
//                Expression e2;
//                if (v instanceof Term term)
//                    e2 = new Expression(term);
//                else
//                    e2 = (Expression) v;
//                List<Term> terms = new ArrayList<>(e1.getTerms().size() + e2.getTerms().size());
//                terms.addAll(e1.getTerms());
//                terms.addAll(e2.getTerms());
//                return new Expression(terms, e1.getConstant() + e2.getConstant());
//            }
//
//            if (u instanceof Term t) {
//                if (v instanceof Expression e)
//                    return add(e, t);
//                if (v instanceof Term t2)
//                    return add(new Expression(t), t2);
//                if (v instanceof Variable variable)
//                    return add(t, new Term(variable));
//                return new Expression(t, (Double) v);
//            }
//
//            Variable variable = (Variable) u;
//            if (v instanceof Expression e)
//                return add(e, variable);
//            if (v instanceof Term t)
//                return add(t, variable);
//            if (v instanceof Variable v2)
//                return add(new Term(variable), v2);
//            return new Expression(new Term(variable), (Double) v);
//        }
//
//        public static <U, V> Expression subtract(U u, V v) throws Exception {
//            return add(u, negate(v));
//        }
//
//        public static <U, V> Constraint equals(U u, V v) throws Exception {
//            if (u instanceof Double)
//                return equals(v, u);
//
//            if (u instanceof Variable variable) {
//                if (v instanceof Expression || v instanceof Term)
//                    return equals(v, variable);
//                return equals(new Term(variable), v);
//            }
//
//            if (u instanceof Term term) {
//                if (v instanceof Expression)
//                    return equals(v, term);
//                return equals(new Expression(term), v);
//            }
//
//            Expression e1 = (Expression) u;
//            if (v instanceof Expression e2)
//                return new Constraint(subtract(e1, e2), NSLayoutRelation.EQUAL);
//            if (v instanceof Variable variable)
//                return equals(e1, new Term(variable));
//            if (v instanceof Term t)
//                return equals(e1, new Expression(t));
//            Double constant = (Double) v;
//            return equals(e1, new Expression(constant));
//        }
//
//        public static <U, V> Constraint lessThanOrEqualTo(U u, V v) throws Exception {
//            if (u instanceof Double)
//                return lessThanOrEqualTo(v, u);
//
//            if (u instanceof Variable variable)
//                return lessThanOrEqualTo(new Term(variable), v);
//
//            if (u instanceof Term term)
//                return lessThanOrEqualTo(new Expression(term), v);
//
//            Expression e1 = (Expression) u;
//            if (v instanceof Expression e2)
//                return new Constraint(subtract(e1, e2), NSLayoutRelation.LESS_THAN_OR_EQUAL);
//            if (v instanceof Variable variable)
//                return lessThanOrEqualTo(e1, new Term(variable));
//            if (v instanceof Term t)
//                return lessThanOrEqualTo(e1, new Expression(t));
//            Double constant = (Double) v;
//            return lessThanOrEqualTo(e1, new Expression(constant));
//        }
//
//        public static <U, V> Constraint greaterThanOrEqualTo(U u, V v) throws Exception {
//            if (u instanceof Double)
//                return greaterThanOrEqualTo(v, u);
//
//            if (u instanceof Variable variable)
//                return greaterThanOrEqualTo(new Term(variable), v);
//
//            if (u instanceof Term term)
//                return greaterThanOrEqualTo(new Expression(term), v);
//
//            Expression e1 = (Expression) u;
//            if (v instanceof Expression e2) {
//                return new Constraint(subtract(e1, e2), NSLayoutRelation.GREATER_THAN_OR_EQUAL);
//            }
//            if (v instanceof Variable variable) {
//                return greaterThanOrEqualTo(e1, new Term(variable));
//            }
//            if (v instanceof Term t) {
//                return greaterThanOrEqualTo(e1, new Expression(t));
//            }
//            Double constant = (Double) v;
//            return greaterThanOrEqualTo(e1, new Expression(constant));
//        }
//
//        // Constraint strength modifier
//        public static Constraint modifyStrength(Constraint constraint, double strength) {
//            return new Constraint(constraint, strength);
//        }
//
//        public static Constraint modifyStrength(double strength, Constraint constraint) {
//            return modifyStrength(constraint, strength);
//        }
//    }
}
