package com.steve.hdc.client;

import com.steve.hdc.Message;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class HDCClient {
    private static final String REST_URI = "http://localhost:8082/message";
    private Client client = ClientBuilder.newClient();
    private User user;

    // get messages after time
    public HDCClient(User usr) {
        this.user = new User();
    }


//    public sendMessage()

    /**
     * A method which attempts to register a new user with the HDC service.
     *
     * @param userName Username of the registering party.
     */
    public Response register(String userName) {
        User newUser = new User(userName);
        return client
                .target(REST_URI)
                .path("/register")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(newUser, MediaType.APPLICATION_JSON));
    }

    public User register(String) {

    }

    public Message getMessages(Message msg, User usr, long timeStamp) {
        return client
                .target(REST_URI)
                .path(String.valueOf(usr.getId()))
                .request(MediaType.APPLICATION_JSON)
                .get(Message.class);
    }

    public Response sendMessage(Receiver rcvr, String textMsg) {
        Message msg = new Message(textMsg);
        return client
                .target(REST_URI)
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(msg, MediaType.APPLICATION_JSON));
    }

}
