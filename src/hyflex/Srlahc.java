package hyflex;

import AbstractClasses.HyperHeuristic;
import AbstractClasses.ProblemDomain;
import java.lang.Math;
import static java.lang.Math.abs;
import static java.lang.Math.max;

public class Srlahc extends HyperHeuristic {
	/**
	 * creates a new ExampleHyperHeuristic object with a random seed
	 */
	public Srlahc(long seed){
		super(seed);
	}
	
	public double power (double n, double m){
        double hasil = 1;
        for (int i=0; i<m; i++){
            hasil = hasil * n;
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
		
        int iter=0, p=0;
		Double[] lh = new Double[20];
		for (int i=0; i<20; i++) {
			lh[i] = current_obj_function_value;
		}

		//initialise the solution at index 0 in the solution memory array
		problem.initialiseSolution(0);

		//the main loop of any hyper-heuristic, which checks if the time limit has been reached
		while (!hasTimeExpired()) {
			//this hyper-heuristic chooses a random low level heuristic to apply
			int heuristic_to_apply = rng.nextInt(number_of_heuristics);
		                        
			//apply the chosen heuristic to the solution at index 0 in the memory
			//the new solution is then stored at index 1 of the solution memory while we decide whether to accept it
			double new_obj_function_value = problem.applyHeuristic(heuristic_to_apply, 0, 1);
			iter++;
			//problem.compareSolutions(heuristic_to_apply, heuristic_to_apply);
			//calculate the change in fitness from the current solution to the new solution
			double delta = current_obj_function_value - new_obj_function_value;
		    //solusi yang bagus yang lebih kecil. kalo delta lebih dari 0 maka solusi baru lebih bagus dari yang lama.
			//all of the problem domains are implemented as minimisation problems. A lower fitness means a better solution.
			p = iter % 20;
			//System.out.print(p);
			//System.out.println(lh[p]);
			if (delta > 0 || new_obj_function_value < lh[p]) {
				//if there is an improvement then we 'accept' the solution by copying the new solution into memory index 0
				problem.copySolution(1, 0);
				//we also set the current objective function value to the new function value, as the new solution is now the current solution
				current_obj_function_value = new_obj_function_value;
				if (new_obj_function_value < lh[p]){
					lh[p] = new_obj_function_value;
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
