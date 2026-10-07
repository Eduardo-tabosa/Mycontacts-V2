package model;

/**
 * Contato comercial: um contato comum com o nome da empresa.
 */
public class ContatoComercial extends Contato {

    private String empresa;

    public ContatoComercial(String nome, String telefone, String email, String empresa) {
        this(0, nome, telefone, email, empresa);
    }

    public ContatoComercial(int id, String nome, String telefone, String email, String empresa) {
        super(id, nome, telefone, email);
        this.empresa = empresa;
    }

    @Override
    public String getEmpresa() {
        return empresa;
    }

    public void setEmpresa(String empresa) {
        this.empresa = empresa;
    }

    @Override
    public String getTipo() {
        return "Comercial";
    }

    @Override
    public String apresentar() {
        return super.apresentar() + "\nEmpresa: " + empresa;
    }
}
