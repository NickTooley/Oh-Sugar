
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;




public class WebCrawler {
	
	
	public static void main(String[] args) {	
		
		HashMap<String, String> searchResultMap = new HashMap<String, String>();

		String basePage = "https://shop.countdown.co.nz";
		String subPage = "/shop/browse/bakery";
		int NUMPRODPERPAGE = 24;
		
		ArrayList<String> categoryArr = new ArrayList<String>();

		
		categoryArr.add("/shop/browse/meat");
		categoryArr.add("/shop/browse/seafood");
		categoryArr.add("/shop/browse/baby-care/baby-food-from-4-mths");
		categoryArr.add("/shop/browse/baby-care/baby-food-from-6-mths");
		categoryArr.add("/shop/browse/baby-care/baby-food-from-9-mths");
		categoryArr.add("/shop/browse/baby-care/baby-food-one-year-");
		categoryArr.add("/shop/browse/baby-care/baby-food-packet-meals");
		categoryArr.add("/shop/browse/baby-care/baby-formula");
		categoryArr.add("/shop/browse/baby-care/other-baby-foods");
		categoryArr.add("/shop/browse/biscuits-crackers");
		categoryArr.add("/shop/browse/breakfast-foods");
		categoryArr.add("/shop/browse/canned-prepared-foods");
		categoryArr.add("/shop/browse/chocolate-sweets-snacks");
		categoryArr.add("/shop/browse/christmas");
		categoryArr.add("/shop/browse/drinks-hot-cold");
		categoryArr.add("/shop/browse/frozen-foods");
		categoryArr.add("/shop/browse/liquor-beer-cider");
		categoryArr.add("/shop/browse/liquor-wine");
		categoryArr.add("/shop/browse/fruit-vegetables");
		categoryArr.add("/shop/browse/bakery");
		
		
		
        try {
        	
        	for (int i = 0; i < categoryArr.size(); i++) {
        		
    		subPage = categoryArr.get(i);
        	
        	int numOfPages = 0;
        	Document doc1 = Jsoup.connect(basePage + subPage).get();
        	int numOfProd = Integer.parseInt(doc1.select(".paging-description.hidden-tablet.hidden").html().substring(0,3));
        	if(numOfProd % NUMPRODPERPAGE == 0) {
        		numOfPages = numOfProd / NUMPRODPERPAGE;
        	}else {
        		numOfPages = numOfProd / NUMPRODPERPAGE + 1;
        	}
        	System.out.println(numOfPages);
        	
        	
        	HashMap<String, String> arr = getAllProducts(doc1);
        	
        	for (int j = 2; j < numOfPages; j++) {
        		Document doc2 = Jsoup.connect(basePage + subPage + "?page=" + j).get();
        		arr.putAll(getAllProducts(doc2));
        		System.out.println("New page completed");
        		Thread.sleep(1500);
        		
        	}
        	
        	System.out.println(arr.toString());
        	
        	System.out.println(arr.size());
//        	
        	List<String> titles = new ArrayList<String>(arr.keySet());
        	List<String> URLs = new ArrayList<String>(arr.values());
//        	for (String URL: arr) {
        	
        	
        	for (int j = 0; j < arr.size(); j++) {
        		Document doc3 = Jsoup.connect(basePage + URLs.get(j)).get();
        		insertFoodData(getFoodData(doc3, titles.get(j)));
        		Thread.sleep(20000);
        		
        		}          
        	}
        	


        } catch (Exception e) {
        	System.out.println(e.getMessage());
        }
        
		
		
        
	}
	
	
	public static HashMap<String, String> getAllProducts(Document doc) {
		
		HashMap<String,String> arr = new HashMap<String,String>();
		
		Elements URLs = doc.select(".gridProductStamp-imageLink");
		List<String> searchResults = doc.select(".gridProductStamp-name").eachText();
		
		for (int i = 0; i < searchResults.size(); i++) {
            String productName = searchResults.get(i);
            String productURL = URLs.get(i).attr("href");
            arr.put(productName, productURL);
        }
		
		return arr;
	}
	
	public static ArrayList<String> getFoodData(Document doc, String title) {
		ArrayList<String> arr = new ArrayList<String>();
		arr.add(title);
		
		 Elements nutritional = doc.select("td");
         Elements headers = doc.select("th");
         Elements breadcrumbs = doc.select("span[itemprop='name']");

         ArrayList<String> crumbList = new ArrayList<String>();

         for(Element crumbs: breadcrumbs){
             crumbList.add(crumbs.html());
         }

         int incrementCount = 0;
         for (Element headings : headers) {
             if(headings.html().equals("Per 100g") || headings.html().equals("Per 100ml")){
                 break;
             }else{
                 incrementCount++;
             }
         }
         
         Double sugarContent = 0.0;
         Double sugarperhundred = 0.0;

         for(Element nutritionals: nutritional){
             if(nutritionals.html().equals("Sugars")){
                 String sugarOGString = nutritionals.nextElementSibling().html();
                 String sugarString = sugarOGString.substring(0, sugarOGString.length() - 1);
                 sugarContent = Double.parseDouble(sugarString);

                 Element nextNutritional = nutritionals;
                 for(int i=0; i < incrementCount;i++){
                     nextNutritional = nextNutritional.nextElementSibling();
                 }

                 String sugarHundredOG = nextNutritional.html();
                 sugarHundredOG = sugarHundredOG.substring(0, sugarHundredOG.length() - 1);
                 sugarperhundred = Double.parseDouble(sugarHundredOG);
             }

         }


         Elements barcode = doc.select(".product-image");
         String barcodeFNL = "";
         for(Element barcodes: barcode){
             String barcodeString = barcodes.attr("src");
             String pattern = "(?<=\\/Content\\/ProductImages\\/large\\/)(.*)(?=\\.jpg\\/)";
             Pattern r = Pattern.compile(pattern);
             Matcher m = r.matcher(barcodeString);

             if (m.find( )) {
                 barcodeFNL = m.group(1);
             }else {
                 
             }
         }
         arr.add(barcodeFNL);
         arr.add(crumbList.get(crumbList.size()-1));
         arr.add(Double.toString(sugarContent));
         arr.add(Double.toString(sugarperhundred));
         
         //title 0
         //barcode 1
         //category 2
         //sugar content 3
         //sugar 100 4
         
         System.out.println("Found: " + arr.toString());
		
		return arr;
	}
	
	public static void insertFoodData(ArrayList<String> arr) {
		
		
		String url = "http://kate.ict.op.ac.nz/~toolnj1/ohsugar/insertFoods.php";
	

        CloseableHttpClient httpclient = HttpClients.createDefault();
		try {
		HttpPost httpPost = new HttpPost(url);
		String JSON = "";
		
		if(arr.get(1) == "") {
			JSON = "{\"foodInsert\": true, \"foods\": [{\"name\": \""+arr.get(0)+"\",\"sugar1\": "+ Double.parseDouble(arr.get(3))+ ", \"sugar2\": "+ Double.parseDouble(arr.get(4)) + ", \"barcode\": \"NULL\", \"category\": \""+ arr.get(2) +"\" }]}";         
		}else {
			JSON = "{\"foodInsert\": true, \"foods\": [{\"name\": \""+arr.get(0)+"\",\"sugar1\": "+ Double.parseDouble(arr.get(3))+ ", \"sugar2\": "+ Double.parseDouble(arr.get(4)) + ", \"barcode\": \""+arr.get(1)+"\", \"category\": \""+ arr.get(2) +"\" }]}";         
		}
		StringEntity entity = new StringEntity(JSON);
		
		System.out.println(JSON);
		
		httpPost.setEntity(entity);
		httpPost.setHeader("Accept", "application/json");
		httpPost.setHeader("Content-type", "application/json");
		
		CloseableHttpResponse response2 = httpclient.execute(httpPost);

		    System.out.println(response2.getStatusLine());
		    HttpEntity entity2 = response2.getEntity();
		
		    response2.close();
		}catch(Exception e) {
			
		}
		
		System.out.println("Added product");
		
	}

}
