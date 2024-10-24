package com.victorlamp.matrixiot.service.agent.utils.script;

import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Component("convertUtils")
public class ConvertUtils {
    private static String[] binaryArray = {"0000", "0001", "0010", "0011",
            "0100", "0101", "0110", "0111", "1000", "1001", "1010", "1011",
            "1100", "1101", "1110", "1111"};
    private static String hexStr = "0123456789ABCDEF";

    /**
     * string转换为BYTE数组
     *
     * @param hexString
     * @return
     */
    public static byte[] hexStringToByte(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    /**
     * 将字节数组转换为16进制格式的字符串
     *
     * @param b
     * @return
     */
    public static String ByteToHexString(byte[] b) {
        String r = "";

        for (int i = 0; i < b.length; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            r += hex.toUpperCase();
        }

        return r;
    }

    // 电磁水表换算 华旭提供
    public static int dchs(String hexString) {
        int temp = 0;
        for (int i = 0; i < 8; i++) {
            temp += (Integer.parseInt(hexString.substring(i, i + 1), 16) * Math.pow(16, 7 - i));
        }
        return temp;
    }

    /**
     * 按长度分离字符
     *
     * @param str
     * @param length
     * @return
     */
    public static List<String> split(String str, int length) {
        List<String> list = new ArrayList<>();
        int z = 0;
        for (int i = 0; i < str.length() / length; i++) {
            String a = str.substring(z, z + length);

            list.add(a);
            z = z + length;
        }
        return list;
    }

    //16进制转换为ASCII
    public static String convertHexToString(String hex) {
        StringBuilder sb = new StringBuilder();
        StringBuilder temp = new StringBuilder();
        //49204c6f7665204a617661 split into two cha racters 49, 20, 4c...
        for (int i = 0; i < hex.length() - 1; i += 2) {
            //grab the hex in pairs
            String output = hex.substring(i, (i + 2));
            if (output.equals("00"))
                continue;

            //convert hex to decimal
            int decimal = Integer.parseInt(output, 16);
            //convert the decimal to character
            sb.append((char) decimal);
            temp.append(decimal);
        }
        return sb.toString();
    }

    public static String ChangeSort(String hex) {
        String tempString = "";
        for (int i = 0; i < hex.length() / 2; i++) {
            tempString = (hex.substring(i * 2, i * 2 + 2) + tempString);
        }
        return tempString;
    }

    /**
     * byte数组中转换为int数值，本方法适用于(低位在前，高位在后)的顺序。
     *
     * @param
     * @return
     */
    public static int bytesToInt(byte[] src, int offset) {
        int value;
        value = (int) ((src[offset] & 0xFF) | ((src[offset + 1] & 0xFF) << 8)
                | ((src[offset + 2] & 0xFF) << 16) | ((src[offset + 3] & 0xFF) << 24));
        return value;
    }

    /**
     * 16进制直接转换成为byte数组(无需Unicode解码)
     *
     * @param
     * @return
     */
    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    /**
     * @param
     * @return 二进制数组转换为二进制字符串 2-2
     */
    public static String bytes2BinStr(byte[] bArray) {

        String outStr = "";
        int pos = 0;
        for (byte b : bArray) {
            // 高四位
            pos = (b & 0xF0) >> 4;
            outStr += binaryArray[pos];
            // 低四位
            pos = b & 0x0F;
            outStr += binaryArray[pos];
        }
        return outStr;
    }

    /**
     * @param hexString
     * @return 将十六进制转换为二进制字节数组 16-2
     */
    public static byte[] hexStr2BinArr(String hexString) {
        // hexString的长度对2取整，作为bytes的长度
        int len = hexString.length() / 2;
        byte[] bytes = new byte[len];
        byte high = 0;// 字节高四位
        byte low = 0;// 字节低四位
        for (int i = 0; i < len; i++) {
            // 右移四位得到高位
            high = (byte) ((hexStr.indexOf(hexString.charAt(2 * i))) << 4);
            low = (byte) hexStr.indexOf(hexString.charAt(2 * i + 1));
            bytes[i] = (byte) (high | low);// 高地位做或运算
        }
        return bytes;
    }

    /**
     * @param hexString
     * @return 将十六进制转换为二进制字符串 16-2
     */
    public static String hexStr2BinStr(String hexString) {
        return bytes2BinStr(hexStr2BinArr(hexString));
    }

    /**
     * ConvertQuantity1
     * 16进制转10进制
     *
     * @param hex
     * @return
     */
    public BigInteger hexToDecimal(String hex) {
        return new BigInteger(hex, 16);
    }

    /**
     * 10进制转16进制
     *
     * @param i
     * @return
     */
    public String demicalToHex(int i) {
        return Integer.toHexString(i);
    }

    /**
     * 16进制转2进制字符
     *
     * @param hex
     * @return
     */
    public String hexToByte(String hex) {
        int i = Integer.parseInt(hex, 16);
        String str2 = Integer.toBinaryString(i);
        return str2;
    }

    /**
     * 2进制字符转16进制
     *
     * @param bString
     * @return
     */
    public String byteToHex(String bString) {
        if (bString == null || bString.equals("") || bString.length() % 8 != 0)
            return null;
        StringBuffer tmp = new StringBuffer();
        int iTmp = 0;
        for (int i = 0; i < bString.length(); i += 4) {
            iTmp = 0;
            for (int j = 0; j < 4; j++) {
                iTmp += Integer.parseInt(bString.substring(i + j, i + j + 1)) << (4 - j - 1);
            }
            tmp.append(Integer.toHexString(iTmp));
        }
        return tmp.toString();
    }

    /**
     * 16进制转2进制字符串
     *
     * @param hexString
     * @return
     */
    public String hexString2binaryString(String hexString) {
        if (hexString == null || hexString.length() % 2 != 0)
            return null;
        String bString = "", tmp;
        for (int i = 0; i < hexString.length(); i++) {
            tmp = "0000" + Integer.toBinaryString(Integer.parseInt(hexString.substring(i, i + 1), 16));
            bString += tmp.substring(tmp.length() - 4);
        }
        return bString;
    }

    /**
     * 2进制字符转16进制
     *
     * @param bString
     * @return
     */
    public String binaryString2hexString(String bString) {
        if (bString == null || bString.equals("") || bString.length() % 8 != 0)
            return null;
        StringBuffer tmp = new StringBuffer();
        int iTmp = 0;
        for (int i = 0; i < bString.length(); i += 4) {
            iTmp = 0;
            for (int j = 0; j < 4; j++) {
                iTmp += Integer.parseInt(bString.substring(i + j, i + j + 1)) << (4 - j - 1);
            }
            tmp.append(Integer.toHexString(iTmp));
        }
        return tmp.toString();
    }

    /**
     * 2进制字符转10进制
     *
     * @param str
     * @return
     */
    public int byteToDecimal(String str) {
        int len = str.length();
        char ch;
        int num = 0;
        for (int i = len - 1; i >= 0; i--) {
            ch = str.charAt(i);
            if (ch != '0' && ch != '1') {
                System.out.println("input wrong!!");
                break;
            }
            if (ch == '1') {
                num = (int) (num + Math.pow(2, (len - 1) - i));
            }
        }
        return num;
    }

    /**
     * 十进制转二进制
     *
     * @param decNum
     * @param digit
     * @return
     */
    public String decimalToByte(int decNum, int digit) {
        String binStr = "";
        for (int i = digit - 1; i >= 0; i--) {
            binStr += (decNum >> i) & 1;
        }
        return binStr;
    }

    /**
     * 16进制高低位转换
     *
     * @param src
     * @return
     */
    public String hexTransfer(String src) {
        if (src.length() % 2 != 0) {
            System.out.println("16进制数字长度错误!");
            return "";
        }
        // 按照2个字节进行截取
        List<String> tmp = new ArrayList<String>();
        for (int i = 0; i < src.length() / 2; i++) {
            tmp.add(src.substring(i * 2, (i + 1) * 2));
        }
        Collections.reverse(tmp);
        StringBuffer sb = new StringBuffer();
        for (String str : tmp) {
            sb.append(str);
        }
        return sb.toString();
    }

    /**
     * 16进制加法
     *
     * @param hex1
     * @param hex2
     * @return
     */
    public String hexAddHex(String hex1, String hex2) {
        BigInteger hi1 = hexToDecimal(hex1);
        BigInteger hi2 = hexToDecimal(hex2);
        return demicalToHex(hi1.add(hi2).intValue());
    }

    /**
     * 16进制减法
     *
     * @param hex1
     * @param hex2
     * @return
     */
    public String hexSubHex(String hex1, String hex2) {
        BigInteger hi1 = hexToDecimal(hex1);
        BigInteger hi2 = hexToDecimal(hex2);
        return demicalToHex(hi1.subtract(hi2).intValue());
    }

    /**
     * hex1 和 hex2 按位与的补码表示
     *
     * @param hex1
     * @param hex2
     * @return
     */
    public String bitwiseAnd(String hex1, String hex2) {
        return demicalToHex(hexToDecimal(hex1).intValue() & hexToDecimal(hex2).intValue());
    }

    /**
     * hex1 和 hex2 按位或的补码表示
     *
     * @param hex1
     * @param hex2
     * @return
     */
    public String bitwiseOr(String hex1, String hex2) {
        return demicalToHex(hexToDecimal(hex1).intValue() | hexToDecimal(hex2).intValue());
    }

    /**
     * 十六进制补位(不足补0)
     *
     * @param s
     * @param length
     * @return
     */
    public String padLeft(String s, int length) {
        byte[] bs = new byte[length];
        byte[] ss = s.getBytes();
        Arrays.fill(bs, (byte) (48 & 0xff));
        System.arraycopy(ss, 0, bs, length - ss.length, ss.length);
        return new String(bs);
    }

    /**
     * "7dd",4,'0'==>"07dd"
     *
     * @param input  需要补位的字符串
     * @param size   补位后的最终长度
     * @param symbol 按symol补充 如'0'
     * @return N_TimeCheck中用到了
     */
    public String fill(String input, int size, char symbol) {
        while (input.length() < size) {
            input = symbol + input;
        }
        return input;
    }

    public String fill(String input, int size) {
        char symbol = '0';
        while (input.length() < size) {
            input = symbol + input;
        }
        return input;
    }

    /**
     * 保留小数
     *
     * @param num
     * @param length
     * @return
     */
    public String reserveDecimal(Double num, int length) {
        String format = "%." + length + "f";
        return String.format(format, num);
    }

    public String AESECBPKCS7Padding2Decrypt(String input) throws Exception {
        AESECBPKCS7Padding2 dece = new AESECBPKCS7Padding2();
        return dece.decrypt(input, "");    //解密算法
    }

    public String AESECBPKCS7Padding2Decrypt(String input, String password) throws Exception {
        AESECBPKCS7Padding2 dece = new AESECBPKCS7Padding2();
        return dece.decrypt(input, password);    //解密算法
    }

    /**
     * 16进制数转换成10进制数
     *
     * @param
     * @return
     */
    public String ConvertQuantity(String hex) {
        if (hex.equals("EEEEEEEE"))
            return "";
        //DecimalFormat df = new DecimalFormat("0.00");
        //return df.format((Utilty.bytesToInt(Utilty.hexStringToBytes(hex), 0))/ (double) 100);
        return hexToDecimal(hex).toString();
    }

    /**
     * 16进制数转换成10进制数(有符号数)
     *
     * @param hex
     * @return
     */
    public String ConvertQuantity4(String hex) {
        if ((hex.length() == 4 && hex.equals("EEEE")) || (hex.length() == 8 && hex.equals("EEEEEEEE")))
            return "";
        return String.valueOf(Integer.valueOf(hex, 16).shortValue());
    }

    /**
     * 16进制数转换成10进制数(100)
     *
     * @param
     * @return
     */
    public String ConvertQuantity3(String hex) {
        if ((hex.length() == 4 && hex.equals("EEEE")) || (hex.length() == 8 && hex.equals("EEEEEEEE")))
            return "";
        DecimalFormat df = new DecimalFormat("0.000");
        return df.format(Integer.parseInt(hex, 16) / (double) 1000);
    }

    /**
     * 16进制数转换成10进制数(1000)
     *
     * @param
     * @return
     */
    public String ConvertQuantity2(String hex) {
        if ((hex.length() == 4 && hex.equals("EEEE")) || (hex.length() == 8 && hex.equals("EEEEEEEE")))
            return "";
        DecimalFormat df = new DecimalFormat("0.000");
        return df.format(Integer.parseInt(hex, 16) / (double) 100);
    }

    /**
     * 16进制数转换成10进制数(1000)
     *
     * @param
     * @return
     */
    public String ConvertQuantity5(String hex) {
        if ((hex.length() == 4 && hex.equals("EEEE")) || (hex.length() == 8 && hex.equals("EEEEEEEE")))
            return "";
        DecimalFormat df = new DecimalFormat("0.00");
        return df.format(Integer.parseInt(hex, 16) / (double) 100);
    }

    /**
     * 16进制数转换成10进制数(1000)
     *
     * @param
     * @return
     */
    public String ConvertQuantity1(String hex) {
        if ((hex.length() == 4 && hex.equals("EEEE")) || (hex.length() == 8 && hex.equals("EEEEEEEE")))
            return "";
        DecimalFormat df = new DecimalFormat("0.000");
        return df.format(Integer.parseInt(hex, 16) / (double) 1000);
    }

    public String dateFormat(Date date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }

    /**
     * 返回水表编码
     *
     * @param
     * @return
     */
    public String AnalyzeAddress(String HexString) {
        String Address = "";
        Address += HexString.substring(12, 14) + HexString.substring(10, 12);
        Address += HexString.substring(8, 10) + HexString.substring(6, 8);
        Address += HexString.substring(4, 6) + HexString.substring(2, 4);
        Address += HexString.substring(0, 2);
        return Address;
    }

    public String calculateChecksum(String HexString) {
        byte[] frameBody = hexStringToByte(HexString);
        byte sum = 0x00;
        byte[] reByte = new byte[1];
        int a = frameBody.length;
        for (int i = 0; i < frameBody.length; i++)
            sum += frameBody[i];
        reByte[0] = sum;
        return ByteToHexString(reByte);
    }

    /**
     * 16进制数转换成10进制数
     *
     * @param hex
     * @return
     */
    public String ConvertQuantity6(String hex) {
        if (hex.equals("EEEEEEEE"))
            return "";
        DecimalFormat df = new DecimalFormat("0.00");
        return df.format((bytesToInt(hexStringToBytes(hex), 0)) / (double) 100);
    }

    public String bytes2HexString(byte[] b) {
        String r = "";

        for (int i = 0; i < b.length; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            r += hex.toUpperCase();
        }

        return r;
    }

    /**
     * 十六进制转浮点小数
     *
     * @param str
     * @return
     */
    public float convertHexToFloat(String str) {
        Long l = convertHexToLong(str);
        float value = Float.intBitsToFloat(l.intValue());
        return value;
    }

    /**
     * 十六进制转Long
     *
     * @param str
     * @return
     */
    public Long convertHexToLong(String str) {
        return Long.parseLong(str, 16);
    }
}
