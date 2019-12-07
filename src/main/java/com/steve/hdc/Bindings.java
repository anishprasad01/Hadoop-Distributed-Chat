package com.steve.hdc;

import express.DynExpress;
import express.http.RequestMethod;
import express.http.request.Request;
import express.http.response.Response;


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

        res.send("Your msg: " + msg);
    }

    @DynExpress(context = "/impressum", method = RequestMethod.PATCH) // Both defined
    public void getImpressum(Request req, Response res) {
        res.send("Impressum page was patched");
    }

    @DynExpress(method = RequestMethod.POST) // Only the method is defined, "/" is used as context
    public void postIndex(Request req, Response res) {
        res.send("POST to index");
    }
}
