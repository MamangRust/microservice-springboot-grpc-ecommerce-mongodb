package com.sanedge.ecommerce.user.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.sanedge.ecommerce.common.adapter.role.RoleAdapter;
import com.sanedge.ecommerce.common.adapter.role.RolePort;
import com.sanedge.ecommerce.common.resilience.DependencyGuardInterceptor;
import com.sanedge.ecommerce.pb.role.RoleQueryServiceGrpc;

import io.grpc.ManagedChannel;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;

@Configuration
public class GrpcClientConfig {

    @Bean(destroyMethod = "shutdownNow")
    public ManagedChannel roleChannel(
            @Value("${grpc.client.role.host:localhost}") String host,
            @Value("${grpc.client.role.port:9101}") int port,
            DependencyGuardInterceptor dependencyGuardInterceptor) {
        return NettyChannelBuilder.forAddress(host, port).usePlaintext()
                .intercept(dependencyGuardInterceptor)
                .build();
    }

    @Bean
    public RoleQueryServiceGrpc.RoleQueryServiceBlockingStub roleQueryServiceBlockingStub(ManagedChannel roleChannel) {
        return RoleQueryServiceGrpc.newBlockingStub(roleChannel);
    }

    @Bean
    public RolePort rolePort(RoleQueryServiceGrpc.RoleQueryServiceBlockingStub roleQueryStub) {
        return new RoleAdapter(roleQueryStub);
    }
}
