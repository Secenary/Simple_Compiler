package cn.edu.yali.compiler.ir;

/**
 * "Variables" in IR
 * <br>
 * We allow variables in IR to store some information about the source language -- for example, whether it corresponds to a specific source language variable in the source language or a temporary variable in the source language. This information is represented by a string
 * name. Temporary variables are named "$[0-9]+", while non-temporary variables are named "[a-zA-Z_][a-zA-Z0-9_]*"
 * <br>
 * IR variables are uniquely identified by name.
 */
public class IRVariable implements IRValue {
    /**
     * @param name The name of the variable in the source language
     * @return An IRVariable corresponding to a specific variable in the source language
     */
    public static IRVariable named(String name) {
        return new IRVariable(name);
    }

    /**
     * @return a new IRVariable corresponding to the temporary variable in the source language
     */
    public static IRVariable temp() {
        return new IRVariable("$" + count++);
    }

    public String getName() {
        return name;
    }

    public boolean isTemp() {
        return name.startsWith("$");
    }

    public boolean isNamed() {
        return !isTemp();
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof IRVariable reg && name.equals(reg.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    private IRVariable(String name) {
        this.name = name;
    }

    private final String name;
    private static int count = 0;
}
