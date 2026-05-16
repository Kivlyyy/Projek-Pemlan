import java.util.List;

public abstract class Disaster {
     protected String name;
     protected double severity;

     public Disaster(String name, double severity) {
          this.name = name;
          this.severity = severity;
     }

     public abstract void trigger(Restaurant restaurant);

     public String getName() {
          return name;
     }

     public double getSeverity() {
          return severity;
     } 
}


class RunawayCustomerDisaster extends Disaster {

     public RunawayCustomerDisaster(double severity) {
          super("Runaway Customer", severity);
     }

     @Override
     public void trigger(Restaurant restaurant) {
          System.out.println("\n    BENCANA: PELANGGAN KABUR    ");
          List<Customer> customers = restaurant.getDiningArea().getCustomers();
          
          if (customers.isEmpty()) {
               System.out.println("Ruang makan kosong. Aman dari pelanggan kabur.");
               return;
          }

          double protection = 0.0;
          for (Amulet amulet : restaurant.getActiveAmulets()) {
               if (amulet instanceof SecurityAmulet) {
                    amulet.applyEffect();
                    protection = amulet.getEffectPercentage() / 100.0;
                    break;
               }
          }

          double finalSeverity = this.severity - protection;
          if (finalSeverity <= 0) {
               System.out.println("Berkat Security Amulet, niat buruk pelanggan berhasil digagalkan secara total!");
               return;
          }

          boolean someoneRunaway = false;
          for (Customer c : customers) {
               if (Math.random() < finalSeverity) {
                    c.setRunaway(true);
                    someoneRunaway = true;
               }
          }

          if (someoneRunaway) {
               System.out.println("Peringatan! Beberapa pelanggan bersiap untuk kabur tanpa membayar!");
          } 
          else {
               System.out.println("Situasi terkendali. Tidak ada yang berani kabur hari ini.");
          }
     }
}

class HungryRatDisaster extends Disaster {

     private double ingredientLossPercentage;

     public HungryRatDisaster(double severity, double ingredientLossPercentage) {
          super("Hungry Rat", severity);
          this.ingredientLossPercentage = ingredientLossPercentage;
     }

     @Override
     public void trigger(Restaurant restaurant) {
          System.out.println("\n    BENCANA: TIKUS LAPAR    ");
          Kitchen kitchen = restaurant.getKitchen();

          double protection = 0.0;
          for (Amulet amulet : restaurant.getActiveAmulets()) {
               if (amulet instanceof CleanerAmulet) {
                    amulet.applyEffect();
                    protection = amulet.getEffectPercentage() / 100.0;
                    break;
               }
          }

          double finalLoss = this.ingredientLossPercentage - protection;
          if (finalLoss <= 0) {
               System.out.println("Berkat Cleaner Amulet, tikus-tikus kabur sebelum sempat menyentuh bahan baku!");
               return;
          }

          System.out.println("Gawat! Rombongan tikus masuk ke dapur dan memakan sekitar " + String.format("%.0f", finalLoss * 100) + "% dari setiap bahan baku.");
          
          kitchen.applyRatDamage(finalLoss);
     }
}