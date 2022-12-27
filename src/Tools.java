
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class Tools {
    public static void sendNotification(String name){
        try{
            SystemTray tray = SystemTray.getSystemTray();
            Image image = ImageIO.read(Objects.requireNonNull(Main.class.getResource("icon.jpg")));
            TrayIcon trayIcon = new TrayIcon(image, "Java AWT Tray Demo");
            trayIcon.setImageAutoSize(true);
            trayIcon.setToolTip("System tray icon demo");
            trayIcon.addActionListener(e -> {
                try {
                    String s = "C:\\Users\\User\\GiveMe\\" + name;
                    Runtime.getRuntime().exec("explorer.exe /select," + s);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            });
            tray.add(trayIcon);
            trayIcon.displayMessage("Файл " + name + " добавлен", "Смотри в GiveMe", TrayIcon.MessageType.INFO);
        }catch(Exception ex){
            System.err.print(ex);
        }
    }
    public static void writeInt(OutputStream out, int x){
        try {
            out.write(ByteBuffer.allocate(4).putInt(x).array());
            out.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static int readInt(InputStream in){
        try {
            byte[] a = in.readNBytes(4);
            return ByteBuffer.wrap(a).getInt();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static void writeString(OutputStream out, String s){
        byte[] a = s.getBytes(StandardCharsets.UTF_8);
        writeInt(out, a.length);
        try {
            out.write(a);
            out.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
    public static String readString(InputStream in){
        try {
            int l = readInt(in);
            byte[] a = in.readNBytes(l);
            return new String(a, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void writeLong(OutputStream out, long x){
        try {
            out.write(ByteBuffer.allocate(8).putLong(x).array());
            out.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static long readLong(InputStream in){
        try {
            byte[] a = in.readNBytes(8);
            return ByteBuffer.wrap(a).getLong();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
