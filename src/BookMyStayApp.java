/**
 * Book My Stay App - Use Case 12
 *
 * Demonstrates persistence and system recovery using file serialization.
 * Booking history and inventory state are saved and restored across application restarts.
 *
 * Author: Lakshmi M
 * Version: 1.0
 */

import java.io.*;
import java.util.*;

class Reservation implements Serializable {
    private static final long serialVersionUID = 1L;

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

class InventoryService implements Serializable {
    private static final long serialVersionUID = 1L;

    private Map<String, Integer> roomAvailability = new HashMap<>();
    private int roomCounter = 100;

    public InventoryService() {
        roomAvailability.put("Single Room", 2);
        roomAvailability.put("Double Room", 2);
        roomAvailability.put("Suite Room", 1);
    }

    public synchronized String allocateRoom(String roomType) throws Exception {
        int available = roomAvailability.getOrDefault(roomType, 0);
        if (available <= 0) throw new Exception("No available rooms of type: " + roomType);
        roomAvailability.put(roomType, available - 1);
        return roomType.substring(0,1).toUpperCase() + roomCounter++;
    }

    public synchronized void restoreAvailability(String roomType, int count) {
        roomAvailability.put(roomType, count);
    }

    public synchronized int getAvailability(String roomType) {
        return roomAvailability.getOrDefault(roomType, 0);
    }

    public synchronized Map<String, Integer> getAllAvailability() {
        return new HashMap<>(roomAvailability);
    }

    public synchronized void setAllAvailability(Map<String, Integer> savedState) {
        roomAvailability = new HashMap<>(savedState);
    }
}

class BookingHistory implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<Reservation> confirmedBookings = new ArrayList<>();

    public synchronized void addReservation(Reservation reservation) {
        confirmedBookings.add(reservation);
    }

    public synchronized List<Reservation> getAllReservations() {
        return Collections.unmodifiableList(confirmedBookings);
    }

    public synchronized void setAllReservations(List<Reservation> savedBookings) {
        confirmedBookings = new ArrayList<>(savedBookings);
    }
}

class PersistenceService {

    private static final String FILENAME = "bookMyStayData.ser";

    public static void saveState(InventoryService inventory, BookingHistory history) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILENAME))) {
            oos.writeObject(inventory.getAllAvailability());
            oos.writeObject(history.getAllReservations());
            System.out.println("System state saved successfully.");
        } catch (IOException e) {
            System.out.println("Error saving state: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public static void loadState(InventoryService inventory, BookingHistory history) {
        File file = new File(FILENAME);
        if (!file.exists()) {
            System.out.println("No previous state found. Starting fresh.");
            return;
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILENAME))) {
            Map<String, Integer> savedInventory = (Map<String, Integer>) ois.readObject();
            List<Reservation> savedBookings = (List<Reservation>) ois.readObject();
            inventory.setAllAvailability(savedInventory);
            history.setAllReservations(savedBookings);
            System.out.println("System state restored successfully.");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error restoring state: " + e.getMessage());
        }
    }
}

public class BookMyStayApp {

    public static void main(String[] args) throws Exception {

        System.out.println("===== Book My Stay App - Data Persistence & Recovery =====");

        InventoryService inventory = new InventoryService();
        BookingHistory history = new BookingHistory();

        // Load previous state if exists
        PersistenceService.loadState(inventory, history);

        // Sample booking if inventory available
        if (inventory.getAvailability("Single Room") > 0) {
            Reservation r1 = new Reservation("Alice", "Single Room", inventory.allocateRoom("Single Room"));
            history.addReservation(r1);
        }
        if (inventory.getAvailability("Double Room") > 0) {
            Reservation r2 = new Reservation("Bob", "Double Room", inventory.allocateRoom("Double Room"));
            history.addReservation(r2);
        }

        System.out.println("\nCurrent Booking History:");
        history.getAllReservations().forEach(Reservation::displayReservation);

        System.out.println("\nCurrent Inventory:");
        System.out.println("Single Room: " + inventory.getAvailability("Single Room"));
        System.out.println("Double Room: " + inventory.getAvailability("Double Room"));
        System.out.println("Suite Room: " + inventory.getAvailability("Suite Room"));

        // Save system state before shutdown
        PersistenceService.saveState(inventory, history);
    }
}