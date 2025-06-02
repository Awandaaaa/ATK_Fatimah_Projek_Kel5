package Form;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.Date;
import java.util.Locale;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import main.Koneksi;
import view.FormBarangRusak;


public class FormTambahBarangRusak extends javax.swing.JDialog {

    NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
    private FormBarangRusak parent;

    
    public FormTambahBarangRusak(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        
        initComponents();
        
       setLocationRelativeTo(null);
        initTableModel();
        setupListeners();
        tampilkanBarang("");
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
    .put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "TUTUP_DIALOG");
getRootPane().getActionMap().put("TUTUP_DIALOG", new AbstractAction() {
    @Override
    public void actionPerformed(ActionEvent e) {
        dispose();
    }
});
SwingUtilities.invokeLater(() -> t_jumlah.requestFocus());

        btn_simpan.setText("SIMPAN");
        btn_simpan.setBackground(new java.awt.Color(70, 130, 180)); 
        btn_simpan.setForeground(Color.WHITE);
        btn_simpan.setFont(new java.awt.Font("Serif", Font.BOLD, 12));
        btn_simpan.setFocusPainted(false);
        btn_simpan.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        btn_simpan.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn_simpan.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn_simpan.setBackground(new java.awt.Color(100, 149, 237)); 
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn_simpan.setBackground(new java.awt.Color(70, 130, 180));
            }
        });
        
        btn_batal.setText("BATAL");
        btn_batal.setBackground(new java.awt.Color(70, 130, 180)); 
        btn_batal.setForeground(Color.WHITE);
        btn_batal.setFont(new java.awt.Font("Serif", Font.BOLD, 12));
        btn_batal.setFocusPainted(false);
        btn_batal.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        btn_batal.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Hover effect
        btn_batal.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn_batal.setBackground(new java.awt.Color(100, 149, 237));        
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn_batal.setBackground(new java.awt.Color(70, 130, 180));
            }
        });
        t_jumlah.setFont(new Font("Segoe UI", Font.PLAIN, 14));
t_keterangan.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        
    }
    
    private void setupListeners() {
        cb_kategori.addActionListener(e -> tampilkanBarang(jTextField1.getText().trim()));

        jTextField1.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                filterBarang();
            }

            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                filterBarang();
            }

            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                filterBarang();
            }

            private void filterBarang() {
                tampilkanBarang(jTextField1.getText().trim());
            }
        });
        
        tbl_barang.addMouseListener(new MouseAdapter() {
    @Override
    public void mouseClicked(MouseEvent e) {
        int row = tbl_barang.getSelectedRow();
        if (row != -1) {
            t_jumlah.requestFocus();
        }
    }
});

            btn_simpan.addActionListener(e -> simpanBarangRusak());

    }
    
    private void initTableModel() {
        DefaultTableModel model = new DefaultTableModel(new Object[]{
            "No Barang", "Nama Barang", "Kategori", "Satuan", "Harga", "Stok", "Barcode", "Supplier"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        tbl_barang.setModel(model);
        setupTableStyle();
    }
    
    private void setupTableStyle() {
        JTableHeader header = tbl_barang.getTableHeader();
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                JLabel label = new JLabel(value.toString());
                label.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 16));
                label.setOpaque(true);
                label.setBackground(new Color(0, 123, 255));
                label.setForeground(Color.WHITE);
                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
                return label;
            }
        });

        tbl_barang.setRowHeight(30);
        tbl_barang.setShowGrid(true);
        tbl_barang.setGridColor(Color.LIGHT_GRAY);
        tbl_barang.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tbl_barang.setSelectionBackground(new Color(204, 229, 255));
        tbl_barang.setSelectionForeground(Color.BLACK);
    }
    
    private String getKolomDariKriteria(String kriteria) {
        return switch (kriteria) {
            case "Nama Barang" ->
                "b.Nama_barang";
            case "Kategori" ->
                "b.Kategori";
            case "Satuan" ->
                "b.Satuan";
            case "Harga" ->
                "b.Harga";
            case "Stok" ->
                "b.Stok";
            case "Barcode" ->
                "b.barcode";
            case "Supplier" ->
                "s.nama";
            default ->
                "b.Nama_barang";
        };
    }

    public void tampilkanBarang(String keyword) {
        DefaultTableModel model = (DefaultTableModel) tbl_barang.getModel();
        model.setRowCount(0);

        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost/atk", "root", "")) {
            String kolom = getKolomDariKriteria((String) cb_kategori.getSelectedItem());
            String sql = "SELECT b.Id_barang, b.Nama_barang, b.Kategori, b.Satuan, b.Harga, b.Stok, b.barcode, "
                    + "s.nama AS nama_supplier FROM barang b "
                    + "LEFT JOIN supplier s ON b.Id_Supplier = s.id_supplier "
                    + "WHERE " + kolom + " LIKE ? ORDER BY " + kolom + " ASC";

            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, "%" + keyword + "%");
                ResultSet rs = ps.executeQuery();

                NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
                while (rs.next()) {
                    model.addRow(new Object[]{
                        rs.getString("Id_barang"),
                        rs.getString("Nama_barang"),
                        rs.getString("Kategori"),
                        rs.getString("Satuan"),
                        formatRupiah.format(rs.getDouble("Harga")),
                        rs.getInt("Stok"),
                        rs.getString("barcode"),
                        rs.getString("nama_supplier")
                    });
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal menampilkan data barang:\n" + e.getMessage());
        }
    }
    
    private void simpanBarangRusak() {
    int selectedRow = tbl_barang.getSelectedRow();
    if (selectedRow == -1) {
JOptionPane.showMessageDialog(this, "Pilih barang yang rusak dari tabel terlebih dahulu!");
        return;
    }

    String idBarang = tbl_barang.getValueAt(selectedRow, 0).toString();
    String barcode = tbl_barang.getValueAt(selectedRow, 6).toString(); 
    int stok = Integer.parseInt(tbl_barang.getValueAt(selectedRow, 5).toString());

    String jumlahStr = t_jumlah.getText().trim();
    String keterangan = t_keterangan.getText().trim();
    Date tanggal = d_tanggal.getDate();

    if (jumlahStr.isEmpty() || tanggal == null) {
        JOptionPane.showMessageDialog(this, "Tanggal dan jumlah rusak wajib diisi!");
        return;
    }
    if (tanggal.after(new java.util.Date())) {
    JOptionPane.showMessageDialog(this, "Tanggal tidak boleh melebihi hari ini!");
    return;
}


    int jumlahRusak;
    try {
        jumlahRusak = Integer.parseInt(jumlahStr);
        if (jumlahRusak <= 0 || jumlahRusak > stok) {
            JOptionPane.showMessageDialog(this, "Jumlah rusak harus valid dan tidak melebihi stok!");
            return;
        }
    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(this, "Jumlah rusak harus berupa angka!");
        return;
    }

    try (Connection conn = Koneksi.getConnection()) {
        conn.setAutoCommit(false);

        String idRusak = generateIdBarangRusak(conn);

        String insertSql = "INSERT INTO barang_rusak (id_barangrusak, id_barang, jumlah_rusak, Tgl_rusak, keterangan, barcode) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
            ps.setString(1, idRusak);
            ps.setString(2, idBarang);
            ps.setInt(3, jumlahRusak);
            ps.setDate(4, new java.sql.Date(tanggal.getTime()));
            ps.setString(5, keterangan);
            ps.setString(6, barcode);
            ps.executeUpdate();
        }

        String updateSql = "UPDATE barang SET stok = stok - ? WHERE id_barang = ?";
        try (PreparedStatement ps = conn.prepareStatement(updateSql)) {
            ps.setInt(1, jumlahRusak);
            ps.setString(2, idBarang);
            ps.executeUpdate();
        }

        resetForm();
        
        conn.commit();
        JOptionPane.showMessageDialog(this, "Barang rusak berhasil disimpan.");
        dispose(); 
    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Terjadi kesalahan saat menyimpan barang rusak.");
    }
}

    
    private String generateIdBarangRusak(Connection conn) throws SQLException {
    String prefix = "BRG-RS";
    String sql = "SELECT MAX(RIGHT(id_barangrusak, 5)) AS max_id FROM barang_rusak";
    try (PreparedStatement ps = conn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
            int next = rs.getString("max_id") != null ? Integer.parseInt(rs.getString("max_id")) + 1 : 1;
            return prefix + String.format("%05d", next);
        }
    }
    return prefix + "00001";
}

    
    private void resetForm() {
    t_jumlah.setText("");
    t_keterangan.setText("");
    d_tanggal.setDate(null);
    tbl_barang.clearSelection();
}



    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new main.gradasiwarna();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        btn_batal = new javax.swing.JButton();
        btn_simpan = new javax.swing.JButton();
        jLabel9 = new javax.swing.JLabel();
        t_jumlah = new javax.swing.JTextField();
        d_tanggal = new com.toedter.calendar.JDateChooser();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        t_keterangan = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tbl_barang = new javax.swing.JTable();
        cb_kategori = new javax.swing.JComboBox<>();
        jTextField1 = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jPanel2.setBackground(new java.awt.Color(0, 0, 255));

        jLabel1.setFont(new java.awt.Font("SansSerif", 1, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Form Barang Rusak");

        jLabel2.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Tambah Barang Rusak");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 708, Short.MAX_VALUE)
            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addContainerGap(29, Short.MAX_VALUE))
        );

        btn_batal.setText("batal");
        btn_batal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_batalActionPerformed(evt);
            }
        });

        btn_simpan.setText("simpan");
        btn_simpan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_simpanActionPerformed(evt);
            }
        });

        jLabel9.setFont(new java.awt.Font("SansSerif", 1, 13)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(0, 0, 0));
        jLabel9.setText("Jumlah Rusak");

        t_jumlah.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        t_jumlah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                t_jumlahActionPerformed(evt);
            }
        });

        jLabel11.setFont(new java.awt.Font("SansSerif", 1, 13)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(0, 0, 0));
        jLabel11.setText("Tanggal");

        jLabel12.setFont(new java.awt.Font("SansSerif", 1, 13)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(0, 0, 0));
        jLabel12.setText("Keterangan");

        t_keterangan.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        t_keterangan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                t_keteranganActionPerformed(evt);
            }
        });

        tbl_barang.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(tbl_barang);

        cb_kategori.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Nama Barang", "Kategori", "Satuan", "Harga", "Stok", "Barcode", "Supplier" }));

        jTextField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField1ActionPerformed(evt);
            }
        });

        jLabel13.setFont(new java.awt.Font("SansSerif", 1, 13)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(0, 0, 0));
        jLabel13.setText("Cari");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jScrollPane1)
                        .addContainerGap())
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(d_tanggal, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel11)
                                    .addComponent(jLabel12))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel9)
                                        .addGap(0, 0, Short.MAX_VALUE))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addComponent(t_jumlah, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(0, 0, Short.MAX_VALUE))
                                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                                .addGap(0, 0, Short.MAX_VALUE)
                                                .addComponent(jLabel13)))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(cb_kategori, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(34, 34, 34))))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(t_keterangan, javax.swing.GroupLayout.PREFERRED_SIZE, 268, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(58, 58, 58))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(btn_simpan, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn_batal, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(t_jumlah, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cb_kategori, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(d_tanggal, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(t_keterangan, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_simpan)
                    .addComponent(btn_batal))
                .addContainerGap(14, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btn_batalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_batalActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btn_batalActionPerformed

    private void btn_simpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_simpanActionPerformed
        btn_simpan.addActionListener(e -> simpanBarangRusak());
        
    }//GEN-LAST:event_btn_simpanActionPerformed

    private void t_jumlahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_t_jumlahActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_t_jumlahActionPerformed

    private void t_keteranganActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_t_keteranganActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_t_keteranganActionPerformed

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField1ActionPerformed

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
            java.util.logging.Logger.getLogger(FormTambahBarangRusak.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(FormTambahBarangRusak.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(FormTambahBarangRusak.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FormTambahBarangRusak.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                FormTambahBarangRusak dialog = new FormTambahBarangRusak(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_batal;
    private javax.swing.JButton btn_simpan;
    private javax.swing.JComboBox<String> cb_kategori;
    private com.toedter.calendar.JDateChooser d_tanggal;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField t_jumlah;
    private javax.swing.JTextField t_keterangan;
    private javax.swing.JTable tbl_barang;
    // End of variables declaration//GEN-END:variables
}
