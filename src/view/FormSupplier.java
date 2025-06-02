package view;

import Form.FormTambahSupplier;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import main.Koneksi;
import java.sql.*;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;

public class FormSupplier extends javax.swing.JPanel {

    public FormSupplier() {
        initComponents();


        
        cb_kriteria.addItem("Nama");
        cb_kriteria.addItem("Nomor_Telepon");
        cb_kriteria.addItem("Alamat");
        cb_kriteria.addItem("Nama_Pemilik");

        DefaultTableModel model = new DefaultTableModel(new Object[]{"Id_Supplier", "Nama", "Telepon", "Alamat", "Pemilik"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tbl_supplier.setModel(model);

        // Sembunyikan kolom Id_Supplier
        tbl_supplier.getColumnModel().getColumn(0).setMinWidth(0);
        tbl_supplier.getColumnModel().getColumn(0).setMaxWidth(0);
        tbl_supplier.getColumnModel().getColumn(0).setWidth(0);

        loadDataSupplier();

        JTableHeader header = tbl_supplier.getTableHeader();
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

        tbl_supplier.setRowHeight(30);
        tbl_supplier.setShowGrid(true);
        tbl_supplier.setGridColor(Color.LIGHT_GRAY);
        tbl_supplier.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tbl_supplier.setSelectionBackground(new Color(204, 229, 255));
        tbl_supplier.setSelectionForeground(Color.BLACK);
        tbl_supplier.setShowVerticalLines(true);

        styleButton(btn_tambah, "TAMBAH");
        styleButton(btn_edit, "EDIT");
        styleButton(btn_hapus, "HAPUS");
    }

    private void styleButton(javax.swing.JButton button, String text) {
        button.setText(text);
        button.setBackground(new java.awt.Color(70, 130, 180));
        button.setForeground(Color.WHITE);
        button.setFont(new java.awt.Font("Serif", Font.BOLD, 12));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new java.awt.Color(100, 149, 237));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new java.awt.Color(70, 130, 180));
            }
        });
    }

    private void loadDataSupplier() {
    DefaultTableModel model = (DefaultTableModel) tbl_supplier.getModel();
    model.setRowCount(0);

    String cari = text_cari.getText().trim();
    String kategori = cb_kriteria.getSelectedItem() != null ? cb_kriteria.getSelectedItem().toString() : "";

    try {
        Connection conn = Koneksi.getConnection();
        String sql = "SELECT * FROM supplier";

        if (!cari.isEmpty() && !kategori.isEmpty()) {
            sql += " WHERE " + kategori + " LIKE ?";
        }

        PreparedStatement ps = conn.prepareStatement(sql);

        if (!cari.isEmpty() && !kategori.isEmpty()) {
            ps.setString(1, "%" + cari + "%");
        }

        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            Object[] row = {
                rs.getInt("Id_Supplier"),
                rs.getString("Nama"),
                rs.getString("Nomor_Telepon"), 
                rs.getString("Alamat"),
                rs.getString("Nama_Pemilik")
            };
            model.addRow(row);
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Gagal load data: " + e.getMessage());
    }
}

    private void updateSupplier() {
    int selectedRow = tbl_supplier.getSelectedRow();
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Pilih data yang ingin diedit.");
        return;
    }

    int idSupplier = (int) tbl_supplier.getValueAt(selectedRow, 0);
    String nama = (String) tbl_supplier.getValueAt(selectedRow, 1);
    String telepon = (String) tbl_supplier.getValueAt(selectedRow, 2); // Sudah dalam format +62
    String alamat = (String) tbl_supplier.getValueAt(selectedRow, 3);
    String pemilik = (String) tbl_supplier.getValueAt(selectedRow, 4);

    JTextField txtNama = new JTextField(nama);
    JTextField txtTelepon = new JTextField(telepon); // Tidak perlu +62 lagi
    applyPhoneNumberFilter(txtTelepon); // Otomatis menambahkan +62 dan validasi
    JTextField txtAlamat = new JTextField(alamat);
    JTextField txtPemilik = new JTextField(pemilik);

    JPanel panel = new JPanel(new GridLayout(0, 1));
    panel.add(new JLabel("Nama Supplier:"));
    panel.add(txtNama);
    panel.add(new JLabel("Nomor Telepon (+62 otomatis):"));
    panel.add(txtTelepon);
    panel.add(new JLabel("Alamat:"));
    panel.add(txtAlamat);
    panel.add(new JLabel("Nama Pemilik:"));
    panel.add(txtPemilik);

    int result = JOptionPane.showConfirmDialog(this, panel, "Edit Data Supplier",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

    if (result == JOptionPane.OK_OPTION) {
        String newNama = txtNama.getText().trim();
        String newTelepon = txtTelepon.getText().trim(); // Sudah terformat +62
        String newAlamat = txtAlamat.getText().trim();
        String newPemilik = txtPemilik.getText().trim();

        if (newNama.isEmpty() || newTelepon.isEmpty() || newAlamat.isEmpty() || newPemilik.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Semua field harus diisi.");
            return;
        }

        // Validasi nomor telepon: harus mulai dengan +628 dan panjang 12–16 karakter
        if (!newTelepon.matches("\\+628\\d{7,10}")) {
            JOptionPane.showMessageDialog(this, "Nomor telepon harus diawali +628 dan panjang 12–13 karakter.");
            return;
        }

        try {
            Connection conn = Koneksi.getConnection();
            String sql = "UPDATE supplier SET Nama=?, Nomor_Telepon=?, Alamat=?, Nama_Pemilik=? WHERE Id_Supplier=?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, newNama);
            pst.setString(2, newTelepon);
            pst.setString(3, newAlamat);
            pst.setString(4, newPemilik);
            pst.setInt(5, idSupplier);
            pst.executeUpdate();

            JOptionPane.showMessageDialog(this, "Data supplier berhasil diupdate.");
            loadDataSupplier();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal update data: " + e.getMessage());
        }
    }
}


    


    private void deleteSupplier() {
        int selectedRow = tbl_supplier.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih data yang ingin dihapus.");
            return;
        }

        int idSupplier = (int) tbl_supplier.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Yakin ingin menghapus data ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                Connection conn = Koneksi.getConnection();
                String sql = "DELETE FROM supplier WHERE Id_Supplier=?";
                PreparedStatement pst = conn.prepareStatement(sql);
                pst.setInt(1, idSupplier);
                pst.executeUpdate();
                JOptionPane.showMessageDialog(this, "Data berhasil dihapus.");
                loadDataSupplier();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Gagal hapus data: " + e.getMessage());
            }
        }
    }


    
     public class PhoneNumberFilter extends DocumentFilter {

    @Override
    public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
            throws BadLocationException {
        replace(fb, offset, 0, string, attr);
    }

    @Override
    public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
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

        if (afterPrefix.length() > 13) {
            java.awt.Toolkit.getDefaultToolkit().beep();
            return;
        }

        fb.replace(0, doc.getLength(), "+" + cleaned, attrs);
    }

    @Override
    public void remove(FilterBypass fb, int offset, int length)
            throws BadLocationException {
        if (offset < 3) {
            return; // Blokir penghapusan "+62"
        }
        super.remove(fb, offset, length);
    }
}


        private void applyPhoneNumberFilter(JTextField field) {
    AbstractDocument doc = (AbstractDocument) field.getDocument();
    doc.setDocumentFilter(new PhoneNumberFilter());
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
        tbl_supplier = new javax.swing.JTable();
        cb_kriteria = new javax.swing.JComboBox<>();
        text_cari = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();

        setLayout(new java.awt.CardLayout());

        jPanel1.setBackground(new java.awt.Color(0, 0, 204));

        jLabel1.setBackground(new java.awt.Color(0, 51, 255));
        jLabel1.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Data Supplier");

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

        tbl_supplier.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane1.setViewportView(tbl_supplier);

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
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 190, Short.MAX_VALUE)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(cb_kriteria, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(text_cari, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(20, 20, 20))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
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

    private void btn_editActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_editActionPerformed
        updateSupplier();
    }//GEN-LAST:event_btn_editActionPerformed

    private void btn_hapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_hapusActionPerformed
        deleteSupplier();
    }//GEN-LAST:event_btn_hapusActionPerformed

    private void btn_tambahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_tambahActionPerformed
        FormTambahSupplier form = new FormTambahSupplier((java.awt.Frame) javax.swing.SwingUtilities.getWindowAncestor(this), true);
        form.setLocationRelativeTo(this);
        form.setVisible(true);
        loadDataSupplier(); // refresh data setelah form ditutup
    }//GEN-LAST:event_btn_tambahActionPerformed

    private void text_cariActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_text_cariActionPerformed
        loadDataSupplier();

    }//GEN-LAST:event_text_cariActionPerformed

    private void text_cariKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_text_cariKeyReleased
        loadDataSupplier();
    }//GEN-LAST:event_text_cariKeyReleased


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_edit;
    private javax.swing.JButton btn_hapus;
    private javax.swing.JButton btn_tambah;
    private javax.swing.JComboBox<String> cb_kriteria;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tbl_supplier;
    private javax.swing.JTextField text_cari;
    // End of variables declaration//GEN-END:variables
}
