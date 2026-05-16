import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.*;

public class GameGUI extends JFrame {
    private GameEngine engine;

    private JPanel mainPanel;
    private CardLayout mainCardLayout;
    
    private JPanel roomPanel;
    private CardLayout roomCardLayout;
    private String currentRoom = "MainMenu";
    
    private BackgroundPanel diningBgPanel;
    private BackgroundPanel kitchenBgPanel;
    
    private JLabel statusLabel;
    private JPanel actionPanel;
    private JPanel navPanel;
    private JPanel tableGridPanel;
    
    private JPanel dialoguePanel;
    private JLabel dialogueText;
    private Queue<String> messageQueue = new LinkedList<>();
    
    private int currentOccupiedSeats = 0;
    private List<Amulet> ownedAmulets = new ArrayList<>();
    private List<Amulet> equippedAmulets = new ArrayList<>();
    
    public GameGUI() {
        engine = new GameEngine();
        
        setTitle("Restaurant Tycoon - Visual Novel Style Tycoon");
        setSize(1100, 800); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        mainCardLayout = new CardLayout();
        mainPanel = new JPanel(mainCardLayout);

        mainPanel.add(createMainMenu(), "MainMenu");
        mainPanel.add(createGameScreen(), "GameScreen");
        
        add(mainPanel);
        redirectSystemOut();
    }

    private ImageIcon getScaledImage(String path, int width, int height) {
        try {
            ImageIcon icon = new ImageIcon(path);
            if (icon.getIconWidth() == -1) return new ImageIcon(); 
            Image img = icon.getImage();
            Image scaledImg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(scaledImg);
        } catch (Exception e) { return new ImageIcon(); }
    }

    class BackgroundPanel extends JPanel {
        private Image bgImage;
        public BackgroundPanel(String imagePath) { setImage(imagePath); }
        public void setImage(String imagePath) {
            try { this.bgImage = new ImageIcon(imagePath).getImage(); repaint(); } catch(Exception e){}
        }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (bgImage != null) g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
        }
    }

    private void setUIEnabled(boolean enabled) {
        for (Component c : navPanel.getComponents()) c.setEnabled(enabled);
        for (Component c : actionPanel.getComponents()) c.setEnabled(enabled);
    }

    private JPanel createMainMenu() {
        BackgroundPanel menuPanel = new BackgroundPanel("Aset/Resto.png"); 
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBackground(new Color(26, 26, 26)); 
        
        JLabel logoLabel = new JLabel(getScaledImage("Aset/Logo.png", 300, 300));
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel title = new JLabel("RESTAURANT TYCOON");
        title.setFont(new Font("Monospaced", Font.BOLD, 48));
        title.setForeground(new Color(222, 255, 154));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JButton btnPlay = new JButton("NEW GAME");
        btnPlay.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnPlay.addActionListener(e -> { engine.startDay(); changeRoom("DiningRoom"); });
        
        JButton btnContinue = new JButton("CONTINUE");
        btnContinue.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnContinue.addActionListener(e -> { engine.loadGame(); changeRoom("DiningRoom"); });
        
        JButton btnExit = new JButton("EXIT");
        btnExit.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnExit.addActionListener(e -> System.exit(0));
        
        menuPanel.add(Box.createVerticalStrut(50));
        menuPanel.add(logoLabel);
        menuPanel.add(Box.createVerticalStrut(20));
        menuPanel.add(title);
        menuPanel.add(Box.createVerticalStrut(50));
        menuPanel.add(btnPlay);
        menuPanel.add(Box.createVerticalStrut(20));
        menuPanel.add(btnContinue);
        menuPanel.add(Box.createVerticalStrut(20));
        menuPanel.add(btnExit);
        
        return menuPanel;
    }

    private JPanel createGameScreen() {
        JPanel gamePanel = new JPanel(new BorderLayout());
        
        statusLabel = new JLabel("Status...", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Monospaced", Font.BOLD, 18));
        statusLabel.setOpaque(true);
        statusLabel.setBackground(Color.DARK_GRAY);
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        gamePanel.add(statusLabel, BorderLayout.NORTH);
        
        JPanel centerOverlay = new JPanel() {
            @Override
            public boolean isOptimizedDrawingEnabled() {
                return false; 
            }
        };
        centerOverlay.setLayout(new OverlayLayout(centerOverlay));
        
        JPanel dialogLayer = new JPanel(new BorderLayout());
        dialogLayer.setOpaque(false);
        
        dialoguePanel = new JPanel(new BorderLayout());
        dialoguePanel.setBackground(new Color(245, 245, 220)); 
        dialoguePanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(139, 69, 19), 6), 
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        dialoguePanel.setPreferredSize(new Dimension(1100, 140));
        dialoguePanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        dialogueText = new JLabel("...");
        dialogueText.setFont(new Font("Arial", Font.BOLD, 16));
        dialoguePanel.add(dialogueText, BorderLayout.CENTER);
        
        JLabel clickHint = new JLabel("Klik untuk melanjutkan >>");
        clickHint.setFont(new Font("Arial", Font.ITALIC, 12));
        clickHint.setForeground(Color.GRAY);
        dialoguePanel.add(clickHint, BorderLayout.SOUTH);
        
        dialoguePanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) { showNextMessage(); }
        });
        
        dialoguePanel.setVisible(false); 
        dialogLayer.add(dialoguePanel, BorderLayout.SOUTH); 
        
        roomCardLayout = new CardLayout();
        roomPanel = new JPanel(roomCardLayout);
        roomPanel.add(createDiningRoom(), "DiningRoom");
        roomPanel.add(createKitchenRoom(), "KitchenRoom");
        roomPanel.add(createSupplierRoom(), "SupplierRoom");
        roomPanel.add(createAmuletRoom(), "AmuletRoom");
        
        centerOverlay.add(dialogLayer);
        centerOverlay.add(roomPanel);
        
        gamePanel.add(centerOverlay, BorderLayout.CENTER);
        
        JPanel controls = new JPanel(new BorderLayout());
        navPanel = new JPanel();
        
        JButton btnNavDining = new JButton("Ke Tempat Makan");
        btnNavDining.addActionListener(e -> changeRoom("DiningRoom"));
        JButton btnNavKitchen = new JButton("Ke Dapur");
        btnNavKitchen.addActionListener(e -> changeRoom("KitchenRoom"));
        JButton btnNavSupplier = new JButton("Ke Toko Bahan");
        btnNavSupplier.addActionListener(e -> changeRoom("SupplierRoom"));
        JButton btnNavAmulet = new JButton("Ke Toko Jimat");
        btnNavAmulet.addActionListener(e -> changeRoom("AmuletRoom"));
        JButton btnNavMenu = new JButton("Kembali ke Main Menu");
        btnNavMenu.setBackground(new Color(255, 69, 0));
        btnNavMenu.setForeground(Color.WHITE);
        btnNavMenu.addActionListener(e -> {
            mainCardLayout.show(mainPanel, "MainMenu");
            currentRoom = "MainMenu";
        });
        
        navPanel.add(btnNavDining);
        navPanel.add(btnNavKitchen);
        navPanel.add(btnNavSupplier);
        navPanel.add(btnNavAmulet);
        navPanel.add(btnNavMenu);
        
        actionPanel = new JPanel(); 
        controls.add(navPanel, BorderLayout.NORTH);
        controls.add(actionPanel, BorderLayout.CENTER);
        
        gamePanel.add(controls, BorderLayout.SOUTH);
        
        return gamePanel;
    }

    private void changeRoom(String roomName) {
        currentRoom = roomName;
        mainCardLayout.show(mainPanel, "GameScreen"); 
        roomCardLayout.show(roomPanel, roomName);
        updateStatus();
        updateActionButtons();
    }

    private JPanel createDiningRoom() {
        diningBgPanel = new BackgroundPanel("Aset/tempatMakan.png");
        diningBgPanel.setLayout(new BorderLayout());
        
        JLabel title = new JLabel("TEMPAT MAKAN", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        diningBgPanel.add(title, BorderLayout.NORTH);
        
        tableGridPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 30));
        tableGridPanel.setOpaque(false); 
        tableGridPanel.setBorder(BorderFactory.createEmptyBorder(220, 20, 20, 20)); 
        
        diningBgPanel.add(tableGridPanel, BorderLayout.CENTER);
        return diningBgPanel;
    }

    private JPanel createKitchenRoom() {
        kitchenBgPanel = new BackgroundPanel("Aset/kitchen.png");
        kitchenBgPanel.setLayout(new BorderLayout());
        JLabel title = new JLabel("DAPUR", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        kitchenBgPanel.add(title, BorderLayout.NORTH);
        
        return kitchenBgPanel;
    }

    private JPanel createSupplierRoom() {
        BackgroundPanel panel = new BackgroundPanel("Aset/Shop.png");
        panel.setLayout(new BorderLayout());
        return panel;
    }

    private JPanel createAmuletRoom() {
        BackgroundPanel panel = new BackgroundPanel("Aset/Amulet_bg.png");
        panel.setLayout(new BorderLayout());
        return panel;
    }

    private void openIngredientShop() {
        JDialog dialog = new JDialog(this, "Pasar Bahan Baku", true);
        dialog.setSize(500, 600);
        dialog.setLayout(new BorderLayout());
        dialog.setLocationRelativeTo(this);

        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(listPanel);

        String[] ings = {"Bread", "Egg", "Bacon", "Tea", "Sugar", "Potato", "Oil"};
        double[] prices = {3000, 2000, 5000, 2000, 1000, 4000, 3000};
        String[] paths = {"Aset/Bread.png", "Aset/Egg.png", "Aset/Bacon.png", "Aset/Tea.png", "Aset/Sugar.png", "Aset/Potato.png", "Aset/Oil.png"};
        int[] quantities = new int[ings.length];

        JLabel totalLabel = new JLabel("Total Harga: Rp.0.00", SwingConstants.CENTER);
        totalLabel.setFont(new Font("Arial", Font.BOLD, 18));
        totalLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        for (int i = 0; i < ings.length; i++) {
            final int index = i;
            JPanel row = new JPanel(new BorderLayout());
            row.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

            JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            infoPanel.add(new JLabel(getScaledImage(paths[i], 50, 50)));
            infoPanel.add(new JLabel("<html><b style='font-size:14px'>" + ings[i] + "</b><br>Rp " + prices[i] + "</html>"));
            row.add(infoPanel, BorderLayout.CENTER);

            JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JButton btnMinus = new JButton("-");
            JLabel qtyLabel = new JLabel("0");
            qtyLabel.setPreferredSize(new Dimension(30, 30));
            qtyLabel.setHorizontalAlignment(SwingConstants.CENTER);
            JButton btnPlus = new JButton("+");

            btnMinus.addActionListener(e -> {
                if (quantities[index] > 0) {
                    quantities[index]--;
                    qtyLabel.setText(String.valueOf(quantities[index]));
                    updateShopTotal(quantities, prices, totalLabel);
                }
            });
            btnPlus.addActionListener(e -> {
                quantities[index]++;
                qtyLabel.setText(String.valueOf(quantities[index]));
                updateShopTotal(quantities, prices, totalLabel);
            });

            controlPanel.add(btnMinus);
            controlPanel.add(qtyLabel);
            controlPanel.add(btnPlus);
            row.add(controlPanel, BorderLayout.EAST);
            listPanel.add(row);
            listPanel.add(new JSeparator());
        }

        JButton btnBuy = new JButton("SELESAI BELANJA");
        btnBuy.setFont(new Font("Arial", Font.BOLD, 16));
        btnBuy.setBackground(new Color(60, 179, 113));
        btnBuy.setForeground(Color.WHITE);
        btnBuy.addActionListener(e -> {
            for (int i = 0; i < ings.length; i++) {
                if (quantities[i] > 0) {
                    engine.buyIngredient(ings[i], quantities[i]);
                }
            }
            dialog.dispose();
            updateStatus();
        });

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(totalLabel, BorderLayout.NORTH);
        bottomPanel.add(btnBuy, BorderLayout.SOUTH);

        dialog.add(scrollPane, BorderLayout.CENTER);
        dialog.add(bottomPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void updateShopTotal(int[] quantities, double[] prices, JLabel totalLabel) {
        double total = 0;
        for (int i = 0; i < quantities.length; i++) total += quantities[i] * prices[i];
        totalLabel.setText("Total Harga: Rp." + String.format("%.2f", total));
    }
    
    private void openRecipeBook() {
        StringBuilder recipes = new StringBuilder("    BUKU RESEP DAPUR    \n\n");
        MenuCatalog catalog = engine.getMenuCatalog();
        for (int i = 1; i <= 20; i++) {
            Menu m = catalog.getMenuByChoice(i);
            if (m != null) {
                recipes.append(m.getName()).append(" (Rp.").append(String.format("%.2f", m.getPrice())).append(")\n");
                recipes.append(m.getDescription()).append("\nBahan: ");
                for (int j = 0; j < m.getIngredients().length; j++) {
                    recipes.append(m.getIngredients()[j].getName()).append(" (x").append(m.getQuantities()[j]).append(")  ");
                }
                recipes.append("\n\n");
            }
        }
        JTextArea area = new JTextArea(recipes.toString());
        area.setEditable(false);
        JScrollPane scroll = new JScrollPane(area);
        scroll.setPreferredSize(new Dimension(400, 300));
        JOptionPane.showMessageDialog(GameGUI.this, scroll, "Buku Resep", JOptionPane.INFORMATION_MESSAGE);
    }

    private void openPriceEditor() {
        MenuCatalog catalog = engine.getMenuCatalog();
        List<String> menuNames = new ArrayList<>();
        List<Integer> menuIndices = new ArrayList<>();
        
        for (int i = 1; i <= 20; i++) {
            Menu m = catalog.getMenuByChoice(i);
            if (m != null) {
                menuNames.add(m.getName() + " (Rp." + String.format("%.2f", m.getPrice()) + ")");
                menuIndices.add(i - 1); 
            }
        }
        if (menuNames.isEmpty()) return;
        
        String[] options = menuNames.toArray(new String[0]);
        String selected = (String) JOptionPane.showInputDialog(GameGUI.this, "Pilih menu yang ingin diubah harganya:", "Ubah Harga Menu", JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        
        if (selected != null) {
            int listIndex = menuNames.indexOf(selected);
            int realIndex = menuIndices.get(listIndex);
            String priceStr = JOptionPane.showInputDialog(GameGUI.this, "Masukkan harga baru untuk menu ini:");
            try {
                double newPrice = Double.parseDouble(priceStr);
                String resultMsg = engine.setMenuPrice(realIndex, newPrice);
                JOptionPane.showMessageDialog(GameGUI.this, resultMsg, "Notifikasi Harga", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(GameGUI.this, "Input harga tidak valid!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void openEquipAmuletDialog() {
        if (ownedAmulets.isEmpty()) {
            JOptionPane.showMessageDialog(GameGUI.this, "Kamu belum memiliki jimat apapun! Silakan beli di Toko Jimat.", "Inventory Kosong", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(new JLabel("Pilih jimat yang ingin dipakai hari ini:"));
        
        List<JCheckBox> checkBoxes = new ArrayList<>();
        for (Amulet am : ownedAmulets) {
            boolean isAlreadyEquipped = false;
            for (Amulet eq : equippedAmulets) {
                if (eq.getName().equals(am.getName())) isAlreadyEquipped = true;
            }
            JCheckBox cb = new JCheckBox(am.getName() + " (Efek: " + String.format("%.1f", am.getEffectPercentage()) + "%)", isAlreadyEquipped);
            checkBoxes.add(cb);
            panel.add(cb);
        }

        int result = JOptionPane.showConfirmDialog(GameGUI.this, panel, "Pilih Jimat Aktif", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            equippedAmulets.clear();
            for (int i = 0; i < checkBoxes.size(); i++) {
                if (checkBoxes.get(i).isSelected()) {
                    equippedAmulets.add(ownedAmulets.get(i));
                }
            }
            JOptionPane.showMessageDialog(GameGUI.this, "Jimat berhasil dipasang untuk berjualan!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void updateActionButtons() {
        actionPanel.removeAll();
        
        if (engine.isPrepPhase()) {
            if (currentRoom.equals("SupplierRoom")) {
                JButton btnBuyIng = new JButton("Buka Katalog Belanja");
                btnBuyIng.addActionListener(e -> openIngredientShop());
                actionPanel.add(btnBuyIng);
            }
            
            if (currentRoom.equals("AmuletRoom")) {
                JButton btnBuyAmulet = new JButton("Beli Jimat (Rp.50000)");
                btnBuyAmulet.addActionListener(e -> {
                    String[] options = {"1. Charming Amulet", "2. Security Amulet", "3. Cleaner Amulet"};
                    String[] paths = {"Aset/Charming.png", "Aset/Security.png", "Aset/Cleaner.png"};
                    String[] descriptions = {
                        "<html><b>Fungsi:</b><br>Memberikan peluang pelanggan memberikan tip ekstra (uang tambahan).</html>",
                        "<html><b>Fungsi:</b><br>Mencegah pelanggan nakal yang berniat kabur tanpa membayar.</html>",
                        "<html><b>Fungsi:</b><br>Mengusir wabah tikus yang ingin mencuri stok bahan baku di dapur.</html>"
                    };

                    JPanel panel = new JPanel(new BorderLayout(15, 15));
                    
                    JComboBox<String> combo = new JComboBox<>(options);
                    JLabel iconLabel = new JLabel(getScaledImage(paths[0], 80, 80));
                    JLabel descLabel = new JLabel(descriptions[0]);
                    descLabel.setPreferredSize(new Dimension(250, 60));
                    
                    combo.addActionListener(evt -> {
                        int idx = combo.getSelectedIndex();
                        iconLabel.setIcon(getScaledImage(paths[idx], 80, 80));
                        descLabel.setText(descriptions[idx]);
                    });

                    JPanel topPanel = new JPanel(new BorderLayout(15, 15));
                    topPanel.add(iconLabel, BorderLayout.WEST);
                    topPanel.add(combo, BorderLayout.CENTER);
                    
                    panel.add(topPanel, BorderLayout.NORTH);
                    panel.add(descLabel, BorderLayout.CENTER);
                    
                    int res = JOptionPane.showConfirmDialog(GameGUI.this, panel, "Toko Jimat Mistis (Rp.50000)", JOptionPane.OK_CANCEL_OPTION);
                    if (res == JOptionPane.OK_OPTION) {
                        System.out.println(engine.buyAmulet(combo.getSelectedIndex() + 1));
                        ownedAmulets = new ArrayList<>(engine.getRestaurant().getActiveAmulets());
                        updateStatus();
                    }
                });
                actionPanel.add(btnBuyAmulet);
            }

            if (currentRoom.equals("KitchenRoom")) {
                JButton btnStock = new JButton("Lihat Stok Dapur");
                btnStock.addActionListener(e -> {
                    StringBuilder stockInfo = new StringBuilder("    STOK SAAT INI    \n\n");
                    Kitchen k = engine.getRestaurant().getKitchen();
                    String[] allIngs = {"Bread", "Egg", "Bacon", "Tea", "Sugar", "Potato", "Oil"};
                    for (String i : allIngs) stockInfo.append("- ").append(i).append(" : ").append(k.getStock(new Ingredient(i, 0))).append(" unit\n");
                    JOptionPane.showMessageDialog(GameGUI.this, stockInfo.toString(), "Inventaris Dapur", JOptionPane.INFORMATION_MESSAGE);
                });

                JButton btnRecipe = new JButton("Buku Resep");
                btnRecipe.addActionListener(e -> openRecipeBook());
                
                JButton btnEditPrice = new JButton("Ubah Harga Menu");
                btnEditPrice.addActionListener(e -> openPriceEditor());
                
                JButton btnEquip = new JButton("Pilih Jimat Aktif");
                btnEquip.addActionListener(e -> openEquipAmuletDialog());
                
                actionPanel.add(btnStock);
                actionPanel.add(btnRecipe);
                actionPanel.add(btnEditPrice);
                actionPanel.add(btnEquip);
            }

            if (currentRoom.equals("DiningRoom")) {
                JButton btnUpgrade = new JButton("Upgrade Kapasitas (Rp.150000)");
                btnUpgrade.addActionListener(e -> {
                    System.out.println(engine.upgradeCapacity());
                    updateTableVisuals(); 
                    updateStatus();
                });
                actionPanel.add(btnUpgrade);

                JButton btnStartSell = new JButton("MULAI BERJUALAN");
                btnStartSell.setBackground(Color.GREEN);
                btnStartSell.addActionListener(e -> {
                    engine.getRestaurant().getActiveAmulets().clear();
                    engine.getRestaurant().getActiveAmulets().addAll(equippedAmulets);

                    messageQueue.clear();
                    currentOccupiedSeats = 0;
                    updateTableVisuals();
                    changeRoom("DiningRoom"); 
                    
                    new Thread(() -> {
                        engine.startSellingPhase();
                        SwingUtilities.invokeLater(() -> {
                            updateStatus();
                            showNextMessage(); 
                        });
                    }).start();
                });
                actionPanel.add(btnStartSell);
            }
            
            JButton btnSave = new JButton("Save Game");
            btnSave.addActionListener(e -> engine.saveGame());
            actionPanel.add(btnSave);

        } else {
            if (currentRoom.equals("DiningRoom") && messageQueue.isEmpty()) {
                JButton btnEndDay = new JButton("AKHIRI HARI & BUANG SISA");
                btnEndDay.setBackground(Color.RED);
                btnEndDay.setForeground(Color.WHITE);
                btnEndDay.addActionListener(e -> {
                    engine.endDay();
                    engine.startDay();
                    
                    engine.getRestaurant().getActiveAmulets().clear();
                    engine.getRestaurant().getActiveAmulets().addAll(ownedAmulets);
                    
                    currentOccupiedSeats = 0;
                    updateTableVisuals();
                    changeRoom("DiningRoom");
                    
                    diningBgPanel.setImage("Aset/tempatMakan.png");
                    kitchenBgPanel.setImage("Aset/kitchen.png");
                });
                actionPanel.add(btnEndDay);
            }
        }
        
        actionPanel.revalidate();
        actionPanel.repaint();
    }

private void updateTableVisuals() {
    tableGridPanel.removeAll();

    int totalCapacity = engine.getRestaurant().getCapacity();

    int occupied = 0;
    for (Customer c : engine.getRestaurant().getDiningArea().getCustomers()) {
        occupied += c.getCapacityNeeded();
    }

    int chairSize = 50;
    int tableWidth = 350;
    int tableHeight = 80;

    ImageIcon emptySeatBack  = getScaledImage("Aset/Aset/MejaKosong.png", chairSize, chairSize);
    ImageIcon fullSeatBack   = getScaledImage("Aset/Aset/Mejalsi1.png",   chairSize, chairSize);
    ImageIcon emptySeatFront = getScaledImage("Aset/Aset/MejaKosong.png", chairSize, chairSize);
    ImageIcon fullSeatFront  = getScaledImage("Aset/Aset/Mejalsi2.png",   chairSize, chairSize);
    ImageIcon tableImg       = getScaledImage("Aset/Aset/MejaKosong.png", tableWidth, tableHeight);

    int seatsProcessed = 0;

    while (seatsProcessed < totalCapacity) {
        int seatsForThisTable = Math.min(10, totalCapacity - seatsProcessed);

        JPanel groupPanel = new JPanel(new BorderLayout());
        groupPanel.setOpaque(false);

        JPanel topChairs    = new JPanel(new FlowLayout(FlowLayout.CENTER, 2, 0));
        JPanel bottomChairs = new JPanel(new FlowLayout(FlowLayout.CENTER, 2, 0));
        topChairs.setOpaque(false);
        bottomChairs.setOpaque(false);

        for (int i = 0; i < seatsForThisTable; i++) {
            boolean isOccupied = (seatsProcessed + i) < occupied;

            if (i % 2 == 0) {
                topChairs.add(new JLabel(isOccupied ? fullSeatBack : emptySeatBack));
            } else {
                bottomChairs.add(new JLabel(isOccupied ? fullSeatFront : emptySeatFront));
            }
        }

        seatsProcessed += seatsForThisTable;

        groupPanel.add(topChairs, BorderLayout.NORTH);
        groupPanel.add(new JLabel(tableImg, SwingConstants.CENTER), BorderLayout.CENTER);
        groupPanel.add(bottomChairs, BorderLayout.SOUTH);

        tableGridPanel.add(groupPanel);
    }

    tableGridPanel.revalidate();
    tableGridPanel.repaint();
}

    private void updateStatus() {
        Restaurant r = engine.getRestaurant();
        String phase = engine.isPrepPhase() ? "PERSIAPAN" : "BERJUALAN";
        statusLabel.setText(String.format("HARI: %d  |  KAS: Rp.%.2f  |  KAPASITAS: %d Kursi  |  FASE: %s",
             engine.getDay(), r.getMoney(), r.getCapacity(), phase));
    }

    private void redirectSystemOut() {
        OutputStream out = new OutputStream() {
            private StringBuilder lineBuffer = new StringBuilder();
            @Override
            public void write(int b) {
                char c = (char) b;
                if (c == '\n') {
                    String line = lineBuffer.toString().trim();
                    lineBuffer.setLength(0);
                    
                    if (!line.isEmpty() && !line.matches("[-=]+") && !line.startsWith("---")) {
                        SwingUtilities.invokeLater(() -> {
                            messageQueue.add(line);
                            
                            if (line.contains("sedang memilih menu")) {
                                messageQueue.add("Harga menu sangat mempengaruhi niat beli mereka...");
                                messageQueue.add("Jika harga terlalu mahal, pelanggan mungkin hanya melihat lalu membatalkan pesanan.");
                            }

                            if (!dialoguePanel.isVisible() && !engine.isPrepPhase()) {
                                showNextMessage();
                            }
                        });
                    }
                } else {
                    lineBuffer.append(c);
                }
            }
        };
        System.setOut(new PrintStream(out, true));
    }

    private void showNextMessage() {
        if (messageQueue.isEmpty()) {
            dialoguePanel.setVisible(false);
            setUIEnabled(true); 
            updateActionButtons(); 
        } else {
            setUIEnabled(false); 
            dialoguePanel.setVisible(true);
            
            String line = messageQueue.poll();
            dialogueText.setText("<html>" + line + "</html>");
            
            triggerVisualEventsSync(line);
        }
    }

    private void triggerVisualEventsSync(String line) {
        diningBgPanel.setImage("Aset/tempatMakan.png"); 
        kitchenBgPanel.setImage("Aset/kitchen.png");
        tableGridPanel.setVisible(true);
        
        if (!currentRoom.equals("SupplierRoom") && !currentRoom.equals("AmuletRoom")) {
            changeRoom("DiningRoom"); 
        }
        
        Matcher m = Pattern.compile("Membutuhkan (\\d+) kursi").matcher(line);
        if (m.find()) {
            int groupSize = Integer.parseInt(m.group(1));
            currentOccupiedSeats += groupSize;
            updateTableVisuals();
        }
        
        if (line.contains("Tikus Lapar") || line.contains("Rombongan tikus masuk")) {
            changeRoom("KitchenRoom");
            kitchenBgPanel.setImage("Aset/TikusNyerang.png");
        }
        
        if (line.contains("PELANGGAN KABUR") || line.contains("beberapa pelanggan bersiap untuk kabur") || line.contains("Pelanggan kabur tidak mau membayar")) {
            changeRoom("DiningRoom");
            diningBgPanel.setImage("Aset/OrangKabur.png");
            tableGridPanel.setVisible(false);
        }

        if (line.contains("Tidak cukup tempat")) {
            JOptionPane.showMessageDialog(GameGUI.this, "Kapasitas Penuh! Pelanggan tidak kebagian tempat duduk.", "Restoran Penuh", JOptionPane.WARNING_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GameGUI().setVisible(true));
    }
}
