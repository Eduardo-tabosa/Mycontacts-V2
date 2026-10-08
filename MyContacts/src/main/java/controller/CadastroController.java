package controller;

import exceptions.ContatoInvalidoException;
import exceptions.ContatoNaoEncontradoException;
import exceptions.PersistenciaException;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Contato;
import model.ContatoComercial;
import service.Agenda;
import utils.ValidadorContato;
import utils.ValidadorEmail;


public class CadastroController {

    private static final String TIPO_PESSOAL = "Pessoal";
    private static final String TIPO_COMERCIAL = "Comercial";
    private static final String CLASSE_ERRO = "campo-erro";

    @FXML private Label lblTitulo;
    @FXML private ComboBox<String> cbTipo;
    @FXML private TextField txtNome;
    @FXML private TextField txtTelefone;
    @FXML private TextField txtEmail;
    @FXML private TextField txtEmpresa;
    @FXML private Label lblEmpresa;
    @FXML private Label lblErro;

    private Agenda agenda;
    private Contato contatoOriginal;   // null quando é um contato novo
    private Contato contatoSalvo;
    private boolean salvo = false;

    @FXML
    private void initialize() {
        cbTipo.getItems().setAll(TIPO_PESSOAL, TIPO_COMERCIAL);
        cbTipo.setValue(TIPO_PESSOAL);
        cbTipo.setOnAction(e -> atualizarCampoEmpresa());
        atualizarCampoEmpresa();

        
        txtEmail.textProperty().addListener((obs, a, novo) ->
                marcar(txtEmail, !novo.isBlank() && !ValidadorEmail.validar(novo)));
        txtTelefone.textProperty().addListener((obs, a, novo) ->
                marcar(txtTelefone, !novo.isBlank() && !ValidadorContato.telefoneValido(novo)));
        txtNome.textProperty().addListener((obs, a, novo) -> marcar(txtNome, false));
    }

    public void setAgenda(Agenda agenda) {
        this.agenda = agenda;
    }

    
    public void setContato(Contato contato) {
        this.contatoOriginal = contato;
        if (contato == null) {
            lblTitulo.setText("Novo contato");
            return;
        }
        lblTitulo.setText("Editar contato");
        cbTipo.setValue(contato instanceof ContatoComercial ? TIPO_COMERCIAL : TIPO_PESSOAL);
        txtNome.setText(contato.getNome());
        txtTelefone.setText(contato.getTelefone());
        txtEmail.setText(contato.getEmail());
        txtEmpresa.setText(contato.getEmpresa() == null ? "" : contato.getEmpresa());
        atualizarCampoEmpresa();
    }

    public boolean isSalvo() {
        return salvo;
    }

    public Contato getContatoSalvo() {
        return contatoSalvo;
    }

    @FXML
    private void onSalvar() {
        Contato contato = montarContato();
        try {
            if (contatoOriginal == null) {
                agenda.adicionarContato(contato);
            } else {
                agenda.atualizarContato(contato);
            }
            contatoSalvo = contato;
            salvo = true;
            fechar();
        } catch (ContatoInvalidoException e) {
            lblErro.setText(e.getMessage());
            destacarCampoComErro(e.getMessage());
        } catch (ContatoNaoEncontradoException | PersistenciaException e) {
            lblErro.setText(e.getMessage());
        }
    }

    @FXML
    private void onCancelar() {
        fechar();
    }

    

    private Contato montarContato() {
        int id = contatoOriginal == null ? 0 : contatoOriginal.getId();
        String nome = txtNome.getText();
        String telefone = txtTelefone.getText();
        String email = txtEmail.getText();

        if (TIPO_COMERCIAL.equals(cbTipo.getValue())) {
            return new ContatoComercial(id, nome, telefone, email, txtEmpresa.getText());
        }
        return new Contato(id, nome, telefone, email);
    }

    private void atualizarCampoEmpresa() {
        boolean comercial = TIPO_COMERCIAL.equals(cbTipo.getValue());
        txtEmpresa.setDisable(!comercial);
        lblEmpresa.setDisable(!comercial);
        if (!comercial) {
            txtEmpresa.clear();
            marcar(txtEmpresa, false);
        }
    }

    private void destacarCampoComErro(String mensagem) {
        String m = mensagem.toLowerCase();
        if (m.contains("nome")) marcar(txtNome, true);
        else if (m.contains("telefone")) marcar(txtTelefone, true);
        else if (m.contains("e-mail")) marcar(txtEmail, true);
        else if (m.contains("empresa")) marcar(txtEmpresa, true);
    }

    private void marcar(TextField campo, boolean comErro) {
        campo.getStyleClass().remove(CLASSE_ERRO);
        if (comErro) campo.getStyleClass().add(CLASSE_ERRO);
    }

    private void fechar() {
        ((Stage) txtNome.getScene().getWindow()).close();
    }
}
