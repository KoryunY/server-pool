package com.gmail.yeritsyankoryun.serverpool.thread;

import static java.lang.Thread.sleep;

public class Spin implements Runnable{

    @Override
    public void run() {
        try {
            sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
