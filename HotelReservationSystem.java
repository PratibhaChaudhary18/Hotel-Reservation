import java.io.*;
import java.util.*;

// ABSTRACT ROOM CLASS  (Abstraction + Encapsulation)
abstract class Room implements Serializable {
    private int roomNumber;
    private String type;
    private double price;
    private boolean booked;

    public Room(int roomNumber, String type, double price) {
        this.roomNumber = roomNumber;
        this.type = type;
        this.price = price;
        this.booked = false;
    }

    public int getRoomNumber() { return roomNumber; }
    public String getType() { return type; }
    public double getPrice() { return price; }
    public boolean isBooked() { return booked; }
    public void setBooked(boolean booked) { this.booked = booked; }

    public abstract String amenities();
}

// SUBCLASSES (Inheritance + Polymorphism)
class StandardRoom extends Room {
    public StandardRoom(int roomNumber) {
        super(roomNumber, "Standard", 2000);
    }
    @Override
    public String amenities() {
        return "AC, Wi-Fi, Television";
    }
}

class DeluxeRoom extends Room {
    public DeluxeRoom(int roomNumber) {
        super(roomNumber, "Deluxe", 3500);
    }
    @Override
    public String amenities() {
        return "AC, Wi-Fi, TV, Mini-Fridge, Breakfast";
    }
}

class SuiteRoom extends Room {
    public SuiteRoom(int roomNumber) {
        super(roomNumber, "Suite", 6000);
    }
    @Override
    public String amenities() {
        return "AC, Wi-Fi, TV, Kitchenette, Jacuzzi, Room-Service";
    }
}

// RESERVATION CLASS
class Reservation implements Serializable {
    private String guestName;
    private int roomNumber;
    private Date checkInDate;
    private int nights;
    private double totalAmount;

    public Reservation(String guestName, int roomNumber, Date checkInDate, int nights, double totalAmount) {
        this.guestName = guestName;
        this.roomNumber = roomNumber;
        this.checkInDate = checkInDate;
        this.nights = nights;
        this.totalAmount = totalAmount;
    }

    public String getGuestName() { return guestName; }
    public int getRoomNumber() { return roomNumber; }
    public double getTotalAmount() { return totalAmount; }

    @Override
    public String toString() {
        return "Guest: " + guestName + " | Room: " + roomNumber + " | Nights: " + nights +
               " | Amount: ₹" + totalAmount + " | Date: " + checkInDate;
    }
}

// PAYMENT SIMULATOR CLASS
class PaymentSimulator {
    public static boolean processPayment(double amount) {
        System.out.println("Processing payment of ₹" + amount + "...");
        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
        System.out.println("✅ Payment successful!");
        return true;
    }
}

// FILE MANAGER CLASS  (File I/O for persistence)
class FileManager {
    private static final String FILE_PATH = "hotelData.ser";

    public static void save(Object obj) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(obj);
            System.out.println("💾 Data saved successfully.");
        } catch (IOException e) {
            System.out.println("Error saving data: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public static Hotel load() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_PATH))) {
            return (Hotel) ois.readObject();
        } catch (Exception e) {
            return null;
        }
    }
}

// HOTEL CLASS  (Composition + Encapsulation)
class Hotel implements Serializable {
    private List<Room> rooms;
    private List<Reservation> reservations;

    public Hotel() {
        rooms = new ArrayList<>();
        reservations = new ArrayList<>();

        // Add sample rooms
        rooms.add(new StandardRoom(101));
        rooms.add(new StandardRoom(102));
        rooms.add(new DeluxeRoom(201));
        rooms.add(new DeluxeRoom(202));
        rooms.add(new SuiteRoom(301));
        rooms.add(new SuiteRoom(302));
    }

    public void showAvailableRooms() {
        System.out.println("\n--- Available Rooms ---");
        boolean any = false;
        for (Room r : rooms) {
            if (!r.isBooked()) {
                System.out.println("Room " + r.getRoomNumber() + " (" + r.getType() +
                                   ") ₹" + r.getPrice() + " | " + r.amenities());
                any = true;
            }
        }
        if (!any) System.out.println("No available rooms.");
    }

    public Room findRoom(int number) {
        for (Room r : rooms) if (r.getRoomNumber() == number) return r;
        return null;
    }

    public void bookRoom(String guest, int roomNum, int nights) {
        Room room = findRoom(roomNum);
        if (room == null) {
            System.out.println("❌ Invalid room number!");
            return;
        }
        if (room.isBooked()) {
            System.out.println("❌ Room already booked!");
            return;
        }

        double total = room.getPrice() * nights;
        if (PaymentSimulator.processPayment(total)) {
            room.setBooked(true);
            Reservation res = new Reservation(guest, roomNum, new Date(), nights, total);
            reservations.add(res);
            System.out.println("✅ Room " + roomNum + " booked successfully for " + guest + "!");
        }
    }

    public void cancelBooking(int roomNum) {
        Reservation toCancel = null;
        for (Reservation r : reservations) {
            if (r.getRoomNumber() == roomNum) {
                toCancel = r;
                break;
            }
        }
        if (toCancel == null) {
            System.out.println("❌ No reservation found for Room " + roomNum);
            return;
        }

        Room room = findRoom(roomNum);
        if (room != null) room.setBooked(false);
        reservations.remove(toCancel);
        System.out.println("❌ Booking for Room " + roomNum + " cancelled.");
    }

    public void showAllReservations() {
        System.out.println("\n--- All Reservations ---");
        if (reservations.isEmpty()) System.out.println("No reservations yet!");
        else reservations.forEach(System.out::println);
    }
}

// MAIN CLASS  (User Interface)
public class HotelReservationSystem {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Hotel hotel = FileManager.load();

        if (hotel == null) {
            hotel = new Hotel();
            System.out.println("🏨 New hotel data created.");
        } else {
            System.out.println("📂 Existing data loaded.");
        }

        while (true) {
            System.out.println("\n===== HOTEL RESERVATION MENU =====");
            System.out.println("1. View Available Rooms");
            System.out.println("2. Book Room");
            System.out.println("3. Cancel Booking");
            System.out.println("4. View All Reservations");
            System.out.println("5. Save & Exit");
            System.out.print("Enter choice: ");
            int ch = sc.nextInt();

            switch (ch) {
                case 1 -> hotel.showAvailableRooms();
                case 2 -> {
                    sc.nextLine();
                    System.out.print("Enter Guest Name: ");
                    String guest = sc.nextLine();
                    System.out.print("Enter Room Number: ");
                    int r = sc.nextInt();
                    System.out.print("Enter No. of Nights: ");
                    int n = sc.nextInt();
                    hotel.bookRoom(guest, r, n);
                }
                case 3 -> {
                    System.out.print("Enter Room Number to Cancel: ");
                    int r = sc.nextInt();
                    hotel.cancelBooking(r);
                }
                case 4 -> hotel.showAllReservations();
                case 5 -> {
                    FileManager.save(hotel);
                    System.out.println("Goodbye!");
                    System.exit(0);
                }
                default -> System.out.println("Invalid option!");
            }
        }
    }
}
