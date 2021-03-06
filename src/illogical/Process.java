package illogical;

/**
 * ClassName:illogical.Process <br/>
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
  private Request watch;
  /**
   * myreq: 当且仅当当前线程释放锁后更新为GRANTED状态，否则为PENDING状态.
   *
   * @since JDK 1.8
   */
  private Request myreq;
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
    this.myreq = new Request(State.PENDING, myProcess);
  }

  /**
   * lock:请求锁. <br/>
   */
  private void lock() {
    myreq.setState(State.PENDING);
    watch = clh.getLock().getTail().getAndSet(myreq);
    boolean flag = true;
    while (watch.getState() == State.PENDING) {
      try {
        if (watch.getMyProcess() != null) {
          if (flag) {
            System.out.println("   " + name + "    | is waiting for " + watch.getMyProcess().name
                    + " | " + myreq.getState() + " | " + watch.getState() + " |    " +
                    "added to queue    | ");
          } else {
            System.out.println("   " + name + "    | is waiting for " + watch.getMyProcess().name
                    + " | " + myreq.getState() + " | " + watch.getState() + " |      " +
                    "                |");
          }
          if (clh.getLock().getTail().get().equals(myreq)) {
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
      System.out.println("   " + name + "    |      get lock     | " + myreq.getState() +
              " | " + watch.getState() + " |    added to queue    | ");
    } else {
      System.out.println("   " + name + "    |      get lock     | " + myreq.getState() +
              " | " + watch.getState() + " |                      |");
    }
    if (clh.getLock().getTail().get().equals(myreq)) {
      System.out.println("— — — — — — — — — — — — — — — — — — — — — — — — |");
    }
  }

  /**
   * unlock:释放锁. <br/>
   */
  private void unlock() {
    myreq.setState(State.GRANTED);
    System.out.println("   " + name + "    |   release lock    | " + myreq.getState() +
            " |    X    |   remove from queue  |");
    if (clh.getLock().getTail().get().equals(myreq)) {
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
  }

  public void setWatch(Request watch) {
    this.watch = watch;
  }
}
