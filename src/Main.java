import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Objects;

public class Main {
    public static Core core = new Core();
    static int WIDTH = 300;
    static int HEIGHT = 300;

    public static void main(String[] args) {
        directorySetUp();
        JFrame frame = new JFrame("GiveMe");
        try {
            frame.setIconImage(ImageIO.read(Objects.requireNonNull(Main.class.getResource("icon.jpg"))));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        frame.setSize(WIDTH, HEIGHT);
        frame.setLocation(100, 100);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        MyPanel panel = new MyPanel(frame);
        JTextField host = new JTextField("localhost", 30);
        JTextField port = new JTextField("8000", 30);
        JLabel label = new JLabel("Код в буфере обмена");
        label.setBounds(10, 80, 200, 30);
        host.setBounds(10, 10, 200, 30);
        port.setBounds(10, 50, 200, 30);
        Button client = new Button("client");
        Button server = new Button("server");
        Button send = new Button("Отправить");
        send.setBounds(WIDTH/2 - 40, HEIGHT/2 - 20 - 37, 80, 40);
        Button ok = new Button("OK");
        ok.setBounds(WIDTH / 2 - 20, HEIGHT / 2 - 20, 40, 40);
        send.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                int res = chooser.showOpenDialog(frame);
                if (res == JFileChooser.APPROVE_OPTION){
                    String path = chooser.getSelectedFile().getAbsolutePath();
                    String name = chooser.getSelectedFile().getName();
                    if (core.status == Core.RUNNING){
                        ArrayList<String> paths = new ArrayList<>();
                        ArrayList<String> names = new ArrayList<>();
                        paths.add(path);
                        names.add(name);
                        core.sendFile(paths, names);
                    }
                }
            }
        });
        ok.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (core.status == Core.CLIENT_SET_UP) {
                    panel.removeAll();
                    String text = host.getText();
                    String host = "localhost";
                    if (!text.equals("localhost")) {
                        host = decrypt(text);
                    }
                    panel.removeAll();
                    panel.add(send);
                    panel.repaint();
                    String finalHost = host;
                    new Thread(() -> {
                        core.initClient(finalHost, Integer.parseInt(port.getText()));
                    }).start();
                } else {
                    panel.removeAll();
                    panel.add(send);
                    new Thread(() -> {
                        core.initServer(Integer.parseInt(port.getText()));
                    }).start();
                }
                core.status = Core.RUNNING;
            }
        });
        panel.add(server);
        panel.add(client);
        frame.add(panel);
        client.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                panel.remove(client);
                panel.remove(server);
                panel.add(host);
                panel.add(port);
                panel.add(ok);
                core.status = Core.CLIENT_SET_UP;
            }
        });
        server.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                panel.remove(client);
                panel.remove(server);
                panel.add(port);
                panel.add(ok);
                panel.add(label);
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                String ip = encrypt(getIP());
                StringSelection stringSelection = new StringSelection(ip);
                clipboard.setContents(stringSelection, null);
                core.status = Core.SERVER_SET_UP;
            }
        });
        frame.setVisible(true);
        new Thread(() -> {
            while (true){
                frame.repaint();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    private static void directorySetUp(){
        Path path = Path.of("C:\\Users\\User\\GiveMe");
        if (!Files.exists(path)){
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static String getIP() {
        String str = null;
        try {
            str = InetAddress.getLocalHost().toString();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return str.substring(str.indexOf('/') + 1);
    }

    private static String decrypt(String text) {
        String ans = "";
        for (int i = 0; i < text.length(); i++) {
            int move;
            if (i % 2 == 0) {
                move = -50;
            } else {
                move = -30;
            }
            ans += (char) ((int) text.charAt(i) + move);
        }
        return ans;
    }

    private static String encrypt(String text) {
        String ans = "";
        for (int i = 0; i < text.length(); i++) {
            int move;
            if (i % 2 == 0) {
                move = 50;
            } else {
                move = 30;
            }
            ans += (char) ((int) text.charAt(i) + move);
        }
        return ans;
    }
}