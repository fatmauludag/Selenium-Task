package com.testium.task;

public class Test {
    public static void main(String[] args) {
        try {
            PageTester.ExecuteAll();
            System.out.println( "All went well :)" );
        } catch (Exception e) {
            System.out.println( "There are some errors !");
            System.out.println( e.getMessage());
            e.printStackTrace();
        } finally {
            PageTester.Cancel();
        }
    }
}
