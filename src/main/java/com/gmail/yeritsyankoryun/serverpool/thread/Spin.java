package com.gmail.yeritsyankoryun.serverpool.thread;

import com.gmail.yeritsyankoryun.serverpool.model.ServerPool;
import com.gmail.yeritsyankoryun.serverpool.model.Server;
import com.gmail.yeritsyankoryun.serverpool.repository.Cloud;

public class Spin extends Thread {
    private Server request;
    private Cloud pool;

    public Spin(Server request, Cloud pool) {
        this.request = request;
        this.pool = pool;
    }

    @Override
    public void run() {
        ServerPool post = new ServerPool();
        post.setAllocatedSize(request.getSize());
        post.getServerRequests().add(request);
        try {
            sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        post.setActive(true);
        pool.save(post);
    }
}
