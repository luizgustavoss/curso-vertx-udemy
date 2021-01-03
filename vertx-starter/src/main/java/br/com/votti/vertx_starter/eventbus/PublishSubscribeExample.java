package br.com.votti.vertx_starter.eventbus;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PublishSubscribeExample extends AbstractVerticle {

  public static void main(String[] args) {
    var vertx = Vertx.vertx();
    vertx.deployVerticle(new Publisher());
    vertx.deployVerticle(new SubscriberOne());
    vertx.deployVerticle(SubscriberTwo.class.getName(), new DeploymentOptions().setInstances(2));
  }


  public static class Publisher extends AbstractVerticle {

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
      startPromise.complete();
      vertx.setPeriodic(1000, id -> {
        vertx.eventBus().publish(Publisher.class.getName(), "A message to everyone");
      });
    }
  }

  public static class SubscriberOne extends AbstractVerticle {

    private static final Logger logger = LoggerFactory.getLogger(SubscriberOne.class);

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
      startPromise.complete();
      vertx.eventBus().<String>consumer(Publisher.class.getName(), message -> {
        logger.debug("Received message {} ", message.body());
      });
    }
  }

  public static class SubscriberTwo extends AbstractVerticle {

    private static final Logger logger = LoggerFactory.getLogger(SubscriberTwo.class);

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
      startPromise.complete();
      vertx.eventBus().<String>consumer(Publisher.class.getName(), message -> {
        logger.debug("Received message {} ", message.body());
      });
    }
  }


}
