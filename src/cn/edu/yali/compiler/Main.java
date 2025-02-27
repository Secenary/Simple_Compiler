package cn.edu.yali.compiler;

import cn.edu.yali.compiler.asm.AssemblyGenerator;
import cn.edu.yali.compiler.lexer.LexicalAnalyzer;
import cn.edu.yali.compiler.lexer.TokenKind;
import cn.edu.yali.compiler.parser.IRGenerator;
import cn.edu.yali.compiler.parser.ProductionCollector;
import cn.edu.yali.compiler.parser.SemanticAnalyzer;
import cn.edu.yali.compiler.parser.SyntaxAnalyzer;
import cn.edu.yali.compiler.parser.table.GrammarInfo;
import cn.edu.yali.compiler.parser.table.TableLoader;
import cn.edu.yali.compiler.symtab.SymbolTable;
import cn.edu.yali.compiler.utils.FilePathConfig;
import cn.edu.yali.compiler.utils.FileUtils;
import cn.edu.yali.compiler.utils.IREmulator;
import java.util.Objects;

public class Main {
    public static void main(String[] args) {
        // Build a symbol table for use by all components/modules
        TokenKind.loadTokenKinds();
        final var symbolTable = new SymbolTable();

        // Lexical Analysis
        final var lexer = new LexicalAnalyzer(symbolTable);
        lexer.loadFile(FilePathConfig.SRC_CODE_PATH);
        lexer.run();
        lexer.dumpTokens(FilePathConfig.TOKEN_PATH);
        final var tokens = lexer.getTokens();
        symbolTable.dumpTable(FilePathConfig.OLD_SYMBOL_TABLE);

        // Read LR analysis tables constructed by third-party programs
        final var tableLoader = new TableLoader();
        final var lrTable = tableLoader.load(FilePathConfig.LR1_TABLE_PATH);

        // // Or use the framework to construct the LR analysis table directly from grammar.txt
        // final var tableGenerator = new TableGenerator();
        // tableGenerator.run();
        // final var lrTable = tableGenerator.getTable();
        // lrTable.dumpTable("data/out/lrTable.csv");

        // Loading the LR Analysis Driver
        final var parser = new SyntaxAnalyzer(symbolTable);
        parser.loadTokens(tokens);
        parser.loadLRTable(lrTable);

        // Add an Observer to the list of generated specifications
        final var productionCollector = new ProductionCollector(GrammarInfo.getBeginProduction());
        parser.registerObserver(productionCollector);

        // Add Observer for semantic checking 
        final var semanticAnalyzer = new SemanticAnalyzer();
        parser.registerObserver(semanticAnalyzer);

        // Add Observer for IR generation
        final var irGenerator = new IRGenerator();
        parser.registerObserver(irGenerator);

        // Perform syntax parsing and call each Observer in turn during the parsing process
        parser.run();

        // Output results of each Observer
        productionCollector.dumpToFile(FilePathConfig.PARSER_PATH);
        symbolTable.dumpTable(FilePathConfig.NEW_SYMBOL_TABLE);
        final var instructions = irGenerator.getIR();
        irGenerator.dumpIR(FilePathConfig.INTERMEDIATE_CODE_PATH);

        // Simulate the execution of the IR and output the results
        final var emulator = IREmulator.load(instructions);
        FileUtils.writeFile(FilePathConfig.EMULATE_RESULT, emulator.execute().map(Objects::toString).orElse("No return value"));

        // Generating assembly from IR
        final var asmGenerator = new AssemblyGenerator();
        asmGenerator.loadIR(instructions);
        asmGenerator.run();
        asmGenerator.dump(FilePathConfig.ASSEMBLY_LANGUAGE_PATH);
    }
}
