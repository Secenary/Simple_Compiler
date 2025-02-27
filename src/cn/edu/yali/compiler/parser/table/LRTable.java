package cn.edu.yali.compiler.parser.table;

import cn.edu.yali.compiler.lexer.Token;
import cn.edu.yali.compiler.lexer.TokenKind;
import cn.edu.yali.compiler.utils.FileUtils;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents LR grammar analysis table
 * <br>
 */
public class LRTable {
    /**
     * Get the corresponding action based on the current state and the current lexical unit
     *
     * @param status Current Status
     * @param token  Current token
     * @return Actions to be taken
     */
    public Action getAction(Status status, Token token) {
        final var tokenKind = token.getKind();
        return status.getAction(tokenKind);
    }

    /**
     * Get the state to be transferred to according to the current state and the convention to the non-terminal symbol
     *
     * @param status      Current Status
     * @param nonTerminal Non-terminal symbol
     * @return The state to be transferred to
     */
    public Status getGoto(Status status, NonTerminal nonTerminal) {
        return status.getGoto(nonTerminal);
    }

    /**
     * @return Starting State
     */
    public Status getInit() {
        return statusInIndexOrder.get(0);
    }

    public void dumpTable(String path) {
        final var text = new StringBuilder();
        // table head
        text.append("Status,ACTION").append(",".repeat(terminals.size()))
            // GOTO occupies the first nonTerminal position, so subtract 1
            .append("GOTO").append(",".repeat(nonTerminals.size() - 1))
            .append("\n");

        text.append(",")
            .append(terminals.stream().map(Term::toString).collect(Collectors.joining(",")))
            .append(",")
            .append(nonTerminals.stream().map(Term::toString).collect(Collectors.joining(",")))
            .append("\n");

        for (final var status : statusInIndexOrder) {
            text.append(status)
                .append(",")
                .append(terminals.stream().map(status::getAction).map(Action::toString).collect(Collectors.joining(",")))
                .append(",")
                .append(nonTerminals.stream().map(status::getGoto).map(this::convertToGotoString).collect(Collectors.joining(",")))
                .append("\n");
        }

        FileUtils.writeFile(path, text.toString());
    }

    private String convertToGotoString(Status status) {
        if (status.equals(Status.error())) {
            return "";
        } else {
            return status.toString();
        }
    }

    LRTable(List<Status> statusInIndexOrder, List<TokenKind> terminals, List<NonTerminal> nonTerminals) {
        this.statusInIndexOrder = statusInIndexOrder;
        this.terminals = terminals;
        this.nonTerminals = nonTerminals;
    }

    private final List<Status> statusInIndexOrder;
    private final List<TokenKind> terminals;
    private final List<NonTerminal> nonTerminals;
}
