package cn.edu.yali.compiler.parser.table;

/**
 * Grammar symbols
 * <br>
 * This class is the base class of all grammar symbols (terminal and non-terminal)
 */
public abstract class Term {
    /**
     * Get the name of the grammar symbol (that is, the descriptor that appears in the grammar file)
     *
     * @return name
     */
    public String getTermName() {
        return termName;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Term term
            && term.termName.equals(termName);
    }

    @Override
    public int hashCode() {
        return termName.hashCode();
    }

    @Override
    public String toString() {
        return termName;
    }

    protected Term(String termName) {
        this.termName = termName;
    }

    private final String termName;
}
