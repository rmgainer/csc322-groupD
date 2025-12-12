import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.OutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class RestaurantApp extends JFrame {
    private static final String MANAGER_USERNAME = "manager";
    private static final String MANAGER_PASSWORD = "password";

    private CardLayout cardLayout;
    private JPanel cardPanel;
    private JLabel balanceLabel;
    private double balance = 0.00;

    private JTextArea recommendationsArea;

    private DefaultListModel<MenuItem> menuModel = new DefaultListModel<>();
    private DefaultListModel<MenuItem> cartModel = new DefaultListModel<>();
    private JLabel cartTotalLabel;

    private JTextArea aiConversationArea;
    private JTextField aiInputField;

    private Chef loggedInChef;
    private JLabel chefLoginStatusLabel;

    private DeliveryPerson loggedInDelivery;
    private JLabel deliveryLoginStatusLabel;

    private boolean managerLoggedIn = false;
    private JLabel managerStatusLabel;

    private DefaultListModel<String> deliveryStatusModel = new DefaultListModel<>();

    private final List<MenuItem> menuItems = new ArrayList<>();
    private final List<Chef> chefs = new ArrayList<>();
    private final List<DeliveryPerson> deliveryPeople = new ArrayList<>();

    private final List<Chef> pendingChefs = new ArrayList<>();
    private final List<DeliveryPerson> pendingDeliveries = new ArrayList<>();

    private DefaultListModel<String> chefStatusModel = new DefaultListModel<>();
    private DefaultListModel<String> deliveryListModel = new DefaultListModel<>();

    private DefaultListModel<Chef> pendingChefModel = new DefaultListModel<>();
    private DefaultListModel<DeliveryPerson> pendingDeliveryModel = new DefaultListModel<>();

    private final List<Customer> customers = new ArrayList<>();
    private final List<Customer> pendingCustomers = new ArrayList<>();
    private DefaultListModel<Customer> pendingCustomerModel = new DefaultListModel<>();
    private Customer loggedInCustomer;
    private JLabel customerStatusLabel;
    private JLabel customerWarningLabel;

    private final List<Order> pendingOrders = new ArrayList<>();
    private DefaultListModel<Order> orderModel = new DefaultListModel<>();
    private int nextOrderId = 1;

    private Customer feedbackCustomer;
    private DeliveryPerson feedbackDelivery;
    private JLabel feedbackCustomerLabel;
    private JComboBox<String> feedbackTypeCombo;
    private JCheckBox feedbackDisputeCheck;
    private JTextArea feedbackTextArea;

    private final List<CustomerFeedback> pendingCustomerFeedback = new ArrayList<>();
    private DefaultListModel<CustomerFeedback> pendingCustomerFeedbackModel = new DefaultListModel<>();

    private final List<KnowledgeEntry> knowledgeBase = new ArrayList<>();

    private final List<DeliveryPerson> currentBidDeliveryList = new ArrayList<>();

    public RestaurantApp() {
        setTitle("Restaurant Management System Demo");
        setSize(1200, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        initializeData();
        initializeKnowledgeBase();

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        JPanel homePanel = createHomePanel();
        JPanel menuPanel = createMenuPanel();
        JPanel cartPanel = createCartPanel();
        JPanel staffPanel = createStaffPanel();
        JPanel chefPanel = createChefPanel();
        JPanel aiPanel = createAIChatPanel();
        JPanel customerPanel = createCustomerPanel();
        JPanel deliveryPanel = createDeliveryPanel();
        JPanel managerPanel = createManagerPanel();

        cardPanel.add(homePanel, "HOME");
        cardPanel.add(menuPanel, "MENU");
        cardPanel.add(cartPanel, "CART");
        cardPanel.add(staffPanel, "STAFF");
        cardPanel.add(chefPanel, "CHEF");
        cardPanel.add(aiPanel, "AI");
        cardPanel.add(customerPanel, "CUSTOMER");
        cardPanel.add(deliveryPanel, "DELIVERY");
        cardPanel.add(managerPanel, "MANAGER");

        setLayout(new BorderLayout());

        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        JButton homeButton = new JButton("Home");
        JButton menuButton = new JButton("Menu");
        JButton cartButton = new JButton("Cart");
        JButton staffButton = new JButton("Staff");
        JButton chefButton = new JButton("Chef Login");
        JButton aiButton = new JButton("AI Helper");
        JButton customerButton = new JButton("Customer Login");
        JButton deliveryButton = new JButton("Delivery Login");
        JButton managerButton = new JButton("Manager");

        homeButton.addActionListener(e -> cardLayout.show(cardPanel, "HOME"));
        menuButton.addActionListener(e -> {
            refreshMenuModel();
            cardLayout.show(cardPanel, "MENU");
        });
        cartButton.addActionListener(e -> cardLayout.show(cardPanel, "CART"));
        staffButton.addActionListener(e -> {
            updateChefStatusModel();
            updateDeliveryStatusModel();
            cardLayout.show(cardPanel, "STAFF");
        });
        chefButton.addActionListener(e -> cardLayout.show(cardPanel, "CHEF"));
        aiButton.addActionListener(e -> cardLayout.show(cardPanel, "AI"));
        customerButton.addActionListener(e -> cardLayout.show(cardPanel, "CUSTOMER"));
        deliveryButton.addActionListener(e -> cardLayout.show(cardPanel, "DELIVERY"));
        managerButton.addActionListener(e -> cardLayout.show(cardPanel, "MANAGER"));

        navPanel.add(homeButton);
        navPanel.add(menuButton);
        navPanel.add(cartButton);
        navPanel.add(staffButton);
        navPanel.add(chefButton);
        navPanel.add(aiButton);
        navPanel.add(customerButton);
        navPanel.add(deliveryButton);
        navPanel.add(managerButton);

        JPanel balancePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        balanceLabel = new JLabel("Balance: $0.00");
        JButton depositButton = new JButton("Deposit");
        depositButton.addActionListener(e -> {
            if (loggedInCustomer != null) {
                try {
                    String input = JOptionPane.showInputDialog("Please enter the amount you would like to deposit ($):");
                    float inputFloat = Float.parseFloat(input);
                    balance += Math.round(inputFloat * 100.0f) / 100.0f;
                    updateBalanceLabel();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Unknown error occurred.", "ERROR", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "You have to be a registered customer to deposit money.");
            }
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

        deliveryPeople.add(new DeliveryPerson("Del 1", "del1", "password1"));
        deliveryPeople.add(new DeliveryPerson("Del 2", "del2", "password2"));
        deliveryPeople.add(new DeliveryPerson("Del 3", "del3", "password3"));
    }

    private void initializeKnowledgeBase() {
        knowledgeBase.clear();
        knowledgeBase.add(new KnowledgeEntry(
                "vip",
                "VIP customers get 5% discount and special dishes. You become VIP after enough spending or several good orders.",
                "system"
        ));
        knowledgeBase.add(new KnowledgeEntry(
                "warning",
                "Normal customers are deregistered after 3 warnings. VIP customers lose VIP after 2 warnings.",
                "system"
        ));
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
                        "- Delivery workers can register/login to bid on orders.\n" +
                        "- Manager approves customer/chef/delivery registrations.\n" +
                        "- Manager chooses the final delivery worker after bidding.\n" +
                        "- Use the AI helper for natural language assistance and voice-style ordering."
        );

        recommendationsArea = new JTextArea();
        recommendationsArea.setEditable(false);
        recommendationsArea.setLineWrap(true);
        recommendationsArea.setWrapStyleWord(true);
        recommendationsArea.setFont(new Font("SansSerif", Font.PLAIN, 14));

        JPanel centerPanel = new JPanel(new BorderLayout(0, 15));
        centerPanel.add(new JScrollPane(description), BorderLayout.NORTH);
        centerPanel.add(new JScrollPane(recommendationsArea), BorderLayout.CENTER);

        panel.add(title, BorderLayout.NORTH);
        panel.add(centerPanel, BorderLayout.CENTER);

        updateRecommendationsArea();

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

    private DeliveryPerson findDeliveryByUsername(String username) {
        for (DeliveryPerson d : deliveryPeople) {
            if (username.equals(d.getUsername())) {
                return d;
            }
        }
        return null;
    }

    private JPanel createChefPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel loginPanel = new JPanel(new GridLayout(5, 2, 5, 5));
        loginPanel.setBorder(BorderFactory.createTitledBorder("Chef Login / Register"));

        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();

        chefLoginStatusLabel = new JLabel("Not logged in");

        JButton loginButton = new JButton("Login");
        JButton registerButton = new JButton("Register");
        JButton logoutButton = new JButton("Logout");

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

        registerButton.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();
            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Username and password required.");
                return;
            }
            if (findChefByUsername(username) != null) {
                JOptionPane.showMessageDialog(this, "Username already exists.");
                return;
            }
            for (Chef c : pendingChefs) {
                if (c.getUsername().equals(username)) {
                    JOptionPane.showMessageDialog(this, "This username already has a pending request.");
                    return;
                }
            }
            Chef newChef = new Chef(username, username, password);
            pendingChefs.add(newChef);
            pendingChefModel.addElement(newChef);
            JOptionPane.showMessageDialog(this, "Registration submitted. Waiting for manager approval.");
        });

        logoutButton.addActionListener(e -> {
            loggedInChef = null;
            chefLoginStatusLabel.setText("Not logged in");
        });

        loginPanel.add(new JLabel("Username:"));
        loginPanel.add(usernameField);
        loginPanel.add(new JLabel("Password:"));
        loginPanel.add(passwordField);
        loginPanel.add(loginButton);
        loginPanel.add(registerButton);
        loginPanel.add(new JLabel("Status:"));
        loginPanel.add(chefLoginStatusLabel);
        loginPanel.add(new JLabel(""));
        loginPanel.add(logoutButton);

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
        customerWarningLabel = new JLabel("Warnings: 0");

        loginButton.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();
            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Username and password required.");
                return;
            }
            Customer c = findCustomerByUsername(username);
            if (c != null && c.getPassword().equals(password)) {
                if (c.isDeregistered()) {
                    JOptionPane.showMessageDialog(this, "Your account has been deregistered and you cannot log in.");
                    return;
                }
                loggedInCustomer = c;
                updateCustomerStatusLabel();
                updateCustomerWarningLabel();
                refreshMenuModel();
                updateRecommendationsArea();

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
            for (Customer pc : pendingCustomers) {
                if (pc.getUsername().equals(username)) {
                    JOptionPane.showMessageDialog(this, "This username already has a pending registration.");
                    return;
                }
            }
            Customer c = new Customer(username, password);
            pendingCustomers.add(c);
            pendingCustomerModel.addElement(c);
            JOptionPane.showMessageDialog(this, "Registration submitted. Waiting for manager approval.");
        });

        loginPanel.add(new JLabel("Username:"));
        loginPanel.add(usernameField);
        loginPanel.add(new JLabel("Password:"));
        loginPanel.add(passwordField);
        loginPanel.add(loginButton);
        loginPanel.add(registerButton);

        JPanel statusPanel = new JPanel();
        statusPanel.setBorder(BorderFactory.createTitledBorder("Status"));
        statusPanel.setLayout(new GridLayout(3, 1));
        statusPanel.add(customerStatusLabel);
        statusPanel.add(customerWarningLabel);
        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> {
            loggedInCustomer = null;
            updateCustomerStatusLabel();
            updateCustomerWarningLabel();
            refreshMenuModel();
            updateRecommendationsArea();
        });
        statusPanel.add(logoutButton);

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
            String type;
            if (loggedInCustomer.isDeregistered()) {
                type = "Deregistered";
            } else {
                type = loggedInCustomer.isVip() ? "VIP Customer" : "Registered Customer";
            }
            customerStatusLabel.setText("Logged in as " + loggedInCustomer.getUsername() + " (" + type + ")");
        }
    }

    private void updateCustomerWarningLabel() {
        if (customerWarningLabel == null) {
            return;
        }
        int warnings = 0;
        if (loggedInCustomer != null) {
            warnings = loggedInCustomer.getWarningCount();
        }
        customerWarningLabel.setText("Warnings: " + warnings);
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

    private JPanel createDeliveryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel loginPanel = new JPanel(new GridLayout(5, 2, 5, 5));
        loginPanel.setBorder(BorderFactory.createTitledBorder("Delivery Login / Register"));

        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();

        JButton loginButton = new JButton("Login");
        JButton registerButton = new JButton("Register");
        JButton logoutButton = new JButton("Logout");

        deliveryLoginStatusLabel = new JLabel("Not logged in");

        loginButton.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();
            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Username and password required.");
                return;
            }
            DeliveryPerson d = findDeliveryByUsername(username);
            if (d != null && password.equals(d.getPassword())) {
                if (d.isFired()) {
                    JOptionPane.showMessageDialog(this, "This delivery worker has been fired.");
                    return;
                }
                loggedInDelivery = d;
                deliveryLoginStatusLabel.setText("Logged in as " + d.getName());
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
            if (findDeliveryByUsername(username) != null) {
                JOptionPane.showMessageDialog(this, "Username already exists.");
                return;
            }
            for (DeliveryPerson d : pendingDeliveries) {
                if (username.equals(d.getUsername())) {
                    JOptionPane.showMessageDialog(this, "This username already has a pending request.");
                    return;
                }
            }
            DeliveryPerson newDel = new DeliveryPerson(username, username, password);
            pendingDeliveries.add(newDel);
            pendingDeliveryModel.addElement(newDel);
            JOptionPane.showMessageDialog(this, "Registration submitted. Waiting for manager approval.");
        });

        logoutButton.addActionListener(e -> {
            loggedInDelivery = null;
            deliveryLoginStatusLabel.setText("Not logged in");
            feedbackCustomer = null;
            feedbackDelivery = null;
            if (feedbackCustomerLabel != null) {
                feedbackCustomerLabel.setText("No completed order selected.");
            }
        });

        loginPanel.add(new JLabel("Username:"));
        loginPanel.add(usernameField);
        loginPanel.add(new JLabel("Password:"));
        loginPanel.add(passwordField);
        loginPanel.add(loginButton);
        loginPanel.add(registerButton);
        loginPanel.add(new JLabel("Status:"));
        loginPanel.add(deliveryLoginStatusLabel);
        loginPanel.add(new JLabel(""));
        loginPanel.add(logoutButton);

        panel.add(loginPanel, BorderLayout.NORTH);

        JPanel ordersPanel = new JPanel(new BorderLayout());
        ordersPanel.setBorder(BorderFactory.createTitledBorder("Pending Orders"));

        JList<Order> orderList = new JList<>(orderModel);
        orderList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        orderList.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane orderScroll = new JScrollPane(orderList);

        JButton bidButton = new JButton("Place Bid on Selected Order");
        JButton completeButton = new JButton("Complete Selected Order (Deliver)");

        bidButton.addActionListener(e -> {
            if (loggedInDelivery == null) {
                JOptionPane.showMessageDialog(this, "Please log in as a delivery worker first.");
                return;
            }
            Order selected = orderList.getSelectedValue();
            if (selected == null) {
                JOptionPane.showMessageDialog(this, "Please select an order.");
                return;
            }
            String input = JOptionPane.showInputDialog(this, "Enter your bid price for this order:");
            if (input == null || input.trim().isEmpty()) {
                return;
            }
            try {
                double price = Double.parseDouble(input.trim());
                if (price <= 0) {
                    JOptionPane.showMessageDialog(this, "Bid price must be positive.");
                    return;
                }
                boolean isNewBest = selected.placeBid(loggedInDelivery, price);
                orderList.repaint();
                if (isNewBest) {
                    JOptionPane.showMessageDialog(this,
                            "Your bid is currently the lowest.\n" +
                                    "Final delivery worker will be chosen by the manager.");
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Bid placed.\nFinal delivery worker will be chosen by the manager.");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid price.");
            }
        });

        completeButton.addActionListener(e -> {
            if (loggedInDelivery == null) {
                JOptionPane.showMessageDialog(this, "Please log in as a delivery worker first.");
                return;
            }
            Order selected = orderList.getSelectedValue();
            if (selected == null) {
                JOptionPane.showMessageDialog(this, "Please select an order.");
                return;
            }
            if (selected.getAssignedDelivery() == null || selected.getAssignedDelivery() != loggedInDelivery) {
                JOptionPane.showMessageDialog(this, "Only the assigned delivery worker can complete this order.");
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Confirm that Order #" + selected.getId() + " has been delivered?",
                    "Complete Order",
                    JOptionPane.YES_NO_OPTION
            );
            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }

            Customer orderCustomer = selected.getCustomer();

            int dishRating = askForRating("Rate the dishes in your order from 1 to 5 (cancel to skip):");
            if (dishRating > 0) {
                for (MenuItem item : selected.getItems()) {
                    item.incrementOrderCount();
                    item.addRating(dishRating);
                    if (orderCustomer != null) {
                        orderCustomer.recordDishOrder(item, dishRating);
                    }
                }
            } else {
                for (MenuItem item : selected.getItems()) {
                    item.incrementOrderCount();
                    if (orderCustomer != null) {
                        orderCustomer.recordDishOrder(item, 0);
                    }
                }
            }

            rateStaff(selected.getChef(), loggedInDelivery);

            pendingOrders.remove(selected);
            orderModel.removeElement(selected);

            if (orderCustomer != null && orderCustomer == loggedInCustomer) {
                updateRecommendationsArea();
            }

            if (orderCustomer != null) {
                feedbackCustomer = orderCustomer;
                feedbackDelivery = loggedInDelivery;
                feedbackCustomerLabel.setText("Customer for feedback: " + orderCustomer.getUsername() +
                        " (Order #" + selected.getId() + ")");
                feedbackTypeCombo.setSelectedIndex(0);
                feedbackDisputeCheck.setSelected(false);
                feedbackTextArea.setText("");
            }
        });

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.add(bidButton);
        btnPanel.add(completeButton);

        ordersPanel.add(orderScroll, BorderLayout.CENTER);
        ordersPanel.add(btnPanel, BorderLayout.SOUTH);

        JPanel feedbackPanel = new JPanel(new BorderLayout());
        feedbackPanel.setBorder(BorderFactory.createTitledBorder("Customer Feedback (after delivery)"));

        feedbackCustomerLabel = new JLabel("No completed order selected.");
        feedbackPanel.add(feedbackCustomerLabel, BorderLayout.NORTH);

        JPanel feedbackTop = new JPanel(new GridLayout(1, 2, 5, 5));
        feedbackTypeCombo = new JComboBox<>(new String[]{"None", "Complaint", "Compliment"});

        feedbackTop.add(new JLabel("Feedback type:"));
        feedbackTop.add(feedbackTypeCombo);

        feedbackTextArea = new JTextArea(3, 20);
        JScrollPane textScroll = new JScrollPane(feedbackTextArea);

        feedbackDisputeCheck = new JCheckBox("Customer disputes complaint");

        JButton submitFeedbackButton = new JButton("Submit Feedback");
        submitFeedbackButton.addActionListener(e -> submitCustomerFeedbackFromDelivery());

        JPanel feedbackButtons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        feedbackButtons.add(feedbackDisputeCheck);
        feedbackButtons.add(submitFeedbackButton);

        JPanel feedbackBottom = new JPanel(new BorderLayout());
        feedbackBottom.add(new JLabel("Details:"), BorderLayout.NORTH);
        feedbackBottom.add(textScroll, BorderLayout.CENTER);
        feedbackBottom.add(feedbackButtons, BorderLayout.SOUTH);

        feedbackPanel.add(feedbackTop, BorderLayout.CENTER);
        feedbackPanel.add(feedbackBottom, BorderLayout.SOUTH);

        JPanel center = new JPanel(new BorderLayout());
        center.add(ordersPanel, BorderLayout.CENTER);
        center.add(feedbackPanel, BorderLayout.SOUTH);

        panel.add(center, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createManagerPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel loginPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        loginPanel.setBorder(BorderFactory.createTitledBorder("Manager Login"));

        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JButton loginButton = new JButton("Login");
        JButton logoutButton = new JButton("Logout");

        managerStatusLabel = new JLabel("Not logged in");

        loginButton.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();
            if (MANAGER_USERNAME.equals(username) && MANAGER_PASSWORD.equals(password)) {
                managerLoggedIn = true;
                managerStatusLabel.setText("Logged in as Manager");
            } else {
                JOptionPane.showMessageDialog(this, "Invalid manager credentials.");
            }
        });

        logoutButton.addActionListener(e -> {
            managerLoggedIn = false;
            managerStatusLabel.setText("Not logged in");
        });

        loginPanel.add(new JLabel("Username:"));
        loginPanel.add(usernameField);
        loginPanel.add(new JLabel("Password:"));
        loginPanel.add(passwordField);
        loginPanel.add(loginButton);
        loginPanel.add(new JLabel(""));
        loginPanel.add(new JLabel("Status:"));
        loginPanel.add(managerStatusLabel);
        loginPanel.add(new JLabel(""));
        loginPanel.add(logoutButton);

        panel.add(loginPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridLayout(2, 2, 10, 10));

        JPanel chefPanel = new JPanel(new BorderLayout());
        chefPanel.setBorder(BorderFactory.createTitledBorder("Pending Chef Registrations"));
        JList<Chef> pendingChefList = new JList<>(pendingChefModel);
        pendingChefList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane chefScroll = new JScrollPane(pendingChefList);
        JButton approveChef = new JButton("Approve");
        JButton rejectChef = new JButton("Reject");
        approveChef.addActionListener(e -> {
            if (!managerLoggedIn) {
                JOptionPane.showMessageDialog(this, "Manager must log in first.");
                return;
            }
            Chef c = pendingChefList.getSelectedValue();
            if (c == null) return;
            pendingChefs.remove(c);
            pendingChefModel.removeElement(c);
            chefs.add(c);
            updateChefStatusModel();
            JOptionPane.showMessageDialog(this, "Chef approved: " + c.getName());
        });
        rejectChef.addActionListener(e -> {
            if (!managerLoggedIn) {
                JOptionPane.showMessageDialog(this, "Manager must log in first.");
                return;
            }
            Chef c = pendingChefList.getSelectedValue();
            if (c == null) return;
            pendingChefs.remove(c);
            pendingChefModel.removeElement(c);
            JOptionPane.showMessageDialog(this, "Chef registration rejected.");
        });
        JPanel chefBtnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        chefBtnPanel.add(approveChef);
        chefBtnPanel.add(rejectChef);
        chefPanel.add(chefScroll, BorderLayout.CENTER);
        chefPanel.add(chefBtnPanel, BorderLayout.SOUTH);

        JPanel delPanel = new JPanel(new BorderLayout());
        delPanel.setBorder(BorderFactory.createTitledBorder("Pending Delivery Registrations"));
        JList<DeliveryPerson> pendingDelList = new JList<>(pendingDeliveryModel);
        pendingDelList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane delScroll = new JScrollPane(pendingDelList);
        JButton approveDel = new JButton("Approve");
        JButton rejectDel = new JButton("Reject");
        approveDel.addActionListener(e -> {
            if (!managerLoggedIn) {
                JOptionPane.showMessageDialog(this, "Manager must log in first.");
                return;
            }
            DeliveryPerson d = pendingDelList.getSelectedValue();
            if (d == null) return;
            pendingDeliveries.remove(d);
            pendingDeliveryModel.removeElement(d);
            deliveryPeople.add(d);
            updateDeliveryStatusModel();
            JOptionPane.showMessageDialog(this, "Delivery worker approved: " + d.getName());
        });
        rejectDel.addActionListener(e -> {
            if (!managerLoggedIn) {
                JOptionPane.showMessageDialog(this, "Manager must log in first.");
                return;
            }
            DeliveryPerson d = pendingDelList.getSelectedValue();
            if (d == null) return;
            pendingDeliveries.remove(d);
            pendingDeliveryModel.removeElement(d);
            JOptionPane.showMessageDialog(this, "Delivery registration rejected.");
        });
        JPanel delBtnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        delBtnPanel.add(approveDel);
        delBtnPanel.add(rejectDel);
        delPanel.add(delScroll, BorderLayout.CENTER);
        delPanel.add(delBtnPanel, BorderLayout.SOUTH);

        JPanel complaintPanel = new JPanel(new BorderLayout());
        complaintPanel.setBorder(BorderFactory.createTitledBorder("Pending Customer Complaints (from delivery)"));
        JList<CustomerFeedback> pendingFeedbackList = new JList<>(pendingCustomerFeedbackModel);
        pendingFeedbackList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane cfScroll = new JScrollPane(pendingFeedbackList);
        JButton markValid = new JButton("Mark Valid");
        JButton markInvalid = new JButton("Mark Invalid");
        markValid.addActionListener(e -> {
            if (!managerLoggedIn) {
                JOptionPane.showMessageDialog(this, "Manager must log in first.");
                return;
            }
            CustomerFeedback fb = pendingFeedbackList.getSelectedValue();
            if (fb == null) return;
            if (!"Complaint".equals(fb.getType())) return;
            Customer c = fb.getCustomer();
            if (c != null) {
                c.incrementComplaintCount();
                c.incrementWarningCount();
                handleCustomerWarningUpdate(c);
            }
            pendingCustomerFeedback.remove(fb);
            pendingCustomerFeedbackModel.removeElement(fb);
            JOptionPane.showMessageDialog(this, "Complaint marked as VALID. Customer warning updated.");
        });
        markInvalid.addActionListener(e -> {
            if (!managerLoggedIn) {
                JOptionPane.showMessageDialog(this, "Manager must log in first.");
                return;
            }
            CustomerFeedback fb = pendingFeedbackList.getSelectedValue();
            if (fb == null) return;
            if (!"Complaint".equals(fb.getType())) return;
            DeliveryPerson d = fb.getDelivery();
            if (d != null) {
                d.incrementWarning();
                updateDeliveryDemotionStatus(d);
                updateDeliveryStatusModel();
            }
            pendingCustomerFeedback.remove(fb);
            pendingCustomerFeedbackModel.removeElement(fb);
            JOptionPane.showMessageDialog(this, "Complaint marked as INVALID. Delivery warning updated.");
        });
        JPanel cfBtnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        cfBtnPanel.add(markValid);
        cfBtnPanel.add(markInvalid);
        complaintPanel.add(cfScroll, BorderLayout.CENTER);
        complaintPanel.add(cfBtnPanel, BorderLayout.SOUTH);

        JPanel customerPanel = new JPanel(new BorderLayout());
        customerPanel.setBorder(BorderFactory.createTitledBorder("Pending Customer Registrations"));
        JList<Customer> pendingCustomerList = new JList<>(pendingCustomerModel);
        pendingCustomerList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane custScroll = new JScrollPane(pendingCustomerList);
        JButton approveCust = new JButton("Approve");
        JButton rejectCust = new JButton("Reject");
        approveCust.addActionListener(e -> {
            if (!managerLoggedIn) {
                JOptionPane.showMessageDialog(this, "Manager must log in first.");
                return;
            }
            Customer c = pendingCustomerList.getSelectedValue();
            if (c == null) return;
            pendingCustomers.remove(c);
            pendingCustomerModel.removeElement(c);
            customers.add(c);
            JOptionPane.showMessageDialog(this, "Customer approved: " + c.getUsername());
        });
        rejectCust.addActionListener(e -> {
            if (!managerLoggedIn) {
                JOptionPane.showMessageDialog(this, "Manager must log in first.");
                return;
            }
            Customer c = pendingCustomerList.getSelectedValue();
            if (c == null) return;
            pendingCustomers.remove(c);
            pendingCustomerModel.removeElement(c);
            JOptionPane.showMessageDialog(this, "Customer registration rejected.");
        });
        JPanel custBtnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        custBtnPanel.add(approveCust);
        custBtnPanel.add(rejectCust);
        customerPanel.add(custScroll, BorderLayout.CENTER);
        customerPanel.add(custBtnPanel, BorderLayout.SOUTH);

        centerPanel.add(chefPanel);
        centerPanel.add(delPanel);
        centerPanel.add(complaintPanel);
        centerPanel.add(customerPanel);

        panel.add(centerPanel, BorderLayout.CENTER);

        JPanel biddingPanel = new JPanel(new BorderLayout());
        biddingPanel.setBorder(BorderFactory.createTitledBorder("Order Bidding (Manager chooses winner)"));

        JList<Order> orderBidList = new JList<>(orderModel);
        orderBidList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        orderBidList.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane orderBidScroll = new JScrollPane(orderBidList);

        DefaultListModel<String> bidListModel = new DefaultListModel<>();
        JList<String> bidList = new JList<>(bidListModel);
        bidList.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane bidScroll = new JScrollPane(bidList);

        orderBidList.addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            Order selected = orderBidList.getSelectedValue();
            bidListModel.clear();
            currentBidDeliveryList.clear();
            if (selected != null) {
                Map<DeliveryPerson, Double> bids = selected.getBids();
                for (Map.Entry<DeliveryPerson, Double> entry : bids.entrySet()) {
                    DeliveryPerson d = entry.getKey();
                    Double price = entry.getValue();
                    currentBidDeliveryList.add(d);
                    bidListModel.addElement(d.getName() + " - $" + String.format("%.2f", price));
                }
            }
        });

        JSplitPane bidSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, orderBidScroll, bidScroll);
        bidSplit.setResizeWeight(0.5);

        JButton assignBidButton = new JButton("Assign Selected Delivery to Order");
        assignBidButton.addActionListener(e -> {
            if (!managerLoggedIn) {
                JOptionPane.showMessageDialog(this, "Manager must log in first.");
                return;
            }
            Order selectedOrder = orderBidList.getSelectedValue();
            int selectedIndex = bidList.getSelectedIndex();
            if (selectedOrder == null) {
                JOptionPane.showMessageDialog(this, "Please select an order.");
                return;
            }
            if (selectedIndex < 0 || selectedIndex >= currentBidDeliveryList.size()) {
                JOptionPane.showMessageDialog(this, "Please select a bid.");
                return;
            }
            DeliveryPerson chosen = currentBidDeliveryList.get(selectedIndex);
            selectedOrder.assignDelivery(chosen);
            orderBidList.repaint();
            JOptionPane.showMessageDialog(this,
                    "Order #" + selectedOrder.getId() + " assigned to " + chosen.getName() + ".");
        });

        biddingPanel.add(bidSplit, BorderLayout.CENTER);
        biddingPanel.add(assignBidButton, BorderLayout.SOUTH);

        panel.add(biddingPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void handleCustomerWarningUpdate(Customer c) {
        if (c.isVip()) {
            if (c.getWarningCount() >= 2) {
                c.setVip(false);
                c.setWarningCount(0);
                c.resetVipProgress();
                JOptionPane.showMessageDialog(this,
                        "VIP reached 2 warnings and is downgraded.\n" +
                                "All VIP progress is reset and must start over.");
                if (loggedInCustomer == c) {
                    updateCustomerStatusLabel();
                    updateCustomerWarningLabel();
                    refreshMenuModel();
                }
            } else {
                if (loggedInCustomer == c) {
                    updateCustomerWarningLabel();
                }
            }
            return;
        }

        if (!c.isVip()) {
            if (c.getWarningCount() >= 3 && !c.isDeregistered()) {
                c.setDeregistered(true);

                JOptionPane.showMessageDialog(this,
                        "Customer reached 3 warnings and is deregistered.");

                if (loggedInCustomer == c) {
                    loggedInCustomer = null;
                    updateCustomerStatusLabel();
                    updateCustomerWarningLabel();
                    refreshMenuModel();
                }
            } else {
                if (loggedInCustomer == c) {
                    updateCustomerWarningLabel();
                }
            }
        }
    }

    private void handleCheckout(java.awt.event.ActionEvent e) {
        if (cartModel.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Your cart is empty.");
            return;
        }

        if (loggedInCustomer == null) {
            JOptionPane.showMessageDialog(this, "You have to be a registered customer to do following action.");
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
            if (loggedInCustomer != null) {
                loggedInCustomer.incrementWarningCount();
                handleCustomerWarningUpdate(loggedInCustomer);
            }
            JOptionPane.showMessageDialog(this, "Insufficient balance. Please deposit more money.");
            return;
        }

        Chef selectedChef = chooseChefForOrder();
        if (selectedChef == null) {
            return;
        }

        if (selectedChef.isFired()) {
            JOptionPane.showMessageDialog(this, "Selected chef has been fired. Please choose another.");
            return;
        }

        List<MenuItem> orderItems = new ArrayList<>();
        for (int i = 0; i < cartModel.size(); i++) {
            orderItems.add(cartModel.getElementAt(i));
        }

        Order order = new Order(nextOrderId++, loggedInCustomer, selectedChef, total, orderItems);
        pendingOrders.add(order);
        orderModel.addElement(order);

        balance -= total;
        updateBalanceLabel();

        String msg = "Order created and waiting for delivery bids.\n"
                + "Order ID: #" + order.getId();
        if (vipNow) {
            msg += "\nVIP 5% discount applied.";
        }
        if (usedFreeDelivery) {
            msg += "\nFree delivery credit used.";
        }
        JOptionPane.showMessageDialog(this, msg);

        if (loggedInCustomer != null) {
            loggedInCustomer.recordOrder(originalTotal);
            if (!loggedInCustomer.isVip()) {
                if (loggedInCustomer.getTotalSpent() > 100.0 ||
                        (loggedInCustomer.getComplaintCount() == 0 && loggedInCustomer.getOrderCount() >= 3)) {
                    loggedInCustomer.setVip(true);
                    updateCustomerStatusLabel();
                    JOptionPane.showMessageDialog(this, "Congratulations! You are now a VIP customer.");
                    refreshMenuModel();
                }
            }
        }

        cartModel.clear();
        updateCartTotal();
        updateRecommendationsArea();
    }

    private Chef chooseChefForOrder() {
        List<Chef> availableChefs = new ArrayList<>();
        for (Chef chef : chefs) {
            if (!chef.isFired()) {
                availableChefs.add(chef);
            }
        }
        if (availableChefs.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No available chefs.");
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
            chef.setSalary(chef.getSalary() * 0.85);
        }

        chef.setDemoted(chef.getDemotionCount() > 0);

        if (chef.getDemotionCount() >= 2) {
            chef.setFired(true);
        }
    }

    private void updateDeliveryDemotionStatus(DeliveryPerson d) {
        int demotionsFromWarnings = d.getWarningCount() / 3;
        int netComplaints = effectiveComplaints(d.getComplaintCount(), d.getComplimentCount());
        int demotionsFromComplaints = netComplaints / 3;

        int targetDemotions = demotionsFromWarnings + demotionsFromComplaints;

        while (d.getDemotionCount() < targetDemotions) {
            d.incrementDemotionCount();
            d.setSalary(d.getSalary() * 0.85);
        }

        d.setDemoted(d.getDemotionCount() > 0);

        if (d.getDemotionCount() >= 2) {
            d.setFired(true);
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
            chef.setSalary(chef.getSalary() * 1.15);
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
            d.setSalary(d.getSalary() * 1.15);
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
            delivery.incrementWarning();
            updateDeliveryDemotionStatus(delivery);
            updateDeliveryStatusModel();
            JOptionPane.showMessageDialog(this, "Complaint recorded for the delivery person.");
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

    private void submitCustomerFeedbackFromDelivery() {
        if (loggedInDelivery == null) {
            JOptionPane.showMessageDialog(this, "Please log in as a delivery worker first.");
            return;
        }
        if (feedbackCustomer == null || feedbackDelivery == null) {
            JOptionPane.showMessageDialog(this, "There is no completed order waiting for customer feedback.");
            return;
        }
        if (feedbackDelivery != loggedInDelivery) {
            JOptionPane.showMessageDialog(this, "Only the delivery worker who completed the order can submit feedback.");
            return;
        }

        String type = (String) feedbackTypeCombo.getSelectedItem();
        if (type == null || "None".equals(type)) {
            JOptionPane.showMessageDialog(this, "Please choose Complaint or Compliment.");
            return;
        }

        String text = feedbackTextArea.getText().trim();
        boolean disputed = feedbackDisputeCheck.isSelected();

        if ("Complaint".equals(type)) {
            CustomerFeedback fb = new CustomerFeedback(feedbackCustomer, feedbackDelivery, type, text, disputed);
            pendingCustomerFeedback.add(fb);
            pendingCustomerFeedbackModel.addElement(fb);
            JOptionPane.showMessageDialog(this, "Complaint submitted and waiting for manager decision.");
        } else if ("Compliment".equals(type)) {
            feedbackCustomer.incrementComplimentCount();
            JOptionPane.showMessageDialog(this, "Compliment recorded for the customer.");
        }

        feedbackTypeCombo.setSelectedIndex(0);
        feedbackDisputeCheck.setSelected(false);
        feedbackTextArea.setText("");
        feedbackCustomer = null;
        feedbackDelivery = null;
        feedbackCustomerLabel.setText("No completed order selected.");
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

        KnowledgeEntry kbEntry = findKnowledgeEntry(userMessage);
        if (kbEntry != null) {
            String answer = kbEntry.getAnswer();
            aiConversationArea.append("AI (KB): " + answer + "\n\n");
            int rating = askForKBAnswerRating();
            if (rating == 0) {
                kbEntry.setFlagged(true);
            }
        } else {
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
    }

    private KnowledgeEntry findKnowledgeEntry(String question) {
        if (question == null) return null;
        String q = question.toLowerCase();
        KnowledgeEntry best = null;
        for (KnowledgeEntry entry : knowledgeBase) {
            if (entry.isRemoved()) continue;
            if (entry.isAuthorBanned()) continue;
            String key = entry.getKeyword();
            if (key == null || key.isEmpty()) continue;
            if (q.contains(key.toLowerCase())) {
                best = entry;
                break;
            }
        }
        return best;
    }

    private int askForKBAnswerRating() {
        while (true) {
            String input = JOptionPane.showInputDialog(
                    this,
                    "Please rate this answer from 0 to 5 (0 = very bad, 5 = excellent):"
            );
            if (input == null) {
                return -1;
            }
            input = input.trim();
            if (input.isEmpty()) {
                return -1;
            }
            try {
                int rating = Integer.parseInt(input);
                if (rating >= 0 && rating <= 5) {
                    return rating;
                } else {
                    JOptionPane.showMessageDialog(this, "Please enter a number between 0 and 5.");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid integer between 0 and 5.");
            }
        }
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
        if (c.isFired()) {
            status = "Fired";
        } else if (c.getDemotionCount() > 0) {
            status = "Demoted" + c.getDemotionCount();
        } else {
            status = "Normal";
        }
        return String.format(
                "%-8s | Complaints:%d | Compliments:%d | Demotions:%d | Status:%-8s | Bonus:%2d | Salary:%.2f ",
                c.getName(),
                c.getComplaints(),
                c.getCompliments(),
                c.getDemotionCount(),
                status,
                c.getBonusCount(),
                c.getSalary()
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
                "%-8s | Complaints:%d | Compliments:%d | Demotions:%d | Status:%-8s | Bonus:%2d | Salary:%.2f ",
                d.getName(),
                d.getComplaints(),
                d.getCompliments(),
                d.getDemotionCount(),
                status,
                d.getBonusCount(),
                d.getSalary()
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

    private List<MenuItem> getTopGlobalMostPopular(int limit) {
        List<MenuItem> items = new ArrayList<>(menuItems);
        items.sort((a, b) -> Integer.compare(b.getOrderCount(), a.getOrderCount()));

        List<MenuItem> result = new ArrayList<>();
        for (MenuItem item : items) {
            if (item.getOrderCount() > 0) {
                result.add(item);
            }
            if (result.size() >= limit) break;
        }
        return result;
    }

    private List<MenuItem> getTopGlobalHighestRated(int limit) {
        List<MenuItem> items = new ArrayList<>(menuItems);
        items.sort((a, b) -> Double.compare(b.getAverageRating(), a.getAverageRating()));

        List<MenuItem> result = new ArrayList<>();
        for (MenuItem item : items) {
            if (item.getAverageRating() > 0) {
                result.add(item);
            }
            if (result.size() >= limit) break;
        }
        return result;
    }

    private List<MenuItem> getTopMostOrderedForCustomer(Customer c, int limit) {
        List<MenuItem> items = new ArrayList<>(menuItems);
        items.sort((a, b) -> {
            int ca = c.getDishOrderCount(a.getName());
            int cb = c.getDishOrderCount(b.getName());
            return Integer.compare(cb, ca);
        });

        List<MenuItem> result = new ArrayList<>();
        for (MenuItem item : items) {
            if (c.getDishOrderCount(item.getName()) > 0) {
                result.add(item);
            }
            if (result.size() >= limit) break;
        }
        return result;
    }

    private List<MenuItem> getTopHighestRatedForCustomer(Customer c, int limit) {
        List<MenuItem> items = new ArrayList<>(menuItems);
        items.sort((a, b) -> {
            double ra = c.getDishAverageRating(a.getName());
            double rb = c.getDishAverageRating(b.getName());
            return Double.compare(rb, ra);
        });

        List<MenuItem> result = new ArrayList<>();
        for (MenuItem item : items) {
            if (c.getDishAverageRating(item.getName()) > 0) {
                result.add(item);
            }
            if (result.size() >= limit) break;
        }
        return result;
    }

    private void updateRecommendationsArea() {
        if (recommendationsArea == null) return;

        StringBuilder sb = new StringBuilder();

        if (loggedInCustomer == null) {
            sb.append("Recommendations for visitors:\n\n");

            List<MenuItem> popular = getTopGlobalMostPopular(3);
            List<MenuItem> rated = getTopGlobalHighestRated(3);

            sb.append("Most popular dishes:\n");
            for (MenuItem m : popular) {
                sb.append(" • ").append(m.getName())
                        .append(" (ordered ").append(m.getOrderCount()).append(" times)\n");
            }

            sb.append("\nHighest rated dishes:\n");
            for (MenuItem m : rated) {
                sb.append(" • ").append(m.getName())
                        .append(" (avg rating ").append(String.format("%.2f", m.getAverageRating())).append(")\n");
            }

        } else {
            sb.append("Welcome, ").append(loggedInCustomer.getUsername());
            if (loggedInCustomer.isVip()) sb.append(" (VIP)");
            sb.append("\n\n");

            List<MenuItem> personalPopular = getTopMostOrderedForCustomer(loggedInCustomer, 3);
            List<MenuItem> personalRated = getTopHighestRatedForCustomer(loggedInCustomer, 3);

            sb.append("Your most ordered dishes:\n");
            for (MenuItem m : personalPopular) {
                sb.append(" • ").append(m.getName())
                        .append(" (").append(loggedInCustomer.getDishOrderCount(m.getName())).append(" orders)\n");
            }

            sb.append("\nYour highest rated dishes:\n");
            for (MenuItem m : personalRated) {
                sb.append(" • ").append(m.getName())
                        .append(" (avg rating ").append(String.format("%.2f", loggedInCustomer.getDishAverageRating(m.getName()))).append(")\n");
            }
        }

        recommendationsArea.setText(sb.toString());
    }
}

class MenuItem {
    private String name;
    private double price;
    private String chefName;
    private boolean vipOnly;

    private int orderCount;
    private int ratingCount;
    private int totalRating;

    public MenuItem(String name, double price, String chefName) {
        this(name, price, chefName, false);
    }

    public MenuItem(String name, double price, String chefName, boolean vipOnly) {
        this.name = name;
        this.price = price;
        this.chefName = chefName;
        this.vipOnly = vipOnly;
        this.orderCount = 0;
        this.ratingCount = 0;
        this.totalRating = 0;
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

    public int getOrderCount() {
        return orderCount;
    }

    public void incrementOrderCount() {
        orderCount++;
    }

    public void addRating(int rating) {
        if (rating >= 1 && rating <= 5) {
            ratingCount++;
            totalRating += rating;
        }
    }

    public double getAverageRating() {
        if (ratingCount == 0) {
            return 0.0;
        }
        return (double) totalRating / ratingCount;
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
    private double salary = 3000.00;

    private int warningCount;
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

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public int getWarningCount() {
        return warningCount;
    }

    public void incrementWarning() {
        warningCount++;
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

    public String toString() {
        return name + " (" + username + ")";
    }
}

class DeliveryPerson {
    private String name;
    private String username;
    private String password;
    private double salary = 1500.00;

    private int warningCount;
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

    public DeliveryPerson(String name, String username, String password) {
        this.name = name;
        this.username = username;
        this.password = password;
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

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public int getWarningCount() {
        return warningCount;
    }

    public void incrementWarning() {
        warningCount++;
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

    public String toString() {
        if (username == null || username.isEmpty()) {
            return name;
        }
        return name + " (" + username + ")";
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
    public int warningCount;
    private boolean deregistered;
    private Map<String, Integer> dishOrderCounts = new HashMap<>();
    private Map<String, Integer> dishRatingTotals = new HashMap<>();
    private Map<String, Integer> dishRatingCounts = new HashMap<>();

    public void recordDishOrder(MenuItem item, int rating) {
        if (item == null) return;

        String key = item.getName().toLowerCase();

        dishOrderCounts.put(key, dishOrderCounts.getOrDefault(key, 0) + 1);

        if (rating >= 1 && rating <= 5) {
            dishRatingTotals.put(key, dishRatingTotals.getOrDefault(key, 0) + rating);
            dishRatingCounts.put(key, dishRatingCounts.getOrDefault(key, 0) + 1);
        }
    }

    public int getDishOrderCount(String itemName) {
        String key = itemName.toLowerCase();
        return dishOrderCounts.getOrDefault(key, 0);
    }

    public double getDishAverageRating(String itemName) {
        String key = itemName.toLowerCase();
        int count = dishRatingCounts.getOrDefault(key, 0);
        if (count == 0) {
            return 0.0;
        }
        int total = dishRatingTotals.getOrDefault(key, 0);
        return (double) total / count;
    }

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

    public int getWarningCount() {
        return warningCount;
    }

    public void incrementWarningCount() {
        warningCount++;
    }

    public void setWarningCount(int warningCount) {
        this.warningCount = warningCount;
    }

    public boolean isVip() {
        return vip;
    }

    public void setVip(boolean vip) {
        this.vip = vip;
    }

    public boolean isDeregistered() {
        return deregistered;
    }

    public void setDeregistered(boolean deregistered) {
        this.deregistered = deregistered;
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

    public void resetVipProgress() {
        this.totalSpent = 0;
        this.orderCount = 0;
        this.ordersSinceLastFreeDelivery = 0;
        this.freeDeliveryCredits = 0;
        this.complaintCount = 0;
        this.complimentCount = 0;
        dishOrderCounts.clear();
        dishRatingTotals.clear();
        dishRatingCounts.clear();
    }

    public String toString() {
        return username;
    }
}

class Order {
    private int id;
    private Customer customer;
    private Chef chef;
    private double amountToPay;

    private DeliveryPerson assignedDelivery;
    private double bestBid = Double.MAX_VALUE;
    private Map<DeliveryPerson, Double> bids = new HashMap<>();

    private List<MenuItem> items;

    public Order(int id, Customer customer, Chef chef, double amountToPay, List<MenuItem> items) {
        this.id = id;
        this.customer = customer;
        this.chef = chef;
        this.amountToPay = amountToPay;
        this.items = new ArrayList<>(items);
    }

    public int getId() {
        return id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public Chef getChef() {
        return chef;
    }

    public double getAmountToPay() {
        return amountToPay;
    }

    public DeliveryPerson getAssignedDelivery() {
        return assignedDelivery;
    }

    public double getBestBid() {
        if (bestBid == Double.MAX_VALUE) return 0.0;
        return bestBid;
    }

    public List<MenuItem> getItems() {
        return items;
    }

    public boolean placeBid(DeliveryPerson d, double price) {
        bids.put(d, price);
        boolean isNewBest = false;
        if (price < bestBid) {
            bestBid = price;
            isNewBest = true;
        }
        d.setLastBidPrice(price);
        return isNewBest;
    }

    public Map<DeliveryPerson, Double> getBids() {
        return new HashMap<>(bids);
    }

    public void assignDelivery(DeliveryPerson d) {
        this.assignedDelivery = d;
        Double price = bids.get(d);
        if (price != null) {
            bestBid = price;
        }
    }

    public String toString() {
        String customerName = customer != null ? customer.getUsername() : "Guest";
        String chefName = chef != null ? chef.getName() : "N/A";
        String status;
        if (assignedDelivery == null) {
            if (bestBid == Double.MAX_VALUE) {
                status = "Waiting bids";
            } else {
                status = "Best bid $" + String.format("%.2f", bestBid);
            }
        } else {
            status = "Assigned: " + assignedDelivery.getName() + " $" + String.format("%.2f", bestBid);
        }
        return String.format("Order #%d  $%.2f  Customer:%s  Chef:%s  [%s]",
                id, amountToPay, customerName, chefName, status);
    }
}

class CustomerFeedback {
    private Customer customer;
    private DeliveryPerson delivery;
    private String type;
    private String text;
    private boolean disputed;

    public CustomerFeedback(Customer customer, DeliveryPerson delivery, String type, String text, boolean disputed) {
        this.customer = customer;
        this.delivery = delivery;
        this.type = type;
        this.text = text;
        this.disputed = disputed;
    }

    public Customer getCustomer() {
        return customer;
    }

    public DeliveryPerson getDelivery() {
        return delivery;
    }

    public String getType() {
        return type;
    }

    public String getText() {
        return text;
    }

    public boolean isDisputed() {
        return disputed;
    }

    public String toString() {
        String cust = customer != null ? customer.getUsername() : "UnknownCustomer";
        String del = delivery != null ? delivery.getName() : "UnknownDelivery";
        String disp = disputed ? "Yes" : "No";
        return "Cust:" + cust + " | From:" + del + " | " + type + " | Disputed:" + disp + " | " + text;
    }
}

class KnowledgeEntry {
    private String keyword;
    private String answer;
    private String author;
    private boolean flagged;
    private boolean removed;
    private boolean authorBanned;

    public KnowledgeEntry(String keyword, String answer, String author) {
        this.keyword = keyword;
        this.answer = answer;
        this.author = author;
    }

    public String getKeyword() {
        return keyword;
    }

    public String getAnswer() {
        return answer;
    }

    public String getAuthor() {
        return author;
    }

    public boolean isFlagged() {
        return flagged;
    }

    public void setFlagged(boolean flagged) {
        this.flagged = flagged;
    }

    public boolean isRemoved() {
        return removed;
    }

    public void setRemoved(boolean removed) {
        this.removed = removed;
    }

    public boolean isAuthorBanned() {
        return authorBanned;
    }

    public void setAuthorBanned(boolean authorBanned) {
        this.authorBanned = authorBanned;
    }

    public String toString() {
        return "Keyword:" + keyword + " Author:" + author + " Flagged:" + flagged;
    }
}
