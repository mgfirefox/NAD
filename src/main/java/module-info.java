open module by.bsuir.nad {
    requires javafx.controls;
    requires javafx.fxml;
    requires static lombok;
    requires com.google.gson;
    requires jakarta.persistence;
    requires org.hibernate.orm.core;
    requires java.naming;
    requires com.google.common;
    requires spring.context;
    requires org.checkerframework.checker.qual;

    exports by.bsuir.nad.client.gui.controller;
    exports by.bsuir.nad.client.gui.scene;
    exports by.bsuir.nad.client.gui.scene.loader;
    exports by.bsuir.nad.client.main;
    exports by.bsuir.nad.client.model;
    //exports by.bsuir.nad.client.model.pojo;

    exports by.bsuir.nad.gson;

    exports by.bsuir.nad.server.db.dao;
    exports by.bsuir.nad.server.db.entity;
    exports by.bsuir.nad.server.db.exception;
    exports by.bsuir.nad.server.main;
    exports by.bsuir.nad.server.db.service;

    exports by.bsuir.nad.tcp;
}
