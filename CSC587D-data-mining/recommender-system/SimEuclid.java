import java.util.Map;

class SimEuclid implements Sim
{    
    public static Double calc(Map<String,Double> u, Map<String,Double> v) {
    	Double sumsqr = 0.0; int count = 0;
    	for (String i : u.keySet()) {
    		if (v.containsKey(i)) {
    			sumsqr += Math.pow(u.get(i) - v.get(i), 2.0);
    			count++;
    		}
    	}
    	if (count==0) return 0.0;
    	else return 1.0/(1.0+Math.sqrt(sumsqr));
    }
}
