package daveiiii.simpleFactions.data.namedQueries;

public class BaseQuery {
    public static String createQuery(String[] queryItems) {
        return String.join(" ", queryItems);
    }
}
