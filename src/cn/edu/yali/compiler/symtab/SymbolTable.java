package cn.edu.yali.compiler.symtab;

import cn.edu.yali.compiler.utils.FileUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * Symbol table
 * <br>
 * Because the source language is relatively simple, and Java has very useful general data structure types, this project does not necessarily need a centralized "symbol table" to store all the information of all symbols in the source language. However, in order to meet the theoretical course teaching and improve the universality of experimental practice skills, we designed this symbol table according to the design of the symbol table in the general compiler project.
 * Its role in the code may not be obvious, but we hope that students can experience the design concept of the symbol table through this.
 */
public class SymbolTable {

    public Map<String, SymbolTableEntry> table = new HashMap<>();
    /**
     * Get an existing entry in the symbol table
     *
     * @param text The text representation of the symbol
     * @return The entry for the symbol in the symbol table
     * @throws RuntimeException The symbol does not exist in the table
     */
    public SymbolTableEntry get(String text) {
        if(has(text)) return table.get(text);
        else throw new RuntimeException();
    }
    /**
     * Add a new entry to the symbol table
     *
     * @param text The text representation of the new symbol to be added to the symbol table
     * @return The new entry corresponding to the symbol in the symbol table
     * @throws RuntimeException The symbol already exists in the table
     */
    public SymbolTableEntry add(String text) {
        if (has(text)) {
            throw new RuntimeException();
        } else {
            SymbolTableEntry tableEntry = new SymbolTableEntry(text);
            table.put(text, tableEntry);
            return tableEntry;
        }
    }

    /**
     * Check if there is an entry in the symbol table
     *
     * @param text The text representation of the symbol to be checked
     * @return Whether the entry of the symbol is in the symbol table
     */
    public boolean has(String text) {
        return table.containsKey(text);
    }

    /**
     * Get all entries in the symbol table for use with {@code dumpTable}
     *
     * @return all entries in the symbol table
     */
    private Map<String, SymbolTableEntry> getAllEntries() {
        return table;
    }

    /**
     * Output the symbol table in format
     *
     * @param path Output file path
     */
    public void dumpTable(String path) {
        final var entriesInOrder = new ArrayList<>(getAllEntries().values());
        entriesInOrder.sort(Comparator.comparing(SymbolTableEntry::getText));

        final var lines = new ArrayList<String>();
        for (final var entry : entriesInOrder) {
            // null in %s will be "null"
            lines.add("(%s, %s)".formatted(entry.getText(), entry.getType()));
        }

        FileUtils.writeLines(path, lines);
    }
}

