package Form;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.NumberFormat;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import view.FormPembelian;
import java.sql.PreparedStatement;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import view.FormPembelian1;

/**
 *
 * @author Agung Fahril Gunawan
 */
public class FormCariBarang1 extends javax.swing.JDialog {

    private FormPembelian1 formPembelian;

    public FormCariBarang1(Frame parent, boolean modal, FormPembelian1 formPembelian) {
        super(parent, modal);
        this.formPembelian = formPembelian;
        initComponents();
        setupUI();
        setupListeners();
        initTableModel();
        tampilkanBarang("");
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
    .put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "TUTUP_DIALOG");
getRootPane().getActionMap().put("TUTUP_DIALOG", new AbstractAction() {
    @Override
    public void actionPerformed(ActionEvent e) {
        dispose();
    }
});
    }
    
    


    private void setupUI() {
        styleButton(btn_tambah, "TAMBAH");
        cb_kategori.setSelectedItem("Nama Barang");
    }

    private void setupListeners() {
        cb_kategori.addActionListener(e -> tampilkanBarang(t_caribarang.getText().trim()));

        t_caribarang.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
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
                tampilkanBarang(t_caribarang.getText().trim());
            }
        });

        btn_tambah.addActionListener(e -> pilihBarang());
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

    private void pilihBarang() {
        int row = tbl_barang.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Pilih data barang terlebih dahulu!");
            return;
        }

        try {
            String barcode = tbl_barang.getValueAt(row, 6).toString();
            String nama = tbl_barang.getValueAt(row, 1).toString();
            String satuan = tbl_barang.getValueAt(row, 3).toString();
            String hargaStr = tbl_barang.getValueAt(row, 4).toString()
                    .replace("Rp", "").replace(".", "").replace(",", ".").trim();
            double harga = Double.parseDouble(hargaStr);
            int stok = Integer.parseInt(tbl_barang.getValueAt(row, 5).toString());
            String supplier = tbl_barang.getValueAt(row, 7).toString();

            formPembelian.terimaDataBarang(barcode, nama, satuan, harga, stok, supplier);
            dispose();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Format data tidak valid: " + e.getMessage());
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
        jScrollPane1 = new javax.swing.JScrollPane();
        tbl_barang = new javax.swing.JTable();
        btn_tambah = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        t_caribarang = new javax.swing.JTextField();
        cb_kategori = new javax.swing.JComboBox<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setBackground(new java.awt.Color(51, 102, 255));

        jPanel1.setBackground(new java.awt.Color(51, 51, 255));

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

        btn_tambah.setText("Tambah");
        btn_tambah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_tambahActionPerformed(evt);
            }
        });

        jLabel1.setText("Cari");

        t_caribarang.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                t_caribarangActionPerformed(evt);
            }
        });

        cb_kategori.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Nama Barang", "Kategori", "Satuan", "Harga", "Stok", "Barcode", "Supplier" }));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(btn_tambah)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(t_caribarang, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 639, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(cb_kategori, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(31, Short.MAX_VALUE)
                .addComponent(cb_kategori, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_tambah)
                    .addComponent(jLabel1)
                    .addComponent(t_caribarang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 317, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(21, 21, 21))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void t_caribarangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_t_caribarangActionPerformed
        if (tbl_barang.getRowCount() > 0) {
            tbl_barang.requestFocus();
            tbl_barang.setRowSelectionInterval(0, 0); // Pilih baris pertama
        }
    }//GEN-LAST:event_t_caribarangActionPerformed

    private void btn_tambahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_tambahActionPerformed
        pilihBarang();
    }//GEN-LAST:event_btn_tambahActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FormPembelian1 dummyForm = new FormPembelian1() {
                @Override
                public void terimaDataBarang(String kode, String nama, String satuan, double harga, int stok, String supplier) {
                    System.out.printf("""
        Data diterima:
        Kode      : %s
        Nama      : %s
        Satuan    : %s
        Harga     : %.2f
        Stok      : %d
        Supplier  : %s
        """, kode, nama, satuan, harga, stok, supplier);
                }
            };

            FormCariBarang1 dialog = new FormCariBarang1(null, true, dummyForm);
            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            dialog.setVisible(true);
});
}


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_tambah;
    private javax.swing.JComboBox<String> cb_kategori;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField t_caribarang;
    private javax.swing.JTable tbl_barang;
    // End of variables declaration//GEN-END:variables
}
