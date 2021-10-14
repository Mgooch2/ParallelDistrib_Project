# ParallelDistrib_Project

## Execution

1. Compile the Java classes
    ```sh
    javac @classes
    ```
2. Create a file called .env containing the following values:
    ```sh
    # The hostname of the TCPServerRouter instance
    ROUTER_HOSTNAME=localhost
    # The IP address of the server/client to connect to
    DESTINATION_IP=127.0.0.1
    ```
    For connection between multiple machines, these should be changed to the relevant values.

3. Launch the server router process

    ```sh
    java -cp out/ TCPServerRouter
    ```

4. Launch the server process

    ```sh
    java -cp out/ TCPServer
    ```

5. Launch the client process

    ```sh
    java -cp out/ TCPClient
    ```

NOTE: Eventually this build process should run as a shell script, and generate a bundled .jar file. However, I'd really like to avoid switching to a hulking build tool like Ant/Gradle/Maven.
