package com.itp.sigedindocentes;

import java.util.StringTokenizer;

public class MethodsClass {
    public static String textCapWords(String string) {
        if (string == null) {
            return null;
        }
        StringBuilder builder = new StringBuilder();
        StringTokenizer st = new StringTokenizer(string, " ");
        while (st.hasMoreElements()) {
            String ne = (String) st.nextElement();
            if (ne.length() > 0) {
                builder.append(ne.substring(0, 1).toUpperCase());
                builder.append(ne.substring(1).toLowerCase());
                builder.append(' ');
            }
        }
        return builder.toString();
    }
}
