

import java.util.ArrayList;

/*
 * This isn't a test class, per se, in that it doesn't perform unit tests.
 * It creates instances of Certificates and adds them to a CertificateStore instance.
 * Then it confirms that the contains() method works for user, key, and expiration.
*/
public class TestCertificates {
	
	public static void main(String[] args) {
		CertificateStore cs = new CertificateStore();
		cs.createStore();
//		System.out.println(cs.getCerts());

//		Print all Certificates in certStore
		System.out.println("\n**** Printing all Certificates ****\n");
		for (Certificate c : cs.getCerts())
			System.out.println(c);
		
		String u1 = "Brian";
		String k1 = "privateKey";
		String u2 = "Brain";
		String k2 = "privateKey";
		String u3 = "Brian";
		String k3 = "passwerd";
		
		System.out.println("User " + u1 + " with key " + k1 + " (true for valid; false for invalid):");
		System.out.println(cs.contains(u1, k1));
		System.out.println("User " + u2 + " with key " + k2 + " (true for valid; false for invalid):");
		System.out.println(cs.contains(u2, k2));
		System.out.println("User " + u3 + " with key " + k3 + " (true for valid; false for invalid):");
		System.out.println(cs.contains(u3, k3));
		
	}

}
