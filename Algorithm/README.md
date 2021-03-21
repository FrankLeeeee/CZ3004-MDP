# Algorithm

## How to configure for exploration
1. change the realRun to be true in simulator.Simulator.java.

```java
private static final boolean realRun = true;
```

2. configure the gRPC server host in config.GrpcConst.java by changing the 
CLIENT_HOST to be the IP address of the server. You need to replace `xxx.xxx.xxx.xxx`
below with the real IP address of the rpi.

```java
public static final String CLIENT_HOST = "xxx.xxx.xxx.xxx";
```

3. Run the main funciton in Simulator.java