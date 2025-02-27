package cn.edu.yali.compiler.parser;

import cn.edu.yali.compiler.lexer.Token;
import cn.edu.yali.compiler.parser.table.*;
import cn.edu.yali.compiler.parser.table.Production;
import cn.edu.yali.compiler.parser.table.Status;
import cn.edu.yali.compiler.symtab.SourceCodeType;
import cn.edu.yali.compiler.symtab.SymbolTable;
import cn.edu.yali.compiler.symtab.SymbolTableEntry;

import java.util.Stack;

// Experiment 3: Implement semantic analysis
public class SemanticAnalyzer implements ActionObserver {

    private SymbolTable tmp_table;
    private Stack<Token> semantic_stack_1 = new Stack<>();
    private Stack<SourceCodeType> semantic_stack_2 = new Stack<>();

    @Override
    public void whenAccept(Status currentStatus) {
        // The code actions to be taken when the process encounters Accept
        semantic_stack_1.clear();
        semantic_stack_2.clear();
        tmp_table = null;
    }

    @Override
    public void whenReduce(Status currentStatus, Production production) {
        // The code actions to be taken when the process encounters reduce production

        if (production.body().getFirst().toString().equals("int")) {
            semantic_stack_2.push(SourceCodeType.Int); //RISC-V 32bit only has "Int" = =
        }

        if (production.head().toString().equals("S") && production.body().getLast().toString().equals("id") && tmp_table.has(semantic_stack_1.peek().getText())) {
            SymbolTableEntry demo = tmp_table.get(semantic_stack_1.peek().getText());
            demo.setType(semantic_stack_2.peek());
            semantic_stack_2.pop();
            semantic_stack_1.pop();
            semantic_stack_1.pop();
        }
    }

    @Override
    public void whenShift(Status currentStatus, Token currentToken) {
        // The code actions to be taken when the process encounters a shift
        semantic_stack_1.push(currentToken);
    }

    @Override
    public void setSymbolTable(SymbolTable table) {
        // Design the symbol table storage structure you may need
        // If you need to use a symbol table, you can store it or part of its information, such as using a member variable to store
        tmp_table = table;
    }
}

