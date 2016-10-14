package com.lyue.aw_an.ndk;



import java.io.UnsupportedEncodingException;

/**
 * Created by Administrator on 2016/2/23.
 */
public class MyUtil {
    public static String byteArrayToString(byte[] array) {
        if (array == null)
            return null;
//        if(array.length > 1) {
//            if (array[array.length - 2] == 0x0D && array[array.length - 1] == 0x0A) {
//                //\r\n结尾无法显示，去掉
//                array = subBytes(array, 0, array.length - 2);
//            }
//        }
        String result = "";
        try {
            result = new String(array, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        result = result.replace("\r", " ");
        result = result.replace("\n", " ");
        return result;
    }

    public static String ByteArrayToHex(byte[] arry) {
        String str = "";
        StringBuilder sb = new StringBuilder(str);
        for (byte element : arry) {
            sb.append(String.format("%02X ", element));
        }
        return sb.toString();
    }


    public static byte[] subBytes(byte[] src, int begin, int count) {
        byte[] bs = new byte[count];
        for (int i = begin; i < begin + count; i++) bs[i - begin] = src[i];
        return bs;
    }

    public static byte[] getByteArrayFromStringAfterSpilt(String str, String spiltStr) {
        byte[] data;
        if (str != null && str.length() > 0) {
            if (str.indexOf(spiltStr) == -1 && str.length() > 2) {
                return null;//没有空格
            }
            String[] str_ary = str.split(spiltStr);
            data = new byte[str_ary.length];
            for (int i = 0; i < str_ary.length; i++) {
                try {
                    data[i] = (byte) Integer.parseInt(str_ary[i], 16);
                } catch (Exception e) {
                    return null;
                }
            }
            return data;
        } else {
            return null;
        }
    }

    public static void Delay(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
