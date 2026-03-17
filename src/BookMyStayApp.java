
/**
 * Book My Stay App - Use Case 10
 *
 * Demonstrates booking cancellation and inventory rollback.
 * Safely reverts room allocation and updates booking history.
 *
 * Author: Lakshmi M
 * Version: 1.0
 */

import java.util.*;

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
    private Map<String, Integer> roomAvailability = new HashMap<>();
    private Map<String, Stack<String>> releasedRoomIds = new HashMap<>();
    private int roomCounter = 100;

    public InventoryService() {
        roomAvailability.put("Single Room", 2);
        roomAvailability.put("Double Room", 1);
        roomAvailability.put("Suite Room", 1);

        // Initialize stacks for rollback
        releasedRoomIds.put("Single Room", new Stack<>());
        releasedRoomIds.put("Double Room", new Stack<>());
        releasedRoomIds.put("Suite Room", new Stack<>());
    }

    public String allocateRoom(String roomType) throws Exception {
        int available = roomAvailability.getOrDefault(roomType, 0);
        if (available <= 0) {
            throw new Exception("No available rooms of type: " + roomType);
        }
        roomAvailability.put(roomType, available - 1);

        String roomId;
        Stack<String> stack = releasedRoomIds.get(roomType);
        if (!stack.isEmpty()) {
            // Reuse room ID from rollback stack
            roomId = stack.pop();
        } else {
            roomId = roomType.substring(0,1).toUpperCase() + roomCounter++;
        }
        return roomId;
    }

    public void rollbackRoom(String roomType, String roomId) {
        roomAvailability.put(roomType, roomAvailability.getOrDefault(roomType, 0) + 1);
        releasedRoomIds.get(roomType).push(roomId);
    }

    public int getAvailability(String roomType) {
        return roomAvailability.getOrDefault(roomType, 0);
    }
}

class BookingHistory {
    private List<Reservation> confirmedBookings = new ArrayList<>();

    public void addReservation(Reservation reservation) {
        confirmedBookings.add(reservation);
    }

    public boolean removeReservation(String roomId) {
        return confirmedBookings.removeIf(r -> r.getRoomId().equals(roomId));
    }

    public List<Reservation> getAllReservations() {
        return Collections.unmodifiableList(confirmedBookings);
    }
}

class CancellationService {
    private InventoryService inventory;
    private BookingHistory history;

    public CancellationService(InventoryService inventory, BookingHistory history) {
        this.inventory = inventory;
        this.history = history;
    }

    public void cancelReservation(String roomId) {
        Optional<Reservation> res = history.getAllReservations()
                .stream()
                .filter(r -> r.getRoomId().equals(roomId))
                .findFirst();
        if (res.isPresent()) {
            Reservation r = res.get();
            inventory.rollbackRoom(r.getRoomType(), r.getRoomId());
            history.removeReservation(roomId);
            System.out.println("Reservation cancelled successfully for Room ID: " + roomId);
        } else {
            System.out.println("Cancellation failed: Reservation not found for Room ID: " + roomId);
        }
    }
}

public class BookMyStayApp {

    public static void main(String[] args) throws Exception {

        System.out.println("===== Book My Stay App - Booking Cancellation & Rollback =====");

        InventoryService inventory = new InventoryService();
        BookingHistory bookingHistory = new BookingHistory();
        CancellationService cancellationService = new CancellationService(inventory, bookingHistory);

        // Confirm bookings
        Reservation r1 = new Reservation("Alice", "Single Room", inventory.allocateRoom("Single Room"));
        Reservation r2 = new Reservation("Bob", "Double Room", inventory.allocateRoom("Double Room"));
        Reservation r3 = new Reservation("Charlie", "Suite Room", inventory.allocateRoom("Suite Room"));

        bookingHistory.addReservation(r1);
        bookingHistory.addReservation(r2);
        bookingHistory.addReservation(r3);

        System.out.println("Current Bookings:");
        bookingHistory.getAllReservations().forEach(Reservation::displayReservation);

        System.out.println("\n--- Performing Cancellations ---");
        cancellationService.cancelReservation(r2.getRoomId()); // Cancel Bob's reservation
        cancellationService.cancelReservation("INVALID_ID");   // Attempt invalid cancellation

        System.out.println("\nUpdated Bookings After Cancellation:");
        bookingHistory.getAllReservations().forEach(Reservation::displayReservation);

        System.out.println("\nInventory after rollback:");
        System.out.println("Single Room: " + inventory.getAvailability("Single Room"));
        System.out.println("Double Room: " + inventory.getAvailability("Double Room"));
        System.out.println("Suite Room: " + inventory.getAvailability("Suite Room"));
    }
}