package com.example.java;

import java.util.Set;

public class Main {

    public static void main(String[] args) {
	// write your code here
        Field field = new Field(100, 100);
        field.addAreas(10, 20, 30, 30, 5, 5, 20, 25);
        int[] leftTop = new int[]{8, 15}, rightBottom = new int[]{40, 40};
        Set<Field.Area> containedAndCrossedArea = field.getContainedAndCrossedAreas(leftTop, rightBottom);
        Set<Field.Area> containedArea = field.getContainedAreas(leftTop, rightBottom);
        System.out.println(containedAndCrossedArea);
        System.out.println(containedArea);
        field.deleteContainedAndCrossedAreas(leftTop, rightBottom);
        System.out.println("Debug");
    }
}
