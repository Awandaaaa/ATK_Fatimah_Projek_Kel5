package view;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import java.awt.Color;
import java.awt.Cursor;
import javax.swing.BorderFactory;

import Form.FormTambahBarang;
import java.awt.Component;

import main.Koneksi;
import java.sql.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;

import javax.swing.JLabel;
import java.util.Locale;

import java.awt.Graphics2D;
import java.awt.Font;
import java.awt.FontMetrics;

import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridLayout;

import java.awt.image.BufferedImage;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;

import javax.swing.*;
import javax.swing.text.NumberFormatter;
import java.text.NumberFormat;
import java.text.ParseException;


public class FormInventori extends javax.swing.JPanel {

    private Connection conn;

    public FormInventori() {
        initComponents();

        text_cari.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                cariBarang();
            }

            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                cariBarang();
            }

            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                cariBarang();
            }
        });

        DefaultTableModel model = new DefaultTableModel(new Object[]{"No Barang", "Nama Barang", "Kategori", "Satuan", "Harga", "Stok", "Barcode", "ID Supplier"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table_barang.setModel(model);

        JTableHeader header = table_barang.getTableHeader();
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

        table_barang.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 16));
        table_barang.getTableHeader().setOpaque(false);
        table_barang.getTableHeader().setBackground(new Color(0, 102, 204));
        table_barang.getTableHeader().setForeground(Color.WHITE);

        table_barang.setRowHeight(30);
        table_barang.setShowGrid(true);
        table_barang.setGridColor(Color.LIGHT_GRAY);
        table_barang.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table_barang.setSelectionBackground(new Color(204, 229, 255));
        table_barang.setSelectionForeground(Color.BLACK);
        table_barang.setShowVerticalLines(true);

        tampilkanBarang();

        btn_tambah.setText("TAMBAH");
        btn_tambah.setBackground(new java.awt.Color(70, 130, 180)); // warna biru steel blue
        btn_tambah.setForeground(Color.WHITE);
        btn_tambah.setFont(new java.awt.Font("Serif", Font.BOLD, 12));
        btn_tambah.setFocusPainted(false);
        btn_tambah.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        btn_tambah.setCursor(new Cursor(Cursor.HAND_CURSOR));

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

        btn_hapus.setText("HAPUS");
        btn_hapus.setBackground(new java.awt.Color(70, 130, 180)); // warna biru steel blue
        btn_hapus.setForeground(Color.WHITE);
        btn_hapus.setFont(new java.awt.Font("Serif", Font.BOLD, 12));
        btn_hapus.setFocusPainted(false);
        btn_hapus.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        btn_hapus.setCursor(new Cursor(Cursor.HAND_CURSOR));

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

        btn_edit.setText("EDIT");
        btn_edit.setBackground(new java.awt.Color(70, 130, 180));
        btn_edit.setForeground(Color.WHITE);
        btn_edit.setFont(new java.awt.Font("Serif", Font.BOLD, 12));
        btn_edit.setFocusPainted(false);
        btn_edit.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        btn_edit.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn_edit.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn_edit.setBackground(new java.awt.Color(100, 149, 237));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn_edit.setBackground(new java.awt.Color(70, 130, 180));
            }
        });

        btn_cetakbarcode.setText("CETAK BARCODE");
        btn_cetakbarcode.setBackground(new java.awt.Color(70, 130, 180));
        btn_cetakbarcode.setForeground(Color.WHITE);
        btn_cetakbarcode.setFont(new java.awt.Font("Serif", Font.BOLD, 12));
        btn_cetakbarcode.setFocusPainted(false);
        btn_cetakbarcode.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        btn_cetakbarcode.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Hover effect
        btn_cetakbarcode.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn_cetakbarcode.setBackground(new java.awt.Color(100, 149, 237));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn_cetakbarcode.setBackground(new java.awt.Color(70, 130, 180));
            }
        });
    }

    private String getKolomDariKriteria(String kriteria) {
        switch (kriteria) {
            case "Nama Barang":
                return "b.Nama_barang";
            case "Kategori":
                return "b.Kategori";
            case "Satuan":
                return "b.Satuan";
            case "Harga":
                return "b.Harga";
            case "Stok":
                return "b.Stok";
            case "Barcode":
                return "b.barcode";
            case "Supplier":
                return "s.nama";
            default:
                return "b.Nama_barang";
        }
    }

    private void cariBarang() {
        String keyword = text_cari.getText().trim();
        String kriteria = cb_kriteria.getSelectedItem().toString();
        String kolom = getKolomDariKriteria(kriteria);

        DefaultTableModel model = (DefaultTableModel) table_barang.getModel();
        model.setRowCount(0);

        try {
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost/atk", "root", "");
            String sql = "SELECT b.Id_barang, b.Nama_barang, b.Kategori, b.Satuan, b.Harga, b.Stok, b.barcode, s.nama AS nama_supplier "
                    + "FROM barang b LEFT JOIN supplier s ON b.Id_Supplier = s.id_supplier "
                    + "WHERE " + kolom + " LIKE ?";

            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, "%" + keyword + "%");
            ResultSet rs = pst.executeQuery();

            NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));

            while (rs.next()) {
                String hargaFormatted = formatRupiah.format(rs.getDouble("Harga"));
                model.addRow(new Object[]{
                    rs.getString("Id_barang"),
                    rs.getString("Nama_barang"),
                    rs.getString("Kategori"),
                    rs.getString("Satuan"),
                    hargaFormatted,
                    rs.getString("Stok"),
                    rs.getString("barcode"),
                    rs.getString("nama_supplier")
                });
            }

            rs.close();
            pst.close();
            con.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal mencari data: " + e.getMessage());
        }
    }

    public void tampilkanBarang() {
        DefaultTableModel model = (DefaultTableModel) table_barang.getModel();
        model.setRowCount(0);

        try {
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost/atk", "root", "");
            String sql = "SELECT b.Id_barang, b.Nama_barang, b.Kategori, b.Satuan, b.Harga, b.Stok, b.barcode, s.nama AS nama_supplier "
                    + "FROM barang b "
                    + "LEFT JOIN supplier s ON b.Id_Supplier = s.id_supplier";
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(sql);

            NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));

            while (rs.next()) {
                String hargaFormatted = formatRupiah.format(rs.getDouble("Harga"));

                model.addRow(new Object[]{
                    rs.getString("Id_barang"),
                    rs.getString("Nama_barang"),
                    rs.getString("Kategori"),
                    rs.getString("Satuan"),
                    hargaFormatted,
                    rs.getString("Stok"),
                    rs.getString("barcode"),
                    rs.getString("nama_supplier")
                });
            }

            rs.close();
            st.close();
            con.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal menampilkan data barang: " + e.getMessage());
        }
    }

   private void updatebarang() {
    int selectedRow = table_barang.getSelectedRow();
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Pilih data yang ingin diedit.");
        return;
    }

    // Ambil data dari tabel
    String idbarang = table_barang.getValueAt(selectedRow, 0).toString();
    String namabarang = table_barang.getValueAt(selectedRow, 1).toString();
    String kategori = table_barang.getValueAt(selectedRow, 2).toString();
    String satuan = table_barang.getValueAt(selectedRow, 3).toString();
    String hargaFormatted = table_barang.getValueAt(selectedRow, 4).toString();
    String stokStr = table_barang.getValueAt(selectedRow, 5).toString();

    double harga;
    int stok;

    try {
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        Number hargaNumber = formatRupiah.parse(hargaFormatted);
        harga = hargaNumber.doubleValue();
        stok = Integer.parseInt(stokStr);
    } catch (NumberFormatException | ParseException e) {
        JOptionPane.showMessageDialog(this, "Format harga atau stok tidak valid: " + e.getMessage());
        return;
    }

    // Komponen input
    JTextField txtNama = new JTextField(namabarang);

    String[] kategoriOptions = {"Alat Tulis", "Kertas & Buku", "Perekat", "Alat Ukur"};
    JComboBox<String> cbKategori = new JComboBox<>(kategoriOptions);
    cbKategori.setSelectedItem(kategori);

    String[] satuanOptions = {"Pcs", "Pak", "Lusin", "Box", "Buah"};
    JComboBox<String> cbSatuan = new JComboBox<>(satuanOptions);
    cbSatuan.setSelectedItem(satuan);

    NumberFormat numberFormat = NumberFormat.getNumberInstance();
    numberFormat.setGroupingUsed(true);
    numberFormat.setMaximumFractionDigits(0);
    NumberFormatter numberFormatter = new NumberFormatter(numberFormat);
    numberFormatter.setAllowsInvalid(false);
    numberFormatter.setMinimum(0.0);

    JFormattedTextField txtHarga = new JFormattedTextField(numberFormatter);
    txtHarga.setValue((long) harga);

    JTextField txtStok = new JTextField(String.valueOf(stok));

    JPanel panel = new JPanel(new GridLayout(0, 1));
    panel.add(new JLabel("Nama Barang:"));
    panel.add(txtNama);
    panel.add(new JLabel("Kategori:"));
    panel.add(cbKategori);
    panel.add(new JLabel("Satuan:"));
    panel.add(cbSatuan);
    panel.add(new JLabel("Harga:"));
    panel.add(txtHarga);
    panel.add(new JLabel("Stok:"));
    panel.add(txtStok);

    int result = JOptionPane.showConfirmDialog(this, panel, "Edit Data Barang",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

    if (result == JOptionPane.OK_OPTION) {
        try {
            String newNama = txtNama.getText().trim();
            if (newNama.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nama barang tidak boleh kosong.");
                return;
            }

            String newKategori = cbKategori.getSelectedItem().toString();
            String newSatuan = cbSatuan.getSelectedItem().toString();

            Number hargaValue = (Number) txtHarga.getValue();
            if (hargaValue == null || hargaValue.doubleValue() < 0) {
                JOptionPane.showMessageDialog(this, "Harga harus berupa angka ≥ 0.");
                return;
            }

            double newHarga = hargaValue.doubleValue();

            String stokInput = txtStok.getText().trim();
            if (stokInput.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Stok tidak boleh kosong.");
                return;
            }

            int newStok = Integer.parseInt(stokInput);
            if (newStok < 0) {
                JOptionPane.showMessageDialog(this, "Stok tidak boleh negatif.");
                return;
            }

            Connection conn = Koneksi.getConnection();
            String sql = "UPDATE barang SET Nama_barang=?, Kategori=?, Satuan=?, Harga=?, Stok=? WHERE Id_barang=?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, newNama);
            pst.setString(2, newKategori);
            pst.setString(3, newSatuan);
            pst.setDouble(4, newHarga);
            pst.setInt(5, newStok);
            pst.setString(6, idbarang);
            pst.executeUpdate();

            JOptionPane.showMessageDialog(this, "Data berhasil diupdate.");
            tampilkanBarang();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal update data: " + e.getMessage());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Harga dan stok harus berupa angka valid.");
        }
    }
}

    private void hapusBarang() {
        int selectedRow = table_barang.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih data yang ingin dihapus.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Yakin ingin menghapus data ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            String id = table_barang.getValueAt(selectedRow, 0).toString();

            try {
                Connection con = Koneksi.getConnection();
                String sql = "DELETE FROM barang WHERE Id_barang=?";
                PreparedStatement pst = con.prepareStatement(sql);
                pst.setString(1, id);
                pst.executeUpdate();

                JOptionPane.showMessageDialog(this, "Data berhasil dihapus.");
                tampilkanBarang();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Gagal menghapus data: " + e.getMessage());
            }
        }
    }

    
    public static String formatRupiah(double angka) {
    DecimalFormatSymbols symbols = new DecimalFormatSymbols();
    symbols.setGroupingSeparator('.');
    symbols.setDecimalSeparator(',');
    symbols.setCurrencySymbol("Rp ");
    
    DecimalFormat rupiahFormat = new DecimalFormat("Rp #,###", symbols);
    return rupiahFormat.format(angka);
}

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new main.gradasiwarna();
        jLabel1 = new javax.swing.JLabel();
        btn_tambah = new javax.swing.JButton();
        btn_hapus = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        table_barang = new javax.swing.JTable();
        btn_cetakbarcode = new javax.swing.JButton();
        text_cari = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        cb_kriteria = new javax.swing.JComboBox<>();
        btn_edit = new javax.swing.JButton();

        setMaximumSize(new java.awt.Dimension(755, 509));
        setMinimumSize(new java.awt.Dimension(755, 509));
        setPreferredSize(new java.awt.Dimension(755, 509));
        setLayout(new java.awt.CardLayout());

        jPanel1.setBackground(new java.awt.Color(0, 0, 255));
        jPanel1.setMaximumSize(new java.awt.Dimension(755, 509));
        jPanel1.setMinimumSize(new java.awt.Dimension(755, 509));
        jPanel1.setPreferredSize(new java.awt.Dimension(755, 509));

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Data Barang");

        btn_tambah.setText("TAMBAH");
        btn_tambah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_tambahActionPerformed(evt);
            }
        });

        btn_hapus.setText("HAPUS");
        btn_hapus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_hapusActionPerformed(evt);
            }
        });

        table_barang.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane1.setViewportView(table_barang);

        btn_cetakbarcode.setText("CETAK BARCODE");
        btn_cetakbarcode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_cetakbarcodeActionPerformed(evt);
            }
        });

        text_cari.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        text_cari.setPreferredSize(new java.awt.Dimension(70, 20));
        text_cari.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                text_cariActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Cari  :");

        cb_kriteria.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Nama Barang", "Kategori", "Satuan", "Harga", "Stok", "Barcode", "Supplier", " " }));

        btn_edit.setText("EDIT");
        btn_edit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_editActionPerformed(evt);
            }
        });

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
                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 426, Short.MAX_VALUE)
                                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(btn_tambah, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btn_edit, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btn_hapus, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btn_cetakbarcode)
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(cb_kriteria, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(text_cari, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(14, 14, 14))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(22, 22, 22)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btn_tambah)
                            .addComponent(btn_cetakbarcode)
                            .addComponent(btn_edit)
                            .addComponent(btn_hapus)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(text_cari, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(cb_kriteria, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(16, 16, 16)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 386, Short.MAX_VALUE)
                .addContainerGap())
        );

        add(jPanel1, "card2");
    }// </editor-fold>//GEN-END:initComponents

    private void btn_tambahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_tambahActionPerformed
        FormTambahBarang form = new FormTambahBarang((java.awt.Frame) javax.swing.SwingUtilities.getWindowAncestor(this), true);
        form.setLocationRelativeTo(this);
        form.setVisible(true);

        tampilkanBarang();
    }//GEN-LAST:event_btn_tambahActionPerformed

    private void btn_hapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_hapusActionPerformed
        btn_hapus.addActionListener(e -> hapusBarang());

    }//GEN-LAST:event_btn_hapusActionPerformed

    private void btn_cetakbarcodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_cetakbarcodeActionPerformed
         int selectedRow = table_barang.getSelectedRow();
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Pilih data yang ingin dicetak barcode-nya.");
        return;
    }

    try {
        String kode = table_barang.getValueAt(selectedRow, 6).toString();
        String nama = table_barang.getValueAt(selectedRow, 1).toString(); 

        int barcodeWidth = 300;
        int barcodeHeight = 100;
        int labelHeight = 25;
        int textHeight = 20;
        int padding = 20;
        int imageHeight = labelHeight + barcodeHeight + textHeight + padding;

        BufferedImage resultImage = new BufferedImage(barcodeWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = resultImage.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, barcodeWidth, imageHeight);

        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        FontMetrics fm = g.getFontMetrics();
        int namaX = (barcodeWidth - fm.stringWidth(nama)) / 2;
        g.drawString(nama, namaX, labelHeight - 5);

        BitMatrix bitMatrix = new MultiFormatWriter().encode(kode, BarcodeFormat.CODE_128, barcodeWidth, barcodeHeight);
        BufferedImage barcodeImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
        g.drawImage(barcodeImage, 0, labelHeight, null);

        g.setFont(new Font("Arial", Font.PLAIN, 14));
        fm = g.getFontMetrics();
        int kodeX = (barcodeWidth - fm.stringWidth(kode)) / 2;
        g.drawString(kode, kodeX, imageHeight - 10);
        g.dispose();

        JDialog previewDialog = new JDialog((Frame) null, "Preview Barcode", true);
        previewDialog.setSize(barcodeWidth + 40, imageHeight + 80);
        previewDialog.setLocationRelativeTo(this);
        JLabel label = new JLabel(new ImageIcon(resultImage));
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(label, BorderLayout.CENTER);

        JButton btnSimpan = new JButton("Simpan PNG");
        btnSimpan.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Simpan Barcode");
            fileChooser.setSelectedFile(new File("barcode_" + kode + ".png"));
            int userSelection = fileChooser.showSaveDialog(previewDialog);
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToSave = fileChooser.getSelectedFile();
                try {
                    ImageIO.write(resultImage, "png", fileToSave);
                    JOptionPane.showMessageDialog(previewDialog, "Disimpan: " + fileToSave.getAbsolutePath());
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(previewDialog, "Gagal simpan: " + ex.getMessage());
                }
            }
        });

        JButton btnCetak = new JButton("Cetak");
        btnCetak.addActionListener(e -> {
            PrinterJob job = PrinterJob.getPrinterJob();
            job.setPrintable((graphics, pageFormat, pageIndex) -> {
                if (pageIndex > 0) return Printable.NO_SUCH_PAGE;
                Graphics2D g2 = (Graphics2D) graphics;
                g2.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
                g2.drawImage(resultImage, 0, 0, resultImage.getWidth(), resultImage.getHeight(), null);
                return Printable.PAGE_EXISTS;
            });
            if (job.printDialog()) {
                try {
                    job.print();
                } catch (PrinterException ex) {
                    JOptionPane.showMessageDialog(previewDialog, "Gagal cetak: " + ex.getMessage());
                }
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(btnSimpan);
        buttonPanel.add(btnCetak);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        previewDialog.setContentPane(panel);
        previewDialog.setVisible(true);

    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Gagal cetak barcode: " + e.getMessage());
    }
    }//GEN-LAST:event_btn_cetakbarcodeActionPerformed

    private void text_cariActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_text_cariActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_text_cariActionPerformed

    private void btn_editActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_editActionPerformed
        updatebarang();
    }//GEN-LAST:event_btn_editActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_cetakbarcode;
    private javax.swing.JButton btn_edit;
    private javax.swing.JButton btn_hapus;
    private javax.swing.JButton btn_tambah;
    private javax.swing.JComboBox<String> cb_kriteria;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable table_barang;
    private javax.swing.JTextField text_cari;
    // End of variables declaration//GEN-END:variables
}
