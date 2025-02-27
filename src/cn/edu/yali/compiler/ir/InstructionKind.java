package cn.edu.yali.compiler.ir;

/**
 * Types of IR
 */
public enum InstructionKind {
    ADD, SUB, MUL, MOV, RET;

    /**
     * @return Whether the IR is binary (has a return value, has two parameters)
     */
    public boolean isBinary() {
        return this != MOV && this != RET;
    }

    /**
     * @return Whether the IR is unary (has a return value, has one parameter)
     */
    public boolean isUnary() {
        return this == MOV;
    }

    /**
     * @return Is the IR a RET instruction?
     */
    public boolean isReturn() {
        return this == RET;
    }
}
