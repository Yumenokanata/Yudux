package indi.yume.yudux.functions;

/**
 * Created by yume on 17-4-27.
 */
public final class Unit {
    private static final Unit u = new Unit();

    private Unit() {

    }

    /**
     * The only value of the unit type.
     *
     * @return The only value of the unit type.
     */
    public static Unit unit() {
        return u;
    }

    @Override
    public String toString() {
        return "unit";
    }

}
