import java.io.IOException;
import java.util.Scanner;



public class WeatherApp {

    public static void main(String[] args) throws IOException{



        // Instantiate the lists
        SortedLinkedList<Site> siteList = new SortedLinkedList<Site>();
        SortedLinkedList<Favourite> favList = new SortedLinkedList<Favourite>();


        
        // Download a list of sites in to the site list
        Utility.FetchSites( siteList );



        /* FOR TESTING */
        siteList.print();

        

        // -------------------------------------------
        // To add a favourite to the favourites list
        // -------------------------------------------
        // A Favourite contains a site and a temp
        // So we need to search for the site
        // Here we are only getting a pointer to the 
        // actual node ( garbage collected )
        Site searchedSite = siteList.search( "Yass" );
        // Next we place the searchedSite and temp in to a new Favourite 
        // Here we are instantiating a new Favourite ( wont be garbage collected )
        Double temp = 28.5;
        Favourite newFav = new Favourite( searchedSite, temp );
        // Then call add method of the Favourites list
        favList.add( newFav );

        // Or the complete process short handed
        favList.add( new Favourite( siteList.search( "Townsville" ), 28 ) );
        
        
        
        // Testing purpose only
        favList.print();

        

        // To access a list of items:
        // First create a symbolic link to the list
        SortedLinkedList<Favourite> myList = favList;

        // Then create a temporary favourite or site or whatever is appropriate
        Favourite site;

        // Then iterate over the items using the next() function
        /*
        while( (site = (Favourite)favList.next()) != null ){
            System.out.println("-----------------------");
            System.out.println( site.getName() );
            System.out.println( site.getURL() );
            System.out.println( site.getTemp() );
        }
        System.out.println("-----------------------");
        */
        


        // Testing the short list function
        // The following block of code demonstrated use of the
        // shortlist function 
        Scanner key = new Scanner( System.in );
        while( true ){
            SortedLinkedList<Site> newList = new SortedLinkedList<Site>();
            System.out.println( "Enter prefix to search for or quit" );
            String str = key.next();
            if( str.compareTo( "quit" ) == 0 ) break;
            
            newList.shortList( str, siteList );
            newList.print();
            
        }
    }

}
