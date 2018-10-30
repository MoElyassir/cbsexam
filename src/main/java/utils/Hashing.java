package utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import org.bouncycastle.util.encoders.Hex;


public final class Hashing {




    // TODO: You should add a salt and make this secure
    // remove as md5 is not secure
    public static String md5(String rawString, byte[] salt) {
        try {

            // We load the hashing algoritm we wish to use.
            MessageDigest md = MessageDigest.getInstance("MD5");

            md.update(salt);

            // We convert to byte array
            byte[] byteArray = md.digest(rawString.getBytes());

            // Initialize a string buffer
            StringBuffer sb = new StringBuffer();

            // Run through byteArray one element at a time and append the value to our stringBuffer
            for (int i = 0; i < byteArray.length; ++i) {
                sb.append(Integer.toHexString((byteArray[i] & 0xFF) | 0x100).substring(1, 3));
            }

            //Convert back to a single string and return
            return sb.toString();

        } catch (java.security.NoSuchAlgorithmException e) {

            //If somethings breaks
            System.out.println("Could not hash string");
        }

        return null;
    }


    // TODO: You should add a salt and make this secure
    /*public static String sha(String rawString, byte salt) {
        try {
            // We load the hashing algoritm we wish to use.
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            digest.update(salt);

            return digest.digest();
            // We convert to byte array
            byte[] hash = digest.digest(rawString.getBytes(StandardCharsets.UTF_8));

            StringBuffer sha256hex = new StringBuffer();

            for (int i = 0; i < hash.length; i++){
                String sbHash = Integer.toHexString( hash[i] & 0xff);
                if(sbHash.length() == '1') sha256hex.append('0');
                sha256hex.append(sbHash);
            }

            // And return the string
            return sha256hex.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return rawString;
    }*/
    public static String sha(String rawString) {
        try {
            // We load the hashing algoritm we wish to use.
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            rawString = rawString + "oiehrgoiherg";

            // We convert to byte array
            byte[] hash = digest.digest(rawString.getBytes(StandardCharsets.UTF_8));

            // We create the hashed string
            String sha256hex = new String(Hex.encode(hash));

            // And return the string
            return sha256hex;

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return rawString;
    }

    private static byte[] getSalt() throws NoSuchAlgorithmException, NoSuchProviderException {

        SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");

        byte[] salt = new byte[17];

        random.nextBytes(salt);

        salt.toString();

        return salt;
    }

}