import java.util.Scanner;

public class LogisticRegTeach2 {
	Double [][] x; //data
	int N, M; //number of rows and columns
	Double[] y; //class attribute, class values are 1 or -1 
	
	Double[] w; //this will hold the learned weights
	Double[] g ; /* Gradients of w */

	Double kappa = 2.0; //learning rate
	
	Double threshold = 0.5; //Default threshold for classification 
	
	int iter = 50; //number of iterations
	Double p1, p2;
	
	public LogisticRegTeach2(Double [][] x) {
		this.x = x;
		this.N = x.length; //number of rows
		this.M = x[0].length; //number of attributes (the last attribute is attribute y)
		
		//Fill y and use its column in x as a dummy filled with 1's
		this.y = new Double[N];
		for(int n=0; n<N; n++) { y[n] = x[n][M-1]; x[n][M-1] = 1.0; } 
				
		//Now do the computation.
		this.w = new Double[M];
		ComputeWeightsWithGD();
	}
	
	public Double Classify(Double[] xn) {
		if (this.prob(xn, 1.0) >= this.threshold) 
			return 1.0;
		else return -1.0;
	}
		
	Double[] gradient_at_w() {
		Double[] s = new Double[M];
		//TO DO *****************************************
		//Double ex = 0.0;
		for(int m=0; m<M; m++) {
			s[m] = 0.0;   /* initialize to 0  ?? */
			for(int n=0; n<N; n++) {
				s[m] = s[m] + ( y[n] * x[n][m] * prob(x[n], -y[n]) ) ;   /* sum */
			}
			s[m] =  s[m] / N ;   /* average */
		}
		//System.out.println("G="+s[0]+", "+s[1]+", "+s[2]);
		return s;
	}
	
	Double E() {
		Double sum = 0.0;
		for(int n=0; n<N; n++) 
			sum += Math.log(1/prob(x[n], y[n]));  /* calcualtes log( 1/(1+e...) ) */
		return sum/N;   /* avg of E from xls */
	}
	
	Double prob(Double[] xn, Double yn) {
		Double wx = 0.0;
		for(int m=0; m<M; m++) wx += w[m]*xn[m];  /* w1x1 + w2x2 + b */
		return 1/(1+Math.exp(-yn*wx));   /* 1 / (1 + exp(-ywx)) */  /* multiply yx */
	}	

	//Gradient descent
	void ComputeWeightsWithGD() {
		 
		for(int j=0; j<M; j++) w[j]=0.0; //Initial weights
		
		System.out.println("E="+E());
		
		int t=0;

		do { 
			//TO DO ********************
			//calculate Gradient
			g = gradient_at_w();
			// calculate new weights : wi + (k * gradient) */
			for(int j=0; j<M; j++) {
				w[j] = w[j] + (kappa * g[j]) ;
			}
			// print E
			//System.out.println("E="+E());

		} while (t++<iter);


		// Prediction
		this.Predict();

	}
	
	public void PrintW() {
		System.out.print("Weights: ");
		for(int j=0; j<w.length; j++) 
			System.out.print(w[j] + " ");
		System.out.print("\n");
	}

	public void Predict() {
		
		int i, b;
		Scanner input = new Scanner(System.in);
		Double [] x1 = new Double[M-1];
		Double pred, p1;

		System.out.println("Logestic Regression =  x[1] * "+w[0]+ " + x[2] * "+w[1]+" b * "+w[2]+" ;");
		for (i=0; i<M-1; i++) {
			System.out.println("Prediciton: Enter x["+i+"]: ");
			x1[i] = input.nextDouble();
		}
		System.out.println("Enter 1 for +ve and -1 for negative: "); 
		b= input.nextInt();

		p1 = 0.0;
		for (i=0; i<M-1; i++)
			p1 = p1 + (x1[i] * w[i]); 
		p1 = p1 + (b * w[i]);
		p1 = Math.exp( p1);
		System.out.println("Expression: exp("+ p1 +")");
		System.out.println("Predicited Value: "+ Math.exp(p1));

		//System.out.println("Classify: "+Classify(x1));
		/*pred =  this.prob(x1, x1[M]) - threshold;
		if ( pred > 0)
			System.out.println("D: 1" );
		else
			System.out.println("D: -1" ); */
	}

	public static void main(String[] args) throws Exception {
		
		Double [][] data = {
				{1.0,	1.0,	1.0},
				{0.9,	1.0,	1.0},
				{0.9,	0.875,	1.0},
				{0.7,	0.75,	-1.0},
				{0.6,	0.875,	-1.0},
				{0.6,	0.875,	1.0},
				{0.5,	0.75,	-1.0},
				{0.5,	0.8125,	-1.0},
				{0.5,	1.0,	1.0},
				{0.5,	0.875,	-1.0},
				{0.5,	0.875,	1.0}
		};
		
		LogisticRegTeach2 lr = new LogisticRegTeach2(data);
		lr.PrintW();

	}
}
