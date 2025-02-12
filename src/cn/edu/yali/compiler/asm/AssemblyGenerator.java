package cn.edu.yali.compiler.asm;

import cn.edu.yali.compiler.ir.IRImmediate;
import cn.edu.yali.compiler.ir.IRVariable;
import cn.edu.yali.compiler.ir.Instruction;
import cn.edu.yali.compiler.parser.table.BMap;
import cn.edu.yali.compiler.utils.FileUtils;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;


/**
 * TODO: 实验四: 实现汇编生成
 * <br>
 * 在编译器的整体框架中, 代码生成可以称作后端, 而前面的所有工作都可称为前端.
 * <br>
 * 在前端完成的所有工作中, 都是与目标平台无关的, 而后端的工作为将前端生成的目标平台无关信息
 * 根据目标平台生成汇编代码. 前后端的分离有利于实现编译器面向不同平台生成汇编代码. 由于前后
 * 端分离的原因, 有可能前端生成的中间代码并不符合目标平台的汇编代码特点. 具体到本项目你可以
 * 尝试加入一个方法将中间代码调整为更接近 risc-v 汇编的形式, 这样会有利于汇编代码的生成.
 * <br>
 * 为保证实现上的自由, 框架中并未对后端提供基建, 在具体实现时可自行设计相关数据结构.
 *
 * @see AssemblyGenerator#run() 代码生成与寄存器分配
 */
public class AssemblyGenerator {
    private List<Instruction> tmpInstructions = new ArrayList<>();
    private List<String> AssemblyCode = new ArrayList<>();
    private BMap<IRVariable, String> Allocreg = new BMap<>();
    private IRVariable DollarVariable = IRVariable.temp();
    private boolean IsOcuupied = false;
    private LinkedHashSet<Integer> numbers = new LinkedHashSet<Integer>();

    /**
     * 加载前端提供的中间代码
     * <br>
     * 视具体实现而定, 在加载中或加载后会生成一些在代码生成中会用到的信息. 如变量的引用
     * 信息. 这些信息可以通过简单的映射维护, 或者自行增加记录信息的数据结构.
     *
     * @param originInstructions 前端提供的中间代码
     */
    public void loadIR(List<Instruction> originInstructions) {
        // TODO: 读入前端提供的中间代码并生成所需要的信息
        tmpInstructions = originInstructions;
        for (int i = 0; i < tmpInstructions.size(); i++) {
            Instruction tmpInstruction = tmpInstructions.get(i);

            if (tmpInstruction.getKind().toString().equals("ADD")) {
                String tmp1 = tmpInstruction.getLHS().toString();
                String tmp2 = tmpInstruction.getRHS().toString();
                boolean flag1 = true, flag2 = true;
                for (int j = 0; j < tmp1.length(); j++) {
                    char ch = tmp1.charAt(j);
                    if(!Character.isDigit(ch)) flag1 = false;
                }
                for (int j = 0; j < tmp2.length(); j++) {
                    char ch = tmp2.charAt(j);
                    if(!Character.isDigit(ch)) flag2 = false;
                }
                if (flag1 && flag2) {
                    int cal_result = Integer.parseInt(tmp1) + Integer.parseInt(tmp2);
                    tmpInstructions.set(i, Instruction.createMov(tmpInstruction.getResult(), IRImmediate.of(cal_result)));
                }
                else if (flag1) {
                    tmpInstructions.set(i, Instruction.createAdd(tmpInstruction.getResult(), tmpInstruction.getRHS(), tmpInstruction.getLHS()));
                }
            }

            else if (tmpInstruction.getKind().toString().equals("SUB")) {
                String tmp1 = tmpInstruction.getLHS().toString();
                String tmp2 = tmpInstruction.getRHS().toString();
                boolean flag1 = true, flag2 = true;
                for (int j = 0; j < tmp1.length(); j++) {
                    char ch = tmp1.charAt(j);
                    if(!Character.isDigit(ch)) flag1 = false;
                }
                for (int j = 0; j < tmp2.length(); j++) {
                    char ch = tmp2.charAt(j);
                    if(!Character.isDigit(ch)) flag2 = false;
                }
                if (flag1 && flag2) {
                    int cal_result = Integer.parseInt(tmp1) - Integer.parseInt(tmp2);
                    tmpInstructions.set(i, Instruction.createMov(tmpInstruction.getResult(), IRImmediate.of(cal_result)));
                }
                else if (flag1) {
                    tmpInstructions.set(i, Instruction.createMov(DollarVariable, tmpInstruction.getLHS()));
                    tmpInstructions.add(i + 1, Instruction.createSub(tmpInstruction.getResult(), DollarVariable, tmpInstruction.getRHS()));
                }
            }

            else if (tmpInstruction.getKind().toString().equals("MUL")) {
                String tmp1 = tmpInstruction.getLHS().toString();
                String tmp2 = tmpInstruction.getRHS().toString();
                boolean flag1 = true, flag2 = true;
                for (int j = 0; j < tmp1.length(); j++) {
                    char ch = tmp1.charAt(j);
                    if(!Character.isDigit(ch)) flag1 = false;
                }
                for (int j = 0; j < tmp2.length(); j++) {
                    char ch = tmp2.charAt(j);
                    if(!Character.isDigit(ch)) flag2 = false;
                }
                if (flag1 && flag2) {
                    int cal_result = Integer.parseInt(tmp1) + Integer.parseInt(tmp2);
                    tmpInstructions.set(i, Instruction.createMov(tmpInstruction.getResult(), IRImmediate.of(cal_result)));
                }
                else if (flag1) {
                    tmpInstructions.set(i, Instruction.createMov(DollarVariable, tmpInstruction.getLHS()));
                    tmpInstructions.add(i + 1, Instruction.createMul(tmpInstruction.getResult(), DollarVariable, tmpInstruction.getRHS()));
                }
                else if (flag2) {
                    tmpInstructions.set(i, Instruction.createMov(DollarVariable, tmpInstruction.getRHS()));
                    tmpInstructions.add(i + 1, Instruction.createMul(tmpInstruction.getResult(), DollarVariable, tmpInstruction.getLHS()));
                }
            }

            else if (tmpInstruction.getKind().toString().equals("RET")) {
                if(i + 1 < tmpInstructions.size()) {
                    for(int j = i + 1; j < tmpInstructions.size(); j++) tmpInstructions.remove(j);
                }
            }

        }
        AssemblyCode.add(".text");
    }


    /**
     * 执行代码生成.
     * <br>
     * 根据理论课的做法, 在代码生成时同时完成寄存器分配的工作. 若你觉得这样的做法不好,
     * 也可以将寄存器分配和代码生成分开进行.
     * <br>
     * 提示: 寄存器分配中需要的信息较多, 关于全局的与代码生成过程无关的信息建议在代码生
     * 成前完成建立, 与代码生成的过程相关的信息可自行设计数据结构进行记录并动态维护.
     */
    public void run() {
        // TODO: 执行寄存器分配与代码生成
        int cnt = 0;
        for (int i = 0; i < tmpInstructions.size(); i++) {
            var perInstruction = tmpInstructions.get(i);
            if (perInstruction.getKind().toString().equals("ADD") || perInstruction.getKind().toString().equals("SUB") || perInstruction.getKind().toString().equals("MUL")) {

                if (cnt == 7) {
                    IsOcuupied = true;
                    cnt = 0;
                }

                if (IsOcuupied) {
                    for (int j = i; j < tmpInstructions.size(); j++) {
                        if (tmpInstructions.get(j).getKind().toString().equals("ADD") || tmpInstructions.get(j).getKind().toString().equals("SUB") || tmpInstructions.get(j).getKind().toString().equals("MUL")) {
                            if (Allocreg.containsKey(tmpInstructions.get(j).getResult()))
                                numbers.add(Integer.valueOf(Allocreg.getByKey(tmpInstructions.get(j).getResult()).substring(1)));

                            if (Allocreg.containsKey((IRVariable) tmpInstructions.get(j).getLHS()))
                                numbers.add(Integer.valueOf(Allocreg.getByKey((IRVariable) tmpInstructions.get(j).getLHS()).substring(1)));

                            boolean flag3 = true;
                            for (int k = 0; k < perInstruction.getRHS().toString().length(); k++) {
                                char ch = perInstruction.getRHS().toString().charAt(k);
                                if(!Character.isDigit(ch)) flag3 = false;
                            }
                            if (!flag3 && Allocreg.containsKey((IRVariable) tmpInstructions.get(j).getRHS()))
                                numbers.add(Integer.valueOf(Allocreg.getByKey((IRVariable) tmpInstructions.get(j).getRHS()).substring(1)));

                        }
                    }

                    boolean flag4 = false;
                    for (int j = 0; j <= 6; j++) {
                        if (!numbers.contains(j)) {
                            StringBuilder t = new StringBuilder("t");
                            t.append(j);
                            String t_registers = t.toString();
                            numbers.add(j);
                            if(!Allocreg.containsKey(perInstruction.getResult())) {
                                Allocreg.replace(perInstruction.getResult(), t_registers);
                            }
                            flag4 = true;
                            break;
                        }
                    }

                    if (flag4) {
                        if (perInstruction.getKind().toString().equals("ADD")) {
                            AssemblyCode.add("    add " + Allocreg.getByKey(perInstruction.getResult()) + ", " + Allocreg.getByKey((IRVariable) perInstruction.getLHS()) + ", " + Allocreg.getByKey((IRVariable) perInstruction.getRHS()));
                        }
                        if (perInstruction.getKind().toString().equals("SUB")) {
                            AssemblyCode.add("    sub " + Allocreg.getByKey(perInstruction.getResult()) + ", " + Allocreg.getByKey((IRVariable) perInstruction.getLHS()) + ", " + Allocreg.getByKey((IRVariable) perInstruction.getRHS()));
                        }
                        if (perInstruction.getKind().toString().equals("MUL")) {
                            AssemblyCode.add("    mul " + Allocreg.getByKey(perInstruction.getResult()) + ", " + Allocreg.getByKey((IRVariable) perInstruction.getLHS()) + ", " + Allocreg.getByKey((IRVariable) perInstruction.getRHS()));
                        }
                    }

                    else throw new RuntimeException("No Registers Available");

                    continue;
                }

                if (!Allocreg.containsKey(perInstruction.getResult())) {
                    StringBuilder t = new StringBuilder("t");
                    t.append(cnt);
                    String t_registers = t.toString();
                    Allocreg.replace(perInstruction.getResult(), t_registers);
                    ++cnt;
                }

                if (!Allocreg.containsKey((IRVariable) perInstruction.getLHS())) {
                    StringBuilder t = new StringBuilder("t");
                    t.append(cnt);
                    String t_registers = t.toString();
                    Allocreg.replace((IRVariable) perInstruction.getLHS(), t_registers);
                    ++cnt;
                }

                boolean flag2 = true;
                for (int j = 0; j < perInstruction.getRHS().toString().length(); j++) {
                    char ch = perInstruction.getRHS().toString().charAt(j);
                    if(!Character.isDigit(ch)) flag2 = false;
                }
                if (!flag2 && !Allocreg.containsKey((IRVariable) perInstruction.getRHS())) {
                    StringBuilder t = new StringBuilder("t");
                    t.append(cnt);
                    String t_registers = t.toString();
                    Allocreg.replace((IRVariable) perInstruction.getRHS(), t_registers);
                    ++cnt;
                }

                if (perInstruction.getKind().toString().equals("ADD")) {
                    boolean flag = true;
                    for(int j = 0; j < perInstruction.getRHS().toString().length(); j++) {
                        char ch = perInstruction.getRHS().toString().charAt(j);
                        if(!Character.isDigit(ch)) flag = false;
                    }
                    if (flag)
                        AssemblyCode.add("    addi " + Allocreg.getByKey(perInstruction.getResult()) + ", " + Allocreg.getByKey((IRVariable) perInstruction.getLHS()) + ", " + perInstruction.getRHS());
                    else
                        AssemblyCode.add("    add " + Allocreg.getByKey(perInstruction.getResult()) + ", " + Allocreg.getByKey((IRVariable) perInstruction.getLHS()) + ", " + Allocreg.getByKey((IRVariable) perInstruction.getRHS()));
                }
                else if (perInstruction.getKind().toString().equals("SUB"))
                    AssemblyCode.add("    sub " + Allocreg.getByKey(perInstruction.getResult()) + ", " + Allocreg.getByKey((IRVariable) perInstruction.getLHS())  + ", " + Allocreg.getByKey((IRVariable) perInstruction.getRHS()));
                else if (perInstruction.getKind().toString().equals("MUL"))
                    AssemblyCode.add("    mul " + Allocreg.getByKey(perInstruction.getResult()) + ", " + Allocreg.getByKey((IRVariable) perInstruction.getLHS())  + ", " + Allocreg.getByKey((IRVariable) perInstruction.getRHS()));
            }

            if (perInstruction.getKind().toString().equals("MOV")) {
                if (!Allocreg.containsKey(perInstruction.getResult())) {

                    boolean flag = true;
                    for (int j = 0; j < perInstruction.getFrom().toString().length(); j++) {
                        char ch = perInstruction.getFrom().toString().charAt(j);
                        if(!Character.isDigit(ch)) flag = false;
                    }
                    if (flag) {
                        StringBuilder t = new StringBuilder("t");
                        t.append(cnt);
                        String t_registers = t.toString();
                        Allocreg.replace(perInstruction.getResult(), t_registers);
                        AssemblyCode.add("    li " + t_registers + ", " + perInstruction.getFrom());
                    }
                    else {
                        StringBuilder t = new StringBuilder("t");
                        t.append(cnt);
                        String t_registers = t.toString();
                        Allocreg.replace(perInstruction.getResult(), t_registers);
                        AssemblyCode.add("    mv " + t_registers + ", " + Allocreg.getByKey((IRVariable) perInstruction.getFrom()));
                    }
                    ++cnt;
                }
            }

            if (perInstruction.getKind().toString().equals("RET"))
                AssemblyCode.add("    mv a0, " + Allocreg.getByKey((IRVariable) perInstruction.getReturnValue()));
        }

    }


    /**
     * 输出汇编代码到文件
     *
     * @param path 输出文件路径
     */
    public void dump(String path) {
        // TODO: 输出汇编代码到文件
        FileUtils.writeLines(path, AssemblyCode.stream().toList());
    }
}