package cn.edu.yali.compiler.ir;

/**
 * Immediate Values ​​in IR
 */
public class IRImmediate implements IRValue {
    public static IRImmediate of(int value) {
        return new IRImmediate(value);
    }

    public int getValue() {
        return value;
    }

    private final int value;

    @Override
    public String toString() {
        return Integer.toString(value);
    }

    private IRImmediate(int value) {
        this.value = value;
    }
}
