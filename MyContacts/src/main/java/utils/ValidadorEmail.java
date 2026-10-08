package utils;

import java.util.regex.Pattern;

public class ValidadorEmail {

    private static final Pattern PADRAO =
            Pattern.compile("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)*\\.[A-Za-z]{2,}$");

    private ValidadorEmail() {
    }

    public static boolean validar(String email) {
        if (email == null) return false;
        return PADRAO.matcher(email.trim()).matches();
    }
}
