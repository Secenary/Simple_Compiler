package cn.edu.yali.compiler.lexer;

import cn.edu.yali.compiler.parser.table.NonTerminal;
import cn.edu.yali.compiler.parser.table.Production;
import cn.edu.yali.compiler.parser.table.Term;
import cn.edu.yali.compiler.utils.FilePathConfig;
import cn.edu.yali.compiler.utils.FileUtils;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * <br>
 * This class represents the type of a token. Since the possible types of a token need to be read from the code point file (codingMap.txt) according to the experimental design,
 * In order to obtain the flexibility of reading files at runtime and to ensure that some typo about the type can be detected when constructing a token, we have taken certain checks.
 * <br>
 * At the same time, since the subsequent experimental process also needs to read the grammar file (grammar.txt) or the LR analysis table (LR1_table.csv), in order to identify the terminators and non-terminators in these files,
 * The TokenKind class is also used as a terminal symbol for classes representing grammars and productions. That's why it inherits Term (grammar item) as a parent class.
 * <br>
 * @see Term grammar item - the basic element that constitutes the production rule
 * @see NonTerminal non-terminal - an item that can be reduced by other items through the production rule
 * @see Production production - the basic element of BNF grammar description
 */
public class TokenKind extends Term {
    // A set of strings that are allowed to be used as ids for TokenKind
    private static final Map<String, TokenKind> allowed = new HashMap<>();
    private static final TokenKind eof = new TokenKind("$", -1);

    /**
     * Read the set of allowed identifiers from the code point file
     */
    public static void loadTokenKinds() {
        if (!allowed.isEmpty()) {
            throw new RuntimeException("Can not set allowed twice");
        }

        final var lines = FileUtils.readLines(FilePathConfig.CODING_MAP_PATH);
        for (final var line : lines) {
            // Each line of the code point file looks like:
            // 54 IntConst
            // Space separation, code point in front, identifier in the back
            final var words = line.split(" ");
            final var code = Integer.parseInt(words[0]);
            final var id = words[1];

            allowed.put(id, new TokenKind(id, code));
        }

        // EOF
        allowed.put("$", eof);
    }

    /**
     * @param id Identifier
     * @return Whether the identifier is allowed as a TokenKind identifier
     */
    public static boolean isAllowed(String id) {
        if (allowed == null) {
            throw new RuntimeException("Empty allowed");
        }

        return allowed.containsKey(id);
    }

    /**
     * @return A Map of identifiers to TokenKinds, with a key set containing all allowed identifiers
     */
    public static Map<String, TokenKind> allAllowedTokenKinds() {
        return Collections.unmodifiableMap(allowed);
    }

    /**
     * @param id Identifier
     * @return a TokenKind constructed from the given identifier
     * @throws RuntimeException The codepoint file has not been read, or the identifier is not allowed as an identifier for TokenKind
     */
    public static TokenKind fromString(String id) {
        if (allowed == null || !allowed.containsKey(id)) {
            throw new RuntimeException("Illegal Identifier");
        }

        return allowed.get(id);
    }

    /**
     * @return TokenKind representing EOF
     */
    public static TokenKind eof() {
        return eof;
    }

    /**
     * @return Get the identifier of this TokenKind
     */
    public String getIdentifier() {
        return getTermName();
    }

    /**
     * @return Get the code point of the TokenKind
     */
    public int getCode() {
        return code;
    }

    private TokenKind(String id, int code) {
        super(id);
        this.code = code;
    }

    private final int code;
}
