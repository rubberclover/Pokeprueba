This is the file document with the explanation on how to run the application. You have to follow the next steps in order to start the program.

Requirements necesar to run the application:
- Docker Desktop https://docs.docker.com/desktop/install/windows-install/
- JavaFX (Our version: 20) https://gluonhq.com/products/javafx/ 
- Eclipse to run Java

Steps:
- Docker :
    - Install Docker Desktop and launch it.
    - Go to the repository folder (Pokeprueba) with the Command Prompt.
    - Start the docker container with: docker-compose up -d
    - Load the Java Schema with : java -jar scalardb-schema-loader-3.12.2.jar --config scalardb.properties --schema-file pokemon.json --coordinator
- JavaFX :
    - Install JavaFX (20 to avoid any incompatibilities with other versions).
    - Move the downloaded folder to any emplacement (Recommanded: "C:\Program Files\Java")
    - Launch your IDE (Eclipse) and add JavaFX in your build path.
        - To add it, create a User Library and add External Jars. And in "PathToFolder\javafx-sdk-20\lib", select all .jar files. Replace javafx-sdk-20 by your version.
        - Then add the Library created to your project.
    - To start the application, create a new Run Configuration.
        - In Java Application, the main class should be : "presentation.PokedexMain".
        - Then in Arguments, put "--module-path "PathToFolder\javafx-sdk-20\lib" --add-modules javafx.base,javafx.controls,javafx.graphics,javafx.fxml,javafx.media,javafx.web,javafx.swing" in the VM arguments. Replace javafx-sdk-20 by your version.
- Start the application with this configuration.

