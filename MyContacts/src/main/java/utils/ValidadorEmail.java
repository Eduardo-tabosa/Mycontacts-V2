package utils;

import java.util.regex.Pattern;

/**
 * Validação de e-mail. A versão anterior só checava se havia "@" e ".";
 * agora usamos uma expressão regular que exige usuário, domínio e extensão.
 */
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
