package api;

import javax.sql.rowset.serial.SerialException;
import java.util.List;

public interface Service {
    List<Result> search(Request request) throws Exception;
}
