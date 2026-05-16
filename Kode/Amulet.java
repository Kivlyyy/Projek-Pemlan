import java.util.Random;

public abstract class Amulet {
    protected String name;
    protected double effectPercentage; 

    public Amulet(String name, double effectPercentage) {
        this.name = name;
        this.effectPercentage = generateRandomPercentage();
    }

    public abstract void applyEffect();

    public String getName() {
        return name;
    }

    private double generateRandomPercentage() {
        Random rand = new Random();
        double min = 10.0;
        double max = 60.0;
        return min + (max - min) * rand.nextDouble();
    }

    public double getEffectPercentage() {
        return effectPercentage;
    }
}

class CharmingAmulet extends Amulet {
    
    public CharmingAmulet(double effectPercentage) {
        super("Charming Amulet", effectPercentage);
    }

    @Override
    public void applyEffect() {
        System.out.println("[JIMAT AKTIF] Charming Amulet bersinar! Pelanggan ini memberikan uang tip ekstra.");
    }
}

class SecurityAmulet extends Amulet {

    public SecurityAmulet(double effectPercentage) {
        super("Security Amulet", effectPercentage);
    }

    @Override
    public void applyEffect() {
        System.out.println("[JIMAT AKTIF] Security Amulet bekerja! Pelanggan nakal ketakutan dan gagal kabur.");
    }
}

class CleanerAmulet extends Amulet {
    
    public CleanerAmulet(double effectPercentage) {
        super("Cleaner Amulet", effectPercentage);
    }

    @Override
    public void applyEffect() {
        System.out.println("[JIMAT AKTIF] Cleaner Amulet memancarkan aura! Tikus-tikus kabur dari dapur.");
    }
}