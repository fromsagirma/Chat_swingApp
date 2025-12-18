import java.awt.*;
import java.io.*;
import java.net.*;
import javax.swing.*;

public class ChatClient1 extends JFrame {

    private JPanel chatPanel;
    private JScrollPane scrollPane;
    private JTextField inputField;
    private PrintWriter writer;

    public ChatClient1(String title) {
        setTitle(title);
        setSize(400, 550);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Header
        JLabel header = new JLabel(title, SwingConstants.CENTER);
        header.setOpaque(true);
        header.setBackground(new Color(0, 120, 255));
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Arial", Font.BOLD, 20));
        add(header, BorderLayout.NORTH);

        // Chat Panel
        chatPanel = new JPanel();
        chatPanel.setLayout(new BoxLayout(chatPanel, BoxLayout.Y_AXIS));
        chatPanel.setBackground(Color.WHITE);

        scrollPane = new JScrollPane(chatPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        add(scrollPane, BorderLayout.CENTER);

        // Bottom Input
        JPanel bottom = new JPanel(new BorderLayout());
        inputField = new JTextField();
        JButton sendBtn = new JButton("Send");

        sendBtn.setBackground(new Color(0, 120, 255));
        sendBtn.setForeground(Color.WHITE);

        bottom.add(inputField, BorderLayout.CENTER);
        bottom.add(sendBtn, BorderLayout.EAST);
        add(bottom, BorderLayout.SOUTH);

        sendBtn.addActionListener(e -> sendMessage());
        inputField.addActionListener(e -> sendMessage());

        connectToServer();
        setVisible(true);
    }

    private void connectToServer() {
        try {
            Socket socket = new Socket("localhost", 5000);
            writer = new PrintWriter(socket.getOutputStream(), true);

            new Thread(() -> {
                try {
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(socket.getInputStream())
                    );
                    String msg;
                    while ((msg = reader.readLine()) != null) {
                        addMessage(msg, false); // RECEIVED
                    }
                } catch (Exception ignored) {}
            }).start();

        } catch (Exception e) {
            addMessage("Cannot connect to server.", false);
        }
    }

    private void sendMessage() {
        String msg = inputField.getText().trim();
        if (msg.isEmpty()) return;

        writer.println(msg);
        addMessage(msg, true); // SENT
        inputField.setText("");
    }

   private void addMessage(String msg, boolean isSender) {
        JPanel wrapper = new JPanel(new FlowLayout(isSender ? FlowLayout.RIGHT : FlowLayout.LEFT));
        wrapper.setOpaque(false);

        JLabel bubble = new JLabel("<html><body style='width: 200px'>" + msg + "</body></html>");
        bubble.setFont(new Font("Arial", Font.PLAIN, 15));
        bubble.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        bubble.setOpaque(true);

        // Fixed bubble size
        bubble.setMaximumSize(new Dimension(300, Integer.MAX_VALUE));
        bubble.setPreferredSize(new Dimension(250, bubble.getPreferredSize().height));

        if (isSender) {
            // Sender bubble: BLUE + WHITE text
            bubble.setBackground(new Color(0, 120, 255));
            bubble.setForeground(Color.WHITE);
        } else {
            // Receiver bubble: GRAY + BLACK text
            bubble.setBackground(new Color(230, 230, 230));
            bubble.setForeground(Color.BLACK);
        }

        wrapper.add(bubble);
        chatPanel.add(wrapper);
        chatPanel.add(Box.createVerticalStrut(4)); // spacing between messages

        chatPanel.revalidate();
        chatPanel.repaint();

        // Auto-scroll to bottom
        SwingUtilities.invokeLater(() -> scrollPane.getVerticalScrollBar()
                .setValue(scrollPane.getVerticalScrollBar().getMaximum()));
    }

    public static void main(String[] args) {
        new ChatClient1("Client 1");
    }
}