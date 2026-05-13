public class Snack extends Menu {
    
     public Snack(String name, double price, Ingredient[] ingredients, int[] quantities) {
          super(name, price, ingredients, quantities, "Camilan");
     }

     public void prepare() {
          System.out.println("Menyiapkan camilan: " + name);
     }
     
     public String getDescription() {
          return "[Camilan] " + name + " — camilan nikmat untuk menemani.";
     }
}