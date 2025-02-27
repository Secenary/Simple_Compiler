package cn.edu.yali.compiler.ir;

/**
 * Represents the "value" in IR, which may be an IR variable or an IR immediate value. In short, it is something that can be used as a parameter of Instruction.
 */
public interface IRValue {
    default boolean isIRVariable() {
        return this instanceof IRVariable;
    }

    default boolean isImmediate() {
        return this instanceof IRImmediate;
    }
}
