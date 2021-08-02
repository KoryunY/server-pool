package com.gmail.yeritsyankoryun.serverpool.thread;

import com.gmail.yeritsyankoryun.serverpool.model.ApplicationModel;
import com.gmail.yeritsyankoryun.serverpool.model.ServerModel;
import com.gmail.yeritsyankoryun.serverpool.repository.ApplicationRepository;
import com.gmail.yeritsyankoryun.serverpool.repository.ServerRepository;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static java.lang.Thread.sleep;

public class Wait implements Runnable {
    private final int id;
    private final ConcurrentHashMap<Integer, Future<ServerModel>> spinningServers;

    public Wait(int id, ConcurrentHashMap<Integer, Future<ServerModel>> spinningServers) {
        this.id = id;
        this.spinningServers = spinningServers;
    }

    @Override
    public void run() {
        Future<ServerModel> future = spinningServers.get(id);
        while (!future.isDone()) {
            try {
                sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //spinningServers.remove(id);
    }
}
