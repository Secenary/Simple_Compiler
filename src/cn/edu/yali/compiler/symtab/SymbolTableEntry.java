package cn.edu.yali.compiler.symtab;

/**
 * Symbol table entries
 */
public class SymbolTableEntry {
    /**
     * @param text The textual representation of the symbol. For identifier symbols, this parameter should be the identifier text.
     */
    public SymbolTableEntry(String text) {
        this.text = text;
        this.type = null;
    }

    /**
     * @return Textual representation of symbols
     */
    public String getText() {
        return text;
    }

    /**
     * @return The type of source language object to which this identifier symbol can be bound
     */
    public SourceCodeType getType() {
        return type;
    }

    /**
     * Since this type can only be obtained after syntax analysis, in order to construct the symbol table during lexical analysis,
     * We can only expose the interface to modify the member. This member should and should only be modified once.
     *
     * @param type The type of the source language object to which this identifier symbol can be bound
     */
    public void setType(SourceCodeType type) {
        if (this.type != null) {
            throw new RuntimeException("Can NOT set type for an entry twice");
        }

        this.type = type;
    }

    private final String text;
    private SourceCodeType type;
}
