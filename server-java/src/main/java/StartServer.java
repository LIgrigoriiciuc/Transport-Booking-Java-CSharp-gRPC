import Network.NetworkGrpcServiceImpl;
import Repository.*;
import Service.*;
import Service.TransactionsLogic.TransactionManager;
import Util.ConnectionHolder;
import Util.DatabaseConnection;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import io.grpc.Server;
import io.grpc.ServerBuilder;

public class StartServer {
    private static final int DEFAULT_PORT = 65535;
    public static void main(String[] args) {
        System.out.println("Working directory: " + System.getProperty("user.dir"));
        int port = loadPort();
        SeatRepository seatRepo = new SeatRepository();
        TripRepository tripRepo = new TripRepository();
        ReservationRepository resRepo = new ReservationRepository();
        UserRepository userRepo = new UserRepository();
        OfficeRepository officeRepo = new OfficeRepository();
        OfficeService officeService = new OfficeService(officeRepo);
        AuthService authService = new AuthService(userRepo, officeService);
        TripService tripService   = new TripService(tripRepo);
        SeatService seatService = new SeatService(seatRepo);
        ReservationService resService = new ReservationService(resRepo, seatService);
        TransactionManager txManager     = new TransactionManager();

        FacadeService facade = new FacadeService(
                authService, tripService, seatService,
                resService, officeService, txManager);

        NetworkGrpcServiceImpl grpcService = new NetworkGrpcServiceImpl(facade);
        Server server = null;
        try {
            server = ServerBuilder
                    .forPort(port)
                    .addService(grpcService)
                    .build()
                    .start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("gRPC server started on port " + port);

        Server finalServer = server;
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            finalServer.shutdown();
            DatabaseConnection.close();
            System.out.println("Server stopped.");
        }));

        try {
            server.awaitTermination();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    private static int loadPort() {
        Properties props = new Properties();
        try (InputStream in = StartServer.class.getClassLoader()
                .getResourceAsStream("server.properties")) {
            if (in != null) props.load(in);
        } catch (IOException ignored) {}
        try {
            return Integer.parseInt(
                    props.getProperty("server.port", String.valueOf(DEFAULT_PORT)));
        } catch (NumberFormatException e) {
            return DEFAULT_PORT;
        }
    }
}


