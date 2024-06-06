package com.example.proyectogrupo4_gtics.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender emailSender;

    public void sendEmailWithAttachment(String to, String subject, String text, String pathToAttachment) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(text);

        FileSystemResource file = new FileSystemResource(new File(pathToAttachment));
        helper.addAttachment("Image", file);

        emailSender.send(message);
    }


    public void sendHtmlMessage(String to, String subject, String name, String password) throws MessagingException, IOException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        // Carga de la plantilla HTML desde los recursos
        var resource = new ClassPathResource("email.html");
        String html = new String(StreamUtils.copyToByteArray(resource.getInputStream()), StandardCharsets.UTF_8);

        // Reemplazar marcadores de posición en la plantilla
        html = html.replace("{{name}}", name);
        html = html.replace("{{password}}", password);

        helper.setFrom("syong7350@gmail.com");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(html, true); // true indica que el mensaje es HTML

        ClassPathResource bannerResource = new ClassPathResource("static/assets_superAdmin/img/banner.png");
        helper.addInline("bannerImage", bannerResource);

        emailSender.send(message);
    }


    public void sendHtmlRechazo(String to, String subject, String name,String motivo) throws MessagingException, IOException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        // Carga de la plantilla HTML desde los recursos
        var resource = new ClassPathResource("emailRechazo");
        String html = new String(StreamUtils.copyToByteArray(resource.getInputStream()), StandardCharsets.UTF_8);

        // Reemplazar marcadores de posición en la plantilla
        html = html.replace("{{name}}", name);
        html = html.replace("{{motivo}}", motivo);

        helper.setFrom("syong7350@gmail.com");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(html, true); // true indica que el mensaje es HTML

        ClassPathResource bannerResource = new ClassPathResource("static/assets_superAdmin/img/banner.png");
        helper.addInline("bannerImage", bannerResource);

        emailSender.send(message);
    }


    public void sendHtmlBanDele(String to, String subject, String name,String titulo,String texto) throws MessagingException, IOException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        // Carga de la plantilla HTML desde los recursos
        var resource = new ClassPathResource("emailRechazo");
        String html = new String(StreamUtils.copyToByteArray(resource.getInputStream()), StandardCharsets.UTF_8);

        // Reemplazar marcadores de posición en la plantilla
        html = html.replace("{{name}}", name);
        html = html.replace("{{titulo}}", titulo);
        html = html.replace("{{texto}}",texto);

        helper.setFrom("syong7350@gmail.com");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(html, true); // true indica que el mensaje es HTML

        ClassPathResource bannerResource = new ClassPathResource("static/assets_superAdmin/img/banner.png");
        helper.addInline("bannerImage", bannerResource);

        emailSender.send(message);
    }

    public void sendHtmlEditAdmin(String to, String subject, String name,String lastName,String site,String email) throws MessagingException, IOException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        // Carga de la plantilla HTML desde los recursos
        var resource = new ClassPathResource("emailRechazo");
        String html = new String(StreamUtils.copyToByteArray(resource.getInputStream()), StandardCharsets.UTF_8);

        // Reemplazar marcadores de posición en la plantilla
        html = html.replace("{{name}}", name);
        html = html.replace("{{apellido}}", lastName);
        html = html.replace("{{sede}}", site);
        html = html.replace("{{correo}}", email);


        helper.setFrom("syong7350@gmail.com");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(html, true); // true indica que el mensaje es HTML

        ClassPathResource bannerResource = new ClassPathResource("static/assets_superAdmin/img/banner.png");
        helper.addInline("bannerImage", bannerResource);

        emailSender.send(message);
    }


    public void sendHtmlEditPharma(String to, String subject, String name,String lastName,String site,String email,String distrit) throws MessagingException, IOException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        // Carga de la plantilla HTML desde los recursos
        var resource = new ClassPathResource("emailRechazo");
        String html = new String(StreamUtils.copyToByteArray(resource.getInputStream()), StandardCharsets.UTF_8);

        // Reemplazar marcadores de posición en la plantilla
        html = html.replace("{{name}}", name);
        html = html.replace("{{apellido}}", lastName);
        html = html.replace("{{sede}}", site);
        html = html.replace("{{correo}}", email);
        html = html.replace("{{distrito}}", distrit);

        helper.setFrom("syong7350@gmail.com");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(html, true); // true indica que el mensaje es HTML

        ClassPathResource bannerResource = new ClassPathResource("static/assets_superAdmin/img/banner.png");
        helper.addInline("bannerImage", bannerResource);

        emailSender.send(message);
    }



    public void sendSimpleMessage(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("syong7350@gmail.com");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        emailSender.send(message);
    }



}
