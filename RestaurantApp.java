import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.OutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class RestaurantApp extends JFrame {
    private CardLayout cardLayout;
    private JPanel cardPanel;
    private JLabel balanceLabel;
    private JTextField amountField;
    private DefaultListModel<MenuItem> cartModel;
    private DefaultListModel<String> staffModel;
    private JLabel cartTotalLabel;
    private JTextArea aiChatArea;
    private JTextField aiInputField;
    private double balance = 0.0;
    private List<MenuItem> menuItems;
    private List<Chef> chefs;
    private List<DeliveryPerson> deliveryPeople;
    private int warning_count = 0;
    private boolean isCustomer = false;

    public RestaurantApp() {
        setTitle("Restaurant Management System Demo");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 550);
        setLocationRelativeTo(null);

        menuItems = new ArrayList<>();
        chefs = new ArrayList<>();
        deliveryPeople = new ArrayList<>();
        cartModel = new DefaultListModel<>();
        staffModel = new DefaultListModel<>();

        initializeData();

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        JPanel homePanel = createHomePanel();
        JPanel menuPanel = createMenuPanel();
        JPanel cartPanel = createCartPanel();
        JPanel staffPanel = createStaffPanel();
        JPanel aiPanel = createAIChatPanel();
       // JPanel filePanel = createFilePanel();

        cardPanel.add(homePanel, "HOME");
        cardPanel.add(menuPanel, "MENU");
        cardPanel.add(cartPanel, "CART");
        cardPanel.add(staffPanel, "STAFF");
        cardPanel.add(aiPanel, "AI");
        //cardPanel.add(filePanel, "FILE");

        JPanel topBar = createTopBar();
        setLayout(new BorderLayout());
        add(topBar, BorderLayout.NORTH);
        add(cardPanel, BorderLayout.CENTER);
        setVisible(true);
    }

    private void initializeData() {
        menuItems.add(new MenuItem("Margherita Pizza", 12.99));
        menuItems.add(new MenuItem("Spaghetti Bolognese", 14.49));
        menuItems.add(new MenuItem("Caesar Salad", 9.99));
        menuItems.add(new MenuItem("Grilled Salmon", 18.99));
        menuItems.add(new MenuItem("Cheeseburger", 11.49));

        chefs.add(new Chef("Chef 1"));
        chefs.add(new Chef("Chef 2"));
        chefs.add(new Chef("Chef 3"));

        deliveryPeople.add(new DeliveryPerson("Del 1"));
        deliveryPeople.add(new DeliveryPerson("Del 2"));
        deliveryPeople.add(new DeliveryPerson("Del 3"));
    }

    private JPanel createTopBar() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBorder(new EmptyBorder(5, 5, 5, 5));

        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));

        JButton homeButton = new JButton("Home");
        homeButton.addActionListener(e -> cardLayout.show(cardPanel, "HOME"));

        JButton menuButton = new JButton("Menu");
        menuButton.addActionListener(e -> cardLayout.show(cardPanel, "MENU"));

        JButton cartButton = new JButton("Cart");
        cartButton.addActionListener(e -> cardLayout.show(cardPanel, "CART"));

        JButton staffButton = new JButton("Staff");
        staffButton.addActionListener(e -> cardLayout.show(cardPanel, "STAFF"));

        JButton aiButton = new JButton("Ask AI");
        aiButton.addActionListener(e -> cardLayout.show(cardPanel, "AI"));

        JButton fileButton = new JButton("File");
        //fileButton.addActionListener(e -> cardLayout.show(cardPanel, "FILE"));
        
        JButton registrationButton = new JButton("Registration");
        registrationButton.addActionListener(this::customerRegistration);

        navPanel.add(homeButton);
        navPanel.add(menuButton);
        navPanel.add(cartButton);
        navPanel.add(staffButton);
        navPanel.add(aiButton);
        navPanel.add(fileButton);
        navPanel.add(registrationButton);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        balanceLabel = new JLabel("Balance: $" + balance);
        JLabel amountLabel = new JLabel("Amount:");
        amountField = new JTextField(8);
        JButton depositButton = new JButton("Deposit");
        depositButton.addActionListener(this::handleDeposit);
        rightPanel.add(balanceLabel);
        rightPanel.add(amountLabel);
        rightPanel.add(amountField);
        rightPanel.add(depositButton);

        JLabel warningLabel = new JLabel("Warning: " + warning_count);
        rightPanel.add(warningLabel);

        topBar.add(navPanel, BorderLayout.WEST);
        topBar.add(rightPanel, BorderLayout.EAST);
        return topBar;
    }

    private JPanel createHomePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Restaurant Management System Demo", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));

        JTextArea welcomeText = new JTextArea();
        welcomeText.setEditable(false);
        welcomeText.setFont(new Font("SansSerif", Font.PLAIN, 14));
        welcomeText.setLineWrap(true);
        welcomeText.setWrapStyleWord(true);
        welcomeText.setText(
                "Welcome to the Restaurant Management System Demo.\n\n" +
                        "Use the navigation bar above to:\n" +
                        "• Browse the menu and add dishes to your cart.\n" +
                        "• View current staff (chefs and delivery workers).\n" +
                        "• Manage your balance and place orders.\n" +
                        "• Ask the AI questions about the restaurant.\n" + 
                        "• You can file a complaint/compliment toward chef or delivery person.\n" +
                        "• You have to be a registered customer to deposit balance into the system and place order."
        );

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(welcomeText, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createMenuPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel menuLabel = new JLabel("Menu");
        menuLabel.setFont(new Font("SansSerif", Font.BOLD, 20));

        DefaultListModel<MenuItem> menuModel = new DefaultListModel<>();
        for (MenuItem item : menuItems) {
            menuModel.addElement(item);
        }
        JList<MenuItem> menuList = new JList<>(menuModel);
        menuList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        menuList.setFont(new Font("Monospaced", Font.PLAIN, 14));

        JScrollPane scrollPane = new JScrollPane(menuList);

        JButton addButton = new JButton("Add to Cart");
        addButton.addActionListener(e -> {
            MenuItem selected = menuList.getSelectedValue();
            if (selected != null) {
                cartModel.addElement(selected);
                updateCartTotal();
            } else {
                JOptionPane.showMessageDialog(this, "Please select a menu item to add.");
            }
        });

        panel.add(menuLabel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(addButton, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createCartPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel cartLabel = new JLabel("Cart");
        cartLabel.setFont(new Font("SansSerif", Font.BOLD, 20));

        JList<MenuItem> cartList = new JList<>(cartModel);
        cartList.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(cartList);

        cartTotalLabel = new JLabel("Total: $0.00");
        JButton removeButton = new JButton("Remove Selected");
        JButton placeOrderButton = new JButton("Place Order");

        removeButton.addActionListener(e -> {
            int selectedIndex = cartList.getSelectedIndex();
            if (selectedIndex != -1) {
                cartModel.remove(selectedIndex);
                updateCartTotal();
            } else {
                JOptionPane.showMessageDialog(this, "Please select an item to remove.");
            }
        });

        placeOrderButton.addActionListener(e -> handlePlaceOrder());

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));

        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        totalPanel.add(cartTotalLabel);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(removeButton);
        buttonPanel.add(placeOrderButton);

        bottomPanel.add(totalPanel);
        bottomPanel.add(buttonPanel);

        panel.add(cartLabel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createStaffPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel staffLabel = new JLabel("Staff");
        staffLabel.setFont(new Font("SansSerif", Font.BOLD, 20));

        JList<String> staffList = new JList<>(staffModel);
        staffList.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(staffList);

        JButton refreshButton = new JButton("Refresh Staff Info");
        refreshButton.addActionListener(e -> refreshStaffModel());

        panel.add(staffLabel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(refreshButton, BorderLayout.SOUTH);

        refreshStaffModel();
        return panel;
    }

    private JPanel createAIChatPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel aiLabel = new JLabel("AI Assistant");
        aiLabel.setFont(new Font("SansSerif", Font.BOLD, 20));

        aiChatArea = new JTextArea();
        aiChatArea.setEditable(false);
        aiChatArea.setLineWrap(true);
        aiChatArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(aiChatArea);

        aiInputField = new JTextField();
        JButton sendButton = new JButton("Send");
        sendButton.addActionListener(e -> sendAIMessage());

        JPanel inputPanel = new JPanel(new BorderLayout(5, 0));
        inputPanel.add(aiInputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        panel.add(aiLabel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(inputPanel, BorderLayout.SOUTH);
        return panel;
    }

    private void refreshStaffModel() {
        staffModel.clear();
        for (Chef c : chefs) {
            staffModel.addElement("Chef: " + c.getName() + " | Demoted: " + c.getdemotedCount()
                    + " | Status: " + (c.isSuspended() ? "Suspended" : "Active"));
        }
        for (DeliveryPerson d : deliveryPeople) {
            staffModel.addElement("Delivery: " + d.getName() + " | Demoted: " + d.getdemotedCount()
                    + " | Status: " + (d.isSuspended() ? "Suspended" : "Active"));
        }
    }
    private void customerRegistration(ActionEvent e) {
        if (isCustomer){
            JOptionPane.showMessageDialog(this, "You already be an registered customer.");
            return;
        }
        else {
            isCustomer = true;
            JOptionPane.showMessageDialog(this, "You have registered as a customer");
            return;
        }
    }

    private void handleDeposit(ActionEvent e) {
        String text = amountField.getText().trim();
        if (text.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter an amount to deposit.");
            return;
        }
        try {
            double amount = Double.parseDouble(text);
            if (amount <= 0) {
                JOptionPane.showMessageDialog(this, "Please enter a positive amount.");
                return;
            }
            balance += amount;
            updateBalanceLabel();
            amountField.setText("");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid amount. Please enter a numeric value.");
        }
    }

    private void handlePlaceOrder() {
        if (cartModel.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Your cart is empty.");
            return;
        }

        if(!isCustomer){
            JOptionPane.showMessageDialog(this, "You have to be a registered customer to do following action.");
            return;
        }

        double total = 0.0;
        for (int i = 0; i < cartModel.size(); i++) {
            total += cartModel.get(i).getPrice();
        }

        if (total > balance) {
            warning_count += 1;
            JOptionPane.showMessageDialog(this, "Insufficient balance. Please deposit more funds.");
            return;
        }

        List<Chef> availableChefs = new ArrayList<>();
        for (Chef c : chefs) {
            if (!c.isSuspended()) {
                availableChefs.add(c);
            }
        }

        List<DeliveryPerson> availableDelivery = new ArrayList<>();
        for (DeliveryPerson d : deliveryPeople) {
            if (!d.isSuspended()) {
                availableDelivery.add(d);
            }
        }

        if (availableChefs.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No available chefs. Unable to place order.");
            return;
        }
        if (availableDelivery.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No available delivery workers. Unable to place order.");
            return;
        }

        Chef selectedChef = availableChefs.get(0);
        DeliveryPerson selectedDelivery = availableDelivery.get(0);

        balance -= total;
        updateBalanceLabel();
        JOptionPane.showMessageDialog(this, "Order placed successfully! Total: $" + String.format("%.2f", total));
        for (int i = cartModel.size() - 1; i >= 0; i--) {
            cartModel.remove(i);
        }
        updateCartTotal();
        rateStaffAfterOrder(selectedChef, selectedDelivery);
    }

    private void rateStaffAfterOrder(Chef chef, DeliveryPerson delivery) {
        String[] ratingOptions = {"1", "2", "3", "4", "5"};

        String chefRatingStr = (String) JOptionPane.showInputDialog(
                this,
                "Please rate the Chef (1-5):",
                "Rate Chef",
                JOptionPane.PLAIN_MESSAGE,
                null,
                ratingOptions,
                "5"
        );
        if (chefRatingStr != null) {
            int chefRating = Integer.parseInt(chefRatingStr);
            chef.addRating(chefRating);
        }

        String deliveryRatingStr = (String) JOptionPane.showInputDialog(
                this,
                "Please rate the Delivery Person (1-5):",
                "Rate Delivery",
                JOptionPane.PLAIN_MESSAGE,
                null,
                ratingOptions,
                "5"
        );
        if (deliveryRatingStr != null) {
            int deliveryRating = Integer.parseInt(deliveryRatingStr);
            delivery.addRating(deliveryRating);
        }

        refreshStaffModel();
    }

    private void sendAIMessage() {
        String userMessage = aiInputField.getText().trim();
        if (userMessage.isEmpty()) {
            return;
        }

        aiChatArea.append("You: " + userMessage + "\n");
        aiInputField.setText("");

        new Thread(() -> {
            try {
                String response = callOllamaAPI(userMessage);
                SwingUtilities.invokeLater(() -> {
                    aiChatArea.append("AI: " + response + "\n\n");
                });
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> {
                    aiChatArea.append("AI: Sorry, I couldn't connect to the AI service.\n\n");
                });
            }
        }).start();
    }

    private String callOllamaAPI(String prompt) throws Exception {
        String model = "qwen3:1.7b"; // Your preference model
        String apiUrl = "http://localhost:11434/api/generate";
        String escapedPrompt = prompt.replace("\\", "\\\\");
        escapedPrompt = escapedPrompt.replace("\"", "\\\"");
        escapedPrompt = escapedPrompt.replace("\n", "\\n");
        escapedPrompt = escapedPrompt.replace("\r", "\\r");
        String payload = "{"
                + "\"model\": \"" + model + "\","
                + "\"prompt\": \"" + escapedPrompt + "\","
                + "\"stream\": false"
                + "}";

        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = payload.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        int statusCode = connection.getResponseCode();
        BufferedReader reader;
        if (statusCode >= 200 && statusCode < 300) {
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
        } else {
            reader = new BufferedReader(new InputStreamReader(connection.getErrorStream(), "utf-8"));
        }

        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line.trim());
        }
        reader.close();

        String json = response.toString();
        String key = "\"response\":\"";
        int idx = json.indexOf(key);
        if (idx == -1) {
            return "I couldn't parse the AI response.";
        }
        idx += key.length();
        int endIdx = json.indexOf("\"", idx);
        if (endIdx == -1) {
            return "I couldn't parse the AI response.";
        }
        String respText = json.substring(idx, endIdx);
        respText = respText.replace("\\n", "\n");
        respText = respText.replace("\\r", "\r");
        respText = respText.replace("\\\"", "\"");
        respText = respText.replace("\\\\", "\\");
        return respText;
    }

    private void updateCartTotal() {
        double total = 0.0;
        for (int i = 0; i < cartModel.size(); i++) {
            total += cartModel.get(i).getPrice();
        }
        cartTotalLabel.setText("Total: $" + String.format("%.2f", total));
    }

    private void updateBalanceLabel() {
        balanceLabel.setText("Balance: $" + String.format("%.2f", balance));
    }

    private static class MenuItem {
        private final String name;
        private final double price;

        MenuItem(String name, double price) {
            this.name = name;
            this.price = price;
        }

        String getName() {
            return name;
        }

        double getPrice() {
            return price;
        }

        @Override
        public String toString() {
            return String.format("%-20s $%.2f", name, price);
        }
    }

    private static class Chef {
        private final String name;
        private int ratingSum = 0;
        private int ratingCount = 0;
        private int demotedCount = 0;
        private boolean suspended = false;

        Chef(String name) {
            this.name = name;
        }

        String getName() {
            return name;
        }

        void addRating(int rating) {
            ratingSum += rating;
            ratingCount += 1;
            if (rating <= 2) {
                demotedCount += 1;
                if (demotedCount >= 2) {
                    suspended = true;
                }
            }
        }

        int getdemotedCount() {
            return demotedCount;
        }

        boolean isSuspended() {
            return suspended;
        }

        double getAverageRating() {
            if (ratingCount == 0) return 0.0;
            return (double) ratingSum / ratingCount;
        }
    }

    private static class DeliveryPerson {
        private final String name;
        private int ratingSum = 0;
        private int ratingCount = 0;
        private int demotedCount = 0;
        private boolean suspended = false;

        DeliveryPerson(String name) {
            this.name = name;
        }

        String getName() {
            return name;
        }

        void addRating(int rating) {
            ratingSum += rating;
            ratingCount += 1;
            if (rating <= 2) {
                demotedCount += 1;
                if (demotedCount >= 2) {
                    suspended = true;
                }
            }
        }

        int getdemotedCount() {
            return demotedCount;
        }

        boolean isSuspended() {
            return suspended;
        }

        double getAverageRating() {
            if (ratingCount == 0) return 0.0;
            return (double) ratingSum / ratingCount;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(RestaurantApp::new);
    }
}