
import java.util.*;

abstract class Room {
    protected String roomType;
    protected int beds;
    protected double price;

    public Room(String roomType, int beds, double price) {
        this.roomType = roomType;
        this.beds = beds;
        this.price = price;
    }

    public String getRoomType() {
        return roomType;
    }

    public void displayRoomDetails() {
        System.out.println("Room Type: " + roomType + ", Beds: " + beds + ", Price: ₹" + price);
    }
}

class SingleRoom extends Room {
    public SingleRoom() { super("Single Room", 1, 2000); }
}

class DoubleRoom extends Room {
    public DoubleRoom() { super("Double Room", 2, 3500); }
}

class SuiteRoom extends Room {
    public SuiteRoom() { super("Suite Room", 3, 6000); }
}

class Reservation {
    private String guestName;
    private String requestedRoomType;

    public Reservation(String guestName, String requestedRoomType) {
        this.guestName = guestName;
        this.requestedRoomType = requestedRoomType;
    }

    public String getGuestName() { return guestName; }
    public String getRequestedRoomType() { return requestedRoomType; }
}

class InventoryService {
    private Map<String, Integer> roomAvailability = new HashMap<>();
    private Map<String, Set<String>> allocatedRoomIds = new HashMap<>();
    private int roomCounter = 100;

    public InventoryService() {
        roomAvailability.put("Single Room", 5);
        roomAvailability.put("Double Room", 3);
        roomAvailability.put("Suite Room", 2);

        allocatedRoomIds.put("Single Room", new HashSet<>());
        allocatedRoomIds.put("Double Room", new HashSet<>());
        allocatedRoomIds.put("Suite Room", new HashSet<>());
    }

    public boolean allocateRoom(Reservation reservation) {
        String type = reservation.getRequestedRoomType();
        int available = roomAvailability.getOrDefault(type, 0);

        if (available <= 0) return false;

        // Generate unique room ID
        String roomId = type.substring(0,1).toUpperCase() + roomCounter++;
        allocatedRoomIds.get(type).add(roomId);

        // Decrement availability
        roomAvailability.put(type, available - 1);

        System.out.println("Reservation Confirmed for " + reservation.getGuestName());
        System.out.println("Room Type: " + type + ", Assigned Room ID: " + roomId);
        System.out.println("-------------------------------------");
        return true;
    }

    public int getAvailability(String roomType) {
        return roomAvailability.getOrDefault(roomType, 0);
    }
}

class BookingRequestQueue {
    private Queue<Reservation> requestQueue = new LinkedList<>();

    public void addRequest(Reservation reservation) {
        requestQueue.offer(reservation);
    }

    public Reservation getNextRequest() {
        return requestQueue.poll();
    }

    public boolean hasPendingRequests() {
        return !requestQueue.isEmpty();
    }
}

public class BookMyStayApp {

    public static void main(String[] args) {

        System.out.println("===== Book My Stay App - Room Allocation =====");

        // Setup rooms and inventory
        Room[] rooms = {new SingleRoom(), new DoubleRoom(), new SuiteRoom()};
        InventoryService inventory = new InventoryService();
        BookingRequestQueue bookingQueue = new BookingRequestQueue();

        // Sample booking requests
        bookingQueue.addRequest(new Reservation("Alice", "Single Room"));
        bookingQueue.addRequest(new Reservation("Bob", "Double Room"));
        bookingQueue.addRequest(new Reservation("Charlie", "Suite Room"));
        bookingQueue.addRequest(new Reservation("David", "Single Room"));
        bookingQueue.addRequest(new Reservation("Eve", "Suite Room"));

        // Process queued requests
        while (bookingQueue.hasPendingRequests()) {
            Reservation r = bookingQueue.getNextRequest();
            boolean success = inventory.allocateRoom(r);
            if (!success) {
                System.out.println("No available " + r.getRequestedRoomType() + " for " + r.getGuestName());
                System.out.println("-------------------------------------");
            }
        }

        System.out.println("All queued booking requests have been processed.");
    }
}
