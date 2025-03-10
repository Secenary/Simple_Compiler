package cn.edu.yali.compiler.utils;

import cn.edu.yali.compiler.ir.IRImmediate;
import cn.edu.yali.compiler.ir.IRValue;
import cn.edu.yali.compiler.ir.IRVariable;
import cn.edu.yali.compiler.ir.Instruction;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Class used to simulate execution of IR
 */
public class IREmulator {
    public static IREmulator load(List<Instruction> instructions) {
        return new IREmulator(instructions);
    }

    public Optional<Integer> execute() {
        for (final var instruction : instructions) {
            switch (instruction.getKind()) {
                case MOV -> {
                    final var from = eval(instruction.getFrom());
                    environment.put(instruction.getResult(), from);
                }

                case ADD -> {
                    final var lhs = eval(instruction.getLHS());
                    final var rhs = eval(instruction.getRHS());
                    environment.put(instruction.getResult(), lhs + rhs);
                }

                case SUB -> {
                    final var lhs = eval(instruction.getLHS());
                    final var rhs = eval(instruction.getRHS());
                    environment.put(instruction.getResult(), lhs - rhs);
                }

                case MUL -> {
                    final var lhs = eval(instruction.getLHS());
                    final var rhs = eval(instruction.getRHS());
                    environment.put(instruction.getResult(), lhs * rhs);
                }

                case RET -> this.returnValue = eval(instruction.getReturnValue());

                default -> throw new RuntimeException("Unknown instruction kind: " + instruction.getKind());
            }
        }

        return Optional.ofNullable(this.returnValue);
    }

    public Integer eval(IRValue value) {
        if (value instanceof IRImmediate immediate) {
            return immediate.getValue();
        } else if (value instanceof IRVariable variable) {
            return environment.get(variable);
        } else {
            throw new RuntimeException("Unknown IR value type");
        }
    }

    private IREmulator(List<Instruction> instructions) {
        this.instructions = instructions;
        this.environment = new HashMap<>();
        this.returnValue = null;
    }

    private final List<Instruction> instructions;
    private final Map<IRVariable, Integer> environment;
    private Integer returnValue;
}
