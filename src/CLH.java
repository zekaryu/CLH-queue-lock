/**
 * ClassName:CLH <br/>
 * Function:CLH 队列锁. <br/>
 * Reason:CLH 队列锁. <br/>
 * Date:2017/9/11 16:59 <br/>
 * 
 * @since JDK 1.8
 */
public class CLH {

  /**  
   * lock: clh队列锁的lock对象.  
   * @since JDK 1.8  
   */
  private Lock lock;

  private CLH() {
    this.lock = new Lock();
  }

  public Lock getLock() {
    return lock;
  }

  public void setLock(Lock lock) {
    this.lock = lock;
  }

  public static void main(String[] args) throws InterruptedException {
    CLH clh = new CLH();
    Process process1 = new Process("p1",clh);
    Process process2 = new Process("p2",clh);
    Process process3 = new Process("p3",clh);
    Process process4 = new Process("p4",clh);
    System.out.println("  线程   |       action      |  myreq  |  watch  |        queue         |");
    System.out.println("— — — — — — — — — — — — — — — — — — — — — — — — |");
    new Thread(process1).start();
    Thread.sleep(100);
    new Thread(process2).start();
    Thread.sleep(100);
    new Thread(process3).start();

  }
}

