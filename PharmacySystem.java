import java.io.*;
import java.util.*;

public class PharmacySystem {

    // ===========================
    // Medicine CLASS (Model)
    // ===========================
    static class Medicine {
        private String id;
        private String name;
        private int quantity;
        private double price;  // Base price without tax

        public Medicine(String id, String name, int quantity, double price) {
            this.id = id;
            this.name = name;
            this.quantity = quantity;
            this.price = price;
        }

        public String getId() { return id; }
        public String getName() { return name; }
        public int getQuantity() { return quantity; }
        public double getPrice() { return price; }

        public void setQuantity(int quantity) { this.quantity = quantity; }

        @Override
        public String toString() {
            return id + "," + name + "," + quantity + "," + price;
        }
    }

    // =======================================
    // Inventory Manager CLASS
    // =======================================
    static class InventoryManager {

        private HashMap<String, Medicine> inventory = new HashMap<>();
        private final String FILE_NAME = "inventory.txt";

        public InventoryManager() {
            loadFromFile();
            if (inventory.isEmpty()) {
                loadDefaultMedicines();
                saveToFile();
            }
        }

        public void addMedicine(Medicine med) {
            inventory.put(med.getId(), med);
            saveToFile();
            System.out.println("Medicine Added Successfully!");
        }

        public void updateQuantity(String id, int qty) {
            try {
                Medicine med = inventory.get(id);
                med.setQuantity(qty);
                saveToFile();
                System.out.println("Quantity Updated!");
            } catch (Exception e) {
                System.out.println("Invalid Medicine ID!");
            }
        }

        public Medicine getMedicine(String id) {
            return inventory.get(id);
        }

        public void displayAll() {
            System.out.println("\n--- AVAILABLE MEDICINES (with base price) ---");
            for (Medicine m : inventory.values()) {
                System.out.println("ID: " + m.getId() +
                        " | Name: " + m.getName() +
                        " | Qty: " + m.getQuantity() +
                        " | Price (Rs.): " + m.getPrice());
            }
        }

        // ======================
        // 15 DEFAULT MEDICINES
        // ======================
        private void loadDefaultMedicines() {
            addMedicine(new Medicine("M001", "Paracetamol", 50, 25));
            addMedicine(new Medicine("M002", "Amoxicillin", 40, 60));
            addMedicine(new Medicine("M003", "Ibuprofen", 30, 45));
            addMedicine(new Medicine("M004", "Cough Syrup", 20, 80));
            addMedicine(new Medicine("M005", "Vitamin C", 100, 15));
            addMedicine(new Medicine("M006", "Cetirizine", 70, 12));
            addMedicine(new Medicine("M007", "Omeprazole", 25, 50));
            addMedicine(new Medicine("M008", "Metformin", 40, 30));
            addMedicine(new Medicine("M009", "Aspirin", 60, 20));
            addMedicine(new Medicine("M010", "ORS Pack", 90, 18));
            addMedicine(new Medicine("M011", "Insulin", 10, 250));
            addMedicine(new Medicine("M012", "Pain Relief Gel", 20, 95));
            addMedicine(new Medicine("M013", "Antacid Tablets", 75, 10));
            addMedicine(new Medicine("M014", "Bandage Roll", 40, 22));
            addMedicine(new Medicine("M015", "Digital Thermometer", 15, 120));
        }

        // ======================
        // FILE OPERATIONS
        // ======================
        private void saveToFile() {
            try {
                PrintWriter pw = new PrintWriter(new FileWriter(FILE_NAME));
                for (Medicine m : inventory.values()) {
                    pw.println(m.toString());
                }
                pw.close();
            } catch (Exception e) {
                System.out.println("Error saving file!");
            }
        }

        private void loadFromFile() {
            try {
                File file = new File(FILE_NAME);
                if (!file.exists()) return;

                BufferedReader br = new BufferedReader(new FileReader(file));
                String line;

                while ((line = br.readLine()) != null) {
                    String[] p = line.split(",");
                    inventory.put(p[0], new Medicine(
                            p[0], p[1],
                            Integer.parseInt(p[2]),
                            Double.parseDouble(p[3])
                    ));
                }

                br.close();

            } catch (Exception e) {
                System.out.println("Error loading file!");
            }
        }
    }

    // =======================================
    // BILLING MANAGER CLASS
    // =======================================
    static class BillingManager {

        final double CGST = 0.09;
        final double SGST = 0.09;

        public void generateBill(List<Medicine> items) {

            double subtotal = 0;

            System.out.println("\n---------- BILL SUMMARY ----------");
            for (Medicine m : items) {
                System.out.println(m.getName() + " - Rs." + m.getPrice());
                subtotal += m.getPrice();
            }

            double cgstAmount = subtotal * CGST;
            double sgstAmount = subtotal * SGST;
            double totalTax = cgstAmount + sgstAmount;
            double grandTotal = subtotal + totalTax;

            System.out.println("----------------------------------");
            System.out.println("Subtotal: Rs." + subtotal);
            System.out.println("CGST (9%): Rs." + cgstAmount);
            System.out.println("SGST (9%): Rs." + sgstAmount);
            System.out.println("Total Tax: Rs." + totalTax);
            System.out.println("GRAND TOTAL: Rs." + grandTotal);
            System.out.println("----------------------------------");
        }
    }

    // =======================================
    // MAIN METHOD (USER INTERFACE)
    // =======================================
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        InventoryManager inv = new InventoryManager();
        BillingManager bill = new BillingManager();

        while (true) {
            System.out.println("\n====== DIGITAL PHARMACY MANAGEMENT SYSTEM ======");
            System.out.println("1. View Medicines");
            System.out.println("2. Purchase Medicine");
            System.out.println("3. Add Medicine");
            System.out.println("4. Update Quantity");
            System.out.println("5. Exit");
            System.out.print("Choose option: ");

            int ch = sc.nextInt();

            switch (ch) {

                case 1:
                    inv.displayAll();
                    break;

                case 2:
                    List<Medicine> cart = new ArrayList<>();

                    while (true) {
                        System.out.print("Enter Medicine ID to Buy (0 to finish): ");
                        String id = sc.next();

                        if (id.equals("0")) break;

                        Medicine m = inv.getMedicine(id);

                        if (m != null && m.getQuantity() > 0) {
                            cart.add(m);
                            inv.updateQuantity(id, m.getQuantity() - 1);
                            System.out.println(m.getName() + " added.");
                        } else {
                            System.out.println("Invalid ID or Out of Stock!");
                        }
                    }

                    bill.generateBill(cart);
                    break;

                case 3:
                    System.out.print("Enter New Medicine ID: ");
                    String nid = sc.next();
                    System.out.print("Enter Name: ");
                    String name = sc.next();
                    System.out.print("Enter Quantity: ");
                    int qty = sc.nextInt();
                    System.out.print("Enter Base Price: ");
                    double price = sc.nextDouble();
                    inv.addMedicine(new Medicine(nid, name, qty, price));
                    break;

                case 4:
                    System.out.print("Enter Medicine ID: ");
                    String mid = sc.next();
                    System.out.print("Enter New Quantity: ");
                    int newQty = sc.nextInt();
                    inv.updateQuantity(mid, newQty);
                    break;

                case 5:
                    System.out.println("Thank you for using the system!");
                    System.exit(0);

                default:
                    System.out.println("Invalid option!");
            }
        }
    }
}
