package Backend;


import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class Encryptor {

    //Advanced Encryption Standard
    //128 bit
    static byte[] IV = { 0x01, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f };
    static byte[] encryptionKey = {65, 12, 12, 12, 12, 12, 12, 12, 12,
            12, 12, 12, 12, 12, 12, 12 };

    public static String encrypt(String input){
        try{
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            SecretKeySpec key = new SecretKeySpec(encryptionKey,"AES");
            cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(IV));
            byte[] cipherText = cipher.doFinal(input.getBytes());
            return Base64.getEncoder()
                    .encodeToString(cipherText);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }


//    public static String decrypt(String cipherText){
//        try{
//            System.out.println("Trying to decrypt a string of length "+cipherText.length());
//            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
//            SecretKeySpec key = new SecretKeySpec(encryptionKey,"AES");
//            cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(IV));
//            byte[] plainText = cipher.doFinal(Base64.getDecoder()
//                    .decode(cipherText));
//            return new String(plainText);
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//        return null;
//    }
public static String decrypt(String cipherText) {
    try {
//        System.out.println("Trying to decrypt a string of length " + cipherText.length());
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SecretKeySpec key = new SecretKeySpec(encryptionKey, "AES");
        cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(IV));

        // Decode Base64 string back to byte array (might have padding)
        byte[] decodedBytes = Base64.getDecoder().decode(cipherText);

        // Handle potential padding issue (explained in point 2)
        byte[] cipherTextBytes = handlePadding(decodedBytes);

        byte[] plainText = cipher.doFinal(cipherTextBytes);
        return new String(plainText);
    } catch (Exception e) {
        e.printStackTrace();
    }
    return null;
}
    private static byte[] handlePadding(byte[] decodedBytes) {
        int length = decodedBytes.length;
        if (length % 16 == 0) {
            return decodedBytes; // No padding
        }
        // Assuming the padding is all zeros (common case)
        int newLength = length - (length % 16);
        byte[] cipherTextBytes = new byte[newLength];
        System.arraycopy(decodedBytes, 0, cipherTextBytes, 0, newLength);
        return cipherTextBytes;
    }


    public static byte[] stringToByteArray(String keyString){
        String[] keyFragments = keyString.split(" ");

        byte[] key = new byte[16];
        for (int i = 0; i < keyFragments.length; i++) {
            key[i] = Byte.parseByte(keyFragments[i]);
        }
        return key;
    }
}