import java.sql.*;
import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

public class ClaimDAO {

    // ===== INSERT CLAIM =====
    public boolean insertClaim(String empname, String email, double amount,
                               String description, File proofFile) {
        String sql = "INSERT INTO employee (empname, email, amount, proof, proofname, description, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, 'Pending')";
        
        try (Connection conn = DB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            conn.setAutoCommit(true); // ✅ Ensure changes are saved immediately

            pstmt.setString(1, empname);
            pstmt.setString(2, email);
            pstmt.setDouble(3, amount);

            // Handle proof file
            if (proofFile != null && proofFile.exists()) {
                byte[] fileBytes = Files.readAllBytes(proofFile.toPath());
                pstmt.setBytes(4, fileBytes);
                pstmt.setString(5, proofFile.getName());
            } else {
                pstmt.setNull(4, Types.BINARY);
                pstmt.setNull(5, Types.VARCHAR);
            }

            pstmt.setString(6, description);

            int rows = pstmt.executeUpdate();
            System.out.println("Inserted rows: " + rows);

            // ✅ Send confirmation email to employee
            if (rows > 0) {
                sendEmail(
                    email,
                    "Expense Claim Submitted Successfully",
                    "Hello " + empname + ",\n\n" +
                    "Your expense claim has been successfully submitted for review.\n" +
                    "Amount: ₹" + amount + "\n" +
                    "Description: " + description + "\n\n" +
                    "Current Status: Pending\n\n" +
                    "You will receive another email when your claim is approved or rejected.\n\n" +
                    "Thank you,\nFinance Team"
                );
            }

            return rows > 0;

        } catch (Exception e) {
            System.err.println("✗ insertClaim() error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // ===== GET ALL CLAIMS =====
    public ArrayList<ClaimData> getAllClaims() {
        String sql = "SELECT sr_no, empname, email, amount, proofname, description, status, created_at " +
                     "FROM employee ORDER BY created_at DESC";
        
        ArrayList<ClaimData> list = new ArrayList<>();

        try (Connection conn = DB.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                list.add(new ClaimData(
                        rs.getInt("sr_no"),
                        rs.getString("empname"),
                        rs.getString("email"),
                        rs.getDouble("amount"),
                        rs.getString("proofname"),
                        rs.getString("description"),
                        rs.getString("status"),
                        rs.getTimestamp("created_at")
                ));
            }
            System.out.println("✓ Retrieved claims: " + list.size());
        } catch (Exception e) {
            System.err.println("✗ getAllClaims() error: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    // ===== GET CLAIMS BY STATUS =====
    public ArrayList<ClaimData> getClaimsByStatus(String status) {
        String sql = "SELECT sr_no, empname, email, amount, proofname, description, status, created_at " +
                     "FROM employee WHERE status = ? ORDER BY created_at DESC";
        ArrayList<ClaimData> list = new ArrayList<>();

        try (Connection conn = DB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new ClaimData(
                            rs.getInt("sr_no"),
                            rs.getString("empname"),
                            rs.getString("email"),
                            rs.getDouble("amount"),
                            rs.getString("proofname"),
                            rs.getString("description"),
                            rs.getString("status"),
                            rs.getTimestamp("created_at")
                    ));
                }
            }
        } catch (Exception e) {
            System.err.println("✗ getClaimsByStatus() error: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    // ===== GET CLAIMS BY EMPLOYEE =====
    public ArrayList<ClaimData> getClaimsByEmployee(String empname) {
        String sql = "SELECT sr_no, empname, email, amount, proofname, description, status, created_at " +
                     "FROM employee WHERE empname = ? ORDER BY created_at DESC";
        
        ArrayList<ClaimData> list = new ArrayList<>();

        try (Connection conn = DB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, empname);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new ClaimData(
                            rs.getInt("sr_no"),
                            rs.getString("empname"),
                            rs.getString("email"),
                            rs.getDouble("amount"),
                            rs.getString("proofname"),
                            rs.getString("description"),
                            rs.getString("status"),
                            rs.getTimestamp("created_at")
                    ));
                }
            }
        } catch (Exception e) {
            System.err.println("✗ getClaimsByEmployee() error: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    // ===== UPDATE CLAIM STATUS (Approve / Reject) =====
    public boolean updateClaimStatus(int srNo, String status) {
        String sql = "UPDATE employee SET status = ? WHERE sr_no = ?";
        
        try (Connection conn = DB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            conn.setAutoCommit(true); // ✅ make sure updates apply immediately

            pstmt.setString(1, status);
            pstmt.setInt(2, srNo);

            System.out.println("Executing SQL: " + pstmt.toString()); // debug log
            int rows = pstmt.executeUpdate();

            if (rows > 0) {
                System.out.println("✓ Updated status for sr_no=" + srNo + " → " + status);
                return true;
            } else {
                System.out.println("✗ No rows updated (check sr_no: " + srNo + ")");
                return false;
            }
        } catch (Exception e) {
            System.err.println("✗ updateClaimStatus() error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // ===== GET PROOF IMAGE =====
    public byte[] getProofImage(int srNo) {
        String sql = "SELECT proof FROM employee WHERE sr_no = ?";
        
        try (Connection conn = DB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, srNo);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBytes("proof");
                }
            }
        } catch (Exception e) {
            System.err.println("✗ getProofImage() error: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    // ===== SEND EMAIL FUNCTION =====
    private void sendEmail(String recipient, String subject, String body) {
        final String sender = "aarusheepandagare@gmail.com";
        final String password = "zgqt mklx fxsd tfyk"; // app password
        final String host = "smtp.gmail.com";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(sender, password);
            }
        });

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(sender));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
            message.setSubject(subject);
            message.setText(body);
            Transport.send(message);
            System.out.println("📧 Email sent successfully to: " + recipient);
        } catch (MessagingException e) {
            System.err.println("✗ Failed to send email to: " + recipient);
            e.printStackTrace();
        }
    }
}

// ====== DATA HOLDER CLASS ======
class ClaimData {
    private int srNo;
    private String empname;
    private String email;
    private double amount;
    private String proofname;
    private String description;
    private String status;
    private Timestamp createdAt;

    public ClaimData(int srNo, String empname, String email, double amount,
                     String proofname, String description, String status, Timestamp createdAt) {
        this.srNo = srNo;
        this.empname = empname;
        this.email = email;
        this.amount = amount;
        this.proofname = proofname;
        this.description = description;
        this.status = status;
        this.createdAt = createdAt;
    }

    public int getSrNo() { return srNo; }
    public String getEmpname() { return empname; }
    public String getEmail() { return email; }
    public double getAmount() { return amount; }
    public String getProofname() { return proofname; }
    public String getDescription() { return description; }
    public String getStatus() { return status; }
    public Timestamp getCreatedAt() { return createdAt; }
}
