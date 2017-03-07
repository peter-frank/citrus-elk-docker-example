package org.tnobody.citruselk;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.dsl.builder.HttpClientRequestActionBuilder;
import com.consol.citrus.dsl.testng.TestNGCitrusTestDesigner;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.message.MessageType;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * org.tnobody.citruselk
 *
 * @author Tim Keiner
 * @since 2017-03-02
 */
@Test
public class ElkTestIT extends TestNGCitrusTestDesigner {

    @Autowired
    private HttpClient elasticClient;

    @Test
    @CitrusTest(name = "Test Connection")
    public void testConnection() {
        description("Just a setup test");

        http()
                .client(elasticClient)
                .send()
                .get("/")
                .accept("application/json")
        ;

        http()
                .client(elasticClient)
                .receive()
                .response(HttpStatus.OK)
                .messageType(MessageType.JSON)
        ;

    }

    @Test
    @CitrusTest(name = "Test Index creation")
    public void testIndexCreation() {
       request(elasticClient, "my-index")
                .accept("application/json")
        ;

        http()
                .client(elasticClient)
                .receive()
                .response(HttpStatus.OK)
                .messageType(MessageType.JSON)
        ;
    }

    @Test
    @CitrusTest(name = "Test Apache Logs")
    public void testApacheLog() {
        sleep(10000);

        request(elasticClient, "/my-index/_count");

        http()
                .client(elasticClient)
                .receive()
                .response(HttpStatus.OK)
                .messageType(MessageType.JSON)
                .validate("$.count", "1000")
                ;
    }


    private HttpClientRequestActionBuilder request(HttpClient client, String path) {
        return http().client(client).send().get(path);
    }
}
