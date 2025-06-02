package Form;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import javax.swing.BorderFactory;

import main.Koneksi;
import java.sql.*;
import java.util.UUID;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;
import view.FormInventori;

import javax.swing.text.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
public class FormTambahBarang extends javax.swing.JDialog {

    public static javax.swing.JComboBox cb_supplier_static;
    private Connection conn;

    /**
     * Creates new form FormTambahBarang
     */
    public FormTambahBarang(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        // Format untuk text_harga
((AbstractDocument) text_harga.getDocument()).setDocumentFilter(new RupiahFormattedDocumentFilter());





// Format untuk text_stok
((AbstractDocument) text_stok.getDocument()).setDocumentFilter(new NumberOnlyDocumentFilter());
        text_barcode.setEnabled(false);

        cb_supplier_static = cb_supplier;
        FormTambahBarang.loadComboSupplier(cb_supplier);
        loadKategoriSatuan();
        updateBarcodeOtomatis();

        // Style tombol
        btn_simpan.setText("SIMPAN");
        btn_simpan.setBackground(new java.awt.Color(70, 130, 180)); // warna biru steel blue
        btn_simpan.setForeground(Color.WHITE);
        btn_simpan.setFont(new java.awt.Font("Serif", Font.BOLD, 12));
        btn_simpan.setFocusPainted(false);
        btn_simpan.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        btn_simpan.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Hover effect
        btn_simpan.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn_simpan.setBackground(new java.awt.Color(100, 149, 237)); // Cornflower Blue
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn_simpan.setBackground(new java.awt.Color(70, 130, 180));
            }
        });

        // Style tombol
        btn_batal.setText("BATAL");
        btn_batal.setBackground(new java.awt.Color(70, 130, 180)); // warna biru steel blue
        btn_batal.setForeground(Color.WHITE);
        btn_batal.setFont(new java.awt.Font("Serif", Font.BOLD, 12));
        btn_batal.setFocusPainted(false);
        btn_batal.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        btn_batal.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Hover effect
        btn_batal.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn_batal.setBackground(new java.awt.Color(100, 149, 237)); // Cornflower Blue
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn_batal.setBackground(new java.awt.Color(70, 130, 180));
            }
        });

    }

    private void loadKategoriSatuan() {
        // Kategori
        cb_kategori.removeAllItems();
        cb_kategori.addItem("Alat Tulis");
        cb_kategori.addItem("Kertas & Buku");
        cb_kategori.addItem("Perekat");
        cb_kategori.addItem("Alat Ukur");
        cb_kategori.addItem("Lain - lain");

        // Satuan
        cb_satuan.removeAllItems();
        cb_satuan.addItem("pcs");
        cb_satuan.addItem("pak");
        cb_satuan.addItem("lusin");
        cb_satuan.addItem("box");
        cb_satuan.addItem("buah");
    }

    public static class ItemSupplier {

        private int id;
        private String nama;

        public ItemSupplier(int id, String nama) {
            this.id = id;
            this.nama = nama;
        }

        public int getId() {
            return id;
        }

        @Override
        public String toString() {
            return nama; // supaya hanya nama supplier yang tampil di combo box
        }
    }

    public static void loadComboSupplier(JComboBox cb_supplier) {
        try {
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost/atk", "root", "");
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT id_supplier, nama FROM supplier");

            cb_supplier.removeAllItems(); // kosongkan dulu

            while (rs.next()) {
                int id = rs.getInt("id_supplier");
                String nama = rs.getString("nama");

                cb_supplier.addItem(new ItemSupplier(id, nama));
            }

            rs.close();
            st.close();
            con.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Gagal load supplier: " + e.getMessage());
        }
    }

    public class ComboItem {

        private String label;
        private int value;

        public ComboItem(String label, int value) {
            this.label = label;
            this.value = value;
        }

        @Override
        public String toString() {
            return label;
        }

        public int getValue() {
            return value;
        }
    }

    private void updateBarcodeOtomatis() {
    String nama = text_namabarang.getText().trim();
    String kategori = cb_kategori.getSelectedItem() != null ? cb_kategori.getSelectedItem().toString() : "";
    String satuan = cb_satuan.getSelectedItem() != null ? cb_satuan.getSelectedItem().toString() : "";

    if (!nama.isEmpty() && !kategori.isEmpty() && !satuan.isEmpty()) {
        String prefix = nama.substring(0, Math.min(3, nama.length())).toUpperCase();
        String kat = kategori.substring(0, Math.min(2, kategori.length())).toUpperCase();
        String sat = satuan.substring(0, Math.min(2, satuan.length())).toUpperCase();

        String timeStamp = new java.text.SimpleDateFormat("yyMMddHHmmss").format(new java.util.Date());
        String barcode = prefix + kat + sat + timeStamp;
        text_barcode.setText(barcode);
    }
}
    
    
    public class RupiahFormattedDocumentFilter extends DocumentFilter {
    private static final DecimalFormat formatter;

    static {
        formatter = (DecimalFormat) NumberFormat.getInstance(new Locale("id", "ID"));
        formatter.setGroupingUsed(true);
        formatter.setMaximumFractionDigits(0);
    }

    @Override
    public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
            throws BadLocationException {
        if (string == null || string.isEmpty()) return;
        replace(fb, offset, 0, string, attr);
    }

    @Override
    public void remove(FilterBypass fb, int offset, int length)
            throws BadLocationException {
        Document doc = fb.getDocument();
        String currentText = doc.getText(0, doc.getLength());
        String cleanText = currentText.replaceAll("[^\\d]", "");

        if (cleanText.isEmpty()) {
            fb.replace(0, doc.getLength(), "", null);
            return;
        }

        StringBuilder sb = new StringBuilder(cleanText);
        int pos = offset - countNonDigits(currentText.substring(0, offset));
        if (pos >= 0 && pos < sb.length()) {
            sb.deleteCharAt(pos);
        }

        String newValue = sb.toString().isEmpty() ? "" : formatRupiah(sb.toString());
        fb.replace(0, doc.getLength(), newValue, null);
    }

    @Override
    public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
            throws BadLocationException {
        Document doc = fb.getDocument();
        String currentText = doc.getText(0, doc.getLength());
        String cleanCurrent = currentText.replaceAll("[^\\d]", "");
        String newText = (text != null) ? text.replaceAll("[^\\d]", "") : "";

        int digitOffset = offset - countNonDigits(currentText.substring(0, offset));
        StringBuilder sb = new StringBuilder(cleanCurrent);
        if (length > 0 && digitOffset + length <= sb.length()) {
            sb.replace(digitOffset, digitOffset + length, newText);
        } else if (!newText.isEmpty()) {
            sb.insert(digitOffset, newText);
        }

        String newValue = sb.toString().isEmpty() ? "" : formatRupiah(sb.toString());
        fb.replace(0, doc.getLength(), newValue, attrs);
    }

    private int countNonDigits(String text) {
        return (int) text.chars().filter(c -> !Character.isDigit(c)).count();
    }

    private String formatRupiah(String digitsOnly) {
        try {
            long number = Long.parseLong(digitsOnly);
            return "Rp. " + formatter.format(number);
        } catch (NumberFormatException e) {
            return "Rp. 0";
        }
    }
}


    
    public class NumberOnlyDocumentFilter extends DocumentFilter {
    @Override
    public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
        if (text.matches("\\d*")) {
            super.replace(fb, offset, length, text, attrs);
        }
    }

    @Override
    public void insertString(FilterBypass fb, int offset, String text, AttributeSet attr) throws BadLocationException {
        if (text.matches("\\d*")) {
            super.insertString(fb, offset, text, attr);
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

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new main.gradasiwarna();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        text_barcode = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        text_namabarang = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        cb_kategori = new javax.swing.JComboBox<>();
        jLabel6 = new javax.swing.JLabel();
        text_harga = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        text_stok = new javax.swing.JTextField();
        btn_batal = new javax.swing.JButton();
        btn_simpan = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        cb_supplier = new javax.swing.JComboBox<>();
        jLabel10 = new javax.swing.JLabel();
        cb_satuan = new javax.swing.JComboBox<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jPanel2.setBackground(new java.awt.Color(0, 0, 255));

        jLabel1.setFont(new java.awt.Font("SansSerif", 1, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Form Inventori");

        jLabel2.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Tambah Barang");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 385, Short.MAX_VALUE)
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

        jLabel3.setFont(new java.awt.Font("SansSerif", 1, 13)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(0, 0, 0));
        jLabel3.setText("Barcode");

        text_barcode.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        text_barcode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                text_barcodeActionPerformed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("SansSerif", 1, 13)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(0, 0, 0));
        jLabel4.setText("Nama Barang");

        text_namabarang.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        text_namabarang.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                text_namabarangActionPerformed(evt);
            }
        });
        text_namabarang.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                text_namabarangKeyReleased(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("SansSerif", 1, 13)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(0, 0, 0));
        jLabel5.setText("Kategori");

        cb_kategori.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        cb_kategori.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cb_kategori.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cb_kategoriActionPerformed(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("SansSerif", 1, 13)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(0, 0, 0));
        jLabel6.setText("Harga");

        text_harga.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        text_harga.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                text_hargaActionPerformed(evt);
            }
        });

        jLabel7.setFont(new java.awt.Font("SansSerif", 1, 13)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(0, 0, 0));
        jLabel7.setText("Stok");

        text_stok.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        text_stok.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                text_stokActionPerformed(evt);
            }
        });

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

        jLabel8.setFont(new java.awt.Font("SansSerif", 1, 13)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(0, 0, 0));
        jLabel8.setText("Supplier");

        cb_supplier.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N

        jLabel10.setFont(new java.awt.Font("SansSerif", 1, 13)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(0, 0, 0));
        jLabel10.setText("Satuan");

        cb_satuan.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        cb_satuan.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cb_satuan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cb_satuanActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(cb_satuan, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(cb_supplier, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(text_harga, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(cb_kategori, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(text_namabarang, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(text_barcode, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(text_stok, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                    .addComponent(btn_simpan, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(btn_batal, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(text_barcode, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(text_namabarang, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cb_kategori, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cb_satuan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(text_harga, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(text_stok, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cb_supplier, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(27, 27, 27)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_batal)
                    .addComponent(btn_simpan))
                .addGap(73, 73, 73))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 500, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void text_barcodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_text_barcodeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_text_barcodeActionPerformed

    private void text_namabarangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_text_namabarangActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_text_namabarangActionPerformed

    private void text_hargaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_text_hargaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_text_hargaActionPerformed

    private void text_stokActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_text_stokActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_text_stokActionPerformed

    private void btn_batalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_batalActionPerformed
        dispose();
    }//GEN-LAST:event_btn_batalActionPerformed

    private void btn_simpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_simpanActionPerformed
// Ambil input dari form
String namaBarang = text_namabarang.getText().trim();
String kategori = cb_kategori.getSelectedItem() != null ? cb_kategori.getSelectedItem().toString() : "";
String satuan = cb_satuan.getSelectedItem() != null ? cb_satuan.getSelectedItem().toString() : "";
String harga = text_harga.getText().replace("Rp. ", "").replace(".", "").trim();
String stok = text_stok.getText().trim();

// Validasi input
if (namaBarang.isEmpty() || kategori.isEmpty() || satuan.isEmpty() || harga.isEmpty() || stok.isEmpty()) {
    JOptionPane.showMessageDialog(this, "Semua field harus diisi!");
    return;
}

// Generate ID Barang (random dan rapi)
String idBarang = "BRG-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

// Generate barcode otomatis
String prefix = namaBarang.substring(0, Math.min(3, namaBarang.length())).toUpperCase();
String kat = kategori.substring(0, Math.min(2, kategori.length())).toUpperCase();
String sat = satuan.substring(0, Math.min(2, satuan.length())).toUpperCase();
String timeStamp = new java.text.SimpleDateFormat("yyMMddHHmmss").format(new java.util.Date());
String barcode = prefix + kat + sat + timeStamp;
text_barcode.setText(barcode);

// Ambil ID Supplier
ItemSupplier selectedSupplier = (ItemSupplier) cb_supplier.getSelectedItem();
int supplierId = selectedSupplier != null ? selectedSupplier.getId() : -1;

if (supplierId == -1) {
    JOptionPane.showMessageDialog(this, "Supplier belum dipilih.");
    return;
}

try {
    Connection con = DriverManager.getConnection("jdbc:mysql://localhost/atk", "root", "");

    String sql = "INSERT INTO barang (Id_barang, barcode, Nama_barang, Kategori, Satuan, Harga, Stok, Id_Supplier) "
               + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    PreparedStatement ps = con.prepareStatement(sql);
    ps.setString(1, idBarang);
    ps.setString(2, barcode);
    ps.setString(3, namaBarang);
    ps.setString(4, kategori);
    ps.setString(5, satuan);
    ps.setString(6, harga);
    ps.setString(7, stok);
    ps.setInt(8, supplierId);

    ps.executeUpdate();
    JOptionPane.showMessageDialog(this, "Data berhasil disimpan!");

    // Refresh tabel barang jika form induknya FormInventori
    if (getParent() instanceof FormInventori) {
        ((FormInventori) getParent()).tampilkanBarang();
    }

    dispose(); // tutup form
    ps.close();
    con.close();

} catch (SQLException e) {
    JOptionPane.showMessageDialog(this, "Gagal menyimpan data: " + e.getMessage());
}
    }//GEN-LAST:event_btn_simpanActionPerformed

    private void text_namabarangKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_text_namabarangKeyReleased
        updateBarcodeOtomatis();
    }//GEN-LAST:event_text_namabarangKeyReleased

    private void cb_kategoriActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cb_kategoriActionPerformed
        updateBarcodeOtomatis();
    }//GEN-LAST:event_cb_kategoriActionPerformed

    private void cb_satuanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cb_satuanActionPerformed
        updateBarcodeOtomatis();
    }//GEN-LAST:event_cb_satuanActionPerformed

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
            java.util.logging.Logger.getLogger(FormTambahBarang.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(FormTambahBarang.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(FormTambahBarang.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FormTambahBarang.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                FormTambahBarang dialog = new FormTambahBarang(new javax.swing.JFrame(), true);
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
    private javax.swing.JComboBox<String> cb_satuan;
    private javax.swing.JComboBox<String> cb_supplier;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JTextField text_barcode;
    private javax.swing.JTextField text_harga;
    private javax.swing.JTextField text_namabarang;
    private javax.swing.JTextField text_stok;
    // End of variables declaration//GEN-END:variables
}
