package view;

import Form.FormTambahBarang;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import javax.swing.BorderFactory;

import Form.FormTambahBarang;
import java.awt.Component;

import main.Koneksi;
import java.sql.*;
import javax.swing.JLabel;
import javax.swing.table.DefaultTableModel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.client.j2se.MatrixToImageWriter;

import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.awt.Graphics2D;
import java.awt.Font;
import java.awt.FontMetrics;

import com.google.zxing.*;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.imageio.ImageIO;
import java.util.Date;
import java.util.Locale;
import main.Session;

public class FormLaporanPembelian extends javax.swing.JPanel {

    private boolean sedangMemuatSupplier = false;
    private Connection conn;

    public FormLaporanPembelian() {
        initComponents();
        loadSupplierToComboBox();
        loadAllData();
        tampilkanHariTanggal();
        label_user.setText("Login sebagai: " + Session.getRole());
        
        JD_Awal.getDateEditor().addPropertyChangeListener("date", new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (JD_Awal.getDate() != null && JD_Akhir.getDate() != null) {
                    tampilkanLaporanPembelian(JD_Awal.getDate(), JD_Akhir.getDate());
                }
            }
        });

        JD_Akhir.getDateEditor().addPropertyChangeListener("date", new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (JD_Awal.getDate() != null && JD_Akhir.getDate() != null) {
                    tampilkanLaporanPembelian(JD_Awal.getDate(), JD_Akhir.getDate());
                }
            }
        });
        DefaultTableModel model = new DefaultTableModel(new Object[]{"No", "Tanggal", "Nama Barang", "Jumlah", "Harga", "Total", "Supplier"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Semua sel tidak dapat diedit langsung
            }
        };
        tabel_pembelian.setModel(model);

        JTableHeader header = tabel_pembelian.getTableHeader();
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

        tabel_pembelian.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 16));
        tabel_pembelian.getTableHeader().setOpaque(false);
        tabel_pembelian.getTableHeader().setBackground(new Color(0, 102, 204));
        tabel_pembelian.getTableHeader().setForeground(Color.WHITE);

        tabel_pembelian.setRowHeight(30);
        tabel_pembelian.setShowGrid(true);
        tabel_pembelian.setGridColor(Color.LIGHT_GRAY);
        tabel_pembelian.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tabel_pembelian.setSelectionBackground(new Color(204, 229, 255));
        tabel_pembelian.setSelectionForeground(Color.BLACK);
        tabel_pembelian.setShowVerticalLines(true);

        tabel_pembelian.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectedRow = tabel_pembelian.getSelectedRow();
                if (selectedRow != -1) {
                    // Ambil ID pembelian dari kolom pertama (indeks 0)
                    String idPembelian = tabel_pembelian.getValueAt(selectedRow, 0).toString();
                    // Masukkan ke textfield t_cari
                    t_cari.setText(idPembelian);
                }
            }
        });

        tampilkanLaporanPembelianLengkap();

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

        btnClear.setText("CLEAR");
        btnClear.setBackground(new java.awt.Color(70, 130, 180)); // warna biru steel blue
        btnClear.setForeground(Color.WHITE);
        btnClear.setFont(new java.awt.Font("Serif", Font.BOLD, 12));
        btnClear.setFocusPainted(false);
        btnClear.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        btnClear.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Hover effect
        btnClear.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnClear.setBackground(new java.awt.Color(100, 149, 237)); // Cornflower Blue
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnClear.setBackground(new java.awt.Color(70, 130, 180));
            }
        });
    }

    private void loadSupplierToComboBox() {
        sedangMemuatSupplier = true;

        try {
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost/atk", "root", "");
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT Nama FROM supplier ORDER BY Nama ASC");

            cb_supplier.removeAllItems(); // reset dulu
            cb_supplier.addItem("-- Pilih Supplier --"); // pilihan default

            while (rs.next()) {
                cb_supplier.addItem(rs.getString("Nama"));
            }

            rs.close();
            st.close();
            con.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal memuat data supplier: " + e.getMessage());
        }

        sedangMemuatSupplier = false;
    }

    public void tampilkanLaporanPembelianLengkap() {
        DefaultTableModel model = (DefaultTableModel) tabel_pembelian.getModel();
        model.setRowCount(0);

        String[] kolom = {"ID Pembelian", "Tanggal", "Nama Barang", "Jumlah", "Harga", "Total", "Supplier"};
        model.setColumnIdentifiers(kolom);
        tabel_pembelian.setModel(model);

        try {
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost/atk", "root", "");
            String sql = "SELECT pr.id_pembelianrinci, s.Nama, p.Tgl_Pembelian, "
                    + "b.Nama_barang, pr.Jumlah_Beli, pr.Harga_Satuan, pr.Total "
                    + "FROM pembelian p "
                    + "JOIN supplier s ON p.id_Supplier = s.id_Supplier "
                    + "JOIN pembelianrinci pr ON p.id_pembelian = pr.id_pembelian "
                    + "JOIN barang b ON pr.id_Barang = b.id_Barang "
                    + "ORDER BY p.Tgl_Pembelian DESC";

            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(sql);

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("id_pembelianrinci"),
                    rs.getDate("Tgl_Pembelian"),
                    rs.getString("Nama_barang"),
                    rs.getInt("Jumlah_Beli"),
                    rs.getDouble("Harga_Satuan"),
                    rs.getDouble("Total"),
                    rs.getString("Nama")
                });
            }

            rs.close();
            st.close();
            con.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal menampilkan laporan pembelian: " + e.getMessage());
        }
    }

    public void tampilkanLaporanPembelian(Date tglAwal, Date tglAkhir) {
        DefaultTableModel model = (DefaultTableModel) tabel_pembelian.getModel();
        model.setRowCount(0);

        String[] kolom = {"ID Pembelian", "Tanggal", "Nama Barang", "Jumlah", "Harga", "Total", "Supplier"};
        model.setColumnIdentifiers(kolom);
        tabel_pembelian.setModel(model);

        try {
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost/atk", "root", "");
            String sql = "SELECT pr.id_pembelianrinci, s.Nama, p.Tgl_Pembelian, "
                    + "b.Nama_barang, pr.Jumlah_Beli, pr.Harga_Satuan, pr.Total "
                    + "FROM pembelian p "
                    + "JOIN supplier s ON p.id_Supplier = s.id_Supplier "
                    + "JOIN pembelianrinci pr ON p.id_pembelian = pr.id_pembelian "
                    + "JOIN barang b ON pr.id_Barang = b.id_Barang "
                    + "WHERE p.Tgl_Pembelian BETWEEN ? AND ? "
                    + "ORDER BY p.Tgl_Pembelian DESC";

            PreparedStatement pst = con.prepareStatement(sql);
            pst.setDate(1, new java.sql.Date(tglAwal.getTime()));
            pst.setDate(2, new java.sql.Date(tglAkhir.getTime()));
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("id_pembelianrinci"),
                    rs.getDate("Tgl_Pembelian"),
                    rs.getString("Nama_barang"),
                    rs.getInt("Jumlah_Beli"),
                    rs.getDouble("Harga_Satuan"),
                    rs.getDouble("Total"),
                    rs.getString("Nama")
                });
            }

            rs.close();
            pst.close();
            con.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal menampilkan laporan pembelian: " + e.getMessage());
        }
    }

    public void tampilkanLaporanBerdasarkanID(String idPembelian) {
        DefaultTableModel model = (DefaultTableModel) tabel_pembelian.getModel();
        model.setRowCount(0);

        // Kolom baru tanpa 'No', diganti jadi 'ID Pembelian'
        String[] kolom = {"ID Pembelian", "Tanggal", "Nama Barang", "Jumlah", "Harga", "Total", "Supplier"};
        model.setColumnIdentifiers(kolom);
        tabel_pembelian.setModel(model);

        try {
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost/atk", "root", "");
            String sql = "SELECT pr.id_pembelianrinci, s.Nama AS nama_supplier, p.Tgl_Pembelian, "
                    + "b.Nama_barang, pr.Jumlah_Beli, pr.Harga_Satuan, pr.Total "
                    + "FROM pembelian p "
                    + "JOIN supplier s ON p.id_Supplier = s.id_Supplier "
                    + "JOIN pembelianrinci pr ON p.id_pembelian = pr.id_pembelian "
                    + "JOIN barang b ON pr.id_Barang = b.id_Barang "
                    + "WHERE p.id_pembelian = ?";

            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, idPembelian);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("id_pembelianrinci"),
                    rs.getDate("Tgl_Pembelian"),
                    rs.getString("Nama_barang"),
                    rs.getInt("Jumlah_Beli"),
                    rs.getDouble("Harga_Satuan"),
                    rs.getDouble("Total"),
                    rs.getString("nama_supplier")
                });
            }

            rs.close();
            pst.close();
            con.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal mencari data berdasarkan ID Pembelian: " + e.getMessage());
        }
    }

// Method untuk load data semua pembelian tanpa filter
    private void loadAllData() {
        DefaultTableModel model = (DefaultTableModel) tabel_pembelian.getModel();
        model.setRowCount(0);

        String[] kolom = {"ID Pembelian", "Tanggal", "Nama Barang", "Jumlah", "Harga", "Total", "Supplier"};
        model.setColumnIdentifiers(kolom);
        tabel_pembelian.setModel(model);

        try {
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost/atk", "root", "");
            String sql = "SELECT pr.id_pembelianrinci, s.Nama AS nama_supplier, p.Tgl_Pembelian, "
                    + "b.Nama_barang, pr.Jumlah_Beli, pr.Harga_Satuan, pr.Total "
                    + "FROM pembelian p "
                    + "JOIN supplier s ON p.id_Supplier = s.Id_Supplier "
                    + "JOIN pembelianrinci pr ON p.id_pembelian = pr.id_pembelian "
                    + "JOIN barang b ON pr.id_Barang = b.id_Barang "
                    + "ORDER BY p.Tgl_Pembelian DESC";

            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(sql);

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("id_pembelianrinci"),
                    rs.getDate("Tgl_Pembelian"),
                    rs.getString("Nama_barang"),
                    rs.getInt("Jumlah_Beli"),
                    rs.getDouble("Harga_Satuan"),
                    rs.getDouble("Total"),
                    rs.getString("nama_supplier") // ← yang diperbaiki di sini
                });
            }

            rs.close();
            st.close();
            con.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal memuat semua data pembelian: " + e.getMessage());
        }
    }

// Method untuk Clear filter, dipanggil saat tombol Clear diklik
    public void clearFilters() {
        t_cari.setText("");
        cb_supplier.setSelectedIndex(0);  // Reset ke "-- Pilih User --"
        JD_Awal.setDate(null);
        JD_Akhir.setDate(null);
        loadAllData();
    }

    private void formWindowOpened(java.awt.event.WindowEvent evt) {
        loadSupplierToComboBox();
    }

    private String ambilIdPembelianDariBaris(int row) {
        DefaultTableModel model = (DefaultTableModel) tabel_pembelian.getModel();
        return model.getValueAt(row, 0).toString(); // kolom 0 = ID Pembelian
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
        btn_hapus = new javax.swing.JButton();
        label_user = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tabel_pembelian = new javax.swing.JTable();
        JD_Awal = new com.toedter.calendar.JDateChooser();
        JD_Akhir = new com.toedter.calendar.JDateChooser();
        btnClear = new javax.swing.JButton();
        t_cari = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        cb_supplier = new javax.swing.JComboBox<>();
        jLabel7 = new javax.swing.JLabel();

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
        jLabel1.setText("Laporan Data Pembelian");

        btn_hapus.setBackground(new java.awt.Color(204, 0, 0));
        btn_hapus.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btn_hapus.setForeground(new java.awt.Color(255, 255, 255));
        btn_hapus.setText("HAPUS");
        btn_hapus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_hapusActionPerformed(evt);
            }
        });

        label_user.setBackground(new java.awt.Color(0, 51, 255));
        label_user.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        label_user.setForeground(new java.awt.Color(255, 255, 255));
        label_user.setText("User :");

        jLabel3.setBackground(new java.awt.Color(0, 51, 255));
        jLabel3.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("    ");

        tabel_pembelian.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane1.setViewportView(tabel_pembelian);

        btnClear.setBackground(new java.awt.Color(204, 204, 0));
        btnClear.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnClear.setForeground(new java.awt.Color(255, 255, 255));
        btnClear.setText("CLEAR");
        btnClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearActionPerformed(evt);
            }
        });

        t_cari.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                t_cariKeyReleased(evt);
            }
        });

        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Awal");

        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("Akhir");

        cb_supplier.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Pilih Supplier", " " }));
        cb_supplier.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cb_supplierActionPerformed(evt);
            }
        });

        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("ID Pembelian");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(label_user, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(JD_Awal, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(JD_Akhir, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(t_cari, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cb_supplier, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel7))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 58, Short.MAX_VALUE)
                        .addComponent(btnClear)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn_hapus)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(label_user)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel3))
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(13, 13, 13)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jLabel2))
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(JD_Awal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(JD_Akhir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(t_cari, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(cb_supplier, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 384, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btn_hapus)
                            .addComponent(btnClear))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        jLabel3.getAccessibleContext().setAccessibleName("          ");

        add(jPanel1, "card2");
    }// </editor-fold>//GEN-END:initComponents

    private void btn_hapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_hapusActionPerformed

        int selectedRow = tabel_pembelian.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih dulu baris yang ingin dihapus.");
            return;
        }

        int konfirmasi = JOptionPane.showConfirmDialog(this, "Apakah Anda yakin ingin menghapus data ini?", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);

        if (konfirmasi == JOptionPane.YES_OPTION) {
            try {
                // Ambil ID pembelianrinci dari kolom tersembunyi (kolom ke-7)
                String idPembelianrinci = tabel_pembelian.getValueAt(selectedRow, 7).toString();

                Connection con = DriverManager.getConnection("jdbc:mysql://localhost/atk", "root", "");

                // Hapus dari tabel pembelianrinci berdasarkan id_pembelianrinci
                String sql = "DELETE FROM pembelianrinci WHERE id_pembelianrinci = ?";
                PreparedStatement pst = con.prepareStatement(sql);
                pst.setString(1, idPembelianrinci);
                int rowsAffected = pst.executeUpdate();

                pst.close();
                con.close();

                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Data berhasil dihapus.");
                    tampilkanLaporanPembelianLengkap(); // refresh tabel
                } else {
                    JOptionPane.showMessageDialog(this, "Data tidak ditemukan atau sudah dihapus.");
                }

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Gagal menghapus data: " + e.getMessage());
            }
        }
    }//GEN-LAST:event_btn_hapusActionPerformed

    private void t_cariKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_t_cariKeyReleased
        String id = t_cari.getText();

        DefaultTableModel model = (DefaultTableModel) tabel_pembelian.getModel();
        model.setRowCount(0);

        String[] kolom = {"ID Pembelian", "Tanggal", "Nama Barang", "Jumlah", "Harga", "Total", "Supplier"};
        model.setColumnIdentifiers(kolom);
        tabel_pembelian.setModel(model);

        try {
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost/atk", "root", "");
            String sql = "SELECT pr.id_pembelianrinci, s.Nama AS nama_supplier, p.Tgl_Pembelian, "
                    + "b.Nama_barang, pr.Jumlah_Beli, pr.Harga_Satuan, pr.Total "
                    + "FROM pembelian p "
                    + "JOIN supplier s ON p.id_Supplier = s.Id_Supplier "
                    + "JOIN pembelianrinci pr ON p.id_pembelian = pr.id_pembelian "
                    + "JOIN barang b ON pr.id_Barang = b.id_Barang "
                    + "WHERE p.id_pembelian LIKE ? "
                    + "ORDER BY p.Tgl_Pembelian DESC";

            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, "%" + id + "%");
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("id_pembelianrinci"),
                    rs.getDate("Tgl_Pembelian"),
                    rs.getString("Nama_barang"),
                    rs.getInt("Jumlah_Beli"),
                    rs.getDouble("Harga_Satuan"),
                    rs.getDouble("Total"),
                    rs.getString("nama_supplier")
                });
            }

            rs.close();
            pst.close();
            con.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal mencari berdasarkan ID Pembelian: " + e.getMessage());
        }
    }//GEN-LAST:event_t_cariKeyReleased

    private void cb_supplierActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cb_supplierActionPerformed
        if (sedangMemuatSupplier) {
            return;
        }

        Object selected = cb_supplier.getSelectedItem();
        if (selected == null) {
            return;
        }

        String namaSupplier = selected.toString();

// Jika default dipilih, tampilkan semua data
        if (namaSupplier.equals("-- Pilih Supplier --")) {
            loadAllData(); // pastikan loadAllData() ini memuat data pembelian, bukan penjualan
            return;
        }

// Ambil tanggal dari JDateChooser
        Date tanggalAwal = JD_Awal.getDate();
        Date tanggalAkhir = JD_Akhir.getDate();

        DefaultTableModel model = (DefaultTableModel) tabel_pembelian.getModel();
        model.setRowCount(0);

        try {
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost/atk", "root", "");

            String sql = "SELECT pr.id_pembelianrinci, s.Nama AS nama_supplier, p.Tgl_Pembelian, "
                    + "b.Nama_barang, pr.Jumlah_Beli, pr.Harga_Satuan, pr.Total "
                    + "FROM pembelian p "
                    + "JOIN supplier s ON p.id_Supplier = s.Id_Supplier "
                    + "JOIN pembelianrinci pr ON p.id_pembelian = pr.id_pembelian "
                    + "JOIN barang b ON pr.id_Barang = b.id_Barang "
                    + "WHERE s.Nama = ?";

            if (tanggalAwal != null && tanggalAkhir != null) {
                sql += " AND p.Tgl_Pembelian BETWEEN ? AND ?";
            }

            sql += " ORDER BY p.Tgl_Pembelian DESC";

            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, namaSupplier);

            if (tanggalAwal != null && tanggalAkhir != null) {
                java.sql.Date sqlTglAwal = new java.sql.Date(tanggalAwal.getTime());
                java.sql.Date sqlTglAkhir = new java.sql.Date(tanggalAkhir.getTime());
                pst.setDate(2, sqlTglAwal);
                pst.setDate(3, sqlTglAkhir);
            }

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("id_pembelianrinci"),
                    rs.getDate("Tgl_Pembelian"),
                    rs.getString("Nama_barang"),
                    rs.getInt("Jumlah_Beli"),
                    rs.getDouble("Harga_Satuan"),
                    rs.getDouble("Total"),
                    rs.getString("nama_supplier")
                });
            }

            rs.close();
            pst.close();
            con.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal filter berdasarkan supplier dan tanggal: " + e.getMessage());
        }
    }//GEN-LAST:event_cb_supplierActionPerformed

    private void btnClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearActionPerformed
        clearFilters();
    }//GEN-LAST:event_btnClearActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.toedter.calendar.JDateChooser JD_Akhir;
    private com.toedter.calendar.JDateChooser JD_Awal;
    private javax.swing.JButton btnClear;
    private javax.swing.JButton btn_hapus;
    private javax.swing.JComboBox<String> cb_supplier;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel label_user;
    private javax.swing.JTextField t_cari;
    private javax.swing.JTable tabel_pembelian;
    // End of variables declaration//GEN-END:variables

    private void tampilkanHariTanggal() {
        try {
            ActionListener taskPerformer = new ActionListener() {
                public void actionPerformed(ActionEvent ae) {
                    // Dapatkan waktu saat ini
                    Calendar dt = Calendar.getInstance();

                    // Format tanggal dan waktu dalam bahasa Indonesia
                    SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd MMMM yyyy", new Locale("id", "ID"));
                    String formattedDate = sdf.format(dt.getTime());

                    // Tampilkan hasil di JLabel
                    jLabel3.setText(formattedDate);
                }
            };

            // Timer untuk memperbarui tampilan setiap detik
            new javax.swing.Timer(1000, taskPerformer).start();
        } catch (Exception e) {
            System.out.println("Error : " + e.getMessage());
        }
    }
}
