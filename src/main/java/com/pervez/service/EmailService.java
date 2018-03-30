package com.pervez.service;

import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.pervez.dao.EmailDao;
import com.pervez.entity.EmailEntity;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

@Service
@EnableScheduling
public class EmailService {

	@Autowired
	EmailDao emailDao;
	
	public void createEmail(String to, String subject, String content){
		EmailEntity emailEntity = new EmailEntity();
		emailEntity.setTo(to);
		emailEntity.setSubject(subject);
		emailEntity.setContent(content);
		emailEntity.setRetryCount(0);
		emailEntity.setIsSent(false);
		
		emailDao.saveOrUpdate(emailEntity);
	}
	
	
	@Scheduled(fixedDelay = 30000)
	private void emailScheduler(){
		
		List<EmailEntity> emailEntities = emailDao.getUnsentEmail();
		for(EmailEntity emailEntity : emailEntities){
			Thread t = new Thread(new Runnable() {
				
				@Override
				public void run() {
					
					try{
						sendHtmlEmail(emailEntity);
					}catch (Exception e) {
						emailEntity.setRetryCount(emailEntity.getRetryCount()+1);
						emailDao.saveOrUpdate(emailEntity);
					}
				}
			});
			
			t.start();
		
		}
	}
	
	
	private synchronized void sendHtmlEmail(EmailEntity emailEntity) throws AddressException, MessagingException {
		String toAddress = emailEntity.getTo();
		String subject = emailEntity.getSubject();
		String message = emailEntity.getContent();
		// sets SMTP server properties
			String host = null;
			String port = null;
			String mailFrom = null;
			String password = null;
		
	        Properties properties = new Properties();
	        properties.put("mail.smtp.host", host);
	        properties.put("mail.smtp.port", port);
	        properties.put("mail.smtp.auth", "true");
	        properties.put("mail.smtp.starttls.enable", "true");
	 
	        // creates a new session with an authenticator
	        Authenticator auth = new Authenticator() {
	            public PasswordAuthentication getPasswordAuthentication() {
	                return new PasswordAuthentication(mailFrom, password);
	            }
	        };
	 
	        Session session = Session.getInstance(properties, auth);
	 
	        // creates a new e-mail message
	        Message msg = new MimeMessage(session);
	        msg.setFrom(new InternetAddress(mailFrom));
	        InternetAddress[] toAddresses = { new InternetAddress(toAddress) };
	        msg.setRecipients(Message.RecipientType.TO, toAddresses);
	        msg.setSubject(subject);
	        msg.setSentDate(new Date());
	        
	        // set plain text message
	        //msg.setContent(message, "text/html");
	        
	        
	        Multipart mp = new MimeMultipart();

	        MimeBodyPart mbp1 = new MimeBodyPart();
	        mbp1.setContent(message,"text/html");
	        mp.addBodyPart(mbp1);

	        /*if(filename!=null)
	        {
	            MimeBodyPart mbp2 = null;
	            FileDataSource fds =null;
	            for(int counter=0;counter<filename.length;counter++)
	            {
	            	try{
	                mbp2 = null;
	                fds =null;
	                mbp2=new MimeBodyPart();
	                
	                String fname = filename[counter];
	                
	                String fpath = FileUtility.getRootPath()+"/emailAttachments/"+fname;
	                File f = new File(fpath);
	                System.out.println(f.getAbsolutePath());
	                fds = new FileDataSource(f);
	                mbp2.setDataHandler(new DataHandler(fds));
	                mbp2.setFileName(fds.getName());
	                mp.addBodyPart(mbp2);
	            	}catch(Exception e){
	            		e.printStackTrace();
	            	}
	            }
	        }*/
	        msg.setContent(mp);
	        // sends the e-mail
	        
	        Transport.send(msg);
	        
	        emailEntity.setIsSent(true);
	        emailDao.saveOrUpdate(emailEntity);

}
}
