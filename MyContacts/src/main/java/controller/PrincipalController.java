package controller;

import exceptions.ContatoNaoEncontradoException;
import exceptions.PersistenciaException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Contato;
import model.ContatoComercial;
import service.Agenda;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


public class PrincipalController {

    private static final String FILTRO_TODOS = "Todos";
    private static final String FILTRO_PESSOAIS = "Pessoais";
    private static final String FILTRO_COMERCIAIS = "Comerciais";

    @FXML private TextField txtBusca;
    @FXML private ComboBox<String> cbFiltro;
    @FXML private TableView<Contato> tabela;
    @FXML private TableColumn<Contato, String> colNome;
    @FXML private TableColumn<Contato, String> colTelefone;
    @FXML private TableColumn<Contato, String> colEmail;
    @FXML private TableColumn<Contato, String> colEmpresa;
    @FXML private TableColumn<Contato, String> colTipo;
    @FXML private Button btnEditar;
    @FXML private Button btnDetalhes;
    @FXML private Button btnRemover;
    @FXML private Label lblTotal;
    @FXML private Label lblStatus;

    private Agenda agenda;
    private final ObservableList<Contato> dados = FXCollections.observableArrayList();

    
    @FXML
    private void initialize() {
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colTelefone.setCellValueFactory(new PropertyValueFactory<>("telefone"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colEmpresa.setCellValueFactory(new PropertyValueFactory<>("empresa"));
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        tabela.setItems(dados);

        cbFiltro.getItems().setAll(FILTRO_TODOS, FILTRO_PESSOAIS, FILTRO_COMERCIAIS);
        cbFiltro.setValue(FILTRO_TODOS);

        
        txtBusca.textProperty().addListener((obs, antigo, novo) -> atualizarTabela());
        cbFiltro.setOnAction(e -> atualizarTabela());

        
        tabela.getSelectionModel().selectedItemProperty().addListener((obs, antigo, novo) -> {
            boolean nada = novo == null;
            btnEditar.setDisable(nada);
            btnDetalhes.setDisable(nada);
            btnRemover.setDisable(nada);
        });
        btnEditar.setDisable(true);
        btnDetalhes.setDisable(true);
        btnRemover.setDisable(true);

        
        tabela.setRowFactory(tv -> {
            TableRow<Contato> linha = new TableRow<>();
            linha.setOnMouseClicked(evento -> {
                if (evento.getClickCount() == 2 && !linha.isEmpty()) {
                    abrirDetalhes(linha.getItem());
                }
            });
            return linha;
        });
    }

    public void setAgenda(Agenda agenda) {
        this.agenda = agenda;
        atualizarTabela();
    }

    

    @FXML
    private void onNovo() {
        if (abrirCadastro(null)) {
            mostrarStatus("Contato adicionado.");
        }
    }

    @FXML
    private void onEditar() {
        Contato selecionado = tabela.getSelectionModel().getSelectedItem();
        if (selecionado != null && abrirCadastro(selecionado)) {
            mostrarStatus("Contato atualizado.");
        }
    }

    @FXML
    private void onDetalhes() {
        Contato selecionado = tabela.getSelectionModel().getSelectedItem();
        if (selecionado != null) {
            abrirDetalhes(selecionado);
        }
    }

    @FXML
    private void onRemover() {
        Contato selecionado = tabela.getSelectionModel().getSelectedItem();
        if (selecionado == null) return;

        Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacao.setTitle("Remover contato");
        confirmacao.setHeaderText("Remover " + selecionado.getNome() + "?");
        confirmacao.setContentText("Essa ação não pode ser desfeita.");
        estilizar(confirmacao);

        Optional<ButtonType> resposta = confirmacao.showAndWait();
        if (resposta.isPresent() && resposta.get() == ButtonType.OK) {
            try {
                agenda.removerContato(selecionado.getId());
                atualizarTabela();
                mostrarStatus("Contato removido.");
            } catch (ContatoNaoEncontradoException | PersistenciaException e) {
                mostrarErro(e.getMessage());
                agenda.recarregar();
                atualizarTabela();
            }
        }
    }

    @FXML
    private void onLimparBusca() {
        txtBusca.clear();
        cbFiltro.setValue(FILTRO_TODOS);
    }

    

    private void atualizarTabela() {
        if (agenda == null) return;

        List<Contato> resultado = agenda.buscarPorNome(txtBusca.getText());

        String filtro = cbFiltro.getValue();
        if (FILTRO_COMERCIAIS.equals(filtro)) {
            resultado = resultado.stream()
                    .filter(c -> c instanceof ContatoComercial)
                    .collect(Collectors.toList());
        } else if (FILTRO_PESSOAIS.equals(filtro)) {
            resultado = resultado.stream()
                    .filter(c -> !(c instanceof ContatoComercial))
                    .collect(Collectors.toList());
        }

        dados.setAll(resultado);
        int total = agenda.totalContatos();
        lblTotal.setText(total == 1 ? "1 contato" : total + " contatos");
    }

    
    private boolean abrirCadastro(Contato contato) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/cadastro.fxml"));
            Parent raiz = loader.load();

            CadastroController controller = loader.getController();
            controller.setAgenda(agenda);
            controller.setContato(contato);

            Stage janela = criarJanela(raiz, contato == null ? "Novo contato" : "Editar contato");
            janela.showAndWait();

            if (controller.isSalvo()) {
                atualizarTabela();
                tabela.getSelectionModel().select(controller.getContatoSalvo());
                return true;
            }
        } catch (IOException e) {
            mostrarErro("Não foi possível abrir o formulário: " + e.getMessage());
        }
        return false;
    }

    private void abrirDetalhes(Contato contato) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/detalhes.fxml"));
            Parent raiz = loader.load();

            DetalhesController controller = loader.getController();
            controller.setContato(contato);

            criarJanela(raiz, "Detalhes do contato").showAndWait();
        } catch (IOException e) {
            mostrarErro("Não foi possível abrir os detalhes: " + e.getMessage());
        }
    }

    private Stage criarJanela(Parent raiz, String titulo) {
        Scene cena = new Scene(raiz);
        cena.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

        Stage janela = new Stage();
        janela.setTitle(titulo);
        janela.initModality(Modality.APPLICATION_MODAL);
        janela.initOwner(tabela.getScene().getWindow());
        janela.setResizable(false);
        janela.setScene(cena);
        return janela;
    }

    private void mostrarStatus(String mensagem) {
        lblStatus.setText(mensagem);
    }

    private void mostrarErro(String mensagem) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle("Erro");
        alerta.setHeaderText(null);
        alerta.setContentText(mensagem);
        estilizar(alerta);
        alerta.showAndWait();
    }

    private void estilizar(Alert alerta) {
        alerta.getDialogPane().getStylesheets()
                .add(getClass().getResource("/css/style.css").toExternalForm());
        alerta.initOwner(tabela.getScene().getWindow());
    }
}
