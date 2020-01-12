package webappautomation;

//Selenium and TestNG classes
import org.testng.annotations.Test;
import org.testng.annotations.BeforeTest;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import java.util.Arrays;
import java.util.List;
//Mail read classes
import java.util.Properties;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
//HTML Parser classes
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

 public class Zomato extends GenerateReports
{
	//----------------Variable declaration for automating OTP received on email---------------
     static String protocol = "imap";
     static String host = "imap.gmail.com";
     static String port = "993";
     static String userEmail="YOUR_Email_Address";// change accordingly
	 static String password = "Your_Email_Password";// change accordingly
	 static String OTP;
	//--------------------Variable declaration for  entering user information-----------------------------------------------
	 static String userFullName="";
	 static String userFirstName="";
	 static String userPhoneNo="";
	 static String profilePicFilePath="D:\\Automation\\photos\\2.png";	//----------------------Variable declaration for entering information about the website and selenium,testng---------
	 static String expectedHomePgURL="https://www.zomato.com/mumbai";
	 static String expectedHomePgTitle="Restaurants - Mumbai Restaurants, Restaurants in Mumbai | Zomato India";
	 static String baseURL="https://www.zomato.com/mumbai";
	 static String mealType="Lunch"; //Values can be Breakfast, Lunch,Dinner etc
	 static String restaurantHomePage="https://www.zomato.com/mumbai/barbeque-nation-marol";
	 static String restaurantReview="I have visited this place many times for different occasions and it's  a best for all of them.Food is good.Also the service is very good.For everyday day there is a different menu so that you won't get bored.";
	 static WebDriver driver;
	 static Actions builder;
	 static WebDriverWait wait;
	
	//-Java code for reading the email and extracting OTP from it- .
	private Properties getServerProperties(String protocol, String host,String port) {
        Properties properties = new Properties();
         // server setting
        properties.put(String.format("mail.%s.host", protocol), host);
        properties.put(String.format("mail.%s.port", protocol), port);
        // SSL setting
        properties.setProperty(
                String.format("mail.%s.socketFactory.class", protocol),
                "javax.net.ssl.SSLSocketFactory");
        properties.setProperty(
                String.format("mail.%s.socketFactory.fallback", protocol),
                "false");
        properties.setProperty(
                String.format("mail.%s.socketFactory.port", protocol),
                String.valueOf(port));
        return properties;
    }
    public String readMail(String protocol, String host, String port,
            String userName, String password) 
    {
    	String emailMsgText="";
        Properties properties = getServerProperties(protocol, host, port);
        Session session = Session.getDefaultInstance(properties);
        try {
            // connects to the message store
            Store store = session.getStore(protocol);
            store.connect(userName, password);
 
            // opens the inbox folder
            Folder folderInbox = store.getFolder("INBOX");
            folderInbox.open(Folder.READ_ONLY);
            // fetches new messages from server
           Message[] messages = folderInbox.getMessages();
        // Sort messages from recent to oldest
           Arrays.sort( messages, ( m1, m2 ) -> {
             try {
               return m2.getReceivedDate().compareTo( m1.getReceivedDate() );
             } catch ( MessagingException e ) {
               throw new RuntimeException( e );
               
             }
           } );
    
            for (Message msg : messages) {
                String subject = msg.getSubject();
                try
                {
                if(subject.contentEquals("Log in to Zomato"))
                {
                	emailMsgText=msg.getContent().toString();
                }
                }
                catch(Exception e)
                {
                System.out.println("Error while reading latest email message");
                }
                break;
            }
            // disconnect
            folderInbox.close(false);
            store.close();
        } catch (NoSuchProviderException ex) {
            System.out.println("No provider for protocol: " + protocol);
            ex.printStackTrace();
        } catch (MessagingException ex) {
            System.out.println("Could not connect to the message store");
            ex.printStackTrace();
        }
        return emailMsgText;
    }
	//Java code for getting the OTP from the mail's text  .
	public static void GetOTP(String emailHTMLText) 
	   {
		     
		      String html=emailHTMLText;
		      String href=null;
		 try 
		 {
		   Document document = Jsoup.parse(html);
	      //a with token
	      Elements links = document.select("a[href]");

	      for (Element link : links) 
	      { 
	    	  if(link.attr("href").contains("token"))
	    	  {
	             href=link.attr("href");
	    	  }
	      }
	      //Split the string into parts to obtain the string which contains the OTP
	        String[] parts=href.split("&");
	      //Separate the OTP and the  other  string
	        OTP=parts[1].substring(6); 
          }     
		 catch(Exception e)
		  {
		    	  System.out.println("Exception occured: "+e);
          }
	       
	   }
	
	//--------------------TestNG testcases are as follows:---------------------------------------------------
	@BeforeTest
	  public void ConfigureBrowser() 
	  {
		  System.setProperty("webdriver.chrome.driver", "D:\\Automation\\chromedriver_win32\\chromedriver.exe");
		  driver=new ChromeDriver();
		  driver.get(baseURL);
		  driver.manage().window().maximize();
		  driver.manage().deleteAllCookies();
		  driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
		 
	  }
	
	@Test(priority=0)
	  public void OpenZomatoWebsite()
	  {
		  //verify HomePage
		  String actualHomePgURL=driver.getCurrentUrl();
		  String actualHomePgTitle=driver.getTitle();
		  Assert.assertEquals(actualHomePgURL, expectedHomePgURL);
		  Assert.assertEquals(actualHomePgTitle, expectedHomePgTitle);
	  }
	
	@Test(priority=1)
	  public void RegisterUser() throws InterruptedException 
	  {   
		  String actualRegisterMsg=null;
		  String expectedRegisterMsg="Thank you for signing up!";
		  driver.findElement(By.linkText("Create an account")).click();
		  Thread.sleep(5000);
		  driver.findElement(By.linkText("Sign up")).click();
		  driver.findElement(By.id("sd-fullname")).sendKeys(userFullName);
		  driver.findElement(By.id("sd-email")).sendKeys("iamironman@gmail.com");
		  driver.findElement(By.id("sd-newsletter")).click();
		  driver.findElement(By.id("sd-submit")).click();
		  //Verify either the user got successfully registered or is already registered.
		  actualRegisterMsg=driver.findElement(By.id("sd-error")).getText();
		  if(expectedRegisterMsg.equals(actualRegisterMsg))
		  {
		 
	             //Close registration prompt
			      System.out.println("Hey "+userFirstName +actualRegisterMsg);
	    		  driver.findElement(By.xpath("//*[@id=\"modal-container\"]/i")).click();  //*[@id="sd-error"] 
		  }
		  else
		  {
			//Print actual registration message and close registration prompt
			  System.out.println(actualRegisterMsg+" -Please use different email id");
			  driver.findElement(By.xpath("//*[@id=\"modal-container\"]/i")).click();
		  }
	  }

@Test(priority=2)
public void LoginWithEmail() throws InterruptedException
{   
	  String emailText=null;
   //Click on Log In link  
	  WebDriverWait wait=new WebDriverWait(driver,10);
	  JavascriptExecutor js=(JavascriptExecutor)driver;
	  WebElement signinLink=driver.findElement(By.id("signin-link"));
	  js.executeScript("arguments[0].click();", signinLink);
	  //Click on login button
	  WebElement loginLink=
	 wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"login-email\"]")));
	  loginLink.click();
	  //Enter email 
	  driver.findElement(By.id("ld-email")).sendKeys(userEmail); 
	  //Click on Submit 
	  driver.findElement(By.id("ld-submit-global")).click();  
	  Thread.sleep(5000);
	   //Call the methods to read the email and extract OTP from it
	  emailText=readMail(protocol,host,port,userEmail,password);
	  GetOTP(emailText); 
	  driver.findElement(By.className("verification-code-value")).sendKeys(OTP);
	  Thread.sleep(3000); 
	  driver.findElement(By.xpath("//*[@id=\"ld-login-otp-page\"]/div[2]/a")).click();
	  //Wait to load the page after sucessfull validation of OTP
	  Thread.sleep(9000);
	 //Verify logged in user
	   String loggedinUser=driver.findElement(By.xpath("//*[@id=\"login-navigation\"]/div/div/span")).getText();
	  System.out.println("Logged in User is :"+loggedinUser);
	  Assert.assertTrue(loggedinUser.contains(userFirstName), "LoggedIn User is different");
}

@Test(priority=3)
public static void editUserInformation() throws InterruptedException
{
	  wait=new WebDriverWait(driver,10);
	  String userBioDetails="Hey I am "+userFullName+". I love eating food at Buffets bcoz they offers a lot"
	  		+ " of variety in food and also the food is   unlimited.";
	  String userWebsiteInfo="https://"+userFirstName.toLowerCase()+".com";
	  String userTwitterHandle="iAm"+userFirstName;
	  //Go to Settings option to edit user details
	  builder=new Actions(driver);
	  WebElement userPanel=driver.findElement(By.xpath("//*[@id=\"login-navigation\"]/div/div/span"));
	  Action clickUserPanel=builder.moveToElement(userPanel).click().build();
	  clickUserPanel.perform();
	  Thread.sleep(10000);
	  WebElement settings=driver.findElement(By.linkText("Settings"));
	  Action clickUserLogOut=builder.moveToElement(settings).click().build();
	  clickUserLogOut.perform();
	  //Enter User Details
	  WebElement aboutUser=driver.findElement(By.id("bio"));
	  WebElement twitterhandle=driver.findElement(By.id("twitter_handle"));
	  WebElement userWebsite=driver.findElement(By.id("website_link"));
	  WebElement userMobileNo=driver.findElement(By.id("mobile"));
	  aboutUser.clear();
	  aboutUser.sendKeys(userBioDetails);
	  twitterhandle.clear();
	  twitterhandle.sendKeys(userTwitterHandle);
	  userWebsite.clear();
	  userWebsite.sendKeys(userWebsiteInfo);
	  userMobileNo.clear();
	  userMobileNo.sendKeys(userPhoneNo);
	  //Submit the form .
	  WebElement submitForm=driver.findElement(By.id("submit"));
	  submitForm.click();
	  Thread.sleep(5000);
	  //Change  profile picture of the user.
	  WebElement profilePicPanel=driver.findElement(By.id("profile-photo"));
	  WebElement changeProfileBtn=driver.findElement(By.id("change-picture"));
	  Action setProfilePic=builder.moveToElement(profilePicPanel).moveToElement(changeProfileBtn).click().build();
	  setProfilePic.perform();
	  Thread.sleep(9000);
	  WebElement choosefileBtn=driver.findElement(By.id("profile_photo_upload"));
	  choosefileBtn.sendKeys(profilePicFilePath);
	  Thread.sleep(10000);
	  WebElement setProfilePicBtn=driver.findElement(By.id("submit-crop"));
	  wait.until(ExpectedConditions.visibilityOf(setProfilePicBtn));
	  setProfilePicBtn.click();
	  Thread.sleep(5000);
}

@Test(priority=4)
public void SearchRestaurant() throws InterruptedException
{
	  driver.navigate().to(baseURL);
	  String actualRestaurantTitle,expectedRestaurantTitle;
	  expectedRestaurantTitle="Barbeque Nation, Marol, Mumbai - Zomato";
	  //Search Barbeque Nation restaurant
	  WebElement searchBox=driver.findElement(By.xpath("//*[@id=\"keywords_input\"]"));
	  searchBox.sendKeys("Barbeque Nation Marol");
	  Thread.sleep(5000);
	  //From the list of suggestions displayed, select the first one .
	  WebElement bbqMarol=driver.findElement(By.xpath("//*[@id=\"keywords-by\"]/li[1]/a/div"));
	  builder=new Actions(driver);
	  Action clicKBbqMarol=builder.moveToElement(bbqMarol).click().build();
	  clicKBbqMarol.perform();		  
	  actualRestaurantTitle=driver.getTitle();
	  Assert.assertEquals(actualRestaurantTitle, expectedRestaurantTitle);	  
}

@Test(priority=5)
public void GetRestaurantInfo() throws InterruptedException
{
	  String phoneNo,openHours,avgCost,cuisines;
	  //Get Phone Numbers
	  Thread.sleep(5000);
	   phoneNo=driver.findElement(By.xpath("//*[@id=\"phoneNoString\"]/span/span[1]/span")).getText();
	   System.out.println("Phone Numbers : "+phoneNo);
	   //Get  Cuisines
	   Thread.sleep(5000);
	   cuisines=driver.findElement(By.xpath("//*[@id=\"mainframe\"]/div[1]/div/div[1]/div[3]/div[1]/div[1]/div[2]/div/div/a[1]")).getText()+", "+
	   driver.findElement(By.xpath("//*[@id=\"mainframe\"]/div[1]/div/div[1]/div[3]/div[1]/div[1]/div[2]/div/div/a[2]")).getText()+", "+
			   driver.findElement(By.xpath("//*[@id=\"mainframe\"]/div[1]/div/div[1]/div[3]/div[1]/div[1]/div[2]/div/div/a[3]")).getText();
	   System.out.println("Cuisines : "+cuisines);
	   //Get Opening Hours 
	   Thread.sleep(5000);
	  openHours=driver.findElement(By.xpath("//*[@id=\"mainframe\"]/div[1]/div/div[1]/div[3]/div[1]/div[2]/div[1]/div/div/div[2]/div/div[1]")).getText();
	  System.out.println("Opening Hours : "+openHours);
	  //Get Average Cost 
	  Thread.sleep(5000);
	  avgCost=driver.findElement(By.xpath("//*[@id=\"mainframe\"]/div[1]/div/div[1]/div[3]/div[1]/div[1]/div[3]/div/div/span[2]")).getText();
	  //Print Details
	  System.out.println("avgCost : "+avgCost);
}

@Test(priority=6)
public void ViewMenu() throws InterruptedException
{
	  wait=new WebDriverWait(driver,10);
	  String actualURL,expectedURL;
	  expectedURL="https://www.zomato.com/mumbai/barbeque-nation-marol/menu";
	  Thread.sleep(5000);
	  WebElement menulink=driver.findElement(By.linkText("Menu"));
	  wait.until(ExpectedConditions.visibilityOf(menulink));
	  menulink.click();
	  Thread.sleep(5000);
	  //Verify URL
	  actualURL=driver.getCurrentUrl();
	  Assert.assertEquals(actualURL, expectedURL);
	 
}

@Test(priority=7)
public void ViewReview() throws InterruptedException
{
	wait=new WebDriverWait(driver,10);
	String actualURL,expectedURL;
	  expectedURL="https://www.zomato.com/mumbai/barbeque-nation-marol/reviews";
	  Thread.sleep(15000);
	WebElement reviewLink=driver.findElement(By.partialLinkText("Reviews"));
	wait.until(ExpectedConditions.visibilityOf(reviewLink));
	reviewLink.click();
	Thread.sleep(5000);
	//Verify URL
	actualURL=driver.getCurrentUrl();
	Assert.assertEquals(actualURL, expectedURL);
	//Get Latest Review
	Thread.sleep(5000);
	String review=driver.findElement(By.xpath("//*[@id=\"reviews-container\"]/div[1]/div[3]/div[1]/div[1]/div/div[1]/div[4]")).getText();
	System.out.println(review);
}

@Test(priority=8)
public void ViewPhotos() throws InterruptedException
{
  wait=new WebDriverWait(driver,10);
	String actualURL,expectedURL;
	  expectedURL="https://www.zomato.com/mumbai/barbeque-nation-marol/photos";
	  Thread.sleep(3000);
	WebElement photoLink=driver.findElement(By.partialLinkText("Photos"));
	wait.until(ExpectedConditions.visibilityOf(photoLink));
	photoLink.click();
  Thread.sleep(3000);
	//Verify URL
	  actualURL=driver.getCurrentUrl();
		Assert.assertEquals(actualURL, expectedURL);	
}

@Test(priority=9)
public void GetDirections() throws InterruptedException
{
	 String zomatoWindowhandle;
    //Go to Overview options.
	driver.findElement(By.xpath("//*[@id=\"mainframe\"]/div[1]/div/div[1]/div[2]/div[1]/div/a[1]/span")).click();	
	//Click on direction widget present on the zomto website
	  Thread.sleep(5000);
	  driver.findElement(By.xpath("//*[@id=\"mainframe\"]/div[1]/div/div[1]/div[3]/div[1]/div[2]/div[4]/div/div/a")).click();
	  zomatoWindowhandle=driver.getWindowHandle();
	  //Open google maps 
	  driver.findElement(By.xpath("//*[@id=\"tabtop\"]/div/div[2]/a/div")).click(); 
	  Set<String> allwindowhandles=driver.getWindowHandles();
	  Thread.sleep(30000);
	 //Change Focus to Google map tab.
	  for(String currentWindowHandle :allwindowhandles)
	  {
		  if(!currentWindowHandle.equals(zomatoWindowhandle))
		  {
			  driver.switchTo().window(currentWindowHandle);
			//Verify the title 
			  String actualTitle=driver.getTitle();
			  Assert.assertEquals(actualTitle, "Google Maps");
			  
			//Enter "From" location
			  String mylocation="Chakala (J B Nagar), J B Nagar, Andheri-Kurla Road, Andheri (E)";
			  //Thread.sleep(5000);
			  driver.findElement(By.xpath("//*[@id=\"sb_ifc50\"]/input")).sendKeys(mylocation);
			  Thread.sleep(5000);
			  driver.findElement(By.xpath("//*[@id=\"directions-searchbox-0\"]/button[1]")).click();
			  Thread.sleep(9000);
			  //Verify searched result
			  String searchedString=driver.getCurrentUrl();
			  if(searchedString.contains("Chakala"))
			  {
				  //Close google map tab
				  driver.close();
			  }
			  else
		System.out.println("Google maps search Failed");	  
		  }
	  }
	//switch to zomato tab and assert
	 driver.switchTo().window(zomatoWindowhandle);
   Assert.assertTrue(driver.getWindowHandle().equals(zomatoWindowhandle), "Failed to switch to zomato website");		  
}

@Test(priority=10)
public void BookmarkRestaurant() throws InterruptedException
{
	  driver.navigate().to(restaurantHomePage);
	  driver.manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS);
	  wait=new WebDriverWait(driver,30);
	  WebElement bookmarkLink=driver.findElement(By.xpath("/html/body/div[6]/div[1]/div/div[1]/div[1]/div/div[2]/div[3]/div/div[1]/div[1]/div"));
	  wait.until(ExpectedConditions.visibilityOf(bookmarkLink));
	  Thread.sleep(9000);
	  String colorBeforeClick=bookmarkLink.getCssValue("background-color");
	  bookmarkLink.click();
	  Assert.assertFalse((bookmarkLink.getCssValue("background-color")).equals(colorBeforeClick));
	  Thread.sleep(9000);
	  WebElement userPanel=driver.findElement(By.xpath("//*[@id=\"login-navigation\"]/div/div/span"));
	  Action clickUserPanel=builder.moveToElement(userPanel).click().build();
	  clickUserPanel.perform();
	  Thread.sleep(9000);
	  WebElement bookmark=driver.findElement(By.linkText("Bookmarks"));
	  wait.until(ExpectedConditions.visibilityOf(bookmark));
	  Action clickBookmark=builder.moveToElement(bookmark).click().build();
	  clickBookmark.perform();
	  Thread.sleep(12000);
	  WebElement bookmarkList=driver.findElement(By.linkText("Barbeque Nation"));
	  wait.until(ExpectedConditions.visibilityOf(bookmarkList));
	  Assert.assertTrue(bookmarkList.isDisplayed());
}

@Test(priority=11)
public void RateARestaurant() throws InterruptedException
{
	      driver.navigate().to(restaurantHomePage);
		  driver.manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS);
		  wait=new WebDriverWait(driver,30);
		  WebElement rateRestauarntLink=driver.findElement(By.xpath("//*[@id=\"mainframe\"]/div[1]/div/div[1]/div[1]/div/div[2]/div[3]/div/div[1]/div[4]/div"));
		  wait.until(ExpectedConditions.visibilityOf(rateRestauarntLink));
		  rateRestauarntLink.click();
		  Thread.sleep(7000);
		  WebElement rate4Star=driver.findElement(By.xpath("//*[@id=\"mainframe\"]/div[1]/div/div[1]/div[1]/div/div[2]/div[3]/div/div[1]/div[5]/div/div[1]/div/div[2]/div[1]/div/a[4]"));
		  builder=new Actions(driver);
		  String colorBeforeClick=rateRestauarntLink.getCssValue("background-color");
		  Action hoverOn4Star=builder.moveToElement(rate4Star).build();
		  hoverOn4Star.perform();
		  Thread.sleep(3000);
		  Action give4Star=builder.click(rate4Star).build();
		  give4Star.perform();
		  wait.until(ExpectedConditions.visibilityOf(rateRestauarntLink));
		  Thread.sleep(9000);
		  Assert.assertFalse((rateRestauarntLink.getCssValue("background-color")).equals(colorBeforeClick));
		  
}

@Test(priority=12)
public void AddReview() throws InterruptedException
{
	  driver.navigate().to(restaurantHomePage);
	  driver.manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS);
	  wait=new WebDriverWait(driver,30);
	  Thread.sleep(5000);
	  WebElement addReviewLink=driver.findElement(By.id("resinfo-wr"));
	  wait.until(ExpectedConditions.visibilityOf(addReviewLink));
	  addReviewLink.click(); 
	  WebElement reviewBtn=driver.findElement(By.xpath("//*[@id=\"quick_review_initial\"]/button"));
	  wait.until(ExpectedConditions.visibilityOf(reviewBtn));
	  reviewBtn.click();
	  WebElement reviewTxtbox=driver.findElement(By.id("review-form-textarea-id"));
	  reviewTxtbox.sendKeys(restaurantReview);
	  WebElement reviewSubmit=driver.findElement(By.id("review-submit"));
	  reviewSubmit.click();
	  Thread.sleep(5000);
	  WebElement addReviewLink2=driver.findElement(By.id("resinfo-wr"));
	  wait.until(ExpectedConditions.visibilityOf(addReviewLink2));
	  Assert.assertTrue((addReviewLink2.getText()).equals("My Review"));
      Thread.sleep(5000);
	  WebElement userPanel=driver.findElement(By.xpath("//*[@id=\"login-navigation\"]/div/div/span"));
	  Action clickUserPanel=builder.moveToElement(userPanel).click().build();
	  clickUserPanel.perform();
	  Thread.sleep(5000);
	  WebElement review=driver.findElement(By.linkText("Reviews"));
	  wait.until(ExpectedConditions.visibilityOf(review));
	  Action clickReview=builder.moveToElement(review).click().build();
	  clickReview.perform();
	  WebElement ReviewList=driver.findElement(By.linkText("Barbeque Nation"));
	  wait.until(ExpectedConditions.visibilityOf(ReviewList));
	  Assert.assertTrue(ReviewList.isDisplayed());
}

@Test(priority=13)
public void RestaurantsVisited() throws InterruptedException
{
	  driver.navigate().to(restaurantHomePage);
	  driver.manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS);
	  wait=new WebDriverWait(driver,30);
	  WebElement visitedLink=driver.findElement(By.id("resinfo-bt"));
	  wait.until(ExpectedConditions.visibilityOf(visitedLink));
	  String colorBeforeClick=visitedLink.getCssValue("background-color");
	  visitedLink.click();
	  Assert.assertFalse((visitedLink.getCssValue("background-color")).equals(colorBeforeClick));
	  Thread.sleep(5000);
	  WebElement userPanel=driver.findElement(By.xpath("//*[@id=\"login-navigation\"]/div/div/span"));
	  Action clickUserPanel=builder.moveToElement(userPanel).click().build();
	  clickUserPanel.perform();
	  Thread.sleep(5000);
	  WebElement profileLink=driver.findElement(By.linkText("Profile"));
	  wait.until(ExpectedConditions.visibilityOf(profileLink));
	  Action clickOnProfile=builder.moveToElement(profileLink).click().build();
	  clickOnProfile.perform();
	  Thread.sleep(5000);
	  WebElement beenVisitedLink=driver.findElement(By.xpath("//*[@id=\"selectors\"]/a[6]"));
	  wait.until(ExpectedConditions.visibilityOf(beenVisitedLink));
	  beenVisitedLink.click();
	  WebElement restaurantVisitedList=driver.findElement(By.linkText("Barbeque Nation"));
	  wait.until(ExpectedConditions.visibilityOf(restaurantVisitedList));
	  Assert.assertTrue(restaurantVisitedList.isDisplayed());  
}

@Test(priority=14)
public void AddToCollection() throws InterruptedException
{
	  driver.navigate().to(restaurantHomePage);
	  driver.manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS);
	  wait=new WebDriverWait(driver,30);
	  Thread.sleep(5000);
	  WebElement addCollectionLink=driver.findElement(By.id("res-page-add-user-collection"));
	  wait.until(ExpectedConditions.visibilityOf(addCollectionLink));
	  addCollectionLink.click();
	  WebElement addToCollection=driver.findElement(By.xpath("//*[@id=\"302918\"]/div"));
	  wait.until(ExpectedConditions.visibilityOf(addToCollection));
	  addToCollection.click();
	  WebElement saveCollectionBtn=driver.findElement(By.id("res-page-add-res-to-collection"));
	  wait.until(ExpectedConditions.visibilityOf(saveCollectionBtn));
	  saveCollectionBtn.click();
	  Thread.sleep(5000);
	  Assert.assertTrue(driver.findElement(By.xpath("//*[@id=\"res-page-add-user-collection\"]/div")).isDisplayed());
}

@Test(priority=15)
public void BookATable() throws InterruptedException
{
	  WebElement bookTableLink = null;
	  driver.navigate().to(restaurantHomePage);
	  Thread.sleep(5000);
	  wait=new WebDriverWait(driver,30);
	  try
	  {
	   bookTableLink=driver.findElement(By.id("booktable"));
	   String checkLinkProperty=bookTableLink.getAttribute("class");
	  if(checkLinkProperty.contains("disabled"))
	  {
		  System.out.println("Online booking of table at this restaurant is not available right now.");
	  }
	  else
	  {
		  
		  bookTableLink.click();
		  driver.manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS);
		  String expectedURL="https://www.zomato.com/mumbai/barbeque-nation-marol/book";
		  Assert.assertEquals(driver.getCurrentUrl(), expectedURL);
		  Thread.sleep(5000);
		  WebElement bookTableBtn=driver.findElement(By.id("confirm-booking"));
		  wait.until(ExpectedConditions.visibilityOf(bookTableBtn));
		  bookTableBtn.click();
		  WebElement phoneNoVerificatn=driver.findElement(By.id("verification_code"));
		  wait.until(ExpectedConditions.visibilityOf(phoneNoVerificatn));
		  Assert.assertTrue(phoneNoVerificatn.isDisplayed());
	  }
	  }
	  catch(org.openqa.selenium.ElementClickInterceptedException e)
	 {
		  System.out.println("EXception occurred in Test->BookTable "+e);
 }
}

@Test(priority=16)
void orderFood() throws InterruptedException 
{
	  driver.navigate().to(baseURL);
	  driver.manage().timeouts().pageLoadTimeout(20, TimeUnit.SECONDS);
	  wait=new WebDriverWait(driver,30);
	  builder=new Actions(driver);
	  Thread.sleep(5000);
	 WebElement detectLocation=driver.findElement(By.xpath("/html/body/section[1]/div/div[2]/div/div/div[1]/div[1]/button/span")); 
	Action getLocation=builder.moveToElement(detectLocation).click(detectLocation).build();
	getLocation.perform();
	Thread.sleep(7000);
	 WebElement orderFoodLink=driver.findElement(By.xpath("/html/body/section[1]/div/div[2]/div/div/div[2]"));
	  Action findFood=builder.moveToElement(orderFoodLink).click(orderFoodLink).build();
	 findFood.perform();
	 Thread.sleep(7000);
	  String orderFoodPg=driver.getWindowHandle();
	  WebElement firstRestaurant=driver.findElement(By.xpath("//*[@id=\"orig-search-list\"]/div[1]/div[2]/a"));
	 firstRestaurant.click();
	  Set<String> allwindowHandles=driver.getWindowHandles();
	  for(String currentHandle:allwindowHandles)
	  {
		  if(!currentHandle.equals(orderFoodPg))
		  {
			  driver.switchTo().window(currentHandle);
			 WebElement addFoodItem=driver.findElement(By.linkText("ADD"));
			 addFoodItem.click();
			 Thread.sleep(3000);
			 if(isAddToCartWebElementPresent())
			 {
				 
				 driver.findElement(By.xpath("/html/body/div[12]/div/div[3]/div")).click();
			 }
			 Thread.sleep(5000);
			  driver.findElement(By.xpath("//*[@id=\"app\"]/div/div/div[2]/div[3]/button")).click();  
		  }
	  }
	  driver.switchTo().window(orderFoodPg);	  
}

public boolean isAddToCartWebElementPresent() 
{ 
    try 
    { 
    	
        if(driver.findElement(By.xpath("/html/body/div[12]/div/div[3]/div")).isDisplayed())
        {
        	return true; 
        }
        else 
       	return false;
        
    }   
    catch (org.openqa.selenium.NoSuchElementException Ex) 
    { 
        return false; 
    }   
} 

@Test(priority=17)
public void ViewOrderHistory() throws InterruptedException
{
	driver.navigate().to(baseURL);
	driver.manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS);
    wait=new WebDriverWait(driver,30);
    Thread.sleep(5000);
    builder=new Actions(driver);
	  WebElement userPanel=driver.findElement(By.xpath("//*[@id=\"login-navigation\"]/div/div/span"));
	  Action clickUserPanel=builder.moveToElement(userPanel).click().build();
	  clickUserPanel.perform();
	  Thread.sleep(10000);
	  WebElement profileLink=driver.findElement(By.linkText("Profile"));
	  Action clickProfile=builder.moveToElement(profileLink).click().build();
	  clickProfile.perform();
	  Thread.sleep(5000);
	  WebElement orderHistoryLink=driver.findElement(By.xpath("/html/body/div[6]/div/div[2]/div[1]/div/section/div[2]/div/a[2]"));
	  wait.until(ExpectedConditions.visibilityOf(orderHistoryLink));
	  orderHistoryLink.click();
	  Thread.sleep(5000);
	  Assert.assertTrue(driver.getCurrentUrl().contains("ordering"));
	  WebElement orderCount=driver.findElement(By.xpath("//*[@id=\"ordering\"]/h3"));
	  wait.until(ExpectedConditions.visibilityOf(orderCount));
	  System.out.println(orderCount.getText());
	    
}

@Test(priority=18)
public void SearchRestaurantByTypeOfMeal() throws InterruptedException
{
   wait=new WebDriverWait(driver,20);
	  //Go to home page.
	  driver.navigate().to(baseURL);
	  driver.manage().timeouts().pageLoadTimeout(20, TimeUnit.SECONDS);
	  //Enter your type of meal in "mealType" variable .
	  WebElement mealTypeLunch= driver.findElement(By.xpath("/html/body/section[2]/div/a[5]/img"));
	  wait.until(ExpectedConditions.visibilityOf(mealTypeLunch));
	  mealTypeLunch.click();
	  //Verify the search result
   Assert.assertTrue(driver.getTitle().contains("Dinner"));
}

@Test(priority=19)
public void GetBestRestaurants() throws InterruptedException 
{
	  String expectedURL="https://www.zomato.com/mumbai/best-restaurants";
	  wait=new WebDriverWait(driver,20);  
	  //Get the list highest rater restaurants.
	  driver.navigate().to(baseURL);
	  driver.manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS);
	  driver.findElement(By.id("search_button")).click();
	  //Filter the list by rating -high to low
	  Thread.sleep(9000);
	  WebElement rating=driver.findElement(By.xpath("//*[@id=\"mainframe\"]/div[2]/div[3]/div[2]/div/div[1]/div[1]/div[10]/a[2]/div/span"));
	  wait.until(ExpectedConditions.visibilityOf(rating));
	  rating.click();
	  //Verify the searched result by Title.
	  Thread.sleep(10000);
	 Assert.assertEquals(driver.getCurrentUrl(), expectedURL);     
}

@Test(priority=20)
	  public void ViewTrendingRestaurants() throws InterruptedException
	  {
		driver.navigate().to(baseURL);
		driver.manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS);
		WebElement trendingRetaurants=driver.findElement(By.xpath("/html/body/section[1]/div/div[1]/div/div[1]/div/a/div/div[2]/div/div/div[1]"));
		trendingRetaurants.click();
		Thread.sleep(5000);
		Assert.assertEquals(driver.getCurrentUrl(),"https://www.zomato.com/mumbai/top-restaurants");
	  }
	
@Test(priority=21)
public void GetZomatoApp()
{
	  //Navigate to home page.
	  driver.navigate().to(baseURL);
		driver.manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS);
	  //Click on Get App link.
	  WebElement getApp=driver.findElement(By.linkText("Get the App"));
	  getApp.click();
	  //Get app link on mobile number
	  WebElement phoneNoTxtBox=driver.findElement(By.id("phone-no"));
	  phoneNoTxtBox.sendKeys(userPhoneNo);
  WebElement txtApplinkBtn=driver.findElement(By.id("app-download-sms"));
  txtApplinkBtn.click();
  //Get app link on email
  WebElement emailTxtBox=driver.findElement(By.id("email-id"));
  emailTxtBox.sendKeys(userEmail); 
  WebElement txtApplinkBtnEmail=driver.findElement(By.id("send-email"));
  txtApplinkBtnEmail.click();  
}

@Test(priority=22)
public void BuyZomatoGold() throws InterruptedException 
{
    driver.navigate().to(baseURL);
	  driver.findElement(By.xpath("//*[@id=\"gold_entry_point_header\"]/span")).click();
	  Thread.sleep(5000);
	  driver.findElement(By.xpath("//*[@id=\"active_plan\"]/div[2]/button")).click();
	 Assert.assertEquals(driver.getTitle(), "Zomato Gold");
	  //Verify  payment page
	 Thread.sleep(9000);
	  Assert.assertTrue(driver.getCurrentUrl().contains("makepayment"));
	  driver.findElement(By.name("number")).sendKeys("4672462461827468768");
	  driver.findElement(By.name("month")).sendKeys("99");
	  driver.findElement(By.name("year")).sendKeys("9999");
	  driver.findElement(By.name("verification_value")).sendKeys("567");
    driver.findElement(By.name("first_name")).sendKeys("Saiyan");
    driver.findElement(By.name("last_name")).sendKeys("Prince");
    Thread.sleep(7000);
    Assert.assertTrue(driver.findElement(By.xpath("//*[@id=\"payment_kit\"]/div/button")).isEnabled());
}

@Test(priority=23)
void AboutUs() throws InterruptedException
{
	  driver.navigate().to(baseURL);
	  String zomatoWindow=driver.getWindowHandle();
       Thread.sleep(5000);
	  driver.findElement(By.linkText("About Us")).click();
	  Thread.sleep(5000);
	  Set<String> allwindowHandles=driver.getWindowHandles();
	  for(String currentHandle:allwindowHandles)
	  {
		 
			  driver.switchTo().window(currentHandle);
			  Thread.sleep(3000);
			  if(driver.getCurrentUrl().equals("https://www.zomato.com/about"))
			  {
			  JavascriptExecutor js= (JavascriptExecutor)driver;
			  js.executeScript("window.scrollTo(0,document.body.scrollHeight)");
			  Thread.sleep(3000);
			  driver.close();
			  }
	   }
	  driver.switchTo().window(zomatoWindow);
	  Assert.assertEquals(driver.getCurrentUrl(), baseURL);
}


@Test(priority=24)
public void Logout() throws InterruptedException
{
    driver.navigate().to(baseURL);
    String homepageHandle=driver.getWindowHandle();
	  driver.manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS);
	  Thread.sleep(5000);
	  builder=new Actions(driver);
	  Set<String> allwindowHandles=driver.getWindowHandles();
	  for(String currentHandle:allwindowHandles)
	  {
		 
			  driver.switchTo().window(currentHandle);
			  if(driver.getCurrentUrl().equals("https://www.zomato.com/mumbai"))
			  {
				  WebElement userPanel=driver.findElement(By.xpath("//*[@id=\"login-navigation\"]/div/div/span"));
				  Action clickUserPanel=builder.moveToElement(userPanel).click().build();
				  clickUserPanel.perform();
				  Thread.sleep(9000);
				  WebElement logout=driver.findElement(By.linkText("Log out"));
				  Action clickUserLogOut=builder.moveToElement(logout).click().build();
				  clickUserLogOut.perform();
				//Verify if the user is logged out
				  Thread.sleep(9000);
				 WebElement loginLink=driver.findElement(By.linkText("Log in"));
				 Assert.assertTrue(loginLink.isDisplayed());
			      driver.close();
			  }
	   }
	 
}
	
@AfterTest
public void CloseBrowser() 
{
	  
    driver.quit();
}

}
