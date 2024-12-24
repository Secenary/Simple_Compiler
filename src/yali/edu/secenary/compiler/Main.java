package yali.edu.secenary.compiler;

import yali.edu.secenary.compiler.asm.AssemblyGenerator;
import yali.edu.secenary.compiler.lexer.LexicalAnalyzer;
import yali.edu.secenary.compiler.lexer.TokenKind;
import yali.edu.secenary.compiler.parser.IRGenerator;
import yali.edu.secenary.compiler.parser.ProductionCollector;
import yali.edu.secenary.compiler.parser.SemanticAnalyzer;
import yali.edu.secenary.compiler.parser.SyntaxAnalyzer;
import yali.edu.secenary.compiler.parser.table.GrammarInfo;
import yali.edu.secenary.compiler.parser.table.TableLoader;
import yali.edu.secenary.compiler.symtab.SymbolTable;
import yali.edu.secenary.compiler.utils.FilePathConfig;
import yali.edu.secenary.compiler.utils.FileUtils;
import yali.edu.secenary.compiler.utils.IREmulator;

import java.util.Objects;

public class Main {
    public static void main(String[] args) {
        // 构建符号表以供各部分使用
        TokenKind.loadTokenKinds();
        final var symbolTable = new SymbolTable();

        // 词法分析
        final var lexer = new LexicalAnalyzer(symbolTable);
        lexer.loadFile(FilePathConfig.SRC_CODE_PATH);
        lexer.run();
        lexer.dumpTokens(FilePathConfig.TOKEN_PATH);
        final var tokens = lexer.getTokens();
        symbolTable.dumpTable(FilePathConfig.OLD_SYMBOL_TABLE);

        // 读取第三方程序构造的 LR 分析表
        final var tableLoader = new TableLoader();
        final var lrTable = tableLoader.load(FilePathConfig.LR1_TABLE_PATH);

        // // 或使用框架自带部分直接从 grammar.txt 构造 LR 分析表
        // final var tableGenerator = new TableGenerator();
        // tableGenerator.run();
        // final var lrTable = tableGenerator.getTable();
        // lrTable.dumpTable("data/out/lrTable.csv");

        // 加载 LR 分析驱动程序
        final var parser = new SyntaxAnalyzer(symbolTable);
        parser.loadTokens(tokens);
        parser.loadLRTable(lrTable);

        // 加入生成规约列表的 Observer
        final var productionCollector = new ProductionCollector(GrammarInfo.getBeginProduction());
        parser.registerObserver(productionCollector);

        // 加入用作语义检查的 Observer
        final var semanticAnalyzer = new SemanticAnalyzer();
        parser.registerObserver(semanticAnalyzer);

        // 加入用作 IR 生成的 Observer
        final var irGenerator = new IRGenerator();
        parser.registerObserver(irGenerator);

        // 执行语法解析并在解析过程中依次调用各 Observer
        parser.run();

        // 各 Observer 输出结果
        productionCollector.dumpToFile(FilePathConfig.PARSER_PATH);
        symbolTable.dumpTable(FilePathConfig.NEW_SYMBOL_TABLE);
        final var instructions = irGenerator.getIR();
        irGenerator.dumpIR(FilePathConfig.INTERMEDIATE_CODE_PATH);

        // 模拟执行 IR 并输出结果
        final var emulator = IREmulator.load(instructions);
        FileUtils.writeFile(FilePathConfig.EMULATE_RESULT, emulator.execute().map(Objects::toString).orElse("No return value"));

        // 由 IR 生成汇编
        final var asmGenerator = new AssemblyGenerator();
        asmGenerator.loadIR(instructions);
        asmGenerator.run();
        asmGenerator.dump(FilePathConfig.ASSEMBLY_LANGUAGE_PATH);
    }
}
