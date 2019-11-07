package search;

import java.util.List;

public class ThreadedSearch<T> implements Searcher<T>, Runnable {

    private int numThreads;
    private T target;
    private List<T> list;
    private int begin;
    private int end;
    private Answer answer;

    public ThreadedSearch(int numThreads) {
        this.numThreads = numThreads;
    }

    private ThreadedSearch(T target, List<T> list, int begin, int end, Answer answer) {
        this.target = target;
        this.list = list;
        this.begin = begin;
        this.end = end;
        this.answer = answer;
    }

    /**
     * Searches `list` in parallel using `numThreads` threads.
     * <p>
     * You can assume that the list size is divisible by `numThreads`
     */
    @Override
    public boolean search(T target, List<T> list) throws InterruptedException {
     
        Answer answer = new Answer();
        //create arrays that hold the threadSearch
        Thread[] threads = new Thread[numThreads];
    
        for(int i=0; i<numThreads; i++) {
            int begin = (list.size()*i)/ numThreads;
            int end = (list.size()*(i+1))/ numThreads;
            ThreadedSearch<T> threadedSearch = new ThreadedSearch<T>(target, list, begin,end,answer);
            threads[i] = new Thread(threadedSearch);
            threads[i].start();
        }  

        for(int i = 0; i < numThreads; i++){
            threads[i].join();
        }
        return answer.getAnswer();
    }

    @Override
    public void run() {
        for (int i=0; i<end; i++){
            if (answer.getAnswer() == true){
              break;
            }
            if (list.get(i).equals(target)){
              answer.setAnswer(true);
            }
          }
    }

    private class Answer {
        private boolean answer = false;

        // In a more general setting you would typically want to synchronize
        // this method as well. Because the answer is just a boolean that only
        // goes from initial initial value (`false`) to `true` (and not back
        // again), we can safely not synchronize this, and doing so substantially
        // speeds up the lookup if we add calls to `getAnswer()` to every step in
        // our threaded loops.
        public boolean getAnswer() {
            return answer;
        }

        // This has to be synchronized to ensure that no two threads modify
        // this at the same time, possibly causing race conditions.
        // Actually, that's not really true here, because we're just overwriting
        // the old value of answer with the new one, and no one will actually
        // call with any value other than `true`. In general, though, you do
        // need to synchronize update methods like this to avoid race conditions.
        public synchronized void setAnswer(boolean newAnswer) {
            answer = newAnswer;
        }
    }

}
