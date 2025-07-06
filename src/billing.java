/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
import Project.ConnectionProvider;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.awt.Desktop;
import java.awt.Font;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import javax.swing.table.DefaultTableModel;
import java.util.Properties;
import jakarta.mail.*;
import jakarta.mail.internet.*;
import jakarta.activation.*;
import jakarta.activation.*;  
/**
 *
 * @author vaibav
 */
public class billing extends javax.swing.JFrame {
public int finalTotal=0;
    /**
     * Creates new form billing
     */
    public billing() 
    {
        initComponents();
        loadCustomerNames();
        generateBillNumber();
        SimpleDateFormat dFormat=new SimpleDateFormat("dd-MM-yyyy");
        Date date=new Date();
        jLabel25.setText(dFormat.format(date));
        DateTimeFormatter dtf=DateTimeFormatter.ofPattern("HH-mm:ss");
        LocalDateTime now=LocalDateTime.now();
        jLabel26.setText(dtf.format(now));
    }
 public class EmailSender {
    public static void sendEmail(String recipientEmail,String billno) {
        final String senderEmail = "solutionsripple@gmail.com";
        final String senderPassword = "ekiu rprs efyp vkqs";

        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");

        Session session = Session.getInstance(properties, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmail, senderPassword);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(senderEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject("Purchase from Madurai Branch");

            // Body Part (text)
            MimeBodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setText("Dear Sir,\n\n Below we have attached the proof of the purchase of items from our branch");

            // Attachment Part (PDF)
            MimeBodyPart attachmentPart = new MimeBodyPart();
            String filename = "C:/path/to/your/bill.pdf"; // <--- Change this to your actual bill pdf path
            File file = new File("D:\\pdf\\"+billno+".pdf");
            if (file.exists()) {
                attachmentPart.attachFile(file);
            } else {
                System.out.println("Attachment file not found!");
            }

            // Create Multipart
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);
            multipart.addBodyPart(attachmentPart);

            // Set content
            message.setContent(multipart);

            // Send Email
            Transport.send(message);
            System.out.println("Email with PDF sent successfully!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
    public void loadCustomerNames() 
    {
        try 
        {
            Connection con = ConnectionProvider.getCon();
            if (con == null) 
            {
                JOptionPane.showMessageDialog(null, "Database Connection Failed!");
                return;
            }
            Statement st = con.createStatement();
            String query = "SELECT name FROM customer";
            ResultSet rs = st.executeQuery("SELECT * FROM customer;");
            jComboBox1.removeAllItems();
            System.out.println("Loading customer names...");
            boolean found = false;
            while (rs.next()) 
            {
                String customerName = rs.getString("name");
                System.out.println("Found: " + customerName); 
                jComboBox1.addItem(customerName);
                found = true;
            }
            if (!found) 
            {
                JOptionPane.showMessageDialog(null, "No customers found in the database.");
            }
            rs.close();
            st.close();
            con.close();
        } 
        catch (Exception e) 
        {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
        }
    }
    private void fetchCustomerDetails(String name) 
    {
        try 
        {
            Connection con = ConnectionProvider.getCon();
            Statement st = con.createStatement();
            String query = "SELECT custId, contactNo, email, address, gender FROM customer WHERE name = '" + name + "'";
            ResultSet rs = st.executeQuery(query);  
            if (rs.next()) 
            {      
                jTextField1.setText(rs.getString("contactNo"));
                jTextField2.setText(rs.getString("email"));
                jTextField3.setText(rs.getString("address")); 
            } 
            else 
            {
                JOptionPane.showMessageDialog(null, "No customer found with the selected name.");
            }
            rs.close();
            st.close();
            con.close();
        } 
        catch (Exception e) 
        {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
        }
    }
private void fetchLaptopDetails(String brandNo) 
{
    try 
    {
        Connection con = ConnectionProvider.getCon();
        Statement st = con.createStatement();
        String query = "SELECT brand_name, colour, ram, processor, price FROM laptop WHERE brand_no = '" + brandNo + "'";
        ResultSet rs = st.executeQuery(query);
        if (rs.next()) 
        {
            jTextField5.setText(rs.getString("brand_name"));
            jTextField6.setText(rs.getString("colour"));
            jTextField7.setText(rs.getString("ram"));
            jTextField8.setText(rs.getString("processor"));
            jTextField9.setText(rs.getString("price"));
        }
        else 
        {
            JOptionPane.showMessageDialog(null, "No laptop found with the given Brand No.");
        }
        rs.close();
        st.close();
        con.close();
    } 
    catch (Exception e) 
    {
        JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
    }
}
private void generateBillNumber() 
{
    try 
    {
        Connection con = ConnectionProvider.getCon();
        Statement st = con.createStatement();
        String query = "SELECT MAX(billno) FROM billno";
        ResultSet rs = st.executeQuery(query);
        int newBillNo = 1; 
        if (rs.next() && rs.getInt(1) != 0) 
        {
            newBillNo = rs.getInt(1) + 1; 
        }
        jLabel28.setText(String.valueOf(newBillNo));
        rs.close();
        st.close();
        con.close();
    } 
    catch (Exception e) 
    {
        JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
    }
}
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jTextField3 = new javax.swing.JTextField();
        jSeparator2 = new javax.swing.JSeparator();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jTextField4 = new javax.swing.JTextField();
        jTextField5 = new javax.swing.JTextField();
        jTextField6 = new javax.swing.JTextField();
        jTextField7 = new javax.swing.JTextField();
        jTextField8 = new javax.swing.JTextField();
        jTextField9 = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jLabel14 = new javax.swing.JLabel();
        jTextField10 = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jTextField11 = new javax.swing.JTextField();
        jTextField12 = new javax.swing.JTextField();
        jTextField13 = new javax.swing.JTextField();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setLocation(new java.awt.Point(0, 0));
        setMinimumSize(new java.awt.Dimension(1366, 178));
        setUndecorated(true);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(153, 255, 153));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 50)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(102, 204, 255));
        jLabel1.setText("Billing");
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(481, 80, 170, -1));
        jPanel1.add(jSeparator1, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 160, 1355, 10));

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Buyer Details");
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 180, 175, -1));

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Enter Name");
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 240, 100, -1));

        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });
        jPanel1.add(jComboBox1, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 230, 151, -1));

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("Contact No");
        jPanel1.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 310, 80, -1));
        jPanel1.add(jTextField1, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 310, 151, -1));

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("Email");
        jPanel1.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 380, -1, -1));
        jPanel1.add(jTextField2, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 380, 151, -1));

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("Address");
        jPanel1.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 450, 68, -1));
        jPanel1.add(jTextField3, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 450, 151, -1));
        jPanel1.add(jSeparator2, new org.netbeans.lib.awtextra.AbsoluteConstraints(398, 183, -1, 60));

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("Product Details");
        jPanel1.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 170, 175, -1));

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setText("Enter BrandNo");
        jPanel1.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 220, 142, -1));

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(255, 255, 255));
        jLabel9.setText("Name");
        jPanel1.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 320, 100, -1));

        jLabel10.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(255, 255, 255));
        jLabel10.setText("Colour");
        jPanel1.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 380, 100, -1));

        jLabel11.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(255, 255, 255));
        jLabel11.setText("Ram/Rom");
        jPanel1.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 430, 100, -1));

        jLabel12.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(255, 255, 255));
        jLabel12.setText("Processor");
        jPanel1.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 490, 100, -1));

        jLabel13.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(255, 255, 255));
        jLabel13.setText("Price");
        jPanel1.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 560, 100, -1));
        jPanel1.add(jTextField4, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 230, 100, -1));
        jPanel1.add(jTextField5, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 320, 180, -1));

        jTextField6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField6ActionPerformed(evt);
            }
        });
        jPanel1.add(jTextField6, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 380, 180, -1));
        jPanel1.add(jTextField7, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 430, 180, -1));
        jPanel1.add(jTextField8, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 490, 180, -1));
        jPanel1.add(jTextField9, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 560, 180, -1));

        jButton1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/add.jpg"))); // NOI18N
        jButton1.setText("Add");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 620, -1, -1));

        jLabel14.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(255, 255, 255));
        jLabel14.setText("Enter Quantity");
        jPanel1.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 270, 142, -1));
        jPanel1.add(jTextField10, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 270, 100, -1));

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Brand Name", "Brand No", "price", "quantity", "total"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        jPanel1.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(880, 190, 577, 193));

        jLabel15.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(255, 255, 255));
        jLabel15.setText("Calculation");
        jPanel1.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(870, 390, 175, -1));

        jLabel16.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(255, 255, 255));
        jLabel16.setText("Total");
        jPanel1.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(920, 450, 142, -1));

        jLabel17.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(255, 255, 255));
        jLabel17.setText("Advance given");
        jPanel1.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(920, 510, 142, -1));

        jLabel18.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(255, 255, 255));
        jLabel18.setText("Remaining price");
        jPanel1.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(920, 570, 142, -1));
        jPanel1.add(jTextField11, new org.netbeans.lib.awtextra.AbsoluteConstraints(1190, 450, 204, -1));

        jTextField12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField12ActionPerformed(evt);
            }
        });
        jPanel1.add(jTextField12, new org.netbeans.lib.awtextra.AbsoluteConstraints(1190, 510, 204, -1));
        jPanel1.add(jTextField13, new org.netbeans.lib.awtextra.AbsoluteConstraints(1190, 570, 204, -1));

        jButton2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/save.jpg"))); // NOI18N
        jButton2.setText("Save");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(1000, 670, -1, -1));

        jButton3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/reset (1).jpg"))); // NOI18N
        jButton3.setText("Reset");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton3, new org.netbeans.lib.awtextra.AbsoluteConstraints(1180, 670, -1, -1));

        jButton4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/close (2).jpg"))); // NOI18N
        jButton4.setText("Close");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton4, new org.netbeans.lib.awtextra.AbsoluteConstraints(1350, 670, -1, -1));

        jButton5.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton5.setText("Enter");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton5, new org.netbeans.lib.awtextra.AbsoluteConstraints(750, 230, -1, -1));

        jLabel19.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel19.setForeground(new java.awt.Color(255, 255, 255));
        jLabel19.setText("Date");
        jPanel1.add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(810, 100, 43, -1));

        jLabel20.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel20.setForeground(new java.awt.Color(255, 255, 255));
        jLabel20.setText("Time");
        jPanel1.add(jLabel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(810, 130, 43, -1));
        jPanel1.add(jLabel21, new org.netbeans.lib.awtextra.AbsoluteConstraints(1077, 6, 43, -1));
        jPanel1.add(jLabel22, new org.netbeans.lib.awtextra.AbsoluteConstraints(979, 30, 43, -1));
        jPanel1.add(jLabel23, new org.netbeans.lib.awtextra.AbsoluteConstraints(1028, 6, 43, -1));
        jPanel1.add(jLabel24, new org.netbeans.lib.awtextra.AbsoluteConstraints(930, 30, 43, -1));

        jLabel25.setForeground(new java.awt.Color(255, 255, 255));
        jLabel25.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 1, true));
        jPanel1.add(jLabel25, new org.netbeans.lib.awtextra.AbsoluteConstraints(880, 100, 106, 18));

        jLabel26.setForeground(new java.awt.Color(255, 255, 255));
        jLabel26.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 1, true));
        jPanel1.add(jLabel26, new org.netbeans.lib.awtextra.AbsoluteConstraints(880, 130, 106, 18));

        jLabel28.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel28.setForeground(new java.awt.Color(255, 51, 51));
        jPanel1.add(jLabel28, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 110, 70, 42));

        jLabel27.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel27.setForeground(new java.awt.Color(255, 255, 255));
        jLabel27.setText("Bill No");
        jPanel1.add(jLabel27, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 120, -1, -1));

        jLabel29.setIcon(new javax.swing.ImageIcon(getClass().getResource("/anders-jilden-cYrMQA7a3Wc-unsplash.jpg"))); // NOI18N
        jLabel29.setText("jLabel29");
        jLabel29.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 1, true));
        jPanel1.add(jLabel29, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1820, 940));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1820, 950));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTextField6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField6ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField6ActionPerformed

    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed
        // TODO add your handling code here:
        String selectedName = (String) jComboBox1.getSelectedItem();
        fetchCustomerDetails(selectedName);
    }//GEN-LAST:event_jComboBox1ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        // TODO add your handling code here:
        String brandNo = jTextField4.getText();
    fetchLaptopDetails(brandNo);
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
    int price = Integer.parseInt(jTextField9.getText());
    int quantity = Integer.parseInt(jTextField10.getText());
    int total = price * quantity;
    DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
    model.insertRow(0, new Object[]{jTextField5.getText(), jTextField4.getText(), price, quantity, total});
    finalTotal = finalTotal + total;
    jTextField11.setText(String.valueOf(finalTotal));
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jTextField12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField12ActionPerformed
        // TODO add your handling code here:
        String paidAmount=jTextField12.getText();
        int z=Integer.parseInt(paidAmount);
        finalTotal=finalTotal-z;
        String finalTotal1=String.valueOf(finalTotal);
        jTextField13.setText(finalTotal1);
        jTextField13.setEditable(false);
    }//GEN-LAST:event_jTextField12ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // TODO add your handling code here:
        setVisible(false);
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
        setVisible(false);
        new billing().setVisible(true);
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
    String name = (String) jComboBox1.getSelectedItem();
    String billNo = jLabel28.getText();
    String contactNo = jTextField1.getText();
    String email = jTextField2.getText();
    String address = jTextField3.getText();
    String brname=jTextField5.getText();
    
    String path = "D:\\pdf\\"; 
    Document doc = new Document(PageSize.A4);
    try 
    {
        PdfWriter.getInstance(doc, new FileOutputStream(path + jLabel28.getText() + ".pdf"));
        doc.open();
        
        Paragraph title = new Paragraph("Ripple Solutions\nContact: (+91) 9816273172\n\n",FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, Font.BOLD));
        title.setAlignment(Element.ALIGN_CENTER);
        doc.add(title);        
        doc.add(new Paragraph("--------------------------------------------------------------------------------------------------------------------------\n"));
        Paragraph buyerDetails = new Paragraph(
                "Date: " + jLabel25.getText() + "\n" +
                "Bill No: " + billNo + "\n\n" +
                "Buyer Details:\n" +
                "Name: " + name + "\n" +
                "Contact No: " + contactNo + "\n" +
                "Email: " + email + "\n" +
                "Address: " + address + "\n\n",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11));
        doc.add(buyerDetails);
        PdfPTable table = new PdfPTable(5);
        PdfPCell c1 = new PdfPCell(new Phrase("Brand Name", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
        PdfPCell c2 = new PdfPCell(new Phrase("Brand No", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
        PdfPCell c3 = new PdfPCell(new Phrase("Price", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
        PdfPCell c4 = new PdfPCell(new Phrase("Quantity", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
        PdfPCell c5 = new PdfPCell(new Phrase("Sub Total", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
        table.addCell(c1);
        table.addCell(c2);
        table.addCell(c3);
        table.addCell(c4);
        table.addCell(c5);
        for (int i = 0; i < jTable1.getRowCount(); i++) 
        {
            if (jTable1.getValueAt(i, 0) != null) 
            { 
                table.addCell(jTable1.getValueAt(i, 0).toString()); 
                table.addCell(jTable1.getValueAt(i, 1).toString()); 
                table.addCell(jTable1.getValueAt(i, 2).toString()); 
                table.addCell(jTable1.getValueAt(i, 3).toString()); 
                table.addCell(jTable1.getValueAt(i, 4).toString()); 
            }
        }
        doc.add(table);
        Paragraph summary = new Paragraph(
                "\nTotal: " + jTextField11.getText() + "\n" +
                "Paid Amount: " + jTextField12.getText() + "\n" +
                "To be Paid: " + jTextField13.getText() + "\n\n" +
                "Thank You for Your Purchase!",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12));
        summary.setAlignment(Element.ALIGN_CENTER);
        doc.add(summary);
        doc.close();
        String tot=jTextField11.getText();
        int total = Integer.parseInt(tot);
        try 
        {
            Connection con = ConnectionProvider.getCon();
            Statement st = con.createStatement();
            String query = "SELECT MAX(billno) FROM billno";
            ResultSet rs = st.executeQuery(query);
            int newBillNo = 1;
            if (rs.next() && rs.getInt(1) != 0) 
            {
                newBillNo = rs.getInt(1) + 1;
            }
            String insertQuery = "INSERT INTO billno (billno) VALUES (" + newBillNo + ")";
            st.executeUpdate(insertQuery);
            jLabel28.setText(String.valueOf(newBillNo));
            if(total >= 75000){
            String customerInsertQuery = "INSERT INTO cust_bill (bill_id, date, name, email, amount) " +
                                 "VALUES ('" + billNo + "', '" + jLabel25.getText() + "', '" + name + "', '" + email + "', '" + tot + "')";
    st.executeUpdate(customerInsertQuery);
            }
            rs.close();
            st.close();
            con.close();
        }
        catch (Exception e) 
        {
            JOptionPane.showMessageDialog(null, "Database Error: " + e.getMessage());
        }
        EmailSender.sendEmail("vaibavmugesh@gmail.com",billNo);
       // SMSSender.sendSMS(contactNo, "\nRipple Solutions!\n Your purchase "+brname+" for Rs."+tot+" has been confirmed!\n Thank you for choosing Ripple\n");
        JOptionPane.showMessageDialog(null, "Bill Generated Successfully!");
        this.dispose();
        new billing().setVisible(true);
    } 
    catch (Exception e) 
    {
        JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
    }
    }//GEN-LAST:event_jButton2ActionPerformed
private void formWindowOpened(java.awt.event.WindowEvent evt) 
{                                  
    loadCustomerNames();  
}

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(billing.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(billing.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(billing.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(billing.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            new billing().setVisible(true);
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField10;
    private javax.swing.JTextField jTextField11;
    private javax.swing.JTextField jTextField12;
    private javax.swing.JTextField jTextField13;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField5;
    private javax.swing.JTextField jTextField6;
    private javax.swing.JTextField jTextField7;
    private javax.swing.JTextField jTextField8;
    private javax.swing.JTextField jTextField9;
    // End of variables declaration//GEN-END:variables
}