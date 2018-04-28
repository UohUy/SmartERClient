package monash.smarterclient;

import java.security.MessageDigest;

public class MD5Parser {

        public static String encode(String content) {
            String encoded = null;
            try {
                MessageDigest digest = MessageDigest.getInstance("MD5");
                digest.update(content.getBytes());
                encoded = getEncode16(digest);
                //return getEncode32(digest);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return encoded;
        }

        private static String getEncode16(MessageDigest digest) {
            StringBuilder builder = new StringBuilder();
            for (byte b : digest.digest()) {
                builder.append(Integer.toHexString((b >> 4) & 0xf));
                builder.append(Integer.toHexString(b & 0xf));
            }
            return builder.toString().toUpperCase();
        }
    }
