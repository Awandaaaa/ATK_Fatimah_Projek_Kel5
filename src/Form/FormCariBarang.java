package Form;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
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
import view.FormPenjualan;
import java.text.NumberFormat;
import java.util.Locale;


public class FormCariBarang extends javax.swing.JDialog {
    
    private FormPenjualan formPenjualan;
    
    
    public FormCariBarang(java.awt.Frame parent, boolean modal, FormPenjualan formPenjualan) { 
    super(parent, modal);
    this.formPenjualan = formPenjualan;
    
    initComponents();
    
    text_cari.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
    public void insertUpdate(javax.swing.event.DocumentEvent e) {
        tampilkanBarang(text_cari.getText().trim());
    }

    public void removeUpdate(javax.swing.event.DocumentEvent e) {
        tampilkanBarang(text_cari.getText().trim());
    }

    public void changedUpdate(javax.swing.event.DocumentEvent e) {
        tampilkanBarang(text_cari.getText().trim());
    }
});

    styleButtons();
        
        
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
        
        cb_kriteria.addActionListener(e -> {
    tampilkanBarang(text_cari.getText().trim());
});
        
        cb_kriteria.setSelectedItem("Nama Barang"); 
tampilkanBarang(""); 

    }
        
    private void styleButtons() {       
        styleButton(btn_tambah, "TAMBAH");
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
    switch (kriteria) {
        case "Nama Barang": return "b.Nama_barang";
        case "Kategori": return "b.Kategori";
        case "Satuan": return "b.Satuan";
        case "Harga": return "b.Harga";
        case "Stok": return "b.Stok";
        case "Barcode": return "b.barcode";
        case "Supplier": return "s.nama";
        default: return "b.Nama_barang"; 
    }
}

     public void tampilkanBarang(String keyword) {
    DefaultTableModel model = (DefaultTableModel) table_barang.getModel();
    model.setRowCount(0); 

    try {
        String kriteria = (String) cb_kriteria.getSelectedItem();
        String kolom = getKolomDariKriteria(kriteria);

        Connection con = DriverManager.getConnection("jdbc:mysql://localhost/atk", "root", "");
        String sql = "SELECT b.Id_barang, b.Nama_barang, b.Kategori, b.Satuan, b.Harga, b.Stok, b.barcode, s.nama AS nama_supplier "
                   + "FROM barang b "
                   + "LEFT JOIN supplier s ON b.Id_Supplier = s.id_supplier "
                   + "WHERE " + kolom + " LIKE ? "
                   + "ORDER BY " + kolom + " ASC";
        java.sql.PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1, "%" + keyword + "%");
        ResultSet rs = ps.executeQuery();

        Locale indo = new Locale("id", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(indo);

        while (rs.next()) {
            double harga = rs.getDouble("Harga");
            String hargaRupiah = formatRupiah.format(harga);

            model.addRow(new Object[]{
                rs.getString("Id_barang"),
                rs.getString("Nama_barang"),
                rs.getString("Kategori"),
                rs.getString("Satuan"),
                hargaRupiah,                 rs.getString("Stok"),
                rs.getString("barcode"),
                rs.getString("nama_supplier")
            });
        }

        rs.close();
        ps.close();
        con.close();
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Gagal menampilkan data barang: " + e.getMessage());
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
        table_barang = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        text_cari = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        btn_tambah = new javax.swing.JButton();
        cb_kriteria = new javax.swing.JComboBox<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(0, 51, 255));

        table_barang.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(table_barang);

        jLabel1.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Cari Barang");

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

        btn_tambah.setText("TAMBAH");
        btn_tambah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_tambahActionPerformed(evt);
            }
        });

        cb_kriteria.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Nama Barang", "Kategori", "Satuan", "Harga", "Stok", "Barcode", "Supplier", " " }));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(btn_tambah, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(410, 410, 410)))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(cb_kriteria, 0, 178, Short.MAX_VALUE)
                            .addComponent(text_cari, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(21, 21, 21)
                        .addComponent(btn_tambah))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(text_cari, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(cb_kriteria, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 391, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
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

    private void text_cariActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_text_cariActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_text_cariActionPerformed

    private void btn_tambahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_tambahActionPerformed
 int selectedRow = table_barang.getSelectedRow();
    if (selectedRow >= 0) {
        try {
            String barcode = table_barang.getValueAt(selectedRow, 6).toString();
            String nama = table_barang.getValueAt(selectedRow, 1).toString();
            String satuan = table_barang.getValueAt(selectedRow, 3).toString();

            String hargaStr = table_barang.getValueAt(selectedRow, 4).toString();
            hargaStr = hargaStr.replace("Rp", "").replace(".", "").replace(",", ".").trim();
            double harga = Double.parseDouble(hargaStr);

            int stok = Integer.parseInt(table_barang.getValueAt(selectedRow, 5).toString());

            formPenjualan.terimaDataBarang(barcode, nama, satuan, harga, stok);
            dispose();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Format harga tidak valid!");
        }
    } else {
        JOptionPane.showMessageDialog(this, "Pilih data barang terlebih dahulu!");
    }
    }//GEN-LAST:event_btn_tambahActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
    java.awt.EventQueue.invokeLater(new Runnable() {
        public void run() {

            FormPenjualan dummyPenjualan = new FormPenjualan() {
                @Override
                public void terimaDataBarang(String kode, String nama, String satuan, double harga, int stok) {

                    System.out.println("Data diterima:");
                    System.out.println("Kode   : " + kode);
                    System.out.println("Nama   : " + nama);
                    System.out.println("Satuan : " + satuan);
                    System.out.println("Harga  : " + harga);
                    System.out.println("Stok   : " + stok);
                }
            };

            FormCariBarang dialog = new FormCariBarang(new javax.swing.JFrame(), true, dummyPenjualan);
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
