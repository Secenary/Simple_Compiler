package cn.edu.yali.compiler.ir;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Instructions in intermediate representation.
 * <br>
 * If you need to extend IR, you can modify this file. However, you may need to modify the contents of IRGenerator, AssemblyGenerator and IREmulator simultaneously.
 * <br>
 * The intermediate representation of this project adopts the form of three-address code/quadruple, and the instructions are separated from IR variables (also known as virtual registers). The implementation adopts the form of homogeneous IR + auxiliary getter, which has the advantages of
 * realizing a unified storage structure for instructions with different numbers of parameters, and providing human-readable parameter access support.
 */
public class Instruction {
    //============================== Constructors for different kinds of IR ==============================
    public static Instruction createAdd(IRVariable result, IRValue lhs, IRValue rhs) {
        return new Instruction(InstructionKind.ADD, result, List.of(lhs, rhs));
    }

    public static Instruction createSub(IRVariable result, IRValue lhs, IRValue rhs) {
        return new Instruction(InstructionKind.SUB, result, List.of(lhs, rhs));
    }

    public static Instruction createMul(IRVariable result, IRValue lhs, IRValue rhs) {
        return new Instruction(InstructionKind.MUL, result, List.of(lhs, rhs));
    }

    public static Instruction createMov(IRVariable result, IRValue from) {
        return new Instruction(InstructionKind.MOV, result, List.of(from));
    }

    public static Instruction createRet(IRValue returnValue) {
        return new Instruction(InstructionKind.RET, null, List.of(returnValue));
    }


    //============================== Parameter getters for different types of IR ==============================
    public InstructionKind getKind() {
        return kind;
    }

    public IRVariable getResult() {
        ensureKindMatch(Set.of(InstructionKind.ADD, InstructionKind.SUB, InstructionKind.MUL, InstructionKind.MOV));
        return result;
    }

    public IRValue getLHS() {
        ensureKindMatch(Set.of(InstructionKind.ADD, InstructionKind.SUB, InstructionKind.MUL));
        return operands.get(0);
    }

    public IRValue getRHS() {
        ensureKindMatch(Set.of(InstructionKind.ADD, InstructionKind.SUB, InstructionKind.MUL));
        return operands.get(1);
    }

    public IRValue getFrom() {
        ensureKindMatch(Set.of(InstructionKind.MOV));
        return operands.get(0);
    }

    public IRValue getReturnValue() {
        ensureKindMatch(Set.of(InstructionKind.RET));
        return operands.get(0);
    }


    //============================== Infrastructure ==============================
    @Override
    public String toString() {
        final var kindString = kind.toString();
        final var resultString = result == null ? "" : result.toString();
        final var operandsString = operands.stream().map(Objects::toString).collect(Collectors.joining(", "));
        return "(%s, %s, %s)".formatted(kindString, resultString, operandsString);
    }

    public List<IRValue> getOperands() {
        return Collections.unmodifiableList(operands);
    }

    private Instruction(InstructionKind kind, IRVariable result, List<IRValue> operands) {
        this.kind = kind;
        this.result = result;
        this.operands = operands;
    }

    private final InstructionKind kind;
    private final IRVariable result;
    private final List<IRValue> operands;

    private void ensureKindMatch(Set<InstructionKind> targetKinds) {
        final var kind = getKind();
        if (!targetKinds.contains(kind)) {
            final var acceptKindsString = targetKinds.stream()
                .map(InstructionKind::toString)
                .collect(Collectors.joining(","));

            throw new RuntimeException(
                "Illegal operand access, except %s, but given %s".formatted(acceptKindsString, kind));
        }
    }
}
