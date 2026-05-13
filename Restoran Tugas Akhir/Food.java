public class Food extends Menu {
     public Food (String name, double price, Ingredient[] ingredients, int[] quantities) {
          super(name, price, ingredients, quantities, "Makanan Utama");
     }
     
     public void prepare() {
        System.out.println("Memasak makanan: " + name);
     }


     public String getDescription() {
          return "[Makanan] " + name + " — sajian utama yang lezat.";
     }
}