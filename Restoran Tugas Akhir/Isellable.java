public interface Isellable {
    String getName();
    double getPrice();
    String getDescription();
    boolean isAvailable(Kitchen inventory);
    void sell(Kitchen inventory);
}

