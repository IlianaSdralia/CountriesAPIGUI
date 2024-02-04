package ME2352.CountriesGUI.CountriesEffect;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class App extends Application {

    private static final String BASE_URL = "http://localhost:8081/api/";
    private static final String[] PATHS = {"all", "country", "language", "currency"};

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Countries API GUI");

        ComboBox<String> pathComboBox = new ComboBox<>(FXCollections.observableArrayList(PATHS));

        TextField searchField = new TextField();
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        Button searchButton = new Button("Search");
        ListView<String> resultsList = new ListView<>();

        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(100, 100, 100, 100));
        gridPane.setVgap(5);
        gridPane.setHgap(5);

        GridPane.setConstraints(pathComboBox, 0, 0);
        GridPane.setConstraints(searchField, 1, 0);
        GridPane.setConstraints(searchButton, 2, 0);

        gridPane.getChildren().addAll(pathComboBox, searchField, searchButton);

        FlowPane flowPane = new FlowPane();
        flowPane.setPadding(new Insets(100, 100, 100, 100));
        flowPane.setVgap(5);
        flowPane.setHgap(5);

        FlowPane.setMargin(resultsList, new Insets(0, 0, 0, 10));
        flowPane.getChildren().add(resultsList);

        FlowPane mainLayout = new FlowPane();
        mainLayout.getChildren().addAll(gridPane, flowPane);

     // Προσθήκη λειτουργικότητας για το κλικ του κουμπιού
        searchButton.setOnAction(event -> {
            String selectedPath = pathComboBox.getValue();
            String searchTerm = searchField.getText();
            if (selectedPath != null) {
                if ("all".equals(selectedPath)) {
                    // Handle the "all" path without a search term
                    ArrayList<String> searchResults = performApiSearchAll();
                    resultsList.getItems().setAll(searchResults);
                } else if (!searchTerm.isEmpty()) {
                    // Handle other paths with a search term
                    ArrayList<String> searchResults = performApiSearch(selectedPath, searchTerm);
                    resultsList.getItems().setAll(searchResults);
                }
            }
        });
        
        // Προσθήκη λειτουργικότητας για την αλλαγή του επιλεγμένου path
        pathComboBox.setOnAction(event -> {
            // Clear the input box when the selected path changes
            searchField.clear();
        });

        Scene scene = new Scene(mainLayout, 1000, 1000);
        primaryStage.setScene(scene);

        primaryStage.show();
    }

    private ArrayList<String> performApiSearch(String path, String searchTerm) {
        ArrayList<String> results = new ArrayList<>();
        try {
            URL url = new URL(BASE_URL + path + "/" + searchTerm);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                // Parse the JSON response
                JSONArray jsonArray = new JSONArray(reader.lines().collect(Collectors.joining("\n")));

                // Convert the JSON array to a pretty-printed JSON string
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(jsonArray.toString());
                String prettyJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonNode);

                // Remove square brackets, curly braces, and double quotes
                String cleanedJson = prettyJson
                        .replaceAll("\\[", "")
                        .replaceAll("\\]", "")
                        .replaceAll("\\{", "")
                        .replaceAll("\\}", "")
                        .replaceAll("\"", "");

                // Add the cleaned JSON string to the results
                results.add(cleanedJson);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return results;
    }
    private ArrayList<String> performApiSearchAll() {
        ArrayList<String> results = new ArrayList<>();
        try {
            URL url = new URL(BASE_URL + "all");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String response = reader.lines().collect(Collectors.joining("\n"));
                results.add(response);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return results;
    }


}