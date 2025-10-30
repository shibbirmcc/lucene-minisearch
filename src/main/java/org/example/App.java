package org.example;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import org.example.config.AppConfig;
import org.example.http.HttpServer;
import org.example.routes.AppRouter;
import org.example.routes.MetricsRouter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App {
    private static final Logger logger = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
        try{
            AppConfig config = new AppConfig();

            EventLoopGroup bossGroup = new NioEventLoopGroup();
            EventLoopGroup workerGroup = new NioEventLoopGroup();

            var appServer = new HttpServer(bossGroup, workerGroup)
                    .withPort(config.getServer().getAppPort())
                    .withRouter(new AppRouter())
                    .start();

            var metricRouter = new HttpServer(workerGroup, bossGroup)
                    .withPort(config.getServer().getMetricPort())
                    .withRouter(new MetricsRouter())
                    .start();

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                appServer.stop();
                metricRouter.stop();
                bossGroup.shutdownGracefully();
                workerGroup.shutdownGracefully();
            }));

            appServer.blockUntilClosed();
            metricRouter.blockUntilClosed();
        }catch (Exception e){
            logger.error(e.getMessage());
        }
    }
}
