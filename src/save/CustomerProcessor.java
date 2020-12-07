package save;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;
import java.util.function.Function;

public class CustomerProcessor {
    private static final Map<String, Function<Parameter<JsonObject>, JsonObject>> FUNCTIONS = new java.util.HashMap<>();

    static {
        FUNCTIONS.put("getCustomers", arg -> {
            JsonArrayBuilder builder = Json.createArrayBuilder();
            String query = "SELECT"
                    + " s.ID"
                    + ",s.Name"
                    + ",DATE_FORMAT(s.BirthDate,'%d-%m-%Y')"
                    + ",s.City"
                    + " FROM Customers";
            try(ResultSet result = arg.C.createStatement().executeQuery(query)) {
                System.out.println(result);
                if(result.first())
                    do {
                        builder.add(
                                Json.createObjectBuilder()
                                        .add("ID", result.getLong(1))
                                        .add(Customer.NAME, result.getString(2))
                                        .add(Customer.BIRTH_DATE, result.getString(3))
                                        .add(Customer.CITY, result.getInt(4))
                        );
                    } while(result.next());
            } catch(Throwable e) {
                e.printStackTrace();
            }
            return Json.createObjectBuilder().add("data", builder).build();
        });
        FUNCTIONS.put("addCustomer", arg -> {
            String query = "INSERT INTO Customers(Name, BirthDate, City) VALUES(?,STR_TO_DATE(?,'%d/%m/%Y'),?)";
            String text = "";
            int code = 500;
            try(PreparedStatement pS = arg.C.prepareStatement(query)) {
                pS.setString(1, arg.T.getString(Customer.NAME));
                pS.setString(2, arg.T.getString(Customer.BIRTH_DATE));
                pS.setString(3, arg.T.getString(Customer.CITY));
                System.out.println(">> " + pS);
                if(pS.executeUpdate() > 0) {
                    code = 200;
                    text = "OK";
                }
            } catch(Throwable e) {
                e.printStackTrace();
                text = "Internal Server Error";
            }
            return Json.createObjectBuilder()
                    .add("code", code)
                    .add("text", text)
                    .build();
        });
    }

    public static final class Parameter<T> {
        public final Connection C;
        public final T T;

        public Parameter(Connection c, T t) {
            C = c;
            T = t;
        }
    }

    private CustomerProcessor() {}
}