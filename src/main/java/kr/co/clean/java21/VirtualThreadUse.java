package kr.co.clean.java21;

import java.lang.Thread.Builder;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class VirtualThreadUse {

  public static void main(String[] args) throws Exception{

      System.out.println("zzzzzzzzz");
      run();


  }

  public static void run() {
    //1...
    Thread.startVirtualThread(()->{
      System.out.println("Virtual Thread 1");
    });

    //2...
    Runnable runnable = () -> System.out.println("Virtual Thread 2");
    Thread vThread = Thread.ofVirtual().start(runnable);

    // Namings VR THREAD
    Thread.Builder builder = Thread.ofVirtual().name("VR THREAD1");
    Thread vThread1 = builder.start(runnable);

    System.out.println("is=" +vThread1.isVirtual());

    // 3...

    try (ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor()){
      for(int i=0;i<4;i++)
        executorService.submit(runnable);
    }

  }

}
