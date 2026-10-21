package com.sanedge.ecommerce.auth.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.sanedge.ecommerce.common.adapter.role.RoleAdapter;
import com.sanedge.ecommerce.common.adapter.role.RolePort;
import com.sanedge.ecommerce.common.adapter.user.UserAdapter;
import com.sanedge.ecommerce.common.adapter.user.UserPort;
import com.sanedge.ecommerce.common.resilience.DependencyGuardInterceptor;
import com.sanedge.ecommerce.pb.role.RoleQueryServiceGrpc;
import com.sanedge.ecommerce.pb.user.UserCommandServiceGrpc;
import com.sanedge.ecommerce.pb.user.UserQueryServiceGrpc;

import io.grpc.ManagedChannel;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;

/** Outbound gRPC channels/stubs used by the auth service. */
@Configuration
public class GrpcClientConfig {

    @Value("${grpc.client.user.host:localhost}")
    private String userHost;

    @Value("${grpc.client.user.port:9102}")
    private int userPort;

    @Value("${grpc.client.role.host:localhost}")
    private String roleHost;

    @Value("${grpc.client.role.port:9101}")
    private int rolePort;

    @Bean(destroyMethod = "shutdownNow")
    public ManagedChannel userChannel(DependencyGuardInterceptor dependencyGuardInterceptor) {
        return NettyChannelBuilder.forAddress(userHost, userPort)
                .usePlaintext()
                .intercept(dependencyGuardInterceptor)
                .build();
    }

    @Bean(destroyMethod = "shutdownNow")
    public ManagedChannel roleChannel(DependencyGuardInterceptor dependencyGuardInterceptor) {
        return NettyChannelBuilder.forAddress(roleHost, rolePort)
                .usePlaintext()
                .intercept(dependencyGuardInterceptor)
                .build();
    }

    @Bean
    public UserQueryServiceGrpc.UserQueryServiceBlockingStub userQueryStub(
            @Qualifier("userChannel") ManagedChannel userChannel) {
        return UserQueryServiceGrpc.newBlockingStub(userChannel);
    }

    @Bean
    public UserCommandServiceGrpc.UserCommandServiceBlockingStub userCommandStub(
            @Qualifier("userChannel") ManagedChannel userChannel) {
        return UserCommandServiceGrpc.newBlockingStub(userChannel);
    }

    @Bean
    public RoleQueryServiceGrpc.RoleQueryServiceBlockingStub roleQueryStub(
            @Qualifier("roleChannel") ManagedChannel roleChannel) {
        return RoleQueryServiceGrpc.newBlockingStub(roleChannel);
    }

    @Bean
    public UserPort userPort(UserQueryServiceGrpc.UserQueryServiceBlockingStub userQueryStub,
            UserCommandServiceGrpc.UserCommandServiceBlockingStub userCommandStub) {
        return new UserAdapter(userQueryStub, userCommandStub);
    }

    @Bean
    public RolePort rolePort(RoleQueryServiceGrpc.RoleQueryServiceBlockingStub roleQueryStub) {
        return new RoleAdapter(roleQueryStub);
    }
}
