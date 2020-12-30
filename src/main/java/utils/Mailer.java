package utils;
import java.util.Properties;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Session;
import javax.mail.Transport;
import javax.activation.FileDataSource;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import common.GlobalConfigHandler;
import config.Constants;


public class Mailer {

    Session session = null;
    Message message;
    InternetAddress[] myToList;
    InternetAddress[] myBccList;
    InternetAddress[] myCcList;


    public void createSession() {
        
        Properties prop = new Properties();
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.starttls.enable", "true");
        prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.port", "587");
        prop.put("mail.smtp.socketFactory.port", "587");
        prop.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        prop.put("mail.smtp.connectiontimeout","5000");
        prop.put("mail.smtp.timeout", "25000");
        
        session = Session.getInstance(prop, new javax.mail.Authenticator() { 
          protected PasswordAuthentication getPasswordAuthentication() {                      
              return new PasswordAuthentication(Constants.EMAILER_EMAIL,Constants.EMAILER_PWD); 
          } 
        });
    }

    private void messageContent(String name, String file_name, String scope){
        String subject = getEmailSubject(name);
        Multipart multipart = new MimeMultipart();

        try {
            if(session != null){
                message = new MimeMessage(session);
                message.setFrom(new InternetAddress(Constants.EMAILER_EMAIL));
                message.addRecipients(Message.RecipientType.TO,myToList);
                message.addRecipients(Message.RecipientType.BCC,myBccList);
                message.addRecipients(Message.RecipientType.CC,myCcList);
                message.setSubject(subject);
                BodyPart messageBodyPart1 = new MimeBodyPart();
                StringBuilder sb = new StringBuilder();
                sb.append("Hi Team, \n\nGreetings of the day! \n\n");
                sb.append("This email was generated from GGM Api Test Suites. \n\n");
                sb.append("API/Suite Name : "+name+"\n\n");
                sb.append(scope+"\n\n");
                sb.append("Please download the attachment for detailed information.");
                messageBodyPart1.setText(sb.toString());
                BodyPart messageBodyPart2 = new MimeBodyPart();
                messageBodyPart2.setText("\n\nBest Regards, \nUmesh Shukla"/*\nGGM Search & Reco QA"*/);
                // zf.createZip();
                DataSource source = new FileDataSource("./"+Constants.CUSTOM_REPORT_FOLDER+"/Runtime/"+file_name);
                BodyPart messageBodyPart3 = new MimeBodyPart();
                messageBodyPart3.setDataHandler(new DataHandler(source));
                messageBodyPart3.setFileName(file_name);
                multipart.addBodyPart(messageBodyPart1);
                multipart.addBodyPart(messageBodyPart2);
                multipart.addBodyPart(messageBodyPart3);
                message.setContent(multipart);
            }
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    private void recipientList() {
        try {
            myToList = InternetAddress.parse(Constants.EMAIL_TO_LIST);
            myBccList = InternetAddress.parse(Constants.EMAIL_BCC_LIST);
            myCcList = InternetAddress.parse(Constants.EMAIL_CC_LIST);
        } catch (AddressException e) {
            e.printStackTrace();
        }
    }

    public void sendEmail(String api_name, String file_name, String scope){
        createSession();
        if(session != null){
            try {
                recipientList();
                messageContent(api_name, file_name, scope);
                Transport.send(message);
                System.out.println("Report Successfully Sent To Receipents.");
            } catch (MessagingException e) {
                System.out.println("Email sending failed.");
                throw new RuntimeException(e);
            }
        }
    }

    private static String getEmailSubject(String name){
        String env = GlobalConfigHandler.getEnv();
        if(env.equalsIgnoreCase("local")){
            env = "Staging";
        }else if(env.equalsIgnoreCase("prod")){
            env = "Production";
        }

        String type = GlobalConfigHandler.getType();

        return env+" | "+ type +" | "+ name +" Api Test Suite Execution Report (Compared Data) | "+CommonUtils.getCurrentDateTime();
    }
}