# ParallelDistrib_Project

## Execution

1. Compile the Java classes
    ```sh
    javac *.java
    ```
2. Create a file called .env containing the following values:
    ```sh
    # The hostname of the TCPServerRouter instance
    ROUTER_HOSTNAME=localhost
    # The IP address of the server/client to connect to
    DESTINATION_IP=127.0.0.1
    ```
    For connection between multiple machines, these should be changed to the relevant values.

3. Launch the server router process in the background

    ```sh
    java TCPServerRouter&
    ```

4. Launch the server process in the background

    ```sh
    java TCPServer&
    ```

5. Launch the client process

    ```sh
    java TCPClient
    ```