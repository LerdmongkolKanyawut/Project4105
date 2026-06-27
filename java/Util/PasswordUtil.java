package util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * PasswordUtil – hash / verify รหัสผ่านด้วย SHA-256 + Salt
 * (หากต้องการความปลอดภัยสูงขึ้น แนะนำใช้ BCrypt จาก library เพิ่มเติม)
 */
public class PasswordUtil {

    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * สร้าง salt แบบสุ่ม 16 bytes → Base64
     */
    public static String generateSalt() {
        byte[] salt = new byte[16];
        RANDOM.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    /**
     * Hash รหัสผ่านพร้อม salt รูปแบบที่เก็บใน DB: salt$hash
     */
    public static String hashPassword(String plainPassword) {
        String salt = generateSalt();
        String hash = sha256(salt + plainPassword);
        return salt + "$" + hash;
    }

    /**
     * ตรวจสอบรหัสผ่าน
     *
     * @param plainPassword รหัสผ่านที่ผู้ใช้กรอก
     * @param storedPassword ค่าที่เก็บใน DB (salt$hash)
     */
    public static boolean verifyPassword(String plainPassword, String storedPassword) {
        if (storedPassword == null || !storedPassword.contains("$")) {
            return false;
        }
        String[] parts = storedPassword.split("\\$", 2);
        String salt = parts[0];
        String expectedHash = parts[1];
        String actualHash = sha256(salt + plainPassword);
        return actualHash.equals(expectedHash);
    }

    private static String sha256(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(input.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
