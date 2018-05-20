
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.swing.JOptionPane;

/**
 *
 * @author a80052136
 */
public class SendEmail {
    public static String sendEmail(String summaryStr, String subcon, 
            ArrayList<String> subconEmail, String senderEmail, String password, 
            String username) {
        try{
            String host ="smtpscn.huawei.com" ;
            String user = username;
            String pass = password;
            // Recipient's email ID
            ArrayList<String> to = subconEmail;
            // Sender's email ID
            String from = senderEmail;
            // cc's email ID
            String cc = senderEmail;
            String subject = "BIS Report Submission Status";
            String messageText = summaryStr;
            boolean sessionDebug = false;

            Properties props = System.getProperties(); // to set different type of properties

            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", host);
            props.put("mail.smtp.port", "587");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.required", "true");
            props.put("mail.smtp.ssl.trust", "smtpscn.huawei.com");

            java.security.Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
            Session mailSession = Session.getDefaultInstance(props, null);
            mailSession.setDebug(sessionDebug);
            
            // Create a default MimeMessage oject
            Message msg = new MimeMessage(mailSession);
            
            // Set From: heder field of the header
            msg.setFrom(new InternetAddress(from));
            
            // Set To: header filed of the header
            InternetAddress[] address = new InternetAddress[to.size()]; // address of sender
            for (int i = 0; i < to.size(); i++) {
                address[i] = new InternetAddress(to.get(i));
            }
            msg.setRecipients(Message.RecipientType.TO, address); // receiver email
            InternetAddress[] address2 = {new InternetAddress(cc)}; // address of sender
            msg.setRecipients(Message.RecipientType.CC, address2); // receiver email
            
            // Set Subject: header field
            msg.setSubject(subject); 
            msg.setSentDate(new Date()); // message send date
            
            // Create the message part
            BodyPart msgBodyPart = new MimeBodyPart();
            
            // Now set the actual message
            msgBodyPart.setText(messageText);
            
            // Create a multipar message
            Multipart multipart = new MimeMultipart();
            
            // Set text message part
            multipart.addBodyPart(msgBodyPart);
            
            // Part two is attachment
            msgBodyPart = new MimeBodyPart();
            String filename = System.getProperty("user.dir") + "\\" + 
                    subcon + ".xlsx";
            DataSource source = new FileDataSource(filename);
            msgBodyPart.setDataHandler(new DataHandler(source));
            msgBodyPart.setFileName(source.getName());
            //msgBodyPart.setFileName(filename);
            multipart.addBodyPart(msgBodyPart);

            // Send the complete message parts
            msg.setContent(multipart);
                       
            //msg.setText(messageText); // actual message
            
            System.out.println(msg.getAllRecipients());

           Transport transport=mailSession.getTransport("smtp"); //server through which we are going to send msg
           transport.connect(host, user, pass); // we need because we have to authenticate sender email and password
           transport.sendMessage(msg, msg.getAllRecipients());
           transport.close();
           System.out.println("message send successfully");
           return "successful";
        }
        catch(javax.mail.AuthenticationFailedException e) {
            return "failed";
        }
        catch(Exception ex)
        {
            System.out.println(ex); // if any error occur then error message will print
        }
        return "unsuccessful";
    } 
}
