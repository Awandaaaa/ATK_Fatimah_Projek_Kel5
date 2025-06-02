package view;

import Form.FormTambahBarangRusak;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GridLayout;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import main.Koneksi;

public class FormBarangRusak extends javax.swing.JPanel {

    public FormBarangRusak() {
        initComponents();
        // Style buttons
        styleButton(btn_tambah, "TAMBAH");
        styleButton(btn_edit, "EDIT");
        styleButton(btn_hapus, "HAPUS");
        // tampilkanBarangRusak("") should be called after construction, e.g., by the parent component
    }

    private void styleButton(JButton button, String text) {
        button.setText(text);
        button.setBackground(new Color(70, 130, 180));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Serif", Font.BOLD, 12));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(100, 149, 237));
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(70, 130, 180));
            }
        });
    }

    public void tampilkanBarangRusak(String keyword) {
        DefaultTableModel model = (DefaultTableModel) table_barangrusak.getModel();
        model.setRowCount(0);

        try (Connection con = Koneksi.getConnection()) {
            String sql = "SELECT br.id_barangrusak, br.id_barang, br.jumlah_rusak, br.Tgl_rusak, br.keterangan " +
                         "FROM barang_rusak br WHERE br.id_barang LIKE ? ORDER BY br.Tgl_rusak ASC";
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, "%" + keyword + "%");
                ResultSet rs = ps.executeQuery();

                while (rs.next()) {
                    model.addRow(new Object[]{
                        rs.getString("id_barangrusak"),
                        rs.getString("id_barang"),
                        rs.getInt("jumlah_rusak"),
                        rs.getDate("Tgl_rusak"),
                        rs.getString("keterangan")
                    });
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                SwingUtilities.getWindowAncestor(this),
                "Gagal menampilkan data barang:\n" + e.getMessage()
            );
        }
    }

    private void updateBarangRusak() {
        int selectedRow = table_barangrusak.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(
                SwingUtilities.getWindowAncestor(this),
                "Pilih data yang ingin diedit."
            );
            return;
        }
        // Ambil data dari tabel
        String idBarangRusak = table_barangrusak.getValueAt(selectedRow, 0).toString();
        String idBarang = table_barangrusak.getValueAt(selectedRow, 1).toString();
        String jumlahRusakStr = table_barangrusak.getValueAt(selectedRow, 2).toString();
        String tglRusak = table_barangrusak.getValueAt(selectedRow, 3).toString();
        String keterangan = table_barangrusak.getValueAt(selectedRow, 4).toString();
        // Input fields
        JTextField txtIdBarang = new JTextField(idBarang);
        JTextField txtJumlahRusak = new JTextField(jumlahRusakStr);
        JTextField txtTglRusak = new JTextField(tglRusak);
        JTextField txtKeterangan = new JTextField(keterangan);
        // Buat panel form
        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("ID Barang:"));
        panel.add(txtIdBarang);
        panel.add(new JLabel("Jumlah Rusak:"));
        panel.add(txtJumlahRusak);
        panel.add(new JLabel("Tanggal Rusak:"));
        panel.add(txtTglRusak);
        panel.add(new JLabel("Keterangan:"));
        panel.add(txtKeterangan);

        int result = JOptionPane.showConfirmDialog(
            SwingUtilities.getWindowAncestor(this),
            panel,
            "Edit Data Barang Rusak",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );
        if (result == JOptionPane.OK_OPTION) {
            try {
                String newIdBarang = txtIdBarang.getText().trim();
                int newJumlahRusak = Integer.parseInt(txtJumlahRusak.getText().trim());
                String newTglRusak = txtTglRusak.getText().trim();
                String newKeterangan = txtKeterangan.getText().trim();

                try (Connection conn = Koneksi.getConnection()) {
                    String sql = "UPDATE barang_rusak SET id_barang=?, jumlah_rusak=?, Tgl_rusak=?, keterangan=? WHERE id_barangrusak=?";
                    PreparedStatement pst = conn.prepareStatement(sql);
                    pst.setString(1, newIdBarang);
                    pst.setInt(2, newJumlahRusak);
                    pst.setString(3, newTglRusak);
                    pst.setString(4, newKeterangan);
                    pst.setString(5, idBarangRusak);
                    pst.executeUpdate();
                    JOptionPane.showMessageDialog(
                        SwingUtilities.getWindowAncestor(this),
                        "Data berhasil diupdate."
                    );
                    tampilkanBarangRusak(""); // Refresh the table
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(
                    SwingUtilities.getWindowAncestor(this),
                    "Gagal update data: " + e.getMessage()
                );
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(
                    SwingUtilities.getWindowAncestor(this),
                    "Jumlah rusak harus berupa angka valid."
                );
            }
        }
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new main.gradasiwarna();
        jLabel1 = new javax.swing.JLabel();
        btn_tambah = new javax.swing.JButton();
        btn_edit = new javax.swing.JButton();
        btn_hapus = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        table_barangrusak = new javax.swing.JTable();
        jLabel3 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();

        setLayout(new java.awt.CardLayout());

        jPanel1.setBackground(new java.awt.Color(0, 51, 255));

        jLabel1.setBackground(new java.awt.Color(0, 51, 255));
        jLabel1.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Data Barang Rusak");

        btn_tambah.setText("TAMBAH");
        btn_tambah.addActionListener(evt -> btn_tambahActionPerformed(evt));

        btn_edit.setText("EDIT");
        btn_edit.addActionListener(evt -> btn_editActionPerformed(evt));

        btn_hapus.setText("HAPUS");
        btn_hapus.addActionListener(evt -> btn_hapusActionPerformed(evt));

        jScrollPane1.setViewportView(table_barangrusak);

        jLabel3.setBackground(new java.awt.Color(0, 51, 255));
        jLabel3.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("08 - 06 - 2025");

        jLabel5.setBackground(new java.awt.Color(0, 51, 255));
        jLabel5.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("User :");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 743, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(btn_tambah, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn_edit, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn_hapus, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel3))
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_tambah)
                    .addComponent(btn_edit)
                    .addComponent(btn_hapus))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 390, Short.MAX_VALUE)
                .addContainerGap())
        );

        add(jPanel1, "card2");
    }// </editor-fold>//GEN-END:initComponents

    private void btn_tambahActionPerformed(java.awt.event.ActionEvent evt) {
        FormTambahBarangRusak form = new FormTambahBarangRusak(
            (java.awt.Frame) SwingUtilities.getWindowAncestor(this), true
        );
        form.setLocationRelativeTo(SwingUtilities.getWindowAncestor(this));
        form.setVisible(true);
        tampilkanBarangRusak("");
    }

    private void btn_editActionPerformed(java.awt.event.ActionEvent evt) {
        updateBarangRusak();
    }

    private void btn_hapusActionPerformed(java.awt.event.ActionEvent evt) {
        int selectedRow = table_barangrusak.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(
                SwingUtilities.getWindowAncestor(this),
                "Pilih baris yang akan dihapus",
                "Peringatan",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(
            SwingUtilities.getWindowAncestor(this),
            "Apakah Anda yakin ingin menghapus data ini?",
            "Konfirmasi Hapus",
            JOptionPane.YES_NO_OPTION
        );
        if (confirm == JOptionPane.YES_OPTION) {
            DefaultTableModel model = (DefaultTableModel) table_barangrusak.getModel();
            String id = model.getValueAt(selectedRow, 0).toString(); // Get the ID of the selected item
            try (Connection con = Koneksi.getConnection()) {
                String sql = "DELETE FROM barang_rusak WHERE id_barangrusak=?";
                PreparedStatement pst = con.prepareStatement(sql);
                pst.setString(1, id);
                pst.executeUpdate();
                model.removeRow(selectedRow);
                JOptionPane.showMessageDialog(
                    SwingUtilities.getWindowAncestor(this),
                    "Data berhasil dihapus",
                    "Sukses",
                    JOptionPane.INFORMATION_MESSAGE
                );
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(
                    SwingUtilities.getWindowAncestor(this),
                    "Gagal menghapus data: " + e.getMessage()
                );
            }
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_edit;
    private javax.swing.JButton btn_hapus;
    private javax.swing.JButton btn_tambah;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable table_barangrusak;
    // End of variables declaration//GEN-END:variables

}