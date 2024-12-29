import org.abx.ws.annotations.WSMethod;
import org.abx.ws.annotations.WSService;

public class ClientReq implements WSService {

    @WSMethod(params = {"a","b"})
    public double multiply(double a, double b) {
        return a * b;
    }
}
