package kr.co.clean.java.thread;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation.class)
public class ThreadPoolExecutorTest {

  static Callable<Boolean> heavyTask;

  ThreadPoolExecutor threadPool;

  Future<Boolean> future1,future2,future3,future4;

  @BeforeAll
  static void setUp() {
    heavyTask = ()-> {
      Thread.sleep(3000L);
      System.out.println("heavyTask run");
      return true;
    };

  }

  @Test
  @Order(0)
  @DisplayName("풀생성")
  void createLazyThreadPool() {
    final int corePoolSize = 2;
    final int maximumPoolSize = 3;
    final long keepAliveTime = 3L;
    final int queueCapacity = 1;

    System.out.println("createLazyThreadPool");

    threadPool = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime,
        TimeUnit.SECONDS, new LinkedBlockingQueue<>(queueCapacity));

    assertEquals(0, threadPool.getPoolSize());
    assertEquals(0, threadPool.getQueue().size());
  }

  @Test
  @Order(1)
  @DisplayName("첫 스레드 생성")
  void createFirstThread() {
    System.out.println("createFirstThread");

    future1 = threadPool.submit(heavyTask);
    assertEquals(1, threadPool.getPoolSize());
    assertEquals(0, threadPool.getQueue().size());
    
  }

  @Test
  @Order(2)
  @DisplayName("풀갯수 스레드 생성")
  void reachToCorePoolSize() throws InterruptedException {
    System.out.println("reachToCorePoolSize");

    future2 = threadPool.submit(heavyTask);

    assertEquals(2, threadPool.getPoolSize());
    assertEquals(0, threadPool.getQueue().size());
  }

  @Test
  @Order(3)
  @DisplayName("큐 대기 갯수 확인")
  void reachToQueueCapacity() {
    System.out.println("reachToQueueCapacity");
    future3 = threadPool.submit(heavyTask);

    assertEquals(2, threadPool.getPoolSize());
    assertEquals(1, threadPool.getQueue().size());
  }

  @Test
  @Order(4)
  @DisplayName("큐 보다 높게")
  void reachToMaxPoolSize() {
    System.out.println("reachToMaxPoolSize");
    future4 = threadPool.submit(heavyTask);

    assertEquals(3, threadPool.getPoolSize());
    assertEquals(1, threadPool.getQueue().size());
  }

  @Test
  @Order(5)
  @DisplayName("거부 확인")
  void overMaxPoolSize() {
    System.out.println("overMaxPoolSize");
    assertThrows(
        RejectedExecutionException.class,
        () -> threadPool.submit(heavyTask)
    );
  }

  @Test
  @Order(6)
  void result() throws ExecutionException, InterruptedException {

    future1.get();
    future2.get();
    future4.get();
    future3.get();

    // 모든 작업 완료 후, keepAliveTime 동안 작업 요청이 없으면 쓰레드 풀의 쓰레드 개수는 기본치(coreThreadPool)로 유지됩니다.
    assertEquals(2, threadPool.getPoolSize());
    assertEquals(0, threadPool.getQueue().size());
  }

}
