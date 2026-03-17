
/**
 * Book My Stay App - Use Case 11
 *
 * Demonstrates thread-safe concurrent booking simulation.
 * Multiple threads submit booking requests simultaneously.
 *
 * Author: Lakshmi M
 * Version: 1.0
 */

import java.util.*;
import java.util.concurrent.*;

class Reservation {
    private String guestName;
    private String roomType;
    private String roomId;

    public Reservation(String guestName, String roomType, String roomId) {
        this.guestName = guestName;
        this.roomType = roomType;
        this.roomId = roomId;
    }

    public String getGuestName() { return guestName; }
    public String getRoomType() { return roomType; }
    public String getRoomId() { return roomId; }

    public void displayReservation() {
        System.out.println("Guest: " + guestName + ", Room Type: " + roomType + ", Room ID: " + roomId);
    }
}

class InventoryService {
    private final Map<String, Integer> roomAvailability = new HashMap<>();
    private int roomCounter = 100;

    public InventoryService() {
        roomAvailability.put("Single Room", 2);
        roomAvailability.put("Double Room", 2);
        roomAvailability.put("Suite Room", 1);
    }

    // Thread-safe allocation
    public synchronized String allocateRoom(String roomType) throws Exception {
        int available = roomAvailability.getOrDefault(roomType, 0);
        if (available <= 0) {
            throw new Exception("No available rooms of type: " + roomType);
        }
        roomAvailability.put(roomType, available - 1);
        return roomType.substring(0,1).toUpperCase() + roomCounter++;
    }

    public synchronized int getAvailability(String roomType) {
        return roomAvailability.getOrDefault(roomType, 0);
    }
}

class BookingHistory {
    private final List<Reservation> confirmedBookings = new ArrayList<>();

    // Thread-safe addition
    public synchronized void addReservation(Reservation reservation) {
        confirmedBookings.add(reservation);
    }

    public synchronized List<Reservation> getAllReservations() {
        return Collections.unmodifiableList(confirmedBookings);
    }
}

class BookingTask implements Runnable {
    private String guestName;
    private String roomType;
    private InventoryService inventory;
    private BookingHistory history;

    public BookingTask(String guestName, String roomType, InventoryService inventory, BookingHistory history) {
        this.guestName = guestName;
        this.roomType = roomType;
        this.inventory = inventory;
        this.history = history;
    }

    @Override
    public void run() {
        try {
            String roomId = inventory.allocateRoom(roomType);
            Reservation reservation = new Reservation(guestName, roomType, roomId);
            history.addReservation(reservation);
            System.out.println("Booking confirmed by thread " + Thread.currentThread().getName());
            reservation.displayReservation();
        } catch (Exception e) {
            System.out.println("Booking failed for " + guestName + ": " + e.getMessage());
        }
    }
}

public class BookMyStayApp {

    public static void main(String[] args) {

        System.out.println("===== Book My Stay App - Concurrent Booking Simulation =====");

        InventoryService inventory = new InventoryService();
        BookingHistory history = new BookingHistory();

        // Simulate multiple guests booking concurrently
        String[] guestNames = {"Alice", "Bob", "Charlie", "David", "Eva"};
        String[] roomTypes = {"Single Room", "Double Room", "Suite Room", "Single Room", "Double Room"};

        ExecutorService executor = Executors.newFixedThreadPool(3);

        for (int i = 0; i < guestNames.length; i++) {
            executor.submit(new BookingTask(guestNames[i], roomTypes[i], inventory, history));
        }

        executor.shutdown();
        try {
            executor.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("\nFinal Booking History:");
        history.getAllReservations().forEach(Reservation::displayReservation);

        System.out.println("\nFinal Inventory Status:");
        System.out.println("Single Room: " + inventory.getAvailability("Single Room"));
        System.out.println("Double Room: " + inventory.getAvailability("Double Room"));
        System.out.println("Suite Room: " + inventory.getAvailability("Suite Room"));
    }
}