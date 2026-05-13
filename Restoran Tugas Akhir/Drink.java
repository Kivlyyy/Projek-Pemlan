public class Drink extends Menu {
     private DrinkType type;

     public Drink(String name, double price, Ingredient[] ingredients, int[] quantities, DrinkType type) {
          super(name, price, ingredients, quantities, "Minuman");
          this.type = type;
     }

     public DrinkType getType() { 
          return type; 
     }

     public void prepare() {
          System.out.println("Meracik minuman (" + type + "): " + name);
     }

     public String getDescription() {
          return "[Minuman] " + name + " — minuman segar pilihan.";
     }
}