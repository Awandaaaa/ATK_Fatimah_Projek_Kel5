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

public class FormLaporanPenjualan extends javax.swing.JPanel {

    private boolean sedangMemuatUser = false; // Mencegah event saat load combobox
    private Connection conn;

    public FormLaporanPenjualan() {
        initComponents();
        loadUserToComboBox();
        loadAllData();
        tampilkanHariTanggal();
        label_user.setText("Login sebagai: " + Session.getRole());
        
        JD_Awal.getDateEditor().addPropertyChangeListener("date", new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (JD_Awal.getDate() != null && JD_Akhir.getDate() != null) {
                    tampilkanLaporanPenjualan(JD_Awal.getDate(), JD_Akhir.getDate());
                }
            }
        });

        JD_Akhir.getDateEditor().addPropertyChangeListener("date", new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (JD_Awal.getDate() != null && JD_Akhir.getDate() != null) {
                    tampilkanLaporanPenjualan(JD_Awal.getDate(), JD_Akhir.getDate());
                }
            }
        });
        DefaultTableModel model = new DefaultTableModel(new Object[]{"ID Penjualan", "Tanggal", "Nama Barang", "Jumlah", "Harga", "Total", "User"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Semua sel tidak dapat diedit langsung
            }
        };
        tabel_penjualan.setModel(model);

        JTableHeader header = tabel_penjualan.getTableHeader();
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

        tabel_penjualan.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 16));
        tabel_penjualan.getTableHeader().setOpaque(false);
        tabel_penjualan.getTableHeader().setBackground(new Color(0, 102, 204));
        tabel_penjualan.getTableHeader().setForeground(Color.WHITE);

        tabel_penjualan.setRowHeight(30);
        tabel_penjualan.setShowGrid(true);
        tabel_penjualan.setGridColor(Color.LIGHT_GRAY);
        tabel_penjualan.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tabel_penjualan.setSelectionBackground(new Color(204, 229, 255));
        tabel_penjualan.setSelectionForeground(Color.BLACK);
        tabel_penjualan.setShowVerticalLines(true);

        tabel_penjualan.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int baris = tabel_penjualan.getSelectedRow(); // Ambil baris yang diklik
                if (baris >= 0) {
                    String idPenjualan = tabel_penjualan.getValueAt(baris, 0).toString(); // Ambil data dari kolom ke-0 (ID Penjualan)
                    t_cari.setText(idPenjualan); // Masukkan ke TextField
                }
            }
        });

        tampilkanLaporanPenjualanLengkap();

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

    private void loadUserToComboBox() {
        sedangMemuatUser = true;

        try {
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost/atk", "root", "");
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT Nama FROM users ORDER BY Nama ASC");

            cb_user.removeAllItems(); // reset dulu
            cb_user.addItem("-- Pilih User --"); // pilihan default

            while (rs.next()) {
                cb_user.addItem(rs.getString("Nama"));
            }

            rs.close();
            st.close();
            con.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal memuat data user: " + e.getMessage());
        }

        sedangMemuatUser = false;
    }

    public void tampilkanLaporanPenjualanLengkap() {
        DefaultTableModel model = (DefaultTableModel) tabel_penjualan.getModel();
        model.setRowCount(0); // reset isi

        String[] kolom = {"ID Penjualan", "Tanggal", "Nama Barang", "Jumlah", "Harga", "Total", "User"};
        model.setColumnIdentifiers(kolom);
        tabel_penjualan.setModel(model);

        try {
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost/atk", "root", "");
            String sql = "SELECT p.id_Penjualan, u.Nama AS nama_user, p.tanggal, "
                    + "b.Nama_barang, pr.Jumlah_Jual, pr.Harga_Satuan, pr.Total "
                    + "FROM penjualan p "
                    + "JOIN users u ON p.id_user = u.id_user "
                    + "JOIN penjualanrinci pr ON p.id_Penjualan = pr.id_Penjualan "
                    + "JOIN barang b ON pr.id_Barang = b.id_Barang "
                    + "ORDER BY p.tanggal DESC";

            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(sql);

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("id_Penjualan"),
                    rs.getDate("tanggal"),
                    rs.getString("Nama_barang"),
                    rs.getInt("Jumlah_Jual"),
                    rs.getDouble("Harga_Satuan"),
                    rs.getDouble("Total"),
                    rs.getString("nama_user")
                });
            }

            rs.close();
            st.close();
            con.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal menampilkan laporan penjualan lengkap: " + e.getMessage());
        }
    }

    public void tampilkanLaporanPenjualan(Date tglAwal, Date tglAkhir) {
        DefaultTableModel model = (DefaultTableModel) tabel_penjualan.getModel();
        model.setRowCount(0); // reset isi tabel

        String[] kolom = {"ID Penjualan", "Tanggal", "Nama Barang", "Jumlah", "Harga", "Total", "User"};
        model.setColumnIdentifiers(kolom);
        tabel_penjualan.setModel(model);

        try {
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost/atk", "root", "");
            String sql = "SELECT p.id_Penjualan, u.Nama AS nama_user, p.tanggal, "
                    + "b.Nama_barang, pr.Jumlah_Jual, pr.Harga_Satuan, pr.Total "
                    + "FROM penjualan p "
                    + "JOIN users u ON p.id_user = u.id_user "
                    + "JOIN penjualanrinci pr ON p.id_Penjualan = pr.id_Penjualan "
                    + "JOIN barang b ON pr.id_Barang = b.id_Barang "
                    + "WHERE p.tanggal BETWEEN ? AND ? "
                    + "ORDER BY p.tanggal DESC";

            PreparedStatement pst = con.prepareStatement(sql);
            pst.setDate(1, new java.sql.Date(tglAwal.getTime()));
            pst.setDate(2, new java.sql.Date(tglAkhir.getTime()));
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("id_Penjualan"),
                    rs.getDate("tanggal"),
                    rs.getString("Nama_barang"),
                    rs.getInt("Jumlah_Jual"),
                    rs.getDouble("Harga_Satuan"),
                    rs.getDouble("Total"),
                    rs.getString("nama_user")
                });
            }

            rs.close();
            pst.close();
            con.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal menampilkan laporan penjualan: " + e.getMessage());
        }
    }

    public void tampilkanLaporanBerdasarkanID(String idPenjualan) {
        DefaultTableModel model = (DefaultTableModel) tabel_penjualan.getModel();
        model.setRowCount(0);

        String[] kolom = {"ID Penjualan", "Tanggal", "Nama Barang", "Jumlah", "Harga", "Total", "User"};
        model.setColumnIdentifiers(kolom);
        tabel_penjualan.setModel(model);

        try {
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost/atk", "root", "");
            String sql = "SELECT p.id_Penjualan, u.Nama AS nama_user, p.tanggal, "
                    + "b.Nama_barang, pr.Jumlah_Jual, pr.Harga_Satuan, pr.Total "
                    + "FROM penjualan p "
                    + "JOIN users u ON p.id_user = u.id_user "
                    + "JOIN penjualanrinci pr ON p.id_Penjualan = pr.id_Penjualan "
                    + "JOIN barang b ON pr.id_Barang = b.id_Barang "
                    + "WHERE p.id_Penjualan = ?";

            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, idPenjualan);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("id_Penjualan"),
                    rs.getDate("tanggal"),
                    rs.getString("Nama_barang"),
                    rs.getInt("Jumlah_Jual"),
                    rs.getDouble("Harga_Satuan"),
                    rs.getDouble("Total"),
                    rs.getString("nama_user")
                });
            }

            rs.close();
            pst.close();
            con.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal mencari data berdasarkan ID Penjualan: " + e.getMessage());
        }
    }

// Method untuk load data semua tanpa filter
    private void loadAllData() {
        DefaultTableModel model = (DefaultTableModel) tabel_penjualan.getModel();
        model.setRowCount(0);

        try {
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost/atk", "root", "");
            String sql = "SELECT p.id_Penjualan, u.Nama AS nama_user, p.tanggal, "
                    + "b.Nama_barang, pr.Jumlah_Jual, pr.Harga_Satuan, pr.Total "
                    + "FROM penjualan p "
                    + "JOIN users u ON p.id_user = u.id_user "
                    + "JOIN penjualanrinci pr ON p.id_Penjualan = pr.id_Penjualan "
                    + "JOIN barang b ON pr.id_Barang = b.id_Barang "
                    + "ORDER BY p.tanggal DESC";

            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(sql);

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("id_Penjualan"),
                    rs.getDate("tanggal"),
                    rs.getString("Nama_barang"),
                    rs.getInt("Jumlah_Jual"),
                    rs.getDouble("Harga_Satuan"),
                    rs.getDouble("Total"),
                    rs.getString("nama_user")
                });
            }

            rs.close();
            st.close();
            con.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal memuat semua data: " + e.getMessage());
        }
    }

// Method untuk Clear filter, dipanggil saat tombol Clear diklik
    public void clearFilters() {
        t_cari.setText("");
        cb_user.setSelectedIndex(0);  // Reset ke "-- Pilih User --"
        JD_Awal.setDate(null);
        JD_Akhir.setDate(null);
        loadAllData();
    }

    private void formWindowOpened(java.awt.event.WindowEvent evt) {
        loadUserToComboBox();
    }

    private String ambilIdPenjualanDariBaris(int row) {
        // Misal kamu menyimpan id_Penjualan di kolom ke-7 (kolom ke-0 sampai 6 ditampilkan)
        DefaultTableModel model = (DefaultTableModel) tabel_penjualan.getModel();
        return model.getValueAt(row, 7).toString(); // sesuaikan dengan posisi ID kamu
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
        tgl_muncul = new javax.swing.JLabel();
        JD_Awal = new com.toedter.calendar.JDateChooser();
        jScrollPane1 = new javax.swing.JScrollPane();
        tabel_penjualan = new javax.swing.JTable();
        JD_Akhir = new com.toedter.calendar.JDateChooser();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        t_cari = new javax.swing.JTextField();
        cb_user = new javax.swing.JComboBox<>();
        btnClear = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();

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
        jLabel1.setText("Laporan Data Penjualan");

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

        tgl_muncul.setBackground(new java.awt.Color(0, 51, 255));
        tgl_muncul.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        tgl_muncul.setForeground(new java.awt.Color(255, 255, 255));
        tgl_muncul.setText("   ");

        tabel_penjualan.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane1.setViewportView(tabel_penjualan);

        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Awal");

        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("Akhir");

        t_cari.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                t_cariKeyReleased(evt);
            }
        });

        cb_user.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Pilih User", "q", "Dinda", "Awanda", "Randi" }));
        cb_user.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cb_userActionPerformed(evt);
            }
        });

        btnClear.setBackground(new java.awt.Color(204, 204, 0));
        btnClear.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnClear.setForeground(new java.awt.Color(255, 255, 255));
        btnClear.setText("CLEAR");
        btnClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearActionPerformed(evt);
            }
        });

        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("ID Penjualan");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(JD_Awal, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel2))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(JD_Akhir, javax.swing.GroupLayout.DEFAULT_SIZE, 125, Short.MAX_VALUE)
                                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(t_cari, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(cb_user, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jLabel6)))
                            .addComponent(jLabel1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 74, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(btnClear)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btn_hapus))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(tgl_muncul, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(label_user, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 125, Short.MAX_VALUE)))))
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
                        .addComponent(tgl_muncul))
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(12, 12, 12)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel4)
                    .addComponent(jLabel6))
                .addGap(7, 7, 7)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(JD_Awal, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(JD_Akhir, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(t_cari, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(cb_user, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 384, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btn_hapus)
                        .addComponent(btnClear)))
                .addContainerGap())
        );

        add(jPanel1, "card2");
    }// </editor-fold>//GEN-END:initComponents

    private void btn_hapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_hapusActionPerformed
        int selectedRow = tabel_penjualan.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih dulu baris yang ingin dihapus.");
            return;
        }

        int konfirmasi = JOptionPane.showConfirmDialog(this, "Apakah Anda yakin ingin menghapus data ini?", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);

        if (konfirmasi == JOptionPane.YES_OPTION) {
            // Ambil ID Penjualan dari model asli (kolom tersembunyi/real)
            // Jika sudah diganti ke "No", pastikan id_Penjualan tetap bisa diakses (misal lewat model tambahan/hidden field)
            try {
                // Misal kita pakai kolom ID Penjualan disimpan secara tersembunyi atau punya cara khusus ambil ID-nya
                String idPenjualan = ambilIdPenjualanDariBaris(selectedRow); // <-- kamu perlu bikin method ini

                Connection con = DriverManager.getConnection("jdbc:mysql://localhost/atk", "root", "");

                // Hapus dulu di tabel penjualanrinci (karena ada foreign key)
                String deleteRinci = "DELETE FROM penjualanrinci WHERE id_Penjualan = ?";
                PreparedStatement pstRinci = con.prepareStatement(deleteRinci);
                pstRinci.setString(1, idPenjualan);
                pstRinci.executeUpdate();

                // Baru hapus di tabel penjualan
                String deletePenjualan = "DELETE FROM penjualan WHERE id_Penjualan = ?";
                PreparedStatement pstPenjualan = con.prepareStatement(deletePenjualan);
                pstPenjualan.setString(1, idPenjualan);
                pstPenjualan.executeUpdate();

                pstRinci.close();
                pstPenjualan.close();
                con.close();

                JOptionPane.showMessageDialog(this, "Data berhasil dihapus.");
                tampilkanLaporanPenjualanLengkap(); // Refresh tabel
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Gagal menghapus data: " + e.getMessage());
            }
        }
    }//GEN-LAST:event_btn_hapusActionPerformed

    private void cb_userActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cb_userActionPerformed
        if (sedangMemuatUser) {
            return;
        }

        Object selected = cb_user.getSelectedItem();
        if (selected == null) {
            return;
        }

        String namaUser = selected.toString();

        // Jika default dipilih, tampilkan semua data
        if (namaUser.equals("-- Pilih User --")) {
            loadAllData();
            return;
        }

        // Ambil tanggal dari JDateChooser
        Date tanggalAwal = JD_Awal.getDate();
        Date tanggalAkhir = JD_Akhir.getDate();

        DefaultTableModel model = (DefaultTableModel) tabel_penjualan.getModel();
        model.setRowCount(0);

        try {
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost/atk", "root", "");

            String sql = "SELECT p.id_Penjualan, u.Nama AS nama_user, p.tanggal, "
                    + "b.Nama_barang, pr.Jumlah_Jual, pr.Harga_Satuan, pr.Total "
                    + "FROM penjualan p "
                    + "JOIN users u ON p.id_user = u.id_user "
                    + "JOIN penjualanrinci pr ON p.id_Penjualan = pr.id_Penjualan "
                    + "JOIN barang b ON pr.id_Barang = b.id_Barang "
                    + "WHERE u.Nama = ?";

            if (tanggalAwal != null && tanggalAkhir != null) {
                sql += " AND p.tanggal BETWEEN ? AND ?";
            }

            sql += " ORDER BY p.tanggal DESC";

            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, namaUser);

            if (tanggalAwal != null && tanggalAkhir != null) {
                java.sql.Date sqlTglAwal = new java.sql.Date(tanggalAwal.getTime());
                java.sql.Date sqlTglAkhir = new java.sql.Date(tanggalAkhir.getTime());
                pst.setDate(2, sqlTglAwal);
                pst.setDate(3, sqlTglAkhir);
            }

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("id_Penjualan"),
                    rs.getDate("tanggal"),
                    rs.getString("Nama_barang"),
                    rs.getInt("Jumlah_Jual"),
                    rs.getDouble("Harga_Satuan"),
                    rs.getDouble("Total"),
                    rs.getString("nama_user")
                });
            }

            rs.close();
            pst.close();
            con.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal filter berdasarkan user dan tanggal: " + e.getMessage());
        }
    }//GEN-LAST:event_cb_userActionPerformed

    private void t_cariKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_t_cariKeyReleased

        String id = t_cari.getText();

        DefaultTableModel model = (DefaultTableModel) tabel_penjualan.getModel();
        model.setRowCount(0);

        String[] kolom = {"ID Penjualan", "Tanggal", "Nama Barang", "Jumlah", "Harga", "Total", "User"};
        model.setColumnIdentifiers(kolom);

        try {
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost/atk", "root", "");
            String sql = "SELECT p.id_Penjualan, u.Nama AS nama_user, p.tanggal, "
                    + "b.Nama_barang, pr.Jumlah_Jual, pr.Harga_Satuan, pr.Total "
                    + "FROM penjualan p "
                    + "JOIN users u ON p.id_user = u.id_user "
                    + "JOIN penjualanrinci pr ON p.id_Penjualan = pr.id_Penjualan "
                    + "JOIN barang b ON pr.id_Barang = b.id_Barang "
                    + "WHERE p.id_Penjualan LIKE ?";

            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, "%" + id + "%");
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("id_Penjualan"),
                    rs.getDate("tanggal"),
                    rs.getString("Nama_barang"),
                    rs.getInt("Jumlah_Jual"),
                    rs.getDouble("Harga_Satuan"),
                    rs.getDouble("Total"),
                    rs.getString("nama_user")
                });
            }

            rs.close();
            pst.close();
            con.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal mencari berdasarkan ID: " + e.getMessage());
        }
    }//GEN-LAST:event_t_cariKeyReleased

    private void btnClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearActionPerformed
        clearFilters();
    }//GEN-LAST:event_btnClearActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.toedter.calendar.JDateChooser JD_Akhir;
    private com.toedter.calendar.JDateChooser JD_Awal;
    private javax.swing.JButton btnClear;
    private javax.swing.JButton btn_hapus;
    private javax.swing.JComboBox<String> cb_user;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel label_user;
    private javax.swing.JTextField t_cari;
    private javax.swing.JTable tabel_penjualan;
    private javax.swing.JLabel tgl_muncul;
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
                    tgl_muncul.setText(formattedDate);
                }
            };

            // Timer untuk memperbarui tampilan setiap detik
            new javax.swing.Timer(1000, taskPerformer).start();
        } catch (Exception e) {
            System.out.println("Error : " + e.getMessage());
        }
    }
}
