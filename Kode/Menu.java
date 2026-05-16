public abstract class Menu implements Isellable {
     protected String name;
     protected double price;
     
     protected Ingredient[] ingredients; 
     protected int[] quantities;         
     
     protected String category;

     public Menu(String name, double price, Ingredient[] ingredients, int[] quantities, String category) {
          this.name = name;
          this.price = price;
          this.ingredients = ingredients;
          this.quantities = quantities;
          this.category = category;
     }

     public String getName() { 
          return name; 
     }
    
     public double getPrice() { 
          return price; 
     }

     public void setPrice(double newPrice) {
         this.price = newPrice;
     }
     
     public Ingredient[] getIngredients() { 
          return ingredients; 
     }
     
     public int[] getQuantities() { 
          return quantities; 
     }
     
     public String getCategory() { 
          return category; 
     }

     @Override
     public void sell(Kitchen inventory) {
          try {
               inventory.cook(this);
               System.out.println(name + " berhasil dijual dan disajikan ke pelanggan!\n");
          } catch (Exception e) {
               System.out.println(e.getMessage());
          }
     }

     @Override
     public boolean isAvailable(Kitchen kitchen) {
          for (int i = 0; i < ingredients.length; i++) {
               if (kitchen.getStock(ingredients[i]) < quantities[i]) {
                    return false;
               }
          }
          return true;
     }

     public abstract String getDescription();

     public abstract void prepare();

     @Override
     public String toString() {
          return String.format("%s - Rp.%.2f", name, price);
     }
}