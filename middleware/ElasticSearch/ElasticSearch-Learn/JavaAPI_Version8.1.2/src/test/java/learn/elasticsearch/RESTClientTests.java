package learn.elasticsearch;

import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.RequestLine;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Arrays;

@SpringBootTest
public class RESTClientTests {
    private static final Logger logger = LoggerFactory.getLogger(RestClientTest.class);
    @Resource
    private RestClient client;

    @Test
    void responseRead() throws IOException {
        Response response = client.performRequest(new Request("GET", "/test1/_search"));
        RequestLine requestLine = response.getRequestLine();
        logger.info("RequestLine: {}", requestLine); // GET /test1/_search HTTP/1.1
        HttpHost host = response.getHost();
        logger.info("Host: {}", host); // http://127.0.0.1:9200
        int statusCode = response.getStatusLine().getStatusCode();
        logger.info("StatusCode: {}", statusCode); // 200
        Header[] headers = response.getHeaders();
        logger.info("Headers: {}", Arrays.toString(headers)); // [X-elastic-product: Elasticsearch, content-type: application/json, content-length: 924]
        String responseBody = EntityUtils.toString(response.getEntity());
        logger.info("ResponseBody: {}", responseBody); // JSON 数据
    }
}
