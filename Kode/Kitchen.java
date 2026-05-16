public class Kitchen {
     
     private Ingredient[] stockIngredients;
     private int[] stockQuantities;
     private int itemCount; 

     private Menu[] menus;
     private int menuCount; 

     public Kitchen() {
          this.stockIngredients = new Ingredient[50];
          this.stockQuantities = new int[50];
          this.itemCount = 0;

          this.menus = new Menu[40];
          this.menuCount = 0;
     }

     public void addMenu(Menu menu) {
          if (menuCount < menus.length) {
               menus[menuCount] = menu;
               menuCount++;
          } else {
               System.out.println("Kapasitas menu di dapur sudah penuh!");
          }
     }

     public void upgradeMenu(Menu oldMenu, Menu newMenu) {
          for (int i = 0; i < menuCount; i++) {
               if (menus[i] != null && menus[i].getName().equals(oldMenu.getName())) {
                    menus[i] = newMenu;
                    System.out.println("Menu " + oldMenu.getName() + " di-upgrade menjadi " + newMenu.getName());
                    return; 
               }
          }
          System.out.println("Menu lama tidak ditemukan!");
     }

     public void buyIngredient(Ingredient ing, int qty) {
          for (int i = 0; i < itemCount; i++) {
               if (stockIngredients[i] != null && stockIngredients[i].getName().equalsIgnoreCase(ing.getName())) {
                    stockQuantities[i] += qty; 
                    System.out.println("Membeli " + qty + " " + ing.getName() + ". Stok: " + stockQuantities[i]);
                    return;
               }
          }

          if (itemCount < stockIngredients.length) {
               stockIngredients[itemCount] = ing;
               stockQuantities[itemCount] = qty;
               System.out.println("Membeli " + qty + " " + ing.getName() + ". Stok: " + stockQuantities[itemCount]);
               itemCount++;
          } else {
               System.out.println("Rak dapur penuh, tidak bisa menampung jenis bahan baru!");
          }
     }

     private int checkStockAmount(Ingredient ing) {
          for (int i = 0; i < itemCount; i++) {
               if (stockIngredients[i] != null && stockIngredients[i].getName().equalsIgnoreCase(ing.getName())) {
                    return stockQuantities[i];
               }
          }
          return 0; 
     }

     private void reduceStock(Ingredient ing, int qty) {
          for (int i = 0; i < itemCount; i++) {
               if (stockIngredients[i] != null && stockIngredients[i].getName().equalsIgnoreCase(ing.getName())) {
                    stockQuantities[i] -= qty;
                    break;
               }
          }
     }

     public boolean cook(Menu menu) throws OutOfStockException {
          Ingredient[] recipeIngredients = menu.getIngredients();
          int[] recipeQuantities = menu.getQuantities();

          for (int i = 0; i < recipeIngredients.length; i++) {
               Ingredient neededIng = recipeIngredients[i];
               int neededQty = recipeQuantities[i];
               int currentStock = checkStockAmount(neededIng);

               if (currentStock < neededQty) {
                    throw new OutOfStockException("Gagal memasak! Stok " + neededIng.getName() + " tidak mencukupi.");
               }
          }

          for (int i = 0; i < recipeIngredients.length; i++) {
               reduceStock(recipeIngredients[i], recipeQuantities[i]);
          }

          System.out.println("Memasak " + menu.getName() + "...");
          menu.prepare(); 
          return true;
     }

     public double throwUnusedIngredients() {
          double totalLoss = 0;
          System.out.println("Membuang sisa bahan baku hari ini...");

          for (int i = 0; i < itemCount; i++) {
               if (stockIngredients[i] != null && stockQuantities[i] > 0) {
                    double loss = stockIngredients[i].getPrice() * stockQuantities[i];
                    totalLoss += loss;
                    System.out.println("Terbuang: " + stockQuantities[i] + "x " + 
                         stockIngredients[i].getName() + 
                         " (kerugian: Rp." + String.format("%.2f", loss) + ")");
               }
               stockIngredients[i] = null;
               stockQuantities[i] = 0;
          }

          this.itemCount = 0;
          System.out.println("Total kerugian bahan terbuang: Rp." + String.format("%.2f", totalLoss));
          return totalLoss;
     }

     public int getStock(Ingredient ing) {
          for (int i = 0; i < itemCount; i++) {
               if (stockIngredients[i] != null && 
                    stockIngredients[i].getName().equalsIgnoreCase(ing.getName())) {
                    return stockQuantities[i];
               }
          }
          return 0;
     }

     public void applyRatDamage(double lossPercentage) {
          for (int i = 0; i < itemCount; i++) {
               if (stockIngredients[i] != null && stockQuantities[i] > 0) {
                    int lostAmount = (int) Math.floor(stockQuantities[i] * lossPercentage);
                    if (lostAmount > 0) {
                         stockQuantities[i] -= lostAmount;
                         System.out.println("Tikus memakan " + lostAmount + " " + stockIngredients[i].getName() + ". (Sisa: " + stockQuantities[i] + ")");
                    }
               }
          }
     }
}
