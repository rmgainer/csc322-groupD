import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
    private double balance = 0.00;

    private DefaultListModel<MenuItem> menuModel = new DefaultListModel<>();
    private DefaultListModel<MenuItem> cartModel = new DefaultListModel<>();
    private JLabel cartTotalLabel;

    private JTextArea aiConversationArea;
    private JTextField aiInputField;

    private Chef loggedInChef;
    private JLabel chefLoginStatusLabel;

    private final List<MenuItem> menuItems = new ArrayList<>();
    private final List<Chef> chefs = new ArrayList<>();
    private final List<DeliveryPerson> deliveryPeople = new ArrayList<>();

    private DefaultListModel<String> chefStatusModel = new DefaultListModel<>();
    private DefaultListModel<String> deliveryStatusModel = new DefaultListModel<>();

    public RestaurantApp() {
        setTitle("Restaurant Management System Demo");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        initializeData();

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        JPanel homePanel = createHomePanel();
        JPanel menuPanel = createMenuPanel();
        JPanel cartPanel = createCartPanel();
        JPanel staffPanel = createStaffPanel();
        JPanel chefPanel = createChefLoginPanel();
        JPanel aiPanel = createAIChatPanel();
        cardPanel.add(homePanel, "HOME");
        cardPanel.add(menuPanel, "MENU");
        cardPanel.add(cartPanel, "CART");
        cardPanel.add(staffPanel, "STAFF");
        cardPanel.add(chefPanel, "CHEF");
        cardPanel.add(aiPanel, "AI");

        JPanel topBar = createTopBar();
        setLayout(new BorderLayout());
        add(topBar, BorderLayout.NORTH);
        add(cardPanel, BorderLayout.CENTER);
        setVisible(true);
    }

    private void initializeData() {
        chefs.add(new Chef("Chef 1", "chef1", "password1"));
        chefs.add(new Chef("Chef 2", "chef2", "password2"));
        chefs.add(new Chef("Chef 3", "chef3", "password3"));

        for (Chef chef : chefs) {
            chef.decideMenu(menuItems);
        }

        deliveryPeople.add(new DeliveryPerson("Del 1"));
        deliveryPeople.add(new DeliveryPerson("Del 2"));
        deliveryPeople.add(new DeliveryPerson("Del 3"));
    }

    private JPanel createTopBar() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBorder(new EmptyBorder(5, 5, 5, 5));

        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        JButton homeButton = new JButton("Home");
        JButton menuButton = new JButton("Menu");
        JButton cartButton = new JButton("Cart");
        JButton staffButton = new JButton("Staff");
        JButton chefButton = new JButton("Chef Login");
        JButton aiButton = new JButton("AI Assistant");

        homeButton.addActionListener(e -> cardLayout.show(cardPanel, "HOME"));
        menuButton.addActionListener(e -> cardLayout.show(cardPanel, "MENU"));
        cartButton.addActionListener(e -> cardLayout.show(cardPanel, "CART"));
        staffButton.addActionListener(e -> cardLayout.show(cardPanel, "STAFF"));
        chefButton.addActionListener(e -> cardLayout.show(cardPanel, "CHEF"));
        aiButton.addActionListener(e -> cardLayout.show(cardPanel, "AI"));

        navPanel.add(homeButton);
        navPanel.add(menuButton);
        navPanel.add(cartButton);
        navPanel.add(staffButton);
        navPanel.add(chefButton);
        navPanel.add(aiButton);

        JPanel balancePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        balanceLabel = new JLabel("Balance: $0.00");
        JButton depositButton = new JButton("Deposit $20");
        depositButton.addActionListener(e -> {
            balance += 20.0;
            updateBalanceLabel();
        });

        balancePanel.add(balanceLabel);
        balancePanel.add(depositButton);

        topBar.add(navPanel, BorderLayout.WEST);
        topBar.add(balancePanel, BorderLayout.EAST);

        return topBar;
    }

    private JPanel createHomePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Restaurant Management System Demo", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 26));

        JTextArea intro = new JTextArea(
                "Welcome to the Restaurant Management System Demo.\n\n"
                        + "Use the navigation bar above to:\n"
                        + "- Browse the menu and add items to your cart.\n"
                        + "- View your cart and place orders.\n"
                        + "- See staff information, performance and status.\n"
                        + "- Chat with the AI Assistant powered by Ollama.\n"
                        + "- Manage your balance (for this demo, money is virtual)."
        );
        intro.setEditable(false);
        intro.setFont(new Font("SansSerif", Font.PLAIN, 14));
        intro.setLineWrap(true);
        intro.setWrapStyleWord(true);

        panel.add(title, BorderLayout.NORTH);
        panel.add(new JScrollPane(intro), BorderLayout.CENTER);

        return panel;
    }

    private JPanel createMenuPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel menuLabel = new JLabel("Menu");
        menuLabel.setFont(new Font("SansSerif", Font.BOLD, 20));

        refreshMenuModel();
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

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(addButton);

        panel.add(menuLabel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void refreshMenuModel() {
        menuModel.clear();
        for (MenuItem item : menuItems) {
            menuModel.addElement(item);
        }
    }

    private void updateChefMenuModel(DefaultListModel<MenuItem> chefMenuModel) {
        chefMenuModel.clear();
        if (loggedInChef == null) {
            return;
        }
        String chefName = loggedInChef.getName();
        for (MenuItem item : menuItems) {
            if (item.getChefName().equals(chefName)) {
                chefMenuModel.addElement(item);
            }
        }
    }

    private Chef findChefByUsername(String username) {
        for (Chef chef : chefs) {
            if (chef.getUsername().equals(username)) {
                return chef;
            }
        }
        return null;
    }

    private JPanel createChefLoginPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel title = new JLabel("Chef Login and Menu Management");
        title.setFont(new Font("SansSerif", Font.BOLD, 20));

        JPanel loginPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        JLabel userLabel = new JLabel("Username:");
        JTextField userField = new JTextField(10);
        JLabel passLabel = new JLabel("Password:");
        JPasswordField passField = new JPasswordField(10);
        JButton loginButton = new JButton("Login");
        JButton logoutButton = new JButton("Logout");
        chefLoginStatusLabel = new JLabel("Not logged in");

        loginPanel.add(userLabel);
        loginPanel.add(userField);
        loginPanel.add(passLabel);
        loginPanel.add(passField);
        loginPanel.add(loginButton);
        loginPanel.add(logoutButton);

        DefaultListModel<MenuItem> chefMenuModel = new DefaultListModel<>();
        JList<MenuItem> chefMenuList = new JList<>(chefMenuModel);
        chefMenuList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        chefMenuList.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane chefScrollPane = new JScrollPane(chefMenuList);

        JButton addDishButton = new JButton("Add Dish");
        JButton editDishButton = new JButton("Edit Selected");
        JButton removeDishButton = new JButton("Remove Selected");

        addDishButton.addActionListener(e -> {
            if (loggedInChef == null) {
                JOptionPane.showMessageDialog(this, "Please log in as a chef first.");
                return;
            }
            if (loggedInChef.isFired()) {
                JOptionPane.showMessageDialog(this, "This chef has been fired and cannot modify the menu.");
                return;
            }
            JTextField nameField = new JTextField();
            JTextField priceField = new JTextField();
            Object[] message = {"Dish Name:", nameField, "Price:", priceField};
            int option = JOptionPane.showConfirmDialog(this, message, "Add Dish", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                String dishName = nameField.getText().trim();
                String priceText = priceField.getText().trim();
                if (dishName.isEmpty() || priceText.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Dish name and price are required.");
                    return;
                }
                double price;
                try {
                    price = Double.parseDouble(priceText);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Invalid price.");
                    return;
                }
                MenuItem item = new MenuItem(dishName, price, loggedInChef.getName());
                menuItems.add(item);
                refreshMenuModel();
                updateChefMenuModel(chefMenuModel);
            }
        });

        editDishButton.addActionListener(e -> {
            if (loggedInChef == null) {
                JOptionPane.showMessageDialog(this, "Please log in as a chef first.");
                return;
            }
            if (loggedInChef.isFired()) {
                JOptionPane.showMessageDialog(this, "This chef has been fired and cannot modify the menu.");
                return;
            }
            MenuItem selected = chefMenuList.getSelectedValue();
            if (selected == null) {
                JOptionPane.showMessageDialog(this, "Please select a dish to edit.");
                return;
            }
            JTextField nameField = new JTextField(selected.getName());
            JTextField priceField = new JTextField(String.valueOf(selected.getPrice()));
            Object[] message = {"Dish Name:", nameField, "Price:", priceField};
            int option = JOptionPane.showConfirmDialog(this, message, "Edit Dish", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                String dishName = nameField.getText().trim();
                String priceText = priceField.getText().trim();
                if (dishName.isEmpty() || priceText.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Dish name and price are required.");
                    return;
                }
                double price;
                try {
                    price = Double.parseDouble(priceText);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Invalid price.");
                    return;
                }
                MenuItem newItem = new MenuItem(dishName, price, loggedInChef.getName());
                for (int i = 0; i < menuItems.size(); i++) {
                    if (menuItems.get(i) == selected) {
                        menuItems.set(i, newItem);
                        break;
                    }
                }
                refreshMenuModel();
                updateChefMenuModel(chefMenuModel);
            }
        });

        removeDishButton.addActionListener(e -> {
            if (loggedInChef == null) {
                JOptionPane.showMessageDialog(this, "Please log in as a chef first.");
                return;
            }
            if (loggedInChef.isFired()) {
                JOptionPane.showMessageDialog(this, "This chef has been fired and cannot modify the menu.");
                return;
            }
            MenuItem selected = chefMenuList.getSelectedValue();
            if (selected == null) {
                JOptionPane.showMessageDialog(this, "Please select a dish to remove.");
                return;
            }
            menuItems.remove(selected);
            for (int i = cartModel.getSize() - 1; i >= 0; i--) {
                if (cartModel.getElementAt(i) == selected) {
                    cartModel.remove(i);
                }
            }
            refreshMenuModel();
            updateChefMenuModel(chefMenuModel);
            updateCartTotal();
        });

        loginButton.addActionListener(e -> {
            String username = userField.getText().trim();
            String password = new String(passField.getPassword());
            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Username and password are required.");
                return;
            }
            Chef chef = findChefByUsername(username);
            if (chef == null || !chef.checkPassword(password)) {
                JOptionPane.showMessageDialog(this, "Invalid username or password.");
                return;
            }
            if (chef.isFired()) {
                JOptionPane.showMessageDialog(this, "This chef has been fired and cannot log in.");
                return;
            }
            loggedInChef = chef;
            chefLoginStatusLabel.setText("Logged in as " + chef.getName());
            updateChefMenuModel(chefMenuModel);
        });

        logoutButton.addActionListener(e -> {
            loggedInChef = null;
            chefLoginStatusLabel.setText("Not logged in");
            chefMenuModel.clear();
        });

        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.add(chefLoginStatusLabel, BorderLayout.WEST);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        buttonPanel.add(addDishButton);
        buttonPanel.add(editDishButton);
        buttonPanel.add(removeDishButton);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(title, BorderLayout.NORTH);
        topPanel.add(loginPanel, BorderLayout.CENTER);
        topPanel.add(statusPanel, BorderLayout.SOUTH);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(chefScrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createCartPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel cartLabel = new JLabel("Cart");
        cartLabel.setFont(new Font("SansSerif", Font.BOLD, 20));

        JList<MenuItem> cartList = new JList<>(cartModel);
        cartList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        cartList.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(cartList);

        cartTotalLabel = new JLabel("Total: $0.00");
        cartTotalLabel.setFont(new Font("SansSerif", Font.BOLD, 16));

        JButton removeButton = new JButton("Remove Selected");
        removeButton.addActionListener(e -> {
            MenuItem selected = cartList.getSelectedValue();
            if (selected != null) {
                cartModel.removeElement(selected);
                updateCartTotal();
            } else {
                JOptionPane.showMessageDialog(this, "Please select an item to remove.");
            }
        });

        JButton checkoutButton = new JButton("Checkout");
        checkoutButton.addActionListener(this::handleCheckout);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(cartTotalLabel);
        bottomPanel.add(removeButton);
        bottomPanel.add(checkoutButton);

        panel.add(cartLabel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createStaffPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel chefPanel = createChefPanel();
        JPanel deliveryPanel = createDeliveryPanel();

        panel.add(chefPanel);
        panel.add(deliveryPanel);

        return panel;
    }

    private JPanel createChefPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Chefs"));

        chefStatusModel.clear();
        for (Chef chef : chefs) {
            chefStatusModel.addElement(formatChefStatus(chef));
        }

        JList<String> chefList = new JList<>(chefStatusModel);
        chefList.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(chefList);

        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createDeliveryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Delivery Workers"));

        deliveryStatusModel.clear();
        for (DeliveryPerson d : deliveryPeople) {
            deliveryStatusModel.addElement(formatDeliveryStatus(d));
        }

        JList<String> deliveryList = new JList<>(deliveryStatusModel);
        deliveryList.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(deliveryList);

        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createAIChatPanel() {
       JPanel panel = new JPanel(new BorderLayout());
       panel.setBorder(new EmptyBorder(10, 10, 10, 10));

       aiConversationArea = new JTextArea();
       aiConversationArea.setEditable(false);
       aiConversationArea.setLineWrap(true);
       aiConversationArea.setWrapStyleWord(true);
 
       aiConversationArea.setBorder(new EmptyBorder(5, 5, 10, 5));

       aiInputField = new JTextField();
       JButton sendButton = new JButton("Send");

       ActionListener sendAction = e -> sendAIMessage();
       aiInputField.addActionListener(sendAction);
       sendButton.addActionListener(sendAction);


       JScrollPane scrollPane = new JScrollPane(aiConversationArea);
       scrollPane.setBorder(BorderFactory.createEmptyBorder());
       panel.add(scrollPane, BorderLayout.CENTER);


       JPanel bottom = new JPanel(new BorderLayout());
       bottom.setBorder(new EmptyBorder(5, 0, 0, 0));
       bottom.add(aiInputField, BorderLayout.CENTER);
       bottom.add(sendButton, BorderLayout.EAST);

       panel.add(bottom, BorderLayout.SOUTH);
       return panel;
    }

    private void handleCheckout(ActionEvent e) {
        if (cartModel.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Your cart is empty.");
            return;
        }

        double total = calculateCartTotal();
        if (total > balance) {
            JOptionPane.showMessageDialog(this, "Insufficient balance. Please deposit more money.");
            return;
        }

        String[] chefOptions = chefs.stream().map(Chef::getName).toArray(String[]::new);

        String selectedChefName = (String) JOptionPane.showInputDialog(
                this,
                "Choose a chef:",
                "Select Chef",
                JOptionPane.PLAIN_MESSAGE,
                null,
                chefOptions,
                chefOptions[0]
        );

        if (selectedChefName == null) return;

        Chef selectedChef = chefs.stream()
                .filter(c -> c.getName().equals(selectedChefName))
                .findFirst()
                .orElse(null);

        if (selectedChef == null) {
            JOptionPane.showMessageDialog(this, "Invalid chef selection.");
            return;
        }

        if (selectedChef.isFired()) {
            JOptionPane.showMessageDialog(this, "Selected chef has been fired. Please choose another.");
            return;
        }

        for (DeliveryPerson d : deliveryPeople) {
            double bid = 5.0 + Math.random() * 10.0;
            d.setLastBidPrice(bid);
        }

        DeliveryPerson lowestBidder = null;
        double lowestBid = Double.MAX_VALUE;
        for (DeliveryPerson d : deliveryPeople) {
            if (d.getLastBidPrice() < lowestBid) {
                lowestBid = d.getLastBidPrice();
                lowestBidder = d;
            }
        }

        String[] delOptions = deliveryPeople.stream().map(DeliveryPerson::getName).toArray(String[]::new);
        String defaultDeliveryName = lowestBidder != null ? lowestBidder.getName() : delOptions[0];

        StringBuilder bidsInfo = new StringBuilder();
        for (DeliveryPerson d : deliveryPeople) {
            bidsInfo.append(d.getName())
                    .append(": $")
                    .append(String.format("%.2f", d.getLastBidPrice()))
                    .append("\n");
        }

        String selectedDeliveryName = (String) JOptionPane.showInputDialog(
                this,
                "Delivery bidding results (system generated):\n" + bidsInfo +
                        "\nLowest bid: " + (lowestBidder != null
                        ? lowestBidder.getName() + " ($" + String.format("%.2f", lowestBid) + ")"
                        : "N/A") +
                        "\nChoose delivery worker:",
                "Select Delivery Worker",
                JOptionPane.PLAIN_MESSAGE,
                null,
                delOptions,
                defaultDeliveryName
        );

        if (selectedDeliveryName == null) return;

        DeliveryPerson selectedDelivery = deliveryPeople.stream()
                .filter(d -> d.getName().equals(selectedDeliveryName))
                .findFirst()
                .orElse(null);

        if (selectedDelivery == null) {
            JOptionPane.showMessageDialog(this, "Invalid delivery worker selection.");
            return;
        }

        if (selectedDelivery.isFired()) {
            JOptionPane.showMessageDialog(this, "Selected delivery worker has been fired. Please choose another.");
            return;
        }

        if (lowestBidder != null && selectedDelivery != lowestBidder) {
            String memo = JOptionPane.showInputDialog(this,
                    "You selected a higher-price bid.\nPlease enter justification memo:",
                    "Manager Justification",
                    JOptionPane.PLAIN_MESSAGE);
            if (memo == null || memo.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Justification memo is required when not choosing the lowest bid.");
                return;
            }
            selectedDelivery.setLastJustificationMemo(memo.trim());
        } else {
            selectedDelivery.setLastJustificationMemo("Chose lowest bid.");
        }

        balance -= total;
        updateBalanceLabel();
        JOptionPane.showMessageDialog(this, "Order placed successfully!");

        rateStaff(selectedChef, selectedDelivery);

        cartModel.clear();
        updateCartTotal();
    }

    private void rateStaff(Chef chef, DeliveryPerson delivery) {
        String chefRatingStr = JOptionPane.showInputDialog(this, "Rate the chef (1-5):");
        if (chefRatingStr != null) {
            try {
                int rating = Integer.parseInt(chefRatingStr);
                if (rating >= 1 && rating <= 5) {
                    chef.addRating(rating);
                }
            } catch (NumberFormatException ignored) {
            }
        }

        String delRatingStr = JOptionPane.showInputDialog(this, "Rate the delivery worker (1-5):");
        if (delRatingStr != null) {
            try {
                int rating = Integer.parseInt(delRatingStr);
                if (rating >= 1 && rating <= 5) {
                    delivery.addRating(rating);
                }
            } catch (NumberFormatException ignored) {
            }
        }

        refreshStaffStatus();
    }

    private void refreshStaffStatus() {
        if (chefStatusModel != null) {
            chefStatusModel.clear();
            for (Chef chef : chefs) {
                chefStatusModel.addElement(formatChefStatus(chef));
            }
        }
        if (deliveryStatusModel != null) {
            deliveryStatusModel.clear();
            for (DeliveryPerson d : deliveryPeople) {
                deliveryStatusModel.addElement(formatDeliveryStatus(d));
            }
        }
    }

    private void sendAIMessage() {
        String userMessage = aiInputField.getText().trim();
        if (userMessage.isEmpty()) {
            return;
        }

        aiConversationArea.append("You: " + userMessage + "\n");
        aiInputField.setText("");

        new Thread(() -> {
            try {
                String response = callOllamaAPI(userMessage);
                SwingUtilities.invokeLater(() -> {
                    aiConversationArea.append("AI: " + response + "\n\n");
                });
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> {
                    aiConversationArea.append("AI: Sorry, I couldn't connect to the AI service.\n\n");
                });
            }
        }).start();
    }

    private String callOllamaAPI(String prompt) throws Exception {
        String model = "llama3.2:3b";
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


    private double calculateCartTotal() {
        double total = 0.0;
        for (int i = 0; i < cartModel.size(); i++) {
            total += cartModel.getElementAt(i).getPrice();
        }
        return total;
    }

    private void updateCartTotal() {
        double total = calculateCartTotal();
        cartTotalLabel.setText(String.format("Total: $%.2f", total));
    }

    private void updateBalanceLabel() {
        balanceLabel.setText(String.format("Balance: $%.2f", balance));
    }

    private String formatChefStatus(Chef c) {
        String status;
        if (c.isFired()) {
            status = "Fired";
        } else if (c.getDemotionCount() > 0) {
            status = "Demoted" + c.getDemotionCount();
        } else {
            status = "Normal";
        }
        return String.format(
                "%-8s | Demotions:%d | Status:%-8s | Bonus:%2d",
                c.getName(),
                c.getDemotionCount(),
                status,
                c.getBonusCount()
        );
    }

    private String formatDeliveryStatus(DeliveryPerson d) {
        String status;
        if (d.isFired()) {
            status = "Fired";
        } else if (d.getDemotionCount() > 0) {
            status = "Demoted" + d.getDemotionCount();
        } else {
            status = "Normal";
        }
        return String.format(
                "%-8s | Demotions:%d | Status:%-8s | Bonus:%2d | Bid:$%.2f | Memo:%s",
                d.getName(),
                d.getDemotionCount(),
                status,
                d.getBonusCount(),
                d.getLastBidPrice(),
                d.getLastJustificationMemo()
        );
    }

    private static class MenuItem {
        private final String name;
        private final double price;
        private final String chefName;

        MenuItem(String name, double price, String chefName) {
            this.name = name;
            this.price = price;
            this.chefName = chefName;
        }

        String getName() {
            return name;
        }

        double getPrice() {
            return price;
        }

        String getChefName() {
            return chefName;
        }

        @Override
        public String toString() {
            return String.format("%-20s $%.2f (Chef: %s)", name, price, chefName);
        }
    }

    private static class Chef {
        private final String name;
        private final String username;
        private final String password;

        private int ratingSum = 0;
        private int ratingCount = 0;
        private int complaints = 0;
        private int compliments = 0;
        private int demotionCount = 0;
        private int bonusCount = 0;
        private boolean fired = false;

        Chef(String name, String username, String password) {
            this.name = name;
            this.username = username;
            this.password = password;
        }

        String getName() {
            return name;
        }

        String getUsername() {
            return username;
        }

        boolean checkPassword(String password) {
            return this.password.equals(password);
        }

        void decideMenu(List<MenuItem> menuItems) {
            if ("Chef 1".equals(name)) {
                menuItems.add(new MenuItem("Margherita Pizza", 12.99, name));
                menuItems.add(new MenuItem("Spaghetti Bolognese", 14.49, name));
            } else if ("Chef 2".equals(name)) {
                menuItems.add(new MenuItem("Caesar Salad", 9.99, name));
            } else if ("Chef 3".equals(name)) {
                menuItems.add(new MenuItem("Grilled Salmon", 18.99, name));
                menuItems.add(new MenuItem("Cheeseburger", 11.49, name));
            }
        }

        void addRating(int rating) {
            ratingSum += rating;
            ratingCount += 1;

            if (rating < 2) {
                complaints += 1;
            } else if (rating > 4) {
                compliments += 1;
            }

            while (compliments > 0 && complaints > 0) {
                compliments -= 1;
                complaints -= 1;
            }

            checkPerformance();
        }

        private void checkPerformance() {
            if (!fired) {
                if (complaints >= 3 || (ratingCount >= 3 && getAverageRating() < 2.0)) {
                    demotionCount += 1;
                    complaints = 0;
                    compliments = 0;
                    if (demotionCount >= 2) {
                        fired = true;
                    }
                }
            }
            if (!fired) {
                if (compliments >= 3 || (ratingCount >= 3 && getAverageRating() > 4.0)) {
                    bonusCount += 1;
                    compliments = 0;
                }
            }
        }

        int getDemotionCount() {
            return demotionCount;
        }

        int getBonusCount() {
            return bonusCount;
        }

        boolean isFired() {
            return fired;
        }

        private double getAverageRating() {
            if (ratingCount == 0) return 0.0;
            return (double) ratingSum / ratingCount;
        }
    }

    private static class DeliveryPerson {
        private final String name;

        private int ratingSum = 0;
        private int ratingCount = 0;
        private int complaints = 0;
        private int compliments = 0;
        private int demotionCount = 0;
        private int bonusCount = 0;
        private boolean fired = false;

        private double lastBidPrice = 0.0;
        private String lastJustificationMemo = "";

        DeliveryPerson(String name) {
            this.name = name;
        }

        String getName() {
            return name;
        }

        void addRating(int rating) {
            ratingSum += rating;
            ratingCount += 1;

            if (rating < 2) {
                complaints += 1;
            } else if (rating > 4) {
                compliments += 1;
            }

            while (compliments > 0 && complaints > 0) {
                compliments -= 1;
                complaints -= 1;
            }

            checkPerformance();
        }

        private void checkPerformance() {
            if (!fired) {
                if (complaints >= 3 || (ratingCount >= 3 && getAverageRating() < 2.0)) {
                    demotionCount += 1;
                    complaints = 0;
                    compliments = 0;
                    if (demotionCount >= 2) {
                        fired = true;
                    }
                }
            }
            if (!fired) {
                if (compliments >= 3 || (ratingCount >= 3 && getAverageRating() > 4.0)) {
                    bonusCount += 1;
                    compliments = 0;
                }
            }
        }

        int getDemotionCount() {
            return demotionCount;
        }

        int getBonusCount() {
            return bonusCount;
        }

        boolean isFired() {
            return fired;
        }

        private double getAverageRating() {
            if (ratingCount == 0) return 0.0;
            return (double) ratingSum / ratingCount;
        }

        double getLastBidPrice() {
            return lastBidPrice;
        }

        void setLastBidPrice(double price) {
            this.lastBidPrice = price;
        }

        String getLastJustificationMemo() {
            return lastJustificationMemo;
        }

        void setLastJustificationMemo(String memo) {
            this.lastJustificationMemo = memo;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(RestaurantApp::new);
    }
}
