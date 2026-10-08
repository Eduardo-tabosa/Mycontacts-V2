package model;

import java.util.Objects;

public class Contato {

    private int id;
    private String nome;
    private String telefone;
    private String email;

    public Contato(String nome, String telefone, String email) {
        this(0, nome, telefone, email);
    }

    public Contato(int id, String nome, String telefone, String email) {
        this.id = id;
        this.nome = nome;
        this.telefone = telefone;
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmpresa() {
        return null;
    }

    public String getTipo() {
        return "Pessoal";
    }

    public String apresentar() {
        return "Nome: " + nome + "\n"
                + "Telefone: " + telefone + "\n"
                + "Email: " + email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Contato)) return false;
        Contato outro = (Contato) o;
        if (id != 0 && outro.id != 0) return id == outro.id;
        return Objects.equals(nome, outro.nome)
                && Objects.equals(telefone, outro.telefone)
                && Objects.equals(email, outro.email);
    }

    @Override
    public int hashCode() {
        return id != 0 ? Integer.hashCode(id) : Objects.hash(nome, telefone, email);
    }

    @Override
    public String toString() {
        return nome + " (" + telefone + ")";
    }
}
