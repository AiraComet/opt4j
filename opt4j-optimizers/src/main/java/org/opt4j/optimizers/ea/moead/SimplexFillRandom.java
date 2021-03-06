/*******************************************************************************
 * Copyright (c) 2014 Opt4J
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *******************************************************************************/

package org.opt4j.optimizers.ea.moead;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.opt4j.core.common.random.RandomMersenneTwister;


/**
* The {@link SimplexFillRandom} Fills a unit simplex in an m dimensional space with numSubproblems points,
* selected after Fig. 1 in  A. Jaszkiewicz,
*"On the performance of multiple-objective genetic local search on the 0/1 knapsack problem
* - a comparative experiment," in IEEE
* Transactions on Evolutionary Computation, vol. 6, no. 4, pp. 402-412, Aug.
* 2002.
*
* This selection assures that the probability to draw a weightVector in any hypervolume
* contained in [0,1]^numSubproblems is proportional to the size of that hypervolume
* @author Christian Vögl
*/
class SimplexFillRandom implements SimplexFill {
    private Random rand;

    public SimplexFillRandom() {
        rand =  new RandomMersenneTwister(0);
    }
    @Override
    /**
    * The actuall filling method
    * @ param numSubproblems
    *               The number of points to generate. In this use case, this corresponds to
                    the subproblems considered by the selection step in the decomposition algorithm.
    * @ param numObjectives:
                    The dimension of the vectorspace containing those points. In this use case, this
                    is equal to the number of objectives
    * @ return      A List of WeightVectors, where each WeightVector represents one point generated by this method
     */
    public List<WeightVector> fill (int numSubproblems, int numObjectives)
    {
        List<WeightVector> points =new ArrayList<>();

        for(int i = 0; i < numSubproblems; i++)
        {
            double sum  = 0;
            double[] values = new double[numObjectives];
            double u = rand.nextDouble();
            //if there is only one objective it has to get weight 1.
            if(numObjectives > 1){
                //transform the random number drawn previously to a front heavy distribution in (0,1)
                //use it as first weight.
                values[0] = 1 - Math.pow(u, 1.0 / (numObjectives -1));

                for(int k = 2; k<numObjectives; k++)
                {
                    u = rand.nextDouble();

                    double factor = 1;
                    for(int j = 0; j< k-1; j++)
                    {
                        factor -= values[j];
                    }
                    //following weights are drawn from a more tail-heavy distribution, but scaled by
                    //(1 - sum_of_previous_weights). Therefore subsequent numbers are smaller after drawing a
                    // relatively large number and vice versa.
                    double z = 1 - Math.pow(u,(1.0/(numObjectives-k)));
                    values[k-1] = factor * z;
                }

                for( int j = 0; j <numObjectives-1; j++)
                {
                    sum+= values[j];
                }
            }
            if(numObjectives>0){
                // set the weight of the last subproblem so that the sum of weights equals one.
                // if there are no subproblems, we don't set anything here.
                values[numObjectives-1] = 1 - sum;
            }
            points.add(new WeightVector(values));
        }
        return points;
    }
 }