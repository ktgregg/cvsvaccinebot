import com.twilio.Twilio;
import com.twilio.exception.AuthenticationException;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

public class TwilioSMS { 

	
  private static final String ACCOUNT_SID = "AccountSID";
  private static final String AUTH_TOKEN = "AuthToken";

  
  public static void sendSMS(String str, String phoneNumber) throws AuthenticationException
  {
	  Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
	     
     String s = "San Diego: Available";
     
     Message message = Message.creator(new PhoneNumber(phoneNumber),
    	        new PhoneNumber("+TwilioSMSNumber"), str).create();

    	    System.out.println(message.getSid());
  }
} 