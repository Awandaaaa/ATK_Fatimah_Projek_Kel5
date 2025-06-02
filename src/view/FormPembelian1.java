package view;

import Form.FormCariBarang;
import Form.FormCariBarang1;
import Form.FormTambahBarang;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import java.sql.Connection;
import java.sql.*;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import main.Koneksi;
import main.Session;

public class FormPembelian1 extends javax.swing.JPanel {

    /**
     * Creates new form FormPembelian
     */
    private boolean sedangFormatHarga = false;
    private boolean sedangMenyimpan = false;
    NumberFormat rupiahFormat = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));

    public FormPembelian1() {
        // === Inisialisasi Komponen dan Session ===
        initComponents();
        initHargaListener();
        initBayarListener();
        initDiskonListener();
        initHargaListener();
        t_kasir.setText(Session.getNama());
        label_users.setText("Login sebagai: " + Session.getRole());

        // === Setup Tabel Pembelian ===
        String[] kolom = {"Barcode", "Nama Barang", "Satuan", "Jumlah", "Harga", "Total", "Supplier", "Tanggal"};
        DefaultTableModel model = new DefaultTableModel(null, kolom);
        tbl_pembelian.setModel(model);

        // Renderer untuk kolom Harga & Total
        DefaultTableCellRenderer rupiahRenderer = new DefaultTableCellRenderer() {
            @Override
            public void setValue(Object value) {
                if (value instanceof Number) {
                    setText(formatRupiah(((Number) value).doubleValue()));
                } else {
                    super.setValue(value);
                }
            }
        };
        tbl_pembelian.getColumnModel().getColumn(4).setCellRenderer(rupiahRenderer); // Harga
        tbl_pembelian.getColumnModel().getColumn(5).setCellRenderer(rupiahRenderer); // Total

        // === Keyboard Shortcut: DELETE untuk hapus baris ===
        tbl_pembelian.getInputMap(JTable.WHEN_FOCUSED)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "hapusBaris");
        tbl_pembelian.getActionMap().put("hapusBaris", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                hapusBarisTerpilih();
            }
        });

        // CTRL + DELETE untuk reset transaksi
        tbl_pembelian.getInputMap(JTable.WHEN_FOCUSED)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, KeyEvent.CTRL_DOWN_MASK), "resetTransaksi");
        tbl_pembelian.getActionMap().put("resetTransaksi", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetTransaksiPembelian();
            }
        });

        // === Validasi Input Diskon ===
        t_diskon.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String text = t_diskon.getText().trim().replace("%", "");
                if (!text.isEmpty()) {
                    try {
                        double persen = Double.parseDouble(text);
                        if (persen > 100) {
                            JOptionPane.showMessageDialog(FormPembelian1.this,
                                    "Diskon tidak boleh lebih dari 100%");
                            t_diskon.setText("100");
                        }
                    } catch (NumberFormatException ex) {
                        t_diskon.setText("0");
                    }
                }
            }
        });

        // === Style Header Tabel ===
        JTableHeader header = tbl_pembelian.getTableHeader();
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

        tbl_pembelian.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 16));
        tbl_pembelian.getTableHeader().setOpaque(false);
        tbl_pembelian.getTableHeader().setBackground(new Color(0, 102, 204));
        tbl_pembelian.getTableHeader().setForeground(Color.WHITE);

        tbl_pembelian.setRowHeight(30);
        tbl_pembelian.setShowGrid(true);
        tbl_pembelian.setGridColor(Color.LIGHT_GRAY);
        tbl_pembelian.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tbl_pembelian.setSelectionBackground(new Color(204, 229, 255));
        tbl_pembelian.setSelectionForeground(Color.BLACK);
        tbl_pembelian.setShowVerticalLines(true);

        //Style dan Hover Tombol
        styleButton(btn_simpan, "SIMPAN");
        styleButton(btn_tambah, "TAMBAH");
        styleButton(btn_caribarang, "CARI BARANG");
        styleButton(btn_hapus, "HAPUS");
    }

// === Fungsi Bantu: Styling Tombol ===
    private void styleButton(JButton button, String text) {
        button.setText(text);
        button.setBackground(new Color(70, 130, 180)); // Steel Blue
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Serif", Font.BOLD, 12));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent evt) {
                button.setBackground(new Color(100, 149, 237)); // Cornflower Blue
            }

            @Override
            public void mouseExited(MouseEvent evt) {
                button.setBackground(new Color(70, 130, 180));
            }
        });
    }

    // Listener untuk field harga
    private void initHargaListener() {
        t_harga.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                t_harga.removeKeyListener(this); // Cegah loop
                String input = t_harga.getText().replaceAll("[^\\d]", "");
                if (!input.isEmpty()) {
                    double value = Double.parseDouble(input);
                    t_harga.setText(formatRupiah(value));
                }
                t_harga.addKeyListener(this);
            }
        });
    }

// Listener untuk field bayar
    private void initBayarListener() {
        t_bayar.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (sedangFormatHarga) {
                    return;
                }
                sedangFormatHarga = true;
                String input = t_bayar.getText().replaceAll("[^\\d]", "");
                if (!input.isEmpty()) {
                    double value = Double.parseDouble(input);
                    t_bayar.setText(formatRupiah(value));
                }
                hitungTotalDanKembalian(); // supaya langsung update kembalian
                sedangFormatHarga = false;
            }
        });
    }

    private void initDiskonListener() {
        t_diskon.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                hitungTotalDanKembalian(); // langsung update saat diskon diketik
            }
        });
    }

    private String formatRupiah(double value) {
        DecimalFormat df = (DecimalFormat) DecimalFormat.getCurrencyInstance(new Locale("id", "ID"));
        df.setMaximumFractionDigits(0); // hilangkan ",00"
        return df.format(value);
    }

    public void isiDataBarangDariBarcode(String barcode) {
        try {
            Connection conn = Koneksi.getConnection();
            String sql = """
            SELECT 
                b.barcode, 
                b.Nama_barang, 
                b.Harga, 
                b.Stok, 
                b.Satuan, 
                s.Nama AS nama_supplier
            FROM 
                barang b
            LEFT JOIN 
                supplier s ON b.id_Supplier = s.id_Supplier
            WHERE 
                b.barcode = ?
        """;

            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, barcode);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                t_barcode.setText(rs.getString("barcode"));
                t_namabarang.setText(rs.getString("Nama_barang"));
                t_harga.setText(String.valueOf(rs.getDouble("Harga")));
                t_stok.setText(String.valueOf(rs.getInt("Stok")));
                t_satuan.setText(rs.getString("Satuan"));
                t_supplier.setText(rs.getString("nama_supplier"));

                // Set tanggal hari ini
                d_tanggal.setDate(new java.util.Date()); // untuk JDateChooser
                t_jumlah.setText("1");
                t_jumlah.requestFocus();

            } else {
//                JOptionPane.showMessageDialog(null, "Barang dengan barcode tersebut tidak ditemukan.");
                kosongkanFieldBarang();
            }

            rs.close();
            pst.close();
        } catch (SQLException ex) {
            Logger.getLogger(FormPembelian.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void kosongkanFieldBarang() {
        t_barcode.setText("");
        t_namabarang.setText("");
        t_satuan.setText("");
        t_stok.setText("");
        t_supplier.setText("");
        t_harga.setText("");
        t_jumlah.setText("");
    }

    private void hitungTotalDanKembalian() {
        double subtotal = 0.0;
        DefaultTableModel model = (DefaultTableModel) tbl_pembelian.getModel();

        for (int i = 0; i < model.getRowCount(); i++) {
            String totalStr = model.getValueAt(i, 5).toString();
            subtotal += parseCurrency(totalStr);

        }

        double diskonPersen = parsePersen(t_diskon.getText());
        double diskon = subtotal * (diskonPersen / 100.0);
        double totalSetelahDiskon = subtotal - diskon;

        double bayar = parseCurrency(t_bayar.getText());
        double kembali = bayar - totalSetelahDiskon;

        t_subtotal.setText(rupiahFormat.format(subtotal));
        t_total.setText(rupiahFormat.format(totalSetelahDiskon));
        t_kembali.setText(rupiahFormat.format(kembali));
    }

    private void tambahKeTabelPembelian() {
        try {
            String barcode = t_barcode.getText().trim();
            String nama = t_namabarang.getText().trim();
            String satuan = t_satuan.getText().trim();
            String jumlahStr = t_jumlah.getText().trim();
            String hargaStr = t_harga.getText().trim();
            String supplier = t_supplier.getText().trim();

            // Validasi field kosong
            if (barcode.isEmpty() || nama.isEmpty() || satuan.isEmpty()
                    || jumlahStr.isEmpty() || hargaStr.isEmpty() || supplier.isEmpty() || d_tanggal.getDate() == null) {
                JOptionPane.showMessageDialog(this, "Semua field harus diisi terlebih dahulu.", "Validasi", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Validasi nilai jumlah dan harga
            int jumlahBaru;
            double harga;
            try {
                jumlahBaru = Integer.parseInt(jumlahStr);
                harga = parseCurrency(hargaStr);
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(this, "Jumlah dan harga harus berupa angka.", "Validasi", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Validasi jumlah tidak boleh 0 atau negatif
            if (jumlahBaru <= 0) {
                JOptionPane.showMessageDialog(this, "Jumlah harus lebih dari 0.", "Validasi", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String tanggal = new SimpleDateFormat("yyyy-MM-dd").format(d_tanggal.getDate());

            DefaultTableModel model = (DefaultTableModel) tbl_pembelian.getModel();
            boolean sudahAda = false;

            for (int i = 0; i < model.getRowCount(); i++) {
                if (model.getValueAt(i, 0).toString().equals(barcode)) {
                    int jumlahLama = Integer.parseInt(model.getValueAt(i, 3).toString());
                    int jumlahBaruTotal = jumlahLama + jumlahBaru;
                    double totalBaru = jumlahBaruTotal * harga;

                    model.setValueAt(jumlahBaruTotal, i, 3);
                    model.setValueAt(rupiahFormat.format(totalBaru), i, 5);
                    sudahAda = true;
                    break;
                }
            }

            if (!sudahAda) {
                double totalItem = jumlahBaru * harga;

                Object[] row = new Object[]{
                    barcode,
                    nama,
                    satuan,
                    jumlahBaru,
                    rupiahFormat.format(harga),
                    rupiahFormat.format(totalItem),
                    supplier,
                    tanggal
                };
                model.addRow(row);
            }

            hitungTotalDanKembalian();
            kosongkanFieldBarang();
            t_barcode.requestFocus();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Terjadi kesalahan saat menambahkan ke tabel.");
            Logger.getLogger(FormPembelian.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    private void simpanTransaksiPembelian() {
        System.out.println("Total: " + parseCurrency(t_total.getText()));
        System.out.println("Diskon: " + parsePersen(t_diskon.getText()));
        System.out.println("Bayar: " + parseCurrency(t_bayar.getText()));

        if (!validasiSebelumSimpan() || sedangMenyimpan) {
            return;
        }
        sedangMenyimpan = true;

        try (Connection conn = Koneksi.getConnection()) {
            conn.setAutoCommit(false);

            String tanggal = new SimpleDateFormat("yyyy-MM-dd").format(d_tanggal.getDate());
            double total = parseCurrency(t_total.getText());
            double persenDiskon = parsePersen(t_diskon.getText());
            double diskon = total * (persenDiskon / 100.0);
            double bayar = parseCurrency(t_bayar.getText());
            double kembalian = bayar - (total - diskon);

            String idUser = getIdUser(conn, t_kasir.getText().trim());
            int idSupplier = ambilAtauBuatIdSupplier(conn, tbl_pembelian.getValueAt(0, 6).toString().trim());
            String idPembelian = simpanHeaderPembelian(conn, idSupplier, idUser, tanggal, total, diskon, bayar, kembalian);

            simpanDetailPembelian(conn, idPembelian);

            conn.commit();
            resetForm();
            showDialogSukses();

        } catch (Exception e) {
            e.printStackTrace();
            showInfoDialog("Gagal menyimpan transaksi: " + e.getMessage());
        } finally {
            sedangMenyimpan = false;
        }
    }

    private void showDialogSukses() {
        Window parentWindow = SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(parentWindow, "Sukses", Dialog.ModalityType.APPLICATION_MODAL);

        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setLayout(new BorderLayout());

        JLabel label = new JLabel("Transaksi pembelian berhasil disimpan!", JLabel.CENTER);
        label.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        dialog.add(label, BorderLayout.CENTER);

        JButton okButton = new JButton("OK");
        JPanel panel = new JPanel();
        panel.add(okButton);
        dialog.add(panel, BorderLayout.SOUTH);

        dialog.getRootPane().setDefaultButton(okButton);

        okButton.addActionListener(e -> {
            dialog.dispose();
            // Fokus kembali ke barcode setelah dialog tertutup
            SwingUtilities.invokeLater(() -> {
                btn_simpan.setEnabled(true);
                t_barcode.requestFocus();
            });
        });

        dialog.pack();
        dialog.setLocationRelativeTo(this); // ⬅️ Tengah form
        dialog.setVisible(true);
    }

    private void restoreFocusAfterDialog() {
        SwingUtilities.invokeLater(() -> {
            btn_simpan.setEnabled(true);   // Aktifkan kembali tombol simpan
            t_barcode.requestFocus();      // Fokus ke barcode
        });
    }

    private boolean validasiSebelumSimpan() {
        if (tbl_pembelian.getRowCount() == 0) {
            showInfoDialog("Belum ada barang yang dibeli!");
            return false;
        }

        if (t_total.getText().trim().isEmpty()
                || t_diskon.getText().trim().isEmpty()
                || t_bayar.getText().trim().isEmpty()) {
            showInfoDialog("Field Total, Diskon, dan Bayar tidak boleh kosong!");
            return false;
        }

        if (d_tanggal.getDate() == null) {
            showInfoDialog("Tanggal pembelian belum diisi!");
            return false;
        }

        double total = parseCurrency(t_total.getText());
        double persenDiskon = parsePersen(t_diskon.getText());
        double diskon = total * (persenDiskon / 100.0);
        double bayar = parseCurrency(t_bayar.getText());
        double harusBayar = total - diskon;

        // Debug log
        System.out.println("== DEBUG VALIDASI ==");
        System.out.println("Total     : " + total);
        System.out.println("Diskon %  : " + persenDiskon);
        System.out.println("Diskon Rp : " + diskon);
        System.out.println("Bayar     : " + bayar);
        System.out.println("Harus Bayar: " + harusBayar);

        if (persenDiskon > 100) {
            showInfoDialog("Diskon tidak boleh lebih dari 100%");
            return false;
        }

        if (bayar < harusBayar) {
            showInfoDialog("Jumlah bayar kurang dari total setelah diskon!");
            return false;
        }

        return true;
    }

    private double parseCurrency(String text) {
        if (text == null || text.trim().isEmpty()) {
            return 0.0;
        }
        try {
            String cleaned = text.replace("Rp", "")
                    .replace(".", "") // Hilangkan titik ribuan
                    .replace(",", ".") // Ubah koma jadi titik desimal
                    .replaceAll("[^\\d.]", ""); // Sisakan angka & titik
            return Double.parseDouble(cleaned);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    private double parsePersen(String text) {
        try {
            String clean = text.replaceAll("[^\\d.]", "");
            return clean.isEmpty() ? 0.0 : Double.parseDouble(clean);
        } catch (Exception e) {
            return 0.0;
        }
    }

    private String getIdUser(Connection conn, String namaKasir) throws SQLException {
        String sql = "SELECT id_user FROM users WHERE nama = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, namaKasir);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("id_user"); // Ambil sebagai String
                }
            }
        }
        throw new SQLException("Kasir tidak ditemukan!");
    }

    private String simpanHeaderPembelian(Connection conn, int idSupplier, String idUser, String tanggal,
            double total, double diskon, double bayar, double kembalian) throws SQLException {
        String idPembelian = generateIdPembelian(conn); // Buat ID manual

        String sql = """
        INSERT INTO pembelian (
            id_pembelian, Id_Supplier, id_user, Tgl_Pembelian,
            Total, diskon, bayar, kembalian
        ) VALUES (?, ?, ?, ?, ?, ?, ?, ?)
    """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, idPembelian);
            ps.setInt(2, idSupplier);
            ps.setString(3, idUser);
            ps.setDate(4, java.sql.Date.valueOf(tanggal));
            ps.setDouble(5, total);
            ps.setDouble(6, diskon);
            ps.setDouble(7, bayar);
            ps.setDouble(8, kembalian);
            ps.executeUpdate();
        }

        return idPembelian;
    }

    private String generateIdPembelian(Connection conn) throws SQLException {
        String prefix = "PB";
        String sql = "SELECT MAX(RIGHT(id_pembelian, 5)) AS max_id FROM pembelian";

        try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            int next = 1;
            if (rs.next() && rs.getString("max_id") != null) {
                next = Integer.parseInt(rs.getString("max_id")) + 1;
            }
            return prefix + String.format("%05d", next);
        }
    }

    private int getIdPembelianRinciLastNumber(Connection conn) throws SQLException {
        String sql = "SELECT MAX(RIGHT(id_pembelianrinci, 5)) AS max_num FROM pembelianrinci";
        try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            if (rs.next() && rs.getString("max_num") != null) {
                return Integer.parseInt(rs.getString("max_num"));
            }
        }
        return 0;
    }

    private void simpanDetailPembelian(Connection conn, String idPembelian) throws SQLException {
        String sql = "INSERT INTO pembelianrinci (id_pembelianrinci, Jumlah_Beli, Satuan, Harga_Satuan, Total, Id_Pembelian, Id_Barang, barcode, id_supplier) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            Map<String, Integer> supplierCache = new HashMap<>();

            // Dapatkan angka awal ID
            int counter = getIdPembelianRinciLastNumber(conn); // misalnya return 5 jika PR00005 terakhir
            for (int i = 0; i < tbl_pembelian.getRowCount(); i++) {
                String barcode = tbl_pembelian.getValueAt(i, 0).toString().trim();
                String namaBarang = tbl_pembelian.getValueAt(i, 1).toString().trim();
                String satuan = tbl_pembelian.getValueAt(i, 2).toString().trim();
                int jumlah = Integer.parseInt(tbl_pembelian.getValueAt(i, 3).toString().trim());
                double harga = parseRupiah(tbl_pembelian.getValueAt(i, 4).toString().trim());
                double totalItem = parseRupiah(tbl_pembelian.getValueAt(i, 5).toString().trim());

                String supplierNama = tbl_pembelian.getValueAt(i, 6).toString().trim();

                int idSupplier = supplierCache.computeIfAbsent(supplierNama, nama -> {
                    try {
                        return ambilAtauBuatIdSupplier(conn, nama);
                    } catch (SQLException e) {
                        e.printStackTrace();
                        return -1;
                    }
                });

                if (idSupplier == -1) {
                    continue;
                }

                String idBarang = ambilIdBarangDariBarcode(conn, barcode);
                if (idBarang == null) {
                    idBarang = insertBarangBaru(conn, barcode, namaBarang, satuan, harga, jumlah, "Alat Tulis", idSupplier);
                } else {
                    tambahkanStok(conn, barcode, jumlah);
                }

                // 🔥 Buat ID unik per baris
                String idRinci = String.format("PR%05d", ++counter);

                ps.setString(1, idRinci);
                ps.setInt(2, jumlah);
                ps.setString(3, satuan);
                ps.setDouble(4, harga);
                ps.setDouble(5, totalItem);
                ps.setString(6, idPembelian);
                ps.setString(7, idBarang);
                ps.setString(8, barcode);
                ps.setInt(9, idSupplier);

                ps.addBatch();
            }

            ps.executeBatch();
        }
    }

    private double parseRupiah(String rupiahStr) {
        if (rupiahStr == null || rupiahStr.isEmpty()) {
            return 0.0;
        }
        // Hilangkan Rp, titik ribuan, ubah koma jadi titik
        return Double.parseDouble(
                rupiahStr.replace("Rp", "")
                        .replace(".", "")
                        .replace(",", ".")
                        .trim()
        );
    }

    private String insertBarangBaru(Connection conn, String barcode, String nama, String satuan, double harga, int jumlah, String kategori, int idSupplier) throws SQLException {
        String sql = "INSERT INTO barang (barcode, Nama_barang, Kategori, Satuan, Harga, Stok, Id_Supplier) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, barcode);
            ps.setString(2, nama);
            ps.setString(3, kategori);
            ps.setString(4, satuan);
            ps.setDouble(5, harga);
            ps.setInt(6, jumlah);
            ps.setInt(7, idSupplier);
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                return rs.getString(1);
            } else {
                throw new SQLException("Gagal menambahkan barang baru");
            }
        }
    }

    private String generateIdBarang(Connection conn) throws SQLException {
        String prefix = "BRG";
        String sql = "SELECT MAX(RIGHT(id_barang, 5)) AS max_id FROM barang";

        try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                int next = rs.getString("max_id") != null ? Integer.parseInt(rs.getString("max_id")) + 1 : 1;
                return prefix + String.format("%05d", next);
            }
        }

        return prefix + "00001";
    }

    private void resetForm() {
        // Kosongkan tabel
        DefaultTableModel model = (DefaultTableModel) tbl_pembelian.getModel();
        model.setRowCount(0);

        // Kosongkan field
        t_total.setText("");
        t_subtotal.setText("");
        t_diskon.setText("");
        t_bayar.setText("");
        t_kembali.setText("");
        kosongkanFieldBarang(); // Fungsi yang sudah ada untuk reset input barang
    }

    private int ambilAtauBuatIdSupplier(Connection conn, String namaSupplier) throws SQLException {
        String sqlSelect = "SELECT Id_Supplier FROM supplier WHERE Nama = ?";
        try (PreparedStatement ps = conn.prepareStatement(sqlSelect)) {
            ps.setString(1, namaSupplier);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("Id_Supplier");
            }
        }

        // Jika tidak ditemukan, buat baru tanpa menyet Id_Supplier
        String sqlInsert = "INSERT INTO supplier (Nama) VALUES (?)";
        try (PreparedStatement ps = conn.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, namaSupplier);
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            } else {
                throw new SQLException("Gagal menyisipkan supplier baru.");
            }
        }
    }

    private String ambilIdBarangDariBarcode(Connection conn, String barcode) throws SQLException {
        if (barcode == null || barcode.trim().isEmpty()) {
            return null;
        }

        String sql = "SELECT Id_barang FROM barang WHERE barcode = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, barcode);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("Id_barang"); // Ubah ke getString
                }
            }
        }
        return null;
    }

    private void tambahkanStok(Connection conn, String barcode, int jumlah) throws SQLException {
        if (barcode == null || barcode.trim().isEmpty()) {
            return;
        }

        String sql = "UPDATE barang SET stok = stok + ? WHERE barcode = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, jumlah);
            ps.setString(2, barcode);
            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0) {
                System.out.println("Gagal menambahkan stok. Barcode tidak ditemukan: " + barcode);
            }
        }
    }

    private void hapusBarisTerpilih() {
        int selectedRow = tbl_pembelian.getSelectedRow();
        DefaultTableModel model = (DefaultTableModel) tbl_pembelian.getModel();

        if (model.getRowCount() == 0) {
            showInfoDialog("Tabel pembelian kosong.");
            return;
        }

        if (selectedRow == -1) {
            showInfoDialog("Pilih baris yang ingin dihapus.");
            return;
        }

        showConfirmDialog("Yakin ingin menghapus item ini?", () -> {
            model.removeRow(selectedRow);
            hitungTotalDanKembalian();
            requestFocusKeBarcode();
        });
    }

    private void showSafeConfirmDialog(String message, Runnable onYes) {
        final JDialog dialog = new JDialog((Frame) null, "Konfirmasi", true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setLayout(new BorderLayout());

        JLabel label = new JLabel(message);
        label.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        dialog.add(label, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton yesButton = new JButton("Ya");
        JButton noButton = new JButton("Tidak");

        buttonPanel.add(yesButton);
        buttonPanel.add(noButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        yesButton.addActionListener(e -> {
            dialog.dispose();
            SwingUtilities.invokeLater(onYes); // pastikan dijalankan setelah dialog ditutup
        });

        noButton.addActionListener(e -> dialog.dispose());

        // Set YES sebagai default button untuk ENTER
        dialog.getRootPane().setDefaultButton(yesButton);

        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    private void resetTransaksiPembelian() {
        DefaultTableModel model = (DefaultTableModel) tbl_pembelian.getModel();

        if (model.getRowCount() == 0) {
            showInfoDialog("Tidak ada data untuk dihapus.");
            return;
        }

        showConfirmDialog("Yakin ingin menghapus SEMUA item dari transaksi?", () -> {
            model.setRowCount(0);
            kosongkanFieldBarang();
            hitungTotalDanKembalian();
            requestFocusKeBarcode();
        });
    }

    private void showInfoDialog(String pesan) {
        // Hindari loop ENTER -> trigger tombol lagi
        JOptionPane optionPane = new JOptionPane(pesan, JOptionPane.INFORMATION_MESSAGE);
        JDialog dialog = optionPane.createDialog(this, "Informasi");
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        dialog.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                requestFocusKeBarcode();
            }

            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                requestFocusKeBarcode();
            }
        });

        dialog.setModal(true);
        dialog.setVisible(true);
    }

    private void showConfirmDialog(String pesan, Runnable aksiJikaYes) {
        JOptionPane optionPane = new JOptionPane(pesan, JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_OPTION);
        JDialog dialog = optionPane.createDialog(this, "Konfirmasi");
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        dialog.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                requestFocusKeBarcode();
            }

            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                requestFocusKeBarcode();
            }
        });

        dialog.setModal(true);
        dialog.setVisible(true);

        Object selectedValue = optionPane.getValue();
        if (selectedValue instanceof Integer && (Integer) selectedValue == JOptionPane.YES_OPTION) {
            aksiJikaYes.run();
        }
    }

    private void requestFocusKeBarcode() {
        SwingUtilities.invokeLater(() -> t_barcode.requestFocus());
    }

    public void terimaDataBarang(String kode, String nama, String satuan, double harga, int stok, String supplier) {
        t_barcode.setText(kode);
        t_namabarang.setText(nama);
        t_satuan.setText(satuan);
        t_harga.setText(String.format("%.0f", harga)); // Tanpa desimal
        t_stok.setText(String.valueOf(stok));
        t_supplier.setText(supplier);
        t_jumlah.setText("1");

        // Set tanggal hari ini
        d_tanggal.setDate(new Date());

        // Fokus ke field jumlah
        t_jumlah.requestFocus();
        SwingUtilities.invokeLater(() -> t_jumlah.selectAll());
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
        label_users = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        btn_simpan = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        t_barcode = new javax.swing.JTextField();
        t_kasir = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        t_namabarang = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        t_harga = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        t_stok = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        t_jumlah = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tbl_pembelian = new javax.swing.JTable();
        t_total = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        t_subtotal = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        btn_tambah = new javax.swing.JButton();
        btn_caribarang = new javax.swing.JButton();
        btn_hapus = new javax.swing.JButton();
        jLabel19 = new javax.swing.JLabel();
        d_tanggal = new com.toedter.calendar.JDateChooser();
        t_diskon = new javax.swing.JTextField();
        t_bayar = new javax.swing.JTextField();
        t_kembali = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        t_satuan = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        t_supplier = new javax.swing.JTextField();
        jLabel22 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();

        setLayout(new java.awt.CardLayout());

        jPanel1.setBackground(new java.awt.Color(0, 51, 255));

        label_users.setBackground(new java.awt.Color(0, 51, 255));
        label_users.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        label_users.setForeground(new java.awt.Color(255, 255, 255));
        label_users.setText("Login Sebagai:");

        jLabel6.setBackground(new java.awt.Color(0, 51, 255));
        jLabel6.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("Data Pembelian");

        btn_simpan.setText("simpan");
        btn_simpan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_simpanActionPerformed(evt);
            }
        });

        jLabel8.setBackground(new java.awt.Color(0, 51, 255));
        jLabel8.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setText("Barcode");

        t_barcode.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                t_barcodeKeyReleased(evt);
            }
        });

        jLabel10.setBackground(new java.awt.Color(0, 51, 255));
        jLabel10.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(255, 255, 255));
        jLabel10.setText("Nama Barang");

        jLabel11.setBackground(new java.awt.Color(0, 51, 255));
        jLabel11.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(255, 255, 255));
        jLabel11.setText("Harga");

        t_harga.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                t_hargaKeyReleased(evt);
            }
        });

        jLabel12.setBackground(new java.awt.Color(0, 51, 255));
        jLabel12.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(255, 255, 255));
        jLabel12.setText("Stok");

        jLabel13.setBackground(new java.awt.Color(0, 51, 255));
        jLabel13.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(255, 255, 255));
        jLabel13.setText("Jumlah");

        t_jumlah.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                t_jumlahKeyReleased(evt);
            }
        });

        tbl_pembelian.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(tbl_pembelian);

        jLabel15.setBackground(new java.awt.Color(0, 51, 255));
        jLabel15.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(255, 255, 255));
        jLabel15.setText("Total");

        jLabel16.setBackground(new java.awt.Color(0, 51, 255));
        jLabel16.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(255, 255, 255));
        jLabel16.setText("Sub Total");

        btn_tambah.setText("Tambah");
        btn_tambah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_tambahActionPerformed(evt);
            }
        });

        btn_caribarang.setText("Cari Barang");
        btn_caribarang.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_caribarangActionPerformed(evt);
            }
        });

        btn_hapus.setText("Hapus");
        btn_hapus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_hapusActionPerformed(evt);
            }
        });

        jLabel19.setBackground(new java.awt.Color(0, 51, 255));
        jLabel19.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        jLabel19.setForeground(new java.awt.Color(255, 255, 255));
        jLabel19.setText("Tanggal");

        t_diskon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                t_diskonActionPerformed(evt);
            }
        });
        t_diskon.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                t_diskonKeyReleased(evt);
            }
        });

        t_bayar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                t_bayarKeyReleased(evt);
            }
        });

        jLabel17.setBackground(new java.awt.Color(0, 51, 255));
        jLabel17.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(255, 255, 255));
        jLabel17.setText("Diskon");

        jLabel18.setBackground(new java.awt.Color(0, 51, 255));
        jLabel18.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(255, 255, 255));
        jLabel18.setText("Bayar");

        jLabel20.setBackground(new java.awt.Color(0, 51, 255));
        jLabel20.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        jLabel20.setForeground(new java.awt.Color(255, 255, 255));
        jLabel20.setText("Kembali");

        jLabel14.setBackground(new java.awt.Color(0, 51, 255));
        jLabel14.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(255, 255, 255));
        jLabel14.setText("Supplier");

        jLabel21.setBackground(new java.awt.Color(0, 51, 255));
        jLabel21.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        jLabel21.setForeground(new java.awt.Color(255, 255, 255));
        jLabel21.setText("Satuan");

        t_supplier.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                t_supplierActionPerformed(evt);
            }
        });
        t_supplier.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                t_supplierKeyReleased(evt);
            }
        });

        jLabel22.setBackground(new java.awt.Color(0, 51, 255));
        jLabel22.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        jLabel22.setForeground(new java.awt.Color(255, 255, 255));
        jLabel22.setText("Kasir");

        jLabel1.setText("%");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jScrollPane1)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                .addComponent(btn_tambah, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(btn_hapus, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(t_harga, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(t_stok, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(btn_simpan, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(btn_caribarang, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(t_barcode, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel8)
                                            .addComponent(jLabel11))
                                        .addGap(18, 18, 18)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel12)
                                            .addComponent(jLabel10)
                                            .addComponent(t_namabarang, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel19)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel22)
                                            .addComponent(t_kasir, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(label_users)))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addComponent(t_satuan, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(18, 18, 18)
                                                .addComponent(t_jumlah, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(d_tanggal, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(jLabel21))
                                                .addGap(18, 18, 18)
                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(jLabel13)
                                                    .addComponent(jLabel14)
                                                    .addComponent(t_supplier, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                        .addGap(0, 320, Short.MAX_VALUE))))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(35, 35, 35)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel16)
                            .addComponent(jLabel15)
                            .addComponent(jLabel20))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(t_subtotal, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(t_total, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(t_kembali, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel17)
                            .addComponent(jLabel18))
                        .addGap(20, 20, 20)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(t_bayar, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(t_diskon, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel1)))
                        .addGap(30, 30, 30)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(label_users)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addComponent(jLabel22)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(t_kasir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(47, 47, 47))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(7, 7, 7)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(btn_simpan)
                                    .addComponent(btn_caribarang))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel19, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel8)
                                        .addComponent(jLabel10)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(t_barcode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(t_namabarang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(d_tanggal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel14)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(t_supplier, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel11)
                        .addGap(32, 32, 32))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel12)
                            .addComponent(jLabel21)
                            .addComponent(jLabel13))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(t_stok, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(t_harga, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(t_jumlah, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(t_satuan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_tambah)
                    .addComponent(btn_hapus))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(t_bayar, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel18)
                    .addComponent(t_kembali, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel20))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(t_diskon, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel17)
                    .addComponent(jLabel1)
                    .addComponent(t_subtotal, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(t_total, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15))
                .addGap(86, 86, 86))
        );

        add(jPanel1, "card2");
    }// </editor-fold>//GEN-END:initComponents

    private void btn_hapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_hapusActionPerformed
        hapusBarisTerpilih();
    }//GEN-LAST:event_btn_hapusActionPerformed

    private void btn_caribarangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_caribarangActionPerformed
        FormCariBarang1 form = new FormCariBarang1((java.awt.Frame) javax.swing.SwingUtilities.getWindowAncestor(this), true, this);
        form.setLocationRelativeTo(this);
        form.setVisible(true);
    }//GEN-LAST:event_btn_caribarangActionPerformed

    private void btn_tambahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_tambahActionPerformed
        tambahKeTabelPembelian();
    }//GEN-LAST:event_btn_tambahActionPerformed

    private void btn_simpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_simpanActionPerformed
        simpanTransaksiPembelian();
    }//GEN-LAST:event_btn_simpanActionPerformed

    private void t_barcodeKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_t_barcodeKeyReleased
        isiDataBarangDariBarcode(t_barcode.getText().trim());
    }//GEN-LAST:event_t_barcodeKeyReleased

    private void t_bayarKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_t_bayarKeyReleased
        hitungTotalDanKembalian();
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            t_diskon.requestFocus();
        }
    }//GEN-LAST:event_t_bayarKeyReleased

    private void t_jumlahKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_t_jumlahKeyReleased
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            tambahKeTabelPembelian();
            t_barcode.requestFocus();
        }
    }//GEN-LAST:event_t_jumlahKeyReleased

    private void t_diskonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_t_diskonActionPerformed
        try {
            double diskon = Double.parseDouble(t_diskon.getText().trim());
            if (diskon > 100) {
                showInfoDialog("Diskon tidak boleh lebih dari 100%");
                t_diskon.setText("100");
            }
        } catch (NumberFormatException e) {
            t_diskon.setText("0");
        }
        hitungTotalDanKembalian();
    }//GEN-LAST:event_t_diskonActionPerformed

    private void t_supplierKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_t_supplierKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_t_supplierKeyReleased

    private void t_supplierActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_t_supplierActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_t_supplierActionPerformed

    private void t_hargaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_t_hargaKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_t_hargaKeyReleased

    private void t_diskonKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_t_diskonKeyReleased
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            simpanTransaksiPembelian();
            try {
                double diskon = Double.parseDouble(t_diskon.getText().trim());
                if (diskon > 100) {
                    showInfoDialog("Diskon tidak boleh lebih dari 100%");
                    t_diskon.setText("100");
                }
            } catch (NumberFormatException e) {
                t_diskon.setText("0");
            }
            hitungTotalDanKembalian();
        }
    }//GEN-LAST:event_t_diskonKeyReleased


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_caribarang;
    private javax.swing.JButton btn_hapus;
    private javax.swing.JButton btn_simpan;
    private javax.swing.JButton btn_tambah;
    private com.toedter.calendar.JDateChooser d_tanggal;
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
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel label_users;
    private javax.swing.JTextField t_barcode;
    private javax.swing.JTextField t_bayar;
    private javax.swing.JTextField t_diskon;
    private javax.swing.JTextField t_harga;
    private javax.swing.JTextField t_jumlah;
    private javax.swing.JTextField t_kasir;
    private javax.swing.JTextField t_kembali;
    private javax.swing.JTextField t_namabarang;
    private javax.swing.JTextField t_satuan;
    private javax.swing.JTextField t_stok;
    private javax.swing.JTextField t_subtotal;
    private javax.swing.JTextField t_supplier;
    private javax.swing.JTextField t_total;
    private javax.swing.JTable tbl_pembelian;
    // End of variables declaration//GEN-END:variables

}
