# Algorithm

## How to run simulation
1. change the realRun to be `false` in simulator.Simulator.java.

    ```java
    private static final boolean realRun = false;
    ```

2. If you wish to run image recognition, set `task` to 'IMG' in Simulator.java.

3. Run the main function in Simulator.java and press buttons for different tasks.

## How to run real run
1. change the realRun to be `true` in simulator.Simulator.java.
    ```java
    private static final boolean realRun = true;
    ```

2. configure the gRPC server host in config.GrpcConst.java by changing the 
CLIENT_HOST to be the IP address of the server. You need to replace `xxx.xxx.xxx.xxx`
below with the real IP address of the rpi.

    ```java
    public static final String CLIENT_HOST = "xxx.xxx.xxx.xxx";
    ```
   
3. Set the task to be 'EXP', 'FP' or 'IMG' in Simulator.java.

4. Run the main function in Simulator.java
