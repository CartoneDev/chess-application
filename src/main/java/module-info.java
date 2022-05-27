module com.chess {
    requires javafx.fxml;
    requires javafx.controls;
    requires org.apache.commons.io;
    requires org.apache.logging.log4j;
    requires lombok;
    requires java.logging;

    opens com.chess.application to javafx.fxml;
    exports com.chess.application;
}

