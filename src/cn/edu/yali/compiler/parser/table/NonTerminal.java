package cn.edu.yali.compiler.parser.table;

/**
 * Represents a non-terminal symbol in a grammar symbol
 */
public class NonTerminal extends Term {
    public NonTerminal(String id) {
        super(id);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof NonTerminal && super.equals(obj);
    }
}
