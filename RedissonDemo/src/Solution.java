import java.util.*;

class Solution {

    public int solve(int[][] costs) {
         
         
         int noOfPersons = costs.length;

         PriorityQueue<Person> aQ = new PriorityQueue<>(
                 (p1 , p2) -> {
                     return p1.a.compareTo( p2.a ) ;
                 }
         ) ;
         
         PriorityQueue<Person> bQ = new PriorityQueue<>(
                 (p1 , p2) -> {
                     return p1.b.compareTo( p2.b ) ;
                 }
         ) ;


         Set<Person> persons = new HashSet<Person>() ;
         for(int i = 0 ; i < noOfPersons ; i++){     
             Person person = new Person(i , costs[i][0] , costs[i][1]) ;
             aQ.offer(person) ;
             bQ.offer(person) ;
            // persons.add(person) ;
         }
         
         int k = noOfPersons/2 ;
         int cost = 0;
         System.out.println("size:"+aQ.size() + " , " + bQ.size() ) ;
         while( k > 0){

             System.out.println("PAss "+k) ;
             Person pA = aQ.poll(); 
            

             while(persons.contains(pA))
                pA = aQ.poll() ;

             if(pA == null)
               System.out.println("null pA:") ;

             persons.add(pA);

             Person pB = bQ.poll();
             while(persons.contains(pB))
                pB = bQ.poll() ;
            
             System.out.println("pA:"+pA.toString()) ;
             System.out.println("pB:"+pB.toString()) ;

             cost += pA.a + pB.b ;
             
            
             persons.add(pB);

             k -= 1 ;
         }

         return cost;

    }

    class Person{
        
        Integer index ,  a ,  b ;
        
        public Person(int index , int a  , int b){
            this.index = index ;
            this.a = a;
            this.b = b;
        }

        
		public String toString() {
			return "Person [index=" + index + ", a=" + a + ", b=" + b + "]";
		}

    }


    
    public static void main(String [] args) {
    	int [] [] costs = {
    	                   {1, 5},
    	                   {9, 2},
    	                   {3, 8},
    	                   {4, 7}
    	} ;
    	
    	Solution solution = new Solution();
    	System.out.println( "ANS:" + solution.solve(costs));
    }

}