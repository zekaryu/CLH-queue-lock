/**
 * ClassName:Process <br/>
 * Function:请求锁的线程. <br/>
 * Reason:请求锁的线程. <br/>
 * Date:2017/9/11 17:01 <br/>
 *
 * @since JDK 1.8
 */
public class Process implements Runnable {
  /**
   * clh: 线程请求的clh锁.
   *
   * @since JDK 1.8
   */
  private CLH clh;
  /**
   * watch: 当前线程自旋监视的目标request，为前驱process的myreq.
   *
   * @since JDK 1.8
   */
  private ThreadLocal<Request> watch;
  /**
   * myreq: 当且仅当当前线程释放锁后更新为GRANTED状态，否则为PENDING状态.
   *
   * @since JDK 1.8
   */
  private ThreadLocal<Request> myreq;
  /**
   * name: 当前线程名，方便观察，request对象与线程的对应关系.
   *
   * @since JDK 1.8
   */
  private String name;

  Process(String name, CLH clh) {
    this.clh = clh;
    this.name = name;
    Process myProcess = this;
    //初始化myreq对象，状态为PENDING，对应的线程为当前的myProcess
    this.myreq = new ThreadLocal<Request>() {
      @Override
      protected Request initialValue() {
        return new Request(State.PENDING, myProcess);
      }
    };
    //watch 初始化为null，加入到队列之后，会指向前驱process的myreq
    this.watch = new ThreadLocal<Request>();
  }

  /**
   * lock:请求锁. <br/>
   */
  private void lock() {
    myreq.get().setState(State.PENDING);
    Request tmp = clh.getLock().getTail().getAndSet(myreq.get());
    watch.set(tmp);
    boolean flag = true;
    while (watch.get().getState() == State.PENDING) {
      try {
        if (watch.get().getMyProcess() != null) {
          if (flag) {
            System.out.println("   " + name + "    | is waiting for " + watch.get().getMyProcess().name
                    + " | " + myreq.get().getState() + " | " + watch.get().getState() + " |    " +
                    "added to queue    | ");
          } else {
            System.out.println("   " + name + "    | is waiting for " + watch.get().getMyProcess().name
                    + " | " + myreq.get().getState() + " | " + watch.get().getState() + " |      " +
                    "                |");
          }
          if (clh.getLock().getTail().get().equals(myreq.get())) {
            System.out.println("— — — — — — — — — — — — — — — — — — — — — — — — |");
          }
        }
        Thread.sleep(4000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      flag = false;
    }
    if (flag) {
      System.out.println("   " + name + "    |      get lock     | " + myreq.get().getState() +
              " | " + watch.get().getState() + " |    added to queue    | ");
    } else {
      System.out.println("   " + name + "    |      get lock     | " + myreq.get().getState() +
              " | " + watch.get().getState() + " |                      |");
    }
    if (clh.getLock().getTail().get().equals(myreq.get())) {
      System.out.println("— — — — — — — — — — — — — — — — — — — — — — — — |");
    }
  }

  /**
   * unlock:释放锁. <br/>
   */
  private void unlock() {
    myreq.get().setState(State.GRANTED);
    System.out.println("   " + name + "    |   release lock    | " + myreq.get().getState() +
            " |    X    |   remove from queue  |");
    if (clh.getLock().getTail().get().equals(myreq.get())) {
      System.out.println("— — — — — — — — — — — — — — — — — — — — — — — — |");
    }
//    myreq.set(watch.get());
  }

  @Override
  public void run() {
    //1.请求锁
    lock();
    //2.程序性等待，获取锁之后等待2秒钟，释放锁
    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    //释放锁
    unlock();
    lock();
    //线程结束之前，remove threadlocal 对象（！！！好习惯）
    myreq.remove();
    watch.remove();
  }
}
