package view;

import Form.FormTambahKaryawan;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.HierarchyEvent;
import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import main.Koneksi;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.*;

import javax.swing.JComboBox;

import javax.swing.JLabel;
import javax.swing.JPanel;

import javax.swing.JTable;

import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;


public class FormKelolaKaryawan extends javax.swing.JPanel {
    
    private String currentUserId;

    
    public FormKelolaKaryawan() {
        initComponents();
        
        
        // Tambahkan listener untuk pencarian dinamis saat mengetik
text_cari.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
    public void insertUpdate(javax.swing.event.DocumentEvent e) {
        cariData();
    }

    public void removeUpdate(javax.swing.event.DocumentEvent e) {
        cariData();
    }

    public void changedUpdate(javax.swing.event.DocumentEvent e) {
        cariData();
    }
});

// Tambahkan listener jika kriteria combobox diubah
cb_kriteria.addActionListener(e -> cariData());

        
        
        DefaultTableModel model = new DefaultTableModel(new Object[]{"Id_user", "RFID", "Nama", "Username", "Password", "Role", "Jenis Kelamin", "No Telepon", "Alamat"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Semua sel tidak dapat diedit langsung
            }
        };
        table.setModel(model);
        
        
        
        JTableHeader header = table.getTableHeader();
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = new JLabel(value.toString());
                label.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 16));
                label.setOpaque(true);
                label.setBackground(new Color(0, 123, 255));
                label.setForeground(Color.WHITE);
                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(200, 200, 200)),
                        BorderFactory.createEmptyBorder(10, 0, 10, 0)));
                return label;
            }
        });
        
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 16));
        table.getTableHeader().setOpaque(false);
        table.getTableHeader().setBackground(new Color(0, 102, 204));
        table.getTableHeader().setForeground(Color.WHITE);

        table.setRowHeight(30);
        table.setShowGrid(true);
        table.setGridColor(Color.LIGHT_GRAY);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setSelectionBackground(new Color(204, 229, 255));
        table.setSelectionForeground(Color.BLACK);
        table.setShowVerticalLines(true);
        
        
        
        
        loadData();
        
        
        
        
        // Style tombol
        btn_tambah.setText("TAMBAH");
        btn_tambah.setBackground(new java.awt.Color(70, 130, 180)); // warna biru steel blue
        btn_tambah.setForeground(Color.WHITE);
        btn_tambah.setFont(new java.awt.Font("Serif", Font.BOLD, 12));
        btn_tambah.setFocusPainted(false);
        btn_tambah.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        btn_tambah.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Hover effect
        btn_tambah.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn_tambah.setBackground(new java.awt.Color(100, 149, 237)); // Cornflower Blue
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn_tambah.setBackground(new java.awt.Color(70, 130, 180));
            }
        });
        
        // Style tombol
        btn_edit.setText("EDIT");
        btn_edit.setBackground(new java.awt.Color(70, 130, 180)); // warna biru steel blue
        btn_edit.setForeground(Color.WHITE);
        btn_edit.setFont(new java.awt.Font("Serif", Font.BOLD, 12));
        btn_edit.setFocusPainted(false);
        btn_edit.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        btn_edit.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Hover effect
        btn_edit.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn_edit.setBackground(new java.awt.Color(100, 149, 237)); // Cornflower Blue
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn_edit.setBackground(new java.awt.Color(70, 130, 180));
            }
        });
        
        // Style tombol
        btn_hapus.setText("HAPUS");
        btn_hapus.setBackground(new java.awt.Color(70, 130, 180)); // warna biru steel blue
        btn_hapus.setForeground(Color.WHITE);
        btn_hapus.setFont(new java.awt.Font("Serif", Font.BOLD, 12));
        btn_hapus.setFocusPainted(false);
        btn_hapus.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        btn_hapus.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Hover effect
        btn_hapus.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn_hapus.setBackground(new java.awt.Color(100, 149, 237)); // Cornflower Blue
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn_hapus.setBackground(new java.awt.Color(70, 130, 180));
            }
        });
    }
    
    private void loadData() {
    DefaultTableModel model = (DefaultTableModel) table.getModel();
    model.setRowCount(0); // clear tabel

    String query = "SELECT Id_user, RFID, Nama, Username, Password, Role, Jenis_kelamin, No_Telepon, Alamat FROM users";

    try (Connection conn = Koneksi.getConnection();
         Statement st = conn.createStatement();
         ResultSet rs = st.executeQuery(query)) {

        while (rs.next()) {
            Object[] row = {
                rs.getString("Id_user"),     // kolom 0 (disembunyikan)
                rs.getString("RFID"),
                rs.getString("Nama"),
                rs.getString("Username"),
                rs.getString("Password"),   // kolom 4 (disembunyikan)
                rs.getString("Role"),
                rs.getString("Jenis_kelamin"),
                rs.getString("No_Telepon"),
                rs.getString("Alamat")
            };
            model.addRow(row);
        }

        // Sembunyikan kolom Id_user (index 0)
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setWidth(0);

        // Sembunyikan kolom Password (index 4)
        table.getColumnModel().getColumn(4).setMinWidth(0);
        table.getColumnModel().getColumn(4).setMaxWidth(0);
        table.getColumnModel().getColumn(4).setWidth(0);

    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Error loading data: " + e.getMessage());
    }
}



 private void updateUser() {
    int selectedRow = table.getSelectedRow();
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Pilih data yang ingin diedit terlebih dahulu.");
        return;
    }

    DefaultTableModel model = (DefaultTableModel) table.getModel();

    String idUser = model.getValueAt(selectedRow, 0).toString(); // Id_user
    String rfid = model.getValueAt(selectedRow, 1).toString();
    String nama = model.getValueAt(selectedRow, 2).toString();
    String username = model.getValueAt(selectedRow, 3).toString();
    String role = model.getValueAt(selectedRow, 5).toString();
    String jenisKelamin = model.getValueAt(selectedRow, 6).toString();
    String noTelepon = model.getValueAt(selectedRow, 7).toString().replace("+62", "").replace("62", "").trim();
    String alamat = model.getValueAt(selectedRow, 8).toString();

    JTextField fieldRFID = new JTextField(rfid);
    JTextField fieldNama = new JTextField(nama);
    JTextField fieldUsername = new JTextField(username);
    JComboBox<String> comboRole = new JComboBox<>(new String[]{"admin", "kasir"});
    comboRole.setSelectedItem(role);
    JComboBox<String> comboJK = new JComboBox<>(new String[]{"Laki-laki", "Perempuan"});
    comboJK.setSelectedItem(jenisKelamin);
    JTextField fieldTelp = new JTextField("+62" + noTelepon); // Awali dengan +62
    applyPhoneNumberFilter(fieldTelp); // Terapkan DocumentFilter
    JTextField fieldAlamat = new JTextField(alamat);

    JPanel panel = new JPanel(new GridLayout(0, 1));
    panel.add(new JLabel("RFID:"));
    panel.add(fieldRFID);
    panel.add(new JLabel("Nama:"));
    panel.add(fieldNama);
    panel.add(new JLabel("Username:"));
    panel.add(fieldUsername);
    panel.add(new JLabel("Role:"));
    panel.add(comboRole);
    panel.add(new JLabel("Jenis Kelamin:"));
    panel.add(comboJK);
    panel.add(new JLabel("Nomor Telepon (angka saja, tanpa +62):"));
    panel.add(fieldTelp);
    panel.add(new JLabel("Alamat:"));
    panel.add(fieldAlamat);

    int result = JOptionPane.showConfirmDialog(this, panel, "Edit Data Karyawan", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

    if (result == JOptionPane.OK_OPTION) {
        String newRFID = fieldRFID.getText().trim();
        String newNama = fieldNama.getText().trim();
        String newUsername = fieldUsername.getText().trim();
        String newRole = comboRole.getSelectedItem().toString();
        String newJK = comboJK.getSelectedItem().toString();
        String newTelp = fieldTelp.getText().trim();
        String newAlamat = fieldAlamat.getText().trim();

        // Validasi wajib isi
        if (newRFID.isEmpty() || newNama.isEmpty() || newUsername.isEmpty() || newTelp.isEmpty() || newAlamat.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Semua field harus diisi.");
            return;
        }

        

        if (newTelp.length() < 9 || newTelp.length() > 15) {
            JOptionPane.showMessageDialog(this, "Panjang nomor telepon harus antara 9 sampai 13 digit.");
            return;
        }

        String finalTelp = newTelp;


        try {
            Connection conn = Koneksi.getConnection();
            String sql = "UPDATE users SET RFID=?, Nama=?, Username=?, Role=?, Jenis_kelamin=?, No_Telepon=?, Alamat=? WHERE Id_user=?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, newRFID);
            pst.setString(2, newNama);
            pst.setString(3, newUsername);
            pst.setString(4, newRole);
            pst.setString(5, newJK);
            pst.setString(6, finalTelp);
            pst.setString(7, newAlamat);
            pst.setString(8, idUser);
            pst.executeUpdate();

            JOptionPane.showMessageDialog(this, "Data berhasil diperbarui.");
            loadData();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal memperbarui data: " + e.getMessage());
        }
    }
}


 
         public class PhoneNumberFilter extends DocumentFilter {

    @Override
    public void insertString(DocumentFilter.FilterBypass fb, int offset, String string, AttributeSet attr)
            throws BadLocationException {
        replace(fb, offset, 0, string, attr);
    }

    @Override
    public void replace(DocumentFilter.FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
            throws BadLocationException {
        Document doc = fb.getDocument();
        String currentText = doc.getText(0, doc.getLength());
        StringBuilder newText = new StringBuilder(currentText);
        newText.replace(offset, offset + length, text);

        String cleaned = newText.toString().replaceAll("[^\\d]", "");

        if (!cleaned.startsWith("62")) {
            cleaned = "62" + cleaned;
        }

        String afterPrefix = cleaned.substring(2);

        if (!afterPrefix.isEmpty() && afterPrefix.charAt(0) != '8') {
            java.awt.Toolkit.getDefaultToolkit().beep();
            return;
        }

        if (afterPrefix.length() > 12) {
            java.awt.Toolkit.getDefaultToolkit().beep();
            return;
        }

        fb.replace(0, doc.getLength(), "+" + cleaned, attrs);
    }

    @Override
    public void remove(DocumentFilter.FilterBypass fb, int offset, int length)
            throws BadLocationException {
        if (offset < 3) {
            return; // Blokir penghapusan "+62"
        }
        super.remove(fb, offset, length);
    }
}
         
         
          private void applyPhoneNumberFilter(JTextField field) {
    AbstractDocument doc = (AbstractDocument) field.getDocument();
    doc.setDocumentFilter(new FormKelolaKaryawan.PhoneNumberFilter());
}



          
          
          private void hapusUser() {
    int selectedRow = table.getSelectedRow();
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Pilih data yang ingin dihapus terlebih dahulu.");
        return;
    }

    DefaultTableModel model = (DefaultTableModel) table.getModel();
    String idUser = model.getValueAt(selectedRow, 0).toString();

    // Jika kamu punya session id_user login, misalnya currentUserId
    if (idUser.equals(currentUserId)) {
        JOptionPane.showMessageDialog(this, "Anda tidak dapat menghapus akun Anda sendiri.");
        return;
    }

    int konfirmasi = JOptionPane.showConfirmDialog(
        this,
        "Apakah Anda yakin ingin menghapus data ini?",
        "Konfirmasi Hapus",
        JOptionPane.YES_NO_OPTION
    );

    if (konfirmasi == JOptionPane.YES_OPTION) {
        try (Connection conn = Koneksi.getConnection()) {
            String sql = "DELETE FROM users WHERE Id_user = ?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, idUser);
            int affected = pst.executeUpdate();

            if (affected > 0) {
                JOptionPane.showMessageDialog(this, "Data berhasil dihapus.");
                loadData();
            } else {
                JOptionPane.showMessageDialog(this, "Data gagal dihapus.");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Terjadi kesalahan saat menghapus data: " + e.getMessage());
        }
    }
}

          public void setCurrentUserId(String id) {
    this.currentUserId = id;
}


          
          

          private void cariData() {
    String keyword = text_cari.getText().trim();
    String kriteria = cb_kriteria.getSelectedItem().toString();

    String kolom = switch (kriteria) {
        case "Nama" -> "Nama";
        case "Username" -> "Username";
        case "RFID" -> "RFID";
        case "Role" -> "Role";
        case "Jenis Kelamin" -> "Jenis_kelamin";
        case "No Telepon" -> "No_Telepon";
        case "Alamat" -> "Alamat";
        default -> "Nama";
    };

    DefaultTableModel model = (DefaultTableModel) table.getModel();
    model.setRowCount(0); // clear isi tabel

    String sql = "SELECT Id_user, RFID, Nama, Username, Password, Role, Jenis_kelamin, No_Telepon, Alamat FROM users "
               + "WHERE " + kolom + " LIKE ?";

    try (Connection conn = Koneksi.getConnection();
         PreparedStatement pst = conn.prepareStatement(sql)) {

        pst.setString(1, "%" + keyword + "%");
        ResultSet rs = pst.executeQuery();

        while (rs.next()) {
            Object[] row = {
                rs.getString("Id_user"),
                rs.getString("RFID"),
                rs.getString("Nama"),
                rs.getString("Username"),
                rs.getString("Password"),
                rs.getString("Role"),
                rs.getString("Jenis_kelamin"),
                rs.getString("No_Telepon"),
                rs.getString("Alamat")
            };
            model.addRow(row);
        }

        // Sembunyikan kolom Id_user dan Password
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setWidth(0);
        table.getColumnModel().getColumn(4).setMinWidth(0);
        table.getColumnModel().getColumn(4).setMaxWidth(0);
        table.getColumnModel().getColumn(4).setWidth(0);

    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Gagal mencari data: " + e.getMessage());
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

        jPanel1 = new main.gradasiwarna();
        jLabel1 = new javax.swing.JLabel();
        btn_tambah = new javax.swing.JButton();
        btn_edit = new javax.swing.JButton();
        btn_hapus = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        table = new javax.swing.JTable();
        cb_kriteria = new javax.swing.JComboBox<>();
        text_cari = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();

        setLayout(new java.awt.CardLayout());

        jPanel1.setBackground(new java.awt.Color(0, 51, 255));

        jLabel1.setBackground(new java.awt.Color(0, 51, 255));
        jLabel1.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Menu Karywan");

        btn_tambah.setText("TAMBAH");
        btn_tambah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_tambahActionPerformed(evt);
            }
        });

        btn_edit.setText("EDIT");
        btn_edit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_editActionPerformed(evt);
            }
        });

        btn_hapus.setText("HAPUS");
        btn_hapus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_hapusActionPerformed(evt);
            }
        });

        table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane1.setViewportView(table);

        cb_kriteria.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Nama", "Username", "RFID", "Role", "Jenis Kelamin", "No Telepon", "Alamat" }));
        cb_kriteria.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cb_kriteriaActionPerformed(evt);
            }
        });

        text_cari.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        text_cari.setPreferredSize(new java.awt.Dimension(70, 20));
        text_cari.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                text_cariActionPerformed(evt);
            }
        });
        text_cari.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                text_cariKeyReleased(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Cari  :");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jScrollPane1)
                        .addContainerGap())
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(btn_tambah, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btn_edit, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btn_hapus, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 193, Short.MAX_VALUE)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(cb_kriteria, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(text_cari, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(17, 17, 17))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btn_tambah)
                            .addComponent(btn_edit)
                            .addComponent(btn_hapus)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(text_cari, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(cb_kriteria, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 386, Short.MAX_VALUE)
                .addContainerGap())
        );

        add(jPanel1, "card2");
    }// </editor-fold>//GEN-END:initComponents

    private void btn_tambahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_tambahActionPerformed
    FormTambahKaryawan form = new FormTambahKaryawan((java.awt.Frame) javax.swing.SwingUtilities.getWindowAncestor(this), true);
    form.setLocationRelativeTo(this);
    form.setVisible(true);

    
    
     loadData(); // Refresh tabel
    
    }//GEN-LAST:event_btn_tambahActionPerformed

    private void btn_editActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_editActionPerformed
        updateUser();
    }//GEN-LAST:event_btn_editActionPerformed

    private void btn_hapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_hapusActionPerformed
        hapusUser();
    }//GEN-LAST:event_btn_hapusActionPerformed

    private void text_cariActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_text_cariActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_text_cariActionPerformed

    private void text_cariKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_text_cariKeyReleased
        
    }//GEN-LAST:event_text_cariKeyReleased

    private void cb_kriteriaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cb_kriteriaActionPerformed
        
    }//GEN-LAST:event_cb_kriteriaActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_edit;
    private javax.swing.JButton btn_hapus;
    private javax.swing.JButton btn_tambah;
    private javax.swing.JComboBox<String> cb_kriteria;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable table;
    private javax.swing.JTextField text_cari;
    // End of variables declaration//GEN-END:variables
}
