package com.victorlamp.matrixiot.service.agent.utils.script;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Security;

//import com.huaxu.javax.crypto.Cipher;
//import com.huaxu.javax.crypto.spec.SecretKeySpec;

/**
 * AES 加密 ECB 模式 PKCS7Padding 填充模式
 *
 * @author
 */
public class AESECBPKCS7Padding2 {

    static String ENCRYPT_CHARSET = "UTF-8";
    static String mode = "AES/ECB/PKCS5Padding";

    //add new bouncycastle ciphers
       /*static {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
     }*/

    public void main() throws Exception {

        // encryption key should be multiple of 16 character long
        String key = "6543210987654321";
        String data = "005D02002B00000AE06001000000000200000000031907231616050400000500000000000006017C07000008FFF20900040004000001000500180000010000000000000000000200000000000000000003000600080000000000001E00";

        //String encrypted = AESECBPKCS7Padding2.encrypt(data, key);
        // System.out.println("加密后数据: "  + encrypted);
        AESECBPKCS7Padding2 dece = new AESECBPKCS7Padding2();
        String decrypted = dece.decrypt("06BB226C39A7A691D8FB53AD7DF9DEABEB40B926DCAF45975EE6910FB1E1F5002F5CF7826F5EE0539AD53C2D37BF7E08F4A248FA32E5974F4462E5F17E8DFF463B99264D36E1CC6D4775E2ADFD1D0C98C08DAE68B501E121ED282B4337B0018CF64F0A3C2281CB7468F289072F154BB58CF67AA135AD1156D4E5AE759DE1909F", key);
        System.out.println("解密后数据: " + decrypted);
    }

    /**
     * * 将二进制转换成16进制 *
     * * @param buf *
     *
     * @return
     */
    public String parseByte2HexStr(byte buf[]) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < buf.length; i++) {
            String hex = Integer.toHexString(buf[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex.toUpperCase());
        }
        return sb.toString();
    }

    /**
     * * 将16进制转换为二进制 *
     * * @param hexStr
     * * @return
     */
    public byte[] parseHexStr2Byte(String hexStr) {
        if (hexStr.length() < 1)
            return null;
        byte[] result = new byte[hexStr.length() / 2];
        for (int i = 0; i < hexStr.length() / 2; i++) {
            int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
            int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2),
                    16);
            result[i] = (byte) (high * 16 + low);
        }
        return result;
    }

    /**
     * Hex字符串转byte
     *
     * @param inHex 待转换的Hex字符串
     * @return 转换后的byte
     */
    public byte hexToByte(String inHex) {
        return (byte) Integer.parseInt(inHex, 16);
    }

    /**
     * hex字符串转byte数组
     *
     * @param inHex 待转换的Hex字符串
     * @return 转换后的byte数组结果
     */
    public byte[] hexToByteArray(String inHex) {
        int hexlen = inHex.length();
        byte[] result;
        if (hexlen % 2 == 1) {
            //奇数
            hexlen++;
            result = new byte[(hexlen / 2)];
            inHex = "0" + inHex;
        } else {
            //偶数
            result = new byte[(hexlen / 2)];
        }
        int j = 0;
        for (int i = 0; i < hexlen; i += 2) {
            result[j] = hexToByte(inHex.substring(i, i + 2));
            j++;
        }
        return result;
    }

    /**
     * encrypt input text
     *
     * @param input
     * @param key
     * @return
     */
    public String encrypt(String input, String key) throws Exception {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        if (key.length() == 0)
            key = "6543210987654321";
        byte[] crypted = null;
        try {

            SecretKeySpec skey = new SecretKeySpec(key.getBytes(), "AES");

            Cipher cipher = Cipher.getInstance(mode);
            cipher.init(Cipher.ENCRYPT_MODE, skey);
            crypted = cipher.doFinal(parseHexStr2Byte(input));
        } catch (Exception e) {
            System.out.println(e.toString());
            e.printStackTrace();
        }

        //return new String(Base64.encodeBase64(crypted));
        return parseByte2HexStr(crypted).toUpperCase();
//        return new String(Base64.encodeBase64(crypted));
    }

    /**
     * decrypt input text
     *
     * @param input
     * @param key
     * @return
     */
    public String decrypt(String input, String key) throws Exception {
        if (key.length() == 0)
            key = "6543210987654321";
        byte[] output = null;
        try {
            SecretKeySpec skey = new SecretKeySpec(key.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance(mode);
            cipher.init(Cipher.DECRYPT_MODE, skey);
            output = cipher.doFinal(hexToByteArray(input));
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return parseByte2HexStr(output).toUpperCase();
    }

}

