package ai.nettogrof.battlesnake.snakes.common;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import spark.Filter;
import spark.Request;
import spark.Response;
import spark.Spark;

/**
 * This class is used by the "webserver" to accept use CORS header to let a https webpage to call snakes directly
 * Really simple helper for enabling CORS in a spark application;
 * 
 */
public final class CorsFilter /*implements Apply*/{

    private final static Map<String, String> corsHeaders = new ConcurrentHashMap<>();

    private CorsFilter() {
        corsHeaders.put("Access-Control-Allow-Methods", "GET,PUT,POST,DELETE,OPTIONS");
        corsHeaders.put("Access-Control-Allow-Origin", "*");
        corsHeaders.put("Access-Control-Allow-Headers", "Content-Type,Authorization,X-Requested-With,Content-Length,Accept,Origin,");
        corsHeaders.put("Access-Control-Allow-Credentials", "true");
    }

   
    public static void apply() {
        final Filter filter = new Filter() {
            @Override
            public void handle(final Request request,final Response response) throws Exception {
                corsHeaders.forEach((key, value) -> {
                    response.header(key, value);
                });
            }
        };
        Spark.after(filter);
    }
}
