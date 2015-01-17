import java.util.Map;

class SimPearson implements Sim
{    
    public static Double calc(Map<String,Double> u, Map<String,Double> v) {
        //TO DO
        int count = 0, ucount=0, vcount =0;
        Double usum = 0.0, vsum = 0.0, umean = 0.0, vmean = 0.0, sumsqrv=0.0, sumsqru = 0.0; sumprod = 0.0; // summation variables

        // loop over to sum all u's and v's
        for (String i:u.keySet()) {
            if (v.containsKey(i)) {// both user u, v must have rating for item i 
                usum += u.get(i);
                vsum += v.get(i);
                count++; 
            }
        } 
    
        // calculate mean
        umean = usum / count;
        vmean = vsum / count;
        
        if (count==0) return 0.0; // case with no match 
        else {
            // loop over to calculate pearson formula
            for (String i:u.keySet()) {
                if (v.containsKey(i)) {// both user u, v must have rating for item i 
                    sumprod += ((u.get(i) - umean) * (v.get(i) - vmean));
                    sumsqru += ((u.get(i) - umean) * (u.get(i) - umean));
                    sumsqrv += ((v.get(i) - vmean) * (v.get(i) - vmean));
                }
            } 
            
            return sumprod/(Math.sqrt(sumsqru)*Math.sqrt(sumsqrv)); 
        }        
        //return 0.0;
    }
}
