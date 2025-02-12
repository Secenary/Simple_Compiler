package cn.edu.yali.compiler.parser;

import cn.edu.yali.compiler.ir.IRImmediate;
import cn.edu.yali.compiler.ir.IRVariable;
import cn.edu.yali.compiler.ir.Instruction;
import cn.edu.yali.compiler.lexer.Token;
import cn.edu.yali.compiler.parser.table.Production;
import cn.edu.yali.compiler.parser.table.Status;
import cn.edu.yali.compiler.symtab.SymbolTable;
import cn.edu.yali.compiler.utils.FileUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;


import static cn.edu.yali.compiler.lexer.Token.normal;
import static java.lang.Character.isDigit;

// TODO: 实验三: 实现 IR 生成

/**
 *
 */
public class IRGenerator implements ActionObserver {

    private SymbolTable externSymbolTable;
    private List<Instruction> ir_list = new ArrayList<>();
    private Stack<Token> ir_stack = new Stack<>();
    private IRVariable DollarVariable;
    private int cnt = 0;

    @Override
    public void whenShift(Status currentStatus, Token currentToken) {
        // TODO
        ir_stack.push(currentToken);
    }

    private boolean judge(Stack<Token> tmp_stack) {
        if((tmp_stack.get(tmp_stack.size() - 1).getKind().toString().equals("id") || tmp_stack.get(tmp_stack.size() - 1).getKind().toString().equals("IntConst")) && tmp_stack.get(tmp_stack.size() - 2).getKind().toString().equals("=") && tmp_stack.get(tmp_stack.size() - 3).getKind().toString().equals("id")) return true;
        else return false;
    }

    @Override
    public void whenReduce(Status currentStatus, Production production) {
        // TODO

        if (production.head().toString().equals("S")) {
            if (production.body().getFirst().toString().equals("return"))
                ir_list.add(Instruction.createRet(IRVariable.named(ir_stack.peek().getText())));

            if (judge(ir_stack)) {
                boolean IsDigit2 = true;
                for (int i = 0; i < ir_stack.get(3).getText().length(); i++) {
                    char c = ir_stack.get(3).getText().charAt(i);
                    if(!isDigit(c)) IsDigit2 = false;
                }
                if(!IsDigit2)
                    ir_list.add(Instruction.createMov(IRVariable.named(ir_stack.get(1).getText()), IRVariable.named(ir_stack.get(3).getText())));
                else
                    ir_list.add(Instruction.createMov(IRVariable.named(ir_stack.get(1).getText()), IRImmediate.of(Integer.parseInt(ir_stack.get(3).getText()))));
            }
            ir_stack.clear();
        }

        if (production.body().toString().contains("+")) {
            String tmp1 = null, tmp2 = null;
            for(int i = ir_stack.size() - 1; i >= 0; i--) {
                if((ir_stack.get(i).getKind().toString().equals("id") || ir_stack.get(i).getKind().toString().equals("IntConst")) && tmp2 == null) {
                    tmp2 = ir_stack.get(i).getText();
                    continue;
                }
                if((ir_stack.get(i).getKind().toString().equals("id") || ir_stack.get(i).getKind().toString().equals("IntConst")) && tmp1 == null)
                    tmp1 = ir_stack.get(i).getText();
                if(tmp1 != null && tmp2 != null) break;
            }
            cnt = 0;
            while(cnt < 3) {
                if(ir_stack.peek().getKind().toString().equals("id")) cnt++;
                if(ir_stack.peek().getKind().toString().equals("IntConst")) cnt++;
                if(ir_stack.peek().getKind().toString().equals("+")) cnt++;
                ir_stack.pop();
            }
            DollarVariable = IRVariable.temp();
            ir_stack.push(normal("id", DollarVariable.getName()));
            boolean IsDigit1 = true, IsDigit2 = true;
            if (tmp1 != null) {
                for (int i = 0; i < tmp1.length(); i++) {
                    char c = tmp1.charAt(i);
                    if(!isDigit(c)) IsDigit1 = false;
                }
            }
            if (tmp2 != null) {
                for (int i = 0; i < tmp2.length(); i++) {
                    char c = tmp2.charAt(i);
                    if(!isDigit(c)) IsDigit2 = false;
                }
            }
            if(tmp1 != null && tmp2 != null) {
                if (!IsDigit1 && !IsDigit2)
                    ir_list.add(Instruction.createAdd(DollarVariable, IRVariable.named(tmp1), IRVariable.named(tmp2)));
                else if (IsDigit1 && !IsDigit2)
                    ir_list.add(Instruction.createAdd(DollarVariable, IRImmediate.of(Integer.parseInt(tmp1)), IRVariable.named(tmp2)));
                else if (!IsDigit1 && IsDigit2)
                    ir_list.add(Instruction.createAdd(DollarVariable, IRVariable.named(tmp1), IRImmediate.of(Integer.parseInt(tmp2))));
                else if (IsDigit1 && IsDigit2)
                    ir_list.add(Instruction.createAdd(DollarVariable, IRImmediate.of(Integer.parseInt(tmp1)), IRImmediate.of(Integer.parseInt(tmp2))));
            }
        }
        else if (production.body().toString().contains("-")) {
            String tmp1 = null, tmp2 = null;
            for(int i = ir_stack.size() - 1; i >= 0; i--) {
                if((ir_stack.get(i).getKind().toString().equals("id") || ir_stack.get(i).getKind().toString().equals("IntConst")) && tmp2 == null) {
                    tmp2 = ir_stack.get(i).getText();
                    continue;
                }
                if((ir_stack.get(i).getKind().toString().equals("id") || ir_stack.get(i).getKind().toString().equals("IntConst")) && tmp1 == null)
                    tmp1 = ir_stack.get(i).getText();
                if(tmp1 != null && tmp2 != null) break;
            }
            cnt = 0;
            while(cnt < 3) {
                if(ir_stack.peek().getKind().toString().equals("id")) cnt++;
                if(ir_stack.peek().getKind().toString().equals("IntConst")) cnt++;
                if(ir_stack.peek().getKind().toString().equals("-")) cnt++;
                ir_stack.pop();
            }
            DollarVariable = IRVariable.temp();
            ir_stack.push(normal("id", DollarVariable.getName()));
            boolean IsDigit1 = true, IsDigit2 = true;
            if (tmp1 != null) {
                for (int i = 0; i < tmp1.length(); i++) {
                    char c = tmp1.charAt(i);
                    if(!isDigit(c)) IsDigit1 = false;
                }
            }
            if (tmp2 != null) {
                for (int i = 0; i < tmp2.length(); i++) {
                    char c = tmp2.charAt(i);
                    if(!isDigit(c)) IsDigit2 = false;
                }
            }
            if(tmp1 != null && tmp2 != null) {
                if (!IsDigit1 && !IsDigit2)
                    ir_list.add(Instruction.createSub(DollarVariable, IRVariable.named(tmp1), IRVariable.named(tmp2)));
                else if (IsDigit1 && !IsDigit2)
                    ir_list.add(Instruction.createSub(DollarVariable, IRImmediate.of(Integer.parseInt(tmp1)), IRVariable.named(tmp2)));
                else if (!IsDigit1 && IsDigit2)
                    ir_list.add(Instruction.createSub(DollarVariable, IRVariable.named(tmp1), IRImmediate.of(Integer.parseInt(tmp2))));
                else if (IsDigit1 && IsDigit2)
                    ir_list.add(Instruction.createSub(DollarVariable, IRImmediate.of(Integer.parseInt(tmp1)), IRImmediate.of(Integer.parseInt(tmp2))));
            }
        }
        else if (production.body().toString().contains("*")) {
            String tmp1 = null, tmp2 = null;
            for(int i = ir_stack.size() - 1; i >= 0; i--) {
                if((ir_stack.get(i).getKind().toString().equals("id") || ir_stack.get(i).getKind().toString().equals("IntConst")) && tmp2 == null) {
                    tmp2 = ir_stack.get(i).getText();
                    continue;
                }
                if((ir_stack.get(i).getKind().toString().equals("id") || ir_stack.get(i).getKind().toString().equals("IntConst")) && tmp1 == null)
                    tmp1 = ir_stack.get(i).getText();
                if(tmp1 != null && tmp2 != null) break;
            }
            cnt = 0;
            while(cnt < 3) {
                if(ir_stack.peek().getKind().toString().equals("id")) cnt++;
                if(ir_stack.peek().getKind().toString().equals("IntConst")) cnt++;
                if(ir_stack.peek().getKind().toString().equals("*")) cnt++;
                ir_stack.pop();
            }
            DollarVariable = IRVariable.temp();
            ir_stack.push(normal("id", DollarVariable.getName()));
            boolean IsDigit1 = true, IsDigit2 = true;
            if (tmp1 != null) {
                for (int i = 0; i < tmp1.length(); i++) {
                    char c = tmp1.charAt(i);
                    if(!isDigit(c)) IsDigit1 = false;
                }
            }
            if (tmp2 != null) {
                for (int i = 0; i < tmp2.length(); i++) {
                    char c = tmp2.charAt(i);
                    if(!isDigit(c)) IsDigit2 = false;
                }
            }
            if(tmp1 != null && tmp2 != null) {
                if (!IsDigit1 && !IsDigit2)
                    ir_list.add(Instruction.createMul(DollarVariable, IRVariable.named(tmp1), IRVariable.named(tmp2)));
                else if (IsDigit1 && !IsDigit2)
                    ir_list.add(Instruction.createMul(DollarVariable, IRImmediate.of(Integer.parseInt(tmp1)), IRVariable.named(tmp2)));
                else if (!IsDigit1 && IsDigit2)
                    ir_list.add(Instruction.createMul(DollarVariable, IRVariable.named(tmp1), IRImmediate.of(Integer.parseInt(tmp2))));
                else if (IsDigit1 && IsDigit2)
                    ir_list.add(Instruction.createMul(DollarVariable, IRImmediate.of(Integer.parseInt(tmp1)), IRImmediate.of(Integer.parseInt(tmp2))));
            }
        }
    }


    @Override
    public void whenAccept(Status currentStatus) {
        // TODO
        ir_stack.clear();
    }

    @Override
    public void setSymbolTable(SymbolTable table) {
        // TODO
        externSymbolTable = table;
    }

    public List<Instruction> getIR() {
        // TODO
        return ir_list;
    }

    public void dumpIR(String path) {
        FileUtils.writeLines(path, getIR().stream().map(Instruction::toString).toList());
    }
}

