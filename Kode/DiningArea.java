import java.util.ArrayList;
import java.util.List;

public class DiningArea {
     
     private int currentCapacity;
     private List<Customer> customers;

     public DiningArea(int maxCapacity) {
          this.currentCapacity = maxCapacity;
          this.customers = new ArrayList<>();
     }

public boolean admitCustomer(Customer customer) throws CapacityFullException {
     int occupiedSeats = 0;
     for (Customer c : customers) {
          occupiedSeats += c.getCapacityNeeded();
     }
     
     if (occupiedSeats >= currentCapacity) {
          throw new CapacityFullException("Restoran sudah penuh! Kapasitas: " + currentCapacity);
     }
     
     if (occupiedSeats + customer.getCapacityNeeded() <= currentCapacity) {
          customers.add(customer);
          System.out.println("Pelanggan dipersilakan duduk. Membutuhkan " 
               + customer.getCapacityNeeded() + " kursi.");
          return true;
     } 
     else {
          System.out.println("Tidak cukup tempat untuk rombongan ini.");
          return false;
     }
}

     public void processOrders(Restaurant restaurant, List<Menu> availableMenus) {
          Kitchen kitchen = restaurant.getKitchen();
          double totalIncome = 0;

          System.out.println("\n    MEMPROSES PESANAN DI MEJA    ");
          for (Customer customer : customers) {
               
               customer.makeOrder(availableMenus);
               
               if (customer.isRunaway()) {
                    System.out.println("BENCANA! Pelanggan kabur tidak mau membayar pesanan!");
                    for (Menu m : customer.getOrders()) {
                         try {
                         kitchen.cook(m);
                         } 
                         catch (Exception e) {
                         
                         }
                    }
                    continue; 
               }

               double customerBill = 0;
               List<Menu> orders = customer.getOrders();
               
               for (int i = 0; i < orders.size(); i++) {
                    Menu m = orders.get(i);
                    try {
                         kitchen.cook(m);
                         customerBill += m.getPrice();
                    } 
                    catch (OutOfStockException e) {
                         System.out.println(e.getMessage());
                         customer.handleOutOfStock(m, availableMenus);
                    }
               }

               if (customerBill > 0) {
                    double payment = customer.pay(); 
                    
                    for (Amulet amulet : restaurant.getActiveAmulets()) {
                         if (amulet instanceof CharmingAmulet) {
                              amulet.applyEffect();
                              payment += (payment * (amulet.getEffectPercentage() / 100.0));
                              break;
                         }
                    }

                    restaurant.addMoney(payment);
                    totalIncome += payment;
                    System.out.println("💰 Pelanggan membayar: Rp." + String.format("%.2f", payment));
               }
          }
          System.out.println("---------------------------------");
          System.out.println("Total Pendapatan Sesi Ini: Rp." + String.format("%.2f", totalIncome));
     }

     public void processSingleCustomerOrder(Restaurant restaurant, Customer customer, List<Menu> availableMenus) {
          Kitchen kitchen = restaurant.getKitchen();
          double customerBill = 0;

          customer.makeOrder(availableMenus);
          
          if (customer.isRunaway()) {
               System.out.println("BENCANA! Pelanggan kabur tidak mau membayar pesanan!");
               for (Menu m : customer.getOrders()) {
                    try { kitchen.cook(m); } 
                    catch (Exception e) {}
               }
               return; 
          }

          List<Menu> orders = customer.getOrders();
          for (int i = 0; i < orders.size(); i++) {
               Menu m = orders.get(i);
               try {
                    kitchen.cook(m);
                    customerBill += m.getPrice();
               } catch (OutOfStockException e) {
                    System.out.println(e.getMessage());
                    customer.handleOutOfStock(m, availableMenus);
               }
          }

          if (customerBill > 0) {
               double payment = customer.pay(); 
               
               for (Amulet amulet : restaurant.getActiveAmulets()) {
                    if (amulet instanceof CharmingAmulet) {
                         amulet.applyEffect();
                         payment += (payment * (amulet.getEffectPercentage() / 100.0));
                         break;
                    }
               }

               restaurant.addMoney(payment);
               System.out.println("Pelanggan membayar: Rp." + String.format("%.2f", payment));
          }
          System.out.println("---------------------------------");
     }

     public void clearCustomers() {
          customers.clear();
     }

     public void updateCapacity(int newCapacity) {
          this.currentCapacity = newCapacity;
     }

     public int getCurrentCapacity() {
          return currentCapacity;
     }

     public List<Customer> getCustomers() {
          return customers;
     }
}
