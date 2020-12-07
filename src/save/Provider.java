package save;

import java.io.IOException;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

class Provider implements java.io.Closeable {
    @Override
    public void close() throws IOException {

    }

    public class Connector implements Runnable, AutoCloseable {
        private final AtomicBoolean CLOSED = new AtomicBoolean();
        private final Connection C;
        private Provider holder;
        private Socket client;
        private boolean active;
        Connector()  throws Throwable {
            C = DriverManager.getConnection("jdbc:sqlserver://;server_name=DESKTOP-VDVDFS2;databaseName=RentC;integratedSecurity=true");
        }

        public void start(Socket client, Provider holder) {
            if(holder == null)
                throw new UnsupportedOperationException("Required the owner of this connector");
            synchronized(C) {
                if(active)
                    throw new UnsupportedOperationException("This connector has started");
                if(client == null || client.isClosed())
                    throw new IllegalArgumentException("This client has expired");
                this.client = client;
                this.holder = holder;
                C.notify();
                active = !active;
            }
        }

        @Override
        public void close() throws Exception {

        }

        @Override
        public void run() {

        }
    }

    private final BlockingQueue<Connector> RUNNERS;

    public Provider(int capacity) {
        RUNNERS = new ArrayBlockingQueue<>(capacity);
        for(int i = -1; ++i < capacity; ) {
            String name = Connector.class.getSimpleName() + "-" + i;
            Connector connector;
            try {
                connector = new Connector();
            } catch(Throwable e) {
                System.out.format("Creation of connector %s has failed (%s)%n", name, e.getMessage());
                continue;
            }
            RUNNERS.add(connector);
            new Thread(connector).start();
        }
        if(RUNNERS.size() < 1)
            throw new UnsupportedOperationException("No Connector available at all");
    }
    /**
     * @param timeout Time runs out in seconds.
     * @return Instance of Connector or {@code null} if none is available.
     */
    public Connector request(long timeout) {
        try { return RUNNERS.poll(timeout, TimeUnit.SECONDS); }
        catch(Throwable e) {
            return null;
        }
    }
}