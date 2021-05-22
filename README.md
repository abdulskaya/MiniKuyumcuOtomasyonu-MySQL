# MiniKuyumcuOtomasyonu-MySQL 
Uygulama mini kuyumcu otomasyonu olarak açıklanabilir. Konsole üzerinden çalışmaktadır. MySQL veritabanını kullanmaktadır.
Tablolar:
  +urun_listesi
  +yapilan_satislar
  
Program ürünlerin listesini ve yapılan satışları anlık olarak veritabanımıza kaydetmektedir.
Satış yapıldığı esnada Europa/Istanbul saat dilimini esas alarak satışın yapıldığı tarihi ve saati de tabloya eklemektedir.

# Nesne Fonksiyonlarımız #

  Kuyumcu kuyumcu = new Kuyumcu("Altinzade","Mehmet Altinzade");
  kuyumcu.urunEkle("SafAltin",54);
	kuyumcu.urunCikart("TamAltin");
  kuyumcu.satisYap();
  kuyumcu.altinFiyati();
