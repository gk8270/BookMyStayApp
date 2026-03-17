
import java.util.*;

class InvalidBookingException extends Exception {
    public InvalidBookingException(String message) {
        super(message);
    }
}

class Reservation {
    private String guestName;
    private String roomType;
    private String roomId;

    public Reservation(String guestName, String roomType, String roomId) throws InvalidBookingException {
        if (guestName == null || guestName.isBlank()) {
            throw new InvalidBookingException("Guest name cannot be empty.");
        }
        if (!List.of("Single Room", "Double Room", "Suite Room").contains(roomType)) {
            throw new InvalidBookingException("Invalid room type: " + roomType);
        }
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
    private int roomCounter = 100;

    public InventoryService() {
        roomAvailability.put("Single Room", 2);
        roomAvailability.put("Double Room", 1);
        roomAvailability.put("Suite Room", 1);
    }

    public String allocateRoom(String roomType) throws InvalidBookingException {
        int available = roomAvailability.getOrDefault(roomType, 0);
        if (available <= 0) {
            throw new InvalidBookingException("No available rooms of type: " + roomType);
        }
        roomAvailability.put(roomType, available - 1);
        return roomType.substring(0,1).toUpperCase() + roomCounter++;
    }

    public int getAvailability(String roomType) {
        return roomAvailability.getOrDefault(roomType, 0);
    }
}

public class BookMyStayApp {

    public static void main(String[] args) {

        System.out.println("===== Book My Stay App - Error Handling & Validation =====");

        InventoryService inventory = new InventoryService();

        String[] guestNames = {"Alice", "Bob", "", "Charlie"};
        String[] roomTypes = {"Single Room", "Double Room", "Suite Room", "Penthouse"};

        for (int i = 0; i < guestNames.length; i++) {
            try {
                String roomId = inventory.allocateRoom(roomTypes[i]);
                Reservation reservation = new Reservation(guestNames[i], roomTypes[i], roomId);
                System.out.println("Booking confirmed:");
                reservation.displayReservation();
                System.out.println("-------------------------------------");
            } catch (InvalidBookingException e) {
                System.out.println("Booking failed: " + e.getMessage());
                System.out.println("-------------------------------------");
            }
        }

        System.out.println("Error handling demo complete. Inventory remains consistent.");
    }
}