public class Ingredient {
     private String name;
     private double fixedPrice;

     public Ingredient(String name, double fixedPrice) {
          this.name = name;
          this.fixedPrice = fixedPrice;
     }

     public String getName() {
          return name;
     }

     public double getPrice() {
          return fixedPrice;
     }
}


