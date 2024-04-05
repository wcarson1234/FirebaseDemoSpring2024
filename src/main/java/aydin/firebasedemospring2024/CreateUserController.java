package aydin.firebasedemospring2024;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.remoteconfig.User;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CreateUserController {
    @FXML
    private TextField confirmPasswordTextField;

    @FXML
    private TextField emailTextField;

    @FXML
    private Label errorMessageLabel;

    @FXML
    private TextField firstNameTextField;

    @FXML
    private TextField lastNameTextField;

    @FXML
    private TextField passwordTextField;

    @FXML
    private TextField phoneNumberTextField;
    public void registerButtonHandler() {
        if(checkFields() && createAccountInFireauth()) {
            createUserInFireStore();
            clearTextFields();
            errorMessageLabel.setText("SUCCESS");
        }

    }
    public boolean createAccountInFireauth() {
        UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                .setEmail(emailTextField.getText())
                .setEmailVerified(false)
                .setPassword(passwordTextField.getText())
                .setPhoneNumber("+" +phoneNumberTextField.getText())
                .setDisplayName(String.format("%s %s",firstNameTextField.getText(),lastNameTextField.getText()))
                .setDisabled(false);

        UserRecord userRecord;
        try {
            userRecord = DemoApp.fauth.createUser(request);
            System.out.println("Successfully created new user with Firebase Uid: " + userRecord.getUid()
                    + " check Firebase > Authentication > Users tab");
            return true;

        } catch (FirebaseAuthException ex) {
            // Logger.getLogger(FirestoreContext.class.getName()).log(Level.SEVERE, null, ex);
            errorMessageLabel.setVisible(true);
            errorMessageLabel.setText("Error: Something went wrong, please try adjusting fields");
            return false;
        }

    }
    public boolean checkFields() {
        if(emailTextField.getText().isEmpty() ||
                firstNameTextField.getText().isEmpty() ||
                lastNameTextField.getText().isEmpty() ||
                passwordTextField.getText().isEmpty() ||
                phoneNumberTextField.getText().isEmpty() ||
                confirmPasswordTextField.getText().isEmpty()
        ) {
            errorMessageLabel.setText("Error: One or more empty fields");
            errorMessageLabel.setVisible(true);
            return false;
        }
        if(!(passwordTextField.getText().equals(confirmPasswordTextField.getText()))) {
            errorMessageLabel.setText("Error: Passwords do not match");
            errorMessageLabel.setVisible(true);
            return false;
        }
        if(passwordTextField.getText().length() < 6) {
            errorMessageLabel.setText("Error: Password must be 6 characters long");
            errorMessageLabel.setVisible(true);
        }
        //if()
        return true;
    }
    public void returnToLoginScreen() throws IOException {
        DemoApp.switchScene("Login.fxml", 600, 600);
    }
    private void createUserInFireStore() {
        try {
            UserRecord userRecord = DemoApp.fauth.getInstance().getUserByEmail(emailTextField.getText());

            DocumentReference docRef = DemoApp.fstore.collection("user").document(userRecord.getUid());

            Map<String, Object> data = new HashMap<>();
            data.put("firstName", firstNameTextField.getText());
            data.put("lastName", lastNameTextField.getText());
            data.put("email",emailTextField.getText());
            data.put("phoneNumber", phoneNumberTextField.getText());
            data.put("password", passwordTextField.getText());
            data.put("UID", userRecord.getUid());

            //asynchronously write data
            ApiFuture<WriteResult> result = docRef.set(data);
        } catch (FirebaseAuthException e) {
            throw new RuntimeException(e);
        }


    }
    private void clearTextFields() {
        passwordTextField.clear();
        phoneNumberTextField.clear();
        lastNameTextField.clear();
        firstNameTextField.clear();
        confirmPasswordTextField.clear();
        emailTextField.clear();

    }
}