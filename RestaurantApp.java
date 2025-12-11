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

    private DefaultListModel<String> deliveryStatusModel = new DefaultListModel<>();

    private final List<MenuItem> menuItems = new ArrayList<>();
    private final List<Chef> chefs = new ArrayList<>();
    private final List<DeliveryPerson> deliveryPeople = new ArrayList<>();

    private DefaultListModel<String> chefStatusModel = new DefaultListModel<>();
    private DefaultListModel<String> deliveryListModel = new DefaultListModel<>();

    private final List<Customer> customers = new ArrayList<>();
    private Customer loggedInCustomer;
    private JLabel customerStatusLabel;

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
        JPanel chefPanel = createChefPanel();
        JPanel aiPanel = createAIChatPanel();
        JPanel customerPanel = createCustomerPanel();

        cardPanel.add(homePanel, "HOME");
        cardPanel.add(menuPanel, "MENU");
        cardPanel.add(cartPanel, "CART");
        cardPanel.add(staffPanel, "STAFF");
        cardPanel.add(chefPanel, "CHEF");
        cardPanel.add(aiPanel, "AI");
        cardPanel.add(customerPanel, "CUSTOMER");

        setLayout(new BorderLayout());

        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        JButton homeButton = new JButton("Home");
        JButton menuButton = new JButton("Menu");
        JButton cartButton = new JButton("Cart");
        JButton staffButton = new JButton("Staff");
        JButton chefButton = new JButton("Chef Login");
        JButton aiButton = new JButton("AI Helper");
        JButton customerButton = new JButton("Customer Login");

        homeButton.addActionListener(e -> cardLayout.show(cardPanel, "HOME"));
        menuButton.addActionListener(e -> {
            refreshMenuModel();
            cardLayout.show(cardPanel, "MENU");
        });
        cartButton.addActionListener(e -> cardLayout.show(cardPanel, "CART"));
        staffButton.addActionListener(e -> cardLayout.show(cardPanel, "STAFF"));
        chefButton.addActionListener(e -> cardLayout.show(cardPanel, "CHEF"));
        aiButton.addActionListener(e -> cardLayout.show(cardPanel, "AI"));
        customerButton.addActionListener(e -> cardLayout.show(cardPanel, "CUSTOMER"));

        navPanel.add(homeButton);
        navPanel.add(menuButton);
        navPanel.add(cartButton);
        navPanel.add(staffButton);
        navPanel.add(chefButton);
        navPanel.add(aiButton);
        navPanel.add(customerButton);

        JPanel balancePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        balanceLabel = new JLabel("Balance: $0.00");
        JButton depositButton = new JButton("Deposit $20");
        depositButton.addActionListener(e -> {
            balance += 20;
            updateBalanceLabel();
        });
        balancePanel.add(balanceLabel);
        balancePanel.add(depositButton);

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBorder(new EmptyBorder(5, 10, 5, 10));
        topBar.add(navPanel, BorderLayout.WEST);
        topBar.add(balancePanel, BorderLayout.EAST);

        add(topBar, BorderLayout.NORTH);
        add(cardPanel, BorderLayout.CENTER);

        updateBalanceLabel();
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

    private JPanel createHomePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Restaurant Management System Demo", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 24));

        JTextArea description = new JTextArea();
        description.setEditable(false);
        description.setLineWrap(true);
        description.setWrapStyleWord(true);
        description.setFont(new Font("SansSerif", Font.PLAIN, 14));
        description.setText(
                "Welcome to the Restaurant Management System Demo.\n\n" +
                        "You can:\n" +
                        "- Browse the menu and add dishes to your cart.\n" +
                        "- Checkout using your balance.\n" +
                        "- View staff status.\n" +
                        "- Chef can log in to manage their own dishes.\n" +
                        "- Customers can register/login and become VIP.\n" +
                        "- Use the AI helper for natural language assistance and voice-style ordering."
        );

        panel.add(title, BorderLayout.NORTH);
        panel.add(new JScrollPane(description), BorderLayout.CENTER);

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
        boolean vip = loggedInCustomer != null && loggedInCustomer.isVip();
        for (MenuItem item : menuItems) {
            if (!item.isVipOnly() || vip) {
                menuModel.addElement(item);
            }
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

    private Customer findCustomerByUsername(String username) {
        for (Customer c : customers) {
            if (c.getUsername().equals(username)) {
                return c;
            }
        }
        return null;
    }

    private JPanel createChefPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel loginPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        loginPanel.setBorder(BorderFactory.createTitledBorder("Chef Login"));

        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();

        chefLoginStatusLabel = new JLabel("Not logged in");

        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();

            Chef chef = findChefByUsername(username);
            if (chef != null && chef.getPassword().equals(password)) {
                loggedInChef = chef;
                chefLoginStatusLabel.setText("Logged in as " + chef.getName());
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials.");
            }
        });

        loginPanel.add(new JLabel("Username:"));
        loginPanel.add(usernameField);
        loginPanel.add(new JLabel("Password:"));
        loginPanel.add(passwordField);
        loginPanel.add(loginButton);
        loginPanel.add(chefLoginStatusLabel);

        JPanel managePanel = new JPanel(new BorderLayout());
        managePanel.setBorder(BorderFactory.createTitledBorder("Manage Dishes"));

        DefaultListModel<MenuItem> chefMenuModel = new DefaultListModel<>();
        JList<MenuItem> chefMenuList = new JList<>(chefMenuModel);
        chefMenuList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        chefMenuList.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane chefMenuScroll = new JScrollPane(chefMenuList);

        JButton refreshButton = new JButton("Refresh My Dishes");
        refreshButton.addActionListener(e -> updateChefMenuModel(chefMenuModel));

        JButton addDishButton = new JButton("Add New Dish");
        addDishButton.addActionListener(e -> {
            if (loggedInChef == null) {
                JOptionPane.showMessageDialog(this, "Please log in as a chef first.");
                return;
            }
            String name = JOptionPane.showInputDialog(this, "Dish name:");
            if (name == null || name.trim().isEmpty()) {
                return;
            }
            String priceStr = JOptionPane.showInputDialog(this, "Price:");
            if (priceStr == null || priceStr.trim().isEmpty()) {
                return;
            }
            try {
                double price = Double.parseDouble(priceStr.trim());
                int vipChoice = JOptionPane.showConfirmDialog(this, "VIP only dish?", "Dish Type", JOptionPane.YES_NO_OPTION);
                boolean vipOnly = (vipChoice == JOptionPane.YES_OPTION);
                MenuItem newItem = new MenuItem(name.trim(), price, loggedInChef.getName(), vipOnly);
                menuItems.add(newItem);
                refreshMenuModel();
                updateChefMenuModel(chefMenuModel);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid price.");
            }
        });

        JButton removeDishButton = new JButton("Remove Selected Dish");
        removeDishButton.addActionListener(e -> {
            if (loggedInChef == null) {
                JOptionPane.showMessageDialog(this, "Please log in as a chef first.");
                return;
            }
            MenuItem selected = chefMenuList.getSelectedValue();
            if (selected == null) {
                JOptionPane.showMessageDialog(this, "Select a dish to remove.");
                return;
            }
            if (!selected.getChefName().equals(loggedInChef.getName())) {
                JOptionPane.showMessageDialog(this, "You can only remove your own dishes.");
                return;
            }
            menuItems.remove(selected);
            refreshMenuModel();
            updateChefMenuModel(chefMenuModel);
        });

        JPanel manageButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        manageButtons.add(refreshButton);
        manageButtons.add(addDishButton);
        manageButtons.add(removeDishButton);

        managePanel.add(chefMenuScroll, BorderLayout.CENTER);
        managePanel.add(manageButtons, BorderLayout.SOUTH);

        panel.add(loginPanel, BorderLayout.NORTH);
        panel.add(managePanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createCustomerPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel loginPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        loginPanel.setBorder(BorderFactory.createTitledBorder("Customer Login / Register"));

        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();

        JButton loginButton = new JButton("Login");
        JButton registerButton = new JButton("Register");

        customerStatusLabel = new JLabel("Not logged in");

        loginButton.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();
            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Username and password required.");
                return;
            }
            Customer c = findCustomerByUsername(username);
            if (c != null && c.getPassword().equals(password)) {
                loggedInCustomer = c;
                updateCustomerStatusLabel();
                refreshMenuModel();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials.");
            }
        });

        registerButton.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();
            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Username and password required.");
                return;
            }
            if (findCustomerByUsername(username) != null) {
                JOptionPane.showMessageDialog(this, "Username already exists.");
                return;
            }
            Customer c = new Customer(username, password);
            customers.add(c);
            loggedInCustomer = c;
            updateCustomerStatusLabel();
            refreshMenuModel();
            JOptionPane.showMessageDialog(this, "Registered and logged in as " + username);
        });

        loginPanel.add(new JLabel("Username:"));
        loginPanel.add(usernameField);
        loginPanel.add(new JLabel("Password:"));
        loginPanel.add(passwordField);
        loginPanel.add(loginButton);
        loginPanel.add(registerButton);

        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBorder(BorderFactory.createTitledBorder("Status"));
        statusPanel.add(customerStatusLabel, BorderLayout.CENTER);

        panel.add(loginPanel, BorderLayout.NORTH);
        panel.add(statusPanel, BorderLayout.CENTER);

        return panel;
    }

    private void updateCustomerStatusLabel() {
        if (customerStatusLabel == null) {
            return;
        }
        if (loggedInCustomer == null) {
            customerStatusLabel.setText("Not logged in");
        } else {
            String type = loggedInCustomer.isVip() ? "VIP Customer" : "Registered Customer";
            customerStatusLabel.setText("Logged in as " + loggedInCustomer.getUsername() + " (" + type + ")");
        }
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
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel chefPanel = new JPanel(new BorderLayout());
        chefPanel.setBorder(BorderFactory.createTitledBorder("Chefs"));

        chefStatusModel.clear();
        for (Chef chef : chefs) {
            chefStatusModel.addElement(formatChefStatus(chef));
        }

        JList<String> chefList = new JList<>(chefStatusModel);
        chefList.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(chefList);

        chefPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel deliveryPanel = new JPanel(new BorderLayout());
        deliveryPanel.setBorder(BorderFactory.createTitledBorder("Delivery People"));

        deliveryStatusModel.clear();
        for (DeliveryPerson d : deliveryPeople) {
            deliveryStatusModel.addElement(formatDeliveryStatus(d));
        }

        JList<String> deliveryList = new JList<>(deliveryStatusModel);
        deliveryList.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane deliveryScroll = new JScrollPane(deliveryList);

        deliveryPanel.add(deliveryScroll, BorderLayout.CENTER);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, chefPanel, deliveryPanel);
        splitPane.setResizeWeight(0.5);

        panel.add(splitPane, BorderLayout.CENTER);

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

        JButton voiceOrderButton = new JButton("Voice Order");
        voiceOrderButton.addActionListener(e -> handleVoiceOrder());

        JScrollPane scrollPane = new JScrollPane(aiConversationArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setBorder(new EmptyBorder(5, 0, 0, 0));
        bottom.add(voiceOrderButton, BorderLayout.WEST);
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

        double originalTotal = calculateCartTotal();
        double total = originalTotal;

        boolean vipNow = loggedInCustomer != null && loggedInCustomer.isVip();
        boolean usedFreeDelivery = false;

        if (vipNow) {
            double discount = total * 0.05;
            total -= discount;
        }

        if (loggedInCustomer != null && loggedInCustomer.getFreeDeliveryCredits() > 0) {
            loggedInCustomer.useFreeDeliveryCredit();
            usedFreeDelivery = true;
        }

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

        String msg = "Order placed successfully!";
        if (vipNow) {
            msg += " (VIP 5% discount applied)";
        }
        if (usedFreeDelivery) {
            msg += " (Free delivery used)";
        }
        JOptionPane.showMessageDialog(this, msg);

        if (loggedInCustomer != null) {
            loggedInCustomer.recordOrder(originalTotal);
            if (!loggedInCustomer.isVip()) {
                if (loggedInCustomer.getComplaintCount() == 0 &&
                        (loggedInCustomer.getTotalSpent() > 100.0 || loggedInCustomer.getOrderCount() >= 3)) {
                    loggedInCustomer.setVip(true);
                    updateCustomerStatusLabel();
                    JOptionPane.showMessageDialog(this, "Congratulations! You are now a VIP customer.");
                    refreshMenuModel();
                }
            }
        }

        rateStaff(selectedChef, selectedDelivery);

        cartModel.clear();
        updateCartTotal();
    }

    private Chef chooseChefForOrder() {
        List<Chef> availableChefs = new ArrayList<>();
        for (Chef chef : chefs) {
            if (!chef.isFired()) {
                availableChefs.add(chef);
            }
        }
        if (availableChefs.isEmpty()) {
            return null;
        }

        String[] chefOptions = availableChefs.stream().map(Chef::getName).toArray(String[]::new);

        String selectedChefName = (String) JOptionPane.showInputDialog(
                this,
                "Choose a chef:",
                "Select Chef",
                JOptionPane.PLAIN_MESSAGE,
                null,
                chefOptions,
                chefOptions[0]
        );

        if (selectedChefName == null) {
            return null;
        }

        for (Chef chef : availableChefs) {
            if (chef.getName().equals(selectedChefName)) {
                return chef;
            }
        }
        return null;
    }

    private DeliveryPerson chooseDeliveryPersonForOrder() {
        List<DeliveryPerson> availableDelivery = new ArrayList<>();
        for (DeliveryPerson d : deliveryPeople) {
            if (!d.isFired()) {
                availableDelivery.add(d);
            }
        }
        if (availableDelivery.isEmpty()) {
            return null;
        }

        String[] options = availableDelivery.stream().map(DeliveryPerson::getName).toArray(String[]::new);

        String selectedName = (String) JOptionPane.showInputDialog(
                this,
                "Choose a delivery person:",
                "Select Delivery",
                JOptionPane.PLAIN_MESSAGE,
                null,
                options,
                options[0]
        );

        if (selectedName == null) {
            return null;
        }

        for (DeliveryPerson d : availableDelivery) {
            if (d.getName().equals(selectedName)) {
                return d;
            }
        }
        return null;
    }

    private int askForRating(String message) {
        while (true) {
            String input = JOptionPane.showInputDialog(this, message);
            if (input == null) {
                return -1;
            }
            input = input.trim();
            if (input.isEmpty()) {
                return -1;
            }
            try {
                int rating = Integer.parseInt(input);
                if (rating >= 1 && rating <= 5) {
                    return rating;
                } else {
                    JOptionPane.showMessageDialog(this, "Please enter a number between 1 and 5.");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid integer.");
            }
        }
    }

    private void updateChefDemotionStatus(Chef chef) {
    int demotionsFromWarnings = chef.getWarningCount() / 3;
    int netComplaints = effectiveComplaints(chef.getComplaintCount(), chef.getComplimentCount());
    int demotionsFromComplaints = netComplaints / 3;

    int targetDemotions = demotionsFromWarnings + demotionsFromComplaints;

    while (chef.getDemotionCount() < targetDemotions) {
        chef.incrementDemotionCount();
    }

    chef.setDemoted(chef.getDemotionCount() > 0);

    if (chef.getDemotionCount() >= 2) {
        chef.setSuspended(true);
    }
    }

    private void updateDeliveryDemotionStatus(DeliveryPerson d) {
    int demotionsFromWarnings = d.getWarningCount() / 3;
    int netComplaints = effectiveComplaints(d.getComplaintCount(), d.getComplimentCount());
    int demotionsFromComplaints = netComplaints / 3;

    int targetDemotions = demotionsFromWarnings + demotionsFromComplaints;

    while (d.getDemotionCount() < targetDemotions) {
        d.incrementDemotionCount();
    }

    d.setDemoted(d.getDemotionCount() > 0);

    if (d.getDemotionCount() >= 2) {
        d.setSuspended(true);
    }
    }

    private void applyChefRating(Chef chef, int rating) {
    if (chef.isFired()) {
        return;
    }

    chef.addRating(rating);

    if (rating > 4) {
        chef.incrementHighRatingCount();
    }

    if (rating < 2) {
        chef.incrementWarning();
    }

    updateChefDemotionStatus(chef);

    int bonusesFromHighRatings = chef.getHighRatingCount() / 3;
    int bonusesFromCompliments = chef.getComplimentCount() / 3;
    int targetBonus = bonusesFromHighRatings + bonusesFromCompliments;

    while (chef.getBonusCount() < targetBonus) {
        chef.incrementBonus();
    }

    updateChefStatusModel();
    }

    private void applyDeliveryRating(DeliveryPerson d, int rating) {
    if (d.isFired()) {
        return;
    }

    d.addRating(rating);

    if (rating > 4) {
        d.incrementHighRatingCount();
    }

    if (rating < 2) {
        d.incrementWarning();
    }

    updateDeliveryDemotionStatus(d);

    int bonusesFromHighRatings = d.getHighRatingCount() / 3;
    int bonusesFromCompliments = d.getComplimentCount() / 3;
    int targetBonus = bonusesFromHighRatings + bonusesFromCompliments;

    while (d.getBonusCount() < targetBonus) {
        d.incrementBonusCount();
    }

    updateDeliveryStatusModel();
    }

    private void rateStaff(Chef chef, DeliveryPerson delivery) {
        int chefRating = askForRating("Rate the chef (1-5):");
        int deliveryRating = askForRating("Rate the delivery person (1-5):");

        if (chefRating > 0) {
            applyChefRating(chef, chefRating);
        }
        if (deliveryRating > 0) {
            applyDeliveryRating(delivery, deliveryRating);
        }

        collectChefFeedback(chef);
        collectDeliveryFeedback(delivery);

        updateChefStatusModel();
        updateDeliveryStatusModel();
    }

    private void collectChefFeedback(Chef chef) {
        Object[] options = {"None", "Complaint", "Compliment"};
        int choice = JOptionPane.showOptionDialog(
                this,
                "Do you want to leave a complaint or compliment for the chef?",
                "Chef Feedback",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );

        if (choice == 1) {
            String text = JOptionPane.showInputDialog(this, "Please enter your complaint:");
            int weight = 1;
            if (loggedInCustomer != null && loggedInCustomer.isVip()) {
                weight = 2;
            }
            for (int i = 0; i < weight; i++) {
                chef.incrementComplaintCount();
            }
            chef.setLastComplaint(text != null ? text.trim() : "");
            if (loggedInCustomer != null) {
                loggedInCustomer.incrementComplaintCount();
            }
            updateChefDemotionStatus(chef);
        } else if (choice == 2) {
            String text = JOptionPane.showInputDialog(this, "Please enter your compliment:");
            int weight = 1;
            if (loggedInCustomer != null && loggedInCustomer.isVip()) {
                weight = 2;
            }
            for (int i = 0; i < weight; i++) {
                chef.incrementComplimentCount();
            }
            chef.setLastCompliment(text != null ? text.trim() : "");

            if (loggedInCustomer != null) {
                loggedInCustomer.incrementComplimentCount();
            }
        }
    }

    private void collectDeliveryFeedback(DeliveryPerson delivery) {
        Object[] options = {"None", "Complaint", "Compliment"};
        int choice = JOptionPane.showOptionDialog(
                this,
                "Do you want to leave a complaint or compliment for the delivery person?",
                "Delivery Feedback",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );

        if (choice == 1) {
            String text = JOptionPane.showInputDialog(this, "Enter your complaint:");
            int weight = 1;
            if (loggedInCustomer != null && loggedInCustomer.isVip()) {
                weight = 2;
            }
            for (int i = 0; i < weight; i++) {
                delivery.incrementComplaintCount();
            }
            delivery.setLastComplaint(text != null ? text.trim() : "");
            if (loggedInCustomer != null) {
                loggedInCustomer.incrementComplaintCount();
            }
            updateDeliveryDemotionStatus(delivery);
        } else if (choice == 2) {
            String text = JOptionPane.showInputDialog(this, "Enter your compliment:");
            int weight = 1;
            if (loggedInCustomer != null && loggedInCustomer.isVip()) {
                weight = 2;
            }
            for (int i = 0; i < weight; i++) {
                delivery.incrementComplimentCount();
            }
            delivery.setLastCompliment(text != null ? text.trim() : "");

            if (loggedInCustomer != null) {
                loggedInCustomer.incrementComplimentCount();
            }
        }
    }

    private void updateChefStatusModel() {
        chefStatusModel.clear();
        for (Chef c : chefs) {
            chefStatusModel.addElement(formatChefStatus(c));
        }
    }

    private void updateDeliveryStatusModel() {
        deliveryStatusModel.clear();
        for (DeliveryPerson d : deliveryPeople) {
            deliveryStatusModel.addElement(formatDeliveryStatus(d));
        }
    }

    private void handleVoiceOrder() {
        String transcript = JOptionPane.showInputDialog(this, "Describe your order using natural language.");
        if (transcript == null) {
            return;
        }
        transcript = transcript.trim();
        if (transcript.isEmpty()) {
            return;
        }
        aiConversationArea.append("Voice order: " + transcript + "\n");
        StringBuilder menuNames = new StringBuilder();
        boolean vip = loggedInCustomer != null && loggedInCustomer.isVip();
        List<MenuItem> availableItems = new ArrayList<>();
        for (MenuItem item : menuItems) {
            if (!item.isVipOnly() || vip) {
                availableItems.add(item);
            }
        }
        for (int i = 0; i < availableItems.size(); i++) {
            if (i > 0) {
                menuNames.append(", ");
            }
            menuNames.append(availableItems.get(i).getName());
        }
        StringBuilder promptBuilder = new StringBuilder();
        promptBuilder.append("You are an ordering assistant for a restaurant.\n");
        promptBuilder.append("Menu items: ").append(menuNames).append(".\n");
        promptBuilder.append("The customer said: \"").append(transcript).append("\".\n");
        promptBuilder.append("Extract the order and respond only with lines in the exact format: item name x quantity. ");
        promptBuilder.append("Use only item names from the menu list and assume quantity 1 if not specified.\n");
        String prompt = promptBuilder.toString();
        new Thread(() -> {
            try {
                String response = callOllamaAPI(prompt);
                SwingUtilities.invokeLater(() -> {
                    aiConversationArea.append("AI order parse:\n" + response + "\n\n");
                    processVoiceOrderResponse(response);
                });
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this, "Could not process voice order.");
                });
            }
        }).start();
    }

    private void processVoiceOrderResponse(String response) {
        if (response == null) {
            return;
        }
        String trimmed = response.trim();
        if (trimmed.isEmpty()) {
            return;
        }
        String[] lines = trimmed.split("\\r?\\n");
        int addedCount = 0;
        boolean vip = loggedInCustomer != null && loggedInCustomer.isVip();
        for (String line : lines) {
            if (line == null) {
                continue;
            }
            line = line.trim();
            if (line.isEmpty()) {
                continue;
            }
            String[] parts = line.split("x");
            if (parts.length == 0) {
                continue;
            }
            String name = parts[0].trim();
            if (name.isEmpty()) {
                continue;
            }
            int quantity = 1;
            if (parts.length > 1) {
                try {
                    quantity = Integer.parseInt(parts[1].trim());
                } catch (NumberFormatException ex) {
                    quantity = 1;
                }
            }
            if (quantity < 1) {
                quantity = 1;
            }
            for (MenuItem item : menuItems) {
                if (item.isVipOnly() && !vip) {
                    continue;
                }
                if (item.getName().equalsIgnoreCase(name)) {
                    for (int i = 0; i < quantity; i++) {
                        cartModel.addElement(item);
                    }
                    addedCount += quantity;
                    break;
                }
            }
        }
        if (addedCount > 0) {
            updateCartTotal();
            JOptionPane.showMessageDialog(this, "Added " + addedCount + " item(s) to cart from voice order.");
        } else {
            JOptionPane.showMessageDialog(this, "No items from the menu were detected in the voice order.");
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
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/json");

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = payload.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        BufferedReader reader;
        if (connection.getResponseCode() >= 200 && connection.getResponseCode() < 300) {
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
    if (c.isSuspended()) {
        status = "Fired";
    } else if (c.getDemotionCount() > 0) {
        status = "Demoted" + c.getDemotionCount();
    } else {
        status = "Normal";
    }
    return String.format(
            "%-8s | Complaints:%d | Compliments:%d | Demotions:%d | Status:%-8s | Bonus:%2d",
            c.getName(),
            c.getComplaints(),
            c.getCompliments(),
            c.getDemotionCount(),
            status,
            c.getBonusCount()
    );
    }

    private String formatDeliveryStatus(DeliveryPerson d) {
    String status;
    if (d.isSuspended()) {
        status = "Fired";
    } else if (d.getDemotionCount() > 0) {
        status = "Demoted" + d.getDemotionCount();
    } else {
        status = "Normal";
    }
    return String.format(
            "%-8s | Complaints:%d | Compliments:%d | Demotions:%d | Status:%-8s | Bonus:%2d",
            d.getName(),
            d.getComplaints(),
            d.getCompliments(),
            d.getDemotionCount(),
            status,
            d.getBonusCount()
    );
    }

    private int effectiveComplaints(int complaints, int compliments) {
        int net = complaints - compliments;
        return Math.max(0, net);
    }

    private int effectiveCompliments(int complaints, int compliments) {
        int net = compliments - complaints;
        return Math.max(0, net);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            RestaurantApp app = new RestaurantApp();
            app.setVisible(true);
        });
    }
}

class MenuItem {
    private String name;
    private double price;
    private String chefName;
    private boolean vipOnly;

    public MenuItem(String name, double price, String chefName) {
        this.name = name;
        this.price = price;
        this.chefName = chefName;
        this.vipOnly = false;
    }

    public MenuItem(String name, double price, String chefName, boolean vipOnly) {
        this.name = name;
        this.price = price;
        this.chefName = chefName;
        this.vipOnly = vipOnly;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public String getChefName() {
        return chefName;
    }

    public boolean isVipOnly() {
        return vipOnly;
    }

    public String toString() {
        String tag = vipOnly ? "VIP" : "Ord";
        return String.format("%-20s $%6.2f  (%s, %s)", name, price, chefName, tag);
    }
}

class Chef {
    private String name;
    private String username;
    private String password;

    private int warningCount;
    private boolean suspended;
    private boolean fired;

    private int bonusCount;
    private int totalRating;
    private int ratingCount;

    private int complaintCount;
    private int complimentCount;
    private String lastComplaint;
    private String lastCompliment;

    private boolean demoted;
    private int demotionCount;
    private int highRatingCount;

    public Chef(String name, String username, String password) {
        this.name = name;
        this.username = username;
        this.password = password;
    }

    public void decideMenu(java.util.List<MenuItem> menuItems) {
        if (name.equals("Chef 1")) {
            menuItems.add(new MenuItem("Burger", 9.99, name));
            menuItems.add(new MenuItem("Fries", 3.99, name));
            menuItems.add(new MenuItem("Chef1 Special", 15.99, name, true));
        } else if (name.equals("Chef 2")) {
            menuItems.add(new MenuItem("Sushi", 12.99, name));
            menuItems.add(new MenuItem("Ramen", 10.49, name));
            menuItems.add(new MenuItem("Chef2 Special", 18.49, name, true));
        } else if (name.equals("Chef 3")) {
            menuItems.add(new MenuItem("Pasta", 11.99, name));
            menuItems.add(new MenuItem("Salad", 7.49, name));
            menuItems.add(new MenuItem("Chef3 Special", 16.99, name, true));
        }
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public int getWarningCount() {
        return warningCount;
    }

    public void incrementWarning() {
        warningCount++;
    }

    public boolean isSuspended() {
        return suspended;
    }

    public void setSuspended(boolean suspended) {
        this.suspended = suspended;
    }

    public boolean isFired() {
        return fired;
    }

    public void setFired(boolean fired) {
        this.fired = fired;
    }

    public int getHighRatingCount() {
        return highRatingCount;
    }

    public void incrementHighRatingCount() {
        highRatingCount++;
    }
    public int getBonusCount() {
        return bonusCount;
    }

    public void incrementBonus() {
        bonusCount++;
    }

    public void addRating(int rating) {
        totalRating += rating;
        ratingCount++;
    }

    public double getAverageRating() {
        if (ratingCount == 0) {
            return 0.0;
        }
        return (double) totalRating / ratingCount;
    }

    public int getComplaintCount() {
        return complaintCount;
    }

    public void incrementComplaintCount() {
        complaintCount++;
    }

    public int getComplimentCount() {
        return complimentCount;
    }

    public void incrementComplimentCount() {
        complimentCount++;
    }

    public String getLastComplaint() {
        return lastComplaint;
    }

    public void setLastComplaint(String lastComplaint) {
        this.lastComplaint = lastComplaint;
    }

    public String getLastCompliment() {
        return lastCompliment;
    }

    public void setLastCompliment(String lastCompliment) {
        this.lastCompliment = lastCompliment;
    }

    public boolean isDemoted() {
        return demoted;
    }

    public void setDemoted(boolean demoted) {
        this.demoted = demoted;
    }

    public int getDemotionCount() {
        return demotionCount;
    }

    public void incrementDemotionCount() {
        demotionCount++;
    }

    public int getComplaints() {
        return complaintCount;
    }

    public int getCompliments() {
        return complimentCount;
    }
}

class DeliveryPerson {
    private String name;

    private int warningCount;
    private boolean suspended;
    private boolean fired;

    private int totalRating;
    private int ratingCount;

    private int complaintCount;
    private int complimentCount;
    private String lastComplaint;
    private String lastCompliment;

    private boolean demoted;
    private int demotionCount;
    private int bonusCount;
    private int highRatingCount;

    private double lastBidPrice;
    private String lastJustificationMemo;

    public DeliveryPerson(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getWarningCount() {
        return warningCount;
    }

    public void incrementWarning() {
        warningCount++;
    }

    public boolean isSuspended() {
        return suspended;
    }

    public void setSuspended(boolean suspended) {
        this.suspended = suspended;
    }

    public boolean isFired() {
        return fired;
    }

    public void setFired(boolean fired) {
        this.fired = fired;
    }

    public int getHighRatingCount() {
        return highRatingCount;
    }

    public void incrementHighRatingCount() {
        highRatingCount++;
    }

    public void addRating(int rating) {
        totalRating += rating;
        ratingCount++;
    }

    public double getAverageRating() {
        if (ratingCount == 0) {
            return 0.0;
        }
        return (double) totalRating / ratingCount;
    }

    public int getComplaintCount() {
        return complaintCount;
    }

    public void incrementComplaintCount() {
        complaintCount++;
    }

    public int getComplimentCount() {
        return complimentCount;
    }

    public void incrementComplimentCount() {
        complimentCount++;
    }

    public String getLastComplaint() {
        return lastComplaint;
    }

    public void setLastComplaint(String lastComplaint) {
        this.lastComplaint = lastComplaint;
    }

    public String getLastCompliment() {
        return lastCompliment;
    }

    public void setLastCompliment(String lastCompliment) {
        this.lastCompliment = lastCompliment;
    }

    public boolean isDemoted() {
        return demoted;
    }

    public void setDemoted(boolean demoted) {
        this.demoted = demoted;
    }

    public int getDemotionCount() {
        return demotionCount;
    }

    public void incrementDemotionCount() {
        demotionCount++;
    }

    public int getBonusCount() {
        return bonusCount;
    }

    public void incrementBonusCount() {
        bonusCount++;
    }

    public double getLastBidPrice() {
        return lastBidPrice;
    }

    public void setLastBidPrice(double lastBidPrice) {
        this.lastBidPrice = lastBidPrice;
    }

    public String getLastJustificationMemo() {
        return lastJustificationMemo;
    }

    public void setLastJustificationMemo(String lastJustificationMemo) {
        this.lastJustificationMemo = lastJustificationMemo;
    }

    public int getComplaints() {
        return complaintCount;
    }

    public int getCompliments() {
        return complimentCount;
    }
}

class Customer {
    private String username;
    private String password;
    private boolean vip;
    private double totalSpent;
    private int orderCount;
    private int ordersSinceLastFreeDelivery;
    private int freeDeliveryCredits;
    private int complaintCount;
    private int complimentCount;

    public Customer(String username, String password) {
        this.username = username;
        this.password = password;
        this.vip = false;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public boolean isVip() {
        return vip;
    }

    public void setVip(boolean vip) {
        this.vip = vip;
    }

    public double getTotalSpent() {
        return totalSpent;
    }

    public int getOrderCount() {
        return orderCount;
    }

    public void recordOrder(double amount) {
        orderCount++;
        totalSpent += amount;
        ordersSinceLastFreeDelivery++;
        if (ordersSinceLastFreeDelivery >= 3) {
            freeDeliveryCredits++;
            ordersSinceLastFreeDelivery = 0;
        }
    }

    public int getFreeDeliveryCredits() {
        return freeDeliveryCredits;
    }

    public void useFreeDeliveryCredit() {
        if (freeDeliveryCredits > 0) {
            freeDeliveryCredits--;
        }
    }

    public int getComplaintCount() {
        return complaintCount;
    }

    public void incrementComplaintCount() {
        complaintCount++;
    }

    public int getComplimentCount() {
        return complimentCount;
    }

    public void incrementComplimentCount() {
        complimentCount++;
    }
}
