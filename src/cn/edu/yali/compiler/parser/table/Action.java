package cn.edu.yali.compiler.parser.table;

import java.util.Objects;

/**
 * Represents an action in the LR analysis table action table
 */
public class Action {
    public enum ActionKind {Reduce, Shift, Accept, Error}

    /**
     * @return Constructed Accept Action
     */
    public static Action accept() {
        return acceptInstance;
    }

    /**
     * @param status The state to be added to the state stack after moving in
     * @return Constructed shift action
     */
    public static Action shift(Status status) {
        return new Action(ActionKind.Shift, null, status);
    }

    /**
     * @param production Productions to be reduced
     * @return Constructed reduce action
     */
    public static Action reduce(Production production) {
        return new Action(ActionKind.Reduce, production, null);
    }

    /**
     * @return Constructed wrong action
     */
    public static Action error() {
        return errorInstance;
    }

    public ActionKind getKind() {
        return kind;
    }

    /**
     * @return Get the production of the reduce action
     * @throws RuntimeException Action is not a reduce action
     */
    public Production getProduction() {
        if (kind != ActionKind.Reduce) {
            throw new RuntimeException("Only reduce action could have a production");
        }

        assert production != null;
        return production;
    }

    /**
     * @return Get the status of the shift action
     * @throws RuntimeException The action is not a shift action
     */
    public Status getStatus() {
        if (kind != ActionKind.Shift) {
            throw new RuntimeException("Only shift action could hava a status");
        }

        assert status != null;
        return status;
    }

    @Override
    public String toString() {
        return switch (kind) {
            case Accept -> "accept";
            case Error -> "";
            case Reduce -> "reduce " + production;
            case Shift -> "shift " + status;
        };
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Action action
            && action.getKind().equals(kind)
            && switch (kind) {
            case Shift -> action.status.equals(status);
            case Reduce -> action.production.equals(production);
            case Accept, Error -> true;
        };
    }

    @Override
    public int hashCode() {
        return Objects.hash(kind, status, production);
    }

    private static final Action acceptInstance = new Action(ActionKind.Accept, null, null);
    private static final Action errorInstance = new Action(ActionKind.Error, null, null);

    private Action(ActionKind kind, Production production, Status status) {
        this.kind = kind;
        this.production = production;
        this.status = status;
    }

    private final ActionKind kind;
    private final Production production;
    private final Status status;
}
