import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeMap;

public class CVSVaccineFinder
{
	ArrayList<String> targetCities;
	TreeMap<String, Boolean> entries;
	String textMessage = "";
	boolean foundMatch;
	
	private static final String PHONE_NUMBER = "+PHONE_NUMBER";
	
	CVSVaccineFinder(String[] targetCities)
	{
		this.targetCities = new ArrayList<>();
		this.entries = new TreeMap<>();
		this.foundMatch = false;

		for (String city : targetCities)
		{
			this.targetCities.add(city);
		}
	}
	
	CVSVaccineFinder()
	{
		this.targetCities = new ArrayList<>();
		this.entries = new TreeMap<>();
		this.foundMatch = false;
	}
	
	void addTargetCities(String[] targetCities)
	{
		for (String city : targetCities)
		{
			this.targetCities.add(city);
		}
	}
	void addTargetCity(String targetCity)
	{
		this.targetCities.add(targetCity);
	}
	
	
	void parse()
	{
		URL url;

        try {
            // get URL content

        	String stateCode = "CA";
            String a="https://www.cvs.com/immunizations/covid-19-vaccine/immunizations/covid-19-vaccine.vaccine-status." + stateCode + ".json?vaccineinfo";
            url = new URL(a);
            URLConnection conn = url.openConnection();

            // open the stream and put it into BufferedReader
            BufferedReader br = new BufferedReader(
                               new InputStreamReader(conn.getInputStream()));

            String inputLine;
            String output = null;
            while ((inputLine = br.readLine()) != null)
            {
            	output+= inputLine;
            }
        	

            String time = output.substring(output.indexOf("T", output.indexOf("T") + 7) + 1, output.indexOf("."));
            String date = output.substring(output.indexOf("Time") + 7, output.indexOf("T", output.indexOf("T") + 7));

            
            String s = output.substring(output.indexOf("CA") + 5);
            while (!s.substring(1,10).equals("isBooking"))
            {
            	String city = s.substring(s.indexOf("city") + 7, s.indexOf(",") - 1);
            	s = s.substring(s.indexOf(",") + 1);
            	String state = s.substring(s.indexOf("state") + 8, s.indexOf(",") - 1);
            	s = s.substring(s.indexOf(",") + 1);
            	String statusString = s.substring(s.indexOf("status") + 9, s.indexOf(",") - 2);
            	s = s.substring(s.indexOf(",") + 1);
            	
            	boolean status = false;
            	if (statusString.equals("Available"))
            	{
            		status = true;
            	}	
            	entries.put(city, status);
            	
            }

            textMessage += "....\n";
            textMessage += "\nVaccines Found in target regions\n";  
            
            for (String cityString : targetCities)
            {
            	if (entries.containsKey(cityString.toUpperCase()) && entries.get(cityString.toUpperCase()))
    			{
            		textMessage += cityString + "\n";
            		foundMatch = true;
    			}
            } 
            
            if (!foundMatch)
            {
                textMessage += "\nNo Vaccines Available\n";
            }

            
            textMessage+="\nLast Updated\n";
            textMessage+=date + "\n";
            textMessage+=time + "\n";
       

            br.close();
            
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
	}
	
	void run() throws InterruptedException
	{
		while (true)
		{
			parse();
			showAvailable();
			printSMS();
			if (foundMatch)
			{
				sendSMS();
			}
			showAll();
			
			entries.clear();
			textMessage = "";
			foundMatch = false;
			int minutes = 15;
			int seconds = minutes * 60;
			int ms = seconds * 1000;
			Thread.sleep(ms);
		}
	}
	
	public static void main(String[] args)
	{
		CVSVaccineFinder finder = new CVSVaccineFinder();
		finder.addTargetCity("San Diego");
		finder.addTargetCity("Poway");
		finder.addTargetCity("Rancho Bernardo");
		
		try {
			finder.run();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
    }
	
	void printSMS()
	{
		System.out.println(textMessage);
	}
	
	void sendSMS()
	{
		TwilioSMS.sendSMS(textMessage, PHONE_NUMBER);
	}
	
	void showAvailable()
	{
		Iterator<String> iterator = entries.keySet().iterator();
		while(iterator.hasNext())
		{
			String entry = iterator.next();
			if (entries.get(entry))
			{
        		System.out.println(entry + " - Available");
			}
		}
	}
	
	void showAll()
	{
		Iterator<String> iterator = entries.keySet().iterator();
		while(iterator.hasNext())
		{
			String entry = iterator.next();
			if (entries.get(entry))
			{
        		System.out.println(entry + " - Available");
			}
			else
			{
				System.out.println(entry + " - Not Available");
			}
		}
	}

}


class cityEntry
{
	String state;
	String city;
	boolean available;
	public cityEntry(String state, String city, boolean available)
	{
		this.state = state;
		this.city = city;
		this.available = available;
	}
	
	@Override
	public String toString()
	{
    	return ("State: " + state + ", City: " + city + ", status: " + available);
	}

	public String getCity() {
		return city;
	}

	public boolean isAvailable() {
		return available;
	}
}

