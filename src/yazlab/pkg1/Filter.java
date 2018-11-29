/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package yazlab.pkg1;

import static java.lang.Math.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ffeki
 */
public class Filter {
     double hpuanpay = 0;
        double hpuan = 0;
        double hpuanpayda = 0;

    double matris[][]; // 0. index kullanıcı id si 1. index benzerlik oranı 2. index kullanıcının oy verdiği kitapların ortalaması
    int ID = 8;
    String [] sonuc;

//    public static void main(String args[]) {
//        Filter fl = new Filter();
//        fl.benzerlikorani();
//        for (int i = 0; i < sonuc.length; i++) {
//            System.out.println(sonuc[i]);
//            
//        }
//        
//
//    }
    //

    class Book {

        String isbn;
        int rate;

        public Book(String isbn, int rate) {

            this.isbn = isbn;
            this.rate = rate;

        }
    }

    public int search(int id) {
        int i;
        for (i = 0; i < matris.length; i++) {
            if (matris[i][0] == (double) id) {
                break;
            }

        }
        return i;
    }

    public void benzerlikorani() {
        double kitaprank[];
        String kitapisbn[];
        int komsuluk[];
        Map<String, Integer> kitaplar = new HashMap<>();
        //Map<Integer, Double> ratio = new HashMap<Integer, Double>();
        Map<String, Integer> map1 = new HashMap<>();    // giriş yapan kullanıcının kitap isbn leri ve puanları
        Map<Integer, HashMap<String, Integer>> map2 = new HashMap<Integer, HashMap<String, Integer>>(); //ortak kitap beğenen db deki kullanıcıların idleri - katap isbnleri ve puanları
        Map<Integer, HashMap<String, Integer>> mOther = new HashMap<Integer, HashMap<String, Integer>>();
        Map<Integer, HashMap<String, Integer>> komsukitaplar = new HashMap<Integer, HashMap<String, Integer>>();

        DbConnect db = new DbConnect();
        String sql = "SELECT * from ratings WHERE ID=" + ID;

        try {
            int i = 0;
            db.rs = db.st.executeQuery(sql);
            while (db.rs.next()) {
                map1.put(db.rs.getString("ISBN"), db.rs.getInt("Rating"));
            }
        } catch (Exception e) {
        }

        DbConnect db2 = new DbConnect();
        String sql2 = "SELECT * from ratings WHERE ISBN= '" + map1.keySet().toArray()[0] + "'";
        for (int i = 1; i < map1.size(); i++) {
            sql2 += "OR ISBN = '" + map1.keySet().toArray()[i] + "' ";

        }

        try {
            db2.rs = db2.st.executeQuery(sql2);

            while (db2.rs.next()) {
                if (!map2.containsKey(db2.rs.getInt("ID"))) {
                    map2.put(db2.rs.getInt("ID"), new HashMap<>());
                }
                map2.get(db2.rs.getInt("ID")).put(db2.rs.getString("ISBN"), db2.rs.getInt("Rating"));
            }
        } catch (Exception e) {
        }

        System.out.println(map1.toString());
        System.out.println(map2.toString());

        Set set = map2.entrySet();
        Iterator i = set.iterator();

        // Display elements
        int k = 0;
        matris = new double[map2.size()][3];
        while (i.hasNext()) {
            int t1 = 0;
            int t2 = 0;
            int tpay = 0;
            double tpayda = 0;
            Map.Entry me = (Map.Entry) i.next();
            //System.out.println((Integer) me.getKey());

            for (int j = 0; j < map2.get(me.getKey()).size(); j++) {
                if (map1.containsKey(map2.get(me.getKey()).keySet().toArray()[j])) {
                    int otheruserpoint = map2.get(me.getKey()).get(map2.get(me.getKey()).keySet().toArray()[j]);
                    int mypoint = map1.get(map2.get(me.getKey()).keySet().toArray()[j]);
                    // System.out.print(map2.get(me.getKey()).get(map2.get(me.getKey()).keySet().toArray()[j]) + "  " + j);
                    tpay += otheruserpoint * mypoint;
                    t1 += pow(mypoint, 2);
                    t2 += pow(otheruserpoint, 2);

//                    System.out.println(map2.get(me.getKey()).values().toString().);
                }

            }
            tpayda = sqrt(t1) * sqrt(t2);
            double sonuc = tpay / tpayda;
            if (tpayda == 0) {
                sonuc = 0;
            }
            //System.out.println(sonuc);
            int x = (int) me.getKey();
            matris[k][0] = (double) x;
            matris[k++][1] = sonuc;
        }

        DbConnect db3 = new DbConnect();
        DbConnect db4 = new DbConnect();

        try {
            double tmp = 0;

            Set set3 = map2.entrySet();
            Iterator iterator3 = set3.iterator();

            while (iterator3.hasNext()) {
                Map.Entry mentry = (Map.Entry) iterator3.next();
                String sql3 = "SELECT * FROM ratings WHERE ID=" + mentry.getKey();

                db3.rs = db.st.executeQuery(sql3);

                int mId = Integer.parseInt(mentry.getKey().toString());
                mOther.put(mId, new HashMap<>());

                while (db3.rs.next()) {
                    mOther.get(mentry.getKey()).put(db3.rs.getString("ISBN"), db3.rs.getInt("Rating"));
                    //System.out.println("/*" + db3.rs.getString("ISBN") + " - " + db3.rs.getInt("Rating"));
                }
                String sql4 = "SELECT  AVG(Rating) as avg FROM ratings WHERE ID=" + mentry.getKey();
                db4.rs = db.st.executeQuery(sql4);
                while (db4.rs.next()) {
                    tmp = db4.rs.getDouble("avg");

                }
                matris[search(mId)][2] = tmp;
//                System.out.println(search(mId)+" * "+matris[search(mId)][2]);
            }

        } catch (SQLException ex) {
            Logger.getLogger(Filter.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println(mOther.toString());
        int totalsize = 0;

        Set sett = mOther.entrySet();
        Iterator iteratorr = sett.iterator();
        while (iteratorr.hasNext()) {
            Map.Entry iter = (Map.Entry) iteratorr.next();
            totalsize += mOther.get(iter.getKey()).size();
        }
        //System.out.println(totalsize);  // toplam kitap sayısı test

        // System.out.println(mOther.toString()+"\n"+mOther.size());
        int sayac = 0;
        int sayac2 = 0;
        int sayac3 = 0;
        komsuluk = new int[matris.length];
        for (int j = 0; j < matris.length; j++) {

            if (matris[j][1] > 0.9) {
                komsuluk[sayac2] = (int) matris[j][0];
                sayac2++;
                //System.out.println("///" + mOther.get((int) matris[j][0]).size() + " " + j); // her bir id için kayıtlı kitap sayısı testi
                for (int l = 0; l < mOther.get((int) matris[j][0]).size(); l++) {
                    //System.out.println(mOther.get((int)matris[j][0]).keySet().toArray()[0].toString());
                    kitaplar.put(mOther.get((int) matris[j][0]).keySet().toArray()[l].toString(), 0);
                    sayac3++;
                }
            }
        }

        System.out.println(sayac2 + " " + sayac3 + "  " + kitaplar.toString());

//            for (int l = 0; l < 500; l++) {
//                System.out.println(matris[l][0]+" - "+matris[l][1]+" - "+matris[l][2]);
//            }
        String hdisbn[] = new String[sayac3];
        int hdid[] = new int[sayac3];
        int hdrate[] = new int[sayac3];
        DbConnect db5 = new DbConnect();
        int sayac4 = 0;

        for (int j = 0; j < sayac2; j++) {
            String sql5 = "SELECT * from ratings WHERE ID= " + komsuluk[j];
            try {
                db5.rs = db5.st.executeQuery(sql5);

                while (db5.rs.next()) {
                    if (!komsukitaplar.containsKey(db5.rs.getInt("ID"))) {
                        komsukitaplar.put(db5.rs.getInt("ID"), new HashMap<>());
                    }
                    komsukitaplar.get(db5.rs.getInt("ID")).put(db5.rs.getString("ISBN"), db5.rs.getInt("Rating"));
                    hdisbn[sayac4] = db5.rs.getString("ISBN");
                    hdid[sayac4] = db5.rs.getInt("ID");
                    hdrate[sayac4] = db5.rs.getInt("Rating");
                    sayac4++;
                }
            } catch (Exception e) {
            }
        }

        System.out.println(komsukitaplar.toString());
        //System.out.println(komsukitaplar.values().toArray()[0]+"  "+komsukitaplar.values().toArray()[1]+"  "+komsukitaplar.values().toArray()[2]+"  "+komsukitaplar.keySet().toArray()[1]+" "+komsukitaplar.keySet().toArray()[2]);
       
        kitaprank = new double[kitaplar.size()];
        kitapisbn = new String[kitaplar.size()];
//        for (int j = 0; j < kitaplar.size(); j++) {
//            for (int l = 0; l < hdisbn.length; l++) {
//                //System.out.println(kitaplar.keySet().toArray()[j].toString() + "  " + hdisbn[l]);
//                if (kitaplar.keySet().toArray()[j].toString().equalsIgnoreCase(hdisbn[l])) {
//                    int id = hdid[l];
//                    double rank = hdrate[l];
//                    hpuanpay += (rank - opuan(matris, id)) * borani(matris, id);
//                    hpuanpayda += borani(matris, id);
//                }
//                hpuan = hpuanpay / hpuanpayda;
//
//            }
//            System.out.println(kitaplar.keySet().toArray()[j].toString() + "   " + hpuan);
//            kitaprank[j] = hpuan;
//            kitapisbn[j] = kitaplar.keySet().toArray()[j].toString();
//
//        }
        //sirala(kitaprank, kitapisbn);
        for (int j = 0; j < kitaplar.size(); j++) {
            Object isbn2=kitaplar.keySet().toArray()[j];
            
             kitaprank[j] = hpuan;
             kitapisbn[j] = kitaplar.keySet().toArray()[j].toString();
            komsukitaplar.forEach((id, innerMap) -> innerMap.forEach((isbn, rate) -> {
                if (isbn.equals(isbn2)) {
                    double tpay=toplapay(hpuanpay, rate, opuan(matris, id), borani(matris, id));
                    double tpayda=toplapayda(hpuanpayda,borani(matris, id));
                    double ttoplam=topla(hpuan,hpuanpay,hpuanpayda);
                    
                    
                    
                };                
            }));
            
            topla(hpuan,hpuanpay,hpuanpayda);
        }
        sirala(kitaprank, kitapisbn);
        
        sonuc = kitapisbn.clone();

    }

    public double topla(double toplam ,double pay,double payda){
        toplam=pay/payda;
        hpuan=opuan(matris, ID)+(hpuanpay/hpuanpayda);
        return toplam;
    }
    public double toplapayda(double payda,double boran){
        payda+=boran;
        hpuanpayda=payda;
        return payda;
    }
    public double toplapay(double pay,int rate,double opuan,double borani){
        pay+=(rate-opuan)*borani;
        hpuanpay=pay;
        return pay;
    }
    public void sirala(double dizi[], String dizi2[]) {
        double temp = 0;
        String stemp;
        for (int i = 0; i < dizi.length - 1; i++) {
            for (int j = 0; j < dizi.length - 1; j++) {
                if (dizi[j] < dizi[j + 1]) {
                    temp = dizi[j];
                    dizi[j] = dizi[j + 1];
                    dizi[j + 1] = temp;

                    stemp = dizi2[j];
                    dizi2[j] = dizi2[j + 1];
                    dizi2[j + 1] = stemp;
                }
            }
        }
        for (int i = 0; i < dizi.length; i++) {
            System.out.println("kitabın adı: " + dizi2[i] + " puanı: " + dizi[i]);
        }
    }

    public double opuan(double matris[][], int id) {
        for (int i = 0; i < matris.length; i++) {
            if (matris[i][0] == id) {
                return matris[i][2];
            }
        }
        return 0;
    }

    public double borani(double matris[][], int id) {
        for (int i = 0; i < matris.length; i++) {
            if (matris[i][0] == id) {
                return matris[i][1];
            }
        }
        return 0;
    }

    public void benzerlikorani2() {
        int matris[][] = new int[500][2];
        Map<Integer, Double> ratio = new HashMap<Integer, Double>();
        Map<String, Integer> map1 = new HashMap<>();
        Map<Integer, String> map2 = new HashMap<Integer, String>();

        DbConnect db = new DbConnect();
        String sql = "SELECT * from ratings WHERE ID=" + ID;

        try {
            int i = 0;
            db.rs = db.st.executeQuery(sql);
            while (db.rs.next()) {
                map1.put(db.rs.getString("ISBN"), db.rs.getInt("Rating"));
            }
        } catch (Exception e) {
        }

        DbConnect db2 = new DbConnect();
        String sql2 = "SELECT * from ratings WHERE ISBN= '" + map1.keySet().toArray()[0] + "'";
        for (int i = 1; i < map1.size(); i++) {
            sql2 += "OR ISBN = '" + map1.keySet().toArray()[i] + "' ";

        }

        try {
            db2.rs = db2.st.executeQuery(sql2);
            int i = 0;
            while (db2.rs.next()) {

                map2.put(db2.rs.getInt("ID"), db2.rs.getString("ISBN") + "" + db2.rs.getInt("Rating"));
            }
        } catch (Exception e) {
        }

        System.out.println(map1.toString());
        System.out.println(map2.toString());

        Set set = map2.entrySet();
        Iterator i = set.iterator();

        // Display elements
        int k = 0;
        while (i.hasNext()) {
            Map.Entry me = (Map.Entry) i.next();
            //System.out.println((Integer) me.getKey());

//                for (int j = 0; j < map2.get(me.getKey()).size(); j++) {
//                //System.out.println(map2.get(me.getKey()).keySet().toArray()[5]);
//                    //System.out.println(map2.get(me.getKey()).keySet().toArray()[j]);
//                if(map1.containsKey(map2.get(me.getKey()).keySet().toArray()[j])){
//                    int a = map1.get(map2.get(me.getKey()).keySet().toArray()[j]);
//                    Collection<Integer> b = map2.get(me.getKey()).values();
//
//                    System.out.println(map2.get(me.getKey()).values().toString().);
//                }
//            }
//         System.out.print(me.getKey() );
//         System.out.println(me.getValue());
            //ratio.put(k, 0.0);
//        int k=1;
//        System.out.println(map1.get(8).keySet().toArray()[1]);
            //Creating Books    
            //Adding Books to map   
            //Traversing map  
        }

    }
}
