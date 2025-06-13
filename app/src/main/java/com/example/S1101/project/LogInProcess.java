/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.S1101.project;

import Interface.ICSVReader;
import Interface.ILogInProcess;
import Objects.models.Credentials;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author eyell
 */
public class LogInProcess implements ILogInProcess {

    private final ICSVReader csvreader;

    public LogInProcess(ICSVReader _csvreader) {
        this.csvreader = _csvreader;
    }

    public Credentials performLogin(String username, String password) {
        List<Credentials> credData = csvreader.Getcred();
        return credData.stream()
                .filter(user -> user.getUserName().equals(username) && user.getPassword().equals(password))
                .findFirst()
                .orElse(null);
    }

    public void redirect(String deptCode) {

        switch (deptCode) {
            case "PRS" -> {
                LoginFrame landingPage = new LoginFrame();
                landingPage.setVisible(true);
            }
            default -> {}

        }
    }
}
