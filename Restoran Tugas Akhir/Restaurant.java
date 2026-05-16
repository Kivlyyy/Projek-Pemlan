import java.util.ArrayList;
import java.util.List;

public class Restaurant {
     
     private double money;
     private int capacity;
     private Kitchen kitchen;
     private DiningArea diningArea;
     private List<Amulet> activeAmulets;

     public void setKitchen(Kitchen kitchen) {
          this.kitchen = kitchen;
     }

     public Restaurant(int capacity, double money) {
          this.capacity = capacity;
          this.money = money;
          this.diningArea = new DiningArea(capacity);
          this.activeAmulets = new ArrayList<>();
     }

     public void upgradeCapacity(int amount, double cost) {
          this.capacity += amount;
          this.diningArea.updateCapacity(this.capacity);
     }

     public void setMoney(double money) {
    this.money = money;
}

public void setCapacity(int capacity) {
    this.capacity = capacity;
    this.diningArea.updateCapacity(capacity);
}

     public void addAmulet(Amulet amulet) {
          activeAmulets.add(amulet);
     }

     public double getMoney() {
          return money;
     }

     public void addMoney(double amount) {
          this.money += amount;
     }

     public void deductMoney(double amount) {
          this.money -= amount;
     }

     public int getCapacity() {
          return capacity;
     }

     public Kitchen getKitchen() {
          return kitchen;
     }

     public DiningArea getDiningArea() {
        return diningArea;
     }

     public List<Amulet> getActiveAmulets() {
          return activeAmulets;
     }
}
