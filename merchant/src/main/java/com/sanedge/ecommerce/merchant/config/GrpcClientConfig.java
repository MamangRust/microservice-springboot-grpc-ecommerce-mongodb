package com.sanedge.ecommerce.merchant.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.sanedge.ecommerce.common.adapter.user.UserAdapter;
import com.sanedge.ecommerce.common.adapter.user.UserPort;
import com.sanedge.ecommerce.common.resilience.DependencyGuardInterceptor;
import com.sanedge.ecommerce.pb.user.UserCommandServiceGrpc;
import com.sanedge.ecommerce.pb.user.UserQueryServiceGrpc;

import io.grpc.ManagedChannel;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;

@Configuration
public class GrpcClientConfig {

    @Bean(destroyMethod = "shutdownNow")
    public ManagedChannel userChannel(
            @Value("${grpc.client.user.host:localhost}") String host,
            @Value("${grpc.client.user.port:9102}") int port,
            DependencyGuardInterceptor dependencyGuardInterceptor) {
        return NettyChannelBuilder.forAddress(host, port).usePlaintext()
                .intercept(dependencyGuardInterceptor)
                .build();
    }

    @Bean
    public UserQueryServiceGrpc.UserQueryServiceBlockingStub userQueryServiceBlockingStub(ManagedChannel userChannel) {
        return UserQueryServiceGrpc.newBlockingStub(userChannel);
    }

    @Bean
    public UserCommandServiceGrpc.UserCommandServiceBlockingStub userCommandServiceBlockingStub(
            ManagedChannel userChannel) {
        return UserCommandServiceGrpc.newBlockingStub(userChannel);
    }

    @Bean
    public UserPort userPort(UserQueryServiceGrpc.UserQueryServiceBlockingStub userQueryStub,
            UserCommandServiceGrpc.UserCommandServiceBlockingStub userCommandStub) {
        return new UserAdapter(userQueryStub, userCommandStub);
    }
}
