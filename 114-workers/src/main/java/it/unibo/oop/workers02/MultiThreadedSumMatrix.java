package it.unibo.oop.workers02;

import java.lang.IllegalArgumentException;
import java.util.ArrayList;
import java.util.List;

public class MultiThreadedSumMatrix implements SumMatrix{
    private int nThread;

    public MultiThreadedSumMatrix(int n){

        if(n<1)
            throw new IllegalArgumentException();
        else
            this.nThread=n;

    }

    @Override
    public double sum(double[][] matrix) {
        final List<Worker> workers = new ArrayList<>(nThread);
        final int size = matrix.length % this.nThread + matrix.length / this.nThread;

        for (int start = 0; start < matrix.length; start += size) {
            workers.add(new Worker(matrix, start, size));
        }

        for (final Worker w: workers) {
            w.start();
        }

        double sum = 0;

        for (final Worker w: workers) {
            try {
                w.join();
                sum += w.getResult();
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
        }
        /*
         * Return the sum
         */
        return sum;
    }   

    
    private static final class Worker extends Thread {

        private final double[][] matrix;
        private final int startpos;
        private final int nelem;
        private double res;
    
            /**
             * Builds a new worker.
             * 
             * @param matrix
             *            the matrix to be summed
             * @param startpos
             *            the start position for the sum in charge to this worker
             * @param nelem
             *            the no. of element for him to sum
             */
        private Worker(final double[][] matrix, final int startpos, final int nelem) {
            super();
            this.matrix = matrix;
            this.startpos = startpos;
            this.nelem = nelem;
            this.res=0;

            System.out.println("Working from position " + startpos + " to position " + (startpos + nelem - 1));
            for(int i=0;i<matrix.length && i<startpos+nelem;i++){
                for(int j=0;j<matrix[i].length;j++){
                    res+=j;
                }
            }
        }

        public double getResult() {
            return this.res;
        }
    
}
}