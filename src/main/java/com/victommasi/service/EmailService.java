package com.victommasi.service;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.victommasi.model.Customer;
import com.victommasi.repository.CustomerRepository;
import com.victommasi.wrapper.EmailWrapper;

@Service
public class EmailService {
	
	@Value("${emailUser}")
	String username;
	
	@Value("${emailPassword}")
    String password;

	@Autowired
	CustomerRepository customerRepository;
	
	private String setEmailContent(EmailWrapper emailWrapper) {
		Integer[] ids = emailWrapper.getIds();
		StringBuffer content = new StringBuffer();
		int cont = 1;
		
		content.append("CLIENTES PENDENTES:");
		content.append("\n");
		content.append("\n");
		for(Integer id : ids){
			Customer customer = customerRepository.findOne(id);
			content.append(cont + ".");
			content.append("	Nome: " + customer.getName());
			content.append("\n");
			content.append("	Telefone: " + customer.getPhone());
			content.append("\n");
			content.append("	Assunto: " + customer.getNote());
			content.append("\n\n");
			cont++;
		}
		return content.toString();
	}
	    
	public void exportToEmail(EmailWrapper emailWrapper) {
		String emailContent = setEmailContent(emailWrapper); 
		
		Properties props = new Properties();
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props,
          new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
          });
        
        try {
        	 Message msg = new MimeMessage(session);
        	 msg.setFrom(new InternetAddress("escritoriojevi@gmail.com"));
        	 msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(emailWrapper.getEmail()));
        	 msg.setSubject("Clientes pendentes - JEVI");
        	 msg.setText(emailContent);
             Transport.send(msg);

             System.out.println("Sent");
        }
        catch (MessagingException ex) {
             System.err.println(ex.getMessage());
        }
	}
}