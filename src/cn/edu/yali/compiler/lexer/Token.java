package cn.edu.yali.compiler.lexer;

/**
 * <br>
 * Lexical unit (Token) is the result of lexical analysis. Lexical analysis identifies the structure from the text stream of the source program file, and combines one or more characters that are combined to represent a specific meaning to form a lexical unit.
 * <br>
 * The structure of a lexical unit is very simple. It has a type and possible description text. The latter is in some complex lexical units such as identifiers.
 * The content of the lexical unit is represented in a numeric literal, which is also called a lexeme.
 * <br>
 * In order to facilitate and unify the construction of lexical units, we set the constructor of the lexical unit to private, and construct it through a public static function.
 * This will improve the readability of the code and facilitate us to perform certain checks during construction.
 * @see TokenKind The type of lexical unit, which has a certain complex structure
 */
public class Token {
    /**
     * @return Token representing EOF
     */
    public static Token eof() {
        return new Token(TokenKind.eof(), "");
    }

    /**
     * @param tokenKindId string representation of the token type
     * @return a simple token of this token type (without other text representation, such as punctuation/keywords)
     */
    public static Token simple(String tokenKindId) {
        return simple(TokenKind.fromString(tokenKindId));
    }

    /**
     * @param kind token type
     * @return a simple token of this token type (without other text representation, such as punctuation/keywords)
     */
    public static Token simple(TokenKind kind) {
        return normal(kind, "");
    }

    /**
     * @param tokenKindId string representation of the token type
     * @return a normal token with this token type (with other text representations, such as identifiers/numeric text)
     */
    public static Token normal(String tokenKindId, String text) {
        return normal(TokenKind.fromString(tokenKindId), text);
    }

    /**
     * @param kind token type
     * @param text source text
     * @return a normal token with this token type (with other text, such as identifier/numeric text)
     */
    public static Token normal(TokenKind kind, String text) {
        return new Token(kind, text);
    }

    /**
     * @return The text representation of the token type
     */
    public String getKindId() {
        return kind.getIdentifier();
    }

    /**
     * @return The type of the token
     */
    public TokenKind getKind() {
        return kind;
    }

    /**
     * @return The text of the token, which may be an empty string (but never null)
     */
    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        return "(%s,%s)".formatted(kind, text);
    }

    private Token(TokenKind kind, String text) {
        this.kind = kind;
        this.text = text;
    }

    private final TokenKind kind;
    private final String text;
}
