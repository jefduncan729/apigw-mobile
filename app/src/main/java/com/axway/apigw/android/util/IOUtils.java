package com.axway.apigw.android.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by su on 11/10/2015.
 */
public class IOUtils {

    public static final int BUF_SIZE = 4096;

    public static long copy(InputStream input, OutputStream output) throws IOException {
        byte[] buffer = new byte[BUF_SIZE];
        long count = 0L;
        int n = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        output.flush();
        return count;
    }

    public static void closeQuietly(InputStream input) {
        if (input == null)
            return;
        try {
            input.close();
        } catch (IOException e) {
            //ignore
        }
    }

    public static void closeQuietly(OutputStream output) {
        if (output == null)
            return;
        try {
            output.close();
        } catch (IOException e) {
            //ignore
        }
    }
}
