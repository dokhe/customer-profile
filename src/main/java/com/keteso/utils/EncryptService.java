package com.keteso.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Service
@Slf4j
public class EncryptService {

    Cipher encryptCipher;
    Cipher decryptCipher;
    @Value("${app.security.pass-encrypt-key}")
    String key;
    @Value("${app.security.init-vector}")
    String initVector;

    public void initialize()
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException {
        IvParameterSpec iv = new IvParameterSpec(initVector.getBytes(StandardCharsets.UTF_8));
        SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");

        encryptCipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        encryptCipher.init(Cipher.ENCRYPT_MODE, keySpec, iv);

        decryptCipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        decryptCipher.init(Cipher.DECRYPT_MODE, keySpec, iv);
    }

    public String encrypt(String value) throws Exception {
        initialize();
        byte[] encrypted = encryptCipher.doFinal(value.getBytes(StandardCharsets.UTF_8));
        return new String(Base64.encodeBase64(encrypted), StandardCharsets.UTF_8);
    }

    public String decrypt(String value) throws Exception {
        initialize();
        byte[] encrypted = Base64.decodeBase64(value.getBytes(StandardCharsets.UTF_8));
        byte[] decryptedBytes = decryptCipher.doFinal(encrypted);
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }

}
