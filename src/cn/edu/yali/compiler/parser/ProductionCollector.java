package cn.edu.yali.compiler.parser;

import cn.edu.yali.compiler.lexer.Token;
import cn.edu.yali.compiler.parser.table.Production;
import cn.edu.yali.compiler.parser.table.Status;
import cn.edu.yali.compiler.symtab.SymbolTable;
import cn.edu.yali.compiler.utils.FileUtils;
import java.util.ArrayList;
import java.util.List;

/**
 * Collector of reduced productions
 * <br>
 * This class registers itself as the action observer of the LR driver, saves the reduced productions in each reduce, and outputs all the reduced productions in the order of the reduction after the syntax analysis is completed.
 * The output of this class will be used as the basis for judging the correctness of the code in Experiment 2.
 */
public class ProductionCollector implements ActionObserver {
    public ProductionCollector(Production beginProduction) {
        this.beginProduction = beginProduction;
    }

    private final Production beginProduction;
    private final List<Production> reducedProductions = new ArrayList<>();

    /**
     * Dump the results to a file
     *
     * @param path File Path
     */
    public void dumpToFile(String path) {
        FileUtils.writeLines(path, reducedProductions.stream().map(Production::toString).toList());
    }

    @Override
    public void whenReduce(Status currentStatus, Production production) {
        // When reducing, record the reduced productions
        reducedProductions.add(production);
    }

    @Override
    public void whenShift(Status currentStatus, Token currentToken) {
        // do nothing
    }

    @Override
    public void whenAccept(Status currentStatus) {
        // When accepted, record the specification of the starting production
        reducedProductions.add(beginProduction);
    }

    @Override
    public void setSymbolTable(SymbolTable table) {
        // do nothing
    }
}
