import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AmuletShop extends Shop {
     private List<Amulet> availableAmulets;

     public AmuletShop() {
          super("Toko Amulet");
          availableAmulets = new ArrayList<>();
          availableAmulets.add(new CharmingAmulet(0.0));
          availableAmulets.add(new SecurityAmulet(0.0));
          availableAmulets.add(new CleanerAmulet(0.0));
     }

     public List<Amulet> getAvailableAmulets() {
          return availableAmulets;
     }

     public double generateRandomEffect() {
          return new Random().nextDouble() * 50 + 10;
     }

     public Amulet buyAmulet(int choice) {
          if (choice < 1 || choice > availableAmulets.size()) return null;
     
          double randomEffect = generateRandomEffect();
     
          Amulet template = availableAmulets.get(choice - 1);
     
          if (template instanceof CharmingAmulet) return new CharmingAmulet(randomEffect);
          if (template instanceof SecurityAmulet) return new SecurityAmulet(randomEffect);
          if (template instanceof CleanerAmulet)  return new CleanerAmulet(randomEffect);
     
          return null;
     }

}