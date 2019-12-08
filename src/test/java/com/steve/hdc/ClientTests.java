package com.steve.hdc;

import com.steve.hdc.client.HDCClient;
import org.junit.Test;

import javax.ws.rs.core.Response;

import static junit.framework.Assert.assertEquals;

public class ClientTests {
    public static final int HTTP_CREATED = 200;
    private HDCClient client = new HDCClient();

    @Test
    public void givenCorrectObject_whenCorrectJsonRequest_thenResponseCodeCreated() {
        Message msg = new Message("anish", "mike", "hello!");

        Response response = client.sendMessage(msg);

        String body = response.readEntity(String.class);

        assertEquals(body, "hehehe");
        assertEquals(response.getStatus(), HTTP_CREATED);
    }
}
