import java.util.Map;

class SimCos implements Sim
{    
    public static Double calc(Map<String,Double> u, Map<String,Double> v) {
    	Double sumprod = 0.0, sumsqru = 0.0, sumsqrv = 0.0; 
    	int count = 0;
    	for (String i : u.keySet()) {
    		if (v.containsKey(i)) {
    			sumprod += u.get(i) * v.get(i);
    			sumsqru += u.get(i) * u.get(i);
    			sumsqrv += v.get(i) * v.get(i);
    			count++;
    		}
    	}
    	if (count==0) return 0.0;
    	else return sumprod/(Math.sqrt(sumsqru)*Math.sqrt(sumsqrv));
    }
}