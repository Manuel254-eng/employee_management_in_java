import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.util.Base64;

public class PasswordUtils {

    // Hash a password using PBKDF2 algorithm
    public static String hashPassword(String password) {
        byte[] salt = generateSalt();
        byte[] hash = hashPasswordWithSalt(password, salt);
        return Base64.getEncoder().encodeToString(salt) + ":" + Base64.getEncoder().encodeToString(hash);
    }

    // Verify a password against a stored hash
    public static boolean verifyPassword(String password, String storedHash) {
        String[] parts = storedHash.split(":");
        byte[] salt = Base64.getDecoder().decode(parts[0]);
        byte[] storedHashBytes = Base64.getDecoder().decode(parts[1]);
        byte[] hash = hashPasswordWithSalt(password, salt);
        return java.util.Arrays.equals(storedHashBytes, hash);
    }

    // Generate a random salt
    private static byte[] generateSalt() {
        byte[] salt = new byte[16];
        new java.security.SecureRandom().nextBytes(salt);
        return salt;
    }

    // Hash the password with the given salt
    private static byte[] hashPasswordWithSalt(String password, byte[] salt) {
        try {
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 512);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
            return factory.generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }
}
