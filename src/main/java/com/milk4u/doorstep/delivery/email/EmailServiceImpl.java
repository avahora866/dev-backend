package com.milk4u.doorstep.delivery.email;

import com.sun.deploy.net.cookie.CookieHandler;
import com.sun.deploy.net.offline.OfflineHandler;
import com.sun.deploy.net.proxy.BrowserProxyConfig;
import com.sun.deploy.net.proxy.ProxyHandler;
import com.sun.deploy.security.BrowserAuthenticator;
import com.sun.deploy.security.CertStore;
import com.sun.deploy.security.CredentialManager;
import com.sun.deploy.security.DeployKeyStore;
import com.sun.deploy.services.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.security.SecureRandom;

@Component
public class EmailServiceImpl{

    @Autowired
    private JavaMailSender emailSender;

    @RequestMapping(path="/sendInvoice", method = RequestMethod.POST)
    public void sendSimpleMessage(
            String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("abraarv7@gmail.com");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        emailSender.send(message);
    }
}
