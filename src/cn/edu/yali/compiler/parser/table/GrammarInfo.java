package cn.edu.yali.compiler.parser.table;

import cn.edu.yali.compiler.lexer.TokenKind;
import cn.edu.yali.compiler.utils.FilePathConfig;
import cn.edu.yali.compiler.utils.FileUtils;
import java.util.*;

/**
 * Read the grammar file (grammar.txt) to get the original string and non-terminal symbols of the production
*/
public class GrammarInfo {
    private final Map<String, NonTerminal> nonTerminals = new HashMap<>();
    private final Map<String, Production> productions = new HashMap<>();
    private final List<Production> productionsInOrder = new ArrayList<>();

    private NonTerminal getOrCreateNonTerminal(String name) {
        nonTerminals.computeIfAbsent(name, NonTerminal::new);
        return nonTerminals.get(name);
    }

    private GrammarInfo() {
        final var lines = FileUtils.readLines(FilePathConfig.GRAMMAR_PATH);
        for (int idx = 0; idx < lines.size(); idx++) {
            final var line = lines.get(idx);
            // Production of the form `A -> B ( id intConst ) C;`
            // First delete the semicolon, press -> to cut, then press the space to cut the body
            final var withoutComma = line.replace(";", "");
            final var words = withoutComma.split(" -> ");
            final var headString = words[0];
            final var bodyStrings = words[1].split(" ");

            final var head = getOrCreateNonTerminal(headString);

            final var body = new ArrayList<Term>();
            for (final var termName : bodyStrings) {
                if (TokenKind.isAllowed(termName)) {
                    body.add(TokenKind.fromString(termName));
                } else {
                    body.add(getOrCreateNonTerminal(termName));
                }
            }

            // idx + 1 is to make the production number the same as the row number for easy viewing
            final var production = new Production(idx + 1, head, body);
            productionsInOrder.add(production);
            productions.put(withoutComma, production);
        }
    }

    private static GrammarInfo instance = null;

    private static GrammarInfo getInstance() {
        if (instance == null) {
            instance = new GrammarInfo();
        }

        return instance;
    }

    public static Map<String, NonTerminal> getNonTerminals() {
        return Collections.unmodifiableMap(getInstance().nonTerminals);
    }

    public static Map<String, Production> getProductions() {
        return Collections.unmodifiableMap(getInstance().productions);
    }

    public static NonTerminal getNonTerminal(String name) {
        final var nonTerminals = getNonTerminals();
        if (!nonTerminals.containsKey(name)) {
            throw new RuntimeException("Unknown non-terminal: " + name);
        }

        return nonTerminals.get(name);
    }

    public static Production getProductionByText(String text) {
        final var productions = getProductions();
        if (!productions.containsKey(text)) {
            throw new RuntimeException("Unknown text of production: " + text);
        }

        return productions.get(text);
    }

    public static Production getBeginProduction() {
        return getInstance().productionsInOrder.get(0);
    }

    public static List<Production> getProductionsInOrder() {
        return Collections.unmodifiableList(getInstance().productionsInOrder);
    }
}
