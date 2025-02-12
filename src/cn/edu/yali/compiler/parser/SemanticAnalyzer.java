package cn.edu.yali.compiler.parser;

import cn.edu.yali.compiler.lexer.Token;
import cn.edu.yali.compiler.parser.table.*;
import cn.edu.yali.compiler.parser.table.Production;
import cn.edu.yali.compiler.parser.table.Status;
import cn.edu.yali.compiler.symtab.SourceCodeType;
import cn.edu.yali.compiler.symtab.SymbolTable;
import cn.edu.yali.compiler.symtab.SymbolTableEntry;

import java.util.Stack;

// TODO: 实验三: 实现语义分析
public class SemanticAnalyzer implements ActionObserver {

    private SymbolTable tmp_table;
    private Stack<Token> semantic_stack_1 = new Stack<>();
    private Stack<SourceCodeType> semantic_stack_2 = new Stack<>();

    @Override
    public void whenAccept(Status currentStatus) {
        // TODO: 该过程在遇到 Accept 时要采取的代码动作
        semantic_stack_1.clear();
        semantic_stack_2.clear();
        tmp_table = null;
    }

    @Override
    public void whenReduce(Status currentStatus, Production production) {
        // TODO: 该过程在遇到 reduce production 时要采取的代码动作

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
        // TODO: 该过程在遇到 shift 时要采取的代码动作
        semantic_stack_1.push(currentToken);
    }

    @Override
    public void setSymbolTable(SymbolTable table) {
        // TODO: 设计你可能需要的符号表存储结构
        // 如果需要使用符号表的话, 可以将它或者它的一部分信息存起来, 比如使用一个成员变量存储
        tmp_table = table;
    }
}

