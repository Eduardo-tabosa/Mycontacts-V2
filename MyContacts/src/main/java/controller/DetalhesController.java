package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import model.Contato;

/**
 * Controla a tela de detalhes (detalhes.fxml).
 */
public class DetalhesController {

    @FXML private Label lblIniciais;
    @FXML private Label lblNome;
    @FXML private Label lblTipo;
    @FXML private Label lblTelefone;
    @FXML private Label lblEmail;
    @FXML private Label lblEmpresa;

    public void setContato(Contato contato) {
        lblIniciais.setText(iniciais(contato.getNome()));
        lblNome.setText(contato.getNome());
        lblTipo.setText(contato.getTipo());
        lblTelefone.setText(contato.getTelefone());
        lblEmail.setText(contato.getEmail());
        lblEmpresa.setText(contato.getEmpresa() == null ? "Não informada" : contato.getEmpresa());
    }

    @FXML
    private void onFechar() {
        ((Stage) lblNome.getScene().getWindow()).close();
    }

    private String iniciais(String nome) {
        String[] partes = nome.trim().split("\\s+");
        String primeira = partes[0].substring(0, 1);
        String ultima = partes.length > 1 ? partes[partes.length - 1].substring(0, 1) : "";
        return (primeira + ultima).toUpperCase();
    }
}
