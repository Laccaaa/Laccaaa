package cr30;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class Pemesanan1 extends javax.swing.JFrame {
    private Connection con;
    private Statement stat;
    private ResultSet rs;
    

    public Pemesanan1() {
        initComponents();
        initDatabaseConnection();
        updateID();
        updateIDRiwayat();
        populatePelangganComboBox();   
        populateKamarComboBox();
        tampil_tabel();
    }
    
     private Connection getConnection() {
        try {
            return DriverManager.getConnection("jdbc:postgresql://localhost/Hotel", "postgres", "cantikitu5");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void initDatabaseConnection() {
        try {
            Class.forName("org.postgresql.Driver");
            con = getConnection();
            stat = con.createStatement();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, e);
        }
    }

    private void executeUpdate(String query, String message) {
        try (Statement st = con.createStatement()) {
            if (st.executeUpdate(query) == 1) {
                JOptionPane.showMessageDialog(null, "Data " + message + " Successful");
            } else {
                JOptionPane.showMessageDialog(null, "Data Not " + message);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    private void updateID() {
        String sql = "SELECT id_pemesanan FROM pemesanan ORDER BY id_pemesanan DESC LIMIT 1";
        try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                String lastID = rs.getString("id_pemesanan");
                int nextID = Integer.parseInt(lastID.replaceAll("\\D", "")) + 1;
                jTextFieldID.setText("KK00" + nextID);
                txt_id_bantu.setText(String.valueOf(nextID));
            } else {
                jTextFieldID.setText("KK1");
                txt_id_bantu.setText("1");
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, e);
        }
    }

    private void updateIDRiwayat() {
        String sql = "SELECT id_riwayat FROM riwayat_pemesanan ORDER BY id_riwayat DESC LIMIT 1";
        try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                String lastID = rs.getString("id_riwayat");
                int nextID = Integer.parseInt(lastID.replaceAll("\\D", "")) + 1;
                lblIdRiwayat.setText("EQ00" + nextID);
                txt_id_bantu.setText(String.valueOf(nextID));
            } else {
                lblIdRiwayat.setText("EQ1");
                txt_id_bantu.setText("1");
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, e);
        }
    }
    
    private void updateTanggal() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String currentDate = dateFormat.format(Calendar.getInstance().getTime());
        jTextFieldtgl.setText(currentDate);
    }

    private void populatePelangganComboBox() {
        String query = "SELECT nama_pelanggan FROM pelanggan";
        try (ResultSet rs = stat.executeQuery(query)) {
            jComboBoxNama.removeAllItems();
            while (rs.next()) {
                jComboBoxNama.addItem(rs.getString("nama_pelanggan"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, e);
        }
    }

    private void populateKamarComboBox() {
        String checkIn = jTextFieldCheckIn.getText();
        String checkOut = jTextFieldCheckOut.getText();
        String query = "SELECT jenis_ruang FROM ruang WHERE jenis_ruang NOT IN (" +
                "SELECT jenis_ruang FROM riwayat_pemesanan WHERE (tanggal_check_in <= '" + checkIn + "' AND tanggal_check_out >= '" + checkOut + "'))";
        try (ResultSet rs = stat.executeQuery(query)) {
            jComboBoxKamar.removeAllItems();
            while (rs.next()) {
                jComboBoxKamar.addItem(rs.getString("jenis_ruang"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, e);
        }
    }
    
    
    public void tampil_tabel() {
    initDatabaseConnection(); 
    DefaultTableModel tb = new DefaultTableModel();
    tb.addColumn("id_pemesanan");
    tb.addColumn("nama_pelanggan");
    tb.addColumn("tanggal_pemesanan");
    tb.addColumn("jenis_ruang");
    tb.addColumn("tanggal_check_in");
    tb.addColumn("tanggal_check_out");
    jTable2.setModel(tb);

    try {
        // Query SQL yang melakukan JOIN antara tabel pemesanan dan riwayat_pemesanan
        String query = "SELECT p.id_pemesanan, pl.nama_pelanggan, p.tanggal_pemesanan, r.jenis_ruang, r.tanggal_check_in, r.tanggal_check_out "
                + "FROM pemesanan p "
                + "JOIN pelanggan pl ON p.id_pelanggan = pl.id_pelanggan "
                + "JOIN riwayat_pemesanan r ON p.id_pemesanan = r.id_pemesanan;";
        rs = stat.executeQuery(query);

        while (rs.next()) {
            tb.addRow(new Object[]{
                rs.getString("id_pemesanan"),
                rs.getString("nama_pelanggan"),
                rs.getString("tanggal_pemesanan"),
                rs.getString("jenis_ruang"),
                rs.getString("tanggal_check_in"),
                rs.getString("tanggal_check_out")
            });
        }
    } catch (Exception e) {
        JOptionPane.showMessageDialog(null, "Data Gagal Di Tampilkan: " + e.getMessage());
    }
}
    

    private String getPelangganIDByName(String namaPelanggan) {
        String query = "SELECT id_pelanggan FROM pelanggan WHERE nama_pelanggan = '" + namaPelanggan + "'";
        try (ResultSet rs = stat.executeQuery(query)) {
            if (rs.next()) {
                return rs.getString("id_pelanggan");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void addPemesanan() {
        String jenisRuang = jComboBoxKamar.getSelectedItem().toString();
        String query = "SELECT id_ruang, kapasitas, harga_sewa_perhari FROM ruang WHERE jenis_ruang = '" + jenisRuang + "'";
        try (ResultSet rs = stat.executeQuery(query)) {
            if (rs.next()) {
                String idRuang = rs.getString("id_ruang");
                String kapasitas = rs.getString("kapasitas");
                String hargaSewa = rs.getString("harga_sewa_perhari");

                String namaPelanggan = jComboBoxNama.getSelectedItem().toString();
                String idPelanggan = getPelangganIDByName(namaPelanggan);

                if (idPelanggan != null) {
                    String insertPemesananQuery = "INSERT INTO pemesanan (id_pemesanan, id_pelanggan, tanggal_pemesanan, jenis_pemesanan, jumlah) VALUES " +
                            "('" + jTextFieldID.getText() + "', '" + idPelanggan + "', '" + jTextFieldtgl.getText() + "', '" + jComboBoxJenisRuang.getSelectedItem().toString() + "', '1')";
                   String insertRuangQuery = "INSERT INTO riwayat_pemesanan (id_riwayat, id_pemesanan, id_ruang, tanggal_check_in, tanggal_check_out, jenis_ruang) VALUES " +
                                             "('" + lblIdRiwayat.getText() + "', '" + jTextFieldID.getText() + "', '" + idRuang + "', '" + jTextFieldCheckIn.getText() + "', '" + jTextFieldCheckOut.getText() + "', '" + jComboBoxKamar.getSelectedItem().toString() + "')";
                    executeUpdate(insertPemesananQuery, "Ditambahkan");
                    executeUpdate(insertRuangQuery, "Ditambahkan");
                    updateID();
                    updateIDRiwayat();
                    populatePelangganComboBox();
                    populateKamarComboBox();
                } else {
                    JOptionPane.showMessageDialog(null, "Pelanggan tidak ditemukan!");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, e);
        }
    }

    private void deletePemesanan() {
        // Implement the delete logic here
    }


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        txt_id_bantu = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jRadioButtonKamar = new javax.swing.JRadioButton();
        jLabel1 = new javax.swing.JLabel();
        jTextFieldtgl = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jRadioButtonTgl = new javax.swing.JRadioButton();
        jTextFieldID = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jLabel3 = new javax.swing.JLabel();
        jComboBoxJenisRuang = new javax.swing.JComboBox<>();
        jLabel11 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jTextFieldCheckOut = new javax.swing.JTextField();
        jComboBoxKamar = new javax.swing.JComboBox<>();
        jLabel7 = new javax.swing.JLabel();
        BtnTambah = new javax.swing.JButton();
        jTextFieldCheckIn = new javax.swing.JTextField();
        jComboBoxNama = new javax.swing.JComboBox<>();
        lblIdRiwayat = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        txt_id_bantu.setText("jLabel9");

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel9.setText("PEMESANAN");

        jRadioButtonKamar.setText("jRadioButton1");
        jRadioButtonKamar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonKamarActionPerformed(evt);
            }
        });

        jLabel1.setText("Tanggal Transaksi");

        jTextFieldtgl.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldtglActionPerformed(evt);
            }
        });

        jLabel10.setText("ID Pemesanan");

        jRadioButtonTgl.setText("jRadioButton1");
        jRadioButtonTgl.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonTglActionPerformed(evt);
            }
        });

        jTextFieldID.setEditable(false);
        jTextFieldID.setText("jTextField6");

        jLabel2.setText("Nama Tamu");

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane2.setViewportView(jTable2);

        jLabel3.setText("Jenis Ruangan");

        jComboBoxJenisRuang.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Aula", "Resto", "Kamar" }));
        jComboBoxJenisRuang.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxJenisRuangActionPerformed(evt);
            }
        });

        jLabel11.setText("Ruang yang Tersedia");

        jLabel6.setText("Tanggal Check Out");

        jTextFieldCheckOut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldCheckOutActionPerformed(evt);
            }
        });

        jComboBoxKamar.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select Item" }));

        jLabel7.setText("Tanggal Check In");

        BtnTambah.setText("Tambah");
        BtnTambah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnTambahActionPerformed(evt);
            }
        });

        jTextFieldCheckIn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldCheckInActionPerformed(evt);
            }
        });

        jComboBoxNama.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select Item" }));
        jComboBoxNama.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentMoved(java.awt.event.ComponentEvent evt) {
                jComboBoxNamaComponentMoved(evt);
            }
        });
        jComboBoxNama.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxNamaActionPerformed(evt);
            }
        });

        lblIdRiwayat.setText("jLabel4");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(lblIdRiwayat)
                .addGap(221, 221, 221)
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(txt_id_bantu))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(23, 23, 23)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 610, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(147, 147, 147)
                        .addComponent(BtnTambah)))
                .addContainerGap(32, Short.MAX_VALUE))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(8, 8, 8)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jLabel11)
                                .addGroup(layout.createSequentialGroup()
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                            .addComponent(jLabel3)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                            .addComponent(jComboBoxJenisRuang, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(jLabel2)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(jComboBoxNama, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(jLabel10)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(jTextFieldID, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                    .addGap(82, 82, 82)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel6)
                                        .addComponent(jLabel7)
                                        .addComponent(jLabel1))))
                            .addContainerGap(312, Short.MAX_VALUE))
                        .addGroup(layout.createSequentialGroup()
                            .addGap(373, 373, 373)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jTextFieldCheckOut, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(jComboBoxKamar, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(jRadioButtonKamar, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(layout.createSequentialGroup()
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addComponent(jTextFieldtgl)
                                        .addComponent(jTextFieldCheckIn, javax.swing.GroupLayout.DEFAULT_SIZE, 110, Short.MAX_VALUE))
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jRadioButtonTgl, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGap(9, 9, 9)))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txt_id_bantu)
                        .addComponent(lblIdRiwayat))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel9)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 137, Short.MAX_VALUE)
                .addComponent(BtnTambah)
                .addGap(54, 54, 54)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 246, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                            .addGap(49, 49, 49)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel10)
                                .addComponent(jTextFieldID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGap(18, 18, 18)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel2)
                                .addComponent(jComboBoxNama, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGap(14, 14, 14)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jComboBoxJenisRuang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel3)))
                        .addGroup(layout.createSequentialGroup()
                            .addGap(48, 48, 48)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel1)
                                .addComponent(jTextFieldtgl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jRadioButtonTgl))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel7)
                                .addComponent(jTextFieldCheckIn, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel6)
                                .addComponent(jTextFieldCheckOut, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel11)
                                .addComponent(jComboBoxKamar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jRadioButtonKamar))))
                    .addContainerGap(347, Short.MAX_VALUE)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jRadioButtonKamarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonKamarActionPerformed
       if (jRadioButtonKamar.isSelected()) {
            populateKamarComboBox();
        } else {
            jComboBoxKamar.removeAllItems();
        }

        // TODO add your handling code here:
    }//GEN-LAST:event_jRadioButtonKamarActionPerformed

    private void jTextFieldtglActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldtglActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldtglActionPerformed

    private void jRadioButtonTglActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonTglActionPerformed
         if (jRadioButtonTgl.isSelected()) {
            updateTanggal();
        } else {
            jTextFieldtgl.setText(null);
        }

        // TODO add your handling code here:
    }//GEN-LAST:event_jRadioButtonTglActionPerformed

    private void jComboBoxJenisRuangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxJenisRuangActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBoxJenisRuangActionPerformed

    private void jTextFieldCheckOutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldCheckOutActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldCheckOutActionPerformed

    private void BtnTambahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnTambahActionPerformed
        // TODO add your handling code here:
        addPemesanan();
    }//GEN-LAST:event_BtnTambahActionPerformed

    private void jTextFieldCheckInActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldCheckInActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldCheckInActionPerformed

    private void jComboBoxNamaComponentMoved(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jComboBoxNamaComponentMoved
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBoxNamaComponentMoved

    private void jComboBoxNamaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxNamaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBoxNamaActionPerformed

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
            java.util.logging.Logger.getLogger(Pemesanan1.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Pemesanan1.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Pemesanan1.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Pemesanan1.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Pemesanan1().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BtnTambah;
    private javax.swing.JComboBox<String> jComboBoxJenisRuang;
    private javax.swing.JComboBox<String> jComboBoxKamar;
    private javax.swing.JComboBox<String> jComboBoxNama;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JRadioButton jRadioButtonKamar;
    private javax.swing.JRadioButton jRadioButtonTgl;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable2;
    private javax.swing.JTextField jTextFieldCheckIn;
    private javax.swing.JTextField jTextFieldCheckOut;
    private javax.swing.JTextField jTextFieldID;
    private javax.swing.JTextField jTextFieldtgl;
    private javax.swing.JLabel lblIdRiwayat;
    private javax.swing.JLabel txt_id_bantu;
    // End of variables declaration//GEN-END:variables
}
