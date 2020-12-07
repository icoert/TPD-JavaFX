package save;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args) {
        try { new Server(987).start(); }
        catch(Throwable e) {
            e.printStackTrace();
        }
    }

    public static final long TIMEOUT = 1;//A minute.
    private static final byte[] RESPONSE_TIMEOUT = "{\"code\":429,\"text\":\"Too Many Requests\"".getBytes();

    private ServerSocket server;
    private Runner runner;
    private int port;

    public Server(int port) {
        if((this.port = port) < 0 || port > 0xFFFF)
            throw new IllegalArgumentException("Port value out of range: " + port);
    }

    public void start() throws IOException {
        if(server == null) {
            server = new ServerSocket();
            server.bind(new InetSocketAddress("localhost", port));
            runner = new Runner(server);
            runner.start();
            System.out.println("Server's starting");
        } else
            throw new IllegalArgumentException("Server's already started");
    }

    public void close() throws Exception {
        if(!server.isClosed()) {
            runner.close();
            server.close();
            server = null;
            System.out.println("Server has closed");
        } else
            throw new IllegalArgumentException("Server's already closed");
    }

    private static class Runner implements Runnable, AutoCloseable {
        private final Provider PROVIDER = new Provider(5);
        private final ServerSocket S;
        private boolean up;
        private Thread t;

        private Runner(ServerSocket server) { S = server; }

        public void run() {
            Provider.Connector c;
            for(;;) {
                try {
                    Socket client = S.accept();
                    if((c = PROVIDER.request(TIMEOUT)) == null) {
                        try(OutputStream out = client.getOutputStream()) {
                            out.write(RESPONSE_TIMEOUT);
                            out.flush();
                        }
                        continue;
                    }
                    c.start(client, PROVIDER);
                }
                catch(Throwable e) { break; }
                synchronized(PROVIDER) {
                    if(!up) break;
                }
            }
        }

        public void start() {
            synchronized(PROVIDER) {
                if(!up) {
                    up = true;
                    (t = new Thread(this)).start();
                }
            }
        }

        public void close() throws IOException {
            synchronized(PROVIDER) {
                if(up) {
                    PROVIDER.close();
                    t.interrupt();
                    t = null;
                    up = false;
                }
            }
        }
    }
}
