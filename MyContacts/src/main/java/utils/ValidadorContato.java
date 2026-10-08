package utils;

import exceptions.ContatoInvalidoException;
import model.Contato;
import model.ContatoComercial;

public class ValidadorContato {

    private ValidadorContato() {
    }

    public static void validar(Contato contato) {
        if (contato == null) {
            throw new ContatoInvalidoException("Contato não informado.");
        }
        if (vazio(contato.getNome())) {
            throw new ContatoInvalidoException("O nome é obrigatório.");
        }
        if (contato.getNome().trim().length() > 100) {
            throw new ContatoInvalidoException("O nome pode ter no máximo 100 caracteres.");
        }
        if (!telefoneValido(contato.getTelefone())) {
            throw new ContatoInvalidoException("Telefone inválido. Use de 8 a 13 dígitos, ex: (85) 99999-0000.");
        }
        if (!ValidadorEmail.validar(contato.getEmail())) {
            throw new ContatoInvalidoException("E-mail inválido.");
        }
        if (contato instanceof ContatoComercial && vazio(contato.getEmpresa())) {
            throw new ContatoInvalidoException("Contato comercial precisa ter empresa.");
        }
    }

    public static boolean telefoneValido(String telefone) {
        if (vazio(telefone)) return false;
        if (!telefone.matches("[0-9()+\\-\\s]+")) return false;
        int digitos = telefone.replaceAll("\\D", "").length();
        return digitos >= 8 && digitos <= 13;
    }

    private static boolean vazio(String texto) {
        return texto == null || texto.isBlank();
    }
}
