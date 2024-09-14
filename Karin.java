/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cr30;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.swing.JOptionPane; 
import javax.swing.table.DefaultTableModel;



/**
 *
 * @author LENOVO
 */
public class Karin extends javax.swing.JFrame {
    Connection con;
    Statement stat;
    ResultSet rs;
    String sql;
    /**
     * Creates new form Karin
     */
    public Karin() {
        initComponents();
        tampil_tabel();
        addKeyListenerToTxtFieldNama();
        Tampil_ID_Nama();
    }
 public Connection getConnection() {
        Connection con; 
        try {
            con = DriverManager.getConnection("jdbc:postgresql://localhost/KhodamKarin", "postgres", "cantikitu5");
            return con;
        } catch (Exception e) { 
            e.printStackTrace();
            return null;
        }
    }

    
     public void koneksi() {
        try {
            Class.forName("org.postgresql.Driver");
            con = DriverManager.getConnection("jdbc:postgresql://localhost:5432/KhodamKarin", "postgres", "cantikitu5"); 
            stat = con.createStatement();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, e);
        }
    }

public void executeQueryPelanggan(String query, String message) {
        Connection con = getConnection();
        Statement st;
        try {
            st = con.createStatement();
            if (st.executeUpdate(query) == 1) {
                JOptionPane.showMessageDialog(null, "Data " + message + " Successful");
            } else {
                JOptionPane.showMessageDialog(null, "Data Not " + message);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }     

public void tampil_tabel() {
    koneksi(); // Ensure connection is established

    DefaultTableModel tb = new DefaultTableModel();
    tb.addColumn("NAMA");
    tb.addColumn("KHODAM");
    jTable1.setModel(tb);

    try {
        // Use PreparedStatement to execute query
        String query = "SELECT nama_orang, jenis_khodam FROM nama";
        rs = stat.executeQuery(query);

        while (rs.next()) {
            tb.addRow(new Object[]{
                rs.getString("nama_orang"),
                rs.getString("jenis_khodam")
            });
        }

    } catch (Exception e) {
        JOptionPane.showMessageDialog(null, "Failed to fetch data: " + e.getMessage());
        e.printStackTrace(); // Print the exception to understand what went wrong
    }
}
  

private void addKeyListenerToTxtFieldNama() {
        txtFieldNama.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtFieldNamaKeyReleased(evt);
            }
        });
    }

    private void txtFieldNamaKeyReleased(java.awt.event.KeyEvent evt) {
        // Set lblNama with the value from txtFieldNama
        lblNama.setText(txtFieldNama.getText());

        // Fetch a random khodam from the database and set lblKhodam
        String randomKhodam = getRandomKhodam();
        lblKhodam.setText(randomKhodam);
    }

    private String getRandomKhodam() {
        String randomKhodam = "";
        koneksi();
        try {
            rs = stat.executeQuery("SELECT nama_khodam FROM khodam ORDER BY RANDOM() LIMIT 1;");
            if (rs.next()) {
                randomKhodam = rs.getString("nama_khodam");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Failed to fetch random Khodam: " + e.getMessage());
        }
        return randomKhodam;
    }

     private void Tampil_ID_Nama() {
        try {
            koneksi(); 
            String sql = "SELECT id_nama FROM nama ORDER BY id_nama DESC LIMIT 1";
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(sql);

            if (rs.next()) {
                String No_Urut = rs.getString("id_nama");
                System.out.println("Last ID fetched from DB: " + No_Urut); 

                int a = Integer.parseInt(No_Urut.replaceAll("\\D", "")); 
                System.out.println("Numeric part of ID: " + a); 

                lblID.setText("O0" + (a + 1));
            } else {
                lblID.setText("O0");
            }
            rs.close();
        } catch (Exception e) {
            e.printStackTrace(); // Print the exception to understand what went wrong
            JOptionPane.showMessageDialog(null, e);
        }
    }
    
      private String getKhodamIDByName(String namaKhodam) {
        String query = "SELECT id_khodam FROM khodam WHERE nama_khodam = '" + namaKhodam + "'";
        try (ResultSet rs = stat.executeQuery(query)) {
            if (rs.next()) {
                return rs.getString("id_khodam");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
     
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txtFieldNama = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        btnTerawang = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        lblNama = new javax.swing.JLabel();
        lblKhodam = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        lblID = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 48)); // NOI18N
        jLabel1.setText("CEK KHODAM");

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel2.setText("Nama yang ingin diterawang");

        txtFieldNama.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtFieldNama.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtFieldNamaActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel3.setText("Nama:");

        btnTerawang.setText("TERAWANG");
        btnTerawang.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTerawangActionPerformed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel4.setText("Khodam:");

        lblNama.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        lblNama.setText("nama");

        lblKhodam.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        lblKhodam.setText("khodam");

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        lblID.setText("jLabel5");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblID, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(67, 67, 67)
                        .addComponent(jLabel1))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(txtFieldNama, javax.swing.GroupLayout.PREFERRED_SIZE, 245, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(btnTerawang))
                            .addComponent(jLabel2)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblNama))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblKhodam))))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(19, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblID))
                .addGap(18, 18, 18)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtFieldNama, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnTerawang, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(26, 26, 26)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(lblNama))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(lblKhodam))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 385, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtFieldNamaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtFieldNamaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtFieldNamaActionPerformed

    private void btnTerawangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTerawangActionPerformed
        // TODO add your handling code here:
 Tampil_ID_Nama();
    String query = "SELECT id_khodam, nama_khodam FROM khodam WHERE nama_khodam = '" + lblKhodam.getText() + "'";
    
    try {
        ResultSet rs = stat.executeQuery(query);
        if (rs.next()) {
            String idKhodam = getKhodamIDByName(lblKhodam.getText());

            if (idKhodam != null) {
                String insertQuery = "INSERT INTO nama (id_nama, nama_orang, id_khodam, jenis_khodam) VALUES ('" + lblID.getText() + "', '" + lblNama.getText() + "','" + idKhodam + "', '" + lblKhodam.getText() + "')";
                executeQueryPelanggan(insertQuery, "Ditambahkan");
                tampil_tabel();
            } else {
                JOptionPane.showMessageDialog(null, "Pelanggan tidak ditemukan!");
            }
        } else {
            JOptionPane.showMessageDialog(null, "Nama orang tidak ditemukan!");
        }
    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, e);
    }
        
    }//GEN-LAST:event_btnTerawangActionPerformed

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
            java.util.logging.Logger.getLogger(Karin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Karin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Karin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Karin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Karin().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnTerawang;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JLabel lblID;
    private javax.swing.JLabel lblKhodam;
    private javax.swing.JLabel lblNama;
    private javax.swing.JTextField txtFieldNama;
    // End of variables declaration//GEN-END:variables
}
