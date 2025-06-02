package view;

import Form.FormCariBarang;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.KeyEvent;
import main.Koneksi;
import java.sql.*;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import main.Session;
import java.awt.print.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.HierarchyEvent;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JRootPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;

import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

import javax.swing.text.*;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.UUID;



public class FormPenjualan extends javax.swing.JPanel {

    public FormPenjualan() {
        initComponents();
        inisialisasiForm();
        applyRupiahFilter();

        this.addHierarchyListener(e -> {
            if ((e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0 && this.isShowing()) {
                aturShortcutKeyboard();
            }
        });

    }

    private void inisialisasiForm() {
        text_kasir.setText(Session.getNama());
        label_user.setText("Login sebagai: " + Session.getRole());

        resetForm();
        styleButtons();

        DefaultTableModel model = new DefaultTableModel() {

            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // semua sel tidak bisa diedit
            }
        };
        model.setColumnIdentifiers(new Object[]{
            "Barcode", "Nama", "Harga", "Stok", "Satuan", "Jumlah", "Total"

        });
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

    }

    private void styleButtons() {
        styleButton(btn_simpan, "SIMPAN");
        styleButton(btn_tambah, "TAMBAH");
        styleButton(btn_edit, "EDIT");
        styleButton(btn_hapus, "HAPUS");
        styleButton(btn_cari, "Cari Barang");
    }

    private void styleButton(JButton button, String text) {
        button.setText(text);
        button.setBackground(new Color(70, 130, 180));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Serif", Font.BOLD, 12));
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(8, 16, 8, 16));
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

    private void aturShortcutKeyboard() {
        JRootPane rootPane = getRootPane(); // sekarang sudah tidak null
        InputMap inputMap = rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = rootPane.getActionMap();

        // Shortcut F2 -> tambah
        inputMap.put(KeyStroke.getKeyStroke("F2"), "tambah");
        actionMap.put("tambah", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                btn_tambah.doClick();
            }
        });

        // Shortcut F5 -> simpan
        inputMap.put(KeyStroke.getKeyStroke("F5"), "simpan");
        actionMap.put("simpan", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                btn_simpan.doClick();
            }
        });

    }

    private void cetakStrukThermal(String idPenjualan, String tanggal, String kasir, int total, int diskon, int bayar, int kembalian) {
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setJobName("Struk Penjualan");

        Printable printable = (Graphics g, PageFormat pf, int pageIndex) -> {
            if (pageIndex > 0) {
                return Printable.NO_SUCH_PAGE;
            }

            Graphics2D g2d = (Graphics2D) g;
            g2d.translate(pf.getImageableX(), pf.getImageableY());
            g2d.setFont(new Font("Monospaced", Font.PLAIN, 9));

            int y = 10;
            int lineHeight = 12;

            // Header toko
            g2d.drawString(centerText("===TOKO ATK FATIMAH===", 32), 0, y);
            y += lineHeight;
            g2d.drawString(centerText("Jl. Jember No.1 - 08122222222", 32), 0, y);
            y += lineHeight;
            g2d.drawString("--------------------------------", 0, y);
            y += lineHeight;

            // Info Transaksi
            g2d.drawString("No Faktur : " + idPenjualan, 0, y);
            y += lineHeight;
            g2d.drawString("Tanggal   : " + tanggal, 0, y);
            y += lineHeight;
            g2d.drawString("Kasir     : " + kasir, 0, y);
            y += lineHeight;
            g2d.drawString("--------------------------------", 0, y);
            y += lineHeight;

            // Header item
            g2d.drawString(String.format("%-12s %3s %8s", "Nama", "Jml", "Total"), 0, y);
            y += lineHeight;

            // Item loop
            DefaultTableModel model = (DefaultTableModel) table.getModel();
            for (int i = 0; i < model.getRowCount(); i++) {
                String nama = model.getValueAt(i, 1).toString();
                String jumlah = model.getValueAt(i, 5).toString();
                String totalBarang = model.getValueAt(i, 6).toString();

                // Potong nama jika kepanjangan
                if (nama.length() > 12) {
                    nama = nama.substring(0, 12);
                }

                g2d.drawString(String.format("%-12s %3s %8s", nama, jumlah, formatRupiah(totalBarang)), 0, y);
                y += lineHeight;
            }

            g2d.drawString("--------------------------------", 0, y);
            y += lineHeight;

            // Ringkasan
            g2d.drawString(formatLine("Subtotal", total), 0, y);
            y += lineHeight;
            g2d.drawString(formatLine("Diskon", diskon), 0, y);
            y += lineHeight;
            g2d.drawString(formatLine("Bayar", bayar), 0, y);
            y += lineHeight;
            g2d.drawString(formatLine("Kembalian", kembalian), 0, y);
            y += lineHeight;

            g2d.drawString("--------------------------------", 0, y);
            y += lineHeight;
            g2d.drawString(centerText("Terima Kasih", 32), 0, y);
            y += lineHeight;
            g2d.drawString(centerText("Barang yang sudah dibeli", 32), 0, y);
            y += lineHeight;
            g2d.drawString(centerText("tidak dapat dikembalikan.", 32), 0, y);
            y += lineHeight;

            return Printable.PAGE_EXISTS;
        };

        PageFormat pf = job.defaultPage();
        Paper paper = new Paper();
        double width = 180; // lebar 58mm
        double height = 500;
        paper.setSize(width, height);
        paper.setImageableArea(0, 0, width, height);
        pf.setPaper(paper);

        job.setPrintable(printable, pf);

        try {
            job.print();
        } catch (PrinterException ex) {
            JOptionPane.showMessageDialog(this, "Gagal mencetak: " + ex.getMessage());
        }
    }

    private String formatRupiah(String nominalStr) {
        try {
            int nominal = Integer.parseInt(nominalStr);
            return String.format("%,d", nominal).replace(',', '.');
        } catch (NumberFormatException e) {
            return nominalStr;
        }
    }

    private String formatLine(String label, int amount) {
        return String.format("%-10s : Rp %,8d", label, amount).replace(',', '.');
    }

    private String centerText(String text, int width) {
        int padSize = (width - text.length()) / 2;
        String padding = " ".repeat(Math.max(0, padSize));
        return padding + text;
    }

    private void kurangiStok(Connection con, String barcode, int jumlah) throws SQLException {
        if (jumlah <= 0) {
            throw new IllegalArgumentException("Jumlah pengurangan stok harus lebih dari 0");
        }

        String sql = "UPDATE barang SET stok = stok - ? WHERE barcode = ? AND stok >= ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, jumlah);
            ps.setString(2, barcode);
            ps.setInt(3, jumlah);
            int updated = ps.executeUpdate();

            if (updated == 0) {
                throw new SQLException("Stok tidak mencukupi atau barcode tidak ditemukan: " + barcode);
            }
        }
    }

    private void kosongkanInputBarang() {
        text_barcode.setText("");
        text_nama.setText("");
        text_satuan.setText("");
        text_harga.setText("");
        text_jumlah.setText("");
        text_stok.setText("");
        text_barcode.requestFocus();
    }

    
    private String formatRupiah(double amount) {
    NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));
    return nf.format(amount);
}

    
   private void tambahAtauUpdateTabel() {
    try {
        String barcode = text_barcode.getText().trim();
        String nama = text_nama.getText();
        String satuan = text_satuan.getText();
        double harga = Double.parseDouble(text_harga.getText().trim());
        int jumlah = Integer.parseInt(text_jumlah.getText().trim());
        int stok = Integer.parseInt(text_stok.getText().trim());

        if (jumlah > stok) {
            JOptionPane.showMessageDialog(this, "Jumlah melebihi stok tersedia!");
            return;
        }

        DefaultTableModel model = (DefaultTableModel) table.getModel();
        boolean barangAda = false;

        for (int i = 0; i < model.getRowCount(); i++) {
            if (barcode.equals(model.getValueAt(i, 0))) {
                int jumlahLama = Integer.parseInt(model.getValueAt(i, 5).toString());
                int jumlahBaru = jumlahLama + jumlah;

                if (jumlahBaru > stok) {
                    JOptionPane.showMessageDialog(this, "Jumlah total melebihi stok!");
                    return;
                }

                double totalBaru = harga * jumlahBaru;
                model.setValueAt(jumlahBaru, i, 5);
                model.setValueAt(formatRupiah(totalBaru), i, 6); // Format Rp
                barangAda = true;
                break;
            }
        }

        if (!barangAda) {
            double total = harga * jumlah;
            model.addRow(new Object[]{
                barcode,
                nama,
                formatRupiah(harga),  // Format Rp
                stok,
                satuan,
                jumlah,
                formatRupiah(total)   // Format Rp
            });
        }

        kosongkanInputBarang();
        hitungTotalHarga();

    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(this, "Input harga atau jumlah tidak valid!", "Error", JOptionPane.ERROR_MESSAGE);
    }

    text_barcode.requestFocus();
}

   
   private double parseRupiah(String text) {
    if (text == null || text.isEmpty()) return 0.0;
    // Hapus "Rp", spasi, titik ribuan, ubah koma jadi titik
    text = text.replace("Rp", "").replace(".", "").replace(",", ".").trim();
    try {
        return Double.parseDouble(text);
    } catch (NumberFormatException e) {
        e.printStackTrace();
        return 0.0;
    }
}


    private void hitungTotalHarga() {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        double subtotal = 0.0;

        for (int i = 0; i < model.getRowCount(); i++) {
            try {
                subtotal += parseRupiah(model.getValueAt(i, 6).toString());

            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        text_subtotal.setText("Rp " + NumberFormat.getInstance(new Locale("id", "ID")).format(subtotal));

        // Ambil diskon persen
        double persenDiskon = 0.0;
        try {
            persenDiskon = Double.parseDouble(text_diskon.getText().trim());
        } catch (NumberFormatException e) {
            persenDiskon = 0.0;
        }

        if (persenDiskon < 0 || persenDiskon > 100) {
            JOptionPane.showMessageDialog(this, "Diskon harus antara 0% - 100%!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            persenDiskon = 0.0;
            text_diskon.setText("0");
        }

        // Hitung diskon nominal dari subtotal
        int diskonNominal = (int) Math.round((persenDiskon / 100.0) * subtotal);
        int totalBersih = (int) subtotal - diskonNominal;

        text_total.setText("Rp " + NumberFormat.getInstance(new Locale("id", "ID")).format(totalBersih));

        // Hitung ulang kembalian jika sudah ada input bayar
        if (!text_bayar.getText().trim().isEmpty()) {
            hitungKembalian();
        }
    }

    private void hitungKembalian() {
        try {
            String totalStr = text_total.getText().trim();
            String bayarStr = text_bayar.getText().trim();

            // Cek jika salah satu kosong
            if (totalStr.isEmpty() || bayarStr.isEmpty()) {
                text_kembalian.setText("Rp 0");
                return;
            }

            int total = RupiahFilter.parseToInt(totalStr);
            int bayar = RupiahFilter.parseToInt(bayarStr);
            int kembalian = bayar - total;

            if (kembalian < 0) {
                text_kembalian.setText("Rp 0");
            } else {
                text_kembalian.setText("Rp " + NumberFormat.getInstance(new Locale("id", "ID")).format(kembalian));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void cariBarang() {
        String barcode = text_barcode.getText().trim();
        if (barcode.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Barcode tidak boleh kosong!");
            return;
        }

        try (Connection con = Koneksi.getConnection()) {
            String sql = "SELECT * FROM barang WHERE barcode = ?";
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, barcode);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        text_nama.setText(rs.getString("Nama_barang"));
                        text_satuan.setText(rs.getString("Satuan"));
                        text_harga.setText(rs.getString("Harga"));
                        text_stok.setText(rs.getString("Stok"));
                        text_tanggal.setDate(new java.util.Date());
                        text_jumlah.setText("1");
                        text_jumlah.selectAll();
                        text_jumlah.requestFocus();
                    } else {
                        JOptionPane.showMessageDialog(this, "Barcode tidak ditemukan");
                        kosongkanInputBarang();
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Terjadi kesalahan saat mencari barang", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void resetForm() {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);
        text_subtotal.setText("0");
        text_total.setText("0");
        text_diskon.setText("0");
        text_bayar.setText("0");
        text_kembalian.setText("0");

        // Atur tanggal ke hari ini
        text_tanggal.setDate(new java.util.Date());

        kosongkanInputBarang();
    }

    private String ambilIdBarangDariBarcode(Connection con, String barcode) throws SQLException {
        String sql = "SELECT id_barang FROM barang WHERE barcode = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, barcode);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("id_barang"); // gunakan getString
                } else {
                    System.err.println("ID barang tidak ditemukan untuk barcode: " + barcode);
                }
            }
        }
        return null; // Tidak ditemukan
    }

    
   

    private void simpanTransaksi() {
        if (table.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Belum ada barang yang dibeli!");
            return;
        }

        if (text_tanggal.getDate() == null) {
            JOptionPane.showMessageDialog(this, "Tanggal transaksi belum dipilih!");
            return;
        }

        try {
            String tanggal = new java.text.SimpleDateFormat("yyyy-MM-dd").format(text_tanggal.getDate());
            String kasir = text_kasir.getText();
            int subtotal = RupiahUtils.parseToInt(text_subtotal.getText()); // Ambil dari subtotal asli
            int bayar = RupiahUtils.parseToInt(text_bayar.getText());

            // Ambil diskon persen
            int diskonPersen = text_diskon.getText().trim().isEmpty() ? 0 : Integer.parseInt(text_diskon.getText().trim());
            int diskon = (int) Math.round((diskonPersen / 100.0) * subtotal);

            int totalSetelahDiskon = subtotal - diskon;
            int kembalian = bayar - totalSetelahDiskon;

            if (bayar < totalSetelahDiskon) {
                JOptionPane.showMessageDialog(this, "Jumlah bayar kurang dari total setelah diskon!");
                return;
            }

            try (Connection conn = Koneksi.getConnection()) {
                conn.setAutoCommit(false);

                String idUser = "";
                String sqlUser = "SELECT id_user FROM users WHERE nama = ?";
                try (PreparedStatement psUser = conn.prepareStatement(sqlUser)) {
                    psUser.setString(1, kasir);
                    ResultSet rs = psUser.executeQuery();
                    if (rs.next()) {
                        idUser = rs.getString("id_user");
                    } else {
                        JOptionPane.showMessageDialog(this, "User tidak ditemukan!");
                        conn.rollback();
                        return;
                    }
                }

                // Generate ID penjualan
                String idPenjualan = generateUniqueDetailID(conn);

                // Simpan ke tabel penjualan
                String sqlPenjualan = "INSERT INTO penjualan (id_penjualan, id_user, tanggal, total, diskon, bayar, kembalian) VALUES (?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement ps = conn.prepareStatement(sqlPenjualan)) {
                    ps.setString(1, idPenjualan);
                    ps.setString(2, idUser);
                    ps.setDate(3, java.sql.Date.valueOf(tanggal));
                    ps.setInt(4, subtotal);         // total asli tanpa diskon
                    ps.setInt(5, diskon);           // nilai diskon nominal
                    ps.setInt(6, bayar);
                    ps.setInt(7, kembalian);
                    ps.executeUpdate();
                }

                // Simpan ke tabel penjualanrinci
                String sqlRinci = "INSERT INTO penjualanrinci (id_detail, Jumlah_Jual, Satuan, Harga_Satuan, Total, Id_Penjualan, Id_Barang, barcode) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement psRinci = conn.prepareStatement(sqlRinci)) {
                    for (int i = 0; i < table.getRowCount(); i++) {
                        String barcode = table.getValueAt(i, 0).toString();
                        String satuan = table.getValueAt(i, 4).toString();
                        double harga = RupiahUtils.parseToDouble(table.getValueAt(i, 2).toString());
                        int jumlah = Integer.parseInt(table.getValueAt(i, 5).toString());
                        double totalItem = RupiahUtils.parseToDouble(table.getValueAt(i, 6).toString());

                        String idBarang = ambilIdBarangDariBarcode(conn, barcode);
                        if (idBarang == null) {
                            conn.rollback();
                            JOptionPane.showMessageDialog(this, "Barang dengan barcode " + barcode + " tidak ditemukan.");
                            return;
                        }

                        String idDetail = generateUniqueDetailID(conn);

                        psRinci.setString(1, idDetail);
                        psRinci.setInt(2, jumlah);
                        psRinci.setString(3, satuan);
                        psRinci.setDouble(4, harga);
                        psRinci.setDouble(5, totalItem);
                        psRinci.setString(6, idPenjualan);
                        psRinci.setString(7, idBarang);
                        psRinci.setString(8, barcode);
                        psRinci.addBatch();

                        kurangiStok(conn, barcode, jumlah);
                    }
                    psRinci.executeBatch();
                }

                conn.commit();

                // Tampilkan konfirmasi
                int pilihan = JOptionPane.showConfirmDialog(
                        this,
                        "Transaksi berhasil disimpan.\nApakah Anda ingin mencetak struk?",
                        "Cetak Struk",
                        JOptionPane.YES_NO_OPTION
                );

                if (pilihan == JOptionPane.YES_OPTION) {
                    cetakStrukThermal(idPenjualan, tanggal, kasir, subtotal, diskon, bayar, kembalian);
                } else {
                    JOptionPane.showMessageDialog(this, "Transaksi disimpan tanpa mencetak struk.");
                }

                resetForm();

            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal menyimpan transaksi: " + e.getMessage());
        }
    }

    private void hanyaAngka(java.awt.event.KeyEvent evt) {
        char c = evt.getKeyChar();
        if (!Character.isDigit(c)) {
            evt.consume(); // membatalkan karakter jika bukan angka
        }
    }

    public class RupiahFilter extends DocumentFilter {

        private final String prefix = "Rp ";
        private final NumberFormat formatter = NumberFormat.getInstance(new Locale("id", "ID"));

        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
                throws BadLocationException {
            replace(fb, offset, 0, string, attr);
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                throws BadLocationException {
            Document doc = fb.getDocument();
            try {
                String oldText = doc.getText(0, doc.getLength());
                String newText = oldText.substring(0, offset) + text + oldText.substring(offset + length);

                // Bersihkan prefix dan non-digit
                String angka = newText.replace(prefix, "").replaceAll("[^\\d]", "");

                if (angka.isEmpty()) {
                    fb.replace(0, doc.getLength(), prefix, attrs);
                    return;
                }

                long parsed = Long.parseLong(angka);
                String formatted = prefix + formatter.format(parsed);
                fb.replace(0, doc.getLength(), formatted, attrs);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void remove(FilterBypass fb, int offset, int length)
                throws BadLocationException {
            replace(fb, offset, length, "", null);
        }

        public static int parseToInt(String text) {
            if (text == null || text.trim().isEmpty()) {
                return 0;
            }
            try {
                String angka = text.replace("Rp", "").replace(".", "").replace(",", "").trim();
                return Integer.parseInt(angka);
            } catch (NumberFormatException e) {
                return 0; // Jika gagal parsing, anggap 0 agar aman
            }
        }

    }

    private void applyRupiahFilter() {
        PlainDocument docSubtotal = (PlainDocument) text_subtotal.getDocument();
        docSubtotal.setDocumentFilter(new RupiahFilter());
        text_subtotal.setText("Rp 0");

        PlainDocument docTotal = (PlainDocument) text_total.getDocument();
        docTotal.setDocumentFilter(new RupiahFilter());
        text_total.setText("Rp 0");

        PlainDocument docBayar = (PlainDocument) text_bayar.getDocument();
        docBayar.setDocumentFilter(new RupiahFilter());
        text_bayar.setText("Rp 0");

        PlainDocument docKembalian = (PlainDocument) text_kembalian.getDocument();
        docKembalian.setDocumentFilter(new RupiahFilter());
        text_kembalian.setText("Rp 0");
    }

    public void terimaDataBarang(String barcode, String nama, String satuan, double harga, int stok) {
        text_barcode.setText(barcode);
        text_nama.setText(nama);
        text_satuan.setText(satuan);
        text_harga.setText(String.valueOf(harga));
        text_stok.setText(String.valueOf(stok));
        text_jumlah.setText("1");
        text_jumlah.requestFocus(); // langsung fokus ke jumlah
    }

    public String generateUniqueDetailID(Connection conn) throws SQLException {
        String id;
        boolean exists;
        do {
            id = "DTL-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase(); // misal DTL-A1B2C3
            String sql = "SELECT COUNT(*) FROM penjualanrinci WHERE id_detail = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, id);
                ResultSet rs = ps.executeQuery();
                rs.next();
                exists = rs.getInt(1) > 0;
            }
        } while (exists);
        return id;
    }

    
    
        
    
    
    public class RupiahUtils {
    public static int parseToInt(String text) {
        if (text == null || text.isEmpty()) return 0;
        text = text.replace("Rp", "").replace(".", "").replace(",", ".").trim();
        try {
            return (int) Math.round(Double.parseDouble(text));
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static double parseToDouble(String text) {
        if (text == null || text.isEmpty()) return 0.0;
        text = text.replace("Rp", "").replace(".", "").replace(",", ".").trim();
        try {
            return Double.parseDouble(text);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return 0.0;
        }
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
        label_user = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        btn_simpan = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        text_barcode = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        text_kasir = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        text_satuan = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        text_nama = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        text_harga = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        text_stok = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        text_jumlah = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        table = new javax.swing.JTable();
        text_diskon = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        text_total = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        text_subtotal = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        text_kembalian = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        text_bayar = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        btn_tambah = new javax.swing.JButton();
        btn_edit = new javax.swing.JButton();
        btn_hapus = new javax.swing.JButton();
        jLabel19 = new javax.swing.JLabel();
        text_tanggal = new com.toedter.calendar.JDateChooser();
        btn_cari = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();

        setLayout(new java.awt.CardLayout());

        jPanel1.setBackground(new java.awt.Color(0, 51, 255));

        label_user.setBackground(new java.awt.Color(0, 51, 255));
        label_user.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        label_user.setForeground(new java.awt.Color(255, 255, 255));
        label_user.setText("Login Sebagai: Kasir");

        jLabel6.setBackground(new java.awt.Color(0, 51, 255));
        jLabel6.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("Data Penjualan");

        btn_simpan.setText("simpan");
        btn_simpan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_simpanActionPerformed(evt);
            }
        });

        jLabel7.setBackground(new java.awt.Color(0, 51, 255));
        jLabel7.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("Barcode");

        text_barcode.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                text_barcodeKeyPressed(evt);
            }
        });

        jLabel8.setBackground(new java.awt.Color(0, 51, 255));
        jLabel8.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setText("Kasir");

        text_kasir.setEnabled(false);

        jLabel9.setBackground(new java.awt.Color(0, 51, 255));
        jLabel9.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(255, 255, 255));
        jLabel9.setText("Satuan");

        text_satuan.setEnabled(false);

        jLabel10.setBackground(new java.awt.Color(0, 51, 255));
        jLabel10.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(255, 255, 255));
        jLabel10.setText("Nama");

        text_nama.setEnabled(false);

        jLabel11.setBackground(new java.awt.Color(0, 51, 255));
        jLabel11.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(255, 255, 255));
        jLabel11.setText("Harga");

        text_harga.setEnabled(false);

        jLabel12.setBackground(new java.awt.Color(0, 51, 255));
        jLabel12.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(255, 255, 255));
        jLabel12.setText("Stok");

        text_stok.setEnabled(false);

        jLabel13.setBackground(new java.awt.Color(0, 51, 255));
        jLabel13.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(255, 255, 255));
        jLabel13.setText("Jumlah");

        text_jumlah.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                text_jumlahKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                text_jumlahKeyTyped(evt);
            }
        });

        table.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(table);

        text_diskon.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                text_diskonKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                text_diskonKeyTyped(evt);
            }
        });

        jLabel14.setBackground(new java.awt.Color(0, 51, 255));
        jLabel14.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(255, 255, 255));
        jLabel14.setText("Diskon");

        text_total.setFont(new java.awt.Font("SansSerif", 1, 18)); // NOI18N

        jLabel15.setBackground(new java.awt.Color(0, 51, 255));
        jLabel15.setFont(new java.awt.Font("SansSerif", 1, 16)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(255, 255, 255));
        jLabel15.setText("Total   :");

        jLabel16.setBackground(new java.awt.Color(0, 51, 255));
        jLabel16.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(255, 255, 255));
        jLabel16.setText("Sub Total");

        text_kembalian.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                text_kembalianKeyReleased(evt);
            }
        });

        jLabel17.setBackground(new java.awt.Color(0, 51, 255));
        jLabel17.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(255, 255, 255));
        jLabel17.setText("Kembalian");

        text_bayar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                text_bayarKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                text_bayarKeyTyped(evt);
            }
        });

        jLabel18.setBackground(new java.awt.Color(0, 51, 255));
        jLabel18.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(255, 255, 255));
        jLabel18.setText("Bayar");

        btn_tambah.setText("tambah");
        btn_tambah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_tambahActionPerformed(evt);
            }
        });

        btn_edit.setText("edit");
        btn_edit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_editActionPerformed(evt);
            }
        });

        btn_hapus.setText("hapus");
        btn_hapus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_hapusActionPerformed(evt);
            }
        });

        jLabel19.setBackground(new java.awt.Color(0, 51, 255));
        jLabel19.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        jLabel19.setForeground(new java.awt.Color(255, 255, 255));
        jLabel19.setText("Tanggal");

        text_tanggal.setDoubleBuffered(false);

        btn_cari.setText("Cari Barang");
        btn_cari.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_cariActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("%");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(text_barcode, javax.swing.GroupLayout.DEFAULT_SIZE, 160, Short.MAX_VALUE)
                                .addComponent(jLabel7)
                                .addComponent(jLabel19)
                                .addComponent(text_tanggal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(text_nama, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel10))
                                        .addGap(18, 18, 18)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel11)
                                            .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addComponent(text_harga, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(text_jumlah, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(text_kasir, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel8))
                                        .addGap(18, 18, 18)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(text_satuan, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel9))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(text_stok, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel12)
                                            .addComponent(jLabel13))))
                                .addGap(0, 55, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(label_user)
                                .addGap(8, 8, 8))))
                    .addComponent(jScrollPane1)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(text_subtotal, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addGap(4, 4, 4)
                                .addComponent(text_diskon, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel18, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(text_bayar, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(text_kembalian, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(btn_tambah, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btn_edit, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btn_hapus, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(btn_simpan, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn_cari, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel15)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(text_total, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(text_kasir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6)
                            .addComponent(label_user))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(btn_simpan)
                                    .addComponent(btn_cari))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(text_barcode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(text_total, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel12)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(text_stok, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(text_satuan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel10)
                            .addComponent(jLabel19))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(text_nama, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(text_tanggal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel11)
                            .addComponent(jLabel13))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(text_harga, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(text_jumlah, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_tambah)
                    .addComponent(btn_edit)
                    .addComponent(btn_hapus))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 171, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel16)
                        .addComponent(text_subtotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel18)
                        .addComponent(text_bayar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel14)
                        .addComponent(text_diskon, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel1))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel17)
                        .addComponent(text_kembalian, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(26, 26, 26))
        );

        add(jPanel1, "card2");
    }// </editor-fold>//GEN-END:initComponents

    private void btn_simpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_simpanActionPerformed
        simpanTransaksi();
    }//GEN-LAST:event_btn_simpanActionPerformed

    private void btn_tambahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_tambahActionPerformed
        tambahAtauUpdateTabel();

    }//GEN-LAST:event_btn_tambahActionPerformed

    private void btn_editActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_editActionPerformed
        int row = table.getSelectedRow();
        if (row >= 0) {
            text_barcode.setText(table.getValueAt(row, 0).toString());
            text_nama.setText(table.getValueAt(row, 1).toString());
            text_harga.setText(table.getValueAt(row, 2).toString());
            text_stok.setText(table.getValueAt(row, 3).toString());
            text_satuan.setText(table.getValueAt(row, 4).toString());
            text_jumlah.setText(table.getValueAt(row, 5).toString());
        } else {
            JOptionPane.showMessageDialog(this, "Pilih item di tabel terlebih dahulu.");
        }
    }//GEN-LAST:event_btn_editActionPerformed

    private void btn_hapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_hapusActionPerformed
        int row = table.getSelectedRow();
        if (row >= 0) {
            DefaultTableModel model = (DefaultTableModel) table.getModel();
            model.removeRow(row);
            hitungTotalHarga();
        } else {
            JOptionPane.showMessageDialog(this, "Pilih baris yang ingin dihapus.");
        }
    }//GEN-LAST:event_btn_hapusActionPerformed

    private void text_barcodeKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_text_barcodeKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            String barcode = text_barcode.getText().trim();
            if (barcode.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Silakan masukkan barcode terlebih dahulu.", "Peringatan", JOptionPane.WARNING_MESSAGE);
                return;
            }
            cariBarang(); // Sudah menangani input & hasilnya
        }
    }//GEN-LAST:event_text_barcodeKeyPressed

    private void text_kembalianKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_text_kembalianKeyReleased

    }//GEN-LAST:event_text_kembalianKeyReleased

    private void btn_cariActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_cariActionPerformed
        FormCariBarang form = new FormCariBarang((java.awt.Frame) javax.swing.SwingUtilities.getWindowAncestor(this), true, this);
        form.setLocationRelativeTo(this);
        form.setVisible(true);

    }//GEN-LAST:event_btn_cariActionPerformed

    private void text_diskonKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_text_diskonKeyReleased
        hitungTotalHarga();

        // Jika user menekan tombol Enter, pindahkan fokus ke text_bayar
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            text_bayar.requestFocus();
        }
    }//GEN-LAST:event_text_diskonKeyReleased

    private void text_bayarKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_text_bayarKeyReleased
        hitungKembalian();

        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            simpanTransaksi();
        }
    }//GEN-LAST:event_text_bayarKeyReleased

    private void text_jumlahKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_text_jumlahKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            try {
                int jumlah = Integer.parseInt(text_jumlah.getText());
                int stok = Integer.parseInt(text_stok.getText());
                if (jumlah > stok) {
                    JOptionPane.showMessageDialog(this, "Jumlah melebihi stok tersedia!");
                    return;
                }
                tambahAtauUpdateTabel();
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Jumlah harus berupa angka yang valid");
            }
        }
    }//GEN-LAST:event_text_jumlahKeyPressed

    private void text_jumlahKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_text_jumlahKeyTyped
        hanyaAngka(evt);
    }//GEN-LAST:event_text_jumlahKeyTyped

    private void text_diskonKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_text_diskonKeyTyped
        hanyaAngka(evt);
    }//GEN-LAST:event_text_diskonKeyTyped

    private void text_bayarKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_text_bayarKeyTyped
        hanyaAngka(evt);
    }//GEN-LAST:event_text_bayarKeyTyped


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_cari;
    private javax.swing.JButton btn_edit;
    private javax.swing.JButton btn_hapus;
    private javax.swing.JButton btn_simpan;
    private javax.swing.JButton btn_tambah;
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
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel label_user;
    private javax.swing.JTable table;
    private javax.swing.JTextField text_barcode;
    private javax.swing.JTextField text_bayar;
    private javax.swing.JTextField text_diskon;
    private javax.swing.JTextField text_harga;
    private javax.swing.JTextField text_jumlah;
    private javax.swing.JTextField text_kasir;
    private javax.swing.JTextField text_kembalian;
    private javax.swing.JTextField text_nama;
    private javax.swing.JTextField text_satuan;
    private javax.swing.JTextField text_stok;
    private javax.swing.JTextField text_subtotal;
    private com.toedter.calendar.JDateChooser text_tanggal;
    private javax.swing.JTextField text_total;
    // End of variables declaration//GEN-END:variables

}

