
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

class BookingHistory {
    private List<Reservation> confirmedBookings = new ArrayList<>();

    public void addReservation(Reservation reservation) {
        confirmedBookings.add(reservation);
    }

    public List<Reservation> getAllReservations() {
        return Collections.unmodifiableList(confirmedBookings); // read-only access
    }
}

class BookingReportService {
    private BookingHistory history;

    public BookingReportService(BookingHistory history) {
        this.history = history;
    }

    public void displayAllBookings() {
        List<Reservation> bookings = history.getAllReservations();
        if (bookings.isEmpty()) {
            System.out.println("No confirmed bookings available.");
            return;
        }
        System.out.println("===== Booking History Report =====");
        for (Reservation r : bookings) {
            r.displayReservation();
        }
        System.out.println("Total Confirmed Bookings: " + bookings.size());
        System.out.println("=================================");
    }
}

public class BookMyStayApp {

    public static void main(String[] args) {

        System.out.println("===== Book My Stay App - Booking History =====");

        BookingHistory bookingHistory = new BookingHistory();
        BookingReportService reportService = new BookingReportService(bookingHistory);

        // Sample confirmed reservations
        Reservation r1 = new Reservation("Alice", "Single Room", "S100");
        Reservation r2 = new Reservation("Bob", "Double Room", "D101");
        Reservation r3 = new Reservation("Charlie", "Suite Room", "SU102");

        // Add confirmed reservations to history
        bookingHistory.addReservation(r1);
        bookingHistory.addReservation(r2);
        bookingHistory.addReservation(r3);

        // Admin generates report
        reportService.displayAllBookings();
    }
}