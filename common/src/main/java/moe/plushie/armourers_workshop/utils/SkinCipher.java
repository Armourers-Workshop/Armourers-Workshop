package moe.plushie.armourers_workshop.utils;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

public class SkinCipher {

    private static final SkinCipher INSTANCE = new SkinCipher();

    private final byte[] alphabetOut;
    private final byte[] alphabetIn;

    private SkinCipher() {
        alphabetOut = "AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz1234567890".getBytes();
        alphabetIn = new byte[256];
        for (int i = 0; i < alphabetOut.length; ++i) {
            alphabetIn[alphabetOut[i] & 0xff] = (byte) i;
        }
    }

    public static SkinCipher getInstance() {
        return INSTANCE;
    }

    public String encrypt(String... values) {
        String value = join(values);
        byte[] valueBytes = value.getBytes(StandardCharsets.UTF_8);
        for (int i = 0; i < valueBytes.length; ++i) {
            valueBytes[i] = (byte) (valueBytes[i] ^ 0x77);
        }
        byte[] encodedBytes = convertTo(valueBytes, alphabetIn.length, alphabetOut.length);
        for (int i = 0; i < encodedBytes.length; ++i) {
            encodedBytes[i] = alphabetOut[encodedBytes[i]];
        }
        return new String(encodedBytes, StandardCharsets.UTF_8);
    }

    public String[] decrypt(String value) {
        byte[] valueBytes = value.getBytes(StandardCharsets.UTF_8);
        for (int i = 0; i < valueBytes.length; ++i) {
            valueBytes[i] = alphabetIn[valueBytes[i]];
        }
        byte[] decodedBytes = convertTo(valueBytes, alphabetOut.length, alphabetIn.length);
        for (int i = 0; i < decodedBytes.length; ++i) {
            decodedBytes[i] = (byte) (decodedBytes[i] ^ 0x77);
        }
        return new String(decodedBytes, StandardCharsets.UTF_8).split("\n");
    }

    /**
     * Converts a byte array from a source base to a target base using the alphabet.
     */
    private byte[] convertTo(final byte[] message, final int sourceBase, final int targetBase) {
        // This algorithm is inspired by: http://codegolf.stackexchange.com/a/21672
        var out = new ByteArrayOutputStream();
        byte[] source = message;
        while (source.length > 0) {
            var quotient = new ByteArrayOutputStream(source.length);
            int remainder = 0;
            for (byte val : source) {
                final int accumulator = (val & 0xFF) + remainder * sourceBase;
                final int digit = (accumulator - (accumulator % targetBase)) / targetBase;
                remainder = accumulator % targetBase;
                if (quotient.size() > 0 || digit > 0) {
                    quotient.write(digit);
                }
            }
            out.write(remainder);
            source = quotient.toByteArray();
        }

        // pad output with zeroes corresponding to the number of leading zeroes in the message
        for (int i = 0; i < message.length - 1 && message[i] == 0; i++) {
            out.write(0);
        }

        return reverse(out.toByteArray());
    }

    private byte[] reverse(byte[] bytes) {
        int n = bytes.length;
        for (int i = 0; i < n / 2; i++) {
            byte t = bytes[i];
            bytes[i] = bytes[n - i - 1];
            bytes[n - i - 1] = t;
        }
        return bytes;
    }

    private String join(String[] values) {
        var separator = "";
        var builder = new StringBuilder();
        for (var value : values) {
            builder.append(separator);
            builder.append(value);
            separator = "\n";
        }
        return builder.toString();
    }
}
