package hyflex;

import AbstractClasses.HyperHeuristic;
import AbstractClasses.ProblemDomain;
import java.lang.Math;
import static java.lang.Math.abs;
import static java.lang.Math.max;

public class Rlahc extends HyperHeuristic {
	/**
	 * creates a new ExampleHyperHeuristic object with a random seed
	 */
	public Rlahc(long seed){
		super(seed);
	}
	
	public double power (double n, double m){
        double hasil = 1;
        for (int i=0; i<m; i++){
            hasil = hasil * n;
        }
        return hasil;
    }
    
    public double sigma (double[] n,double m){
        double hasil = 1;
        for (int i=0; i<m; i++){
            hasil = hasil + n[i];
        }
        return hasil;
    }
	
	/**
	 * This method defines the strategy of the hyper-heuristic
	 * @param problem the problem domain to be solved
	 */
	public void solve(ProblemDomain problem) {

		//it is often a good idea to record the number of low level heuristics, as this changes depending on the problem domain
		int number_of_heuristics = problem.getNumberOfHeuristics();
		
		//initialise the variable which keeps track of the current objective function value
		double current_obj_function_value = Double.POSITIVE_INFINITY;
		
		double caccept[] = new double[number_of_heuristics];
        double cnew[] = new double[number_of_heuristics];
        double ctotal[] = new double[number_of_heuristics];
        double w[] = new double[number_of_heuristics];
        double wmin = 0, wtotal = 0, kumulatif, random;
        for (int i=0; i<number_of_heuristics; i++){
            caccept[i] = 0;
            cnew[i] = 0;
            ctotal[i] = 0;
            w[i] = wmin;
        }
        int k = 100, nrep = 10, LP = 5, iter=0, v=0;
        double cb = 0, r = 0.8, rstop = 0.1;
        boolean f = false;
		Double[] llh = new Double[10];
		for (int i=0; i<10; i++) {
			llh[i] = current_obj_function_value;
		}

		//initialise the solution at index 0 in the solution memory array
		problem.initialiseSolution(0);

		//the main loop of any hyper-heuristic, which checks if the time limit has been reached
		while (!hasTimeExpired()) {
			wtotal = sigma(w, number_of_heuristics);
            kumulatif = 0;
            random = rng.nextDouble();
            int heuristic_to_apply;
            
            for (int i=0; i<number_of_heuristics; i++){
                kumulatif = kumulatif + w[i]/wtotal;
                if (kumulatif >= random){
                    heuristic_to_apply = i;
                    //apply the chosen heuristic to the solution at index 0 in the memory
        			//the new solution is then stored at index 1 of the solution memory while we decide whether to accept it
        			double new_obj_function_value = problem.applyHeuristic(heuristic_to_apply, 0, 1);
        			iter++;
        			//calculate the change in fitness from the current solution to the new solution
        			double delta = current_obj_function_value - new_obj_function_value;
        			
        			v = iter % 10;
        			//all of the problem domains are implemented as minimisation problems. A lower fitness means a better solution.
        			if (delta > 0 || new_obj_function_value < llh[v]) {
        				//if there is an improvement then we 'accept' the solution by copying the new solution into memory index 0
        				problem.copySolution(1, 0);
        				//we also set the current objective function value to the new function value, as the new solution is now the current solution
        				current_obj_function_value = new_obj_function_value;
        			} 
        			if (new_obj_function_value < llh[v]){
        				llh[v] = new_obj_function_value;
        			}/*else {
        				//if there is not an improvement in solution quality then we accept the solution with a 50% probability
        				if (rng.nextBoolean()) {
        					//the process for 'accepting' a solution is the same as above
        					problem.copySolution(1, 0);
        					current_obj_function_value = new_obj_function_value;
        				}
        			}*/
        			ctotal[heuristic_to_apply]++;
                    if(problem.compareSolutions(0, 1) == false){
                        cnew[heuristic_to_apply]++;
                    }
                    if (delta > 0) {
                        caccept[heuristic_to_apply]++;
                        cb++;
                        f = false;
                    }
                    if (iter%LP == 0){
                        if (cb/LP < rstop){
                            f = true; 
                            for (int j=0; j<number_of_heuristics; j++){
                                if(ctotal[j] == 0)
                                    w[j] = wmin;
                                else w[j] = max(wmin, cnew[j]/ctotal[j]);
                                caccept[heuristic_to_apply] = 0;
                                cnew[heuristic_to_apply] = 0;
                                ctotal[heuristic_to_apply] = 0;
                            }
                        } else {
                            for (int j=0; j<number_of_heuristics; j++){
                                if(ctotal[j] == 0)
                                    w[j] = wmin;
                                else w[j] = max(wmin, caccept[j]/ctotal[j]);
                                caccept[heuristic_to_apply] = 0;
                                cnew[heuristic_to_apply] = 0;
                                ctotal[heuristic_to_apply] = 0;
                            }
                        }
                        cb = 0;
                    }
                }
            }
			//this hyper-heuristic chooses a random low level heuristic to apply
			//int heuristic_to_apply = rng.nextInt(number_of_heuristics);

			
			//one iteration has been completed, so we return to the start of the main loop and check if the time has expired 
		}
	}
	
	/**
	 * this method must be implemented, to provide a different name for each hyper-heuristic
	 * @return a string representing the name of the hyper-heuristic
	 */
	public String toString() {
		return "Example Hyper Heuristic One";
	}

}
