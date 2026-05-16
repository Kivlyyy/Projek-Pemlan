import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameEngine {

     private int day;
     private Restaurant restaurant;
     private boolean isPrepPhase;
     private MenuCatalog menuCatalog;
     private SupplierShop supplierShop;
     private AmuletShop amuletShop;
     private SaveManager saveManager;

     private double amuletPrice;
     private double upgradeCapacityPrice;

     public GameEngine() {
          this.day = 1;
          this.isPrepPhase = true;
          this.saveManager = new SaveManager();
          this.menuCatalog = new MenuCatalog();
          this.supplierShop = new SupplierShop();
          this.amuletShop = new AmuletShop();

          this.amuletPrice = 50000.0;
          this.upgradeCapacityPrice = 150000.0;

          initSupplierStock();

          this.restaurant = new Restaurant(10, 100000.0);
          this.restaurant.setKitchen(new Kitchen());
     }

     public void startDay() {
          this.isPrepPhase = true;
          restaurant.getDiningArea().clearCustomers();
     }

     public void startSellingPhase() {
          this.isPrepPhase = false;
          runSellingPhase();
     }

     public double endDay() {
          double loss = restaurant.getKitchen().throwUnusedIngredients();
          restaurant.deductMoney(loss);
          day++;
          return loss;
     }

     public String buyIngredient(String ingredientName, int qty) {
          Ingredient ing = supplierShop.sellIngredient(ingredientName);
          if (ing == null) {
               return "Bahan '" + ingredientName + "' tidak ditemukan di supplier.";
          }

          double totalCost = ing.getPrice() * qty;
          if (restaurant.getMoney() < totalCost) {
               return "Uang tidak cukup! Butuh Rp." + String.format("%.2f", totalCost)
                    + ", tersedia Rp." + String.format("%.2f", restaurant.getMoney());
          }

          restaurant.deductMoney(totalCost);
          restaurant.getKitchen().buyIngredient(ing, qty);
          return "Berhasil membeli " + qty + "x " + ingredientName
               + ". Uang tersisa: Rp." + String.format("%.2f", restaurant.getMoney());
     }

     public String setMenuPrice(int menuIndex, double newPrice) {
          Menu m = menuCatalog.getMenuByChoice(menuIndex + 1);
          if (m == null) return "Menu tidak ditemukan.";

          double totalIngredientCost = 0;
          Ingredient[] ingredients = m.getIngredients();
          int[] quantities = m.getQuantities();
          for (int i = 0; i < ingredients.length; i++) {
               totalIngredientCost += ingredients[i].getPrice() * quantities[i];
          }

          m.setPrice(newPrice);

          if (newPrice < totalIngredientCost) {
               return "RUGI! Harga Rp." + String.format("%.2f", newPrice)
                    + " di bawah modal Rp." + String.format("%.2f", totalIngredientCost);
          } else if (newPrice > 35000) {
               return "PERINGATAN! Harga terlalu mahal, banyak pelanggan akan membatalkan pesanan."
                    + " Profit per porsi: Rp." + String.format("%.2f", newPrice - totalIngredientCost);
          } else {
               return "Harga " + m.getName() + " → Rp." + String.format("%.2f", newPrice)
                    + ". Profit per porsi: Rp." + String.format("%.2f", newPrice - totalIngredientCost);
          }
     }

     public String buyAmulet(int choice) {
          if (restaurant.getMoney() < this.amuletPrice) {
               return "Uang tidak cukup untuk membeli jimat! (Harga: Rp." + this.amuletPrice + ")";
          }

          Amulet bought = amuletShop.buyAmulet(choice);
          if (bought == null) {
               return "Pilihan jimat tidak valid.";
          }
          
          List<Amulet> activeAmulets = restaurant.getActiveAmulets();
          for (int i = 0; i < activeAmulets.size(); i++) {
               if (activeAmulets.get(i).getClass().equals(bought.getClass())) {
                    activeAmulets.set(i, bought); 
                    restaurant.deductMoney(this.amuletPrice);
                    return "Jimat " + bought.getName() + " di-upgrade!"
                         + " Efek baru: " + String.format("%.1f", bought.getEffectPercentage()) + "%."
                         + " Uang tersisa: Rp." + String.format("%.2f", restaurant.getMoney());
               }
          }

          restaurant.deductMoney(this.amuletPrice);
          restaurant.addAmulet(bought);
          return "Berhasil membeli " + bought.getName()
               + " (efek " + String.format("%.1f", bought.getEffectPercentage()) + "%)."
               + " Uang tersisa: Rp." + String.format("%.2f", restaurant.getMoney());
     }

     public String upgradeCapacity() {
          int amount = 5;
          if (restaurant.getMoney() < this.upgradeCapacityPrice) {
               return "Uang tidak cukup! Butuh Rp." + this.upgradeCapacityPrice
                    + ", tersedia Rp." + String.format("%.2f", restaurant.getMoney());
          }
          
          restaurant.deductMoney(this.upgradeCapacityPrice); 
          restaurant.upgradeCapacity(amount, this.upgradeCapacityPrice);
          
          return "Upgrade berhasil! Kapasitas sekarang: " + restaurant.getCapacity()
               + ". Uang tersisa: Rp." + String.format("%.2f", restaurant.getMoney());
     }

     // ============================================================
     // FASE BERJUALAN (ALUR VISUAL NOVEL DIPERBAIKI)
     // ============================================================

     private void runSellingPhase() {
          List<Customer> incomingCustomers = generateCustomers();
          DiningArea diningArea = restaurant.getDiningArea();
          List<Menu> availableMenus = getAvailableMenus();

          double startMoney = restaurant.getMoney();
          
          // 1. Silent Admit: Tentukan siapa yang dapat kursi secara diam-diam (agar Bencana bisa deteksi)
          diningArea.clearCustomers();
          int occupied = 0;
          List<Customer> admitted = new ArrayList<>();
          for (Customer c : incomingCustomers) {
               if (occupied + c.getCapacityNeeded() <= restaurant.getCapacity()) {
                    admitted.add(c);
                    occupied += c.getCapacityNeeded();
               } else {
                    break;
               }
          }
          diningArea.getCustomers().addAll(admitted);

          // 2. Trigger Bencana (Tikus mencuri bahan ATAU Pelanggan ditandai mau kabur)
          triggerDisasters(); 

          // 3. Eksekusi Cetak Dialog Sesuai Urutan Visual Novel (Duduk -> Pesan -> Bayar/Kabur)
          for (Customer c : admitted) {
               // Sengaja kita cetak manual di sini biar alurnya rapi per pelanggan
               System.out.println("Pelanggan dipersilakan duduk. Membutuhkan " + c.getCapacityNeeded() + " kursi.");
               diningArea.processSingleCustomerOrder(restaurant, c, availableMenus);
          }
          
          // 4. Print pelanggan yang ditolak (Jika rombongan lebih besar dari sisa kapasitas)
          if (admitted.size() < incomingCustomers.size()) {
               System.out.println("Tidak cukup tempat untuk rombongan selanjutnya.");
               System.out.println("Tidak cukup tempat! Kapasitas Penuh!");
          }

          double income = restaurant.getMoney() - startMoney;
          System.out.println("    SESI BERAKHIR    ");
          System.out.println("Total Pendapatan Sesi Ini: Rp." + String.format("%.2f", income));
     }

     public void triggerDisasters() {
         Random rand = new Random();
         if (rand.nextDouble() > 0.30) return;
     
         int disasterCount = 1 + rand.nextInt(3);
     
         for (int i = 0; i < disasterCount; i++) {
             Disaster disaster;
             if (rand.nextBoolean()) {
                 disaster = new RunawayCustomerDisaster(0.3 + rand.nextDouble() * 0.4);
             } else {
                 double loss = 0.1 + rand.nextDouble() * 0.3;
                 disaster = new HungryRatDisaster(0.5, loss);
             }
             disaster.trigger(restaurant);
         }
     }

     public void saveGame() { saveManager.saveGame(this); }
     public void loadGame() { saveManager.loadGame(this); }
     public boolean hasSaveFile() { return saveManager.hasSaveFile(); }

     private List<Customer> generateCustomers() {
          Random rand = new Random();
          List<Customer> customers = new ArrayList<>();
          int totalGroups = 3 + rand.nextInt(restaurant.getCapacity() + 3);
          for (int i = 0; i < totalGroups; i++) {
               customers.add(new Customer());
          }
          return customers;
     }

     public List<Menu> getAvailableMenus() {
          List<Menu> available = new ArrayList<>();
          Kitchen kitchen = restaurant.getKitchen();
          for (int i = 1; i <= 20; i++) {
               Menu m = menuCatalog.getMenuByChoice(i);
               if (m != null && m.isAvailable(kitchen)) {
                    available.add(m);
               }
          }
          return available;
     }

     private void initSupplierStock() {
          supplierShop.addIngredientToCatalog(new Ingredient("Bread", 3000));
          supplierShop.addIngredientToCatalog(new Ingredient("Egg", 2000));
          supplierShop.addIngredientToCatalog(new Ingredient("Bacon", 5000));
          supplierShop.addIngredientToCatalog(new Ingredient("Tea", 2000));
          supplierShop.addIngredientToCatalog(new Ingredient("Sugar", 1000));
          supplierShop.addIngredientToCatalog(new Ingredient("Potato", 4000));
          supplierShop.addIngredientToCatalog(new Ingredient("Oil", 3000));
     }

     public int getDay()                      { return day; }
     public boolean isPrepPhase()             { return isPrepPhase; }
     public Restaurant getRestaurant()        { return restaurant; }
     public MenuCatalog getMenuCatalog()      { return menuCatalog; }
     public SupplierShop getSupplierShop()    { return supplierShop; }
     public AmuletShop getAmuletShop()        { return amuletShop; }
     
     public double getAmuletPrice()           { return amuletPrice; }
     public double getUpgradeCapacityPrice()  { return upgradeCapacityPrice; }

     public void setDay(int day)                        { this.day = day; }
     public void setRestaurant(Restaurant restaurant)   { this.restaurant = restaurant; }
     
     public void setAmuletPrice(double price)           { this.amuletPrice = price; }
     public void setUpgradeCapacityPrice(double price)  { this.upgradeCapacityPrice = price; }
}