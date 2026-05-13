class OutOfStockException extends Exception {
    public OutOfStockException(String message) {
        super(message);
    }   
}

class CapacityFullException extends Exception {
    public CapacityFullException(String message) {
        super(message);
    }   
}