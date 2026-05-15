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

     public GameEngine() {
          this.day = 1;
          this.isPrepPhase = true;
          this.saveManager = new SaveManager();
          this.menuCatalog = new MenuCatalog();
          this.supplierShop = new SupplierShop();
          this.amuletShop = new AmuletShop();

          initSupplierStock();

          this.restaurant = new Restaurant(10, 500.0);
          this.restaurant.setKitchen(new Kitchen());
     }

     // ============================================================
     // KONTROL FASE — dipanggil oleh GUI
     // ============================================================

     /**
          * Dipanggil GUI saat memulai hari baru.
          * Mengatur ulang state ke fase persiapan.
          */
     public void startDay() {
          this.isPrepPhase = true;
          restaurant.getDiningArea().clearCustomers();
     }

     /**
          * Dipanggil GUI saat tombol "Mulai Berjualan" ditekan.
          * Beralih dari fase persiapan ke fase berjualan.
          */
     public void startSellingPhase() {
          this.isPrepPhase = false;
          runSellingPhase();
     }

     /**
          * Dipanggil GUI saat tombol "Akhiri Hari" ditekan.
          * Mengembalikan total kerugian bahan terbuang agar GUI bisa menampilkannya.
          */
     public double endDay() {
          double loss = restaurant.getKitchen().throwUnusedIngredients();
          restaurant.deductMoney(loss);
          day++;
          return loss;
     }

     // ============================================================
     // AKSI FASE PERSIAPAN — dipanggil tombol GUI
     // ============================================================

     /**
          * Beli bahan baku dari supplier.
          * @return pesan hasil transaksi untuk ditampilkan GUI
          */
     public String buyIngredient(String ingredientName, int qty) {
          Ingredient ing = supplierShop.sellIngredient(ingredientName);
          if (ing == null) {
               return "Bahan '" + ingredientName + "' tidak ditemukan di supplier.";
          }

          double totalCost = ing.getPrice() * qty;
          if (restaurant.getMoney() < totalCost) {
               return "Uang tidak cukup! Butuh $" + String.format("%.2f", totalCost)
                    + ", tersedia $" + String.format("%.2f", restaurant.getMoney());
          }

          restaurant.deductMoney(totalCost);
          restaurant.getKitchen().buyIngredient(ing, qty);
          return "Berhasil membeli " + qty + "x " + ingredientName
               + ". Uang tersisa: $" + String.format("%.2f", restaurant.getMoney());
     }

     /**
          * Beli jimat dari toko jimat.
          * @param choice 1=Charming, 2=Security, 3=Cleaner
          * @return pesan hasil transaksi untuk ditampilkan GUI
          */
     public String buyAmulet(int choice) {
          double amuletCost = 50.0;
          if (restaurant.getMoney() < amuletCost) {
               return "Uang tidak cukup untuk membeli jimat! (Harga: $" + amuletCost + ")";
          }

          Amulet bought = amuletShop.buyAmulet(choice);
          restaurant.addAmulet(bought);
          
          if (bought == null) {
               return "Pilihan jimat tidak valid.";
          }

          restaurant.deductMoney(amuletCost);
          return "Berhasil membeli " + bought.getName()
               + " (efek " + String.format("%.1f", bought.getEffectPercentage()) + "%)."
               + " Uang tersisa: $" + String.format("%.2f", restaurant.getMoney());
     }

     /**
          * Upgrade kapasitas restoran.
          * @return pesan hasil upgrade untuk ditampilkan GUI
          */
     public String upgradeCapacity() {
          double cost = 150.0;
          int amount = 5;
          if (restaurant.getMoney() < cost) {
               return "Uang tidak cukup! Butuh $" + cost
                    + ", tersedia $" + String.format("%.2f", restaurant.getMoney());
          }
          restaurant.upgradeCapacity(amount, cost);
          return "Upgrade berhasil! Kapasitas sekarang: " + restaurant.getCapacity()
               + ". Uang tersisa: $" + String.format("%.2f", restaurant.getMoney());
     }

     // ============================================================
     // FASE BERJUALAN — dipanggil internal setelah startSellingPhase()
     // ============================================================

     private void runSellingPhase() {
          

          List<Customer> incomingCustomers = generateCustomers();
          DiningArea diningArea = restaurant.getDiningArea();

          for (Customer c : incomingCustomers) {
               try {
                    diningArea.admitCustomer(c);
               } 
               catch (CapacityFullException e) {
                    break;
               }
          }
          triggerDisasters();

          List<Menu> availableMenus = getAvailableMenus();
          diningArea.processOrders(restaurant, availableMenus);
     }

     // ============================================================
     // BENCANA
     // ============================================================

     /**
          * Memicu bencana secara random (minimal 1, maksimal 3).
          * Dipanggil otomatis saat fase berjualan dimulai.
          */
     public void triggerDisasters() {
         Random rand = new Random();
     
         if (rand.nextDouble() > 0.20) return;
     
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

     // ============================================================
     // SAVE & LOAD — dipanggil tombol GUI
     // ============================================================

     public void saveGame() {
          saveManager.saveGame(this);
     }

     public void loadGame() {
          saveManager.loadGame(this);
     }

     public boolean hasSaveFile() {
          return saveManager.hasSaveFile();
     }

     // ============================================================
     // HELPER INTERNAL
     // ============================================================

     private List<Customer> generateCustomers() {
          Random rand = new Random();
          List<Customer> customers = new ArrayList<>();
          int totalGroups = 3 + rand.nextInt(restaurant.getCapacity() + 3);
          for (int i = 0; i < totalGroups; i++) {
               customers.add(new Customer());
          }
          return customers;
     }

     private List<Menu> getAvailableMenus() {
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

     // ============================================================
     // GETTER — dipakai GUI untuk membaca state
     // ============================================================

     public int getDay()                      { return day; }
     public boolean isPrepPhase()             { return isPrepPhase; }
     public Restaurant getRestaurant()        { return restaurant; }
     public MenuCatalog getMenuCatalog()      { return menuCatalog; }
     public SupplierShop getSupplierShop()    { return supplierShop; }
     public AmuletShop getAmuletShop()        { return amuletShop; }

     // ============================================================
     // SETTER — dipakai SaveManager saat load
     // ============================================================

     public void setDay(int day)                        { this.day = day; }
     public void setRestaurant(Restaurant restaurant)   { this.restaurant = restaurant; }
}
