package cn.edu.yali.compiler.utils;

/**
 * Various paths that may be used in the program
 */
public final class FilePathConfig {
    //==================================== Input File ========================================//
    /**
     * Input source code to be compiled
     */
    public static final String SRC_CODE_PATH = "data/in/input_code.txt";

    /**
     * Coding table
     */
    public static final String CODING_MAP_PATH = "data/in/coding_map.csv";

    /**
     * Grammar Files
     */
    public final static String GRAMMAR_PATH = "data/in/grammar.txt";

    /**
     * LR analysis table constructed by third-party tools
     */
    public final static String LR1_TABLE_PATH = "data/in/LR1_table.csv";


    //==================================== Output File ========================================//
    /**
     * Token Stream
     */
    public static final String TOKEN_PATH = "data/out/token.txt";

    /**
     * Symbol table before semantic analysis
     */
    public static final String OLD_SYMBOL_TABLE = "data/out/old_symbol_table.txt";

    /**
     * The list of production rules reduced
     */
    public static final String PARSER_PATH = "data/out/parser_list.txt";

    /**
     * Symbol table after semantic analysis
     */
    public static final String NEW_SYMBOL_TABLE = "data/out/new_symbol_table.txt";

    /**
     * Intermediate Code
     */
    public static final String INTERMEDIATE_CODE_PATH = "data/out/intermediate_code.txt";

    /**
     * Results of IR simulation execution
     */
    public static final String EMULATE_RESULT = "data/out/ir_emulate_result.txt";

    /**
     * Assembly Code
     */
    public static final String ASSEMBLY_LANGUAGE_PATH = "data/out/assembly_language.asm";

    private FilePathConfig() {
    }
}
