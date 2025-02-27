package cn.edu.yali.compiler.parser.table;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * represents a production rule
 * <br>
 * The equivalence of a production rule is uniquely determined by its index. That is, two production rules are equal if and only if their indexes are equal.
 *
 * @param index The index of the production rule, which is its line number in the grammar.txt file, starting from 1
 * @param head The head of the production rule
 * @param body The body of the production rule
 */
public record Production(int index, NonTerminal head, List<Term> body) {
    @Override
    public String toString() {
        final var bodyStr = body.stream().map(Objects::toString).collect(Collectors.joining(" "));
        return "%s -> %s".formatted(head, bodyStr);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Production production
            && production.index == index;
    }

    @Override
    public int hashCode() {
        return Objects.hash(index);
    }
}
