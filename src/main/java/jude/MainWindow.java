package jude;

//@@author cheeheng-reused
//Reused from https://se-education.org/guides/tutorials/javaFxPart4.html with minor modifications.

import java.io.IOException;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
//import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

/**
 * The class modelling the main GUI of Jude the chatbot.
 */
public class MainWindow {
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private Jude jude;
    private Image userImage = new Image(this.getClass().getResourceAsStream("/images/User.png"));
    private Image chatbotImage = new Image(this.getClass().getResourceAsStream(
            "/images/ManSmilingBehindWall.jpg"));
    //private Image userImage = null;
    //private Image chatbotImage = null;

    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    public void setJude(Jude d) {
        jude = d;
    }

    /**
     * Handles the user input when the user presses the Enter button or clicks the Send button.
     * Clears the user input after procsessing.
     * @param actionEvent An event which triggers the method call, such as pressing Enter and
     *                    clicking the Send button.
     * @throws IOException When system I/O fails.
     */
    public void handleUserInput(ActionEvent actionEvent) throws IOException {
        String command = userInput.getText();
        if (jude.isTerminationCommand(command)) {
            //@@author cheeheng-reused
            //Code adapted from
            //https://www.javaguides.net/2020/09/javafx-quit-button-example-terminate.html
            // Exit the program when a termination command such as 'bye' is passed to the chatbot.
            Platform.exit();
            return;
        }
        String response = jude.getResponse(command);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(command, userImage),
                DialogBox.getChatbotDialog(response, chatbotImage)
        );
        userInput.clear();
    }
}