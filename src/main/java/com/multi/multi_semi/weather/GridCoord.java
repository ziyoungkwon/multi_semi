package com.multi.multi_semi.weather;

public class GridCoord {
    private final int nx;
    private final int ny;

    public GridCoord(int nx, int ny) {
        this.nx = nx;
        this.ny = ny;
    }

    public int getNx() {
        return nx;
    }

    public int getNy() {
        return ny;
    }

    @Override
    public String toString() {
        return "GridCoord{nx=" + nx + ", ny=" + ny + '}';
    }
}

