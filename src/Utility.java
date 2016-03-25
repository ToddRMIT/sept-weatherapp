import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;



public class Utility{
	
	public static void FetchSites( SiteList siteList ) throws IOException{
		// ACT is included in NSW
		String[] sites = {"nsw","vic","qld","wa","sa","tas","nt"};
		String inputLine;
		String name;
		String address;
		int start;
		int end;
		for( int i = 0; i < sites.length; ++i ){
			URL urls = new URL("http://www.bom.gov.au/".concat(sites[i]).concat("/observations/").concat(sites[i]).concat("all.shtml"));
			BufferedReader in = new BufferedReader( new InputStreamReader( urls.openStream() ) );
			while( (inputLine = in.readLine() ) != null ){
				if( inputLine.matches( ".*station.*shtml.*" ) ){
					start = inputLine.indexOf("shtml\">") + 7;
					end = inputLine.indexOf("</a></th>");
					name = inputLine.subSequence( start, end ).toString();
					address = inputLine.subSequence( inputLine.indexOf("<a href=\"") + 9, start - 2 ).toString();
					siteList.insertSite( new Site( name, address ));
				}
			}
			in.close();	
			System.out.print("*");
		}
		System.out.println();
		return;
	}

}
