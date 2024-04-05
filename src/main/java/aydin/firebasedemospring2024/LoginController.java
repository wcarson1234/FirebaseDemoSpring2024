package aydin.firebasedemospring2024;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {
    @FXML
    private Label errorLabel;
    @FXML
    private TextField emailTextField;

    @FXML
    private PasswordField passwordPasswordTextField;
    @FXML
    private void registerButtonClicked() throws IOException {
        DemoApp.switchScene("CreateUser.fxml", 500, 400);
    }
    @FXML
    private void switchToPrimary() throws IOException {
        DemoApp.switchScene("DataEntry.fxml", 650, 650);
    }

    public void loginButtonHandler() throws IOException {
        if(checkLogin()) {
            switchToPrimary();
        }
    }

    private boolean checkLogin() {

        if(emailTextField.getText().isEmpty() || passwordPasswordTextField.getText().isEmpty()) {
            errorLabel.setText("Email/Password cannot be blank");
            return false;
        }

        try {
            UserRecord userRecord = DemoApp.fauth.getInstance().getUserByEmail(emailTextField.getText());
            String UID = userRecord.getUid();

            DocumentReference docRef = DemoApp.fstore.collection("user").document(UID);
            ApiFuture<DocumentSnapshot> future = docRef.get();

            DocumentSnapshot document = future.get();

            String password = document.getString("password");

            if(passwordPasswordTextField.getText().equals(password)) {
                return true;
            }

        } catch (FirebaseAuthException e) {
            errorLabel.setText("Email not registered");
            return false;

        }catch (InterruptedException e) {

            throw new RuntimeException(e);
        } catch (ExecutionException e) {

            throw new RuntimeException(e);
        }

        errorLabel.setText("Email or Password Incorrect, try again");
        return false;


    }

}