public class MenuCatalog {
    
     private Menu[] menuList;
     private int menuCount;

     public MenuCatalog() {
          this.menuList = new Menu[20];
          this.menuCount = 0;
          initCatalog();
     }

    public Menu[] getMenuList() {
        return menuList;
    }
    
    public int getMenuCount() {
        return menuCount;
    }

     private void initCatalog() {
          Ingredient bread = new Ingredient("Bread", 3000);
          Ingredient egg = new Ingredient("Egg", 2000);
          Ingredient bacon = new Ingredient("Bacon", 5000);
          Ingredient tea = new Ingredient("Tea", 2000);
          Ingredient sugar = new Ingredient("Sugar", 1000);
          Ingredient potato = new Ingredient("Potato", 4000);
          Ingredient oil = new Ingredient("Oil", 3000);

          try {
               Ingredient[] sandwichIngredients = {bread, egg, bacon};
               int[] sandwichQty = {2, 1, 1};
               addMenu(new Food("Breakfast Sandwich", 15000, sandwichIngredients, sandwichQty));

               Ingredient[] baconEggIngredients = {egg, bacon, oil};
               int[] baconEggQty = {2, 2, 1};
               addMenu(new Food("Bacon and Eggs", 18000, baconEggIngredients, baconEggQty));

               Ingredient[] sweetTeaIngredients = {tea, sugar};
               int[] sweetTeaQty = {1, 2};
               addMenu(new Drink("Sweet Tea", 6000, sweetTeaIngredients, sweetTeaQty, DrinkType.MILK_BASED));

               Ingredient[] plainTeaIngredients = {tea};
               int[] plainTeaQty = {1};
               addMenu(new Drink("Plain Tea", 4000, plainTeaIngredients, plainTeaQty, DrinkType.FRUIT_BASED));

               Ingredient[] friesIngredients = {potato, oil};
               int[] friesQty = {2, 1};
               addMenu(new Snack("French Fries", 12000, friesIngredients, friesQty));
                 
          } 
          catch (CapacityFullException e) {
               System.out.println(e.getMessage());
          }
     }

     public void displayCatalog() {
          for (int i = 0; i < menuList.length; i++) {
               if (menuList[i] != null) {
                    System.out.println((i + 1) + ". " + menuList[i].toString());
                    System.out.println("   Description: " + menuList[i].getDescription());
                    
                    System.out.println("   Ingredients needed:");
                    Ingredient[] ingredients = menuList[i].getIngredients();
                    int[] quantities = menuList[i].getQuantities();
                    
                    for (int j = 0; j < ingredients.length; j++) {
                         System.out.println("      - " + ingredients[j].getName() + " (needs " + quantities[j] + ")");
                    }
                    System.out.println("---------------------------------------");
               }
          }
     }

     public Menu getMenuByChoice(int choice) {
          int index = choice - 1; 
          
          if (index >= 0 && index < menuList.length && menuList[index] != null) {
               return menuList[index];
          }
          return null; 
     }

     public void addMenu(Menu newMenu) throws CapacityFullException {
          if (menuCount < menuList.length) {
               menuList[menuCount] = newMenu;
               menuCount++;
               System.out.println("Menu baru [" + newMenu.getName() + "] berhasil ditambahkan ke Katalog!");
          } else {
               throw new CapacityFullException("Gagal! Buku katalog menu sudah penuh.");
          }
     }
}