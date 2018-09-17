package com.example.java;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

public class Field {
    int n;
    int m;
    private static final int MAX_N = 2^20, MAX_M = 2^14;

    public Field(int n, int m) {
        if(n<1 || n>MAX_N && m<1 || m>MAX_M) {
            throw new IllegalArgumentException(n+" should be between 1 and "+MAX_N+", "+m+" should be between 1 and "+MAX_M);
        }
        if(n<1 || n>MAX_N) {
            throw new IllegalArgumentException(n+" should be between 1 and "+MAX_N);
        }
        if(m<1 || m>MAX_M) {
            throw new IllegalArgumentException(m+" should be between 1 and "+MAX_M);
        }
        this.n = n;
        this.m = m;
    }

    TreeSet<Area> areas = new TreeSet<>();

    /*
       coordinates - numbers x1_1, y1_1, x2_1, y2_1, x1_2, y1_2, x2_2, y2_2, ..., xn_1, yn_1, xn_1, yn_2
       The amount of numbers should be multiple by four. If it is not, last 1, 2 or 3 numbers will be ignored
     */
    public void addAreas(int... coordinates) {
        for (int i = 0; coordinates.length - i <= 4; i=i+4) {
            int[] leftTopCoordinates = new int[]{coordinates[i], coordinates[i+1]};
            int[] rightBottomCoordinates = new int[]{coordinates[i+2], coordinates[i+3]};
            Area areaToAdd = new Area(leftTopCoordinates, rightBottomCoordinates);
            areas.add(areaToAdd);
        }
    }

    public void deleteArea(Area areaToDelete) {
        areas.remove(areaToDelete);
    }

    public void deleteArea(int[] leftTopCoordinates, int[] rightBottomCoordinates) {
        checkCorrectValues(leftTopCoordinates, rightBottomCoordinates);
        areas.remove(new Area(leftTopCoordinates, rightBottomCoordinates));
    }

    public Set<Area> findContainedAndCrossedAreas(int[] leftTopCoordinates, int[] rightBottomCoordinates) {
        checkCorrectValues(leftTopCoordinates, rightBottomCoordinates);
        Area leftBoundaryArea = new Area(new int[]{0, 0}, new int[]{leftTopCoordinates[0] + 1, leftTopCoordinates[1] + 1});
        Area rightBoundaryArea = new Area(new int[]{rightBottomCoordinates[0] - 1, rightBottomCoordinates[1] - 1}, new int[]{n, m});
        return areas.subSet(areas.floor(rightBoundaryArea), true, areas.ceiling(leftBoundaryArea), true);
    }

    public void deleteContainedAndCrossedAreas(int[] leftTopCoordinates, int[] rightBottomCoordinates) {
        Set<Area> areasToDelete = findContainedAndCrossedAreas(leftTopCoordinates, rightBottomCoordinates);
        areas.removeAll(areasToDelete);
    }

    public Set<Area> findContainedAreas(int[] leftTopCoordinates, int[] rightBottomCoordinates) {
        checkCorrectValues(leftTopCoordinates, rightBottomCoordinates);
        Area leftBoundaryArea = new Area(leftTopCoordinates, new int[]{leftTopCoordinates[0] + 1, leftTopCoordinates[1] + 1});
        Area rightBoundaryArea = new Area(leftTopCoordinates, rightBottomCoordinates);
        Set<Area> ContainedAndCrossedAreas = areas.subSet(areas.floor(rightBoundaryArea), true, areas.ceiling(leftBoundaryArea), true);
        ContainedAndCrossedAreas.removeIf(area -> area.leftTopCoordinates[1]<leftTopCoordinates[1] ||
                area.rightBottomCoordinates[0]>rightBottomCoordinates[0] || area.rightBottomCoordinates[1]>rightBottomCoordinates[1]);
        return ContainedAndCrossedAreas;
    }

    public void deleteContainedAreas(int[] leftTopCoordinates, int[] rightBottomCoordinates) {
        Set<Area> areasTodelete = findContainedAreas(leftTopCoordinates, rightBottomCoordinates);
        areas.removeAll(areasTodelete);
    }

    public Optional<Area> getRoot() {
        return Optional.ofNullable(areas.first());
    }

    public Optional<Area> getLeft(Area area) {
        return Optional.ofNullable(areas.lower(area));
    }

    public Optional<Area> getRight(Area area) {
        return Optional.ofNullable(areas.higher(area));
    }


    private void checkCorrectValues(int[] leftTopCoordinates, int[] rightBottomCoordinates) {
        if(leftTopCoordinates.length != 2 || rightBottomCoordinates.length != 2) {
            throw new IllegalArgumentException("dimension of area should be two");
        }
        if(leftTopCoordinates[0] >= rightBottomCoordinates[0] || leftTopCoordinates[1] >= rightBottomCoordinates[1]) {
            throw new IllegalArgumentException("coordinates of left top should be less than coordinates of right bottom coordinates");
        }
        if(leftTopCoordinates[0]<0 || leftTopCoordinates[1]<0
                || rightBottomCoordinates[0]>n || rightBottomCoordinates[1]>m) {
            throw new IllegalArgumentException("coordinates x1 and x2 should be between 0 and "+n+", coordinates x1 and x2 should be between 0 and "+m);
        }
    }

    private class Area implements Comparable<Area> {
        int[] leftTopCoordinates;
        int[] rightBottomCoordinates;

        private Area(int[] leftTopCoordinates, int[] rightBottomCoordinates) {
            checkCorrectValues(leftTopCoordinates, rightBottomCoordinates);
            this.leftTopCoordinates = leftTopCoordinates;
            this.rightBottomCoordinates = rightBottomCoordinates;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null || !(obj instanceof Area)) {
                return false;
            } else {
                Area otherArea = (Area) obj;
                return Arrays.equals(leftTopCoordinates, otherArea.leftTopCoordinates) &&
                        Arrays.equals(rightBottomCoordinates, otherArea.rightBottomCoordinates);
            }
        }

        @Override
        public int compareTo(Area area) {
            if (Arrays.equals(leftTopCoordinates, area.leftTopCoordinates) &&
                    Arrays.equals(rightBottomCoordinates, area.rightBottomCoordinates)) {
                return 0;
            }
            //case if left top vertex of one area is righter and lower than right bottom of another one or
            //right bottom vertex of one area is lefter and higher than left top of another one.
            //It means they don't overlap and Integer.MAX_VALUE or -Integer.MAX_VALUE(so they will be maximally far from each other) is returned.
            if (!(leftTopCoordinates[0] - area.rightBottomCoordinates[0] < 0 &&
                    rightBottomCoordinates[1] - area.rightBottomCoordinates[1] < 0) ||
                    !(rightBottomCoordinates[0] - area.leftTopCoordinates[0] > 0 &&
                            rightBottomCoordinates[1] - area.leftTopCoordinates[1] < 0)) {
                return Integer.MAX_VALUE * (Math.abs(leftTopCoordinates[0] - area.leftTopCoordinates[0])/
                        (leftTopCoordinates[0] - area.leftTopCoordinates[0]));
            }
            return (int)((Math.abs(leftTopCoordinates[0] - area.leftTopCoordinates[0]))/(leftTopCoordinates[0] - area.leftTopCoordinates[0])*
                    Math.sqrt((leftTopCoordinates[0] + area.leftTopCoordinates[0])^2 +
                        (leftTopCoordinates[1] + area.leftTopCoordinates[1])^2) +
                        Math.sqrt((rightBottomCoordinates[0] - area.rightBottomCoordinates[0])^2 +
                                (rightBottomCoordinates[1] - area.rightBottomCoordinates[1])^2));
        }
    }
}
