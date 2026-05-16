import java.util.ArrayList;
import java.util.List;

public class SupplierShop extends Shop {
     private List<Ingredient> availableIngredients;
 
     public SupplierShop() {
          super("Toko Bahan Baku");
          this.availableIngredients = new ArrayList<>();
     }
 
     public void addIngredientToCatalog(Ingredient ing) {
          availableIngredients.add(ing);
     }
 
     public Ingredient sellIngredient(String name) {
          for (Ingredient ing : availableIngredients) {
               if (ing.getName().equalsIgnoreCase(name)) {
                    return ing;
               }
          }
          return null;
     }

     public void displayCatalog() {
          System.out.println("\n    DAFTAR BAHAN BAKU TERSEDIA    ");
          for (int i = 0; i < availableIngredients.size(); i++) {
               Ingredient ing = availableIngredients.get(i);
               System.out.println((i + 1) + ". " + ing.getName() + 
                    " — Rp." + String.format("%.2f", ing.getPrice()) + " / unit");
          }
          System.out.println("----------------------------------");
     }
}


