import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;

public class Core {
    private static final int DEL = 2000000000;
    public static final byte CLIENT_SERVER = 0;
    public static final byte CLIENT_SET_UP = 1;
    public static final byte SERVER_SET_UP = 2;
    public static final byte RUNNING = 3;
    public static final byte SERVER_SEARCHING = 4;
    private Socket s = null;
    public byte status;

    public Core() {
        status = CLIENT_SERVER;
    }

    public void sendFile(ArrayList<String> paths, ArrayList<String> names) {
        try {
            Tools.writeInt(s.getOutputStream(), names.size());
            for (int i = 0; i < paths.size(); i++) {
                Tools.writeString(s.getOutputStream(), names.get(i));
                FileInputStream fin = new FileInputStream(paths.get(i));
                long size = new File(paths.get(i)).length();
                Tools.writeLong(s.getOutputStream(), size);
                if (size > (long) DEL * DEL) {
                    continue;
                }
                int packs = (int) (size / DEL);
                for (int j = 0; j < packs; j++) {
                    s.getOutputStream().write(fin.readNBytes(DEL));
                    s.getOutputStream().flush();
                }
                s.getOutputStream().write(fin.readNBytes((int) (size % DEL)));
                s.getOutputStream().flush();
                fin = null;
            }
            System.gc();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void run() {
        while (true) {
            try {
                if (s.getInputStream().available() > 0) {
                    int fileAmount = Tools.readInt(s.getInputStream());
                    for (int i = 0; i < fileAmount; i++) {
                        String name = Tools.readString(s.getInputStream());
                        String path = "C:\\Users\\User\\GiveMe" + "\\" + name;
                        File file = new File(path);
                        FileOutputStream fos;
                        {
                            if (!file.exists()) {
                                fos = new FileOutputStream(file);
                            } else {
                                Random r = new Random();
                                String rus = "абвгдеёжзийклмнопрстуфхцчъыьэюя";
                                String eng = "abcdefghijklmnopqrstuvwxyz";
                                String dig = "0123456789";
                                String sum = rus + rus.toUpperCase() +
                                        eng + eng.toUpperCase() +
                                        dig;
                                String tempName;
                                String tempPath;
                                boolean b = false;
                                while (!b) {
                                    char c = sum.charAt(r.nextInt(sum.length()));
                                    tempName = c + "" + name;
                                    tempPath = "C:\\Users\\User\\GiveMe" + "\\" + tempName;
                                    File tempFile = new File(tempPath);
                                    if (!tempFile.exists()) {
                                        path = tempPath;
                                        name = tempName;
                                        b = true;
                                    }
                                }
                                File finalFile = new File(path);
                                fos = new FileOutputStream(finalFile);
                            }
                        }
                        long size = Tools.readLong(s.getInputStream());
                        int packs = (int) (size / DEL);
                        for (int j = 0; j < packs; j++) {
                            fos.write(s.getInputStream().readNBytes(DEL));
                            fos.flush();
                        }
                        fos.write(s.getInputStream().readNBytes((int) (size % DEL)));
                        fos.flush();
                        fos.close();
                        fos = null;
                        file = null;
                        System.gc();
                        Tools.sendNotification(name);
                    }
                }
                Thread.sleep(100);
                System.gc();
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void initServer(int port) {
        try {
            ServerSocket serverSocket;
            serverSocket = new ServerSocket(port);
            status = SERVER_SEARCHING;
            s = serverSocket.accept();
            status = RUNNING;
            run();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void initClient(String host, int port) {
        try {
            s = new Socket(host, port);
            while (!s.isConnected()) ;
            s.setTcpNoDelay(true);
            status = RUNNING;
            run();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}