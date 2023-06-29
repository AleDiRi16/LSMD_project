package it.unipi.lsmsd.util;

import org.apache.commons.codec.digest.DigestUtils;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.List;


@Component
public class Password {
    private static final Logger LOGGER = LoggerFactory.getLogger(Password.class);

    private static final String salt_char = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final int salt_size = 8;

    private static String password_hash;
    private static String salt;
    public Password(){}
    public Password (String password_hash, String salt) {
        this.password_hash = password_hash;
        this.salt = salt;
    }

    public Password (String password) {
        SecureRandom random = new SecureRandom();
        byte[] Salt = new byte[8];
        random.nextBytes(Salt);
        salt=Salt.toString();
        password_hash = DigestUtils.sha1Hex(password + Salt);
        System.out.println(salt);
    }

    public String getPassword_hash() {
        return password_hash;
    }

    public String getSalt() {
        return salt;
    }

    public boolean checkPassword(String password) {
        boolean compare = DigestUtils.sha1Hex(password + salt).equals(password_hash);
        if (!compare) {
            LOGGER.error(" Password doesn't match");
        }
        return compare;
    }

    public Password fromDocument ( Document document) {
        return new Password(
                document.getEmbedded(List.of("credential", "sha1"), String.class),
                document.getEmbedded(List.of("credential", "salt"), String.class)
        );
    }

    public Document toDocument () {
        return new Document()

                .append("salt", salt)
                .append("sha1", password_hash);
    }

    @Override
    public String toString() {
        return "Password{" +
                "password_hash='" + password_hash + '\'' +
                ", salt='" + salt + '\'' +
                '}';
    }
}