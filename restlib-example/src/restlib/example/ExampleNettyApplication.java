package restlib.example;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.codec.http.HttpServerCodec;

import restlib.Request;
import restlib.data.ExtensionHeaders;
import restlib.data.HttpHeaders;
import restlib.example.echo.BioEchoResource;
import restlib.example.echo.EchoResource;
import restlib.example.echo.NettyEchoResource;
import restlib.ext.netty.v3_5.server.NettyApplication;
import restlib.ext.netty.v3_5.server.NettyApplicationBuilder;
import restlib.ext.netty.v3_5.server.NettyResources;
import restlib.ext.netty.v3_5.server.connector.HttpServerHandler;
import restlib.net.UriSchemes;
import restlib.server.RequestFilters;
import restlib.server.Route;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;

public final class ExampleNettyApplication {    
    private static Function<Request, NettyApplication> APPLICATION_SUPPLIER =
            new Function<Request, NettyApplication>() {
            final Route echo = Route.startsWith("/example/echo");
            final Route bioEcho = Route.startsWith("/example/becho");
      
            final NettyApplication application = NettyApplicationBuilder
                    .newInstance()
                    .addRequestFilter(
                            RequestFilters.queryFilter(
                                    ImmutableList.of(
                                            ExtensionHeaders.X_HTTP_METHOD_OVERRIDE,
                                            HttpHeaders.ACCEPT)))
                    .addRequestFilter(RequestFilters.DEFAULT_EXTENSION_FILTER)                       
                    .addResource(NettyEchoResource.newInstance(EchoResource.newInstance(echo)))
                    .addResource(
                            NettyResources.bioToNettyResource(
                                    BioEchoResource.newInstance(EchoResource.newInstance(bioEcho))))
                    .build();
        
                @Override
                public NettyApplication apply(final Request arg0) {
                    return application;
                }
        
    };
    private static class ExamplePipelineFactory implements ChannelPipelineFactory {
        @Override
        public ChannelPipeline getPipeline() throws Exception {
            // Create a default pipeline implementation.
            ChannelPipeline pipeline = Channels.pipeline();

            /*
            if (this.config.getSSLEngine() != null) {
                SSLEngine engine = this.config.getSSLEngine();
                pipeline.addLast("ssl", new SslHandler(engine));
            }*/
            
            pipeline.addLast("codec", new HttpServerCodec());

            /*
            if (this.config.useContentCompression()) {
                pipeline.addLast("compressor", new HttpContentCompressor());
                pipeline.addLast("decompressor", new HttpContentDecompressor());
            }*/
            
            pipeline.addLast("handler", 
                    HttpServerHandler.newInstance(UriSchemes.HTTP, APPLICATION_SUPPLIER));
            return pipeline;
        }      
    }

    public static void main(final String...args) {
        int port;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        } else {
            port = 8080;
        }

        ServerBootstrap bootstrap = new ServerBootstrap(
                new NioServerSocketChannelFactory(
                        Executors.newCachedThreadPool(),
                        Executors.newCachedThreadPool()));

        // Set up the event pipeline factory.
        bootstrap.setPipelineFactory(new ExamplePipelineFactory());

        // Bind and start to accept incoming connections.
        bootstrap.bind(new InetSocketAddress(port));
    }
}
