import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

public class RecKNNteach {
	
	//user_item_rating_map
	Map<String,Map<String,Double>> r;
	
	Double mu; //mean
	
	Sim simObj = new SimPearson();
	int K = 5; //top number of similar users to consider 
	
	public RecKNNteach(String filename) throws Exception {
		readData(filename);
		computeMu();
	}
		
	//predict rating for each user item
	public Double r_hat(String u, String i) {
		SortedMap<Double, Set<String>> sorted = this.sortedSimUsers(u);
		
		if (sorted.size() == 0) return mu; 		
		Double  rat_sim_sum = 0.0, sim_sum = 0.0;  // sim_sum = sum (simuv) ;  rat_sim_sum = sum (rv * simuv)
                				
		//TO DO
                int count =0;
                // Double rv = 0.0;  
                
                // loop over to sum all v's
                for (Double j:sorted.keySet()) { // for each similarity
                    for (String k: sorted.get(j) ) { // for each user with same similarity
                       /* if (r.get(k).containsKey(i) { // if user has rating for i
                            rv = r.get(k).get(i);
                        }
                        else {  rv = 0.0 ; } */
                        
                        sim_sum += SimEuclid.calc(r.get(u), r.get(k));  // calculate sim
                        rat_sim_sum +=  (r.get(k).get(i) * sim_sum) ; 
                        count++;
                        if (count == k)
                            break;
                         
                    }
                } 
                
		
		if (sim_sum.equals(0.0)) return mu;  // mean used for unexcited case
		else return rat_sim_sum/sim_sum;
	}
	
	//Why SortedMap<Double, Set<String>> as return type? 
	//Because there can be more than one user, say v and w, with the same similarity sim to u.
	//Therefore we store a set of users associated with each sim, e.g. {sim:{v,w}}.
        public SortedMap<Double, Set<String>> sortedSimUsers(String u) {    
            SortedMap<Double, Set<String>> sorted = 
        		new TreeMap<Double, Set<String>>(Collections.reverseOrder());
        
            for (String v : r.keySet()) {
                if ( v.equals(u) ) continue;
            
                Double sim =  this.simObj.calc(r.get(u),r.get(v));
            
                if ( !sorted.containsKey(sim) )
                    sorted.put(sim, new HashSet<String>());
                sorted.get(sim).add(v);
            }
        
            return sorted;
        }
    
	private void readData(String filename) throws Exception {
		r = new HashMap<String,Map<String,Double>>();
		
		System.out.println("Reading file " + filename + " ...");
		BufferedReader br = new BufferedReader( new FileReader(filename) );
		String line;
		while ( (line = br.readLine()) != null )  {
			String[] array = line.split("\t");
			String user = array[0];
			String item = array[1];
			Double rating = Double.parseDouble(array[2]);
			
			if( !r.containsKey(user) ) 
				r.put(user, new HashMap<String,Double>());
			
			r.get(user).put(item,rating);
		}
		br.close();
		System.out.println("End of reading file " + filename);
	}
	
	private void computeMu() {
		Double sum = 0.0;
		int count = 0;
		for(String u : r.keySet())
			for(String i : r.get(u).keySet()) {
				sum += r.get(u).get(i);
				count++;
			}
		mu = sum/count;
	}
    
	public static void main(String[] args) throws Exception {
		RecKNNteach rec = new RecKNNteach("C:/Users/Alex/Google Drive/Eclipse/toby.txt");
		String u = "Toby"; 
		String i = "The Night Listener";
		System.out.println("Prediction for user \"" + u + "\" for item \"" + i + "\" is: " + 
								rec.r_hat("Toby", "The Night Listener"));
	}
}
