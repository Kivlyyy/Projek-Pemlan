import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

public class SaveManager {
     private final String SAVE_FILE = "savegame.txt";

     public void saveGame(GameEngine engine) {
          try (PrintWriter writer = new PrintWriter(new FileWriter(SAVE_FILE))) {
               // Menyimpan data penting ke dalam file text
               writer.println("DAY:" + engine.getDay());
               writer.println("MONEY:" + engine.getRestaurant().getMoney());
               writer.println("CAPACITY:" + engine.getRestaurant().getCapacity());
               System.out.println("Progress game berhasil disimpan ke " + SAVE_FILE + "!");
          } catch (IOException e) {
               System.out.println("Gagal menyimpan game: " + e.getMessage());
          }
     }

     public void loadGame(GameEngine engine) {
          File file = new File(SAVE_FILE);
          if (!file.exists()) {
               System.out.println("Tidak ada file save game yang ditemukan.");
               return;
          }

          try (Scanner scanner = new Scanner(file)) {
               while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    String[] parts = line.split(":");
                    if (parts.length == 2) {
                         String key = parts[0];
                         String value = parts[1];

                         switch (key) {
                              case "DAY":
                                   engine.setDay(Integer.parseInt(value));
                                   break;
                              case "MONEY":
                                   double savedMoney = Double.parseDouble(value);
                                   double currentMoney = engine.getRestaurant().getMoney();
                                   if (savedMoney > currentMoney) {
                                        engine.getRestaurant().addMoney(savedMoney - currentMoney);
                                   } else {
                                        engine.getRestaurant().deductMoney(currentMoney - savedMoney);
                                   }
                                   break;
                              case "CAPACITY":
                                   int savedCap = Integer.parseInt(value);
                                   int currentCap = engine.getRestaurant().getCapacity();
                                   if (savedCap > currentCap) {
                                        // Upgrade dengan cost 0 untuk menyesuaikan kapasitas dari save file
                                        engine.getRestaurant().upgradeCapacity(savedCap - currentCap, 0); 
                                   }
                                   break;
                         }
                    }
               }
               System.out.println("Progress game berhasil diload dari " + SAVE_FILE + "!");
          } catch (Exception e) {
               System.out.println("Gagal meload game: " + e.getMessage());
          }
     }

     public boolean hasSaveFile() {
          return new File(SAVE_FILE).exists();
     }
}