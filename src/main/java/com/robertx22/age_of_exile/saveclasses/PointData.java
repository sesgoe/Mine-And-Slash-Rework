package com.robertx22.age_of_exile.saveclasses;

public class PointData {
    public int x;
    public int y;

    protected PointData() {

    }

    public PointData(int x, int y) {
        this.x = x;
        this.y = y;
    }


    @Override
    public String toString() {
        return x + "_" + y;
    }


    public PointData up() {
        return new PointData(x, y + 1);
    }

    public PointData down() {
        return new PointData(x, y - 1);
    }

    public PointData left() {
        return new PointData(x - 1, y);
    }

    public PointData right() {
        return new PointData(x + 1, y);
    }

    @Override
    public int hashCode() {
        long bits = java.lang.Double.doubleToLongBits(x);
        bits ^= java.lang.Double.doubleToLongBits(y) * 31;
        return (((int) bits) ^ ((int) (bits >> 32)));
    }

    @Override
    public boolean equals(Object obj) { // otherwise hashmaps dont work
        if (obj instanceof PointData) {
            PointData pt = (PointData) obj;
            return (x == pt.x) && (y == pt.y);
        }
        return false;
    }


}
