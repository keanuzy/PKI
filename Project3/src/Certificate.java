

import java.nio.charset.StandardCharsets;
import java.security.spec.KeySpec;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

// For above imports to work, add bcp...jar (in my local project folder) as an external path or directly into git lib

/*
 EMU COSC 480/592
 PKI Project
 This class simulates the role of the certification authority and repository by instantiating
 and authenticating certificates.
 As of now, it uses X.509 version 1
 */
public class Certificate {

	private static final String ROOTPRIVATEKEY = "TopSecretRootPrivateKey";
	private static final String SALT = "ssshhhhhhhhhhh!!!!";
	// Member variables based on the elements of the X.509 version 1 certificates
	private String certNo; // Random int plus current date/time stamp
	private Integer version; // Default sets this to 1
	private Date startDate; // Default sets to time of instantiation
	private String endDate; // Default sets this to 12/31/2099
	private String user; 
	private String userAlg;
	private String userParams;
	private String userPublicKey;
	/*
	 * Add invalid boolean
	 */

	// Instantiate Random to use in constructors
	Random rand = new Random();

	public Certificate() { // Default constructor creates certificate without a user
		Integer r1 = rand.nextInt(1000000000);
		this.certNo = (r1.toString() + new Date()).replaceAll(" ","");
		this.version = 1;
		this.startDate = new Date();
		this.endDate = "2099-12-31";  // For now end date is a string
		this.user = "";
		this.userAlg = "";
		this.userParams = "";
		this.userPublicKey = encrypt(ROOTPRIVATEKEY + this.getUser());

	}
	// Constructor that takes user information
	public Certificate(String endDate, String user, String userAlg, String userParams) {
		Integer r1 = rand.nextInt(1000000000);
		this.certNo = (r1.toString() + new Date()).replaceAll(" ","");
		this.version = 1;
		this.startDate = new Date();
		this.endDate = endDate;
		this.user = user;
		this.userAlg = userAlg;
		this.userParams = userParams;
		this.userPublicKey = encrypt(ROOTPRIVATEKEY + this.getUser());
	}

	// For future use: when additional versions are included
	public Certificate(Integer version, String endDate, String user, String userAlg, String userParams) {
		Integer r1 = rand.nextInt(1000000000);
		this.certNo = (r1.toString() + new Date()).replaceAll(" ","");
		this.version = version;
		this.startDate = new Date();
		this.endDate = endDate;
		this.user = user;
		this.userAlg = userAlg;
		this.userParams = userParams;
		this.userPublicKey = encrypt(ROOTPRIVATEKEY + this.getUser());
	}

	public static String encrypt(String strToEncrypt) {
		try {

			// Create default byte array, which is 16 bytes( 128 bits );
			byte[] iv = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
			IvParameterSpec ivspec = new IvParameterSpec(iv);

			// Create SecretKeyFactory object
			SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");

			// Create KeySpec object and assign with
			// constructor
			KeySpec spec = new PBEKeySpec(ROOTPRIVATEKEY.toCharArray(), SALT.getBytes(), 65536, 256);
			SecretKey tmp = factory.generateSecret(spec);
			SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");

			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivspec);
			// Return encrypted string
			return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes(StandardCharsets.UTF_8)));
		} catch (Exception e) {
			System.out.println("Error while encrypting: " + e.toString());
		}
		return null;
	}

	public static String decrypt(String strToDecrypt) {
		try {

			// Default byte array
			byte[] iv = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
			// Create IvParameterSpec object and assign with
			// constructor
			IvParameterSpec ivspec = new IvParameterSpec(iv);

			// Create SecretKeyFactory Object
			SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");

			// Create KeySpec object and assign with
			// constructor
			KeySpec spec = new PBEKeySpec(ROOTPRIVATEKEY.toCharArray(), SALT.getBytes(), 65536, 256);
			SecretKey tmp = factory.generateSecret(spec);
			SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");

			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			cipher.init(Cipher.DECRYPT_MODE, secretKey, ivspec);
			// Return decrypted string
			return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
		} catch (Exception e) {
			System.out.println("Error while decrypting: " + e.toString());
		}
		return null;
	}

	public boolean validatePublicKey(String str) {
		if (decrypt(str).substring(ROOTPRIVATEKEY.length()).compareToIgnoreCase(ROOTPRIVATEKEY) == 0)
			return true;

		return false;
	}



	@Override
	public String toString() {
		return "Certificate: certNo=" + certNo + "\nversion=" + version + "\nstartDate=" + startDate + "\nendDate="
				+ endDate + "\nuser=" + user + "\nuserAlg=" + userAlg + "\nuserParams=" + userParams + "\nuserKey="
				+ userPublicKey + "\nValid Date: " + this.dateIsValid() + "\n*************************************************************";
	}

	/*
	Checks that certificate isn't expired by confirming that end date is >= today.
	For now, the date is a string. We can change it to a format in Date class if desired.
	 */
	public boolean dateIsValid() {
		SimpleDateFormat ymd = new SimpleDateFormat("yyyy-MM-dd");
		String currentYMD = new String(ymd.format(new Date())); 
		//		System.out.println("Printing YMD from dateIsValid: " + currentYMD);
		if (this.getEndDate().compareTo(currentYMD) >= 0) {
			return true;
		}
		System.out.println("Error: Expired certificate");
		return false;
	}

	/*
	 Method to check for "equality" of two certificates by confirming the same user, userKey.
	 This method is called from contains() in CertificateStore to check if a certificate
	 exists with the user and userKey parameters.
	 */
	public boolean userAndKeyMatch(String user, String userKey) {
		if (this.user.equalsIgnoreCase(user) && this.userPublicKey.equalsIgnoreCase(userKey))
			return true;
		else
			return false;
	}


	public String getCertNo() {
		return certNo;
	}
	public void setCertNo(String certNo) {
		this.certNo = certNo;
	}
	public Integer getVersion() {
		return version;
	}
	public void setVersion(Integer version) {
		this.version = version;
	}
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getUserAlg() {
		return userAlg;
	}
	public void setUserAlg(String userAlg) {
		this.userAlg = userAlg;
	}
	public String getUserParams() {
		return userParams;
	}
	public void setUserParams(String userParams) {
		this.userParams = userParams;
	}
	public String getUserPublicKey() {
		return userPublicKey;
	}
	
	public static void main(String[] args) {
		Certificate c = new Certificate("2021-12-31", "Brian", "DH", "These Params");
		System.out.println(c);
		System.out.println(c.encrypt("TopSecretRootPrivateKeyBrian"));
		System.out.println(c.decrypt(c.getUserPublicKey()));
	}


}