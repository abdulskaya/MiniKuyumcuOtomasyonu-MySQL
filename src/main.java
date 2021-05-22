
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Scanner;

class Kuyumcu{
	
	private String dukkanAdi;
	private String dukkanSahibi;
	private String urunListesi[] = new String[50];
	private Double urunFiyatListesi[] = new Double[50];
	private double kasa;
	private double tamAltin;
	private double ceyrekAltin;
	private double gramAltin;
	private double cumhuriyetAltini;
	private static Connection dbConnect;
	private double kur = 8.42;
	
	public Kuyumcu(String dukkanAdi, String dukkanSahibi) throws Exception {
		this.dukkanAdi = dukkanAdi;
		this.dukkanSahibi = dukkanAdi;
		this.kasa = 500;
		DBConnections();
		urunleriDuzenle();
	}
	

	public void setDukkanAdi(String dukkanAdi){
		this.dukkanAdi = dukkanAdi;
	}
	
	public String getDukkanAdi() {
		return this.dukkanAdi;
	}
	
	public void setDukkanSahibi(String dukkanSahibi) {
		this.dukkanSahibi = dukkanSahibi;
	}
	
	public String getDukkanSahibi() {
		return this.dukkanSahibi;
	}
	
	public void	 urunleriListele() {
		System.out.println("---- URUN LISTESI ----");
		for(int i = 0 ; i < urunListesi.length ; i++) {
			if(urunListesi[i] != null) System.out.println(this.urunListesi[i] + " - " + this.urunFiyatListesi[i] + "TL");
			else break;
		}
	}
	
	public double getKasa() {
		return this.kasa;
	}
	
	private void urunleriDuzenle() throws Exception {
		
		
		for (int i = 0; i < urunListesi.length; i++) {
			this.urunListesi[i] = null;
		}
		
		for (int i = 0; i < urunFiyatListesi.length; i++) {
			this.urunFiyatListesi[i] = null;
		}
		
		String baslangicUrunleri[] = {"TamAltin","CumhuriyetAltini","YarimAltin","CeyrekAltin","GramAltin","Yuzuk","Alyans","Bilezik","Gumus","Bileklik","Gerdanlik"};
		double baslangicUrunFiyatlari[] = {kur*394.29,kur*380.28,kur*190.14,kur*95.60,kur*59.97,kur*219.71,kur*279.09,kur*356.29,kur*1.2,kur*73.63,kur*132.42};
		
		for (int i = 0; i < baslangicUrunleri.length; i++) {
			this.urunListesi[i] = baslangicUrunleri[i];
			this.urunFiyatListesi[i] = baslangicUrunFiyatlari[i];
			main.ekleUrunListesiTable(dbConnect, baslangicUrunleri[i], baslangicUrunFiyatlari[i]);
		}
		
		this.tamAltin = this.urunFiyatListesi[0];
		this.ceyrekAltin = this.urunFiyatListesi[3];
		this.gramAltin = this.urunFiyatListesi[2];
		this.cumhuriyetAltini = this.urunFiyatListesi[1];
		
	}
	
	private int urunListesiSay() {
		int urunAdeti = 0;
		for (int i = 0; i < urunListesi.length; i++) {
			if(this.urunListesi[i] != null) {
				++urunAdeti;
			}else {
				break;
			}
		}
		return urunAdeti;
	}
	
	private int urunFiyatListesiSay() {
		int urunFiyatAdeti = 0;
		for (int i = 0; i < urunListesi.length; i++) {
			if(this.urunListesi[i] != null) {
				++urunFiyatAdeti;
			}else {
				break;
			}
		}
		return urunFiyatAdeti;
	}
	
	public void urunEkle(String urunAdi, double urunFiyati) throws Exception {
		this.urunListesi[this.urunListesiSay()] = urunAdi;
		this.urunFiyatListesi[this.urunFiyatListesiSay()-1] = urunFiyati;
		
		main.ekleUrunListesiTable(dbConnect, urunAdi, urunFiyati);
		
		System.out.println(urunAdi + " eklendi. Yeni urun listesi:");
		
		this.urunleriListele();
		
	}
	
	public void altinFiyati() {
		System.out.println("Tam altin fiyati: " + this.tamAltin);
	}
	
	public void urunCikart(String urunAdi) throws Exception {
		boolean listedeVarMi = false;
		for (int i = 0; i < urunFiyatListesi.length; i++) {
			if(this.urunListesi[i] == urunAdi) {
				main.cikartUrunListesiTable(dbConnect, urunAdi, this.urunFiyatListesi[i]);
				this.urunListesi[i] = null;
				this.urunFiyatListesi[i] = null;
				listedeVarMi = true;
				break;
			}
		}
		
		if(listedeVarMi) {
			for (int i = 0; i < urunListesi.length-1; i++) {
				if(this.urunListesi[i] == null && this.urunListesi[i+1] != null ) {
					for (int j = i; j < urunFiyatListesi.length-1; j++) {
						this.urunListesi[j] = this.urunListesi[j+1];   
					}
				}
			}
			System.out.println();
			System.out.println(urunAdi + " cikarildi. Yeni urun listesi:\n");
			this.urunleriListele();
			
		}else System.out.println("Girdiginiz urun satis listesinde bulunamadi!");
		
	}
	
	public void satisYap() throws Exception {
		Scanner sc = new Scanner(System.in);	
		String tarih,saat;
		ZoneId istanbul = ZoneId.of("Europe/Istanbul"); //bolgemizin saat bilgilerini aktardik
		
		while(true) {
			System.out.println("\n*****" + this.getDukkanAdi() + "Kuyumculuk, hosgeldiniz... *****");
			this.urunleriListele();
			System.out.println("*Lutfen almak istediginiz urunun adini giriniz(harf uyumuna dikkat ediniz):");
			System.out.println("*Alisverisi sonlandirmak icin \"gulegule\" yaziniz.\n");
			String alinacakurun = sc.nextLine();
			 
			boolean varMi = false;
			
			if(!alinacakurun.equals("gulegule")) {
				for (int i = 0; i < urunListesi.length; i++) {
					if(alinacakurun.equals(this.urunListesi[i])) {
						System.out.println(alinacakurun + " satin aldiniz. Urun fiyati " + urunFiyatListesi[i]);
						this.kasa += urunFiyatListesi[i];
						System.out.println("(Kuyumcu Bakiyesi: " + this.getKasa() + ")");
						saat = LocalTime.now(istanbul).toString();
						tarih = LocalDate.now(istanbul).toString();
						main.ekleYapilanSatislarTable(dbConnect, alinacakurun, urunFiyatListesi[i], tarih, saat);
						varMi = true;
						break;
					}else varMi = false;
				}
				if(!varMi) {
					System.out.println("Hatali giris yapildi, lutfen tekrar deneyiniz.");
					continue;
				}
			}else if(alinacakurun.equals("gulegule")) { 
				System.out.println("\nIyi gunler dileriz :)");
				break;
			}
			
		}
	}
	
	public static void DBConnections() throws Exception{
		//Veritabani bilgileri
		String url = "jdbc:mysql://localhost:3306/kuyumcu";
		String username = "root";
		String password = "";
		
		//database baglantimizi yapiyoruz
		dbConnect = main.connectDB(url, username, password);
		
		
		//Baglantimizi kullanarak veritabanimiza tablo olusturuyoruz
		main.createTables(dbConnect);
	}
	
}

public class main {
	public final static boolean createTables(Connection conn) throws Exception{
		
		Statement statement = conn.createStatement();
		try {
			PreparedStatement create;
			create = conn.prepareStatement("CREATE TABLE IF NOT EXISTS urun_listesi(id int NOT NULL AUTO_INCREMENT ,urun_adi varchar(255), urun_fiyati int,  PRIMARY KEY(id))");
			System.out.println("urun_listesi tablosu olusturuldu!");
			create.executeUpdate();
			statement.executeUpdate("TRUNCATE urun_listesi");
			create = conn.prepareStatement("CREATE TABLE IF NOT EXISTS yapilan_satislar(id int NOT NULL AUTO_INCREMENT ,urun_adi varchar(255), urun_fiyati int, satildigi_tarih varchar(255), satildigi_saat varchar(255), PRIMARY KEY(id))");
			System.out.println("yapilan_satislar tablosu olusturuldu!");
			create.executeUpdate();

			return true;
			
		}catch(Exception e) {
			System.out.println("*******************Tablolar olusturulurken bir hata olustu!");
			e.printStackTrace();
			return false;
		}

	}
	
	public final static boolean ekleUrunListesiTable(Connection conn,String urunAdi, double urunFiyati) throws Exception{
		
		try {
			
			PreparedStatement create = conn.prepareStatement("INSERT INTO urun_listesi (urun_adi, urun_fiyati) VALUES ('" + urunAdi+"', '"+ urunFiyati+"')");
			create.executeUpdate();
			return true;
			
		}catch(Exception e) {
			System.out.println("*******************Tablolar guncellenirken bir hata olustu!");
			e.printStackTrace();
			return false;
		}

	}
	
	public final static boolean ekleYapilanSatislarTable(Connection conn,String urunAdi, double urunFiyati, String tarih, String saat) throws Exception{
		
		try {
			
			PreparedStatement create = conn.prepareStatement("INSERT INTO yapilan_satislar (urun_adi, urun_fiyati, satildigi_tarih,	satildigi_saat) VALUES ('" + urunAdi+"', '"+ urunFiyati+"','"+ tarih +"','"+ saat +"')");
			create.executeUpdate();
			return true;
			
		}catch(Exception e) {
			System.out.println("*******************Tablolar guncellenirken bir hata olustu!");
			e.printStackTrace();
			return false;
		}

	}

	
	public final static boolean cikartUrunListesiTable(Connection conn,String urunAdi, double urunFiyati) throws Exception{
		PreparedStatement create;
		try {
			
			create = conn.prepareStatement("DELETE FROM urun_listesi WHERE urun_adi = '" + urunAdi + "'");
			create.executeUpdate();
		
			return true;
			
		}catch(Exception e) {
			System.out.println("*******************Kayit silinirken bir hata olustu!");
			e.printStackTrace();
			return false;
		}
	
	}
	
	public final static Connection connectDB(String url, String username, String password) throws Exception {
		
		Connection dbConnection = null;
		
		try {
			dbConnection = DriverManager.getConnection(url, username, password);
		    System.out.println("DB connected.");
		}catch(Exception e) {
			e.printStackTrace();
			System.out.println("DB connection is failed!");
		}
		
		return dbConnection;
	}
	
	public static void main(String[] args)  {
		
		try {
		
			Kuyumcu kuyumcu = new Kuyumcu("Altinzade","Mehmet Altinzade");
			
			kuyumcu.urunCikart("TamAltin");
			}catch(Exception e){
				e.printStackTrace();
		}
		
	}
}
