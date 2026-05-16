import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Customer {
    
     private int groupSize;
     protected List<Menu> orders;
     protected int capacityNeeded;
     protected boolean isRunaway;
     private double tipPercentage; 

     public Customer() {
          this.orders = new ArrayList<>();
          Random rand = new Random();
          
          this.groupSize = rand.nextInt(4) + 1; 
          this.capacityNeeded = this.groupSize; 
          this.isRunaway = false; 
          
          this.tipPercentage = rand.nextDouble() * 5.0; 
     }

     public void makeOrder(List<Menu> availableMenus) {
          if (availableMenus.isEmpty()) {
               System.out.println("Rombongan " + groupSize + " orang bingung karena tidak ada menu yang tersedia.");
               return;
          }

          Random rand = new Random();
          int numberOfOrders = this.groupSize + rand.nextInt(3); 
          
          System.out.println("Rombongan " + groupSize + " orang sedang memilih menu...");
          
          for (int i = 0; i < numberOfOrders; i++) {
               Menu selectedMenu = availableMenus.get(rand.nextInt(availableMenus.size()));
               
               orders.add(selectedMenu);
               System.out.println("Memesan: " + selectedMenu.getName());
          }
     }

    public void handleOutOfStock(Menu menu, List<Menu> availableMenus) {
        orders.remove(menu);
        Random rand = new Random();
    
        if (rand.nextBoolean()) {
            // pelanggan pergi, tidak ganti menu
            System.out.println("Pelanggan kecewa dan membatalkan pesanan " + menu.getName());
        } else {
            // pelanggan mau ganti menu lain yang tersedia
            List<Menu> otherMenus = new ArrayList<>(availableMenus);
            otherMenus.remove(menu); // hapus menu yang habis dari pilihan
    
            if (!otherMenus.isEmpty()) {
                Menu replacement = otherMenus.get(rand.nextInt(otherMenus.size()));
                orders.add(replacement);
                System.out.println("Pelanggan mengganti pesanan dengan: " + replacement.getName());
            } 
            else {
                System.out.println("Tidak ada menu lain yang tersedia, pelanggan membatalkan.");
            }
        }
    }

     public void applyCharmingAmulet(double amuletEffectPercentage) {
          this.tipPercentage += amuletEffectPercentage;
          System.out.println("Efek Charming Amulet bekerja! Keinginan tip pelanggan meningkat sebesar " + String.format("%.1f", amuletEffectPercentage) + "%");
     }
     
     public double pay() {
          double baseTotal = 0;
          for (Menu m : orders) {
               baseTotal += m.getPrice();
          }
          
          double tipAmount = baseTotal * (this.tipPercentage / 100.0);
          double finalTotal = baseTotal + tipAmount;
          
          if (tipAmount > 0) {
               System.out.println("Pelanggan puas dan memberikan tip sebesar: " + String.format("%.2f", tipAmount));
          }
          
          return finalTotal;
     }

     public int getCapacityNeeded() {
          return capacityNeeded;
     }

     public List<Menu> getOrders() {
          return orders;
     }

     public boolean isRunaway() {
          return isRunaway;
     }

     public void setRunaway(boolean runaway) {
          this.isRunaway = runaway;
     }
}
