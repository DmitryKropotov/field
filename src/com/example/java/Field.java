package com.example.java;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

public class Field {
    private int n;
    private int m;
    private static final int MAX_N = (int)Math.pow(2, 20), MAX_M = (int)Math.pow(2, 14);

    public Field(int n, int m) {
        if((n<1 || n>MAX_N) && (m<1 || m>MAX_M)) {
            throw new IllegalArgumentException("n should be between 1 and "+MAX_N+", m should be between 1 and "+MAX_M);
        }
        if(n<1 || n>MAX_N) {
            throw new IllegalArgumentException("n should be between 1 and "+MAX_N);
        }
        if(m<1 || m>MAX_M) {
            throw new IllegalArgumentException("m should be between 1 and "+MAX_M);
        }
        this.n = n;
        this.m = m;
    }

    private TreeSet<Area> areas = new TreeSet<>();

    /*
       coordinates - numbers x1_1, y1_1, x2_1, y2_1, x1_2, y1_2, x2_2, y2_2, ..., xn_1, yn_1, xn_2, yn_2
       The amount of numbers should be multiple by four. If it is not, last 1, 2 or 3 numbers will be ignored
       Four coordinates xm_1, ym_1, xm_2, ym_2 should satisfy relation xm_1<xm_2, ym_1<ym_2, otherwise they
       will be ignored
     */
    public void addAreas(int... coordinates) {
        Set<Area> areasToAdd = new HashSet<>();
        for (int i = 0; coordinates.length - i >= 4; i=i+4) {
            try {
                int[] leftTopCoordinates = new int[]{coordinates[i], coordinates[i + 1]};
                int[] rightBottomCoordinates = new int[]{coordinates[i + 2], coordinates[i + 3]};
                areasToAdd.add(new Area(leftTopCoordinates, rightBottomCoordinates));
            } catch (IllegalArgumentException e) {
                System.out.println("Exception " + e + " in method addArea");
            }
        }
        areas.addAll(areasToAdd);
    }

    public void deleteArea(Area areaToDelete) {
        areas.remove(areaToDelete);
    }

    public void deleteArea(int[] leftTopCoordinates, int[] rightBottomCoordinates) {
         areas.remove(new Area(leftTopCoordinates, rightBottomCoordinates));
    }

    /*
      coordinates - numbers x1_1, y1_1, x2_1, y2_1, x1_2, y1_2, x2_2, y2_2, ..., xn_1, yn_1, xn_2, yn_2
      The amount of numbers should be multiple by four. If it is not, last 1, 2 or 3 numbers will be ignore
    */
    public void deleteAreas(int... coordinates) {
        Set<Area> areasToDelete = new HashSet<>();
        for (int i = 0; coordinates.length - i >= 4; i=i+4) {
            try {
                int[] leftTopCoordinates = new int[]{coordinates[i], coordinates[i + 1]};
                int[] rightBottomCoordinates = new int[]{coordinates[i + 2], coordinates[i + 3]};
                areasToDelete.add(new Area(leftTopCoordinates, rightBottomCoordinates));
            } catch (IllegalArgumentException e) {
                System.out.println("Exception " + e + " in method addArea");
            }
        }
        areas.removeAll(areasToDelete);
    }

    public void deleteContainedAreas(int[] leftTopCoordinates, int[] rightBottomCoordinates) {
        deleteArea(leftTopCoordinates, rightBottomCoordinates);
        Set<Area> areasToDelete = getContainedAreas(leftTopCoordinates, rightBottomCoordinates);
        areas.removeAll(areasToDelete);
    }

    public void deleteContainedAndCrossedAreas(int[] leftTopCoordinates, int[] rightBottomCoordinates) {
        deleteArea(leftTopCoordinates, rightBottomCoordinates);
        Set<Area> areasToDelete = getContainedAndCrossedAreas(leftTopCoordinates, rightBottomCoordinates);
        areas.removeAll(areasToDelete);
    }

    public TreeSet<Area> getAllAreas() {
        return areas;
    }

    public Set<Area> getContainedAndCrossedAreas(int[] leftTopCoordinates, int[] rightBottomCoordinates) {
        checkCorrectValues(leftTopCoordinates, rightBottomCoordinates);
        Area leftBoundaryArea = new Area(new int[]{0, 0}, new int[]{leftTopCoordinates[0] + 1, leftTopCoordinates[1] + 1});
        Area rightBoundaryArea = new Area(new int[]{rightBottomCoordinates[0] - 1, rightBottomCoordinates[1] - 1}, new int[]{n, m});
        Set<Area> ContainedAndCrossedAreas = areas.subSet(leftBoundaryArea, true, rightBoundaryArea, true);
        ContainedAndCrossedAreas.removeIf(area -> area.rightBottomCoordinates[0]<leftTopCoordinates[0] || area.rightBottomCoordinates[1]<leftTopCoordinates[1] ||
           area.leftTopCoordinates[0]>rightBottomCoordinates[0] || area.leftTopCoordinates[1]>rightBottomCoordinates[1]);
        return ContainedAndCrossedAreas;
    }

    public Set<Area> getContainedAreas(int[] leftTopCoordinates, int[] rightBottomCoordinates) {
        checkCorrectValues(leftTopCoordinates, rightBottomCoordinates);
        Area leftBoundaryArea = new Area(leftTopCoordinates, new int[]{leftTopCoordinates[0] + 1, leftTopCoordinates[1] + 1});
        Area rightBoundaryArea = new Area(new int[]{rightBottomCoordinates[0] - 1, rightBottomCoordinates[1] - 1}, rightBottomCoordinates);
        Set<Area> ContainedAreas = areas.subSet(areas.ceiling(leftBoundaryArea), true, areas.floor(rightBoundaryArea), true);
        ContainedAreas.removeIf(area -> area.leftTopCoordinates[1]<leftTopCoordinates[1] ||
                area.rightBottomCoordinates[0]>rightBottomCoordinates[0] || area.rightBottomCoordinates[1]>rightBottomCoordinates[1]);
        return ContainedAreas;
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

    public class Area implements Comparable<Area> {
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
            return leftTopCoordinates[0] > area.leftTopCoordinates[0]? 1: leftTopCoordinates[0] < area.leftTopCoordinates[0]? -1:
            leftTopCoordinates[1] > area.leftTopCoordinates[1]? 1: leftTopCoordinates[1] < area.leftTopCoordinates[1]? -1:
            rightBottomCoordinates[0] > area.rightBottomCoordinates[0]? 1: rightBottomCoordinates[0] < area.rightBottomCoordinates[0]? -1:
            rightBottomCoordinates[1] > area.rightBottomCoordinates[1]? 1: rightBottomCoordinates[0] > area.rightBottomCoordinates[0]? -1: 0;
        }

        @Override
        public String toString() {
            String result = "";
            for (int i = 0; i < leftTopCoordinates.length; i++) {
                result += leftTopCoordinates[i];
                if(i!=leftTopCoordinates.length - 1) {
                    result += ", ";
                }
            }
            result += ", ";
            for (int i = 0; i < rightBottomCoordinates.length; i++) {
                result += rightBottomCoordinates[i];
                if(i!=leftTopCoordinates.length - 1) {
                    result += ", ";
                }
            }
            return result;
        }
    }
}
