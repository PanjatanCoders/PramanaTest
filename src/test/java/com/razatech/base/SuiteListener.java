package com.razatech.base;

import org.testng.ISuite;
import org.testng.ISuiteListener;

public class SuiteListener implements ISuiteListener {

    @Override
    public void onStart(ISuite suite) {
        System.out.println("========================================");
        System.out.println("Suite started: " + suite.getName());
        System.out.println("========================================");
    }

    @Override
    public void onFinish(ISuite suite) {
        System.out.println("========================================");
        System.out.println("Suite finished: " + suite.getName());
        System.out.println("========================================");
    }
}