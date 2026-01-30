package com.household.inventory.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    // ✅ Login alert email
    public void sendLoginAlert(String toEmail, String username) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Login Alert - Household Inventory");
        message.setText(
                "Hello " + username + ",\n\n" +
                        "You have successfully logged into your Household Inventory account.\n\n" +
                        "If this was not you, please reset your password.\n\n" +
                        "— Inventory App"
        );

        mailSender.send(message);
    }

    // ✅ Low stock email
    public void sendLowStockAlert(String toEmail, String username, String itemName, int qty, int minQty) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Low Stock Alert - " + itemName);
        message.setText(
                "Hello " + username + ",\n\n" +
                        "Low stock alert for: " + itemName + "\n" +
                        "Current quantity: " + qty + "\n" +
                        "Minimum threshold: " + minQty + "\n\n" +
                        "Please restock soon.\n\n" +
                        "— Household Inventory"
        );

        mailSender.send(message);
    }

    // ✅ Expiry alert email (NEW)
    public void sendExpiryAlert(String toEmail, String username, String itemName, String status, String expiryDate) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Expiry Alert - " + itemName);
        message.setText(
                "Hello " + username + ",\n\n" +
                        "Expiry alert for: " + itemName + "\n" +
                        "Status: " + status + "\n" +
                        "Expiry Date: " + expiryDate + "\n\n" +
                        "Please use or replace it soon.\n\n" +
                        "— Household Inventory"
        );

        mailSender.send(message);
    }
}
