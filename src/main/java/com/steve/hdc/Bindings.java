package com.steve.hdc;

import express.DynExpress;
import express.http.RequestMethod;
import express.http.request.Request;
import express.http.response.Response;
import express.utils.Status;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;


public class Bindings {

    @DynExpress() // Default is context="/" and method=RequestMethod.GET
    public void getIndex(Request req, Response res) {
        res.send("Hello World!");
    }

    @DynExpress(context = "/about") // Only context is defined, method=RequestMethod.GET is used as method
    public void getAbout(Request req, Response res) {
        res.send("About page");
    }

    @DynExpress(context = "/message") // Only context is defined, method=RequestMethod.GET is used as method
    public void getMsg(Request req, Response res) {
        Message msg = new Message("mike", "anish", "hello");

        res.send("Your msg: " + msg.getSender());
    }

    @DynExpress(context = "/impressum", method = RequestMethod.PATCH) // Both defined
    public void getImpressum(Request req, Response res) {
        res.send("Impressum page was patched");
    }

    @DynExpress(context = "/message", method = RequestMethod.POST) // Only the method is defined, "/" is used as context
    public void postMessage(Request req, Response res){
        InputStream stream = req.getBody();
        String body = null;
        try {
            body = IOUtils.toString(stream, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        res.setStatus(Status._201);
        res.send("Input was:" + body);
    }


}
