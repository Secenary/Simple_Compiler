package cn.edu.yali.compiler.parser.table;

import cn.edu.yali.compiler.lexer.Token;
import cn.edu.yali.compiler.lexer.TokenKind;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a state in the LR analysis table
 * <br>
 * The equivalence of states is determined uniquely by their numbers. That is, two states are equal if and only if their indexes are the same
 *
 * @param index The index/number of the state in the LR table
 * @param action Which state should be transferred to after encountering a terminal symbol in this state
 * @param goto_ Which state should be transferred to after reducing to a non-terminal symbol in this state
 */
public record Status(int index, Map<TokenKind, Action> action, Map<NonTerminal, Status> goto_) {
    /**
     * Construct a state
     *
     * @param index state index/number
     * @return constructed state
     */
    public static Status create(int index) {
        if (index < 0) {
            throw new RuntimeException("Index of status can NOT smaller than zero");
        }

        return new Status(index);
    }

    /**
     * @return Gets the status representing the error
     */
    public static Status error() {
        return errorInstance;
    }

    public boolean isError() {
        return this == errorInstance;
    }

    @Override
    public String toString() {
        return Integer.toString(index);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Status status
            && status.index == index;
    }

    @Override
    public int hashCode() {
        return index;
    }

    /**
     * When encountering this terminal (Token type), which state should be transferred to
     *
     * @param terminal terminal (Token type)
     * @return The state to be transferred to
     */
    public Action getAction(TokenKind terminal) {
        return action.getOrDefault(terminal, Action.error());
    }

    /**
     * When encountering this lexical unit, which state should be transferred to
     *
     * @param token lexical unit
     * @return the state to be transferred to
     */
    public Action getAction(Token token) {
        return getAction(token.getKind());
    }

    /**
     * When the specification reaches this non-terminal symbol, which state should be transferred to
     *
     * @param nonTerminal non-terminal symbol
     * @return the state to be transferred to
     */
    public Status getGoto(NonTerminal nonTerminal) {
        return goto_.getOrDefault(nonTerminal, Status.error());
    }

    //==================== The following is the implementation code ==============================//

    void setAction(TokenKind terminal, Action action) {
        // It is possible to set the same action, so no error will be reported.
        if (inAndNotEqual(this.action, terminal, action)) {
            throw new RuntimeException("Action conflict at %s on %d".formatted(terminal, index));
        }

        this.action.put(terminal, action);
    }

    void setGoto(NonTerminal nonTerminal, Status goto_) {
        // It is possible to set the same goto, so no error will be reported.
        if (inAndNotEqual(this.goto_, nonTerminal, goto_)) {
            throw new RuntimeException("Goto conflict at %s on %d".formatted(nonTerminal, index));
        }

        this.goto_.put(nonTerminal, goto_);
    }

    private static <K, V> boolean inAndNotEqual(Map<K, V> map, K key, V newValue) {
        return map.containsKey(key) && !newValue.equals(map.get(key));
    }

    private Status(int index) {
        this(index, new HashMap<>(), new HashMap<>());
    }

    private static final Status errorInstance = new Status(-1);
}
