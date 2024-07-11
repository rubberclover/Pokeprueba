This is the file document with the explanation on how to run the application. You have to follow the next steps in order to start the program.

Requirements necesar to run the application:
- Docker Compose
- JavaFX

Steps:
- Go to the repository folder with the Command Prompt.
- Start the docker container with: docker-compose up -d
- Load the Java scheme with : java -jar scalardb-schema-loader-3.12.2.jar --config scalardb.properties --schema-file pokemon.json --coordinator
- Start the application

