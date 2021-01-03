package br.com.votti.vertx_starter.worker;

import br.com.votti.vertx_starter.eventloops.EventLoopExample;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorkerExample extends AbstractVerticle {

  private static final Logger logger = LoggerFactory.getLogger(WorkerExample.class);

  public static void main(String[] args) {
    var vertx = Vertx.vertx();
    vertx.deployVerticle(new WorkerExample());
  }


  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    vertx.deployVerticle(new WorkerVerticle(), new DeploymentOptions()
      .setWorker(true)
      .setWorkerPoolSize(1)
      .setWorkerPoolName("my-worker-verticle")
    );
    startPromise.complete();
    executeBlockingCode();
  }

  private void executeBlockingCode() {
    vertx.executeBlocking(event -> {
      logger.debug("Execute blocking code");
      try {
        Thread.sleep(5000);
        event.complete();
      } catch (InterruptedException e) {
        logger.error("Failed: {} ", e.getCause());
        event.fail(e);
      }
    }, result -> {
      if(result.succeeded()){
        logger.debug("blocking call done ");
      } else{
        logger.debug("blocking call failed due to {} ", result.cause());
      }
    });
  }
}
