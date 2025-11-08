import java.sql.*;
import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;

// ===================== DATABASE CONNECTION CLASS =====================
class DB {
    private static final String URL = "jdbc:postgresql://localhost:5432/Chai.Co";
    private static final String USER = "postgres";
    private static final String PASSWORD = "pgadmin@1804";

    static {
        try {
            Class.forName("org.postgresql.Driver");
            System.out.println("✓ PostgreSQL Driver loaded successfully!");
        } catch (ClassNotFoundException e) {
            System.err.println("✗ PostgreSQL Driver not found!");
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
        conn.setAutoCommit(true);
        System.out.println("✓ Database connection established!");
        return conn;
    }

    public static void closeConnection(Connection conn) {
        if (conn != null) try {
            conn.close();
            System.out.println("Connection closed.");
        } catch (SQLException e) {
            System.err.println("Error closing connection!");
            e.printStackTrace();
        }
    }
}

// ===================== EMPLOYEE STATUS CLASS =====================
class EmployeeStatus {
    private String empName, email, status, description;

    public EmployeeStatus(String empName, String email, String status, String description) {
        this.empName = empName;
        this.email = email;
        this.status = status;
        this.description = description;
    }

    public String getEmpName() { return empName; }
    public String getEmail() { return email; }
    public String getStatus() { return status; }
    public String getDescription() { return description; }
}

// ===================== MAIN EMAIL SENDER CLASS =====================
public class SendStatusEmails {

    private static final String SENDER = "aarusheepandagare@gmail.com";
    private static final String PASSWORD = "zgqt mklx fxsd tfyk"; // Gmail app password

    public static void main(String[] args) {
        List<EmployeeStatus> employees = fetchStatusUpdates();

        if (employees.isEmpty()) {
            System.out.println("No status updates found.");
            return;
        }

        for (EmployeeStatus emp : employees) {
            String subject = "Expense Claim Update - " + emp.getEmpName();
            String body = "Hello " + emp.getEmpName() + ",\n\n"
                        + "Your expense claim has been updated.\n"
                        + "Status: " + emp.getStatus() + "\n"
                        + "Description: " + emp.getDescription() + "\n\n"
                        + "Thank you,\nFinance Team";
            sendEmail(emp.getEmail(), subject, body);
        }
    }

    // ===== Fetch employee emails and status =====
    public static List<EmployeeStatus> fetchStatusUpdates() {
        List<EmployeeStatus> list = new ArrayList<>();
        String sql = "SELECT empname, email, status, description FROM employee WHERE status IN ('Approved', 'Rejected')";

        try (Connection conn = DB.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(new EmployeeStatus(
                        rs.getString("empname"),
                        rs.getString("email"),
                        rs.getString("status"),
                        rs.getString("description")
                ));
            }
            System.out.println("Fetched " + list.size() + " employees to email.");

        } catch (Exception e) {
            System.err.println("Error fetching employee emails: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    // ===== Send email to one recipient =====
    public static void sendEmail(String recipient, String subject, String body) {
        String host = "smtp.gmail.com";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SENDER, PASSWORD);
            }
        });

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(SENDER));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
            message.setSubject(subject);
            message.setText(body);
            Transport.send(message);
            System.out.println("✅ Email sent to: " + recipient);
        } catch (MessagingException e) {
            System.err.println("✗ Failed to send email to: " + recipient);
            e.printStackTrace();
        }
    }
}
