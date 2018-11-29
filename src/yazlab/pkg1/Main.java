/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package yazlab.pkg1;

import java.net.URL;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JWindow;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author ffeki
 */
public class Main extends javax.swing.JFrame {

    int sayac = 0;
    int mp = 0;
    int mr = 0;
    int nw = 0;
    int rec = 0;
    String uname;

    /**
     * Creates new form Ma
     */
    public Main(String user) {
        initComponents();
        DbConnect db = new DbConnect();
        ShowBooks();
        jL_Sayfa.setText("1");
//        ShowMP();
//        ShowMR();
        this.uname = user;

        helloUsername.setText("Hello! " + user);

        if (db.isAdmin(user)) {
            admin.setVisible(true);
        } else {
            admin.setVisible(false);
        }
    }
    
    int getIDfromUsername(){
        DbConnect db = new DbConnect();
        String sql = "Select * from users Where Username='" + uname+"'";
        int aydi=0;
        try {
            db.rs = db.st.executeQuery(sql);
            db.rs.next();
            aydi = db.rs.getInt("ID");
        } catch (SQLException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        return aydi;
    }

    public void ShowRec() {
       
        Filter fl = new Filter();
        fl.ID = getIDfromUsername();
        fl.benzerlikorani();
        String List[] = fl.sonuc.clone();
        Vector cols = new Vector();
        cols.add("Photo");
        cols.add("Title");
        cols.add("Author");

        DefaultTableModel Tablo = new DefaultTableModel(cols, 0) {
            public Class getColumnClass(int column) {
                switch (column) {
                    case 0:
                        return ImageIcon.class;
                    case 1:
                        return String.class;
                    case 2:
                        return String.class;
                    default:
                        return String.class;
                }
            }
        };
        DbConnect db = new DbConnect();
        Tablo.getDataVector().removeAllElements();

        try {
            for (int i = 0; i < 20; i++) {
                String sql = "SELECT * FROM books WHERE ISBN='" + List[i] + "'";
                db.rs = db.st.executeQuery(sql);

                while (db.rs.next()) {
                    ImageIcon img = new ImageIcon(new URL(db.rs.getString("ImgS")));
                    Tablo.addRow(new Object[]{img, db.rs.getString("Title"), db.rs.getString("Author")});

                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        jT_Recom.setModel(Tablo);
    }

    public void ShowNew() {
        //System.out.println("New");
        String ISBN[] = new String[11];
        String ortalama[] = new String[11];

        String sql = "SELECT * FROM books ORDER BY Time DESC LIMIT 10";
        Vector cols = new Vector();
        cols.add("Photo");
        cols.add("Title");
        cols.add("Author");

        DefaultTableModel Tablo = new DefaultTableModel(cols, 0) {
            public Class getColumnClass(int column) {
                switch (column) {
                    case 0:
                        return ImageIcon.class;
                    case 1:
                        return String.class;
                    case 2:
                        return String.class;
                    default:
                        return String.class;
                }
            }
        };
        DbConnect db = new DbConnect();

        try {
            db.rs = db.st.executeQuery(sql);
            Tablo.getDataVector().removeAllElements();

            while (db.rs.next()) {
                ImageIcon img = new ImageIcon(new URL(db.rs.getString("ImgS")));
                Tablo.addRow(new Object[]{img, db.rs.getString("Title"), db.rs.getString("Author")});

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        jT_new.setModel(Tablo);
    }

    public void ShowMR() {
        //System.out.println("MR");
        String ISBN[] = new String[11];
        String ortalama[] = new String[11];

        String sql = "SELECT Avg(Rating) AS ortalama, ISBN FROM ratings GROUP BY ISBN ORDER BY ortalama DESC LIMIT 10";
        Vector cols = new Vector();
        cols.add("Photo");
        cols.add("Title");
        cols.add("Author");
        cols.add("Average");

        DefaultTableModel Tablo = new DefaultTableModel(cols, 0) {
            public Class getColumnClass(int column) {
                switch (column) {
                    case 0:
                        return ImageIcon.class;
                    case 1:
                        return String.class;
                    case 2:
                        return String.class;
                    case 3:
                        return Integer.class;
                    default:
                        return String.class;
                }
            }
        };
        DbConnect db = new DbConnect();

        try {
            db.rs = db.st.executeQuery(sql);
            Tablo.getDataVector().removeAllElements();
            int i = 0;
            while (db.rs.next()) {
                ISBN[i] = db.rs.getString("ISBN");
                ortalama[i] = db.rs.getString("ortalama");
                //System.out.println(a+" "+ISBN[i]+" "+ortalama[i]);
                i++;
            }
            i = 0;

            while (i < 10) {
                //System.out.println(ISBN[i] + " " + ortalama[i]);
                String sql2 = "SELECT * FROM books WHERE ISBN='" + ISBN[i] + "'";
                db.rs = db.st.executeQuery(sql2);

                while (db.rs.next()) {
                    ImageIcon img = new ImageIcon(new URL(db.rs.getString("ImgS")));
                    Tablo.addRow(new Object[]{img, db.rs.getString("Title"), db.rs.getString("Author"), ortalama[i]});
                    //System.out.println(db.rs.getString("Title") + " - " + db.rs.getString("ImgS") + ortalama[i]);
                }
                i++;

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        jT_rated.setModel(Tablo);
    }

    public void ShowMP() {
        //System.out.println("MP");
        String[] isbn = new String[10];
        int[] rank = new int[10];
        String sql = "SELECT COUNT(ID) AS total, ISBN FROM ratings GROUP BY ISBN ORDER BY total DESC LIMIT 10";
        Vector cols = new Vector();
        cols.add("Photo");
        cols.add("Title");
        cols.add("Author");
        cols.add("Rank");

        DefaultTableModel Tablo = new DefaultTableModel(cols, 0) {
            public Class getColumnClass(int column) {
                switch (column) {
                    case 0:
                        return ImageIcon.class;
                    case 1:
                        return String.class;
                    case 2:
                        return String.class;
                    case 3:
                        return Integer.class;
                    default:
                        return String.class;
                }
            }
        };
        DbConnect db = new DbConnect();

        try {
            db.rs = db.st.executeQuery(sql);
            Tablo.getDataVector().removeAllElements();
            int i = 0;
            while (db.rs.next()) {
                isbn[i] = db.rs.getString("ISBN");
                rank[i] = db.rs.getInt("total");
                //System.out.println(isbn[i]);
                i++;
//                sql2 = "SELECT * FROM books WHERE ISBN= '" + db.rs.getString("ISBN") + "'";
//
//                db2.rs = db2.st.executeQuery(sql2);
//
//                db2.rs.next();
//                ImageIcon img = new ImageIcon(new URL(db2.rs.getString("ImgS")));
//                System.out.println("  -  "+db2.rs.getString("Title"));
//                Tablo.addRow(new Object[]{img, db2.rs.getString("Title"), db.rs.getString("total")});

            }

            int j = 0;
            for (int k = 0; k < 10; k++) {
                String sql2 = "SELECT * FROM books WHERE ISBN= '" + isbn[j] + "'";
                db.rs = db.st.executeQuery(sql2);
                while (db.rs.next()) {
                    ImageIcon img = new ImageIcon(new URL(db.rs.getString("ImgS")));
                    Tablo.addRow(new Object[]{img, db.rs.getString("Title"), db.rs.getString("Author"), rank[j]});
                }
                j++;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        jT_popular.setModel(Tablo);
    }

    public void ShowBooks() {
        String sql = "SELECT * FROM books limit 5 OFFSET " + sayac * 5;
        Vector cols = new Vector();
        cols.add("Photo");
        cols.add("Title");
        cols.add("Author");

        DefaultTableModel Tablo = new DefaultTableModel(cols, 0) {
            public Class getColumnClass(int column) {
                switch (column) {
                    case 0:
                        return ImageIcon.class;
                    case 1:
                        return String.class;
                    case 2:
                        return String.class;
                    default:
                        return String.class;
                }
            }
        };
        DbConnect db = new DbConnect();

        try {
            db.rs = db.st.executeQuery(sql);
            Tablo.getDataVector().removeAllElements();

            while (db.rs.next()) {

                ImageIcon img = new ImageIcon(new URL(db.rs.getString("ImgS")));
                Tablo.addRow(new Object[]{img, db.rs.getString("Title"), db.rs.getString("Author")});
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        jT_Books.setModel(Tablo);
    }

    public void ShowMost() {
        String sql = "SELECT * FROM books limit 30";
        Vector cols = new Vector();
        cols.add("Fotoğraf");
        cols.add("ISBN");
        cols.add("Kitap Adı");

        DefaultTableModel Tablo = new DefaultTableModel(cols, 0) {
            public Class getColumnClass(int column) {
                switch (column) {
                    case 0:
                        return ImageIcon.class;
                    case 1:
                        return String.class;
                    case 2:
                        return String.class;
                    default:
                        return String.class;
                }
            }
        };
        DbConnect db = new DbConnect();

        try {
            db.rs = db.st.executeQuery(sql);
            Tablo.getDataVector().removeAllElements();

            while (db.rs.next()) {

                ImageIcon img = new ImageIcon(new URL(db.rs.getString("ImgS")));
                Tablo.addRow(new Object[]{img, db.rs.getString("Book-Title"), db.rs.getString("Book-Author")});
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        jT_Books.setModel(Tablo);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        helloUsername = new javax.swing.JLabel();
        admin = new javax.swing.JButton();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jT_Books = new javax.swing.JTable();
        j_Onceki = new javax.swing.JButton();
        j_Sonraki = new javax.swing.JButton();
        jL_Sayfa = new javax.swing.JLabel();
        jT_Sayfa = new javax.swing.JTextField();
        j_Git = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        jT_popular = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        jT_rated = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jT_new = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        jT_Recom = new javax.swing.JTable();
        jButton1 = new javax.swing.JButton();
        jB_Rating = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        jLabel1.setText("Ne Okusam? - Anasayfa");

        helloUsername.setText("Hoşgeldin, xxxx");

        admin.setText("Admin Panel");
        admin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                adminActionPerformed(evt);
            }
        });

        jTabbedPane1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTabbedPane1MouseClicked(evt);
            }
        });

        jT_Books.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Title", "Author", "Image"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jT_Books.setRowHeight(150);
        jT_Books.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jT_BooksMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jT_Books);

        j_Onceki.setText("<");
        j_Onceki.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                j_OncekiActionPerformed(evt);
            }
        });

        j_Sonraki.setText(">");
        j_Sonraki.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                j_SonrakiActionPerformed(evt);
            }
        });

        jL_Sayfa.setText("    ");

        jT_Sayfa.setText("10");
        jT_Sayfa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jT_SayfaActionPerformed(evt);
            }
        });

        j_Git.setText("Git");
        j_Git.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                j_GitActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1077, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(j_Onceki)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jL_Sayfa, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(j_Sonraki)
                .addGap(18, 18, 18)
                .addComponent(jT_Sayfa, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(j_Git)
                .addGap(391, 391, 391))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 500, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(j_Onceki)
                    .addComponent(jL_Sayfa)
                    .addComponent(j_Sonraki)
                    .addComponent(jT_Sayfa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(j_Git))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Books", jPanel1);

        jT_popular.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Title", "Author", "Image"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jT_popular.setRowHeight(150);
        jT_popular.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jT_popularMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(jT_popular);

        jTabbedPane1.addTab("Most Popular", jScrollPane3);

        jT_rated.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Title", "Author", "Image"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jT_rated.setRowHeight(150);
        jT_rated.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jT_ratedMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(jT_rated);

        jTabbedPane1.addTab("Most Rated", jScrollPane2);

        jT_new.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Title", "Author", "Image"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jT_new.setRowHeight(150);
        jT_new.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jT_newMouseClicked(evt);
            }
        });
        jScrollPane4.setViewportView(jT_new);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1089, Short.MAX_VALUE)
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel2Layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 1089, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 556, Short.MAX_VALUE)
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel2Layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 556, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );

        jTabbedPane1.addTab("New Books", jPanel2);

        jT_Recom.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Title", "Author", "Image"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jT_Recom.setRowHeight(150);
        jT_Recom.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jT_RecomMouseClicked(evt);
            }
        });
        jScrollPane5.setViewportView(jT_Recom);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1089, Short.MAX_VALUE)
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel3Layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 1089, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 556, Short.MAX_VALUE)
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel3Layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 556, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );

        jTabbedPane1.addTab("Recommendation", jPanel3);

        jButton1.setText("Exit");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jB_Rating.setText("Rating");
        jB_Rating.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jB_RatingActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jTabbedPane1)
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 415, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jB_Rating)
                        .addGap(18, 18, 18)
                        .addComponent(admin)
                        .addGap(18, 18, 18)
                        .addComponent(helloUsername, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton1)
                        .addGap(28, 28, 28))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(helloUsername, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(admin)
                    .addComponent(jButton1)
                    .addComponent(jB_Rating))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane1)
                .addContainerGap())
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void adminActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_adminActionPerformed
        dispose();

        Admin admin = new Admin();
        admin.setVisible(true);
    }//GEN-LAST:event_adminActionPerformed

    private void j_OncekiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_j_OncekiActionPerformed
        // TODO add your handling code here:
        sayac--;
        if (sayac < 0) {
            sayac = 0;
        }
        ShowBooks();
        jT_Sayfa.setText("" + (sayac + 1));
        jL_Sayfa.setText("" + (sayac + 1));
    }//GEN-LAST:event_j_OncekiActionPerformed

    private void jT_SayfaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jT_SayfaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jT_SayfaActionPerformed

    private void j_SonrakiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_j_SonrakiActionPerformed
        // TODO add your handling code here:
        int sayi = 0;
        sayac++;
        DbConnect db = new DbConnect();
        String sql = "SELECT COUNT(ISBN) as total FROM books";
        try {
            db.rs = db.st.executeQuery(sql);
            while (db.rs.next()) {
                sayi = db.rs.getInt("total");
            }
        } catch (SQLException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (sayac > (sayi / 5)) {
            sayac = (sayi / 5);
        }
        ShowBooks();
        jT_Sayfa.setText("" + (sayac + 1));
        jL_Sayfa.setText("" + (sayac + 1));
    }//GEN-LAST:event_j_SonrakiActionPerformed

    private void j_GitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_j_GitActionPerformed
        // TODO add your handling code here:
        sayac = Integer.parseInt(jT_Sayfa.getText());
        int sayi = 0;
        DbConnect db = new DbConnect();
        String sql = "SELECT COUNT(ISBN) as total FROM books";
        try {
            db.rs = db.st.executeQuery(sql);
            while (db.rs.next()) {
                sayi = db.rs.getInt("total");
            }
        } catch (SQLException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (sayac > (sayi / 5)) {
            sayac = (sayi / 5);
        }
        ShowBooks();
        jT_Sayfa.setText("" + (sayac + 1));
        jL_Sayfa.setText("" + (sayac + 1));
    }//GEN-LAST:event_j_GitActionPerformed

    private void jTabbedPane1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTabbedPane1MouseClicked
        // TODO add your handling code here:

        int x = jTabbedPane1.getSelectedIndex();
        switch (x) {
            case 0:
                ShowBooks();
                break;

            case 1:
                if (mp < 1) {
                    ShowMP();
                    mp = 1;
                    break;
                } else {
                    break;
                }

            case 2:
                if (mr < 1) {
                    ShowMR();
                    mr = 1;
                    break;
                } else {
                    break;
                }

            case 3:
                if (nw < 1) {
                    ShowNew();
                    nw = 1;
                    break;
                } else {
                    break;
                }
            case 4:
                if (rec < 1) {
                    ShowRec();
                    rec = 1;
                    break;
                } else {
                    break;
                }

            default:
                System.out.println("tabbed - err");
                break;
        }
    }//GEN-LAST:event_jTabbedPane1MouseClicked

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        dispose();
        MainScreen mains = new MainScreen();
        mains.setVisible(true);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jT_BooksMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jT_BooksMouseClicked
        // TODO add your handling code here:
        Read read = new Read();
        read.setVisible(true);
        read.openpdf(read.randomPdf());
    }//GEN-LAST:event_jT_BooksMouseClicked

    private void jT_popularMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jT_popularMouseClicked
        // TODO add your handling code here:
        Read read = new Read();
        read.setVisible(true);
        read.openpdf(read.randomPdf());
    }//GEN-LAST:event_jT_popularMouseClicked

    private void jT_ratedMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jT_ratedMouseClicked
        // TODO add your handling code here:
        Read read = new Read();
        read.setVisible(true);
        read.openpdf(read.randomPdf());
    }//GEN-LAST:event_jT_ratedMouseClicked

    private void jT_newMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jT_newMouseClicked
        // TODO add your handling code here:
        Read read = new Read();
        read.setVisible(true);
        read.openpdf(read.randomPdf());
    }//GEN-LAST:event_jT_newMouseClicked

    private void jT_RecomMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jT_RecomMouseClicked
        // TODO add your handling code here:
        Read read = new Read();
        read.setVisible(true);
        read.openpdf(read.randomPdf());
    }//GEN-LAST:event_jT_RecomMouseClicked

    private void jB_RatingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jB_RatingActionPerformed
        // TODO add your handling code here:
        dispose();
        DbConnect db = new DbConnect();
        String sql = "Select * from users Where Username='" + uname+"'";
        int aydi=0;
        try {
            db.rs = db.st.executeQuery(sql);
            db.rs.next();
            aydi = db.rs.getInt("ID");
        } catch (SQLException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        Rating rate = new Rating(aydi);
        rate.setVisible(true);
    }//GEN-LAST:event_jB_RatingActionPerformed

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
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Main("").setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton admin;
    private javax.swing.JLabel helloUsername;
    private javax.swing.JButton jB_Rating;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jL_Sayfa;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JTable jT_Books;
    private javax.swing.JTable jT_Recom;
    private javax.swing.JTextField jT_Sayfa;
    private javax.swing.JTable jT_new;
    private javax.swing.JTable jT_popular;
    private javax.swing.JTable jT_rated;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JButton j_Git;
    private javax.swing.JButton j_Onceki;
    private javax.swing.JButton j_Sonraki;
    // End of variables declaration//GEN-END:variables
}
