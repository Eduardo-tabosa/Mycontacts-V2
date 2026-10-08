package app;

import controller.PrincipalController;
import dao.Conexao;
import dao.ContatoDAO;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import service.Agenda;

public class AgendaApp extends Application {

    @Override
    public void start(Stage stage) {
        try {
            Agenda agenda = new Agenda(new ContatoDAO(Conexao.getConexao()));

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/principal.fxml"));
            Parent raiz = loader.load();

            PrincipalController controller = loader.getController();
            controller.setAgenda(agenda);

            Scene cena = new Scene(raiz, 900, 560);
            cena.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

            stage.setTitle("MyContacts | Agenda de Contatos");
            stage.setMinWidth(760);
            stage.setMinHeight(460);
            stage.setScene(cena);
            stage.show();
        } catch (Exception e) {
            Alert alerta = new Alert(Alert.AlertType.ERROR);
            alerta.setHeaderText("Não foi possível iniciar a aplicação");
            alerta.setContentText(e.getMessage());
            alerta.showAndWait();
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {
        Conexao.fecharConexaoApp();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
