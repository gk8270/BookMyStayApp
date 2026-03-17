
import java.util.*;

class Reservation {
    private String guestName;
    private String roomId;
    private String roomType;

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

class Service {
    private String serviceName;
    private double cost;

    public Service(String serviceName, double cost) {
        this.serviceName = serviceName;
        this.cost = cost;
    }

    public String getServiceName() { return serviceName; }
    public double getCost() { return cost; }

    public void displayService() {
        System.out.println("- " + serviceName + ": ₹" + cost);
    }
}

class AddOnServiceManager {
    private Map<String, List<Service>> reservationServices = new HashMap<>();

    public void addServiceToReservation(String roomId, Service service) {
        reservationServices.computeIfAbsent(roomId, k -> new ArrayList<>()).add(service);
    }

    public double calculateTotalCost(String roomId) {
        List<Service> services = reservationServices.get(roomId);
        if (services == null) return 0;
        return services.stream().mapToDouble(Service::getCost).sum();
    }

    public void displayServices(String roomId) {
        List<Service> services = reservationServices.get(roomId);
        if (services == null || services.isEmpty()) {
            System.out.println("No add-on services selected for this reservation.");
            return;
        }
        System.out.println("Add-On Services for Room ID " + roomId + ":");
        for (Service s : services) {
            s.displayService();
        }
        System.out.println("Total Additional Cost: ₹" + calculateTotalCost(roomId));
    }
}

public class BookMyStayApp {

    public static void main(String[] args) {

        System.out.println("===== Book My Stay App - Add-On Services =====");

        // Sample confirmed reservations
        Reservation r1 = new Reservation("Alice", "Single Room", "S100");
        Reservation r2 = new Reservation("Bob", "Double Room", "D101");

        // Add-On services
        Service breakfast = new Service("Breakfast", 500);
        Service spa = new Service("Spa Session", 1500);
        Service airportPickup = new Service("Airport Pickup", 800);

        AddOnServiceManager serviceManager = new AddOnServiceManager();

        // Guests select services
        serviceManager.addServiceToReservation(r1.getRoomId(), breakfast);
        serviceManager.addServiceToReservation(r1.getRoomId(), spa);
        serviceManager.addServiceToReservation(r2.getRoomId(), airportPickup);

        // Display reservations and associated services
        r1.displayReservation();
        serviceManager.displayServices(r1.getRoomId());
        System.out.println("-------------------------------------");

        r2.displayReservation();
        serviceManager.displayServices(r2.getRoomId());
        System.out.println("-------------------------------------");

        System.out.println("Add-on service selection complete. Core booking and inventory remain unchanged.");
    }
}