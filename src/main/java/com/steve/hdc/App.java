package com.steve.hdc;

import express.Express;

public class App {
    public static void main(String[] args) {
        Express app = new Express();
        app.bind(new Bindings()); // See class below
        app.listen(8082);
    }
}
